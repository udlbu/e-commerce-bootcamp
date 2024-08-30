import React, { useEffect } from 'react';
import Breadcrumbs from '@src/components/shared/breadcrumbs/Breadcrumbs';
import ProductGallery from '@src/components/show-item-page/product-gallery/ProductGallery';
import OfferInfo from '@src/components/show-item-page/offer-info/OfferInfo';
import { useParams } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import Preloader from '@src/components/shared/preloader/Preloader';
import ShowItemTabs from '@src/components/show-item-page/show-item-tabs/ShowItemTabs';
import { showItemBreadcrumb } from '@src/domain/breadcrumbs';
import OfferApiService from '@src/api/OfferApiService';
import { addOffer } from '@src/store/features/offerDetailsSlice';
import { handleError } from '@src/error-handler/error-handler';

const ShowItemPage: React.FC = () => {
  const { id } = useParams();
  const dispatch = useAppDispatch();
  const offer = useAppSelector((state) => state.offerDetails.offer);
  useEffect(() => {
    fetchOffer();
    return () => {
      dispatch(addOffer(null));
    };
  }, []);

  const fetchOffer = async () => {
    if (id) {
      try {
        const offer = await OfferApiService.getOffer(id);
        dispatch(addOffer(offer.data));
      } catch (ex) {
        handleError(ex);
      }
    }
  };
  const renderSection = () => {
    if (!offer) {
      return <Preloader />;
    }
    return (
      <>
        <Breadcrumbs elements={showItemBreadcrumb(offer)} />
        <section role='showItemPage' className='shop single section'>
          <div className='container'>
            <div className='row'>
              <div className='col-12'>
                <div className='row'>
                  <div className='col-lg-6 col-12'>
                    <ProductGallery product={offer.product} />
                  </div>
                  <div className='col-lg-6 col-12'>
                    <OfferInfo offer={offer} />
                  </div>
                </div>
                <div className='row'>
                  <div className='col-12'>
                    <ShowItemTabs offer={offer} />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </>
    );
  };
  return renderSection();
};

export default ShowItemPage;
