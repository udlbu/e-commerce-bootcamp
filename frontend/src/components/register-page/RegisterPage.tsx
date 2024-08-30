import React, { useState } from 'react';
import { validateEmail } from '@src/components/register-page/validators/email-validator';
import { validateFirstName } from '@src/components/register-page/validators/first-name-validator';
import { validateLastName } from '@src/components/register-page/validators/last-name-validator';
import { validatePassword } from '@src/components/register-page/validators/password-validator';
import { validateBlank } from '@src/components/shared/validators/blank-validator';
import { validateConfirmPassword } from '@src/components/register-page/validators/confirm-password-validator';
import UserApiService from '@src/api/UserApiService';
import { useNavigate } from 'react-router-dom';
import Breadcrumbs from '@src/components/shared/breadcrumbs/Breadcrumbs';
import { registerPageBreadcrumbs } from '@src/domain/breadcrumbs';
import { handleError } from '@src/error-handler/error-handler';
import Button from '@src/components/shared/button/Button';

const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const [processing, setProcessing] = useState(false);
  const [email, setEmail] = useState('');
  const [isEmailError, setIsEmailError] = useState(true);
  const [isEmailDirty, setIsEmailDirty] = useState(false);

  const [firstName, setFirstName] = useState('');
  const [isFirstNameError, setIsFirstNameError] = useState(true);
  const [isFirstNameDirty, setIsFirstNameDirty] = useState(false);

  const [lastName, setLastName] = useState('');
  const [isLastNameError, setIsLastNameError] = useState(true);
  const [isLastNameDirty, setIsLastNameDirty] = useState(false);

  const [password, setPassword] = useState('');
  const [isPasswordError, setIsPasswordError] = useState(true);
  const [isPasswordDirty, setIsPasswordDirty] = useState(false);

  const [confirmPassword, setConfirmPassword] = useState('');
  const [isConfirmPasswordError, setIsConfirmPasswordError] = useState(true);
  const [isConfirmPasswordDirty, setIsConfirmPasswordDirty] = useState(false);

  const [taxId, setTaxId] = useState('');

  const [country, setCountry] = useState('');
  const [isCountryError, setIsCountryError] = useState(true);
  const [isCountryDirty, setIsCountryDirty] = useState(false);

  const [city, setCity] = useState('');
  const [isCityError, setIsCityError] = useState(true);
  const [isCityDirty, setIsCityDirty] = useState(false);

  const [street, setStreet] = useState('');
  const [isStreetError, setIsStreetError] = useState(true);
  const [isStreetDirty, setIsStreetDirty] = useState(false);

  const [buildingNo, setBuildingNo] = useState('');
  const [isBuildingNoError, setIsBuildingNoError] = useState(true);
  const [isBuildingNoDirty, setIsBuildingNoDirty] = useState(false);

  const [flatNo, setFlatNo] = useState('');

  const [postalCode, setPostalCode] = useState('');
  const [isPostalCodeError, setIsPostalCodeError] = useState(true);
  const [isPostalCodeDirty, setIsPostalCodeDirty] = useState(false);

  const handleEmail = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsEmailDirty(true);
    if (!validateEmail(event.target.value)) {
      setIsEmailError(true);
    } else {
      setIsEmailError(false);
    }
    setEmail(event.target.value);
  };

  const handleFirstName = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsFirstNameDirty(true);
    if (!validateFirstName(event.target.value)) {
      setIsFirstNameError(true);
    } else {
      setIsFirstNameError(false);
    }
    setFirstName(event.target.value);
  };

  const handleLastName = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsLastNameDirty(true);
    if (!validateLastName(event.target.value)) {
      setIsLastNameError(true);
    } else {
      setIsLastNameError(false);
    }
    setLastName(event.target.value);
  };

  const handlePassword = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsPasswordDirty(true);
    if (!validatePassword(event.target.value)) {
      setIsPasswordError(true);
    } else {
      setIsPasswordError(false);
    }
    setPassword(event.target.value);
  };

  const handleConfirmPassword = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsConfirmPasswordDirty(true);
    if (!validateConfirmPassword(event.target.value, password)) {
      setIsConfirmPasswordError(true);
    } else {
      setIsConfirmPasswordError(false);
    }
    setConfirmPassword(event.target.value);
  };

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
    return (
      isFirstNameError ||
      isLastNameError ||
      isEmailError ||
      isPasswordError ||
      isConfirmPasswordError ||
      isCountryError ||
      isCityError ||
      isStreetError ||
      isBuildingNoError ||
      isPostalCodeError
    );
  };

  const register = async () => {
    const request = {
      firstName,
      lastName,
      email,
      password,
      taxId,
      address: {
        country,
        city,
        street,
        buildingNo,
        flatNo,
        postalCode,
      },
    };
    setProcessing(true);
    try {
      await UserApiService.addUser(request);
      navigate('/login');
    } catch (ex) {
      handleError(ex);
    } finally {
      setProcessing(false);
    }
  };
  return (
    <>
      <Breadcrumbs elements={registerPageBreadcrumbs} />
      <section className='shop shop-form-section section'>
        <div className='container'>
          <div className='row'>
            <div className='col-lg-12 col-12'>
              <div className='shop-form'>
                <h2>Register</h2>
                <p>Please register to checkout more quickly</p>
                <div className='form'>
                  <div className='row'>
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>
                          First Name<span>*</span>
                        </label>
                        <input
                          autoComplete='off'
                          type='text'
                          name='firstName'
                          role='firstName'
                          value={firstName}
                          placeholder=''
                          onChange={handleFirstName}
                        />
                        {isFirstNameDirty && isFirstNameError && (
                          <div className='error'>
                            First Name is not valid (at least 3 character)
                          </div>
                        )}
                      </div>
                    </div>
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>
                          Last Name<span>*</span>
                        </label>
                        <input
                          autoComplete='off'
                          type='text'
                          name='lastName'
                          role='lastName'
                          value={lastName}
                          placeholder=''
                          onChange={handleLastName}
                        />
                        {isLastNameDirty && isLastNameError && (
                          <div className='error'>Last Name is not valid (at least 4 character)</div>
                        )}
                      </div>
                    </div>
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>
                          Email Address<span>*</span>
                        </label>
                        <input
                          autoComplete='off'
                          type='email'
                          name='email'
                          role='email'
                          value={email}
                          placeholder=''
                          onChange={handleEmail}
                        />
                        {isEmailDirty && isEmailError && (
                          <div className='error'>Email is not valid (pattern: aaa@aaa.bbb)</div>
                        )}
                      </div>
                    </div>
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>Tax Id</label>
                        <input
                          autoComplete='off'
                          type='text'
                          name='taxId'
                          role='taxId'
                          value={taxId}
                          placeholder=''
                          onChange={handleTaxId}
                        />
                      </div>
                    </div>
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>
                          Your Password<span>*</span>
                        </label>
                        <input
                          autoComplete='off'
                          type='password'
                          name='password'
                          role='password'
                          value={password}
                          placeholder=''
                          onChange={handlePassword}
                        />
                        {isPasswordDirty && isPasswordError && (
                          <div className='error'>
                            Password is not valid (at least 5 character without spaces)
                          </div>
                        )}
                      </div>
                    </div>
                    <div className='col-lg-6 col-md-6 col-12'>
                      <div className='form-group'>
                        <label>
                          Confirm Password<span>*</span>
                        </label>
                        <input
                          autoComplete='off'
                          type='password'
                          name='confirmPassword'
                          role='confirmPassword'
                          value={confirmPassword}
                          placeholder=''
                          onChange={handleConfirmPassword}
                        />
                        {isConfirmPasswordDirty && isConfirmPasswordError && (
                          <div className='error'>Password does not match</div>
                        )}
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
                          role='buildingNo'
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
                          role='flatNo'
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
                          role='postalCode'
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
                          role='registerBtn'
                          text='Register'
                          processing={processing}
                          disabled={anyErrors()}
                          onClick={() => register()}
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

export default RegisterPage;
