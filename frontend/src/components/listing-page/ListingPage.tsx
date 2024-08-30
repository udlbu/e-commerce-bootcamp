import React, { useEffect, useState } from 'react';
import CategoryList from '@src/components/listing-page/category-list/CategoryList';
import styles from './ListingPage.module.scss';
import clsx from 'clsx';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import { addListingOffers } from '@src/store/features/listingOffersSlice';
import { useNavigate, useSearchParams } from 'react-router-dom';
import Breadcrumbs from '@src/components/shared/breadcrumbs/Breadcrumbs';
import { listingPageBreadcrumb } from '@src/domain/breadcrumbs';
import OfferApiService from '@src/api/OfferApiService';
import { handleError } from '@src/error-handler/error-handler';
import { SearchOffersRequest } from '@src/types/offer';
import Pagination from '@src/components/shared/pagination/Pagination';
import PageSize, { DEFAULT_PAGE_SIZE } from '@src/components/shared/page-size/PageSize';
import Listing from '@src/components/listing-page/listing/Listing';

const ListingPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [processing, setProcessing] = useState(false);
  const selectedCategory = searchParams.get('category');
  const listingPage = useAppSelector((state) => state.listingOffers);
  const [pageNum, setPageNum] = useState(0);
  const [pageSize, setPageSize] = useState<number>(DEFAULT_PAGE_SIZE);
  useEffect(() => {
    fetchOffers({ page: pageNum, pageSize: pageSize, productCategory: selectedCategory });
  }, []);

  const handleCategoryChange = (category: string) => {
    fetchOffers({ page: 0, pageSize: pageSize, productCategory: category });
    setPageNum(0);
    navigate(`/listing?category=${category}`);
  };

  const handlePageChange = (pageNumber: number) => {
    fetchOffers({ page: pageNumber, pageSize: pageSize, productCategory: selectedCategory });
    setPageNum(pageNumber);
  };

  const handlePageSizeChange = (pageSize: number) => {
    fetchOffers({ page: 0, pageSize: pageSize, productCategory: selectedCategory });
    setPageSize(pageSize);
    setPageNum(0);
  };

  const fetchOffers = async (request: SearchOffersRequest) => {
    try {
      setProcessing(true);
      const response = await OfferApiService.getActiveOffers(request);
      dispatch(addListingOffers(response.data));
    } catch (ex) {
      handleError(ex);
    } finally {
      setProcessing(false);
    }
  };
  return (
    <>
      <Breadcrumbs elements={listingPageBreadcrumb} />
      <section className={clsx('product-area', 'shop-sidebar', 'shop', styles.section)}>
        <div className='container'>
          <div className='row'>
            <div className='col-lg-3 col-md-4 col-12'>
              <div role='categoriesSidebar' className='shop-sidebar'>
                <CategoryList
                  onCategoryChange={handleCategoryChange}
                  selectedCategory={selectedCategory}
                />
              </div>
            </div>
            <div className='col-lg-9 col-md-8 col-12'>
              <PageSize pageSize={pageSize} onPageSizeChange={handlePageSizeChange} />
              <Pagination
                page={pageNum}
                total={listingPage?.total || 0}
                pageSize={pageSize}
                onPageChange={handlePageChange}
              />
              <Listing processing={processing} />
              <Pagination
                page={pageNum}
                total={listingPage?.total || 0}
                pageSize={pageSize}
                onPageChange={handlePageChange}
              />
            </div>
          </div>
        </div>
      </section>
    </>
  );
};

export default ListingPage;
