import { BrowserRouter as Router } from 'react-router-dom';
import './App.css';
import { CartProvider } from './context/CartContext';
import ScrollToTop from './components/common/layout/ScrollToTop';
import { CheckoutProvider } from './context/CheckoutContext';
import { AdminProvider } from './context/AdminContext';
import AppContent from './AppContent';

import { ConsentProvider } from './context/consent/ConsentProvider';

function App() {

  return (
    <ConsentProvider>
      <Router>
        <ScrollToTop />
        <CheckoutProvider>
          <CartProvider>
            <AdminProvider>
              <AppContent />
            </AdminProvider>
          </CartProvider>
        </CheckoutProvider>
      </Router>
    </ConsentProvider>
  );
}

export default App;
