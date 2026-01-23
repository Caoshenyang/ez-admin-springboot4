package com.ez.admin.generator;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Types;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * MyBatis-Plus 代码生成器（交互式）
 *
 * <p>运行时通过控制台交互式输入配置信息：
 * <ul>
 *   <li>业务模块名称（如：user、role、menu、dept、dict）</li>
 *   <li>表名（支持逗号分隔多个表，或输入 all 生成所有表）</li>
 * </ul>
 *
 * <p>生成规则：
 * <ul>
 *   <li>输入模块名：如 "user"，生成包名 "com.ez.admin.system.modules.user"</li>
 *   <li>输入模块名：如 "admin"，生成包名 "com.ez.admin.system.modules.admin"</li>
 *   <li>所有代码统一生成到 ez-admin 单体模块</li>
 *   <li>Java 代码路径：ez-admin/src/main/java/com/ez/admin/system/modules/{模块}/</li>
 *   <li>Mapper XML 路径：ez-admin/src/main/resources/mapper/{模块}/</li>
 * </ul>
 *
 * <p>数据库配置优先级：系统环境变量 > JVM 启动参数 > .env 文件 > 默认值
 *
 * @author ez-admin
 * @since 2025-01-19
 */
public class CodeGenerator {

    /**
     * 读取数据库配置，优先级：系统环境变量 > JVM 启动参数 > .env 文件 > 默认值
     */
    private static final Map<String, String> DOT_ENV = loadDotEnv();
    private static final String DB_HOST = env("DB_HOST", "localhost");
    private static final String DB_PORT = env("DB_PORT", "5432");
    private static final String DB_NAME = env("DB_NAME", "ez-admin");
    private static final String USERNAME = env("DB_USERNAME", "postgres");
    private static final String PASSWORD = env("DB_PASSWORD", "postgres");

    /**
     * PostgreSQL 数据库 URL
     */
    private static final String DB_URL = String.format(
            "jdbc:postgresql://%s:%s/%s",
            DB_HOST, DB_PORT, DB_NAME);

    public static void main(String[] args) {
        AtomicReference<String> moduleShortName = new AtomicReference<>("");

        // 获取项目根路径
        String projectRoot = System.getProperty("user.dir");

        FastAutoGenerator.create(DB_URL, USERNAME, PASSWORD)
                // 全局配置
                .globalConfig((scanner, builder) -> {
                    moduleShortName.set(scanner.apply("请输入业务模块名（如：admin、blog、order）："));

                    // 所有代码统一生成到 ez-admin 单体模块
                    String javaOutputDir = projectRoot + "/ez-admin/src/main/java";

                    builder.author("ez-admin")
                            .disableOpenDir() // 生成完毕后不自动打开资源管理器
                            .enableSpringdoc() // 启用 SpringDoc OpenAPI 注解，生成 @Schema 注解
                            .outputDir(javaOutputDir);
                })
                // 数据源配置（PostgreSQL 类型转换处理）
                .dataSourceConfig(builder ->
                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                            // PostgreSQL SMALLINT 转换为 Integer
                            if (typeCode == Types.SMALLINT) {
                                return DbColumnType.INTEGER;
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        })
                )
                // 包配置
                .packageConfig((scanner, builder) -> {
                    // 包名：com.ez.admin.system.modules.{module}
                    // 例如：输入 user → com.ez.admin.system.modules.user
                    //      输入 admin → com.ez.admin.system.modules.admin
                    String packageName = "com.ez.admin.system.modules." + moduleShortName.get();

                    // Mapper XML 生成路径：ez-admin/src/main/resources/mapper/{module}/
                    String mapperXmlPath = projectRoot + "/ez-admin/src/main/resources/mapper/" + moduleShortName.get();

                    builder.parent(packageName)
                            .entity("entity")
                            .mapper("mapper")
                            .serviceImpl("service")
                            .controller("controller")
                            .pathInfo(Collections.singletonMap(OutputFile.xml, mapperXmlPath));
                })
                // 策略配置
                .strategyConfig((scanner, builder) -> {
                    // 设置需要生成的表名
                    builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔（输入 all 生成所有表）：")))
                            .addTablePrefix("ez_admin_") // 设置过滤表前缀
                            .entityBuilder() // 设置 entity 生成规则
                            .addTableFills(new Column("create_time", FieldFill.INSERT))
                            .addTableFills(new Column("create_by", FieldFill.INSERT))
                            .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE))
                            .addTableFills(new Column("update_by", FieldFill.INSERT_UPDATE))
                            .logicDeleteColumnName("is_deleted")
                            .idType(IdType.ASSIGN_ID) // 使用雪花算法生成主键
                            .enableLombok() // lombok 注解
                            .enableTableFieldAnnotation() // 启用字段注解
                            .fieldUseJavaDoc(false) // 禁用字段 JavaDoc 注释
                            .mapperBuilder() // 设置 mapper 生成规则
                            .serviceBuilder() // 设置 service 生成规则
                            .disableService() // 禁用 Service 接口层生成
                            .formatServiceImplFileName("%sService") // 格式化 ServiceImpl 类名，去掉后缀 "Impl"
                            .mapperBuilder() // 设置 mapper 生成规则
                            .controllerBuilder() // 设置 controller 生成规则
                            .disable();// 禁用生成 @RestController 控制器（根据业务需求手动创建）
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 FreeMarker 引擎模板
                .execute();
    }

    /**
     * 处理表名输入（支持 "all" 关键字）
     *
     * @param tables 表名（多个逗号分隔，或输入 all）
     * @return 表名列表
     */
    protected static List<String> getTables(String tables) {
        return "all".equalsIgnoreCase(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }

    /**
     * 获取环境变量（支持多级优先级）
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    private static String env(String key, String defaultValue) {
        String value = firstNonBlank(
                System.getenv(key),
                System.getProperty(key),
                DOT_ENV.get(key)
        );
        return value == null ? defaultValue : value;
    }

    /**
     * 获取第一个非空值
     *
     * @param values 值数组
     * @return 第一个非空值
     */
    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    /**
     * 加载 .env 文件配置
     *
     * <p>支持的 .env 文件路径：
     * <ul>
     *   <li>项目根目录: .env</li>
     *   <li>资源目录: src/main/resources/.env</li>
     * </ul>
     *
     * @return 配置键值对
     */
    private static Map<String, String> loadDotEnv() {
        Map<String, String> result = new LinkedHashMap<>();
        List<Path> candidates = Arrays.asList(
                Paths.get(".env"),
                Paths.get("src/main/resources/.env")
        );

        for (Path path : candidates) {
            if (!Files.exists(path)) {
                continue;
            }
            try {
                Files.lines(path)
                        .map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                        .forEach(line -> {
                            int idx = line.indexOf("=");
                            if (idx > 0) {
                                String key = line.substring(0, idx).trim();
                                String value = line.substring(idx + 1).trim();
                                result.putIfAbsent(key, stripQuotes(value));
                            }
                        });
                break; // 命中一个即停止
            } catch (IOException ignored) {
                // 读取失败则跳过，使用默认值
            }
        }
        return result;
    }

    /**
     * 去除字符串两端的引号
     *
     * @param value 待处理的字符串
     * @return 去除引号后的字符串
     */
    private static String stripQuotes(String value) {
        if (value == null || value.length() < 2) {
            return value;
        }
        if ((value.startsWith("\"") && value.endsWith("\"")) ||
                (value.startsWith("'") && value.endsWith("'"))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
