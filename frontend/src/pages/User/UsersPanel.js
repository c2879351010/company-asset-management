import {useState} from 'react';
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

function UsersPanel() {
    const [showUserModal, setShowUserModal] = useState(false);
    const [users, setUsers] = useState(mockUsers);
    const [isEditingUser, setIsEditingUser] = useState(false);
    const [editingUser, setEditingUser] = useState(null);

   

    const handleDeleteUser = (user) => {
        if (window.confirm(`ユーザー "${user.name}" を削除してもよろしいですか？`)) {
            setUsers(prev => prev.filter(u => u.employeeId !== user.employeeId));
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
    const handleAddUser = (userData) => {
      // 这里可以调用API添加用户
      console.log('新規ユーザーを追加:', userData);

      // 模拟添加用户到列表
      const newUser = {
        ...userData,
        id: Date.now(), // 临时ID
        createdAt: new Date().toISOString()
      };
      
      setUsers(prev => [...prev, newUser]);
      
      // 显示成功消息
      alert('ユーザーが正常に追加されました');
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
     const handleEditUser = (userData) => {
      // 调用API更新用户信息
      const updatedUsers = users.map(user => 
        user.employeeId === userData.employeeId 
          ?  userData : user
        );
        setUsers(updatedUsers);
    };
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
        <Card className="border-0 shadow-sm">
          <Card.Body className="p-0">
              <UserTable 
                users={users}
                onEdit={editingButtonClicked}
                onDelete={handleDeleteUser}
                onRoleChange={handleRoleChange}
                showActions={true}
              />
          </Card.Body>
        </Card>

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