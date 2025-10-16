import { Route } from "react-router-dom";
import HomePage from "../pages/HomePage";
import ProductPage from "../pages/ProductPage";
import ShopPage from "../pages/ShopPage";
import BlogPage from "../pages/BlogPage";
import BlogPostPage from "../pages/BlogPostPage";
import TermsConditionsPage from "../pages/TermsConditionsPage";
import ImprintPage from "../pages/ImprintPage";
import ContactPage from "../pages/ContactPage";
import DeliveryPage from "../pages/DeliveryPage";
import PrivacyPolicyPage from "../pages/PrivacyPolicyPage";
import PaymentPage from "../pages/PaymentPage";
import CartPage from "../pages/CartPage";
import SuccessPage from "../pages/SuccessPage";
import ShippingPage from "../pages/ShippingPage";

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
