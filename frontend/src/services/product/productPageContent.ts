import { TFunction } from 'i18next';

import Clock from '../../assets/images/product/clock.webp';
import Hand from '../../assets/images/product/hand.webp';
import Finger from '../../assets/images/product/finger.webp';
import Swab from '../../assets/images/product/swab.webp';
import Urine from '../../assets/images/product/urine.webp';
import Saliva from '../../assets/images/product/saliva.webp';
import Icon1 from '../../assets/images/product/Icon1.webp';
import Icon2 from '../../assets/images/product/Icon2.webp';
import Icon3 from '../../assets/images/product/Icon3.webp';

export const getFaqItems = (t: TFunction) => [
  {
    title: t('productPage.faq.return.title'),
    content: t('productPage.faq.return.content'),
  },
  {
    title: t('productPage.faq.shipping.title'),
    content: t('productPage.faq.shipping.content'),
  },
  {
    title: t('productPage.faq.tracking.title'),
    content: t('productPage.faq.tracking.content'),
  },
];

export const getColumns = (t: TFunction) => [
  {
    icon: Icon1,
    header: t('productPage.sections.order.title'),
    description: t('productPage.sections.order.description'),
  },
  {
    icon: Icon2,
    header: t('productPage.sections.test.title'),
    description: t('productPage.sections.test.description'),
  },
  {
    icon: Icon3,
    header: t('productPage.sections.results.title'),
    description: t('productPage.sections.results.description'),
  },
];

export const getFeatureItems = (t: TFunction) => [
  { id: 'clock', icon: Clock, text: t('productPage.features.clock') },
  { id: 'hand', icon: Hand, text: t('productPage.features.hand') },
  { id: 'finger', icon: Finger, text: t('productPage.features.finger') },
  { id: 'swab', icon: Swab, text: t('productPage.features.swab') },
  { id: 'urine', icon: Urine, text: t('productPage.features.urine') },
  { id: 'saliva', icon: Saliva, text: t('productPage.features.saliva') },
];
