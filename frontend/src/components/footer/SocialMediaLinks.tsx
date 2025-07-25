import facebookIcon from '../../assets/icons/Facebook.svg';
import instagramIcon from '../../assets/icons/Instagram.svg';
import xIcon from '../../assets/icons/X.svg';
import linkedinIcon from '../../assets/icons/Linkedin.svg';

import './SocialMediaLinks.css';


const SocialMediaLinks = () => {
  const socialIcons = [
    { href: 'https://facebook.com', src: facebookIcon, alt: 'Facebook' },
    { href: 'https://instagram.com', src: instagramIcon, alt: 'Instagram' },
    { href: 'https://x.com', src: xIcon, alt: 'X' },
    { href: 'https://linkedin.com', src: linkedinIcon, alt: 'LinkedIn' },
  ];

  return (
    <div className="social-media-icons">
      {socialIcons.map(({ href, src, alt }) => (
        <a key={alt} href={href} className="social-icon" target="_blank" rel="noopener noreferrer">
          <img src={src} alt={alt} className="social-image" />
        </a>
      ))}
    </div>
  );
};

export default SocialMediaLinks;
