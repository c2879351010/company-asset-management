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
  
  function AssetsPanel({ stats }) {
    return (
      <div>
        {/* 内容头部 */}
        <div className="mb-4">
          <div className="d-flex justify-content-between align-items-center">
            <div>
              <h2>資産管理</h2>
              <p className="text-muted mb-0">システム内の全ての資産情報を管理</p>
            </div>
            <Button variant="primary" className="d-flex align-items-center">
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
              <Col md={6}>
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
                  <option>全カテゴリー</option>
                  <option>ノートパソコン</option>
                  <option>モニター</option>
                  <option>周辺機器</option>
                  <option>オーディオ機器</option>
                </Form.Select>
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
  
        {/* 資産テーブル */}
        <Card className="border-0 shadow-sm">
          <Card.Body className="p-0">
            <Table responsive hover className="mb-0">
              <thead className="bg-light">
                <tr>
                  <th className="ps-4 py-3" style={{ width: '60px' }}></th>
                  <th className="py-3">資産情報</th>
                  <th className="py-3">カテゴリー</th>
                  <th className="py-3">ステータス</th>
                  <th className="py-3">最終更新</th>
                  <th className="text-center py-3" style={{ width: '120px' }}>操作</th>
                </tr>
              </thead>
              <tbody>
                {/* 資産行 1 */}
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
                      <div className="text-muted small mt-1">16インチ M2 Maxチップ 32GBメモリ</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="light" text="dark" className="fw-normal">
                      ノートパソコン
                    </Badge>
                  </td>
                  <td className="py-3">
                    <Badge bg="success" className="fw-normal">
                      貸出可能
                    </Badge>
                  </td>
                  <td className="py-3">
                    <div className="text-muted small">2024-01-15</div>
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
  
                {/* 資産行 2 */}
                <tr>
                  <td className="ps-4 py-3">
                    <div 
                      className="rounded d-flex align-items-center justify-content-center"
                      style={{ width: '40px', height: '40px', background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' }}
                    >
                      <span className="text-white">💻</span>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-bold">ThinkPad X1 Carbon</div>
                      <div className="text-muted small">AST-002</div>
                      <div className="text-muted small mt-1">14インチ Intel Core i7 16GBメモリ</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="light" text="dark" className="fw-normal">
                      ノートパソコン
                    </Badge>
                  </td>
                  <td className="py-3">
                    <Badge bg="warning" className="fw-normal">
                      貸出中
                    </Badge>
                  </td>
                  <td className="py-3">
                    <div className="text-muted small">2024-01-18</div>
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
  
                {/* 資産行 3 */}
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
                      <div className="text-muted small mt-1">27インチ 4K IPS プロフェッショナルデザインモニター</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="light" text="dark" className="fw-normal">
                      モニター
                    </Badge>
                  </td>
                  <td className="py-3">
                    <Badge bg="success" className="fw-normal">
                      貸出可能
                    </Badge>
                  </td>
                  <td className="py-3">
                    <div className="text-muted small">2024-01-10</div>
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
  
                {/* 資産行 4 */}
                <tr>
                  <td className="ps-4 py-3">
                    <div 
                      className="rounded d-flex align-items-center justify-content-center"
                      style={{ width: '40px', height: '40px', background: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' }}
                    >
                      <span className="text-white">⌨️</span>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-bold">Logicool MX Keys</div>
                      <div className="text-muted small">AST-004</div>
                      <div className="text-muted small mt-1">ワイヤレスBluetoothキーボード、快適なタイピング体験</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="light" text="dark" className="fw-normal">
                      周辺機器
                    </Badge>
                  </td>
                  <td className="py-3">
                    <Badge bg="danger" className="fw-normal">
                      メンテナンス中
                    </Badge>
                  </td>
                  <td className="py-3">
                    <div className="text-muted small">2024-01-20</div>
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
  
                {/* 資産行 5 */}
                <tr>
                  <td className="ps-4 py-3">
                    <div 
                      className="rounded d-flex align-items-center justify-content-center"
                      style={{ width: '40px', height: '40px', background: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' }}
                    >
                      <span className="text-white">📱</span>
                    </div>
                  </td>
                  <td className="py-3">
                    <div>
                      <div className="fw-bold">Apple iPad Pro</div>
                      <div className="text-muted small">AST-005</div>
                      <div className="text-muted small mt-1">12.9インチ M2チップ 5Gバージョン</div>
                    </div>
                  </td>
                  <td className="py-3">
                    <Badge bg="light" text="dark" className="fw-normal">
                      タブレット
                    </Badge>
                  </td>
                  <td className="py-3">
                    <Badge bg="success" className="fw-normal">
                      貸出可能
                    </Badge>
                  </td>
                  <td className="py-3">
                    <div className="text-muted small">2024-01-12</div>
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
            1-5 of {stats.total} 資産を表示
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
      </div>
    );
  }
  
  export default AssetsPanel;