import React, { useState } from 'react';
import { 
    Table, 
    Badge, 
    Button,
    Row,
    Col,
    Card,
    Pagination,
    Form
 } from 'react-bootstrap';
import { userType } from '@types/common';
import PaginationComponent from '@components/common/PaginationComponent';
const UserTable = ({ 
    users, 
    onEdit, 
    onDelete, 
    onRoleChange,
    showActions = true 
}) => {

    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 10; // 每页显示的数量
    
    // 计算总页数
    const totalPages = Math.ceil(users.length / itemsPerPage);
    // 获取当前页的数据
    const currentData = users.slice(
        (currentPage - 1) * itemsPerPage,
        currentPage * itemsPerPage
    );

    // 处理页码变化
    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    // 获取角色对应的徽章颜色
    const getRoleBadgeVariant = (role) => {
        switch (role) {
            case 'ADMIN':
                return 'warning';
            case 'USER':
                return 'light';
            default:
                return 'secondary';
        }
    };


    // 获取用户头像背景色
    const getUserAvatarBackground = (index) => {
        const gradients = [
            'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
            'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
            'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
            'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
            'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
            'linear-gradient(135deg, #5ee7df 0%, #b490ca 100%)'
        ];
        return gradients[index % gradients.length];
    };

    // 获取用户姓名首字母
    const getUserInitials = (name) => {
        if (!name) return '👤';
        return name.split(' ').map(word => word.charAt(0)).join('').toUpperCase();
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
                                    placeholder="ユーザー名、メールアドレス、または社員IDで検索..."
                                    className="border-start-0"
                                />
                            </div>
                        </Col>
                        <Col md={3}>
                            <Form.Select>
                                <option>管理者</option>
                                <option>一般ユーザー</option>
                            </Form.Select>
                        </Col>
                    </Row>
                </Card.Body>
            </Card>

            {/* ユーザーテーブル */}
            <div className = "border-0 shadow-sm rounded" 
                style={{ minHeight: '900px'}}
            >
                <Table responsive hover className="mb-0">
                    <thead className="bg-light">
                        <tr>
                            <th className="ps-4 py-3" style={{ width: '90px' }}></th>
                            <th className="py-3 ">ユーザー情報</th>
                            <th className="py-3 text-center">権限</th>
                            {showActions && (
                                <th className="text-center py-3" style={{ width: '210px' }}>操作</th>
                            )}
                        </tr>
                    </thead>
                    <tbody>
                        {currentData.map((user, index) => (
                            //console.log(user.userId,localStorage.getItem('userId'),user.userId == localStorage.getItem('userId')),
                            //console.log(user.status),
                            <tr key={user.employeeId}>
                                <td className="ps-4 py-3 align-middle">
                                    <div 
                                        className="rounded-circle d-flex align-items-center justify-content-center"
                                        style={{ 
                                            width: '48px', 
                                            height: '48px', 
                                            background: getUserAvatarBackground(index),
                                            color: 'white',
                                            fontWeight: 'bold',
                                            fontSize: '16px'
                                        }}
                                    >
                                        {getUserInitials(user.name)}
                                    </div>
                                </td>
                                <td className="py-3 align-middle">
                                    <div>
                                        <div className="fw-bold">{user.name}</div>
                                        <div className="text-muted small">{user.email}</div>
                                    </div>
                                </td>
                                <td className="py-3 text-center align-middle">
                                    <Badge 
                                        bg={getRoleBadgeVariant(user.role)} 
                                        text={user.role === 'USER' ? 'dark' : 'dark'}
                                        className="fw-normal"
                                    >
                                        {userType[user.role] || user.role}
                                    </Badge>
                                </td>
                                {showActions && (
                                    <td className="text-center py-3 align-middle">
                                        <div className="btn-group" role="group">
                                            <Button 
                                                variant="outline-primary" 
                                                size="sm"
                                                className="border-0"
                                                title="編集"
                                                disabled = {user.userId == localStorage.getItem('userId')}
                                                onClick={() => {
                                                    onEdit && onEdit(user);
                                                }}
                                            >
                                                ✏️
                                            </Button>
                                            {/* <Button 
                                                variant="outline-secondary" 
                                                size="sm"
                                                className="border-0"
                                                title="権限設定"
                                                onClick={() => onRoleChange && onRoleChange(user)}
                                            >
                                                🔑
                                            </Button> */}
                                            <Button 
                                                variant="outline-danger" 
                                                size="sm"
                                                className="border-0"
                                                title="削除"
                                                disabled = {user.userId == localStorage.getItem('userId')|| user.status == 'Active'}
                                                onClick={() => onDelete && onDelete(user)}
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
                totalItems={users.length}
                currentItemsCount={currentData.length}
            />
        </div>
    );
};

export default UserTable;