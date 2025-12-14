import { useState } from 'react';
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Form,
  Badge,
  InputGroup
} from 'react-bootstrap';
import { userRole } from '@types/common';
function MyPage() {
  const [userInfo, setUserInfo] = useState({
    ...userRole,
    name: '山田 太郎',
    email: 'yamada@example.com',
    employeeId: 'aAdD1234-5678',
    role: 'ADMIN' ,
  });

  const [isEditing, setIsEditing] = useState(false);
  
  const [loginInfo, setLoginInfo] = useState({
    currentEmail: 'yamada@example.com',
    newEmail: '',
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });

  const handleSaveAll = () => {
    // 個人情報の保存ロジック
    console.log('個人情報を保存:', userInfo);
    
    // パスワード変更のロジック（パスワードが入力されている場合）
    if (loginInfo.newPassword) {
      console.log('パスワードを変更:', loginInfo);
    }
    
    // 状態をリセット
    setIsEditing(false);
    setLoginInfo(prev => ({
      ...prev,
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }));
    
    alert('変更が保存されました');
  };

  // フォームバリデーション
  const isFormValid = () => {
    // パスワードが入力されている場合は確認
    if (loginInfo.newPassword) {
      return loginInfo.newPassword === loginInfo.confirmPassword && 
            loginInfo.currentPassword.length > 0;
    }
    return true; // パスワード変更なしの場合は常に有効
  };

  // ログイン情報変更ハンドラーの更新
  const handleLoginInfoChange = (field, value) => {
    setLoginInfo(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const [isEditingLogin, setIsEditingLogin] = useState(false);

  // 簡易資産貸出記録
  const borrowRecords = [
    { 
      id: 1, 
      assetName: 'MacBook Pro 2023', 
      assetId: 'AST-001',
      borrowDate: '2024-01-15', 
      returnDate: '2024-01-20',
      status: 'returned',
      notes: 'プロジェクト開発使用'
    },
    { 
      id: 2, 
      assetName: 'ThinkPad X1', 
      assetId: 'AST-002',
      borrowDate: '2024-01-18', 
      returnDate: '2024-01-25',
      status: 'returned',
      notes: '一時代替機器'
    },
    { 
      id: 3, 
      assetName: 'Dell モニター U2720Q', 
      assetId: 'AST-003',
      borrowDate: '2024-01-22', 
      returnDate: null,
      status: 'borrowed',
      notes: '作業画面拡張'
    },
    { 
      id: 4, 
      assetName: 'Logicool MX Keys', 
      assetId: 'AST-004',
      borrowDate: '2024-01-10', 
      returnDate: '2024-01-12',
      status: 'returned',
      notes: '一時使用'
    }
  ];

  const handleSaveLoginInfo = () => {
    // ここにログイン情報保存のロジックを追加
    console.log('ログイン情報を保存:', loginInfo);
    setIsEditingLogin(false);
    // フォームをリセット
    setLoginInfo(prev => ({
      ...prev,
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }));
  };

  const handleCancelEdit = () => {
    setIsEditingLogin(false);
    setLoginInfo(prev => ({
      ...prev,
      newEmail: '',
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }));
  };

  const getStatusBadge = (status) => {
    const statusConfig = {
      borrowed: { label: '貸出中', variant: 'primary' },
      returned: { label: '返却済み', variant: 'success' },
      overdue: { label: '期限超過', variant: 'danger' }
    };
    
    const config = statusConfig[status] || { label: status, variant: 'secondary' };
    return <Badge bg={config.variant}>{config.label}</Badge>;
  };

  const getRoleBadge = (role) => {
    return role === 'ADMIN' 
      ? <Badge bg="warning" className="ms-2">管理者</Badge>
      : <Badge bg="info" className="ms-2">一般ユーザー</Badge>;
  };

  return (
    <Container fluid className="p-4">
      {/* ページタイトル */}
      <Col className='mb-1'>
        <Card className="border-0 shadow-sm">
        <Card.Body className="p-4">
          <Col md={4}>
            <div className="d-flex flex-column">
            <h2 className="mb-2 fw-bold">マイページ</h2>
            <p className="text-muted mb-0">個人情報と設定を管理</p>                            </div>
          </Col>
        </Card.Body>
        </Card>
      </Col>
      <Row className='d-flex '>
        {/* 左カラム - 個人情報と権限 */}
        <Col lg={3} className="d-flex" >
          <Card className="shadow-sm  flex-grow-1 d-flex flex-column">
            <Card.Body className="p-4 d-flex flex-column" style={{ height: '100%' }}>
              {/* ヘッダー部分 */}
              <div className="mb-4">
                  <h5 className="mb-0">個人情報とログイン設定</h5>
              </div>
              <div className='flex-grow-1 d-flex flex-column justify-content-evenly'>
                {/* 個人情報セクション */}
                <div className="d-flex flex-column p-4 shadow-sm">
                  <h6 className="border-bottom pb-2 mb-3">個人情報</h6>
                  <div className="d-flex align-items-start mb-4">
                    <div 
                      className="d-flex align-items-center justify-content-center rounded-circle me-3"
                      style={{
                        width: '60px',
                        height: '60px',
                        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                        color: 'white',
                        fontSize: '1.2rem',
                        fontWeight: 'bold'
                      }}
                    >
                      {userInfo.name.charAt(0)}
                    </div>
                    <div className="flex-grow-1">
                      <div className="d-flex align-items-start mb-1">
                        <h6 className="mb-0 me-2">{userInfo.name}</h6>
                        {getRoleBadge(userInfo.role)}
                      </div>
                      <p className="text-muted mb-1 small">{userInfo.employeeId}</p>
                    </div>
                  </div>

                  {/* 個人情報表示 */}
                  <div className="small">
                    <div className="d-flex justify-content-between align-items-center mb-2">
                      <span className="text-muted">メールアドレス:</span>
                      <span className="fw-medium">{userInfo.email}</span>
                    </div>
                    <div className="d-flex justify-content-between align-items-center">
                      <span className="text-muted">権限レベル:</span>
                      <span className="fw-medium">
                        {userInfo.role === 'ADMIN' ? 'システム管理者' : '一般ユーザー'}
                      </span>
                    </div>
                  </div>
                </div>
                
                {/* 個人情報編集セクション */}
                <div className="d-flex flex-column p-4 shadow-sm mt-4">
                  <div>
                    <h6 className="border-bottom pb-2 mb-3">個人情報編集</h6>
                    
                    <Form.Group className="mb-2">
                      <Form.Label className="small fw-bold">氏名</Form.Label>
                      <Form.Control 
                        size="sm"
                        value={userInfo.name}
                        onChange={(e) => setUserInfo(prev => ({...prev, name: e.target.value}))}
                        placeholder="氏名を入力"
                      />
                    </Form.Group>
                    
                    <Form.Group className="mb-2">
                      <Form.Label className="small fw-bold">メールアドレス</Form.Label>
                      <Form.Control 
                        size="sm"
                        type="email" 
                        value={userInfo.email}
                        onChange={(e) => setUserInfo(prev => ({...prev, email: e.target.value}))}
                        placeholder="メールアドレスを入力"
                      />
                    </Form.Group>
                  </div>
                  {/* パスワード変更セクション */}
                  <div >
                    <h6 className="border-bottom pb-2 mb-3">パスワード変更</h6>
                    
                    <Form.Group className="mb-2">
                      <Form.Label className="small fw-bold">現在のパスワード</Form.Label>
                      <Form.Control 
                        size="sm"
                        type="password" 
                        value={loginInfo.currentPassword}
                        onChange={(e) => handleLoginInfoChange('currentPassword', e.target.value)}
                        placeholder="現在のパスワードを入力"
                      />
                    </Form.Group>
                    
                    <Form.Group className="mb-2">
                      <Form.Label className="small fw-bold">新しいパスワード</Form.Label>
                      <Form.Control 
                        size="sm"
                        type="password" 
                        value={loginInfo.newPassword}
                        onChange={(e) => handleLoginInfoChange('newPassword', e.target.value)}
                        placeholder="新しいパスワードを入力"
                      />
                    </Form.Group>
                    
                    <Form.Group className="mb-3">
                      <Form.Label className="small fw-bold">新しいパスワード（確認）</Form.Label>
                      <Form.Control 
                        size="sm"
                        type="password" 
                        value={loginInfo.confirmPassword}
                        onChange={(e) => handleLoginInfoChange('confirmPassword', e.target.value)}
                        placeholder="新しいパスワードを再入力"
                      />
                    </Form.Group>
                  </div>

                  {/* 保存ボタン */}
                  <div className=" border-top">
                    <div className="d-flex gap-2">
                      <Button 
                        variant="primary" 
                        className="flex-grow-1"
                        onClick={handleSaveAll}
                        disabled={!isFormValid()}
                        size="sm"
                      >
                        💾 保存
                      </Button>
                    </div>
                  </div>

                  {/* セキュリティヒント */}
                  <div className="mt-3">
                    <div className="alert alert-info py-2 mb-0">
                      <small>🔒 定期的なパスワード変更でアカウントを保護</small>
                    </div>
                  </div>
                </div> 
              </div>
            </Card.Body>
          </Card>
        </Col>

        {/* 右カラム - ログイン情報と貸出記録 */}
        <Col lg={9} >

          {/* 資産貸出記録カード */}
          <Card className="shadow-sm">
            <Card.Body className="p-4">
              <div className="d-flex justify-content-between align-items-center mb-3">
                <h5 className="mb-0">資産貸出記録</h5>
                <Badge bg="secondary">全 {borrowRecords.length} 件</Badge>
              </div>

              <div style={{ minHeight: "900px", overflowY: 'auto' }}>
                {borrowRecords.map(record => (
                  <Card 
                    key={record.id} 
                    className="mb-3 border"
                    style={{ cursor: 'pointer' }}
                    onClick={() => console.log('Record clicked:', record.id)}
                  >
                    <Card.Body className="p-3">
                      <div className="d-flex justify-content-between align-items-start mb-2">
                        <div>
                          <div className="fw-bold mb-1">{record.assetName}</div>
                          <small className="text-muted">{record.assetId}</small>
                        </div>
                        {getStatusBadge(record.status)}
                      </div>
                      
                      <div className="small">
                        <div className="d-flex justify-content-between mb-1">
                          <span className="text-muted">貸出日:</span>
                          <span>{record.borrowDate}</span>
                        </div>
                        <div className="d-flex justify-content-between mb-1">
                          <span className="text-muted">返却日:</span>
                          <span>{record.returnDate || '未返却'}</span>
                        </div>
                        {record.notes && (
                          <div className="d-flex justify-content-between">
                            <span className="text-muted">備考:</span>
                            <span className="text-end" style={{ maxWidth: '60%' }}>
                              {record.notes}
                            </span>
                          </div>
                        )}
                      </div>
                    </Card.Body>
                  </Card>
                ))}
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
}

export default MyPage;