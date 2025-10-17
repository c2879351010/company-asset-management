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
  
  function ApplicationsPanel({ stats }) {
    return (
      <div>
        {/* 内容头部 */}
        <div className="mb-4">
          <div className="d-flex justify-content-between align-items-center">
            <div>
              <h2>申請管理</h2>
              <p className="text-muted mb-0">資産貸出申請と承認プロセスを処理</p>
            </div>
            <div className="btn-group">
              <Button variant="outline-primary" className="d-flex align-items-center">
                <span className="me-2">📥</span>
                エクスポート
              </Button>
              <Button variant="outline-secondary" className="d-flex align-items-center">
                <span className="me-2">🔄</span>
                更新
              </Button>
            </div>
          </div>
        </div>
  
        {/* 统计概览 */}
        <Row className="mb-4">
          <Col md={3} className="mb-3">
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
          <Col md={3} className="mb-3">
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
          <Col md={3} className="mb-3">
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
          <Col md={3} className="mb-3">
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
  
        {/* 搜索和筛选栏 */}
        <Card className="border-0 shadow-sm mb-4">
          <Card.Body>
            <Row className="g-3 align-items-center">
              <Col md={4}>
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
                  <option>保留中</option>
                  <option>承認済み</option>
                  <option>拒否済み</option>
                </Form.Select>
              </Col>
              <Col md={3}>
                <Form.Select>
                  <option>全カテゴリー</option>
                  <option>ノートパソコン</option>
                  <option>モニター</option>
                  <option>周辺機器</option>
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
        <Card className="border-0 shadow-sm">
          <Card.Body className="p-0">
            <Table responsive hover className="mb-0">
              <thead className="bg-light">
                <tr>
                  <th className="ps-4 py-3" style={{ width: '60px' }}></th>
                  <th className="py-3">申請情報</th>
                  <th className="py-3">申請者</th>
                  <th className="py-3">期間</th>
                  <th className="py-3">ステータス</th>
                  <th className="py-3">申請日</th>
                  <th className="text-center py-3" style={{ width: '150px' }}>操作</th>
                </tr>
              </thead>
              <tbody>
                {/* 申請行 1 - 保留中 */}
                <tr>
                  <td className="ps-4 py-3">
                    <div 
                      className="rounded d-flex align-items-center justify-content-center"
                      style={{ width: '40px', height: '40px', background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}
                    >
                      <span className="text-white">💻</span>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-bold">MacBook Pro 2023</div>
                      <div className="text-muted small">AST-001</div>
                      <div className="text-muted small mt-1">プロジェクト開発のため</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-semibold">山田 太郎</div>
                      <div className="text-muted small">開発部</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="small">2024-01-20</div>
                      <div className="small text-muted">〜 2024-01-25</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="warning" className="fw-normal">
                      ⏳ 保留中
                    </Badge>
                  </td>
                  <td className="py-3">
                    <div className="text-muted small">2024-01-18</div>
                    <div className="text-muted small">14:30</div>
                  </td>
                  <td className="text-center py-3">
                    <div className="btn-group-vertical btn-group-sm" role="group">
                      <Button 
                        variant="success" 
                        size="sm"
                        className="mb-1"
                      >
                        ✅ 承認
                      </Button>
                      <Button 
                        variant="outline-danger" 
                        size="sm"
                      >
                        ❌ 拒否
                      </Button>
                    </div>
                  </td>
                </tr>
  
                {/* 申請行 2 - 保留中 */}
                <tr>
                  <td className="ps-4 py-3">
                    <div 
                      className="rounded d-flex align-items-center justify-content-center"
                      style={{ width: '40px', height: '40px', background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' }}
                    >
                      <span className="text-white">🖥️</span>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-bold">Dell モニター U2720Q</div>
                      <div className="text-muted small">AST-003</div>
                      <div className="text-muted small mt-1">会議プレゼンテーション用</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-semibold">佐藤 花子</div>
                      <div className="text-muted small">営業部</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="small">2024-01-22</div>
                      <div className="small text-muted">〜 2024-01-22</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="warning" className="fw-normal">
                      ⏳ 保留中
                    </Badge>
                  </td>
                  <td className="py-3">
                    <div className="text-muted small">2024-01-19</div>
                    <div className="text-muted small">10:15</div>
                  </td>
                  <td className="text-center py-3">
                    <div className="btn-group-vertical btn-group-sm" role="group">
                      <Button 
                        variant="success" 
                        size="sm"
                        className="mb-1"
                      >
                        ✅ 承認
                      </Button>
                      <Button 
                        variant="outline-danger" 
                        size="sm"
                      >
                        ❌ 拒否
                      </Button>
                    </div>
                  </td>
                </tr>
  
                {/* 申請行 3 - 承認済み */}
                <tr>
                  <td className="ps-4 py-3">
                    <div 
                      className="rounded d-flex align-items-center justify-content-center"
                      style={{ width: '40px', height: '40px', background: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }}
                    >
                      <span className="text-white">📱</span>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-bold">Apple iPad Pro</div>
                      <div className="text-muted small">AST-005</div>
                      <div className="text-muted small mt-1">顧客デモンストレーション</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-semibold">鈴木 一郎</div>
                      <div className="text-muted small">マーケティング部</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="small">2024-01-15</div>
                      <div className="small text-muted">〜 2024-01-17</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="success" className="fw-normal">
                      ✅ 承認済み
                    </Badge>
                  </td>
                  <td className="py-3">
                    <div className="text-muted small">2024-01-14</div>
                    <div className="text-muted small">09:45</div>
                  </td>
                  <td className="text-center py-3">
                    <div className="text-muted small">
                      <div>2024-01-14 承認</div>
                      <div>山田 管理者</div>
                    </div>
                  </td>
                </tr>
  
                {/* 申請行 4 - 拒否済み */}
                <tr>
                  <td className="ps-4 py-3">
                    <div 
                      className="rounded d-flex align-items-center justify-content-center"
                      style={{ width: '40px', height: '40px', background: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' }}
                    >
                      <span className="text-white">🎧</span>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-bold">Sony WH-1000XM4</div>
                      <div className="text-muted small">AST-006</div>
                      <div className="text-muted small mt-1">オンライン会議用</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-semibold">田中 美咲</div>
                      <div className="text-muted small">人事部</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="small">2024-01-16</div>
                      <div className="small text-muted">〜 2024-01-18</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="danger" className="fw-normal">
                      ❌ 拒否済み
                    </Badge>
                  </td>
                  <td className="py-3">
                    <div className="text-muted small">2024-01-15</div>
                    <div className="text-muted small">16:20</div>
                  </td>
                  <td className="text-center py-3">
                    <div className="text-muted small">
                      <div>2024-01-15 拒否</div>
                      <div>在庫不足のため</div>
                    </div>
                  </td>
                </tr>
  
                {/* 申請行 5 - 承認済み */}
                <tr>
                  <td className="ps-4 py-3">
                    <div 
                      className="rounded d-flex align-items-center justify-content-center"
                      style={{ width: '40px', height: '40px', background: 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)' }}
                    >
                      <span className="text-white">📷</span>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-bold">Canon EOS R5</div>
                      <div className="text-muted small">AST-007</div>
                      <div className="text-muted small mt-1">製品写真撮影</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-semibold">高橋 健太</div>
                      <div className="text-muted small">デザイン部</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="small">2024-01-12</div>
                      <div className="small text-muted">〜 2024-01-14</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="success" className="fw-normal">
                      ✅ 承認済み
                    </Badge>
                  </td>
                  <td className="py-3">
                    <div className="text-muted small">2024-01-11</div>
                    <div className="text-muted small">11:10</div>
                  </td>
                  <td className="text-center py-3">
                    <div className="text-muted small">
                      <div>2024-01-11 承認</div>
                      <div>佐藤 管理者</div>
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
            1-5 of {stats.total} 申請を表示
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
  
        {/* クイックアクションボタン */}
        <Card className="border-0 shadow-sm mt-4">
          <Card.Body>
            <div className="d-flex justify-content-between align-items-center">
              <div>
                <h6 className="mb-1">一括操作</h6>
                <p className="text-muted small mb-0">選択した申請に対して一括操作を実行</p>
              </div>
              <div className="btn-group">
                <Button variant="outline-success" size="sm">
                  ✅ 選択を承認
                </Button>
                <Button variant="outline-danger" size="sm">
                  ❌ 選択を拒否
                </Button>
                <Button variant="outline-secondary" size="sm">
                  📧 選択にメール送信
                </Button>
              </div>
            </div>
          </Card.Body>
        </Card>
      </div>
    );
  }
  
  export default ApplicationsPanel;