/* User */
export const userRole = {
    name: '',
    employeeId:'',
    email: '',
    passworldHash: '',
    role: 'User'
}
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

export default {
    userRole,
    assetRole,
    assetStatus
}