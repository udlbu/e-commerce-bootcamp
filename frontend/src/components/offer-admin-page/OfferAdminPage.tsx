import React, { useEffect, useRef, useState } from 'react';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import { addProducts } from '@src/store/features/productsSlice';
import { validateBlank } from '@src/components/shared/validators/blank-validator';
import styles from './OfferAdminPage.module.scss';
import { validatePrice } from '@src/components/shared/validators/price-validator';
import OfferApiService from '@src/api/OfferApiService';
import Preloader from '@src/components/shared/preloader/Preloader';
import OffersList from '@src/components/offer-admin-page/offers-list/OffersList';
import { Offer } from '@src/types/offer';
import { addOffers } from '@src/store/features/offersSlice';
import Breadcrumbs from '@src/components/shared/breadcrumbs/Breadcrumbs';
import { offerAdminPageBreadcrumbs } from '@src/domain/breadcrumbs';
import { handleError } from '@src/error-handler/error-handler';
import { notifyOfferAdded, notifyOfferModified } from '@src/notifications/notifications';
import Button from '@src/components/shared/button/Button';
import ProductApiService from '@src/api/ProductApiService';

const OfferAdminPage: React.FC = () => {
  const ref = useRef(null);
  const prodRef = useRef(null);
  const dispatch = useAppDispatch();
  const [processing, setProcessing] = useState(false);
  const [editing, setEditing] = useState(false);

  const products = useAppSelector((state) => state.products.data);
  const user = useAppSelector((state) => state.user);

  useEffect(() => {
    // to simplify just fetch 1000
    ProductApiService.getProducts(0, 1000)
      .then((response) => {
        dispatch(addProducts(response.data));
      })
      .catch((err) => handleError(err));
  }, []);

  const [id, setId] = useState(null);
  const [version, setVersion] = useState(null);
  const [product, setProduct] = useState(null);
  const [isProductError, setIsProductError] = useState(true);
  const [isProductDirty, setIsProductDirty] = useState(false);

  const [price, setPrice] = useState('');
  const [isPriceError, setIsPriceError] = useState(true);
  const [isPriceDirty, setIsPriceDirty] = useState(false);

  const [quantity, setQuantity] = useState(1);

  const handleProduct = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setIsProductDirty(true);
    if (!validateBlank(event.target.value)) {
      setIsProductError(true);
    } else {
      setIsProductError(false);
    }
    const selected = products.find((it) => it.id == event.target.value);
    if (!selected) {
      setProduct(null);
    } else {
      setProduct(selected);
    }
  };

  const handlePrice = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsPriceDirty(true);
    if (!validateBlank(event.target.value) || !validatePrice(event.target.value)) {
      setIsPriceError(true);
    } else {
      setIsPriceError(false);
    }
    setPrice(event.target.value);
  };

  const incrementQuantity = () => {
    setQuantity(quantity + 1);
  };

  const decrementQuantity = () => {
    if (quantity === 1) {
      return;
    }
    setQuantity(quantity - 1);
  };

  const isFormValid = (): boolean => {
    return isProductError || isPriceError;
  };

  const addOffer = async () => {
    const request = {
      userId: user.id,
      price: price,
      quantity: quantity.toString(),
      productId: product.id,
    };
    setProcessing(true);

    try {
      await OfferApiService.addOffer(request);
      clearFields();
      notifyOfferAdded();
    } catch (err) {
      handleError(err);
    } finally {
      setProcessing(false);
    }
  };

  const modifyOffer = async () => {
    const request = {
      offerId: id,
      userId: user.id,
      version: version,
      price: price,
      quantity: quantity.toString(),
      productId: product.id,
    };
    setProcessing(true);
    try {
      await OfferApiService.modifyOffer(request);
      const response = await OfferApiService.getCurrentUserOffers(0);
      dispatch(addOffers(response.data));
      clearFields();
      setEditing(false);
      notifyOfferModified();
    } catch (err) {
      handleError(err);
    } finally {
      setProcessing(false);
    }
  };

  const clearFields = () => {
    setId(null);
    setVersion(null);
    setProduct(null);
    setPrice('');
    setQuantity(1);
    setIsProductError(true);
    setIsProductDirty(false);
    setIsPriceError(true);
    setIsPriceDirty(false);
    prodRef.current.value = null;
  };

  const enterEditing = (offer: Offer): void => {
    ref.current.scrollIntoView({ behavior: 'smooth' });
    setEditing(true);
    setId(offer.id);
    setVersion(offer.version);
    setProduct(offer.product);
    setIsProductDirty(false);
    setIsProductError(false);
    setPrice(offer.price);
    setIsPriceDirty(false);
    setIsPriceError(false);
    setQuantity(Number(offer.quantity));
  };

  const cancelEditing = () => {
    setEditing(false);
    clearFields();
  };

  return (
    <>
      <Breadcrumbs elements={offerAdminPageBreadcrumbs} />
      <section className='shop shop-form-section section'>
        <div className='container'>
          <div className='row'>
            <div className='col-lg-12 col-12'>
              <div ref={ref} className='shop-form'>
                <h2>Offer management</h2>
                <p>&nbsp;</p>
                <div className='form row'>
                  <div className='col-lg-5 col-md-5 col-12'>
                    <div className='form-group'>
                      <label>
                        Select Product<span>*</span>
                      </label>
                      <select
                        ref={prodRef}
                        name='product'
                        role='product'
                        value={product?.id}
                        onChange={handleProduct}
                      >
                        <option value={null}></option>
                        {products.map((it) => {
                          return (
                            <option key={it.id} value={it.id}>
                              {it.name}
                            </option>
                          );
                        })}
                      </select>
                      {isProductDirty && isProductError && (
                        <div role='productError' className='error'>
                          Field is required
                        </div>
                      )}
                    </div>
                  </div>
                  <div className='col-lg-3 col-md-3 col-12'>
                    <div className='form-group'>
                      <label>
                        Price<span>*</span>
                      </label>
                      <input
                        type='text'
                        name='price'
                        role='price'
                        value={price}
                        placeholder=''
                        onChange={handlePrice}
                      />
                      {isPriceDirty && isPriceError && (
                        <div role='priceError' className='error'>
                          Field is required and must be valid
                        </div>
                      )}
                    </div>
                  </div>
                  <div className='col-lg-4 col-md-4 col-12'>
                    <div className='form-group'>
                      <label>
                        Quantity<span>*</span>
                      </label>
                      <div className='input-group'>
                        <div className='button minus'>
                          <button
                            role='decrementQuantity'
                            type='button'
                            className='btn btn-primary btn-number'
                            onClick={decrementQuantity}
                          >
                            <i className='ti-minus'></i>
                          </button>
                        </div>
                        <span role='quantity' className={styles.quantity}>
                          {quantity}
                        </span>
                        <div className='button plus'>
                          <button
                            role='incrementQuantity'
                            type='button'
                            className='btn btn-primary btn-number'
                            onClick={incrementQuantity}
                          >
                            <i className='ti-plus'></i>
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                  {product && (
                    <div className='col-lg-12 col-md-12 col-12'>
                      <p>Product Details</p>
                    </div>
                  )}
                  {product && (
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>Product Name</label>
                        <div role='productName' className={styles.field}>
                          {product?.name}
                        </div>
                      </div>
                    </div>
                  )}
                  {product && (
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>Ean</label>
                        <div role='ean' className={styles.field}>
                          {product?.ean}
                        </div>
                      </div>
                    </div>
                  )}
                  {product && (
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>Category</label>
                        <div role='category' className={styles.field}>
                          {product?.category}
                        </div>
                      </div>
                    </div>
                  )}
                  {product && (
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>Description</label>
                        <div role='description' className={styles.fieldArea}>
                          {product?.description}
                        </div>
                      </div>
                    </div>
                  )}
                  {product && (
                    <div className='col-lg-12 col-md-12 col-12'>
                      <div className='form-group'>
                        <div className={styles.imgBox}>
                          <img
                            role='loaded-image'
                            className={styles.loadedImage}
                            src={product.imageUrl}
                            alt=''
                          />
                        </div>
                      </div>
                    </div>
                  )}
                  {!editing && (
                    <div className='col-lg-12 col-md-12 col-12'>
                      <div className='form-group form-btn'>
                        <Button
                          role='addBtn'
                          text='Add Offer'
                          disabled={isFormValid() || processing}
                          onClick={() => addOffer()}
                          processing={processing}
                        />
                      </div>
                    </div>
                  )}
                  {editing && (
                    <div className='col-lg-2 col-md-2 col-12'>
                      <div className='form-group form-btn'>
                        <Button
                          role='updateBtn'
                          text='Save'
                          processing={processing}
                          onClick={() => modifyOffer()}
                          disabled={isFormValid() || processing}
                        />
                      </div>
                    </div>
                  )}
                  {editing && (
                    <div className='col-lg-2 col-md-2 col-12'>
                      <div className='form-group form-btn'>
                        <Button
                          role='cancelBtn'
                          text='Cancel'
                          disabled={processing}
                          processing={false}
                          onClick={() => cancelEditing()}
                        />
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
          <div role='offersList' className='row'>
            <div className='col-12'>
              {processing && <Preloader />}
              {!processing && <OffersList onEnterEditing={enterEditing} />}
            </div>
          </div>
        </div>
      </section>
    </>
  );
};

export default OfferAdminPage;
