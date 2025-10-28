import { useState } from 'react';
import {
    Row,
    Col,
    Card,
    Badge,
    Button,
    Form,
    Modal
} from 'react-bootstrap';
import { assetRole } from '@types/common';
import { mockAssets } from '@mocks/Asset';
import AssetTable from '@components/tables/AssetTable';

function AssetsPanel({}) {
    const [showAssetModal, setShowAssetModal] = useState(false);
    const [assets, setAssets] = useState(mockAssets);
    const [newAsset, setNewAsset] = useState({
        ...assetRole
    });

    const stats = {
        total: assets.length,
        available: 1,
        borrowed: 42,
        maintenance: 25
    };
    
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
            ...newAsset
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
    };

    // 删除资产
    const handleDeleteAsset = (asset) => {
        if (window.confirm(`資産 "${asset.name}" を削除してもよろしいですか？`)) {
            setAssets(prev => prev.filter(a => a.assetCode !== asset.assetCode));
            alert('資産が削除されました');
        }
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
                        <h2 className="mb-2 fw-bold">資産管理</h2>
                        <p className="text-muted mb-0">システム内の全ての資産情報を管理</p>                            </div>
                    </Col>
                    <Col md={2}>
                        <div className="d-flex justify-content-end">
                            <Button 
                                variant="outline-primary"
                                className="d-flex align-items-center"
                                onClick={handleAddAssetClick}
                            >
                                <span className="me-2">➕</span>
                                新規資産追加
                            </Button>        
                        </div>
                    </Col>
                    </Row>
                </Card.Body>
                </Card>
            </Col>

            {/* 資産テーブル - 使用通用组件 */}
            <Card fluid className="border-0 shadow-sm">
                <Card.Body className="p-0">
                    <AssetTable 
                        assets={assets}
                        stats = {stats}
                        onEdit={handleEditAsset}
                        onDelete={handleDeleteAsset}
                        showActions={true}
                    />
                </Card.Body>
            </Card>


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
                        
                            {/* ステータス */}
                            <Col md={6}>
                                <Form.Group className="mb-3">
                                    <Form.Label>初期ステータス</Form.Label>
                                    <Form.Select
                                        value={newAsset.status}
                                        onChange={(e) => handleAssetInputChange('status', e.target.value)}
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
                                        value={newAsset.purchaseDate}
                                        onChange={(e) => handleAssetInputChange('purchaseDate', e.target.value)}
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