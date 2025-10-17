import React,{use, useState,useEffect} from "react";
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import styles from './LoginPage.module.css'
const LoginPage  = () => {
    const [email,setEmail] = useState('');
    const [password,setPassword] = useState('');

    const isAuthenticated = localStorage.getItem('authToken')
    const navigate = useNavigate();
    useEffect(()=>{
        if(isAuthenticated){
            navigate('/', { replace: true });
        }
    },[isAuthenticated,navigate])
    const handleLogin = async (e) => {
        e.preventDefault();
        localStorage.setItem('authToken', '1123123123');
        localStorage.setItem('user', JSON.stringify({ role: 'admin' }))
        // try{
        //     const response = await axios.post('/api/login',{ email,password});
        //     console.log(response.data);
        // }catch(error){
        //     console.error(error);
        // }
    };

    return(
        <div className={styles["login-body"]}>
            <div className={styles['login-container']}>
                <div className={styles['login-header']}>
                    <h2>login</h2>
                </div>
                <form onSubmit={handleLogin} className={styles['login-form']} id="loginForm">
                    <div className={styles['form-group']}>
                        <label>Email:</label>
                        <input
                            type="email"
                            value={email}
                            className = {styles['form-control']}
                            onChange={(e)=>setEmail(e.target.value)}/>
                
                    </div>
                    <div className={styles['form-group']}>
                        <label form="password">Password:</label>
                        <input type="password" 
                            id="password" 
                            className={styles['form-control']}
                            value={password}
                            onChange={(e)=>setPassword(e.target.value)}/>
                    </div>
                    <button className={styles['login-button']} type="submit">Login</button>

                </form>
            </div>
        </div>
    );
};

export default LoginPage;