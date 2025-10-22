import { useState } from 'react';
import React from 'react';
import { Table, Badge, Button } from 'react-bootstrap';
import { assetStatus, assetRole } from '@types/common';


const AssetTable = ({ 
    assets, 
    onEdit, 
    onDelete, 
    showActions = true 
}) => {
    
    
    // 获取状态对应的徽章颜色
    const getStatusBadgeVariant = (status) => {
        switch (status) {
            case 'available':
                return 'success';
            case 'borrowed':
                return 'warning';
            case 'maintenance':
                return 'danger';
            default:
                return 'secondary';
        }
    };

    // 获取资产图标
    const getAssetIcon = (category) => {
        switch (category) {
            case 'laptop':
                return '💻';
            case 'monitor':
                return '🖥️';
            case 'peripheral':
                return '⌨️';
            case 'tablet':
                return '📱';
            case 'audio':
                return '🎧';
            default:
                return '📦';
        }
    };

    // 获取资产图标背景色
    const getAssetIconBackground = (index) => {
        const gradients = [
            'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
            'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
            'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
            'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
        ];
        return gradients[index % gradients.length];
    };

    // 格式化日期
    const formatDate = (dateString) => {
        if (!dateString) return '-';
        return new Date(dateString).toLocaleDateString('ja-JP');
    };

    return (
        <Table responsive hover className="mb-0">
            <thead className="bg-light">
                <tr>
                    <th className="ps-4 py-3" style={{ width: '60px' }}></th>
                    <th className="py-3">資産情報</th>
                    <th className="py-3">ステータス</th>
                    <th className="py-3">購入日</th>
                    {showActions && (
                        <th className="text-center py-3" style={{ width: '120px' }}>操作</th>
                    )}
                </tr>
            </thead>
            <tbody>
                {assets.map((asset, index) => (
                    <tr key={asset.assetCode}>
                        <td className="ps-4 py-3">
                            <div 
                                className="rounded d-flex align-items-center justify-content-center"
                                style={{ 
                                    width: '40px', 
                                    height: '40px', 
                                    background: getAssetIconBackground(index)
                                }}
                            >
                                <span className="text-white">
                                    {getAssetIcon(asset.category)}
                                </span>
                            </div>
                        </td>
                        <td className="py-3">
                            <div>
                                <div className="fw-bold">{asset.name}</div>
                                <div className="text-muted small">{asset.assetCode}</div>
                                <div className="text-muted small mt-1">{asset.description}</div>
                            </div>
                        </td>
                        {/* <td className="py-3">
                            <Badge bg="light" text="dark" className="fw-normal">
                                {assetCategories[asset.category] || asset.category}
                            </Badge>
                        </td> */}
                        <td className="py-3">
                            <Badge 
                                bg={getStatusBadgeVariant(asset.status)} 
                                className="fw-normal"
                            >
                                {assetStatus[asset.status] || asset.status}
                            </Badge>
                        </td>
                        <td className="py-3">
                            <div className="text-muted small">
                                {formatDate(asset.purchaseDate)}
                            </div>
                        </td>
                        {showActions && (
                            <td className="text-center py-3">
                                <div className="btn-group" role="group">
                                    <Button 
                                        variant="outline-primary" 
                                        size="sm"
                                        className="border-0"
                                        title="編集"
                                        onClick={() => onEdit && onEdit(asset)}
                                    >
                                        ✏️
                                    </Button>
                                    <Button 
                                        variant="outline-danger" 
                                        size="sm"
                                        className="border-0"
                                        title="削除"
                                        onClick={() => onDelete && onDelete(asset)}
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
    );
};

export default AssetTable;