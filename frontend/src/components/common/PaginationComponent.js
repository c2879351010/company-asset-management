import React from 'react';
import { Pagination } from 'react-bootstrap';

const PaginationComponent = ({ 
  currentPage,
  totalItems,
  itemsPerPage = 10,
  onPageChange
}) => {
  // 在组件内部计算分页相关数据
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage + 1;
  const endIndex = Math.min(currentPage * itemsPerPage, totalItems);

  // 生成页码数组（只显示当前页附近的页码）
  const getPageNumbers = () => {
    const pages = [];
    const maxVisiblePages = 5;
    
    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);
    
    // 调整起始页码以确保显示足够的页码
    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
  };

  const pageNumbers = getPageNumbers();

  return (
    <div className="d-flex justify-content-between align-items-center">
      <div className="text-muted small p-4 ">
        {startIndex}-{endIndex} of {totalItems} を表示
      </div>
      <div className="me-4">
        <Pagination style={{ marginBottom: 0 }}>
          <Pagination.Prev 
            disabled={currentPage === 1}
            onClick={() => onPageChange(currentPage - 1)}
          />
          
          {/* 显示第一页和省略号 */}
          {pageNumbers[0] > 1 && (
            <>
              <Pagination.Item
                key={1}
                active={1 === currentPage}
                onClick={() => onPageChange(1)}
              >
                1
              </Pagination.Item>
              {pageNumbers[0] > 2 && <Pagination.Ellipsis />}
            </>
          )}
          
          {/* 显示页码 */}
          {pageNumbers.map(page => (
            <Pagination.Item
              key={page}
              active={page === currentPage}
              onClick={() => onPageChange(page)}
            >
              {page}
            </Pagination.Item>
          ))}
          
          {/* 显示最后一页和省略号 */}
          {pageNumbers[pageNumbers.length - 1] < totalPages && (
            <>
              {pageNumbers[pageNumbers.length - 1] < totalPages - 1 && <Pagination.Ellipsis />}
              <Pagination.Item
                key={totalPages}
                active={totalPages === currentPage}
                onClick={() => onPageChange(totalPages)}
              >
                {totalPages}
              </Pagination.Item>
            </>
          )}
          
          <Pagination.Next 
            disabled={currentPage === totalPages}
            onClick={() => onPageChange(currentPage + 1)}
          />
        </Pagination>
      </div>
    </div>
  );
};

export default PaginationComponent;