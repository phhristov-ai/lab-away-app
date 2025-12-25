import { ReactNode } from 'react'
import { Link } from 'react-router-dom'
// @ts-ignore
import { HashLink } from 'react-router-hash-link';

interface NavItemProps {
  to?: string
  label?: string
  icon?: ReactNode
  onClick?: () => void
  isHashLink?: boolean
  scroll?: (el: HTMLElement | null) => void
}

export const NavItem = ({
  to,
  label,
  icon,
  onClick,
  isHashLink,
  scroll,
}: NavItemProps) => {
  const content = (
    <>
      {icon}
      {label && <span>{label}</span>}
    </>
  )

  let Component: any = 'button'
  const props: any = {
    className: 'nav-link-block',
    onClick,
  }

  if (to) {
    Component = isHashLink ? HashLink : Link
    props.to = to
    props.smooth = isHashLink
    props.scroll = isHashLink ? scroll : undefined
  } else {
    props.type = 'button'
    props['aria-label'] = label || 'Action'
  }

  return (
    <li>
      <Component {...props}>
        {content}
      </Component>
    </li>
  )
}
