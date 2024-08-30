import React from 'react';

// This is static component for demonstration purpose. It can be implemented as an complex exercise
const ReviewTab: React.FC = () => {
  return (
    <div className='row'>
      <div className='col-12'>
        <div className='ratting-main'>
          <div className='avg-ratting'>
            <h4>
              4.5 <span>(Overall)</span>
            </h4>
            <span>Based on 1 Comments</span>
          </div>
          <div className='single-rating'>
            <div className='rating-author'>
              <img src='https://via.placeholder.com/200x200' alt='#' />
            </div>
            <div className='rating-des'>
              <h6>User 1</h6>
              <div className='ratings'>
                <ul className='rating'>
                  <li>
                    <i className='fa fa-star'></i>
                  </li>
                  <li>
                    <i className='fa fa-star'></i>
                  </li>
                  <li>
                    <i className='fa fa-star'></i>
                  </li>
                  <li>
                    <i className='fa fa-star-half-o'></i>
                  </li>
                  <li>
                    <i className='fa fa-star-o'></i>
                  </li>
                </ul>
                <div className='rate-count'>
                  (<span>3.5</span>)
                </div>
              </div>
              <p>Review 1</p>
            </div>
          </div>
          <div className='single-rating'>
            <div className='rating-author'>
              <img src='https://via.placeholder.com/200x200' alt='#' />
            </div>
            <div className='rating-des'>
              <h6>User 2</h6>
              <div className='ratings'>
                <ul className='rating'>
                  <li>
                    <i className='fa fa-star'></i>
                  </li>
                  <li>
                    <i className='fa fa-star'></i>
                  </li>
                  <li>
                    <i className='fa fa-star'></i>
                  </li>
                  <li>
                    <i className='fa fa-star'></i>
                  </li>
                  <li>
                    <i className='fa fa-star'></i>
                  </li>
                </ul>
                <div className='rate-count'>
                  (<span>5.0</span>)
                </div>
              </div>
              <p>Review 2</p>
            </div>
          </div>
        </div>
        <div className='comment-review'>
          <div className='add-review'>
            <h5>Add A Review</h5>
            <p>Your email address will not be published. Required fields are marked</p>
          </div>
          <h4>Your Rating</h4>
          <div className='review-inner'>
            <div className='ratings'>
              <ul className='rating'>
                <li>
                  <i className='fa fa-star'></i>
                </li>
                <li>
                  <i className='fa fa-star'></i>
                </li>
                <li>
                  <i className='fa fa-star'></i>
                </li>
                <li>
                  <i className='fa fa-star'></i>
                </li>
                <li>
                  <i className='fa fa-star'></i>
                </li>
              </ul>
            </div>
          </div>
        </div>
        <div className='form row'>
          <div className='col-lg-6 col-12'>
            <div className='form-group'>
              <label>
                Your Name<span>*</span>
              </label>
              <input type='text' name='name' placeholder='' />
            </div>
          </div>
          <div className='col-lg-6 col-12'>
            <div className='form-group'>
              <label>
                Your Email<span>*</span>
              </label>
              <input type='email' name='email' placeholder='' />
            </div>
          </div>
          <div className='col-lg-12 col-12'>
            <div className='form-group'>
              <label>
                Write a review<span>*</span>
              </label>
              <textarea name='message' placeholder=''></textarea>
            </div>
          </div>
          <div className='col-lg-12 col-12'>
            <div className='form-group button5'>
              <button type='submit' className='btn'>
                Submit
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ReviewTab;
