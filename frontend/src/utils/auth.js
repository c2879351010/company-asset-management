export const getCurrentUser = () => {
    try {
      return JSON.parse(localStorage.getItem('USER'));
    } catch {
      return null;
    }
  };
  
  export const hasRole = (role) => {
    const user = getCurrentUser();
    return user && user.roles && user.roles.includes(role);
  };
  
  export const isAdmin = () => {
    return hasRole('ADMIN');
  };
  
  // 获取token用于API调用
  export const getAuthToken = () => {
    return localStorage.getItem('token');
  };