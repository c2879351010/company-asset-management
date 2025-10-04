import { useNavigate } from "react-router-dom";

const Header = () =>{
    const navigate = useNavigate();
    const HandleLogout = () =>{
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
        navigate('/login');
    };
    return(
        <header className="header">
            <div className="header-brand">
                <h1>Test</h1>
            </div>
            <div className="header-user">
                <span>Yes</span>
                <button onClick = {HandleLogout} className="logout-btn"> 
                 logout
                </button>
            </div>

        </header>
    )
};

export default Header;
