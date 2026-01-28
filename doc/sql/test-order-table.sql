-- ============================================================================
-- 测试订单表（用于验证数据权限功能）
-- ============================================================================

DROP TABLE IF EXISTS ez_admin_test_order CASCADE;

CREATE TABLE ez_admin_test_order (
    order_id BIGINT NOT NULL,
    order_no VARCHAR(50) NOT NULL,
    order_amount DECIMAL(10, 2) NOT NULL,
    order_status SMALLINT NOT NULL DEFAULT 0,
    customer_name VARCHAR(100),
    remark VARCHAR(500),
    created_by BIGINT NOT NULL,
    create_time TIMESTAMP NOT NULL,
    update_by BIGINT NOT NULL,
    update_time TIMESTAMP NOT NULL,
    is_deleted SMALLINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_ez_admin_test_order PRIMARY KEY (order_id)
);

COMMENT ON TABLE ez_admin_test_order IS '测试订单表（用于验证数据权限功能）';
COMMENT ON COLUMN ez_admin_test_order.order_id IS '订单ID';
COMMENT ON COLUMN ez_admin_test_order.order_no IS '订单号';
COMMENT ON COLUMN ez_admin_test_order.order_amount IS '订单金额';
COMMENT ON COLUMN ez_admin_test_order.order_status IS '订单状态【0 待支付 1 已支付 2 已完成 3 已取消】';
COMMENT ON COLUMN ez_admin_test_order.customer_name IS '客户名称';
COMMENT ON COLUMN ez_admin_test_order.remark IS '备注';
COMMENT ON COLUMN ez_admin_test_order.created_by IS '创建人ID（用于数据权限过滤）';
COMMENT ON COLUMN ez_admin_test_order.create_time IS '创建时间';
COMMENT ON COLUMN ez_admin_test_order.update_by IS '更新人ID';
COMMENT ON COLUMN ez_admin_test_order.update_time IS '更新时间';
COMMENT ON COLUMN ez_admin_test_order.is_deleted IS '是否删除【0 正常 1 已删除】';

-- 创建索引
CREATE INDEX idx_order_created_by ON ez_admin_test_order(created_by);
CREATE INDEX idx_order_no ON ez_admin_test_order(order_no);

-- 插入测试数据
INSERT INTO ez_admin_test_order (order_id, order_no, order_amount, order_status, customer_name, remark, created_by, create_time, update_by, update_time, is_deleted) VALUES
(1, 'ORD20260128001', 1000.00, 1, '客户A', '测试订单1', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 0),
(2, 'ORD20260128002', 2000.00, 1, '客户B', '测试订单2', 1, CURRENT_TIMESTAMP, 1, CURRENT_TIMESTAMP, 0),
(3, 'ORD20260128003', 3000.00, 1, '客户C', '测试订单3', 2, CURRENT_TIMESTAMP, 2, CURRENT_TIMESTAMP, 0),
(4, 'ORD20260128004', 1500.00, 1, '客户D', '测试订单4', 2, CURRENT_TIMESTAMP, 2, CURRENT_TIMESTAMP, 0),
(5, 'ORD20260128005', 2500.00, 1, '客户E', '测试订单5', 3, CURRENT_TIMESTAMP, 3, CURRENT_TIMESTAMP, 0);

COMMENT ON TABLE ez_admin_test_order IS '测试数据说明：订单1-2由用户1创建，订单3-4由用户2创建，订单5由用户3创建';
