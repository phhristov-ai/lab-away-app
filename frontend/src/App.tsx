import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

import Footer from './components/footer/Footer';
import './App.css';
import { CartProvider } from './context/CartContext';
import ScrollToTop from './components/common/layout/ScrollToTop';
import { CheckoutProvider } from './context/CheckoutContext';
import TopHeader from './components/header/TopHeader';
import Navbar from './components/header/Navbar';
import { AdminProvider } from './context/AdminContext';
import AdminLoginPage from './pages/admin/AdminLoginPage';
import CookieConsentFooter from './components/common/consent/CookieConsentFooter';
import GDPRPopup from './components/common/consent/GDPRPopup';
import { ConsentProvider } from './context/consent/ConsentProvider';
import ScrollToTopButton from './components/common/layout/ScrollToTopButton';
import { useState } from 'react';
import LanguageLayout from './layouts/LanguageLayout';
import LanguagePrefixer from './hooks/utils/LanguagePrefixer';
import { supportedLanguages } from './utils/langMatcher';
import { languageSpecificRoutes } from './utils/languageSpecificRoutes';
import i18n from './i18n/i18n';
import { ConsentState } from './context/consent/types';
import StaticRedirect from './hooks/utils/StaticRedirect';

function App() {

  const [showPopup, setShowPopup] = useState(false);
  const [hasInteracted, setHasInteracted] = useState(false);

  const handleOpenSettings = () => {
    setShowPopup(true);
  };

  const handleConsentUpdate = (consent: ConsentState) => {
    setHasInteracted(true);
    setShowPopup(false);
  };

  return (
    <ConsentProvider>
      <Router>
        <ScrollToTop />
        <CheckoutProvider>
          <CartProvider>
            <AdminProvider>
              <TopHeader />
              <Navbar />

              <div className="page-container">
                <Routes>
                  <Route path="/" element={<Navigate to={`/${i18n.language || 'en'}`} replace />} />

                  {supportedLanguages.map((lang) => (
                    <Route key={lang} path={`/${lang}`} element={<LanguageLayout lang={lang} />}>
                      {languageSpecificRoutes}
                    </Route>
                  ))}

                  <Route path="/admin" element={<AdminLoginPage />} />

                  {/* Serve PDFs (or other static files) directly from S3 */}
                  <Route path="/en/information/*" element={<StaticRedirect />} />
                  <Route path="/de/information/*" element={<StaticRedirect />} />
                  <Route path="/bg/information/*" element={<StaticRedirect />} />
                  <Route path="*" element={<LanguagePrefixer />} />
                </Routes>
              </div>

              <Footer />
              {!hasInteracted && (
                <CookieConsentFooter onOpenSettings={handleOpenSettings} />
              )}

              {showPopup && (
                <GDPRPopup
                  onClose={() => setShowPopup(false)}
                  onConsentUpdate={handleConsentUpdate}
                />
              )}
              <ScrollToTopButton />
            </AdminProvider>
          </CartProvider>
        </CheckoutProvider>
      </Router>
    </ConsentProvider>
  );
}

export default App;
