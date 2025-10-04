import AssetList from '../pages/User/AssetListPage';
import LoginPage from '../pages/Auth/LoginPage'
import Layout from '../components/common/Layout/Layout';
import { PrivateRoute } from './PrivateRoute'
const routes =[
    {
        path: '/login',
        element: 
        <Layout>
            <LoginPage />
        </Layout>,
    },
    // {
    //     path: '/',
    //     element: 
    //     <PrivateRoute>
    //         <Layout />
    //     </PrivateRoute>,
    //     children:[
    //         { index:true,element:<AssetList/>}
    //     ],
    // },
];

export default routes;