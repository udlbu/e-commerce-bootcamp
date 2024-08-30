import React, { useState } from 'react';
import Breadcrumbs from '@src/components/shared/breadcrumbs/Breadcrumbs';
import { useNavigate } from 'react-router-dom';
import './Login.scss';
import UserApiService from '@src/api/UserApiService';
import { addUser } from '@src/store/features/userSlice';
import { useAppDispatch } from '@src/store/hooks';
import { loginPageBreadcrumbs } from '@src/domain/breadcrumbs';
import { validateBlank } from '@src/components/shared/validators/blank-validator';
import Button from '@src/components/shared/button/Button';

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const [processing, setProcessing] = useState(false);

  const [email, setEmail] = useState('');
  const [isEmailError, setIsEmailError] = useState(true);
  const [isEmailDirty, setIsEmailDirty] = useState(false);

  const [password, setPassword] = useState('');
  const [isPasswordError, setIsPasswordError] = useState(true);
  const [isPasswordDirty, setIsPasswordDirty] = useState(false);

  const [loginError, setLoginError] = useState(false);

  const handleEmail = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsEmailDirty(true);
    if (!validateBlank(event.target.value)) {
      setIsEmailError(true);
    } else {
      setIsEmailError(false);
    }
    setEmail(event.target.value);
    setLoginError(false);
  };

  const handlePassword = (event: React.ChangeEvent<HTMLInputElement>) => {
    setIsPasswordDirty(true);
    if (!validateBlank(event.target.value)) {
      setIsPasswordError(true);
    } else {
      setIsPasswordError(false);
    }
    setPassword(event.target.value);
    setLoginError(false);
  };

  const authenticate = async () => {
    setProcessing(true);
    try {
      await UserApiService.authenticate(email, password);
      const response = await UserApiService.getUserCurrent();
      setLoginError(false);
      dispatch(addUser(response.data));
      navigate('/');
    } catch (ex) {
      setLoginError(true);
    } finally {
      setProcessing(false);
    }
  };

  return (
    <>
      <Breadcrumbs elements={loginPageBreadcrumbs} />
      <section className='shop login section'>
        <div className='container'>
          <div className='row'>
            <div className='col-lg-6 offset-lg-3 col-12'>
              <div className='login-form'>
                <h2>Login</h2>
                <div className='form row'>
                  <div className='col-12'>
                    <div className='form-group'>
                      <label>
                        Your Email<span>*</span>
                      </label>
                      <input
                        type='email'
                        name='email'
                        role='email'
                        value={email}
                        placeholder=''
                        onChange={handleEmail}
                      />
                      {isEmailDirty && isEmailError && (
                        <div role='emailError' className='error'>
                          Field is required
                        </div>
                      )}
                    </div>
                  </div>
                  <div className='col-12'>
                    <div className='form-group'>
                      <label>
                        Your Password<span>*</span>
                      </label>
                      <input
                        type='password'
                        name='password'
                        role='password'
                        value={password}
                        placeholder=''
                        onChange={handlePassword}
                      />
                      {isPasswordDirty && isPasswordError && (
                        <div role='passwordError' className='error'>
                          Field is required
                        </div>
                      )}
                      {loginError && (
                        <div role='loginError' className='error'>
                          Invalid email or password
                        </div>
                      )}
                    </div>
                  </div>
                  <div className='col-12'>
                    <div className='form-group form-btn'>
                      <Button
                        role='loginBtn'
                        text='Login'
                        onClick={authenticate}
                        processing={processing}
                      />
                      <a role='registerBtn' className='btn' onClick={() => navigate('/register')}>
                        Register
                      </a>
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

export default LoginPage;
