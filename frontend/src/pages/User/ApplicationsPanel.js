import { useState } from 'react';
import { Container, Card, Button, CardBody,Row,Col } from 'react-bootstrap';
import ApplicationTable from '@components/tables/ApplicationTable';
import { mockApplications } from '@mocks/Application';

function ApplicationsPanel() {
    const [applications, setApplications] = useState(mockApplications);
    
    // 计算统计信息
    const applicationStats = {
        total: applications.length,
        pending: applications.filter(app => app.status === 'pending').length,
        approved: applications.filter(app => app.status === 'approved' || app.status === 'borrowed').length,
        rejected: applications.filter(app => app.status === 'rejected').length
    };

    const handleApproveApplication = (application) => {
        const updatedApplications = applications.map(app => 
          app.recordId === application.recordId 
            ? { ...app, status: 'approved', updateAt: new Date().toISOString() }
            : app
          );
        setApplications(updatedApplications);

        alert('申請が正常に承認されました');
    };

    const handleRejectApplication = (application) => {
        const updatedApplications = applications.map(app => 
          app.recordId === application.recordId 
            ? { ...app, status: 'rejected', updateAt: new Date().toISOString() }
            : app
        );
        setApplications(updatedApplications);
        alert('申請を拒否しました');
    };

    return (
        <div className="p-4">
            <Col className='mb-1'>
                <Card className="border-0 shadow-sm">
                <Card.Body className="p-4">
                    <Row className="align-items-center">
                        <Col md={10}>
                            <div className="d-flex flex-column">
                            <h2 className="mb-2 fw-bold">申請管理</h2>
                            <p className="text-muted mb-0">資産貸出申請と承認プロセスを処理</p>                            </div>
                        </Col>
                        <Col md={2} >
                            <div className="d-flex justify-content-end">
                                <Button variant="outline-primary" >
                                <span className="me-2">📥</span>
                                エクスポート
                                </Button>
                            </div>
                        </Col>
                    </Row>
                </Card.Body>
                </Card>
            </Col>
            <Card className="border-0 shadow-sm">
                <Card.Body className="p-0">
                    <ApplicationTable 
                        applications={applications}
                        stats={applicationStats}
                        onApprove={handleApproveApplication}
                        onReject={handleRejectApplication}
                        showActions={true}
                    />
                </Card.Body>
            </Card>
        </div>
    );
}

export default ApplicationsPanel;