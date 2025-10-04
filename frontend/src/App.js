import { BrowserRouter } from 'react-router-dom'
import AppRouter from './routers/AppRouter'
import Layout from './components/common/Layout/Layout';

function App() {
  return (
    <BrowserRouter>
      
      <AppRouter />
    </BrowserRouter>
  );
}

export default App;
