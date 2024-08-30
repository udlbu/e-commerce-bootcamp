import React, { useEffect, useState } from 'react';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import { addProducts } from '@src/store/features/productsSlice';
import { Product } from '@src/types/product';
import ProductApiService from '@src/api/ProductApiService';
import { notifyProductDeleted } from '@src/notifications/notifications';
import { handleError } from '@src/error-handler/error-handler';
import ProgressIcon from '@src/components/shared/progress-icon/ProgressIcon';
import Pagination from '@src/components/shared/pagination/Pagination';

interface ProductsListProps {
  onEnterEditing: (product: Product) => void;
}

const ProductsList: React.FC<ProductsListProps> = ({ onEnterEditing }: ProductsListProps) => {
  const productsPage = useAppSelector((state) => state.products);
  const [pageNum, setPageNum] = useState(0);
  const dispatch = useAppDispatch();
  const [deleteProcessing, setDeleteProcessing] = useState(new Map());
  useEffect(() => {
    fetchProducts(0);
  }, []);

  const fetchProducts = async (page: number) => {
    try {
      const response = await ProductApiService.getProducts(page);
      dispatch(addProducts(response.data));
    } catch (ex) {
      handleError(ex);
    }
  };

  const handleProductPageChange = (page: number) => {
    fetchProducts(page);
    setPageNum(page);
  };

  const deleteProduct = async (id: string) => {
    try {
      deleteProcessing.set(id, true);
      setDeleteProcessing(new Map(deleteProcessing));
      await ProductApiService.deleteProduct(id);
      const response = await ProductApiService.getProducts(0);
      dispatch(addProducts(response.data));
      setPageNum(0);
      notifyProductDeleted();
    } catch (ex) {
      handleError(ex);
    } finally {
      deleteProcessing.set(id, false);
      setDeleteProcessing(new Map(deleteProcessing));
    }
  };
  return (
    <>
      {productsPage.data.length > 0 && (
        <Pagination
          pageSize={10}
          page={pageNum}
          total={productsPage.total}
          onPageChange={handleProductPageChange}
        />
      )}
      <table className='table table-content'>
        <thead>
          <tr className='main-heading'>
            <th role='imageHeader'>IMAGE</th>
            <th role='nameHeader'>NAME</th>
            <th role='descriptionHeader' className='text-center'>
              DESCRIPTION
            </th>
            <th role='categoryHeader' className='text-center'>
              CATEGORY
            </th>
            <th role='eanHeader' className='text-center'>
              EAN
            </th>
            <th role='deleteHeader' className='text-center'>
              <i className='ti-trash remove-icon' />
            </th>
            <th role='editHeader' className='text-center'>
              <i className='ti-pencil remove-icon' />
            </th>
          </tr>
        </thead>
        <tbody>
          {productsPage.data.map((it) => (
            <tr key={it.id} role='productRow'>
              <td role='imageColumn' className='image text-center' data-title='Image'>
                <img src={it.imageUrl} alt='' />
              </td>
              <td role='nameColumn' className='text-center' data-title='Name'>
                <p className='product-name'>{it.name}</p>
              </td>
              <td role='descriptionColumn' className='text-center' data-title='Description'>
                <p className='product-name'>{it.description.substring(0, 200)}</p>
              </td>
              <td role='categoryColumn' className='text-center' data-title='Category'>
                <p className='product-name'>{it.category}</p>
              </td>
              <td role='eanColumn' className='text-center' data-title='Ean'>
                <p className='product-name'>{it.ean}</p>
              </td>
              <td className='text-center action' data-title='Delete'>
                <a role='deleteBtn' onClick={() => deleteProduct(it.id)}>
                  {!deleteProcessing.get(it.id) && <i className='ti-trash remove-icon' />}
                  {deleteProcessing.get(it.id) && <ProgressIcon />}
                </a>
              </td>
              <td className='text-center action' data-title='Edit'>
                <a role='editBtn' onClick={() => onEnterEditing(it)}>
                  <i className='ti-pencil remove-icon' />
                </a>
              </td>
            </tr>
          ))}
          {productsPage.data.length == 0 && (
            <tr role='emptyRow'>
              <td data-title='Empty'>There are no products</td>
            </tr>
          )}
        </tbody>
      </table>
    </>
  );
};

export default ProductsList;
