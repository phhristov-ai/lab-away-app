import React from 'react';

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
            <a href={link.url}>{link.label}</a>
          </li>
        ))}
      </ul>

    </div>
  );
};

export default LinksList;
