package com.ez.admin.common.framework.datascope;

/**
 * 数据权限上下文
 * <p>
 * 使用 ThreadLocal 保存当前请求的数据权限信息，在数据权限拦截器中使用
 * </p>
 * <p>
 * 生命周期：
 * <ul>
 *   <li>登录时：设置数据权限信息到上下文</li>
 *   <li>请求处理中：数据权限拦截器从上下文获取信息，生成 SQL 条件</li>
 *   <li>请求结束后：清理上下文，避免内存泄漏</li>
 * </ul>
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 1. 登录时设置数据权限信息
 * DataScopeContext.setDataScopeInfo(dataScopeInfo);
 *
 * // 2. 在数据权限拦截器中获取
 * DataScopeInfo info = DataScopeContext.getDataScopeInfo();
 *
 * // 3. 请求结束后清理（建议在拦截器或过滤器中统一处理）
 * DataScopeContext.clear();
 * }</pre>
 * </p>
 *
 * @author ez-admin
 * @since 2026-01-28
 */
public final class DataScopeContext {

    private static final ThreadLocal<DataScopeInfo> CONTEXT = new ThreadLocal<>();

    private DataScopeContext() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * 设置数据权限信息
     *
     * @param dataScopeInfo 数据权限信息
     */
    public static void setDataScopeInfo(DataScopeInfo dataScopeInfo) {
        CONTEXT.set(dataScopeInfo);
    }

    /**
     * 获取数据权限信息
     *
     * @return 数据权限信息，如果未设置则返回 null
     */
    public static DataScopeInfo getDataScopeInfo() {
        return CONTEXT.get();
    }

    /**
     * 清理数据权限信息
     * <p>
     * 建议在请求结束后调用，避免 ThreadLocal 内存泄漏
     * </p>
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
