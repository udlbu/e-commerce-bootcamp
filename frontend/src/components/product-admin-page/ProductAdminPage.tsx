import React, { useRef, useState } from 'react';
import { validateBlank } from '@src/components/shared/validators/blank-validator';
import ProductApiService from '@src/api/ProductApiService';
import { useAppDispatch } from '@src/store/hooks';
import { addProducts } from '@src/store/features/productsSlice';
import { Product, ProductCategory } from '@src/types/product';
import styles from './ProductAdminPage.module.scss';
import Preloader from '@src/components/shared/preloader/Preloader';
import ProductsList from '@src/components/product-admin-page/products-list/ProductsList';
import Breadcrumbs from '@src/components/shared/breadcrumbs/Breadcrumbs';
import { productAdminPageBreadcrumbs } from '@src/domain/breadcrumbs';
import { handleError } from '@src/error-handler/error-handler';
import { notifyProductAdded, notifyProductModified } from '@src/notifications/notifications';
import Button from '@src/components/shared/button/Button';

const ProductAdminPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const ref = useRef(null);
  const imgRef = useRef(null);
  const [processing, setProcessing] = useState(false);
  const [editing, setEditing] = useState(false);

  const [id, setId] = useState(null);
  const [version, setVersion] = useState(null);
  const [name, setName] = useState('');
  const [isNameError, setIsNameError] = useState(true);
  const [isNameDirty, setIsNameDirty] = useState(false);

  const [ean, setEan] = useState('');
  const [isEanError, setIsEanError] = useState(true);
  const [isEanDirty, setIsEanDirty] = useState(false);

  const [category, setCategory] = useState('');
  const [isCategoryError, setIsCategoryError] = useState(true);
  const [isCategoryDirty, setIsCategoryDirty] = useState(false);

  const [image, setImage] = useState(null);
  const [imageError, setImageError] = useState(null);

  const [imageUrl, setImageUrl] = useState('');

  const [description, setDescription] = useState('');

  const handleName = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsNameDirty(true);
    if (!validateBlank(event.target.value)) {
      setIsNameError(true);
    } else {
      setIsNameError(false);
    }
    setName(event.target.value);
  };

  const handleEan = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsEanDirty(true);
    if (!validateBlank(event.target.value)) {
      setIsEanError(true);
    } else {
      setIsEanError(false);
    }
    setEan(event.target.value);
  };

  const handleCategory = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setIsCategoryDirty(true);
    if (!validateBlank(event.target.value)) {
      setIsCategoryError(true);
    } else {
      setIsCategoryError(false);
    }
    setCategory(event.target.value);
  };

  const handleImage = (event: React.ChangeEvent<HTMLInputElement>) => {
    const imageFile = event.target.files[0];
    if (!imageFile) {
      setImageError(null);
      return;
    }
    if (imageFile.type !== 'image/jpeg' && imageFile.type !== 'image/png') {
      setImageError('Incorrect file type (JPEG/PNG)');
      return;
    }
    if (imageFile.size > 1024000) {
      setImageError('File size limit exceeded (max. 1MB)');
      return;
    }
    toBase64(imageFile)
      .then((encoded: string) => {
        setImage(encoded);
        setImageError(null);
      })
      .catch((err) => {
        setImageError('File cannot be loaded');
        console.error('Cannot load file', err);
      });
  };

  const toBase64 = (file: File) => {
    return new Promise((resolve, reject) => {
      const fileReader = new FileReader();
      fileReader.readAsDataURL(file);
      fileReader.onload = () => {
        resolve(fileReader.result);
      };
      fileReader.onerror = (error) => {
        reject(error);
      };
    });
  };
  const handleDescription = (event: React.ChangeEvent<HTMLInputElement>) => {
    setDescription(event.target.value);
  };

  const isFormValid = (): boolean => {
    return isNameError || isEanError || isCategoryError;
  };

  const addProduct = async () => {
    const request = {
      name,
      ean,
      category,
      image,
      description,
    };
    setProcessing(true);
    try {
      await ProductApiService.addProduct(request);
      const response = await ProductApiService.getProducts(0);
      dispatch(addProducts(response.data));
      clearFields();
      notifyProductAdded();
    } catch (ex) {
      handleError(ex);
    } finally {
      setProcessing(false);
    }
  };

  const modifyProduct = async () => {
    const request = {
      id,
      name,
      ean,
      category,
      image,
      description,
      version,
    };
    setProcessing(true);
    try {
      await ProductApiService.modifyProduct(request);
      const response = await ProductApiService.getProducts(0);
      dispatch(addProducts(response.data));
      clearFields();
      setEditing(false);
      notifyProductModified();
    } catch (ex) {
      handleError(ex);
    } finally {
      setProcessing(false);
    }
  };

  const clearFields = () => {
    setId(null);
    setVersion(null);
    setName('');
    setEan('');
    setCategory('');
    setImage(null);
    setImageError(null);
    setImageUrl('');
    setDescription('');
    setIsNameError(true);
    setIsNameDirty(false);
    setIsEanError(true);
    setIsEanDirty(false);
    setIsCategoryError(true);
    setIsCategoryDirty(false);
    imgRef.current.value = null;
  };

  const enterEditing = (product: Product): void => {
    ref.current.scrollIntoView({ behavior: 'smooth' });
    setEditing(true);
    setId(product.id);
    setVersion(product.version);
    setName(product.name);
    setIsNameDirty(false);
    setIsNameError(false);
    setEan(product.ean);
    setIsEanDirty(false);
    setIsEanError(false);
    setCategory(product.category);
    setIsCategoryDirty(false);
    setIsCategoryError(false);
    setImageUrl(product.imageUrl);
    setImageError(null);
    setDescription(product.description);
  };

  const cancelEditing = () => {
    setEditing(false);
    clearFields();
  };

  return (
    <>
      <Breadcrumbs elements={productAdminPageBreadcrumbs} />
      <section className='shop shop-form-section section'>
        <div className='container'>
          <div className='row'>
            <div className='col-lg-12 col-12'>
              <div ref={ref} className='shop-form'>
                <h2>Product management</h2>
                <p>&nbsp;</p>
                <div className='form row'>
                  <div className='col-lg-6 col-md-6 col-12'>
                    <div className='form-group'>
                      <label>
                        Name<span>*</span>
                      </label>
                      <input
                        type='text'
                        name='name'
                        role='name'
                        value={name}
                        placeholder=''
                        onChange={handleName}
                      />
                      {isNameDirty && isNameError && (
                        <div role='nameError' className='error'>
                          Field is required
                        </div>
                      )}
                    </div>
                  </div>
                  <div className='col-lg-6 col-md-6 col-12'>
                    <div className='form-group'>
                      <label>
                        Ean<span>*</span>
                      </label>
                      <input
                        type='text'
                        name='ean'
                        role='ean'
                        value={ean}
                        placeholder=''
                        onChange={handleEan}
                      />
                      {isEanDirty && isEanError && (
                        <div role='eanError' className='error'>
                          Field is required
                        </div>
                      )}
                    </div>
                  </div>
                  <div className='col-lg-6 col-md-6 col-12'>
                    <div className='form-group'>
                      <label>
                        Category<span>*</span>
                      </label>
                      <select
                        name='category'
                        role='category'
                        value={category}
                        onChange={handleCategory}
                      >
                        <option value={null}></option>
                        {Object.keys(ProductCategory).map((it) => {
                          return (
                            <option key={it} value={it}>
                              {ProductCategory[it as keyof typeof ProductCategory]}
                            </option>
                          );
                        })}
                      </select>
                      {isCategoryDirty && isCategoryError && (
                        <div role='categoryError' className='error'>
                          Field is required
                        </div>
                      )}
                    </div>
                  </div>
                  <div className='col-lg-6 col-md-6 col-12'>
                    <div className='form-group'>
                      <label>Description</label>
                      <input
                        type='text'
                        name='description'
                        role='description'
                        value={description}
                        placeholder=''
                        onChange={handleDescription}
                      />
                    </div>
                  </div>

                  <div className='col-lg-6 col-md-6 col-12'>
                    <div className='form-group'>
                      <label>Image</label>
                      <input
                        ref={imgRef}
                        style={{ height: '57px' }}
                        type='file'
                        name='image'
                        role='image'
                        placeholder=''
                        onChange={handleImage}
                      />
                      {imageError && <div className='error'>{imageError}</div>}
                    </div>
                  </div>
                  {!editing && (
                    <div className='col-lg-6 col-md-6 col-12'>
                      <img role='loaded-image' className={styles.loadedImage} src={image} alt='' />
                    </div>
                  )}
                  {editing && !!image && (
                    <div className='col-lg-6 col-md-6 col-12'>
                      <img role='loaded-image' className={styles.loadedImage} src={image} alt='' />
                    </div>
                  )}
                  {editing && !image && (
                    <div className='col-lg-6 col-md-6 col-12'>
                      <img
                        role='loaded-image-url'
                        className={styles.loadedImage}
                        src={imageUrl}
                        alt=''
                      />
                    </div>
                  )}
                  {!editing && (
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group form-btn'>
                        <Button
                          role='addBtn'
                          text='Add Product'
                          processing={processing}
                          disabled={isFormValid()}
                          onClick={() => addProduct()}
                        />
                      </div>
                    </div>
                  )}
                  {editing && (
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group form-btn'>
                        <Button
                          role='updateBtn'
                          text='Save'
                          disabled={isFormValid()}
                          processing={processing}
                          onClick={() => modifyProduct()}
                        />
                      </div>
                    </div>
                  )}
                  {editing && (
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group form-btn'>
                        <button
                          role='cancelBtn'
                          className='btn'
                          type='button'
                          onClick={() => cancelEditing()}
                        >
                          Cancel
                        </button>
                      </div>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
          <div role='productsList' className='row'>
            <div className='col-12'>
              {processing && <Preloader />}
              {!processing && <ProductsList onEnterEditing={enterEditing} />}
            </div>
          </div>
        </div>
      </section>
    </>
  );
};

export default ProductAdminPage;
