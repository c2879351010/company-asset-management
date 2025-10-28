import React, { useState, useEffect } from 'react';
import { Modal, Button, Form, Row, Col, Card } from 'react-bootstrap';
import { userRole,userType } from '@types/common';
const AddUserModal = ({ 
  show, 
  onHide, 
  onAddUser,
  onEditUser, // 新增：编辑用户的函数
  isEditing = false, // 新增：是否为编辑模式
  editingUser = null, // 新增：正在编辑的用户数据
}) => {

  const [newUser, setNewUser] =  useState({...userRole});
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  useEffect(() => {
    if (isEditing && editingUser) {
      setNewUser({
        name: editingUser.name || '',
        employeeId: editingUser.employeeId || '',
        email: editingUser.email || '',
        role: editingUser.role || 'User',
        status: editingUser.status || 'active'
      });
      // 编辑模式下清空密码字段
      setPassword('');
      setConfirmPassword('');
    } else {
      // 添加模式下重置表单
      setNewUser(userRole);
      setPassword('');
      setConfirmPassword('');
    }
  }, [isEditing, editingUser, userRole]);
  // 处理用户表单输入变化
  const handleUserInputChange = (field, value) => {
    setNewUser(prev => ({
      ...prev,
      [field]: value
    }));
  };

  // 提交用户数据（新增或编辑）
  const handleSubmit = () => {
    // 验证必填字段
    if (!newUser.name || !newUser.email || !newUser.employeeId) {
      alert('必須項目を入力してください');
      return;
    }

    // 邮箱格式验证
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(newUser.email)) {
      alert('有効なメールアドレスを入力してください');
      return;
    }

    // 密码验证（如果设置了密码）
    if (password && password !== confirmPassword) {
      alert('パスワードが一致しません');
      return;
    }

    // 准备提交数据
    const userData = {
      ...newUser,
      ...(password && { password }) // 如果有密码则包含密码
    };

    if (isEditing) {
      // 编辑模式：调用编辑函数
      onEditUser && onEditUser(userData);
    } else {
      // 添加模式：调用添加函数
      onAddUser && onAddUser(userData);
    }

    // 重置表单
    handleClose();
  };

  // 关闭模态框
  const handleClose = () => {
    setNewUser(userRole);
    setPassword('');
    setConfirmPassword('');
    onHide();
  };

  // 根据模式获取模态框标题
  const getModalTitle = () => {
    if (isEditing) {
      return (
        <Modal.Title className="d-flex align-items-center">
          <span className="me-2">✏️</span>
          ユーザー編集
        </Modal.Title>
      );
    }
    return (
      <Modal.Title className="d-flex align-items-center">
        <span className="me-2">👤</span>
        新規ユーザー追加
      </Modal.Title>
    );
  };

  // 根据模式获取提交按钮文本
  const getSubmitButtonText = () => {
    if (isEditing) {
      return (
        <>
          <span className="me-2">💾</span>
          変更を保存
        </>
      );
    }
    return (
      <>
        <span className="me-2">✅</span>
        ユーザーを追加
      </>
    );
  };
  

  return (
    <Modal 
      show={show} 
      onHide={handleClose}
      size="lg"
      centered
    >
      <Modal.Header closeButton className="border-0">
        {getModalTitle()}
      </Modal.Header>
      
      <Modal.Body className="p-4">
        <Form>
          <Row className="g-3">
            {/* ユーザー基本情報 */}
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label>
                  姓名 <span className="text-danger">*</span>
                </Form.Label>
                <Form.Control
                  type="text"
                  value={newUser.name}
                  onChange={(e) => handleUserInputChange('name', e.target.value)}
                  placeholder="例: 山田 太郎"
                />
              </Form.Group>
            </Col>
            
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label>
                  ID <span className="text-danger">*</span>
                </Form.Label>
                <Form.Control
                  type="text"
                  placeholder="例: EMP001"
                  value={newUser.employeeId}
                  onChange={(e) => handleUserInputChange('employeeId', e.target.value)}
                  disabled={isEditing} // 编辑模式下ID不可修改
                />
                {isEditing && (
                  <Form.Text className="text-muted">
                    IDは編集できません
                  </Form.Text>
                )}
              </Form.Group>
            </Col>
            
            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label>
                  メールアドレス <span className="text-danger">*</span>
                </Form.Label>
                <Form.Control
                  type="email"
                  placeholder="例: yamada@company.com"
                  value={newUser.email}
                  onChange={(e) => handleUserInputChange('email', e.target.value)}
                />
              </Form.Group>
            </Col>

            <Col md={6}>
              <Form.Group className="mb-3">
                <Form.Label>権限</Form.Label>
                <Form.Select
                  value={newUser.role}
                  onChange={(e) => handleUserInputChange('role', e.target.value)}
                >
                  <option value="User">一般ユーザー</option>
                  <option value="Admin">管理者</option>
                </Form.Select>
              </Form.Group>
            </Col>

            {/* パスワード設定（オプション） */}
            <Col md={12}>
              <Card className="border-0 bg-light">
                <Card.Body>
                  <h6 className="mb-3">
                    {isEditing ? '🔐 パスワード変更' : '🔐 初期パスワード設定'}
                  </h6>
                  <p className="text-muted small mb-3">
                    {isEditing 
                      ? 'パスワードを変更する場合のみ入力してください。空白の場合は変更されません。'
                      : 'パスワードを設定しない場合、システムが自動生成した初期パスワードがメールで送信されます。'
                    }
                  </p>
                  <Row className="g-3">
                    <Col md={6}>
                      <Form.Group>
                        <Form.Label>
                          {isEditing ? '新しいパスワード' : '初期パスワード'}
                        </Form.Label>
                        <Form.Control
                          type="password"
                          placeholder={isEditing ? "新しいパスワードを入力" : "初期パスワードを入力"}
                          value={password}
                          onChange={(e) => setPassword(e.target.value)}
                        />
                      </Form.Group>
                    </Col>
                    <Col md={6}>
                      <Form.Group>
                        <Form.Label>
                          {isEditing ? 'パスワード確認' : 'パスワード確認'}
                        </Form.Label>
                        <Form.Control
                          type="password"
                          placeholder="パスワードを再入力"
                          value={confirmPassword}
                          onChange={(e) => setConfirmPassword(e.target.value)}
                        />
                      </Form.Group>
                    </Col>
                  </Row>
                </Card.Body>
              </Card>
            </Col>
          </Row>
        </Form>
      </Modal.Body>
      
      <Modal.Footer className="border-0">
        <Button 
          variant="outline-secondary" 
          onClick={handleClose}
        >
          キャンセル
        </Button>
        <Button 
          variant="primary" 
          onClick={handleSubmit}
          className="d-flex align-items-center"
        >
          {getSubmitButtonText()}
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default AddUserModal;