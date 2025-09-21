import React from 'react';
import Gallery from '../components/product/Gallery';

import ProductInfo from '../components/product/ProductInfo';
import './ProductPage.css';
import ThreeColumnLayout from '../components/product/ThreeColumnLayout';
import Accordion from '../components/product/Accordion';
import VerticalFeatureList from '../components/product/VerticalFeatureList';

import ImageTextSection from '../components/homepage/ImageTextSection';
import Scientist from '../assets/images/Scientist.webp';
import RandomProductRow from '../components/common/RandomProducts';
import AdminActionButtons from '../components/admin/AdminActionButtons';
import { useProductPage } from '../hooks/useProductPage';
import { t } from 'i18next';
import CategorySelect from '../components/admin/CategorySelect';
import ProductDescription from '../components/product/ProductDescription';

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
    featureItems,
    allCategories,
    selectedCategories,
    handleCategoryChange,
    images,
    setImages
  } = useProductPage();


  if (!product) return <div>Product not found</div>;

  return (

    <div key={isAdmin ? 'admin' : 'user'} className="product-page">
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
            <CategorySelect
              allCategories={allCategories}
              selectedSlugs={selectedCategories.map(cat => cat.slug)}
              onChange={handleCategoryChange}
            />
          )}
          {!isAdmin && (<ProductInfo
            title={title}
            price={price}
            image={
              isFullProduct(product)
                ? product.images[0]?.imageUrl
                : product.thumbnailUrl
            }
            slug={product.slug}
            onTitleChange={setTitle}
            onPriceChange={setPrice}
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
          <VerticalFeatureList items={featureItems} />
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
          imageSrc={Scientist}
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
