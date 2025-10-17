import { BrowserRouter } from 'react-router-dom'
import AppRouter from './routers/AppRouter'
import Layout from './components/common/Layout/Layout';
import 'bootstrap/dist/css/bootstrap.min.css';
function App() {
  return (
    <BrowserRouter>
      
      <AppRouter />
    </BrowserRouter>
  );
}

export default App;
