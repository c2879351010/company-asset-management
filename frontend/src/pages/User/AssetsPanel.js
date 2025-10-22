// AssetsPanel.js
import { useState } from 'react';
import {
    Row,
    Col,
    Card,
    Badge,
    Button,
    Form,
    Pagination,
    Modal
} from 'react-bootstrap';
import { assetRole } from '@types/common';
import { mockAssets } from '@mocks/Asset';
import AssetTable from '@components/tables/AssetTable';

function AssetsPanel({ stats }) {
    const [showAssetModal, setShowAssetModal] = useState(false);
    const [assets, setAssets] = useState(mockAssets);
    const [newAsset, setNewAsset] = useState({
        ...assetRole
    });

    const handleAddAssetClick = () => {
        setNewAsset({
            ...assetRole
        });
        setShowAssetModal(true);
    };

    // 表单输入处理
    const handleAssetInputChange = (field, value) => {
        setNewAsset(prev => ({
            ...prev,
            [field]: value
        }));
    };

    const handleSubmitNewAsset = () => {
        // 验证必填字段
        if (!newAsset.name || !newAsset.assetCode || !newAsset.description) {
            alert('必須項目を入力してください');
            return;
        }

        // 生成新的资产数据
        const newAssetData = {
            ...newAsset,
            category: newAsset.category || 'laptop'
        };

        // 添加到资产列表
        setAssets(prev => [...prev, newAssetData]);

        // 模拟提交成功
        alert('資産が正常に追加されました');
        setShowAssetModal(false);
        
        // 重置表单
        setNewAsset({ ...assetRole });
    };

    // 关闭弹框
    const handleCloseAssetModal = () => {
        setShowAssetModal(false);
        setNewAsset({ ...assetRole });
    };

    // 编辑资产
    const handleEditAsset = (asset) => {
        console.log('編集資産:', asset);
        // 这里可以实现编辑功能
        alert(`資産 ${asset.name} の編集機能を実装してください`);
    };

    // 删除资产
    const handleDeleteAsset = (asset) => {
        if (window.confirm(`資産 "${asset.name}" を削除してもよろしいですか？`)) {
            setAssets(prev => prev.filter(a => a.assetCode !== asset.assetCode));
            alert('資産が削除されました');
        }
    };

    return (
        <div>
            {/* 内容头部 */}
            <div className="mb-4">
                <div className="d-flex justify-content-between align-items-center">
                    <div>
                        <h2>資産管理</h2>
                        <p className="text-muted mb-0">システム内の全ての資産情報を管理</p>
                    </div>
                    <Button 
                        variant="primary"
                        className="d-flex align-items-center"
                        onClick={handleAddAssetClick}
                    >
                        <span className="me-2">➕</span>
                        新規資産追加
                    </Button>
                </div>
            </div>

            {/* 统计概览 */}
            <Row className="mb-4">
                <Col md={3} className="mb-3">
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
                <Col md={3} className="mb-3">
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
                <Col md={3} className="mb-3">
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
                <Col md={3} className="mb-3">
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

            {/* 搜索和筛选栏 */}
            <Card className="border-0 shadow-sm mb-4">
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

            {/* 資産テーブル - 使用通用组件 */}
            <Card className="border-0 shadow-sm">
                <Card.Body className="p-0">
                    <AssetTable 
                        assets={assets}
                        onEdit={handleEditAsset}
                        onDelete={handleDeleteAsset}
                        showActions={true}
                    />
                </Card.Body>
            </Card>

            {/* ページネーション */}
            <div className="d-flex justify-content-between align-items-center mt-4">
                <div className="text-muted small">
                    1-{assets.length} of {stats.total} 資産を表示
                </div>
                <div>
                    <Pagination className="mb-0">
                        <Pagination.Prev disabled>
                            前へ
                        </Pagination.Prev>
                        <Pagination.Item active>{1}</Pagination.Item>
                        <Pagination.Item>{2}</Pagination.Item>
                        <Pagination.Item>{3}</Pagination.Item>
                        <Pagination.Next>
                            次へ
                        </Pagination.Next>
                    </Pagination>
                </div>
            </div>

            {/* 新規資産追加モーダル */}
            <Modal 
                show={showAssetModal} 
                onHide={handleCloseAssetModal}
                size="lg"
                centered
            >
                <Modal.Header closeButton className="border-0">
                    <Modal.Title className="d-flex align-items-center">
                        <span className="me-2">📦</span>
                        新規資産追加
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
                                        value={newAsset.name}
                                        onChange={(e) => handleAssetInputChange('name', e.target.value)}
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
                                        value={newAsset.assetCode}
                                        onChange={(e) => handleAssetInputChange('assetCode', e.target.value)}
                                        placeholder="例: AST-001"
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
                                        value={newAsset.description}
                                        onChange={(e) => handleAssetInputChange('description', e.target.value)}
                                    />
                                </Form.Group>
                            </Col>

                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>カテゴリー</Form.Label>
                                    <Form.Select
                                        value={newAsset.category}
                                        onChange={(e) => handleAssetInputChange('category', e.target.value)}
                                    >
                                        <option value="laptop">ノートパソコン</option>
                                        <option value="monitor">モニター</option>
                                        <option value="peripheral">周辺機器</option>
                                        <option value="tablet">タブレット</option>
                                        <option value="audio">オーディオ機器</option>
                                    </Form.Select>
                                </Form.Group>
                            </Col>

                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>購入日</Form.Label>
                                    <Form.Control
                                        type="date"
                                        value={newAsset.purchaseDate}
                                        onChange={(e) => handleAssetInputChange('purchaseDate', e.target.value)}
                                    />
                                </Form.Group>
                            </Col>
                            
                            {/* ステータス */}
                            <Col md={12}>
                                <Form.Group className="mb-3">
                                    <Form.Label>初期ステータス</Form.Label>
                                    <div>
                                        <Form.Check
                                            inline
                                            type="radio"
                                            name="assetStatus"
                                            label="貸出可能"
                                            value="available"
                                            checked={newAsset.status === 'available'}
                                            onChange={(e) => handleAssetInputChange('status', e.target.value)}
                                        />
                                        <Form.Check
                                            inline
                                            type="radio"
                                            name="assetStatus"
                                            label="貸出中"
                                            value="borrowed"
                                            checked={newAsset.status === 'borrowed'}
                                            onChange={(e) => handleAssetInputChange('status', e.target.value)}
                                        />
                                        <Form.Check
                                            inline
                                            type="radio"
                                            name="assetStatus"
                                            label="メンテナンス中"
                                            value="maintenance"
                                            checked={newAsset.status === 'maintenance'}
                                            onChange={(e) => handleAssetInputChange('status', e.target.value)}
                                        />
                                    </div>
                                </Form.Group>
                            </Col>
                        </Row>
                    </Form>
                </Modal.Body>
                
                <Modal.Footer className="border-0">
                    <Button 
                        variant="outline-secondary" 
                        onClick={handleCloseAssetModal}
                    >
                        キャンセル
                    </Button>
                    <Button 
                        variant="primary" 
                        onClick={handleSubmitNewAsset}
                        className="d-flex align-items-center"
                    >
                        <span className="me-2">✅</span>
                        資産を追加
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
}

export default AssetsPanel;