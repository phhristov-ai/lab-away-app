import './Hamburger.css';

interface HamburgerProps {
  isOpen: boolean;
  onClick: () => void;
}

const Hamburger: React.FC<HamburgerProps> = ({ isOpen, onClick }) => {
  return (
    <button className={`hamburger ${isOpen ? 'open' : ''}`} onClick={onClick} aria-label="Toggle navigation">
      <div className="bar" />
      <div className="bar" />
      <div className="bar" />
    </button>
  );
};

export default Hamburger;
