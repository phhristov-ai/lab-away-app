import { useLocation } from "react-router-dom";
import { Routes, Route, Navigate } from 'react-router-dom';

import Footer from './components/footer/Footer';
import './App.css';
import TopHeader from './components/header/TopHeader';
import Navbar from './components/header/Navbar';
import AdminLoginPage from './pages/admin/AdminLoginPage';
import CookieConsentFooter from './components/common/consent/CookieConsentFooter';
import GDPRPopup from './components/common/consent/GDPRPopup';
import ScrollToTopButton from './components/common/layout/ScrollToTopButton';
import { useState } from 'react';
import LanguageLayout from './layouts/LanguageLayout';
import LanguagePrefixer from './hooks/utils/LanguagePrefixer';
import { supportedLanguages } from './utils/langMatcher';
import { languageSpecificRoutes } from './utils/languageSpecificRoutes';
import i18n from './i18n/i18n';
import { ConsentState } from './context/consent/types';

function AppContent() {
    const location = useLocation();
    const isProductPage = location.pathname.includes("/product");

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
        <>
            <TopHeader />
            <Navbar />
            <div className={`page-container ${isProductPage ? "full-width" : ""}`}>
                <Routes>
                    <Route path="/" element={<Navigate to={`/${i18n.language || 'en'}`} replace />} />

                    {supportedLanguages.map((lang) => (
                        <Route key={lang} path={`/${lang}`} element={<LanguageLayout lang={lang} />}>
                            {languageSpecificRoutes}
                        </Route>
                    ))}

                    <Route path="/admin" element={<AdminLoginPage />} />

                    {/* Ignore PDFs completely — let the browser fetch them */}
                    <Route path="/en/information/*" element={null} />
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
        </>
    );
}

export default AppContent;