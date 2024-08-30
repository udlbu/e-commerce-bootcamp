import React, { useState } from 'react';
import { useAppDispatch, useAppSelector } from '@src/store/hooks';
import { validatePassword } from '@src/components/register-page/validators/password-validator';
import { validateConfirmPassword } from '@src/components/register-page/validators/confirm-password-validator';
import UserApiService from '@src/api/UserApiService';
import { useNavigate } from 'react-router-dom';
import { validateBlank } from '@src/components/shared/validators/blank-validator';
import { handleError } from '@src/error-handler/error-handler';
import { notifyPasswordChanged } from '@src/notifications/notifications';
import { logout } from '@src/store/features/userSlice';

const ChangePasswordPage: React.FC = () => {
  const user = useAppSelector((state) => state.user);
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const [processing, setProcessing] = useState(false);

  const [oldPassword, setOldPassword] = useState('');
  const [isOldPasswordError, setIsOldPasswordError] = useState(true);
  const [isOldPasswordDirty, setIsOldPasswordDirty] = useState(false);

  const [newPassword, setNewPassword] = useState('');
  const [isNewPasswordError, setIsNewPasswordError] = useState(true);
  const [isNewPasswordDirty, setIsNewPasswordDirty] = useState(false);

  const [confirmPassword, setConfirmPassword] = useState('');
  const [isConfirmPasswordError, setIsConfirmPasswordError] = useState(true);
  const [isConfirmPasswordDirty, setIsConfirmPasswordDirty] = useState(false);

  const handleOldPassword = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsOldPasswordDirty(true);
    if (!validateBlank(event.target.value)) {
      setIsOldPasswordError(true);
    } else {
      setIsOldPasswordError(false);
    }
    setOldPassword(event.target.value);
  };

  const handleNewPassword = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsNewPasswordDirty(true);
    if (!validatePassword(event.target.value)) {
      setIsNewPasswordError(true);
    } else {
      setIsNewPasswordError(false);
    }
    setNewPassword(event.target.value);
  };

  const handleConfirmPassword = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsConfirmPasswordDirty(true);
    if (!validateConfirmPassword(event.target.value, newPassword)) {
      setIsConfirmPasswordError(true);
    } else {
      setIsConfirmPasswordError(false);
    }
    setConfirmPassword(event.target.value);
  };

  const anyErrors = (): boolean => {
    return isOldPasswordError || isNewPasswordError || isConfirmPasswordError;
  };

  const changePassword = async () => {
    const request = {
      oldPassword,
      newPassword,
    };
    setProcessing(true);
    try {
      await UserApiService.changePassword(user.id, request);
      notifyPasswordChanged();
      await UserApiService.logout();
      dispatch(logout());
      navigate('/login');
    } catch (ex) {
      handleError(ex);
    } finally {
      setProcessing(false);
    }
  };
  return (
    <>
      <section className='shop shop-form-section section'>
        <div className='container'>
          <div className='row'>
            <div className='col-lg-12 col-12'>
              <div className='shop-form'>
                <h2>Change password</h2>
                <p>&nbsp;</p>
                <div className='form'>
                  <div className='col-lg-6 col-md-6 col-12 offset-lg-3'>
                    <div className='form-group'>
                      <label>
                        Old Password<span>*</span>
                      </label>
                      <input
                        autoComplete='off'
                        type='password'
                        name='oldPassword'
                        role='old-password'
                        value={oldPassword}
                        placeholder=''
                        onChange={handleOldPassword}
                      />
                      {isOldPasswordDirty && isOldPasswordError && (
                        <div className='error'>Password is not valid</div>
                      )}
                    </div>
                  </div>
                  <div className='col-lg-6 col-md-6 col-12 offset-lg-3'>
                    <div className='form-group'>
                      <label>
                        New Password<span>*</span>
                      </label>
                      <input
                        autoComplete='off'
                        type='password'
                        name='newPassword'
                        role='new-password'
                        value={newPassword}
                        placeholder=''
                        onChange={handleNewPassword}
                      />
                      {isNewPasswordDirty && isNewPasswordError && (
                        <div className='error'>
                          Password is not valid (at least 5 character without spaces)
                        </div>
                      )}
                    </div>
                  </div>
                  <div className='col-lg-6 col-md-6 col-12 offset-lg-3'>
                    <div className='form-group'>
                      <label>
                        Confirm Password<span>*</span>
                      </label>
                      <input
                        autoComplete='off'
                        type='password'
                        name='confirmPassword'
                        role='confirm-password'
                        value={confirmPassword}
                        placeholder=''
                        onChange={handleConfirmPassword}
                      />
                      {isConfirmPasswordDirty && isConfirmPasswordError && (
                        <div className='error'>Password does not match</div>
                      )}
                    </div>
                  </div>
                  <div className='col-lg-12 col-md-12 col-12 offset-lg-5'>
                    <div className='form-group form-btn'>
                      <button
                        role='change-password-btn'
                        className='btn'
                        type='button'
                        disabled={anyErrors() || processing}
                        onClick={() => changePassword()}
                      >
                        Change
                      </button>
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

export default ChangePasswordPage;
