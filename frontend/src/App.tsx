import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

import Footer from './components/footer/Footer';
import './App.css';
import { CartProvider } from './context/CartContext';
import ScrollToTop from './components/common/ScrollToTop';
import { CheckoutProvider } from './context/CheckoutContext';
import TopHeader from './components/header/TopHeader';
import Navbar from './components/header/Navbar';
import { AdminProvider } from './context/AdminContext';
import AdminLoginPage from './pages/AdminLoginPage';
import CookieConsentFooter from './components/common/CookieConsentFooter';
import GDPRPopup from './components/common/GDPRPopup';
import { ConsentProvider } from './context/consent/ConsentProvider';
import ScrollToTopButton from './components/common/ScrollToTopButton';
import { useState } from 'react';
import LanguageLayout from './layouts/LanguageLayout';
import LanguagePrefixer from './assets/LanguagePrefixer';
import { supportedLanguages } from './utils/langMatcher';
import { languageSpecificRoutes } from './components/languageSpecificRoutes';
import i18n from './i18n/i18n';
import { ConsentState } from './context/consent/types';
import NotFound from './pages/NotFound';

function App() {

  const [showPopup, setShowPopup] = useState(false);
  const [hasInteracted, setHasInteracted] = useState(false);

  const handleOpenSettings = () => {
    setShowPopup(true);
  };

  const handleConsentUpdate = (consent: ConsentState) => {
    console.log("User interacted:", consent);
    setHasInteracted(true);    // mark consent given
    setShowPopup(false);       // hide popup
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
                  <Route path="*" element={<NotFound />} />
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
