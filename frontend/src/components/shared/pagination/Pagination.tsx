import React from 'react';

interface PaginationProps {
  page: number;
  total: number;
  pageSize: number;
  onPageChange: (page: number) => void;
}

const Pagination: React.FC<PaginationProps> = ({ page, total, pageSize, onPageChange }) => {
  const totalNumberOfPages = Math.ceil(total / pageSize);
  const handlePageChange = (page: number) => {
    if (page >= 0 && page < totalNumberOfPages) {
      onPageChange(page);
    }
  };
  return (
    <div className='pagination-container'>
      <div className='pagination'>
        <ul className='pagination-list'>
          <span role='prev-page' className={page > 0 ? '' : 'page-nav-hidden'}>
            <li>
              <a onClick={() => handlePageChange(page - 1)}>
                <i className='ti-arrow-left'></i>
              </a>
            </li>
          </span>
          <span role='current-page' className='page'>
            {page + 1}&nbsp;of&nbsp;{totalNumberOfPages}
          </span>
          <span role='next-page' className={page < totalNumberOfPages - 1 ? '' : 'page-nav-hidden'}>
            <li>
              <a role='next-page-anchor' onClick={() => handlePageChange(page + 1)}>
                <i className='ti-arrow-right'></i>
              </a>
            </li>
          </span>
        </ul>
      </div>
    </div>
  );
};

export default Pagination;
