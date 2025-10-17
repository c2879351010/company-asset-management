// src/components/Layout/Header.js
import { useNavigate } from 'react-router-dom';
import styles from './Layout.module.css';

const Header = () => {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('user') || '{}');

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
    navigate('/login');
  };

  return (
    <header className={styles["header"]}>
      <div className="header-brand">
        <h1>社内備品管理システム</h1>
      </div>
      <div className={styles["header-user"]}>
        <span>こんにちは、{user.name}さん</span>
        <button onClick={handleLogout} className={styles["logout-btn"]}>
          ログアウト
        </button>
      </div>
    </header>
  );
};

export default Header;