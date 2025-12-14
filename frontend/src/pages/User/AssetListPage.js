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

import { assetRole } from '@types/common';
import { mockAssets } from '@mocks/Asset';

const AssetListPage = () => {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedAsset, setSelectedAsset] = useState(assetRole);
  const [showBorrowModal, setShowBorrowModal] = useState(false);
  const [borrowForm, setBorrowForm] = useState({
    borrowDate: '',
    returnDate: '',
    purpose: '',
    notes: ''
  });
  // モック資産データ
  const assets = mockAssets;
  const assetsStats = {
    total: assets.length,
    available: assets.filter(asset => asset.status === 'available').length,
    maintenance: assets.filter(asset => asset.status === 'maintenance' ).length,
    borrowed: assets.filter(asset => asset.status === 'borrowed').length
};
  // 資産をフィルタリング
  const filteredAssets = assets.filter(asset => {
    const matchesSearch = asset.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         asset.assetCode.toLowerCase().includes(searchTerm.toLowerCase())
       return matchesSearch;
  });

  // ステータスバッジを取得
  const getStatusBadge = (status) => {
    const variants = {
      available: 'success',
      borrowed: 'secondary', 
      maintenance: 'warning'
    };
    
    const labels = {
      available: '貸出可能',
      borrowed: '貸出中',
      maintenance: 'メンテナンス中'
    };
  
    return (
      <Badge bg={variants[status]} className="text-nowrap">
        {labels[status]}
      </Badge>
    );
  };

  // 資産クリックを処理
  const handleAssetClick = (asset) => {
    if (asset.status === 'available') {
      setSelectedAsset(asset);
      setShowBorrowModal(true);
      // デフォルトの貸出日を明日に設定
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
  // フォーム入力変更を処理
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
    // フォームを検証
    if (!borrowForm.borrowDate || !borrowForm.returnDate || !borrowForm.purpose) {
      alert('貸出情報をすべて入力してください');
      return;
    }

    // API
    console.log('貸出申請を送信:', {
      asset: selectedAsset,
      form: borrowForm
    });

    // 送信成功をシミュレート
    alert('貸出申請を送信しました。承認待ちです');
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
    <Container fluid className="d-flex flex-column pt-4 px-4" style={{ minHeight: '500px' }}>
      {/* ページタイトルと検索エリア */}
      <Row className="mb-4">
        <Col>
          <Card className="border-0 shadow-sm">
            <Card.Body className="p-4">
              <Row className="align-items-center">
                <Col md={6}>
                  <div className="d-flex flex-column">
                    <h2 className="mb-2 fw-bold">備品一覧</h2>
                    <p className="text-muted mb-0">設備貸出申請</p>
                  </div>
                </Col>
                <Col md={6}>
                  <InputGroup>
                    <Form.Control
                      type="text"
                      placeholder="物品名、IDまたは説明を検索..."
                      value={searchTerm}
                      onChange={(e) => setSearchTerm(e.target.value)}
                    />
                  </InputGroup>
                </Col>
              </Row>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* 資産リスト - 内部スクロールレイアウト */}
      <Row className="flex-grow-1" style={{ minHeight: '400px' }}>
        <Col className="d-flex flex-column p-0">
          <Card className="border-0 flex-grow-1">
            <Card.Body className="p-0 d-flex flex-column" style={{ height: '100%' }}>
              {filteredAssets.length === 0 ? (
                <div className="d-flex align-items-center justify-content-center flex-grow-1">
                  <div className="text-center">
                    <div className="mb-3" style={{ fontSize: '3rem' }}>📦</div>
                    <h4 className="mb-2">該当する資産が見つかりません</h4>
                    <p className="text-muted">検索条件や分類フィルターを調整してください</p>
                  </div>
                </div>
              ) : (
                <div 
                  className="asset-grid-container flex-grow-1"
                  style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
                    gap: '1rem',
                    padding: '1rem',
                    overflow: 'auto',
                    maxHeight: 'calc(100vh - 340px)', // 動的に最大高さを計算
                    minHeight: '400px'
                  }}
                >
                  {filteredAssets.map(asset => (
                    <Card 
                      key={asset.assetCode}
                      className={`shadow-sm ${asset.status === 'available' ? 'clickable' : ''}`}
                      style={{ 
                        width: '100%',
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
                      {/* サムネイルエリア */}
                      <div 
                        className="position-relative rounded" 
                        style={{ 
                          height: '120px', 
                          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          overflow: 'hidden'
                        }}
                      >
                        <div style={{ 
                          width: '100%', 
                          height: '100%',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center'
                        }}>
                          <img 
                            className="rounded"
                            src={asset.imageUrl} 
                            alt="icon"
                            style={{
                              width: '100%',
                              height: '100%',
                              objectFit: 'cover',
                              objectPosition: 'center'
                            }}
                            onError={(e) => {
                              e.target.src = '/assets/images/error.jpg';
                              e.target.style.objectFit = 'cover';
                            }}
                          />
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
                              fontWeight: 'bold',
                              cursor: 'pointer'
                            }}
                            onMouseEnter={(e) => {
                              e.currentTarget.style.opacity = '1';
                            }}
                            onMouseLeave={(e) => {
                              e.currentTarget.style.opacity = '0';
                            }}
                          >
                            クリックで貸出
                          </div>
                        )}
                      </div>

                      {/* 資産情報 */}
                      <Card.Body className="d-flex flex-column">
                        <div className="d-flex justify-content-between align-items-start mb-2">
                          <Card.Title className="h6 mb-0 flex-grow-1 me-2">
                            {asset.name}
                          </Card.Title>
                          {getStatusBadge(asset.status)}
                        </div>
                        
                        <small className="text-muted mb-2">資産コード: {asset.assetCode}</small>

                        <Card.Text className="text-muted small flex-grow-1">
                          {asset.description}
                        </Card.Text>

                        {/* 操作ボタン */}
                        <div className="mt-auto">
                          {asset.status === 'available' ? (
                            <Button variant="primary" size="sm" className="w-100">
                              貸出申請
                            </Button>
                          ) : asset.status === 'borrowed' ? (
                            <Button variant="secondary" size="sm" className="w-100" disabled>
                              貸出中
                            </Button>
                          ) : (
                            <Button variant="warning" size="sm" className="w-100" disabled>
                              メンテナンス中
                            </Button>
                          )}
                        </div>
                      </Card.Body>
                    </Card>
                  ))}
                </div>
              )}
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* 統計概要 */}
      <Row>
          <Col md={3} >
              <Card className="h-100 border-0 ">
                  <Card.Body className="d-flex align-items-center">
                      <div 
                          className="rounded-circle d-flex align-items-center justify-content-center me-3"
                          style={{ width: '50px', height: '50px', background: '#d1edff' }}
                      >
                          📦
                      </div>
                      <div>
                          <div className="h4 mb-0">{assetsStats.total}</div>
                          <div className="text-muted">総資産数</div>
                      </div>
                  </Card.Body>
              </Card>
          </Col>
          <Col md={3} >
              <Card className="h-100 border-0">
                  <Card.Body className="d-flex align-items-center">
                      <div 
                          className="rounded-circle d-flex align-items-center justify-content-center me-3"
                          style={{ width: '50px', height: '50px', background: '#d4edda' }}
                      >
                          ✅
                      </div>
                      <div>
                          <div className="h4 mb-0">{assetsStats.available}</div>
                          <div className="text-muted">貸出可能</div>
                      </div>
                  </Card.Body>
              </Card>
          </Col>
          <Col md={3} >
              <Card className="h-100 border-0">
                  <Card.Body className="d-flex align-items-center">
                      <div 
                          className="rounded-circle d-flex align-items-center justify-content-center me-3"
                          style={{ width: '50px', height: '50px', background: '#fff3cd' }}
                      >
                          🔄
                      </div>
                      <div>
                          <div className="h4 mb-0">{assetsStats.borrowed}</div>
                          <div className="text-muted">貸出中</div>
                      </div>
                  </Card.Body>
              </Card>
          </Col>
          <Col md={3} >
              <Card className="h-100 border-0">
                  <Card.Body className="d-flex align-items-center">
                      <div 
                          className="rounded-circle d-flex align-items-center justify-content-center me-3"
                          style={{ width: '50px', height: '50px', background: '#f8d7da' }}
                      >
                          🔧
                      </div>
                      <div>
                          <div className="h4 mb-0">{assetsStats.maintenance}</div>
                          <div className="text-muted">メンテナンス中</div>
                      </div>
                  </Card.Body>
              </Card>
          </Col>
      </Row>

      {/* 貸出申請モーダル */}
      <Modal 
        show={showBorrowModal} 
        onHide={handleCloseModal}
        size="lg"
        centered
      >
        <Modal.Header closeButton className="border-0">
          <Modal.Title className="d-flex align-items-center">
            <span className="me-2">📋</span>
            資産貸出申請
          </Modal.Title>
        </Modal.Header>

        <Modal.Body className="p-4">
          {/* 資産情報概要 */}
          <Card className="border-0 bg-light mb-4">
            <Card.Body>
              <Row className="align-items-center">
                <Col xs={3}>
                  <div 
                    className="rounded d-flex align-items-center justify-content-center"
                    style={{ 
                      height: '80px', 
                      overflow: 'hidden'
                    }}
                  >
                    <img 
                      className="rounded"
                      src={selectedAsset?.imageUrl} 
                      alt={selectedAsset?.name}
                      style={{
                        width: '100%',
                        height: '100%',
                        objectFit: 'cover',
                        objectPosition: 'center'
                      }}
                      onError={(e) => {
                        e.target.src = '/assets/images/error.jpg';
                        e.target.style.objectFit = 'cover';
                      }}
                    />
                  </div>
                </Col>
                <Col xs={9}>
                  <div className="d-flex justify-content-between align-items-start mb-2">
                    <h5 className="mb-1">{selectedAsset?.name}</h5>
                    <Badge 
                      bg={selectedAsset?.status === 'available' ? 'success' : 'secondary'}
                      className="fw-normal"
                    >
                      {selectedAsset?.status === 'available' ? '貸出可能' : '貸出中'}
                    </Badge>
                  </div>
                  <div className="text-muted small mb-1">資産コード: {selectedAsset?.assetCode}</div>
                  <div className="text-muted small">{selectedAsset?.description}</div>
                </Col>
              </Row>
            </Card.Body>
          </Card>

          {/* 貸出フォーム */}
          <Form>
            <Row className="g-3">
                           
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>
                    計画貸出日 <span className="text-danger">*</span>
                  </Form.Label>
                  <Form.Control
                    type="date"
                    value={borrowForm.plannedBorrowDate}
                    onChange={(e) => handleInputChange('plannedBorrowDate', e.target.value)}
                    min={new Date().toISOString().split('T')[0]}
                  />
                </Form.Group>
              </Col>

              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>
                    計画返却日 <span className="text-danger">*</span>
                  </Form.Label>
                  <Form.Control
                    type="date"
                    value={borrowForm.plannedReturnDate}
                    onChange={(e) => handleInputChange('plannedReturnDate', e.target.value)}
                    min={borrowForm.plannedBorrowDate || new Date().toISOString().split('T')[0]}
                  />
                </Form.Group>
              </Col>

              <Col md={12}>
                <Form.Group className="mb-3">
                  <Form.Label>詳細説明</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={3}
                    placeholder="利用目的の詳細を入力してください..."
                    value={borrowForm.purpose}
                    onChange={(e) => handleInputChange('purpose', e.target.value)}
                  />
                  <Form.Text className="text-muted">
                    具体的な利用シーンや必要な期間の理由などを記入してください
                  </Form.Text>
                </Form.Group>
              </Col>

            </Row>
          </Form>
        </Modal.Body>

        <Modal.Footer className="border-0">
          <Button 
            variant="outline-secondary" 
            onClick={handleCloseModal}
          >
            キャンセル
          </Button>
          <Button 
            variant="primary" 
            onClick={handleSubmitBorrow}
            className="d-flex align-items-center"
            disabled={!borrowForm.plannedBorrowDate || !borrowForm.plannedReturnDate || !borrowForm.purpose}
          >
            申請を送信
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default AssetListPage;