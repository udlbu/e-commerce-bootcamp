import React from 'react';
import { Breadcrumb } from '@src/domain/breadcrumbs';
import { useNavigate } from 'react-router-dom';

interface BreadcrumbsProps {
  elements: Breadcrumb[];
}

const Breadcrumbs: React.FC<BreadcrumbsProps> = ({ elements }) => {
  const navigate = useNavigate();
  const ul = elements.map((it, i) => {
    if (i == elements.length - 1) {
      return (
        <li key={it.label} role='lastBreadcrumb' className='active'>
          <a href='#'>{it.label}</a>
        </li>
      );
    }
    return (
      <li key={it.label} role='breadcrumb'>
        <a onClick={() => navigate(it.link)}>
          {it.label}
          <i className='ti-arrow-right'></i>
        </a>
      </li>
    );
  });

  return (
    <div className='breadcrumbs'>
      <div className='container'>
        <div className='row'>
          <div className='col-12'>
            <div className='bread-inner'>
              <ul className='bread-list'>
                <li>
                  <a onClick={() => navigate('/')}>
                    Home<i className='ti-arrow-right'></i>
                  </a>
                </li>
                {ul}
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Breadcrumbs;
