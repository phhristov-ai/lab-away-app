import { useEffect, useState } from 'react';

export const useMobileSidebar = (isOpen: boolean) => {
  const [sidebarTop, setSidebarTop] = useState(0);

  useEffect(() => {
    const updateTop = () => {
      const topHeader = document.querySelector('.top-header') as HTMLElement | null;
      const navbar = document.querySelector('.navbar') as HTMLElement | null;

      const topHeaderVisibleHeight = topHeader
        ? Math.max(topHeader.getBoundingClientRect().bottom, 0)
        : 0;
      const navbarHeight = navbar?.offsetHeight || 0;

      setSidebarTop(topHeaderVisibleHeight + navbarHeight);
    };

    if (isOpen) {
      updateTop();
      document.body.style.overflow = 'hidden';
      window.addEventListener('resize', updateTop);
      window.addEventListener('scroll', updateTop);
    } else {
      document.body.style.overflow = '';
    }

    return () => {
      document.body.style.overflow = '';
      window.removeEventListener('resize', updateTop);
      window.removeEventListener('scroll', updateTop);
    };
  }, [isOpen]);

  return sidebarTop;
};
