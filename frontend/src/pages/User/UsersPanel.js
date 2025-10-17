import {
    Row,
    Col,
    Card,
    Badge,
    Button,
    Form,
    Table,
    Pagination
  } from 'react-bootstrap';
  
  function UsersPanel({ stats }) {
    return (
      <div>
        {/* 内容头部 */}
        <div className="mb-4">
          <div className="d-flex justify-content-between align-items-center">
            <div>
              <h2>ユーザー管理</h2>
              <p className="text-muted mb-0">システムユーザーのアカウントと権限を管理</p>
            </div>
            <Button variant="primary" className="d-flex align-items-center">
              <span className="me-2">👤</span>
              新規ユーザー追加
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
                  👥
                </div>
                <div>
                  <div className="h4 mb-0">{stats.total}</div>
                  <div className="text-muted">総ユーザー数</div>
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
                  <div className="h4 mb-0">{stats.active}</div>
                  <div className="text-muted">アクティブ</div>
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
                  ⏸️
                </div>
                <div>
                  <div className="h4 mb-0">{stats.inactive}</div>
                  <div className="text-muted">非アクティブ</div>
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
                  👑
                </div>
                <div>
                  <div className="h4 mb-0">{stats.admin}</div>
                  <div className="text-muted">管理者</div>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
  
        {/* 搜索和筛选栏 */}
        <Card className="border-0 shadow-sm mb-4">
          <Card.Body>
            <Row className="g-3 align-items-center">
              <Col md={5}>
                <div className="input-group">
                  <span className="input-group-text bg-light border-end-0">
                    🔍
                  </span>
                  <Form.Control
                    type="text"
                    placeholder="ユーザー名、メールアドレス、または部署で検索..."
                    className="border-start-0"
                  />
                </div>
              </Col>
              <Col md={3}>
                <Form.Select>
                  <option>全ステータス</option>
                  <option>アクティブ</option>
                  <option>非アクティブ</option>
                </Form.Select>
              </Col>
              <Col md={2}>
                <Form.Select>
                  <option>全権限</option>
                  <option>管理者</option>
                  <option>一般ユーザー</option>
                </Form.Select>
              </Col>
              <Col md={2}>
                <Form.Select>
                  <option>全部署</option>
                  <option>開発部</option>
                  <option>営業部</option>
                  <option>人事部</option>
                </Form.Select>
              </Col>
            </Row>
          </Card.Body>
        </Card>
  
        {/* ユーザーテーブル */}
        <Card className="border-0 shadow-sm">
          <Card.Body className="p-0">
            <Table responsive hover className="mb-0">
              <thead className="bg-light">
                <tr>
                  <th className="ps-4 py-3" style={{ width: '60px' }}></th>
                  <th className="py-3">ユーザー情報</th>
                  <th className="py-3">部署・役職</th>
                  <th className="py-3">最終ログイン</th>
                  <th className="py-3">ステータス</th>
                  <th className="py-3">権限</th>
                  <th className="text-center py-3" style={{ width: '120px' }}>操作</th>
                </tr>
              </thead>
              <tbody>
                {/* ユーザー行 1 */}
                <tr>
                  <td className="ps-4 py-3">
                    <div 
                      className="rounded-circle d-flex align-items-center justify-content-center"
                      style={{ width: '40px', height: '40px', background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}
                    >
                      <span className="text-white">YT</span>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-bold">山田 太郎</div>
                      <div className="text-muted small">taro.yamada@company.com</div>
                      <div className="text-muted small mt-1">社員ID: EMP001</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-semibold">開発部</div>
                      <div className="text-muted small">シニアエンジニア</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="small">2024-01-20</div>
                      <div className="text-muted small">09:15</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="success" className="fw-normal">
                      アクティブ
                    </Badge>
                  </td>
                  <td className="py-3">
                    <Badge bg="primary" className="fw-normal">
                      管理者
                    </Badge>
                  </td>
                  <td className="text-center py-3">
                    <div className="btn-group" role="group">
                      <Button 
                        variant="outline-primary" 
                        size="sm"
                        className="border-0"
                        title="編集"
                      >
                        ✏️
                      </Button>
                      <Button 
                        variant="outline-danger" 
                        size="sm"
                        className="border-0"
                        title="削除"
                      >
                        🗑️
                      </Button>
                    </div>
                  </td>
                </tr>
  
                {/* ユーザー行 2 */}
                <tr>
                  <td className="ps-4 py-3">
                    <div 
                      className="rounded-circle d-flex align-items-center justify-content-center"
                      style={{ width: '40px', height: '40px', background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' }}
                    >
                      <span className="text-white">SH</span>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-bold">佐藤 花子</div>
                      <div className="text-muted small">hanako.sato@company.com</div>
                      <div className="text-muted small mt-1">社員ID: EMP002</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-semibold">営業部</div>
                      <div className="text-muted small">営業マネージャー</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="small">2024-01-19</div>
                      <div className="text-muted small">14:30</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="success" className="fw-normal">
                      アクティブ
                    </Badge>
                  </td>
                  <td className="py-3">
                    <Badge bg="secondary" className="fw-normal">
                      一般ユーザー
                    </Badge>
                  </td>
                  <td className="text-center py-3">
                    <div className="btn-group" role="group">
                      <Button 
                        variant="outline-primary" 
                        size="sm"
                        className="border-0"
                        title="編集"
                      >
                        ✏️
                      </Button>
                      <Button 
                        variant="outline-danger" 
                        size="sm"
                        className="border-0"
                        title="削除"
                      >
                        🗑️
                      </Button>
                    </div>
                  </td>
                </tr>
  
                {/* ユーザー行 3 */}
                <tr>
                  <td className="ps-4 py-3">
                    <div 
                      className="rounded-circle d-flex align-items-center justify-content-center"
                      style={{ width: '40px', height: '40px', background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' }}
                    >
                      <span className="text-white">SI</span>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-bold">鈴木 一郎</div>
                      <div className="text-muted small">ichiro.suzuki@company.com</div>
                      <div className="text-muted small mt-1">社員ID: EMP003</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-semibold">マーケティング部</div>
                      <div className="text-muted small">マーケティングスペシャリスト</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="small">2024-01-18</div>
                      <div className="text-muted small">11:45</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="success" className="fw-normal">
                      アクティブ
                    </Badge>
                  </td>
                  <td className="py-3">
                    <Badge bg="secondary" className="fw-normal">
                      一般ユーザー
                    </Badge>
                  </td>
                  <td className="text-center py-3">
                    <div className="btn-group" role="group">
                      <Button 
                        variant="outline-primary" 
                        size="sm"
                        className="border-0"
                        title="編集"
                      >
                        ✏️
                      </Button>
                      <Button 
                        variant="outline-danger" 
                        size="sm"
                        className="border-0"
                        title="削除"
                      >
                        🗑️
                      </Button>
                    </div>
                  </td>
                </tr>
  
                {/* ユーザー行 4 */}
                <tr>
                  <td className="ps-4 py-3">
                    <div 
                      className="rounded-circle d-flex align-items-center justify-content-center"
                      style={{ width: '40px', height: '40px', background: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }}
                    >
                      <span className="text-white">TM</span>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-bold">田中 美咲</div>
                      <div className="text-muted small">misaki.tanaka@company.com</div>
                      <div className="text-muted small mt-1">社員ID: EMP004</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-semibold">人事部</div>
                      <div className="text-muted small">人事アシスタント</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="small">2024-01-15</div>
                      <div className="text-muted small">16:20</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="warning" className="fw-normal">
                      非アクティブ
                    </Badge>
                  </td>
                  <td className="py-3">
                    <Badge bg="secondary" className="fw-normal">
                      一般ユーザー
                    </Badge>
                  </td>
                  <td className="text-center py-3">
                    <div className="btn-group" role="group">
                      <Button 
                        variant="outline-primary" 
                        size="sm"
                        className="border-0"
                        title="編集"
                      >
                        ✏️
                      </Button>
                      <Button 
                        variant="outline-danger" 
                        size="sm"
                        className="border-0"
                        title="削除"
                      >
                        🗑️
                      </Button>
                    </div>
                  </td>
                </tr>
  
                {/* ユーザー行 5 */}
                <tr>
                  <td className="ps-4 py-3">
                    <div 
                      className="rounded-circle d-flex align-items-center justify-content-center"
                      style={{ width: '40px', height: '40px', background: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' }}
                    >
                      <span className="text-white">TK</span>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-bold">高橋 健太</div>
                      <div className="text-muted small">kenta.takahashi@company.com</div>
                      <div className="text-muted small mt-1">社員ID: EMP005</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-semibold">デザイン部</div>
                      <div className="text-muted small">UI/UXデザイナー</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="small">2024-01-20</div>
                      <div className="text-muted small">08:45</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="success" className="fw-normal">
                      アクティブ
                    </Badge>
                  </td>
                  <td className="py-3">
                    <Badge bg="primary" className="fw-normal">
                      管理者
                    </Badge>
                  </td>
                  <td className="text-center py-3">
                    <div className="btn-group" role="group">
                      <Button 
                        variant="outline-primary" 
                        size="sm"
                        className="border-0"
                        title="編集"
                      >
                        ✏️
                      </Button>
                      <Button 
                        variant="outline-danger" 
                        size="sm"
                        className="border-0"
                        title="削除"
                      >
                        🗑️
                      </Button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </Table>
          </Card.Body>
        </Card>
  
        {/* ページネーション */}
        <div className="d-flex justify-content-between align-items-center mt-4">
          <div className="text-muted small">
            1-5 of {stats.total} ユーザーを表示
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
  
        {/* ユーザーアクション統計 */}
        <Row className="mt-4">
          <Col md={4}>
            <Card className="border-0 shadow-sm">
              <Card.Body>
                <div className="d-flex align-items-center">
                  <div 
                    className="rounded-circle d-flex align-items-center justify-content-center me-3"
                    style={{ width: '50px', height: '50px', background: '#e7f3ff' }}
                  >
                    📋
                  </div>
                  <div>
                    <div className="h5 mb-0">156</div>
                    <div className="text-muted small">今月の申請</div>
                  </div>
                </div>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4}>
            <Card className="border-0 shadow-sm">
              <Card.Body>
                <div className="d-flex align-items-center">
                  <div 
                    className="rounded-circle d-flex align-items-center justify-content-center me-3"
                    style={{ width: '50px', height: '50px', background: '#fff0e6' }}
                  >
                    💻
                  </div>
                  <div>
                    <div className="h5 mb-0">42</div>
                    <div className="text-muted small">貸出中資産</div>
                  </div>
                </div>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4}>
            <Card className="border-0 shadow-sm">
              <Card.Body>
                <div className="d-flex align-items-center">
                  <div 
                    className="rounded-circle d-flex align-items-center justify-content-center me-3"
                    style={{ width: '50px', height: '50px', background: '#f0f9ff' }}
                  >
                    🔔
                  </div>
                  <div>
                    <div className="h5 mb-0">23</div>
                    <div className="text-muted small">保留中通知</div>
                  </div>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </div>
    );
  }
  
  export default UsersPanel;