import React, { useEffect } from 'react';
import Breadcrumbs from '@src/components/shared/breadcrumbs/Breadcrumbs';
import { myOrderPageBreadcrumb } from '@src/domain/breadcrumbs';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import { addOrders } from '@src/store/features/ordersSlice';
import { Price } from '@src/components/shared/price/Price';
import { totalOrderValue } from '@src/types/order';
import OrderApiService from '@src/api/OrderApiService';
import { handleError } from '@src/error-handler/error-handler';

const MyOrdersPage: React.FC = () => {
  const orders = useAppSelector((state) => state.orders.data);
  const user = useAppSelector((state) => state.user);
  const dispatch = useAppDispatch();
  useEffect(() => {
    OrderApiService.searchOffers({ userId: user.id, page: 0, pageSize: 100 })
      .then((response) => {
        dispatch(addOrders(response.data));
      })
      .catch((err) => handleError(err));
  }, []);
  return (
    <>
      <Breadcrumbs elements={myOrderPageBreadcrumb} />
      <section className='shop shop-form-section section'>
        <div className='container'>
          <div className='row'>
            <div className='col-lg-12 col-12'>
              <div className='shop-form'>
                <h2>My Orders</h2>
                <p>&nbsp;</p>
              </div>
            </div>
          </div>
          <div className='row'>
            {orders?.length > 0 && (
              <div role='orders-table' className='col-12'>
                {orders.map((order) => {
                  return (
                    <table key={order.id} className='table table-content table-orders'>
                      <thead>
                        <tr className='main-hading'>
                          <th role='order-id' colSpan={2} className='text-left'>
                            Order Id:&nbsp;{order.id}
                          </th>
                          <th role='order-value' className='text-right'>
                            Order value:&nbsp;
                            <Price value={totalOrderValue(order)} />
                          </th>
                          <th role='payment-method' className='text-right'>
                            Payment method:&nbsp;{order.paymentMethod}
                          </th>
                          <th role='delivery-status' className='text-right'>
                            Delivery status:&nbsp;{order.deliveryStatus}
                          </th>
                        </tr>
                        <tr className='main-hading'>
                          <th role='product-header' className='text-center'>
                            PRODUCT
                          </th>
                          <th role='name-header' className='text-center'>
                            NAME
                          </th>
                          <th role='unit-price-header' className='text-center'>
                            UNIT PRICE
                          </th>
                          <th role='quantity-header' className='text-center'>
                            QUANTITY
                          </th>
                          <th role='total-header' className='text-center'>
                            TOTAL
                          </th>
                        </tr>
                      </thead>
                      <tbody>
                        {order.lines.map((line) => {
                          return (
                            <tr key={line.id} role='cart-item-row'>
                              <td className='image' data-title='Image'>
                                <img role='product-img' src={line.imageUrl} alt='#' />
                              </td>
                              <td className='product-des' data-title='Description'>
                                <p className='product-name'>
                                  <a role='product-name'>{line.productName}</a>
                                </p>
                              </td>
                              <td role='price' className='price text-center' data-title='Price'>
                                <span>
                                  <Price value={line.offerPrice} />
                                </span>
                              </td>
                              <td role='quantity' className='qty text-center' data-title='Qty'>
                                {line.quantity}
                              </td>
                              <td
                                role='total-amount'
                                className='total-amount text-center'
                                data-title='Total'
                              >
                                <span>
                                  <Price value={line.offerPrice * line.quantity} />
                                </span>
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  );
                })}
              </div>
            )}
            {!(orders?.length > 0) && (
              <div role='empty-orders-table' className='col-12'>
                There are no orders
              </div>
            )}
          </div>
        </div>
      </section>
    </>
  );
};

export default MyOrdersPage;
