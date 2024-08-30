import { Offer } from '@src/types/offer';

export interface Breadcrumb {
  link: string;
  label: string;
}

export const listingPageBreadcrumb = [
  {
    label: 'Listing',
    link: '/listing',
  },
];

export const showItemBreadcrumb = (offer: Offer) => {
  return [
    {
      label: 'Listing',
      link: `/listing`,
    },
    {
      label: 'Show Item',
      link: `/offer/${offer.id}`,
    },
  ];
};

export const cartPageBreadcrumb = [
  {
    label: 'Cart',
    link: '/cart',
  },
];

export const loginPageBreadcrumbs = [
  {
    label: 'Login',
    link: '/login',
  },
];

export const offerAdminPageBreadcrumbs = [
  {
    label: 'Offer Management',
    link: '/offers',
  },
];

export const productAdminPageBreadcrumbs = [
  {
    label: 'Product Management',
    link: '/products',
  },
];

export const registerPageBreadcrumbs = [
  {
    label: 'Register',
    link: '/register',
  },
];

export const myOrderPageBreadcrumb = [
  {
    label: 'My Orders',
    link: '/orders',
  },
];

export const editUserPageBreadcrumbs = [
  {
    label: 'Edit Profile',
    link: '/user',
  },
];
