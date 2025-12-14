import { NavLink } from 'react-router-dom';
import { useState } from 'react';
import styles from './Layout.module.css';

const Sidebar = () => {
  const userRole = localStorage.getItem('userRole');
  const [adminMenuOpen, setAdminMenuOpen] = useState(false);


  return (
    <nav className={styles["sidebar"]}>
      <ul className={styles["sidebar-menu"]}>
        <li>
          <NavLink 
            to="/" 
            className={({ isActive }) => isActive ? styles['active'] : ''}
          >
            備品一覧
          </NavLink>
        </li>
        <li>
          <NavLink 
            to="/mypage" 
            className={({ isActive }) => isActive ? styles['active'] : ''}
          >
            マイページ
          </NavLink>
        </li>
        
        {/* 管理者のみ表示 */}
        {userRole === 'ADMIN' && (
          <>
            <li className={styles["admin-menu"]}>
              <div 
                className={`${styles["admin-menu-header"]} ${adminMenuOpen ? styles["active"] : ''}`}
                onClick={() => setAdminMenuOpen(!adminMenuOpen)}
              >
                管理画面
                <span className={styles["dropdown-arrow"]}>
                  {adminMenuOpen ? '▲' : '▼'} 
                </span>
              </div>
              {adminMenuOpen && (
                <ul className={styles["admin-submenu"]}>
                  <li>
                    <NavLink 
                      to="/adminpage/applications" 
                      className={({ isActive }) => isActive ? styles['active'] : ''}
                    >
                      申請管理
                    </NavLink>
                  </li>
                  <li>
                    <NavLink 
                      to="/adminpage/assets" 
                      className={({ isActive }) => isActive ? styles['active'] : ''}
                    >
                      資産管理
                    </NavLink>
                  </li>
                  <li>
                    <NavLink 
                      to="/adminpage/users" 
                      className={({ isActive }) => isActive ? styles['active'] : ''}
                    >
                      ユーザー管理
                    </NavLink>
                  </li>
                </ul>
              )}
            </li>
          </>
        )}
      </ul>
    </nav>
  );
};

export default Sidebar;