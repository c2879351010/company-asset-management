import {useState,useEffect} from 'react';
import {
    Row,
    Col,
    Card,
    Badge,
    Button,
    Form,
    Table,
    Pagination,
    Modal
  } from 'react-bootstrap';
  import { userRole } from '@types/common';
  import { mockUsers } from '@mocks/User';
  import UserTable from '@components/tables/UserTable';
  import AddUserModal from '@components/modals/AddUserModal'; // 导入新的模态框组件
  import axios from 'axios';
  import appConfig from '@config/app.config';
function UsersPanel() {
    const [showUserModal, setShowUserModal] = useState(false);
    const [users, setUsers] = useState(null);
    const [isEditingUser, setIsEditingUser] = useState(false);
    const [editingUser, setEditingUser] = useState(null);
    const [loading, setLoading] = useState(false);
    const [refetchTrigger, setRefetchTrigger] = useState(0);
   

    const handleDeleteUser = async(user) => {
        console.log('删除用户:', user);
        
        if (window.confirm(`ユーザー "${user.userId}" を削除してもよろしいですか？`)) {
          try {
            const token = localStorage.getItem('authToken')
              const response = await axios.get(appConfig.apiBaseUrl + '/api/admin/users',{
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
              setUsers(response.data);
          } catch (err) {
              console.error('获取用户列表失败:', err);
              alert('用户列表获取失败');
              // 可选：回退到mock数据
              // setUsers(mockUsers);
          } finally {
              setLoading(false);
          }
            setUsers(prev => prev.filter(u => u.userId !== user.userId));
        }
    };

    // 获取用户列表的函数
    const fetchUsers = async () => {
      setLoading(true);
      
      try {
        const token = localStorage.getItem('authToken')
          const response = await axios.get(appConfig.apiBaseUrl + '/api/admin/users',{
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
          setUsers(response.data);
      } catch (err) {
          console.error('获取用户列表失败:', err);
          alert('用户列表获取失败');
          // 可选：回退到mock数据
          // setUsers(mockUsers);
      } finally {
          setLoading(false);
      }
  };

    const handleRoleChange = (user) => {
        console.log('権限変更:', user);
        // 实现权限变更逻辑
    };

    // 处理新規ユーザー追加按钮点击
    const handleOpenAddUserModal = () => {
      setIsEditingUser(false);
      setEditingUser(null);
      setShowUserModal(true);
    };

    // 处理添加新用户
    const handleAddUser = async (userData) => {
      setLoading(true);
        try {
            const response = await axios.post(appConfig.apiBaseUrl+'/api/auth/register', userData);

            console.log('Login response:', response);
            // 重定向到主页
            alert('ユーザーが正常に追加されました');
            setRefetchTrigger(prev => prev + 1);
        } catch (err) {
            console.error('Login error:', err);
            alert('ユーザーの追加に失敗しました。もう一度お試しください。');
        } finally {
            setLoading(false);
        }

    };

    // 关闭用户模态框
    const handleCloseUserModal = () => {
      setShowUserModal(false);
      setIsEditingUser(false);
      setEditingUser(null);
    };

    // 打开编辑用户模态框
    const handleOpenEditUserModal = (user) => {
      setIsEditingUser(true);
      setEditingUser(user);
      setShowUserModal(true);
    };

    const editingButtonClicked = (user) => {
      
      handleOpenEditUserModal(user);
    };

     // 处理编辑用户
     const handleEditUser = async(userData) => {
      // 调用API更新用户信息

      
      setLoading(true);
        try {
            const token = localStorage.getItem('authToken')
            console.log('编辑用户数据:', userData);
            const response = await axios.put(appConfig.apiBaseUrl+'/api/admin/users', userData,
            {headers: {
              'Authorization': `Bearer ${token}`,
              'Content-Type': 'application/json'
          }});
            console.log('Login response:', response);
            // 重定向到主页
            alert('ユーザー情報が正常に更新されました');
            setRefetchTrigger(prev => prev + 1);
        } catch (err) {
            console.error('Login error:', err);
            alert('ユーザー情報の更新に失敗しました。もう一度お試しください。');
        } finally {
            setLoading(false);
        }
    };

    // 组件挂载时调用
    useEffect(() => {
        fetchUsers();
    }, [refetchTrigger]);
    return (
      <div className="p-4">
        {/* 内容头部 */}
        <Col className='mb-1'>
            <Card className="border-0 shadow-sm">
            <Card.Body className="p-4">
                <Row className="align-items-center">
                    <Col md={10}>
                        <div className="d-flex flex-column">
                        <h2 className="mb-2 fw-bold">ユーザー管理</h2>
                        <p className="text-muted mb-0">システムユーザーのアカウントと権限を管理</p>                            </div>
                    </Col>
                    <Col md={2} >
                        <div className="d-flex justify-content-end">
                            <Button variant="outline-primary" 
                              className="d-flex align-items-center"
                              onClick={handleOpenAddUserModal}>
                              <span className="me-2">👤</span>
                              新規ユーザー追加
                            </Button>
                        </div>
                    </Col>
                </Row>
            </Card.Body>
            </Card>
        </Col>
  
        {/* 資産テーブル - 使用通用组件 */}
        {users!=null &&<Card className="border-0 shadow-sm">
          <Card.Body className="p-0">
              <UserTable 
                users={users}
                onEdit={editingButtonClicked}
                onDelete={handleDeleteUser}
                onRoleChange={handleRoleChange}
                showActions={true}
              />
          </Card.Body>
        </Card>}

        {/* 添加用户Modal */}
        <AddUserModal
          show={showUserModal}
          onHide={handleCloseUserModal}
          onAddUser={handleAddUser}
          onEditUser={handleEditUser}
          isEditing={isEditingUser}
          editingUser={editingUser}
        />
      </div>
    );
  }
  
  export default UsersPanel;