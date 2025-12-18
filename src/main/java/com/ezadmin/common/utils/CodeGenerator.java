package com.ezadmin.common.utils;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.ibatis.type.JdbcType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class CodeGenerator {

    // 从.env文件读取配置
    private static final Dotenv dotenv = Dotenv.configure()
        .directory("src/main/resources")
        .load();

    private static final String DB_HOST = dotenv.get("DB_HOST", "localhost");
    private static final String DB_PORT = dotenv.get("DB_PORT", "3306");
    private static final String DB_NAME = dotenv.get("DB_NAME", "ez_admin");
    private static final String USERNAME = dotenv.get("DB_USERNAME", "root");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD", "");
    private static final String DB_URL = String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai&tinyInt1isBit=false",
        DB_HOST, DB_PORT, DB_NAME);

    public static void main(String[] args) {

        AtomicReference<String> loadModulePackageName = new AtomicReference<>("");
        // 获取当前路径
        String path = System.getProperty("user.dir");
        // &tinyInt1isBit=false
        FastAutoGenerator.create(DB_URL, USERNAME, PASSWORD)
            .globalConfig((scanner, builder) -> {
                // 设置作者
                builder.author("shenyang")
                    .disableOpenDir()
                    .enableSpringdoc() // 如果想用swagger，生成 springdoc 规范注解 把这个放开
                    // 指定输出目录
                    .outputDir(path + "\\src\\test\\java");
            })
            .dataSourceConfig(builder ->
                builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    if (JdbcType.TINYINT == metaInfo.getJdbcType()) {
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);
                })
            )
            .packageConfig((scanner, builder) -> {
                loadModulePackageName.set(scanner.apply("请输入当前父包模块名称："));
                builder.parent("com.ezadmin.modules") // 设置父包名
                    .moduleName(loadModulePackageName.get()) // 设置父包模块名
                    .service("service")
                    .serviceImpl("service.impl")
                    .mapper("mapper")
                    .entity("entity")
                    // 设置mapperXml生成路径
                    .pathInfo(Collections.singletonMap(OutputFile.xml, path + "\\src\\main\\resources\\mapper\\" + loadModulePackageName.get()));
            })
            .strategyConfig((scanner, builder) -> {
                    // 设置需要生成的表名
                    builder.addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all")))
                        .addTablePrefix("ez_admin_") // 设置过滤表前缀
                        .entityBuilder() // 设置 entity 生成规则
                        .addTableFills(new Column("create_time", FieldFill.INSERT))
                        .addTableFills(new Column("create_by", FieldFill.INSERT))
                        .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE))
                        .addTableFills(new Column("update_by", FieldFill.INSERT_UPDATE))
                        .logicDeleteColumnName("is_deleted")
                        .idType(IdType.ASSIGN_ID)
                        .enableLombok()// lombok 注解
                        .controllerBuilder()// 设置 controller 生成规则
                        .disable()  // 禁用生成@RestController 控制器
                        .build();
                }
            )
            .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
            .execute();
    }

    /**
     * 处理 all 情况
     */
    protected static List<String> getTables(String tables) {
        return "all".equalsIgnoreCase(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }
}
