import React, { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import {
  Container,
  Row,
  Col,
  Card,
  Form,
  Button,
  Alert
} from 'react-bootstrap';
import axios from 'axios';

const LoginPage = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const isAuthenticated = localStorage.getItem('authToken');
    const navigate = useNavigate();
    
    useEffect(() => {
        if (isAuthenticated) {
            navigate('/', { replace: true });
        }
    }, [isAuthenticated, navigate]);

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        
        // 模拟登录成功
        setTimeout(() => {
            localStorage.setItem('authToken', '1123123123');
            localStorage.setItem('email',JSON.stringify({ email: email } ));
            localStorage.setItem('user', JSON.stringify({ role: password == '123123'? 'admin':'user' } ));
            setLoading(false);
            navigate('/', { replace: true });
        }, 1000);

    };

    return (
        <Container fluid className="d-flex align-items-center justify-content-center min-vh-100 bg-light">
            <Row className="w-100 justify-content-center">
                <Col xs={12} sm={8} md={6} lg={4}>
                    <Card className="shadow-lg border-0">
                        <Card.Body className="p-5">
                            {/* ヘッダー */}
                            <div className="text-center mb-4">
                                <h2 className="fw-bold text-primary">ログイン</h2>
                                <p className="text-muted">システムにアクセスするにはログインしてください</p>
                            </div>

                            {/* エラーメッセージ */}
                            {error && (
                                <Alert variant="danger" className="mb-3">
                                    {error}
                                </Alert>
                            )}

                            {/* ログインフォーム */}
                            <Form onSubmit={handleLogin}>
                                <Form.Group className="mb-3">
                                    <Form.Label className="fw-medium">メールアドレス</Form.Label>
                                    <Form.Control
                                        type="email"
                                        placeholder="example@company.com"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        required
                                        size="lg"
                                    />
                                </Form.Group>

                                <Form.Group className="mb-4">
                                    <Form.Label className="fw-medium">パスワード</Form.Label>
                                    <Form.Control
                                        type="password"
                                        placeholder="パスワードを入力"
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        required
                                        size="lg"
                                    />
                                </Form.Group>

                                <Button
                                    variant="primary"
                                    type="submit"
                                    className="w-100 py-2"
                                    size="lg"
                                    disabled={loading}
                                >
                                    {loading ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2" />
                                            ログイン中...
                                        </>
                                    ) : (
                                        'ログイン'
                                    )}
                                </Button>
                            </Form>

                            {/* 追加情報 */}
                            <div className="text-center mt-4">
                                <small className="text-muted">
                                    アカウントをお持ちでない場合はシステム管理者にお問い合わせください
                                </small>
                            </div>
                        </Card.Body>
                    </Card>

                    {/* フッター情報 */}
                    <div className="text-center mt-4">
                        <small className="text-muted">
                            © 2024 資産管理システム. All rights reserved.
                        </small>
                    </div>
                </Col>
            </Row>
        </Container>
    );
};

export default LoginPage;