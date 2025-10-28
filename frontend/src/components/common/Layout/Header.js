import { useNavigate } from 'react-router-dom';
import styles from './Layout.module.css';

const Header = () => {
  const navigate = useNavigate();
  const email = JSON.parse(localStorage.getItem('email') || '{}');

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('email');
    navigate('/login');
  };
  return (
    <header className={styles["header"]}>
      <div className="header-brand">
        <h1>社内備品管理システム</h1>
      </div>
      <div className={styles["header-user"]}>
        <span>こんにちは、{email.email}さん</span>
        <button onClick={handleLogout} className={styles["logout-btn"]}>
          ログアウト
        </button>
      </div>
    </header>
  );
};

export default Header;