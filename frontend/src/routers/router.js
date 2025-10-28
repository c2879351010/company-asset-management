import AssetListPage  from '@pages/User/AssetListPage';
import LoginPage from '@pages/Auth/LoginPage'
import Layout from '@components/common/Layout/Layout';
import MyPage from '@pages/User/MyPage';
import ApplicationsPanel from '@pages/User/ApplicationsPanel';
import AssetsPanel from '@pages/User/AssetsPanel';
import UsersPanel from '@pages/User/UsersPanel';

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
            { path: 'adminpage/applications', element: <ApplicationsPanel /> },
            { path: 'adminpage/assets', element: <AssetsPanel /> },
            { path: 'adminpage/users', element: <UsersPanel /> },
        ],
    },
];

export default routes;