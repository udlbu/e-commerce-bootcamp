import React, { useEffect, useState } from 'react';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import { Offer } from '@src/types/offer';
import { addOffers } from '@src/store/features/offersSlice';
import OfferApiService from '@src/api/OfferApiService';
import { handleError } from '@src/error-handler/error-handler';
import {
  notifyOfferActivated,
  notifyOfferDeactivated,
  notifyOfferDeleted,
} from '@src/notifications/notifications';
import Button from '@src/components/shared/button/Button';
import ProgressIcon from '@src/components/shared/progress-icon/ProgressIcon';
import Pagination from '@src/components/shared/pagination/Pagination';

interface OffersListProps {
  onEnterEditing: (offer: Offer) => void;
}

const OffersList: React.FC<OffersListProps> = ({ onEnterEditing }: OffersListProps) => {
  const offersPage = useAppSelector((state) => state.offers);
  const [pageNum, setPageNum] = useState(0);
  const dispatch = useAppDispatch();
  const [processing, setProcessing] = useState(new Map());
  const [deleteProcessing, setDeleteProcessing] = useState(new Map());
  useEffect(() => {
    fetchOffers(0);
  }, []);

  const fetchOffers = async (page: number) => {
    try {
      const response = await OfferApiService.getCurrentUserOffers(page);
      dispatch(addOffers(response.data));
    } catch (ex) {
      handleError(ex);
    }
  };
  const deleteOffer = async (id: string) => {
    try {
      deleteProcessing.set(id, true);
      setDeleteProcessing(new Map(deleteProcessing));
      await OfferApiService.deleteOffer(id);
      const response = await OfferApiService.getCurrentUserOffers(0);
      dispatch(addOffers(response.data));
      setPageNum(0);
      notifyOfferDeleted();
    } catch (ex) {
      handleError(ex);
    } finally {
      deleteProcessing.set(id, false);
      setDeleteProcessing(new Map(deleteProcessing));
    }
  };

  const changeStatus = async (id: string) => {
    const offer = offersPage.data.find((it) => Number(it.id) === Number(id));
    if (!offer) {
      return;
    }
    processing.set(id, true);
    setProcessing(new Map(processing));
    const changeStatus =
      offer.status === 'ACTIVE'
        ? OfferApiService.deactivateOffer(id)
        : OfferApiService.activateOffer(id);
    try {
      await changeStatus;
      const response = await OfferApiService.getCurrentUserOffers(pageNum);
      dispatch(addOffers(response.data));
      offer.status === 'ACTIVE' ? notifyOfferDeactivated() : notifyOfferActivated();
    } catch (err) {
      handleError(err);
    } finally {
      processing.set(id, false);
      setProcessing(new Map(processing));
    }
  };

  const handleOfferPageChange = (page: number) => {
    fetchOffers(page);
    setPageNum(page);
  };
  return (
    <>
      {offersPage.data.length > 0 && (
        <Pagination
          pageSize={10}
          page={pageNum}
          total={offersPage.total}
          onPageChange={handleOfferPageChange}
        />
      )}
      <table className='table table-content'>
        <thead>
          <tr className='main-heading'>
            <th role='idHeader'>ID</th>
            <th role='productNameHeader'>PRODUCT NAME</th>
            <th role='productCategoryHeader'>CATEGORY</th>
            <th role='priceHeader' className='text-center'>
              PRICE
            </th>
            <th role='quantityHeader' className='text-center'>
              QUANTITY
            </th>
            <th role='statusHeader' className='text-center'>
              STATUS
            </th>
            <th role='deleteHeader' className='text-center'>
              <i className='ti-trash remove-icon' />
            </th>
            <th role='editHeader' className='text-center'>
              <i className='ti-pencil remove-icon' />
            </th>
            <th role='actionHeader' className='text-center'>
              ACTION
            </th>
          </tr>
        </thead>
        <tbody>
          {offersPage.data.map((it) => (
            <tr
              key={it.id}
              role='offerRow'
              className={it.status === 'ACTIVE' ? 'active' : 'inactive'}
            >
              <td role='idColumn' className='text-center' data-title='Id'>
                <p className='product-name'>{it.id}</p>
              </td>
              <td role='nameColumn' className='text-center' data-title='Name'>
                <p className='product-name'>{it.product.name}</p>
              </td>
              <td role='categoryColumn' className='text-center' data-title='Category'>
                <p className='product-name'>{it.product.category}</p>
              </td>
              <td role='priceColumn' className='text-center' data-title='Price'>
                <p className='product-name'>{it.price}</p>
              </td>
              <td role='quantityColumn' className='text-center' data-title='Category'>
                <p className='product-name'>{it.quantity}</p>
              </td>
              <td role='statusColumn' className='text-center' data-title='Ean'>
                <p className='product-name'>{it.status}</p>
              </td>
              <td className='text-center action' data-title='Delete'>
                <a role='deleteBtn' onClick={() => deleteOffer(it.id)}>
                  {!deleteProcessing.get(it.id) && <i className='ti-trash remove-icon' />}
                  {deleteProcessing.get(it.id) && <ProgressIcon />}
                </a>
              </td>
              <td className='text-center action' data-title='Edit'>
                <a role='editBtn' onClick={() => onEnterEditing(it)}>
                  <i className='ti-pencil remove-icon' />
                </a>
              </td>
              <td className='text-center action' data-title='ChangeStatus'>
                <Button
                  role='changeStatusBtn'
                  text={it.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
                  processing={processing.get(it.id)}
                  onClick={() => changeStatus(it.id)}
                />
              </td>
            </tr>
          ))}
          {offersPage.data.length == 0 && (
            <tr role='emptyRow'>
              <td data-title='Empty'>There are no offers</td>
            </tr>
          )}
        </tbody>
      </table>
    </>
  );
};

export default OffersList;
