import { NavLink } from 'react-router-dom';
import styles from './Layout.module.css';

const Sidebar = () => {
  const userRole = JSON.parse(localStorage.getItem('user') || '{}').role;

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
        {userRole === 'admin' && (
          <>
            <li>
              <NavLink 
                to="/adminpage" 
                className={({ isActive }) => isActive ? styles['active'] : ''}
              >
                管理画面
              </NavLink>
            </li>
            <li>
              <NavLink 
                to="/reports" 
                className={({ isActive }) => isActive ? styles['active'] : ''}
              >
                レポート
              </NavLink>
            </li>
          </>
        )}
      </ul>
    </nav>
  );
};

export default Sidebar;