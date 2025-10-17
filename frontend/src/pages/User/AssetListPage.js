// AssetListPage.js
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Form,
  InputGroup,
  Badge,
  Modal,
  Navbar,
  Nav
} from 'react-bootstrap';

const AssetListPage = () => {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');
  const [selectedAsset, setSelectedAsset] = useState(null);
  const [showBorrowModal, setShowBorrowModal] = useState(false);
  const [borrowForm, setBorrowForm] = useState({
    borrowDate: '',
    returnDate: '',
    purpose: '',
    notes: ''
  });
  // 模拟资产数据
  const assets = [
    {
      id: 'AST-001',
      name: 'MacBook Pro 2023',
      category: '笔记本电脑',
      description: '16英寸 M2 Max芯片 32GB内存 1TB存储',
      status: 'available',
      thumbnail: '❤️',
      image: '/images/macbook-pro.jpg'
    },
    {
      id: 'AST-002',
      name: 'ThinkPad X1 Carbon',
      category: '笔记本电脑',
      description: '14英寸 英特尔酷睿i7 16GB内存 512GB SSD',
      status: 'borrowed',
      thumbnail: '💻',
      image: '/images/thinkpad-x1.jpg'
    },
    {
      id: 'AST-003',
      name: 'Dell 显示器 U2720Q',
      category: '显示器',
      description: '27英寸 4K IPS 专业设计显示器',
      status: 'available',
      thumbnail: '🖥️',
      image: '/images/dell-monitor.jpg'
    },
    {
      id: 'AST-004',
      name: '罗技 MX Keys',
      category: '外设',
      description: '无线蓝牙键盘，舒适打字体验',
      status: 'maintenance',
      thumbnail: '⌨️',
      image: '/images/mx-keys.jpg'
    },
    {
      id: 'AST-005',
      name: '苹果 iPad Pro',
      category: '平板电脑',
      description: '12.9英寸 M2芯片 5G版本',
      status: 'available',
      thumbnail: '📱',
      image: '/images/ipad-pro.jpg'
    },
    {
      id: 'AST-006',
      name: '索尼 WH-1000XM4',
      category: '音频设备',
      description: '无线降噪耳机，30小时续航',
      status: 'borrowed',
      thumbnail: '🎧',
      image: '/images/sony-headphones.jpg'
    },
    {
      id: 'AST-007',
      name: '佳能 EOS R5',
      category: '摄影设备',
      description: '全画幅无反相机，8K视频拍摄',
      status: 'available',
      thumbnail: '📷',
      image: '/images/canon-r5.jpg'
    },
    {
      id: 'AST-008',
      name: '戴尔 雷电3扩展坞',
      category: '配件',
      description: '支持多显示器输出的雷电3扩展坞',
      status: 'available',
      thumbnail: '🔌',
      image: '/images/dell-dock.jpg'
    },{
      id: 'AST-001',
      name: 'MacBook Pro 2023',
      category: '笔记本电脑',
      description: '16英寸 M2 Max芯片 32GB内存 1TB存储',
      status: 'available',
      thumbnail: '❤️',
      image: '/images/macbook-pro.jpg'
    },{
      id: 'AST-001',
      name: 'MacBook Pro 2023',
      category: '笔记本电脑',
      description: '16英寸 M2 Max芯片 32GB内存 1TB存储',
      status: 'available',
      thumbnail: '❤️',
      image: '/images/macbook-pro.jpg'
    },{
      id: 'AST-001',
      name: 'MacBook Pro 2023',
      category: '笔记本电脑',
      description: '16英寸 M2 Max芯片 32GB内存 1TB存储',
      status: 'available',
      thumbnail: '❤️',
      image: '/images/macbook-pro.jpg'
    },{
      id: 'AST-001',
      name: 'MacBook Pro 2023',
      category: '笔记本电脑',
      description: '16英寸 M2 Max芯片 32GB内存 1TB存储',
      status: 'available',
      thumbnail: '❤️',
      image: '/images/macbook-pro.jpg'
    }
  ];

  // 分类选项
  const categories = [
    { value: 'all', label: '全部分类' },
    { value: '笔记本电脑', label: '笔记本电脑' },
    { value: '显示器', label: '显示器' },
    { value: '平板电脑', label: '平板电脑' },
    { value: '外设', label: '外设' },
    { value: '音频设备', label: '音频设备' },
    { value: '摄影设备', label: '摄影设备' },
    { value: '配件', label: '配件' }
  ];

  // 过滤资产
  const filteredAssets = assets.filter(asset => {
    const matchesSearch = asset.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         asset.id.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         asset.description.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = selectedCategory === 'all' || asset.category === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  // 获取状态标签
  const getStatusBadge = (status) => {
    const variants = {
      available: 'success',
      borrowed: 'secondary', 
      maintenance: 'warning'
    };
    
    const labels = {
      available: '可借阅',
      borrowed: '已借出',
      maintenance: '维护中'
    };
  
    return (
      <Badge bg={variants[status]} className="text-nowrap">
        {labels[status]}
      </Badge>
    );
  };

  // 处理资产点击
  const handleAssetClick = (asset) => {
    if (asset.status === 'available') {
      setSelectedAsset(asset);
      setShowBorrowModal(true);
      // 设置默认借出日期为明天
      const tomorrow = new Date();
      tomorrow.setDate(tomorrow.getDate() + 1);
      setBorrowForm({
        borrowDate: new Date().toISOString().split('T')[0],
        returnDate: tomorrow.toISOString().split('T')[0],
        purpose: '',
        notes: ''
      });
    }
  };
  // 处理表单输入变化
  const handleInputChange = (field, value) => {
    setBorrowForm(prev => ({
      ...prev,
      [field]: value
    }));
  };
  const handleCloseModal = () => {
    setShowBorrowModal(false);
    setSelectedAsset(null);
    setBorrowForm({
      borrowDate: '',
      returnDate: '',
      purpose: '',
      notes: ''
    });
  };
  const handleSubmitBorrow = () => {
    // 验证表单
    if (!borrowForm.borrowDate || !borrowForm.returnDate || !borrowForm.purpose) {
      alert('请填写完整的借阅信息');
      return;
    }

    // 这里可以添加API调用
    console.log('提交借阅申请:', {
      asset: selectedAsset,
      form: borrowForm
    });

    // 模拟提交成功
    alert('借阅申请已提交，等待审核');
    setShowBorrowModal(false);
    setSelectedAsset(null);
    setBorrowForm({
      borrowDate: '',
      returnDate: '',
      purpose: '',
      notes: ''
    });
  };
  return (
    <Container fluid className="vh-100 d-flex flex-column p-4">
    {/* 页面标题和搜索区域 */}
    <Row className="mb-4">
      <Col>
        <Card className="border-0 shadow-sm">
          <Card.Body className="p-4">
            <Row className="align-items-center">
              <Col md={6}>
                <div className="d-flex flex-column">
                  <h2 className="mb-2 fw-bold">備品一覧</h2>
                  <p className="text-muted mb-0">系统中共有 {assets.length} 个资产设备</p>
                </div>
              </Col>
              <Col md={6}>
                <Row className="g-3">
                  <Col md={8}>
                    <InputGroup>
                      <Form.Control
                        type="text"
                        placeholder="搜索物品名称、ID或描述..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                      />
                      <InputGroup.Text>
                        <i className="bi bi-search"></i>
                      </InputGroup.Text>
                    </InputGroup>
                  </Col>
                  <Col md={4}>
                    <Form.Select
                      value={selectedCategory}
                      onChange={(e) => setSelectedCategory(e.target.value)}
                    >
                      {categories.map(category => (
                        <option key={category.value} value={category.value}>
                          {category.label}
                        </option>
                      ))}
                    </Form.Select>
                  </Col>
                </Row>
              </Col>
            </Row>
          </Card.Body>
        </Card>
      </Col>
    </Row>

    {/* 资产列表 */}
    <Row className='flex-grow-1'>
      <Col className='bg-light'>
        {filteredAssets.length === 0 ? (
          <Card className="text-center border-0 shadow-sm">
            <Card.Body className="p-5">
              <div className="mb-3" style={{ fontSize: '3rem' }}>📦</div>
              <h4 className="mb-2">未找到匹配的资产</h4>
              <p className="text-muted">请尝试调整搜索条件或分类筛选</p>
            </Card.Body>
          </Card>
        ) : (
          <Row className="g-3">
            {filteredAssets.map(asset => (
              <Col key={asset.id} xs={12} md={6} lg={4} xl={3}>
                <Card 
                  className={`h-100 shadow-sm ${asset.status === 'available' ? 'clickable' : ''}`}
                  style={{ 
                    cursor: asset.status === 'available' ? 'pointer' : 'default',
                    transition: 'all 0.3s ease'
                  }}
                  onMouseEnter={(e) => {
                    if (asset.status === 'available') {
                      e.currentTarget.style.transform = 'translateY(-5px)';
                      e.currentTarget.style.boxShadow = '0 4px 15px rgba(0,0,0,0.1)';
                    }
                  }}
                  onMouseLeave={(e) => {
                    if (asset.status === 'available') {
                      e.currentTarget.style.transform = 'translateY(0)';
                      e.currentTarget.style.boxShadow = '0 1px 3px rgba(0,0,0,0.1)';
                    }
                  }}
                  onClick={() => handleAssetClick(asset)}
                >
                  {/* 缩略图区域 */}
                  <div 
                    className="position-relative" 
                    style={{ 
                      height: '120px', 
                      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center'
                    }}
                  >
                    <div style={{ fontSize: '2.5rem' }}>
                      {asset.thumbnail}
                    </div>
                    {asset.status === 'available' && (
                      <div 
                        className="position-absolute top-0 start-0 end-0 bottom-0 d-flex align-items-center justify-content-center"
                        style={{ 
                          background: 'rgba(0,0,0,0.7)', 
                          opacity: 0,
                          transition: 'opacity 0.3s ease',
                          color: 'white',
                          fontSize: '0.9rem',
                          fontWeight: 'bold'
                        }}
                        onMouseEnter={(e) => {
                          e.currentTarget.style.opacity = '1';
                        }}
                        onMouseLeave={(e) => {
                          e.currentTarget.style.opacity = '0';
                        }}
                      >
                        点击借阅
                      </div>
                    )}
                  </div>

                  {/* 资产信息 */}
                  <Card.Body className="d-flex flex-column">
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <Card.Title className="h6 mb-0 flex-grow-1 me-2">
                        {asset.name}
                      </Card.Title>
                      {getStatusBadge(asset.status)}
                    </div>
                    
                    <small className="text-muted mb-2">ID: {asset.id}</small>
                    
                    <div className="d-flex align-items-center mb-2">
                      <small className="text-muted me-1">分类:</small>
                      <small className="fw-medium">{asset.category}</small>
                    </div>
                    
                    <Card.Text className="text-muted small flex-grow-1">
                      {asset.description}
                    </Card.Text>

                    {/* 操作按钮 */}
                    <div className="mt-auto">
                      {asset.status === 'available' ? (
                        <Button variant="primary" size="sm" className="w-100">
                          立即借阅
                        </Button>
                      ) : asset.status === 'borrowed' ? (
                        <Button variant="secondary" size="sm" className="w-100" disabled>
                          已借出
                        </Button>
                      ) : (
                        <Button variant="warning" size="sm" className="w-100" disabled>
                          维护中
                        </Button>
                      )}
                    </div>
                  </Card.Body>
                </Card>
              </Col>
            ))}
          </Row>
        )}
      </Col>
    </Row>

    {/* 统计信息 */}
    <Row className="mt-auto bg-light p-3 border-top">
      <Col>
        <Card className="border-0 bg-light">
          <Card.Body className="py-3">
            <Row className="text-center">
              <Col>
                <div className="d-flex flex-column align-items-center">
                  <span className="h4 mb-0 text-primary fw-bold">
                    {assets.filter(a => a.status === 'available').length}
                  </span>
                  <small className="text-muted">可借阅</small>
                </div>
              </Col>
              <Col>
                <div className="d-flex flex-column align-items-center">
                  <span className="h4 mb-0 text-secondary fw-bold">
                    {assets.filter(a => a.status === 'borrowed').length}
                  </span>
                  <small className="text-muted">已借出</small>
                </div>
              </Col>
              <Col>
                <div className="d-flex flex-column align-items-center">
                  <span className="h4 mb-0 text-warning fw-bold">
                    {assets.filter(a => a.status === 'maintenance').length}
                  </span>
                  <small className="text-muted">维护中</small>
                </div>
              </Col>
            </Row>
          </Card.Body>
        </Card>
      </Col>
    </Row>

    {/* 借阅申请弹框 */}
    <Modal 
      show={showBorrowModal} 
      onHide={handleCloseModal}
      size="lg"
      centered
    >
      <Modal.Header closeButton>
        <Modal.Title>借阅申请 - {selectedAsset?.name}</Modal.Title>
      </Modal.Header>

      <Modal.Body>
        {/* 资产信息概览 */}
        <Row className="mb-4">
          <Col xs={3}>
            <div 
              className="d-flex align-items-center justify-content-center rounded"
              style={{ 
                height: '80px', 
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                color: 'white',
                fontSize: '2rem'
              }}
            >
              {selectedAsset?.thumbnail}
            </div>
          </Col>
          <Col xs={9}>
            <h5>{selectedAsset?.name}</h5>
            <div className="text-muted small mb-1">ID: {selectedAsset?.id}</div>
            <div className="text-muted small mb-1">{selectedAsset?.category}</div>
            <div className="text-muted small">{selectedAsset?.description}</div>
          </Col>
        </Row>

        {/* 借阅表单 */}
        <Form>
          <Row className="mb-3">
            <Col md={6}>
              <Form.Group>
                <Form.Label>借出日期 *</Form.Label>
                <Form.Control
                  type="date"
                  value={borrowForm.borrowDate}
                  onChange={(e) => handleInputChange('borrowDate', e.target.value)}
                  min={new Date().toISOString().split('T')[0]}
                />
              </Form.Group>
            </Col>
            <Col md={6}>
              <Form.Group>
                <Form.Label>预计归还日期 *</Form.Label>
                <Form.Control
                  type="date"
                  value={borrowForm.returnDate}
                  onChange={(e) => handleInputChange('returnDate', e.target.value)}
                  min={borrowForm.borrowDate || new Date().toISOString().split('T')[0]}
                />
              </Form.Group>
            </Col>
          </Row>

          <Form.Group className="mb-3">
            <Form.Label>借阅用途 *</Form.Label>
            <Form.Select
              value={borrowForm.purpose}
              onChange={(e) => handleInputChange('purpose', e.target.value)}
            >
              <option value="">请选择借阅用途</option>
              <option value="project">项目开发</option>
              <option value="meeting">会议使用</option>
              <option value="presentation">演示展示</option>
              <option value="training">培训教学</option>
              <option value="other">其他</option>
            </Form.Select>
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label>详细说明</Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              placeholder="请详细描述您的使用需求..."
              value={borrowForm.notes}
              onChange={(e) => handleInputChange('notes', e.target.value)}
            />
          </Form.Group>
        </Form>
      </Modal.Body>

      <Modal.Footer>
        <Button variant="outline-secondary" onClick={handleCloseModal}>
          取消
        </Button>
        <Button variant="primary" onClick={handleSubmitBorrow}>
          提交申请
        </Button>
      </Modal.Footer>
    </Modal>
  </Container>
    
  );
};

export default AssetListPage;