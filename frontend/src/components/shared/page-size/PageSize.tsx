import React from 'react';

interface PageSizeProps {
  pageSize: number;
  onPageSizeChange: (page: number) => void;
}

export const DEFAULT_PAGE_SIZE = 9;

const PAGE_SIZES = [3, 6, 9, 12, 15, 60, 90];

const PageSize: React.FC<PageSizeProps> = ({ pageSize, onPageSizeChange }) => {
  const [state, setState] = React.useState(pageSize);
  const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const value = Number(event.target.value);
    setState(value);
    onPageSizeChange(value);
  };
  return (
    <div className='shop-top'>
      <div className='shop-shorter'>
        <div className='single-shorter'>
          <label role='page-size-label'>Show:&nbsp;</label>
          <select role='page-size-value' value={state} onChange={handlePageSizeChange}>
            {PAGE_SIZES.map((it) => (
              <option role='page-size-option' key={it} value={it}>
                {it}
              </option>
            ))}
          </select>
        </div>
      </div>
    </div>
  );
};

export default PageSize;
