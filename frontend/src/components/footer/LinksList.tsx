import React from 'react';
import { Link } from 'react-router-dom';

interface Link {
  label: string;
  url: string;
}

interface LinksListProps {
  title: string;
  links: Link[];
}

const LinksList: React.FC<LinksListProps> = ({ title, links }) => {
  return (
    <div>
      <h4>{title}</h4>
      <ul>
        {links.map((link) => (
          <li key={link.url}>
            <Link to={link.url}>{link.label}</Link>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default LinksList;
