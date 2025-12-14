/* User */
export const userRole = {
    firstName: '',
    lastName: '',
    firstKana: '',
    lastKana: '',
    email: '',
    status: 'Active',
    role: 'USER'
}
export const userType = {
    Admin: '管理者',
    User: '一般ユーザー'
};


/* Asset */
export const assetRole = {
    assetCode: '',
    name: '',
    description: '',
    status: 'available',
    purchaseDate: '',
    imageUrl: ''
}
export const assetStatus = {
    available: '貸出可能',
    borrowed: '貸出中',
    maintenance: 'メンテナンス中'
};

/* Applications */
export const applicationRole = {
    recordId: '',
    asset: {
        assetCode: '',
        name: '',
        description: '',
        status: 'available',
        purchaseDate: '',
        imageUrl: ''
    },
    user: {
        name: '',
        employeeId: '',
        email: ''
    },
    applyDate: '',
    plannedBorrowDate: '',
    plannedReturnDate: '',
    actualBorrowDate: '',
    actualReturnDate: '',
    status: 'pending',
    purpose: '',
    adminNote: '',
    createAt: '',
    updateAt: ''
};

export const applicationStatus = {
    pending: '承認待ち',
    approved: '承認済み',
    rejected: '拒否済み'
};
export default {
    userRole,
    assetRole,
    assetStatus
}