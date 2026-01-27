import React from 'react';
import Gallery from '../../components/product/Gallery';

import ProductInfo from '../../components/product/ProductInfo';
import './ProductPage.css';
import ThreeColumnLayout from '../../components/product/ThreeColumnLayout';
import Accordion from '../../components/product/Accordion';
import ImageTextSection from '../../components/common/layout/ImageTextSection';
import ScientistImageSmall from '../../assets/images/product/Scientist_480.webp';
import ScientistImageLarge from '../../assets/images/product/Scientist_768.webp';
import RandomProductRow from '../../components/common/product/RandomProducts';
import AdminActionButtons from '../../components/admin/AdminActionButtons';
import { useProductPage } from '../../hooks/product/useProductPage';
import { t } from 'i18next';
import CategorySelect from '../../components/admin/CategorySelect';
import ProductDescription from '../../components/product/ProductDescription';
import { Helmet } from 'react-helmet';
import ProductAvailabilitySelect from '../../components/product/ProductAvailabilitySelect';

const ProductPage: React.FC = () => {
  const {
    slug,
    product,
    title,
    setTitle,
    description,
    setDescription,
    price,
    setPrice,
    isFullProduct,
    isAdmin,
    handleSave,
    showConfirmDelete,
    handleDeleteClick,
    handleConfirmDelete,
    setShowConfirmDelete,
    showSuccessModal,
    setShowSuccessModal,
    successMessage,
    faqItems,
    columns,
    allCategories,
    selectedCategories,
    handleCategoryChange,
    images,
    setImages,
    enabled,
    setEnabled
  } = useProductPage();


  if (!product) return <div>Product not found</div>;

  return (

    <div key={isAdmin ? 'admin' : 'user'} className="product-page">

      <Helmet>
        <title>{title}</title>
        <meta name="description" content={description} />
        <meta property="og:title" content={title} />
        <meta property="og:description" content={description} />
        <meta property="og:image" content={images[0]?.imageUrlSmall || '/default-image.jpg'} />
        <meta property="og:url" content={`https://mystore.com/product/${slug}`} />
      </Helmet>

      <div className="product-main-section">
        <div className="product-gallery">
          <Gallery
            images={images}
            setImages={setImages}
            isAdmin={isAdmin}
          />
        </div>

        <div className="product-info">
          {isAdmin && (
            <ProductAvailabilitySelect
              enabled={enabled}
              onChange={setEnabled}
            />
          )}
          {isAdmin && (
            <CategorySelect
              allCategories={allCategories}
              selectedSlugs={selectedCategories.map(cat => cat.slug)}
              onChange={handleCategoryChange}
            />
          )}
          {!isAdmin && (<ProductInfo
            title={title}
            price={price}
            image={product.images?.[0]?.imageUrlSmall}
            categories={product.categories}
            slug={product.slug}
            onTitleChange={setTitle}
            onPriceChange={setPrice}
            enabled={enabled}
          />)}
          {isAdmin && (
            <AdminActionButtons
              isNew={slug === "new"}
              onSave={handleSave}
              onDelete={handleDeleteClick}
              showConfirmDelete={showConfirmDelete}
              onConfirmDelete={handleConfirmDelete}
              onCancelDelete={() => setShowConfirmDelete(false)}
              showSuccessModal={showSuccessModal}
              successMessage={successMessage}
              onCloseSuccess={() => setShowSuccessModal(false)}
            />
          )}
          <ProductDescription
            description={isFullProduct(product) ? description : ''}
            isAdmin={isAdmin}
            onDescriptionChange={setDescription}
          />
        </div>
      </div>

      <div className="product-sections">
        <ThreeColumnLayout columns={columns} />
        <div className="accordion-section">
          <h2 className="faq-title">{t('productPage.faq.title')}</h2>
          <Accordion items={faqItems} />
        </div>

        <ImageTextSection
          smallSrc={ScientistImageSmall}
          mediumSrc={ScientistImageLarge}
          imageAlt="No sample"
          title={t('productPage.banner.title')}
          text={t('productPage.banner.subtitle')}
          buttonText={t('productPage.buttons.addToCart')}
          reverse={true}
          displayButton={false}
          buttonLink="/shop"
        />

        <RandomProductRow />
      </div>
    </div>
  );
}

export default ProductPage;
