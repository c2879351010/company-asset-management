// src/components/Layout/Layout.js
import { Outlet, useLocation, Navigate } from 'react-router-dom';
import Header from './Header';
import Sidebar from './Sidebar';
import styles from './Layout.module.css';

const Layout = () => {
  const location = useLocation();
  const isAuthenticated = localStorage.getItem('authToken');
  
  // 認証チェック
  if (!isAuthenticated && location.pathname !== '/login') {
    return <Navigate to="/login" replace />;
  }

  // ログイン画面ではLayoutを使用しない
  if (location.pathname === '/login') {
    return <Outlet />;
  }

  return (
    <div className={styles["layout"]}>
      <Header />
      <div className={styles["layout-body"]}>
        <Sidebar />
        <main className={styles["layout-content"]}>
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default Layout;