import React, { useState } from 'react';
import { validateBlank } from '@src/components/shared/validators/blank-validator';
import Breadcrumbs from '@src/components/shared/breadcrumbs/Breadcrumbs';
import { editUserPageBreadcrumbs } from '@src/domain/breadcrumbs';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import UserApiService from '@src/api/UserApiService';
import { addUser } from '@src/store/features/userSlice';
import { handleError } from '@src/error-handler/error-handler';
import { notifyAccountModified } from '@src/notifications/notifications';
import Button from '@src/components/shared/button/Button';

const EditUserPage: React.FC = () => {
  const user = useAppSelector((state) => state.user);
  const dispatch = useAppDispatch();
  const [processing, setProcessing] = useState(false);

  const [taxId, setTaxId] = useState(user.taxId || '');

  const [country, setCountry] = useState(user.address.country);
  const [isCountryError, setIsCountryError] = useState(false);
  const [isCountryDirty, setIsCountryDirty] = useState(false);

  const [city, setCity] = useState(user.address.city);
  const [isCityError, setIsCityError] = useState(false);
  const [isCityDirty, setIsCityDirty] = useState(false);

  const [street, setStreet] = useState(user.address.street);
  const [isStreetError, setIsStreetError] = useState(false);
  const [isStreetDirty, setIsStreetDirty] = useState(false);

  const [buildingNo, setBuildingNo] = useState(user.address.buildingNo);
  const [isBuildingNoError, setIsBuildingNoError] = useState(false);
  const [isBuildingNoDirty, setIsBuildingNoDirty] = useState(false);

  const [flatNo, setFlatNo] = useState(user.address.flatNo || '');

  const [postalCode, setPostalCode] = useState(user.address.postalCode);
  const [isPostalCodeError, setIsPostalCodeError] = useState(false);
  const [isPostalCodeDirty, setIsPostalCodeDirty] = useState(false);

  const handleTaxId = (event: React.ChangeEvent<HTMLInputElement>) => {
    setTaxId(event.target.value);
  };

  const handleCountry = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsCountryDirty(true);
    if (!validateBlank(event.target.value)) {
      setIsCountryError(true);
    } else {
      setIsCountryError(false);
    }
    setCountry(event.target.value);
  };

  const handleCity = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsCityDirty(true);
    if (!validateBlank(event.target.value)) {
      setIsCityError(true);
    } else {
      setIsCityError(false);
    }
    setCity(event.target.value);
  };

  const handleStreet = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsStreetDirty(true);
    if (!validateBlank(event.target.value)) {
      setIsStreetError(true);
    } else {
      setIsStreetError(false);
    }
    setStreet(event.target.value);
  };

  const handleBuildingNo = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsBuildingNoDirty(true);
    if (!validateBlank(event.target.value)) {
      setIsBuildingNoError(true);
    } else {
      setIsBuildingNoError(false);
    }
    setBuildingNo(event.target.value);
  };

  const handleFlatNo = (event: React.ChangeEvent<HTMLInputElement>) => {
    setFlatNo(event.target.value);
  };

  const handlePostalCode = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsPostalCodeDirty(true);
    if (!validateBlank(event.target.value)) {
      setIsPostalCodeError(true);
    } else {
      setIsPostalCodeError(false);
    }
    setPostalCode(event.target.value);
  };

  const anyErrors = (): boolean => {
    return isCountryError || isCityError || isStreetError || isBuildingNoError || isPostalCodeError;
  };

  const modifyUser = async () => {
    const request = {
      id: user.id,
      email: user.email,
      firstName: user.firstName,
      lastName: user.lastName,
      version: user.version,
      taxId,
      address: {
        id: user.address.id,
        country,
        city,
        street,
        buildingNo,
        flatNo,
        postalCode,
        version: user.address.version,
      },
    };
    setProcessing(true);

    try {
      await UserApiService.modifyUser(request);
      const userResponse = await UserApiService.getUserCurrent();
      dispatch(addUser(userResponse.data));
      notifyAccountModified();
    } catch (ex) {
      handleError(ex);
    } finally {
      setProcessing(false);
    }
  };
  return (
    <>
      <Breadcrumbs elements={editUserPageBreadcrumbs} />
      <section className='shop shop-form-section section'>
        <div className='container'>
          <div className='row'>
            <div className='col-lg-12 col-12'>
              <div className='shop-form'>
                <h2>Edit Profile</h2>
                <p role='edit-user-title'>
                  Hello, {user.firstName}&nbsp;{user.lastName} ({user.email})
                </p>
                <div className='form'>
                  <div className='row'>
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>Tax Id</label>
                        <input
                          autoComplete='off'
                          type='text'
                          name='taxId'
                          role='tax-id'
                          value={taxId}
                          placeholder=''
                          onChange={handleTaxId}
                        />
                      </div>
                    </div>
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>
                          Country<span>*</span>
                        </label>
                        <input
                          autoComplete='off'
                          type='text'
                          name='country'
                          role='country'
                          value={country}
                          placeholder=''
                          onChange={handleCountry}
                        />
                        {isCountryDirty && isCountryError && (
                          <div className='error'>Field is required</div>
                        )}
                      </div>
                    </div>
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>
                          City<span>*</span>
                        </label>
                        <input
                          autoComplete='off'
                          type='text'
                          name='city'
                          role='city'
                          value={city}
                          placeholder=''
                          onChange={handleCity}
                        />
                        {isCityDirty && isCityError && (
                          <div className='error'>Field is required</div>
                        )}
                      </div>
                    </div>
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>
                          Street<span>*</span>
                        </label>
                        <input
                          autoComplete='off'
                          type='text'
                          name='street'
                          role='street'
                          value={street}
                          placeholder=''
                          onChange={handleStreet}
                        />
                        {isStreetDirty && isStreetError && (
                          <div className='error'>Field is required</div>
                        )}
                      </div>
                    </div>
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>
                          Building no.<span>*</span>
                        </label>
                        <input
                          autoComplete='off'
                          type='text'
                          name='buildingNo'
                          role='building-no'
                          value={buildingNo}
                          placeholder=''
                          onChange={handleBuildingNo}
                        />
                        {isBuildingNoDirty && isBuildingNoError && (
                          <div className='error'>Field is required</div>
                        )}
                      </div>
                    </div>
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>Flat no.</label>
                        <input
                          autoComplete='off'
                          type='text'
                          name='flatNo'
                          role='flat-no'
                          value={flatNo}
                          placeholder=''
                          onChange={handleFlatNo}
                        />
                      </div>
                    </div>
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>
                          Postal Code<span>*</span>
                        </label>
                        <input
                          autoComplete='off'
                          type='text'
                          name='postalCode'
                          role='postal-code'
                          value={postalCode}
                          placeholder=''
                          onChange={handlePostalCode}
                        />
                        {isPostalCodeDirty && isPostalCodeError && (
                          <div className='error'>Field is required</div>
                        )}
                      </div>
                    </div>
                    <div className='col-lg-12 col-md-12 col-12'>
                      <div className='form-group form-btn'>
                        <Button
                          role='save-btn'
                          text='Save'
                          processing={processing}
                          disabled={anyErrors()}
                          onClick={() => modifyUser()}
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  );
};

export default EditUserPage;
