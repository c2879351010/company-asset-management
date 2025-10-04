import { Outlet } from "react-router-dom";
import Header from './Header'
import './Layout.css';

const Layout = () => {
    return(
        <div className="layout">
            <Header />
            <div className="layout-body">
                <main className="layout-main">
                    <Outlet />
                </main>
            </div>
        </div>
    )
}
export default Layout;