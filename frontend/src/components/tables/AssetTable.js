import {React, useState } from 'react';
import { 
    Table, 
    Badge, 
    Button,
    Row,
    Col,
    Card,
    Form,
    Modal
 } from 'react-bootstrap';
import { assetStatus } from '@types/common';
import PaginationComponent from '@components/common/PaginationComponent';

const AssetTable = ({ 
    assets, 
    stats,
    onEdit, 
    onDelete, 
    showActions = true 
}) => {
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 10; // 每页显示的数量

    //Modal
    const [showEditModal, setShowEditModal] = useState(false);
    const [editingAsset, setEditingAsset] = useState({
        name: '',
        assetCode: '',
        status: 'available',
        purchaseDate: '',
        description: '',
        imageUrl: ''
    });

    // 计算总页数
    const totalPages = Math.ceil(assets.length / itemsPerPage);
    
    // 获取当前页的数据
    const currentData = assets.slice(
        (currentPage - 1) * itemsPerPage,
        currentPage * itemsPerPage
    );

    // 处理页码变化
    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    // 获取状态对应的徽章颜色
    const getStatusBadgeVariant = (status) => {
        switch (status) {
            case 'available':
                return 'success';
            case 'borrowed':
                return 'warning';
            case 'maintenance':
                return 'danger';
            default:
                return 'secondary';
        }
    };


    // 格式化日期
    const formatDate = (dateString) => {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleDateString('ja-JP');
    };

    // 编辑模态框相关函数
    const handleEdit = (asset) => {
        setEditingAsset({
            name: asset.name || '',
            assetCode: asset.assetCode || '',
            status: asset.status || 'available',
            purchaseDate: asset.purchaseDate ? asset.purchaseDate.split('T')[0] : '',
            description: asset.description || '',
            imageUrl: asset.imageUrl || ''
        });
        setShowEditModal(true);
    };
    
    // 关闭编辑模态框
    const handleCloseEditModal = () => {
        setShowEditModal(false);
        setEditingAsset({
            name: '',
            assetCode: '',
            status: 'available',
            purchaseDate: '',
            description: '',
            imageUrl: ''
        });
    };
    
    // 处理编辑表单输入变化
    const handleEditInputChange = (field, value) => {
        setEditingAsset(prev => ({
            ...prev,
            [field]: value
        }));
    };
    
    // 提交编辑后的资产数据
    const handleSubmitEditAsset = () => {
        // 这里调用编辑资产的 API
        console.log('编辑资产:', editingAsset);
        
        // 关闭模态框
        handleCloseEditModal();
    };
    return (
        <div>
            {/* 搜索和筛选栏 */}
            <Card className="border-0 shadow-sm mb-3">
                <Card.Body>
                    <Row className="g-3 align-items-center">
                        <Col md={9}>
                            <div className="input-group">
                                <span className="input-group-text bg-light border-end-0">
                                    🔍
                                </span>
                                <Form.Control
                                    type="text"
                                    placeholder="資産名、ID、または説明で検索..."
                                    className="border-start-0"
                                />
                            </div>
                        </Col>
                        <Col md={3}>
                            <Form.Select>
                                <option>全ステータス</option>
                                <option>貸出可能</option>
                                <option>貸出中</option>
                                <option>メンテナンス中</option>
                            </Form.Select>
                        </Col>
                    </Row>
                </Card.Body>
            </Card>

            {/* 資産表格 */}
            <div className = "border-0 shadow-sm rounded" 
                style={{ minHeight: '880PX'}}
            >
                <Table responsive hover className="mb-0">
                    <thead className="bg-light">
                        <tr>
                            <th className="ps-4 py-3" style={{ width: '90px' }}></th>
                            <th className="py-3">資産情報</th>
                            <th className="py-3 text-center">ステータス</th>
                            <th className="py-3 text-center">購入日</th>
                            {showActions && (
                                <th className="text-center py-3" style={{ width: '210px' }}>操作</th>
                            )}
                        </tr>
                    </thead>
                    <tbody>
                        {currentData.map((asset, index) => (
                            <tr key={asset.assetCode}>
                                <td className="ps-4 py-3 align-middle">
                                    <div 
                                        className="align-items-center justify-content-center"
                                    >
                                        <img 
                                            className="rounded"
                                            src={asset.imageUrl} 
                                            alt="icon"
                                            style={{
                                            width: '48px',
                                            height: '48px',
                                            objectFit: 'contain'
                                            }}
                                            onError={(e) => {
                                                e.target.src = '/assets/images/error.jpg';
                                                e.target.className = 'w-6 h-6 object-cover rounded';
                                            }}
                                        />
                                    </div>
                                </td>
                                <td className="py-3 align-middle">
                                    <div>
                                        <div className="fw-bold">{asset.name}</div>
                                        <div className="text-muted small">{asset.assetCode}</div>
                                    </div>
                                </td>
                                <td className="py-3 text-center align-middle">
                                    <Badge 
                                        bg={getStatusBadgeVariant(asset.status)} 
                                        className="fw-normal"
                                    >
                                        {assetStatus[asset.status] || asset.status}
                                    </Badge>
                                </td>
                                <td className="py-3 text-center align-middle">
                                    <div className="text-muted small">
                                        {formatDate(asset.purchaseDate)}
                                    </div>
                                </td>
                                {showActions && (
                                    <td className="text-center py-3 align-middle">
                                        <div className="btn-group" role="group">
                                            <Button 
                                                variant="outline-primary" 
                                                size="sm"
                                                className="border-0"
                                                title="編集"
                                                onClick={() =>{
                                                    onEdit && onEdit(asset);
                                                    handleEdit(asset);
                                                }}
                                            >
                                                ✏️
                                            </Button>
                                            <Button 
                                                variant="outline-danger" 
                                                size="sm"
                                                className="border-0"
                                                title="削除"
                                                onClick={() => onDelete && onDelete(asset)}
                                            >
                                                🗑️
                                            </Button>
                                        </div>
                                    </td>
                                )}
                            </tr>
                        ))}
                    </tbody>
                </Table>
            </div>

            {/* ページネーション */}
            <PaginationComponent
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={handlePageChange}
                itemsPerPage={itemsPerPage}
                totalItems={stats.total}
                currentItemsCount={currentData.length}
            />

            {/* 统计概览 */}
            <Row >
                <Col md={3} >
                    <Card className="h-100 border-0 shadow-sm">
                        <Card.Body className="d-flex align-items-center">
                            <div 
                                className="rounded-circle d-flex align-items-center justify-content-center me-3"
                                style={{ width: '50px', height: '50px', background: '#d1edff' }}
                            >
                                📦
                            </div>
                            <div>
                                <div className="h4 mb-0">{stats.total}</div>
                                <div className="text-muted">総資産数</div>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={3} >
                    <Card className="h-100 border-0 shadow-sm">
                        <Card.Body className="d-flex align-items-center">
                            <div 
                                className="rounded-circle d-flex align-items-center justify-content-center me-3"
                                style={{ width: '50px', height: '50px', background: '#d4edda' }}
                            >
                                ✅
                            </div>
                            <div>
                                <div className="h4 mb-0">{stats.available}</div>
                                <div className="text-muted">貸出可能</div>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={3} >
                    <Card className="h-100 border-0 shadow-sm">
                        <Card.Body className="d-flex align-items-center">
                            <div 
                                className="rounded-circle d-flex align-items-center justify-content-center me-3"
                                style={{ width: '50px', height: '50px', background: '#fff3cd' }}
                            >
                                🔄
                            </div>
                            <div>
                                <div className="h4 mb-0">{stats.borrowed}</div>
                                <div className="text-muted">貸出中</div>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={3} >
                    <Card className="h-100 border-0 shadow-sm">
                        <Card.Body className="d-flex align-items-center">
                            <div 
                                className="rounded-circle d-flex align-items-center justify-content-center me-3"
                                style={{ width: '50px', height: '50px', background: '#f8d7da' }}
                            >
                                🔧
                            </div>
                            <div>
                                <div className="h4 mb-0">{stats.maintenance}</div>
                                <div className="text-muted">メンテナンス中</div>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            <Modal 
                show={showEditModal} 
                onHide={handleCloseEditModal}
                size="lg"
                centered
            >
                <Modal.Header closeButton className="border-0">
                    <Modal.Title className="d-flex align-items-center">
                        <span className="me-2">✏️</span>
                        資産編集
                    </Modal.Title>
                </Modal.Header>
                
                <Modal.Body className="p-4">
                    <Form>
                        <Row className="g-3">
                            {/* 資産基本情報 */}
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>
                                        資産名 <span className="text-danger">*</span>
                                    </Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={editingAsset.name}
                                        onChange={(e) => handleEditInputChange('name', e.target.value)}
                                        placeholder="例: MacBook Pro 2023"
                                    />
                                </Form.Group>
                            </Col>
                            
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>
                                        資産コード <span className="text-danger">*</span>
                                    </Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={editingAsset.assetCode}
                                        onChange={(e) => handleEditInputChange('assetCode', e.target.value)}
                                        placeholder="例: AST-001"
                                        disabled // 资产代码通常不可编辑
                                    />
                                </Form.Group>
                            </Col>
                        
                            {/* ステータス */}
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>ステータス</Form.Label>
                                    <Form.Select
                                        value={editingAsset.status}
                                        onChange={(e) => handleEditInputChange('status', e.target.value)}
                                    >
                                        <option value="available">貸出可能</option>
                                        <option value="borrowed">貸出中</option>
                                        <option value="maintenance">メンテナンス中</option>
                                    </Form.Select>
                                </Form.Group>
                            </Col>
                            
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>購入日</Form.Label>
                                    <Form.Control
                                        type="date"
                                        value={editingAsset.purchaseDate}
                                        onChange={(e) => handleEditInputChange('purchaseDate', e.target.value)}
                                    />
                                </Form.Group>
                            </Col>
                            
                            {/* 图片URL */}
                            <Col md={12}>
                                <Form.Group className="mb-3">
                                    <Form.Label>画像URL</Form.Label>
                                    <Form.Control
                                        type="text"
                                        value={editingAsset.imageUrl}
                                        onChange={(e) => handleEditInputChange('imageUrl', e.target.value)}
                                        placeholder="https://example.com/image.jpg"
                                    />
                                </Form.Group>
                            </Col>
                            
                            <Col md={12}>
                                <Form.Group className="mb-3">
                                    <Form.Label>
                                        説明 <span className="text-danger">*</span>
                                    </Form.Label>
                                    <Form.Control
                                        as="textarea"
                                        rows={3}
                                        placeholder="資産の詳細な説明を入力..."
                                        value={editingAsset.description}
                                        onChange={(e) => handleEditInputChange('description', e.target.value)}
                                    />
                                </Form.Group>
                            </Col>    
                        </Row>
                    </Form>
                </Modal.Body>
                
                <Modal.Footer className="border-0">
                    <Button 
                        variant="outline-secondary" 
                        onClick={handleCloseEditModal}
                    >
                        キャンセル
                    </Button>
                    <Button 
                        variant="primary" 
                        onClick={handleSubmitEditAsset}
                        className="d-flex align-items-center"
                    >
                        <span className="me-2">💾</span>
                        変更を保存
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default AssetTable;