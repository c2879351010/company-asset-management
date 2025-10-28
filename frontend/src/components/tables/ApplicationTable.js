import React, { useState } from 'react';
import { 
    Table, 
    Badge, 
    Button,
    Row,
    Col,
    Card,
    Form,
    Modal,
    Alert
 } from 'react-bootstrap';
import { applicationStatus } from '@types/common';
import { approveApplicationAPI } from '@services/applicationService';
import PaginationComponent from '@components/common/PaginationComponent';

const ApplicationTable = ({ 
    applications, 
    stats,
    onApprove, 
    onReject, 
    onEdit,
    showActions = true 
}) => {
    const [currentPage, setCurrentPage] = useState(1);
    const [showApproveModal, setShowApproveModal] = useState(false);
    const [showRejectModal, setShowRejectModal] = useState(false);
    const [selectedApplication, setSelectedApplication] = useState(null);
    const [approvalData, setApprovalData] = useState({
        actualBorrowDate: '',
        adminNote: ''
    });
    const [rejectData, setRejectData] = useState({
        adminNote: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const itemsPerPage = 10; // 每页显示的数量
    
    // 计算总页数
    const totalPages = Math.ceil(applications.length / itemsPerPage);
    
    // 获取当前页的数据
    const currentData = applications.slice(
        (currentPage - 1) * itemsPerPage,
        currentPage * itemsPerPage
    );
    // 处理页码变化
    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    // 处理批准申请点击
    const handleApproveClick = (application) => {
        setSelectedApplication(application);
        setApprovalData({
            actualBorrowDate: application.plannedBorrowDate || '',
            adminNote: ''
        });
        setError('');
        setShowApproveModal(true);
    };

    // 关闭批准模态框
    const handleCloseApproveModal = () => {
        setShowApproveModal(false);
        setSelectedApplication(null);
        setApprovalData({
            actualBorrowDate: '',
            adminNote: ''
        });
        setError('');
    };  

    // 关闭拒绝模态框
    const handleCloseRejectModal = () => {
        setShowRejectModal(false);
        setSelectedApplication(null);
        setRejectData({
            adminNote: ''
        });
        setError('');
    };
    // 处理批准表单输入
    const handleApprovalInputChange = (field, value) => {
        setApprovalData(prev => ({
            ...prev,
            [field]: value
        }));
    };

    // 提交批准申请
    const handleSubmitApproval = async () => {
        // 验证表单
        if (!approvalData.actualBorrowDate) {
            setError('実貸出日を入力してください');
            return;
        }

        if (!approvalData.adminNote.trim()) {
            setError('管理者メモを入力してください');
            return;
        }

        setLoading(true);
        setError('');

        try {
            // 调用API批准申请
            const response = await approveApplicationAPI(
                selectedApplication.recordId,
                {
                    ...approvalData,
                    status: 'approved'
                }
            );

            if (response.success) {
                // 调用父组件的回调函数更新状态
                if (onApprove) {
                    onApprove(selectedApplication, approvalData);
                }
                
                // 关闭模态框
                handleCloseApproveModal();
                
            }
        } catch (error) {
            console.error('批准申请失败:', error);
            setError('承認処理中にエラーが発生しました。もう一度お試しください。');
        } finally {
            setLoading(false);
        }
    };
    //颜色
    const getStatusBadgeVariant = (status) => {
        switch (status) {
            case 'pending':
                return 'success';
            case 'approved':
                return 'warning';
            case 'rejected':
                return 'danger';
            default:
                return 'secondary';
        }
    };


    // 格式化日期
    const formatDate = (dateString) => {
        if (!dateString) return '-';
        try {
            return new Date(dateString).toLocaleDateString('ja-JP');
        } catch {
            return '-';
        }
    };

    // 格式化日期时间
    const formatDateTime = (dateString) => {
        if (!dateString) return '-';
        try {
            const date = new Date(dateString);
            return `${date.toLocaleDateString('ja-JP')} ${date.toLocaleTimeString('ja-JP', { 
                hour: '2-digit', 
                minute: '2-digit' 
            })}`;
        } catch {
            return '-';
        }
    };

    // 渲染操作按钮
    const renderActionButtons = (application) => {
        if (application.status === 'pending') {
            return (
                <div className="btn-group-sm  d-flex justify-content-center" role="group">
                    <Button 
                        variant="success" 
                        size="sm"
                        className="me-2"
                        onClick={() => handleApproveClick(application)}
                    >
                        ✅ 
                    </Button>
                    <Button 
                        variant="outline-danger" 
                        size="sm"
                        onClick={() => onReject && onReject(application)}
                    >
                        ❌ 
                    </Button>
                </div>
            );
        } else {
            return (
                <div className="text-muted small">
                    <div>{formatDate(application.updateAt)} 処理済み</div>
                </div>
            );
        }
    };

    return (
        <div>
            {/* 搜索和筛选栏 */}
            <Card className="border-0 shadow-sm mb-3">
                <Card.Body>
                    <Row className="g-3 align-items-center">
                        <Col md={7}>
                            <div className="input-group">
                                <span className="input-group-text bg-light border-end-0">
                                    🔍
                                </span>
                                <Form.Control
                                    type="text"
                                    placeholder="申請者、資産名、またはIDで検索..."
                                    className="border-start-0"
                                />
                            </div>
                        </Col>
                        <Col md={3}>
                            <Form.Select>
                                <option>全ステータス</option>
                                <option>承認待ち</option>
                                <option>承認済み</option>
                                <option>拒否済み</option>
                            </Form.Select>
                        </Col>
                        
                        <Col md={2}>
                            <Form.Control
                                type="date"
                                placeholder="申請日でフィルター"
                            />
                        </Col>
                    </Row>
                </Card.Body>
            </Card>

            {/* 申請テーブル */}
            <div className = "border-0 shadow-sm rounded" 
                style={{ minHeight: '880PX'}}
            >
                <Table responsive hover className="mb-0">
                    <thead className="bg-light">
                        <tr>
                            <th className="ps-4 py-3" style={{ width: '90px' }}></th>
                            <th className="py-3 " >申請情報</th>
                            <th className="py-3 text-center" >申請者</th>
                            <th className="py-3 text-center" >期間</th>
                            <th className="py-3 text-center">ステータス</th>
                            <th className="py-3 text-center">申請日</th>
                            {showActions && (
                                <th className="text-center py-3" style={{ width: '210px' }}>操作</th>
                            )}
                        </tr>
                    </thead>
                    <tbody>
                        {currentData.map((application, index) => (
                            <tr key={application.recordId}>
                                <td className="ps-4 py-3 align-middle">
                                    <div 
                                        className="align-items-center justify-content-center"
                                    >
                                        <img 
                                            className="rounded"
                                            src={application.asset.imageUrl} 
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
                                        <div className="fw-bold">{application.asset?.name}</div>
                                        <div className="text-muted small">{application.asset?.assetCode}</div>
                                    </div>
                                </td>
                                <td className="py-3 text-center align-middle">
                                    <div>
                                        <div className="fw-semibold">{application.user?.name}</div>
                                    </div>
                                </td>
                                <td className="py-3 text-center align-middle">
                                    <div>
                                        <div className="small">{formatDate(application.plannedBorrowDate)+ 
                                                                '~' + formatDate(application.plannedReturnDate)}</div>
                                        {application.actualBorrowDate && (
                                            <div className="small text-primary mt-1">
                                                実貸出: {formatDate(application.actualBorrowDate)}
                                            </div>
                                        )}
                                    </div>
                                </td>
                                <td className="py-3 text-center align-middle">
                                    <Badge 
                                        bg={getStatusBadgeVariant(application.status)} 
                                        className="fw-normal"
                                    >
                                        {applicationStatus[application.status] || application.status}
                                    </Badge>
                                </td>
                                <td className="py-3 text-center align-middle">
                                    <div className="text-muted small">
                                        {formatDateTime(application.applyDate)}
                                    </div>
                                </td>
                                {showActions && (
                                    <td className="text-center py-3 align-middle">
                                        {renderActionButtons(application)}
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
                                style={{ width: '50px', height: '50px', background: '#fff3cd' }}
                            >
                                ⏳
                            </div>
                            <div>
                                <div className="h4 mb-0">{stats.pending}</div>
                                <div className="text-muted">保留中</div>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={3} >
                    <Card className="h-100 border-0 shadow-sm">
                        <Card.Body className="d-flex align-items-center">
                            <div 
                                className="rounded-circle d-flex align-items-center justify-content-center me-3"
                                style={{ width: '50px', height: '50px', background: '#d1edff' }}
                            >
                                ✅
                            </div>
                            <div>
                                <div className="h4 mb-0">{stats.approved}</div>
                                <div className="text-muted">承認済み</div>
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
                                ❌
                            </div>
                            <div>
                                <div className="h4 mb-0">{stats.rejected}</div>
                                <div className="text-muted">拒否済み</div>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={3} >
                    <Card className="h-100 border-0 shadow-sm">
                        <Card.Body className="d-flex align-items-center">
                            <div 
                                className="rounded-circle d-flex align-items-center justify-content-center me-3"
                                style={{ width: '50px', height: '50px', background: '#e2e3e5' }}
                            >
                                📊
                            </div>
                            <div>
                                <div className="h4 mb-0">{stats.total}</div>
                                <div className="text-muted">合計</div>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            {/* 批准申请Modal */}
            <Modal 
                show={showApproveModal} 
                onHide={handleCloseApproveModal}
                size="lg"
                centered
            >
                <Modal.Header closeButton className="border-0">
                    <Modal.Title className="d-flex align-items-center">
                        <span className="me-2">✅</span>
                        申請承認確認
                    </Modal.Title>
                </Modal.Header>
                
                <Modal.Body className="p-4">
                    {selectedApplication && (
                        <>
                            {/* 申请信息概览 */}
                            <Card className="border-0 bg-light mb-4">
                                <Card.Body>
                                    <h6 className="mb-3">申請情報</h6>
                                    <Row>
                                        <Col md={6}>
                                            <div className="mb-2">
                                                <strong>資産名:</strong> {selectedApplication.asset?.name}
                                            </div>
                                            
                                            <div className="mb-2">
                                                <strong>申請者:</strong> {selectedApplication.user?.name}
                                            </div>
                                        </Col>
                                        <Col md={6}>
                                            <div className="mb-2">
                                                <strong>資産コード:</strong> {selectedApplication.asset?.assetCode}
                                            </div>
                                            <div className="mb-2">
                                                <strong>計画期間:</strong> {formatDate(selectedApplication.plannedBorrowDate)} 〜 {formatDate(selectedApplication.plannedReturnDate)}
                                            </div>
                                        </Col>
                                    </Row>
                                    <Row 
                                        style={{ 
                                            minHeight: '50px', 
                                            maxHeight: '150px', // 根据需求调整
                                            overflowY: 'auto',
                                            padding: '8px' // 可选：添加内边距让内容更美观
                                        }}
                                        className="border rounded" // 可选：添加边框
                                    >
                                        <div className="mb-2">
                                            <strong>目的:</strong> {selectedApplication.purpose}
                                        </div>
                                    </Row>
                                </Card.Body>
                            </Card>

                            {/* 错误提示 */}
                            {error && (
                                <Alert variant="danger" className="mb-3">
                                    {error}
                                </Alert>
                            )}

                            {/* 批准表单 */}
                            <Form>
                                <Row className="g-3">
                                    <Col md={6}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>
                                                実貸出日 <span className="text-danger">*</span>
                                            </Form.Label>
                                            <Form.Control
                                                type="date"
                                                value={approvalData.actualBorrowDate}
                                                onChange={(e) => handleApprovalInputChange('actualBorrowDate', e.target.value)}
                                                min={new Date().toISOString().split('T')[0]}
                                            />
                                            <Form.Text className="text-muted">
                                                実際の貸出開始日を入力してください
                                            </Form.Text>
                                        </Form.Group>
                                    </Col>
                                    
                                    <Col md={12}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>
                                                管理者メモ <span className="text-danger">*</span>
                                            </Form.Label>
                                            <Form.Control
                                                as="textarea"
                                                rows={4}
                                                placeholder="承認に関するメモや注意事項を入力してください..."
                                                value={approvalData.adminNote}
                                                onChange={(e) => handleApprovalInputChange('adminNote', e.target.value)}
                                            />
                                            <Form.Text className="text-muted">
                                                申請者への連絡事項や特記事項を記入してください
                                            </Form.Text>
                                        </Form.Group>
                                    </Col>
                                </Row>
                            </Form>
                        </>
                    )}
                </Modal.Body>
                
                <Modal.Footer className="border-0">
                    <Button 
                        variant="outline-secondary" 
                        onClick={handleCloseApproveModal}
                        disabled={loading}
                    >
                        キャンセル
                    </Button>
                    <Button 
                        variant="success" 
                        onClick={handleSubmitApproval}
                        disabled={loading}
                        className="d-flex align-items-center"
                    >
                        {loading ? (
                            <>
                                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                処理中...
                            </>
                        ) : (
                            <>
                                <span className="me-2">✅</span>
                                承認する
                            </>
                        )}
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* 批准拒绝Modal*/ }
            <Modal 
                show={showRejectModal} 
                onHide={handleCloseRejectModal}
                size="lg"
                centered
            >
                <Modal.Header closeButton className="border-0">
                    <Modal.Title className="d-flex align-items-center">
                        <span className="me-2">✅</span>
                        申請承認拒否
                    </Modal.Title>
                </Modal.Header>
                
                <Modal.Body className="p-4">
                    {selectedApplication && (
                        <>
                            {/* 申请信息概览 */}
                            <Card className="border-0 bg-light mb-4">
                                <Card.Body>
                                    <h6 className="mb-3">申請情報</h6>
                                    <Row>
                                        <Col md={6}>
                                            <div className="mb-2">
                                                <strong>資産名:</strong> {selectedApplication.asset?.name}
                                            </div>
                                            
                                            <div className="mb-2">
                                                <strong>申請者:</strong> {selectedApplication.user?.name}
                                            </div>
                                        </Col>
                                        <Col md={6}>
                                            <div className="mb-2">
                                                <strong>資産コード:</strong> {selectedApplication.asset?.assetCode}
                                            </div>
                                            <div className="mb-2">
                                                <strong>計画期間:</strong> {formatDate(selectedApplication.plannedBorrowDate)} 〜 {formatDate(selectedApplication.plannedReturnDate)}
                                            </div>
                                        </Col>
                                    </Row>
                                    <Row 
                                        style={{ 
                                            minHeight: '50px', 
                                            maxHeight: '150px', // 根据需求调整
                                            overflowY: 'auto',
                                            padding: '8px' // 可选：添加内边距让内容更美观
                                        }}
                                        className="border rounded" // 可选：添加边框
                                    >
                                        <div className="mb-2">
                                            <strong>目的:</strong> {selectedApplication.purpose}
                                        </div>
                                    </Row>
                                </Card.Body>
                            </Card>

                            {/* 错误提示 */}
                            {error && (
                                <Alert variant="danger" className="mb-3">
                                    {error}
                                </Alert>
                            )}

                            {/* 拒绝表单 */}
                            <Form>
                                <Row className="g-3">
                                    <Col md={12}>
                                        <Form.Group className="mb-3">
                                            <Form.Label>
                                                管理者メモ <span className="text-danger">*</span>
                                            </Form.Label>
                                            <Form.Control
                                                as="textarea"
                                                rows={4}
                                                placeholder="承認に関するメモや注意事項を入力してください..."
                                                value={approvalData.adminNote}
                                                onChange={(e) => handleApprovalInputChange('adminNote', e.target.value)}
                                            />
                                            <Form.Text className="text-muted">
                                                申請者への連絡事項や特記事項を記入してください
                                            </Form.Text>
                                        </Form.Group>
                                    </Col>
                                </Row>
                            </Form>
                        </>
                    )}
                </Modal.Body>
                
                <Modal.Footer className="border-0">
                    <Button 
                        variant="outline-secondary" 
                        onClick={handleCloseApproveModal}
                        disabled={loading}
                    >
                        キャンセル
                    </Button>
                    <Button 
                        variant="success" 
                        onClick={handleSubmitApproval}
                        disabled={loading}
                        className="d-flex align-items-center"
                    >
                        {loading ? (
                            <>
                                <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                                処理中...
                            </>
                        ) : (
                            <>
                                <span className="me-2">✅</span>
                                承認する
                            </>
                        )}
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
};

export default ApplicationTable;