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

function App() {

  const [showSettings, setShowSettings] = useState(false);

  const handleOpenSettings = () => {
    setShowSettings(true);
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
                  <Route path="*" element={<LanguagePrefixer />} />
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
