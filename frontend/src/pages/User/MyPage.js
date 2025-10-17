import { useState } from 'react';
import styles from './MyPage.module.css';

function MyPage() {
  const [userInfo, setUserInfo] = useState({
    name: '山田 太郎',
    email: 'yamada@example.com',
    phone: '090-1234-5678',
    department: '技術部',
    position: 'フロントエンドエンジニア',
    joinDate: '2022-03-15',
    role: 'admin' // 'admin' または 'user'
  });

  const [loginInfo, setLoginInfo] = useState({
    currentEmail: 'yamada@example.com',
    newEmail: '',
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });

  const [isEditingLogin, setIsEditingLogin] = useState(false);

  // 簡易資産貸出記録
  const borrowRecords = [
    { 
      id: 1, 
      assetName: 'MacBook Pro 2023', 
      assetId: 'AST-001',
      borrowDate: '2024-01-15', 
      returnDate: '2024-01-20',
      status: 'returned',
      notes: 'プロジェクト開発使用'
    },
    { 
      id: 2, 
      assetName: 'ThinkPad X1', 
      assetId: 'AST-002',
      borrowDate: '2024-01-18', 
      returnDate: '2024-01-25',
      status: 'returned',
      notes: '一時代替機器'
    },
    { 
      id: 3, 
      assetName: 'Dell モニター U2720Q', 
      assetId: 'AST-003',
      borrowDate: '2024-01-22', 
      returnDate: null,
      status: 'borrowed',
      notes: '作業画面拡張'
    },
    { 
      id: 4, 
      assetName: 'Logicool MX Keys', 
      assetId: 'AST-004',
      borrowDate: '2024-01-10', 
      returnDate: '2024-01-12',
      status: 'returned',
      notes: '一時使用'
    }
  ];

  const handleLoginInfoChange = (field, value) => {
    setLoginInfo(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleSaveLoginInfo = () => {
    // ここにログイン情報保存のロジックを追加
    console.log('ログイン情報を保存:', loginInfo);
    setIsEditingLogin(false);
    // フォームをリセット
    setLoginInfo(prev => ({
      ...prev,
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }));
  };

  const handleCancelEdit = () => {
    setIsEditingLogin(false);
    setLoginInfo(prev => ({
      ...prev,
      newEmail: '',
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }));
  };

  const getStatusBadge = (status) => {
    const statusConfig = {
      borrowed: { label: '貸出中', class: styles.statusBorrowed },
      returned: { label: '返却済み', class: styles.statusReturned },
      overdue: { label: '期限超過', class: styles.statusOverdue }
    };
    
    const config = statusConfig[status] || { label: status, class: styles.statusDefault };
    return <span className={`${styles.statusBadge} ${config.class}`}>{config.label}</span>;
  };

  const getRoleBadge = (role) => {
    return role === 'admin' 
      ? <span className={styles.adminBadge}>管理者</span>
      : <span className={styles.userBadge}>一般ユーザー</span>;
  };

  return (
    <div className={styles.page}>
      {/* ページタイトル */}
      <div className={styles.pageHeader}>
        <h1>マイページ</h1>
        <p>個人情報と設定を管理</p>
      </div>

      <div className={styles.content}>
        {/* 左カラム - 個人情報と権限 */}
        <div className={styles.leftColumn}>
          {/* 個人情報カード */}
          <div className={styles.profileCard}>
            <div className={styles.avatarSection}>
              <div className={styles.avatar}>
                {userInfo.name.charAt(0)}
              </div>
              <div className={styles.avatarInfo}>
                <div className={styles.nameRole}>
                  <h3>{userInfo.name}</h3>
                  {getRoleBadge(userInfo.role)}
                </div>
                <p>{userInfo.position}</p>
                <span className={styles.status}>オンライン</span>
              </div>
            </div>
            
            <div className={styles.profileDetails}>
              <div className={styles.detailItem}>
                <span className={styles.label}>メールアドレス:</span>
                <span className={styles.value}>{userInfo.email}</span>
              </div>
              <div className={styles.detailItem}>
                <span className={styles.label}>電話番号:</span>
                <span className={styles.value}>{userInfo.phone}</span>
              </div>
              <div className={styles.detailItem}>
                <span className={styles.label}>部署:</span>
                <span className={styles.value}>{userInfo.department}</span>
              </div>
              <div className={styles.detailItem}>
                <span className={styles.label}>入社日:</span>
                <span className={styles.value}>{userInfo.joinDate}</span>
              </div>
              <div className={styles.detailItem}>
                <span className={styles.label}>権限レベル:</span>
                <span className={styles.value}>
                  {userInfo.role === 'admin' ? 'システム管理者' : '一般ユーザー'}
                </span>
              </div>
            </div>

            <button className={styles.editButton}>
              ✏️ 個人情報を編集
            </button>
          </div>

          {/* 権限説明カード */}
          <div className={styles.permissionCard}>
            <h3>権限説明</h3>
            <div className={styles.permissionList}>
              {userInfo.role === 'admin' ? (
                <>
                  <div className={styles.permissionItem}>
                    <span className={styles.permissionIcon}>🔧</span>
                    <div className={styles.permissionText}>
                      <strong>システム管理</strong>
                      <span>ユーザーとシステム設定を管理</span>
                    </div>
                  </div>
                  <div className={styles.permissionItem}>
                    <span className={styles.permissionIcon}>📦</span>
                    <div className={styles.permissionText}>
                      <strong>資産管理</strong>
                      <span>資産の追加、編集、削除</span>
                    </div>
                  </div>
                  <div className={styles.permissionItem}>
                    <span className={styles.permissionIcon}>👥</span>
                    <div className={styles.permissionText}>
                      <strong>ユーザー管理</strong>
                      <span>ユーザー権限と情報を管理</span>
                    </div>
                  </div>
                </>
              ) : (
                <>
                  <div className={styles.permissionItem}>
                    <span className={styles.permissionIcon}>👀</span>
                    <div className={styles.permissionText}>
                      <strong>資産閲覧</strong>
                      <span>システム資産の閲覧と検索</span>
                    </div>
                  </div>
                  <div className={styles.permissionItem}>
                    <span className={styles.permissionIcon}>📋</span>
                    <div className={styles.permissionText}>
                      <strong>貸出申請</strong>
                      <span>資産貸出申請の提出</span>
                    </div>
                  </div>
                  <div className={styles.permissionItem}>
                    <span className={styles.permissionIcon}>📊</span>
                    <div className={styles.permissionText}>
                      <strong>個人統計</strong>
                      <span>個人貸出記録の閲覧</span>
                    </div>
                  </div>
                </>
              )}
            </div>
          </div>
        </div>

        {/* 右カラム - ログイン情報と貸出記録 */}
        <div className={styles.rightColumn}>
          {/* ログイン情報変更カード */}
          <div className={styles.loginCard}>
            <div className={styles.cardHeader}>
              <h3>ログイン情報</h3>
              {!isEditingLogin && (
                <button 
                  className={styles.editLoginButton}
                  onClick={() => setIsEditingLogin(true)}
                >
                  ✏️ 変更
                </button>
              )}
            </div>

            {isEditingLogin ? (
              <div className={styles.loginForm}>
                <div className={styles.formGroup}>
                  <label>現在のメールアドレス</label>
                  <input 
                    type="email" 
                    value={loginInfo.currentEmail}
                    disabled
                    className={styles.disabledInput}
                  />
                </div>

                <div className={styles.formGroup}>
                  <label>新しいメールアドレス</label>
                  <input 
                    type="email" 
                    value={loginInfo.newEmail}
                    onChange={(e) => handleLoginInfoChange('newEmail', e.target.value)}
                    placeholder="新しいメールアドレスを入力"
                  />
                </div>

                <div className={styles.formGroup}>
                  <label>現在のパスワード</label>
                  <input 
                    type="password" 
                    value={loginInfo.currentPassword}
                    onChange={(e) => handleLoginInfoChange('currentPassword', e.target.value)}
                    placeholder="現在のパスワードを入力"
                  />
                </div>

                <div className={styles.formGroup}>
                  <label>新しいパスワード</label>
                  <input 
                    type="password" 
                    value={loginInfo.newPassword}
                    onChange={(e) => handleLoginInfoChange('newPassword', e.target.value)}
                    placeholder="新しいパスワードを入力"
                  />
                </div>

                <div className={styles.formGroup}>
                  <label>新しいパスワード（確認）</label>
                  <input 
                    type="password" 
                    value={loginInfo.confirmPassword}
                    onChange={(e) => handleLoginInfoChange('confirmPassword', e.target.value)}
                    placeholder="新しいパスワードを再入力"
                  />
                </div>

                <div className={styles.formActions}>
                  <button 
                    className={styles.cancelButton}
                    onClick={handleCancelEdit}
                  >
                    キャンセル
                  </button>
                  <button 
                    className={styles.saveButton}
                    onClick={handleSaveLoginInfo}
                  >
                    変更を保存
                  </button>
                </div>
              </div>
            ) : (
              <div className={styles.loginInfo}>
                <div className={styles.infoItem}>
                  <span className={styles.infoLabel}>ログインメール:</span>
                  <span className={styles.infoValue}>{loginInfo.currentEmail}</span>
                </div>
                <div className={styles.infoItem}>
                  <span className={styles.infoLabel}>最終更新:</span>
                  <span className={styles.infoValue}>2024-01-10 14:30</span>
                </div>
                <div className={styles.securityTip}>
                  🔒 定期的なパスワード変更でアカウントを保護
                </div>
              </div>
            )}
          </div>

          {/* 資産貸出記録カード */}
          <div className={styles.borrowCard}>
            <div className={styles.cardHeader}>
              <h3>資産貸出記録</h3>
              <span className={styles.recordCount}>
                全 {borrowRecords.length} 件
              </span>
            </div>

            <div className={styles.recordsList}>
              {borrowRecords.map(record => (
                <div key={record.id} className={styles.recordItem}>
                  <div className={styles.recordHeader}>
                    <div className={styles.assetInfo}>
                      <span className={styles.assetName}>{record.assetName}</span>
                      <span className={styles.assetId}>{record.assetId}</span>
                    </div>
                    {getStatusBadge(record.status)}
                  </div>
                  
                  <div className={styles.recordDetails}>
                    <div className={styles.detailRow}>
                      <span>貸出日:</span>
                      <span>{record.borrowDate}</span>
                    </div>
                    <div className={styles.detailRow}>
                      <span>返却日:</span>
                      <span>{record.returnDate || '未返却'}</span>
                    </div>
                    {record.notes && (
                      <div className={styles.detailRow}>
                        <span>備考:</span>
                        <span className={styles.notes}>{record.notes}</span>
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>

            <button className={styles.viewAllButton}>
              全ての貸出記録を見る →
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default MyPage;