import AssetListPage  from '@pages/User/AssetListPage';
import LoginPage from '@pages/Auth/LoginPage'
import Layout from '@components/common/Layout/Layout';
import MyPage from '@pages/User/MyPage';
import AdminPage from '@pages/User/AdminPage';
import { PrivateRoute } from './PrivateRoute'
const routes =[
    {
        path: '/login',
        element:<LoginPage />
    },
    {
        path: '/',
        element: 
        (<PrivateRoute>
            <Layout />
        </PrivateRoute>),
        children:[
            { index:true,element:<AssetListPage />},
            { path: 'mypage', element: <MyPage /> },
            { path: 'adminpage', element: <AdminPage /> },
        ],
    },
];

export default routes;