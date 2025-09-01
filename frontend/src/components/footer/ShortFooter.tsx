import Logo from '../header/Logo';
import FooterAbout from './FooterAbout';
import FooterBottom from './FooterBottom';
import './ShortFooter.css';

const ShortFooter = () => {
  return (
    <footer className="footer footer--short">
      <div className="footer-inner">
        <div className="footer-row footer-row-about-short">
          <div className="footer-about-with-logo">
            <Logo />
            <FooterAbout />
          </div>
        </div>
        <div className="footer-row footer-row-bottom">
          <FooterBottom />
        </div>
      </div>
    </footer>
  );
};

export default ShortFooter;
