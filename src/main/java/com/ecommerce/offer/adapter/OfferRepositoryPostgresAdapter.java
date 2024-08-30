package com.ecommerce.offer.adapter;

import com.ecommerce.cart.domain.model.Cart;
import com.ecommerce.offer.domain.exceptions.OfferCannotBeActivatedException;
import com.ecommerce.offer.domain.exceptions.OfferCannotBeDeletedException;
import com.ecommerce.offer.domain.exceptions.OfferCannotBeModifiedException;
import com.ecommerce.offer.domain.model.Offer;
import com.ecommerce.offer.domain.model.OfferId;
import com.ecommerce.offer.domain.model.OfferQuantityChange;
import com.ecommerce.offer.domain.model.OfferStatus;
import com.ecommerce.offer.domain.model.SearchOffer;
import com.ecommerce.offer.domain.port.OfferRepositoryPort;
import com.ecommerce.product.adapter.ProductEntity;
import com.ecommerce.product.adapter.SpringJpaProductRepository;
import com.ecommerce.shared.domain.PageResult;
import com.ecommerce.shared.domain.TotalSize;
import com.ecommerce.shared.domain.exception.ConcurrentModification;
import com.ecommerce.user.adapter.SpringJpaUserRepository;
import com.ecommerce.user.adapter.UserEntity;
import com.ecommerce.user.domain.model.UserId;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

@Transactional
public class OfferRepositoryPostgresAdapter implements OfferRepositoryPort {

    private final SpringJpaOfferRepository springJpaOfferRepository;

    private final SpringJpaProductRepository springJpaProductRepository;

    private final SpringJpaUserRepository springJpaUserRepository;

    public OfferRepositoryPostgresAdapter(SpringJpaOfferRepository springJpaOfferRepository,
                                          SpringJpaProductRepository springJpaProductRepository,
                                          SpringJpaUserRepository springJpaUserRepository) {
        this.springJpaOfferRepository = springJpaOfferRepository;
        this.springJpaProductRepository = springJpaProductRepository;
        this.springJpaUserRepository = springJpaUserRepository;
    }

    @Override
    public Offer findOfferById(OfferId id) {
        return springJpaOfferRepository.findById(id.val()).map(OfferEntity::toDomain).orElse(null);
    }

    @Override
    public Offer saveOffer(Offer offer) {
        ProductEntity product = springJpaProductRepository
                .findById(offer.product().id().val())
                .orElse(null);
        Objects.requireNonNull(product, String.format("Product must exists to create offer <%s>", offer.product()));
        UserEntity user = springJpaUserRepository
                .findById(offer.userId().val())
                .orElse(null);
        Objects.requireNonNull(user, String.format("User must exists to create offer <%s>", offer.userId()));
        OfferEntity offerEntity = OfferEntity.newOffer(offer, product);
        return springJpaOfferRepository.save(offerEntity).toDomain();
    }

    @Override
    public void changeOfferStatus(OfferId offerId, OfferStatus status) {
        springJpaOfferRepository.findById(offerId.val())
                .ifPresent(offerEntity -> {
                    if (Objects.equals(offerEntity.getQuantity(), 0)) {
                        throw new OfferCannotBeActivatedException("Offer with empty stock cannot be activated");
                    }
                    offerEntity.changeStatus(status);
                });
    }


    @Override
    public void deleteOfferById(OfferId offerId) {
        if (isOfferAlreadyBought(offerId)) {
            throw new OfferCannotBeDeletedException("Offer cannot be deleted because there are orders with it");
        }
        springJpaOfferRepository.deleteById(offerId.val());
    }

    @Override
    public PageResult<Offer> findActiveOffers(SearchOffer request) {
        QOfferEntity offer = QOfferEntity.offerEntity;
        BooleanExpression where = Expressions.TRUE;
        if (request.productCategory() != null) {
            where = where.and(offer.product.category.eq(request.productCategory().name()));
        }
        where = where.and(offer.status.eq(OfferStatus.ACTIVE.name()));
        Page<OfferEntity> offersPage = springJpaOfferRepository.findAll(where, toPageRequest(request));
        return new PageResult<>(offersPage.map(OfferEntity::toDomain).toList(), new TotalSize(offersPage.getTotalElements()));
    }

    public PageResult<Offer> findOffersByUserId(UserId userId, Integer page, Integer pageSize) {
        QOfferEntity offer = QOfferEntity.offerEntity;
        BooleanExpression where = Expressions.TRUE;
        where = where.and(offer.userId.eq(userId.val()));
        Page<OfferEntity> offersPage = springJpaOfferRepository.findAll(where, PageRequest.of(page, pageSize, Sort.Direction.ASC, "id"));
        return new PageResult<>(offersPage.map(OfferEntity::toDomain).toList(), new TotalSize(offersPage.getTotalElements()));
    }

    @Override
    public void updateOffer(Offer offer) {
        springJpaOfferRepository.findById(offer.id().val()).ifPresent(entity -> {
            if (!Objects.equals(entity.getVersion(), offer.version().val())) {
                throw new ConcurrentModification(format("Stale object: <%s>", offer.id()));
            }
            if (isOfferAlreadyBought(offer.id()) && isProductOrPriceModification(offer, entity)) {
                throw new OfferCannotBeModifiedException("Neither product nor price can be modified on offer that has already been bought");
            }
            try {
                if (offer.product() != null && !entity.getProduct().getId().equals(offer.product().id().val())) {
                    ProductEntity productEntity = springJpaProductRepository.findById(offer.product().id().val())
                            .orElse(null);
                    Objects.requireNonNull(productEntity, String.format("Product must exists to update the offer <%s>", offer.product()));
                    entity.changeProduct(productEntity);
                }
                entity.merge(offer);
            } catch (ObjectOptimisticLockingFailureException ex) {
                throw new ConcurrentModification(format("Concurrent modification occurred: <%s>", offer.id()), ex);
            }
        });
    }

    private boolean isProductOrPriceModification(Offer offer, OfferEntity entity) {
        if (!Objects.equals(offer.product().id().val(), entity.getProduct().getId())) {
            return true;
        }
        if (!Objects.equals(offer.price().val(), entity.getPrice())) {
            return true;
        }
        return false;
    }

    private boolean isOfferAlreadyBought(OfferId offerId) {
        Long count = springJpaOfferRepository.countOrderLinesWithOffer(offerId.val());
        return count > 0;
    }

    @Override
    public void changeOffersQuantity(Cart cart) {
        QOfferEntity offer = QOfferEntity.offerEntity;
        BooleanExpression where = Expressions.TRUE;
        where = where.and(offer.id.in(cart.toRawOfferIds()));
        where = where.and(offer.status.eq(OfferStatus.ACTIVE.name()));
        Iterable<OfferEntity> offerEntities = springJpaOfferRepository.findAll(where);
        Map<Long, OfferQuantityChange> changes = cart.toQuantityChanges();
        validateOffersVersion(offerEntities, changes);
        offerEntities.forEach(it -> {
            it.setQuantity(changes.get(it.getId()).newQuantity().val());
            if (it.getQuantity() == 0) {
                it.setStatus(OfferStatus.INACTIVE.name());
            }
        });
    }

    private void validateOffersVersion(Iterable<OfferEntity> offers, Map<Long, OfferQuantityChange> changesMap) {
        AtomicInteger validated = new AtomicInteger();
        offers.forEach(it -> {
            Offer offerToChange = changesMap.get(it.getId()).offer();
            if (!Objects.equals(it.getVersion(), offerToChange.version().val())) {
                throw new ConcurrentModification(format("Concurrent modification occurred: offerToChange=<%s> - actualOffer=<%s>", offerToChange, it));
            }
            validated.getAndIncrement();
        });
        if (validated.get() != changesMap.values().size()) {
            throw new ConcurrentModification(format("Concurrent modification occurred: validated=<%s> but actual size=<%s>", validated.get(), changesMap.values().size()));
        }
    }

    private PageRequest toPageRequest(SearchOffer searchOffer) {
        return PageRequest.of(searchOffer.pageNumber().val(), searchOffer.pageSize().val(), Sort.Direction.ASC, "id");
    }
}
