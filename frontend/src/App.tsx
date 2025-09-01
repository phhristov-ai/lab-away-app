import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import ShopPage from './pages/ShopPage';
import ProductPage from './pages/ProductPage';
import BlogPage from './pages/BlogPage';
import BlogPostPage from './pages/BlogPostPage';
import Footer from './components/footer/Footer';
import './App.css';
import CartPage from './pages/CartPage';
import BillingPage from './pages/BillingPage';
import PaymentPage from './pages/PaymentPage';
import { CartProvider } from './context/CartContext';
import ScrollToTop from './components/common/ScrollToTop';
import { CheckoutProvider } from './context/CheckoutContext';
import SuccessPage from './pages/SuccessPage';
import DeliveryPage from './pages/DeliveryPage';
import PrivacyPolicyPage from './pages/PrivacyPolicyPage';
import TermsConditionsPage from './pages/TermsConditionsPage';
import ContactPage from './pages/ContactPage';
import ImprintPage from './pages/ImprintPage';
import './i18n/i18n';
import TopHeader from './components/header/TopHeader';
import Navbar from './components/header/Navbar';
import { AdminProvider } from './context/AdminContext';
import AdminLoginPage from './pages/AdminLoginPage';
import CookieConsentFooter from './components/common/CookieConsentFooter';
import { useState } from 'react';
import GDPRPopup from './components/common/GDPRPopup';
import { ConsentProvider } from './context/consent/ConsentProvider';
import ScrollToTopButton from './components/common/ScrollToTopButton';

function App() {

  const [showSettings, setShowSettings] = useState(false);

  const handleOpenSettings = () => {
    setShowSettings(true);
    console.log('Cookie settings popup should open');
  };

  return (
    <ConsentProvider>
      <Router>
        <ScrollToTop />
        <CheckoutProvider >
          <CartProvider>
            <AdminProvider>
              <TopHeader />
              <Navbar />
              <div className="page-container">
                <Routes>
                  <Route path="/" element={<HomePage />} />
                  <Route path="/shop" element={<ShopPage />} />
                  <Route path="/product/:slug" element={<ProductPage />} />
                  <Route path="/blog" element={<BlogPage />} />
                  <Route path="/blog/:slug" element={<BlogPostPage />} />
                  <Route path="/checkout" element={<BillingPage />} />
                  <Route path="/cart" element={<CartPage />} />
                  <Route path="/payment" element={<PaymentPage />} />
                  <Route path="/success" element={<SuccessPage />} />
                  <Route path="/delivery" element={<DeliveryPage />} />
                  <Route path="/privacy-policy" element={<PrivacyPolicyPage />} />
                  <Route path="/terms-and-conditions" element={<TermsConditionsPage />} />
                  <Route path="/contact" element={<ContactPage />} />
                  <Route path="/imprint" element={<ImprintPage />} />
                  <Route path="/admin" element={<AdminLoginPage />} />
                </Routes>
              </div>
              <Footer />
              <CookieConsentFooter onOpenSettings={handleOpenSettings} />
              {showSettings && <GDPRPopup onClose={() => setShowSettings(false)} />}
              <ScrollToTopButton />
            </AdminProvider>
          </CartProvider>
        </CheckoutProvider>
      </Router>
    </ConsentProvider>
  );
}

export default App;
