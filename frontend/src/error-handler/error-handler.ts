// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-nocheck
import { toast } from 'react-toastify';
import { AxiosError } from 'axios';

export const handleError = (error: AxiosError) => {
  if (error?.response?.data?.errors?.length > 0) {
    toast.error(error.response.data.errors[0].message, {
      position: toast.POSITION.TOP_RIGHT,
    });
    console.error(`${error.response.status}-${error.response.data.errors[0].code}`);
    return;
  }
  if (error.response.status === 401) {
    location.href = '/login';
  }
  toast.error('Unexpected error occurred. \nContact us at support@mail.com', {
    position: toast.POSITION.TOP_RIGHT,
  });
  console.error(error);
};
