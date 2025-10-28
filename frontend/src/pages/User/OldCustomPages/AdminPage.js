import { useState } from 'react';
import {
  Container,
  Row,
  Col,
  Card,
  Nav,
  Badge,
  Button
} from 'react-bootstrap';
import ApplicationsPanel from './ApplicationsPanel';
import AssetsPanel from './AssetsPanel';
import UsersPanel from './UsersPanel';
import { useParams } from 'react-router-dom';

function AdminPage() {
  const { tab } = useParams(); // 假设路由是 /adminpage/:tab
  
  // 使用路由参数代替 useState
  const [activeTab, setActiveTab] = tab || 'applications';
  // モックデータ
  const applicationStats = {
    pending: 12,
    approved: 45,
    rejected: 8,
    total: 65
  };

  const userStats = {
    total: 234,
    active: 189,
    inactive: 45,
    admin: 5
  };

  const renderContent = () => {
    switch (activeTab) {
      case 'applications':
        return <ApplicationsPanel stats={applicationStats} />;
      case 'assets':
        return <AssetsPanel />;
      case 'users':
        return <UsersPanel stats={userStats} />;
      default:
        return null;
    }
  };

  return (
    <Container fluid className="p-4">
      {/* 页面标题 */}
      <div className="mb-4">
        <h1>システム管理</h1>
        <p className="text-muted">システム設定、資産とユーザー情報を管理</p>
      </div>

      <Row>
        {/* 侧边导航 */}
        <Col md={1} className="mb-4">
          <Card className="border-0 shadow-sm">
            <Card.Body className="p-0">
              <Nav variant="pills" className="flex-column">
                <Nav.Item>
                  <Button
                    variant={activeTab === 'applications' ? 'primary' : 'light'}
                    className="w-100 text-start border-0 rounded-0 d-flex align-items-center justify-content-between py-3"
                    onClick={() => setActiveTab('applications')}
                  >
                    <div className="d-flex align-items-center">
                      <span className="me-3">📋</span>
                      <span>申請管理</span>
                    </div>
                    {applicationStats.pending > 0 && (
                      <Badge bg="danger" pill>
                        {applicationStats.pending}
                      </Badge>
                    )}
                  </Button>
                </Nav.Item>
                
                <Nav.Item>
                  <Button
                    variant={activeTab === 'assets' ? 'primary' : 'light'}
                    className="w-100 text-start border-0 rounded-0 d-flex align-items-center py-3"
                    onClick={() => setActiveTab('assets')}
                  >
                    <span className="me-3">💻</span>
                    <span>資産管理</span>
                  </Button>
                </Nav.Item>
                
                <Nav.Item>
                  <Button
                    variant={activeTab === 'users' ? 'primary' : 'light'}
                    className="w-100 text-start border-0 rounded-0 d-flex align-items-center py-3"
                    onClick={() => setActiveTab('users')}
                  >
                    <span className="me-3">👥</span>
                    <span>ユーザー管理</span>
                  </Button>
                </Nav.Item>
              </Nav>
            </Card.Body>
          </Card>
        </Col>

        {/* 主要内容区域 */}
        <Col md={11}>
          <Card className="border-0 shadow-sm">
            <Card.Body className="p-4">
              {renderContent()}
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
}

export default AdminPage;