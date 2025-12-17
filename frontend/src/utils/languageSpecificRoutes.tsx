import { Route } from "react-router-dom";
import HomePage from "../pages/home/HomePage";
import ProductPage from "../pages/product/ProductPage";
import ShopPage from "../pages/product/ShopPage";
import BlogPage from "../pages/blog/BlogPage";
import BlogPostPage from "../pages/blog/BlogPostPage";
import TermsConditionsPage from "../pages/info/TermsConditionsPage";
import ImprintPage from "../pages/info/ImprintPage";
import ContactPage from "../pages/info/ContactPage";
import DeliveryPage from "../pages/info/DeliveryPage";
import PrivacyPolicyPage from "../pages/info/PrivacyPolicyPage";
import PaymentPage from "../pages/checkout/PaymentPage";
import CartPage from "../pages/cart/CartPage";
import SuccessPage from "../pages/checkout/SuccessPage";
import ShippingPage from "../pages/checkout/ShippingPage";

export const languageSpecificRoutes = (
  <>
    <Route index element={<HomePage />} />
    <Route path="shop" element={<ShopPage />} />
    <Route path="product/:slug" element={<ProductPage />} />
    <Route path="blog" element={<BlogPage />} />
    <Route path="blog/:slug" element={<BlogPostPage />} />
    <Route path="checkout" element={<ShippingPage />} />
    <Route path="cart" element={<CartPage />} />
    <Route path="payment" element={<PaymentPage />} />
    <Route path="success" element={<SuccessPage />} />
    <Route path="delivery" element={<DeliveryPage />} />
    <Route path="privacy-policy" element={<PrivacyPolicyPage />} />
    <Route path="terms-and-conditions" element={<TermsConditionsPage />} />
    <Route path="contact" element={<ContactPage />} />
    <Route path="imprint" element={<ImprintPage />} />
  </>
);
