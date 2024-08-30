import React from 'react';
import ProgressIcon from '@src/components/shared/progress-icon/ProgressIcon';

interface ButtonProps {
  processing: boolean;
  text: string;
  role: string;
  onClick: () => void;
  disabled?: boolean;
}
const Button: React.FC<ButtonProps> = ({ processing, text, role, onClick, disabled }) => {
  return (
    <>
      {!processing && (
        <button
          role={role}
          className='btn'
          type='button'
          onClick={() => onClick()}
          disabled={!!disabled}
        >
          {text}
        </button>
      )}
      {processing && <ProgressIcon />}
    </>
  );
};

export default Button;
