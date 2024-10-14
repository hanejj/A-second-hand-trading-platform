import React from 'react';
import { Link } from 'react-router-dom';
import './NavButton.css'; // 스타일 시트를 생성하여 적용할 수 있습니다.

const NavButton = ({ to, children }) => {
  return (
    <Link to={to} className="nav-button">
      {children}
    </Link>
  );
};

export default NavButton;
