/**
 * 申请相关API服务
 */

// 模拟API调用
const simulateAPICall = (data, delay = 1000) => {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve({ success: true, data });
        }, delay);
    });
};

// 批准申请API
export const approveApplicationAPI = async (applicationId, approvalData) => {
    try {
        console.log('调用批准申请API:', { applicationId, approvalData });
        
        // 模拟调用
        const response = await simulateAPICall({
            id: applicationId,
            ...approvalData,
            processedAt: new Date().toISOString(),
            processedBy: 'current_user_id' // 从登录信息获取
        });
        
        return response;
    } catch (error) {
        console.error('批准申请API错误:', error);
        throw error;
    }
};

// 拒绝申请API
export const rejectApplicationAPI = async (applicationId, rejectData) => {
    try {
        console.log('调用拒绝申请API:', { applicationId, rejectData });
        
        // API调用
        const response = await simulateAPICall({
            id: applicationId,
            ...rejectData,
            processedAt: new Date().toISOString(),
            processedBy: 'current_user_id'
        });
        
        return response;
    } catch (error) {
        console.error('拒绝申请API错误:', error);
        throw error;
    }
};