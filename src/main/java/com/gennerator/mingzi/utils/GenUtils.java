package com.gennerator.mingzi.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.gennerator.mingzi.entity.ColumnEntity;
import org.apache.commons.configuration.Configuration;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import com.gennerator.mingzi.entity.TableEntity;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GenUtils {

    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<>();
        templates.add("template/Entity.java.vm");
        templates.add("template/InfoTo.java.vm");
        templates.add("template/ListTo.java.vm");
        templates.add("template/SaveVo.java.vm");
        templates.add("template/UpdateVo.java.vm");
        templates.add("template/Service.java.vm");
        templates.add("template/ServiceImpl.java.vm");
        templates.add("template/Dao.java.vm");
        templates.add("template/Controller.java.vm");
        return templates;
    }

    /**
     * 生成代码
     */
    public static void generatorCode(TableEntity tableEntity, ZipOutputStream zip) {
        Configuration config = getConfig();

        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tableEntity.getTableName());
        map.put("comments", tableEntity.getComments());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getClassName());
        map.put("classname", tableEntity.getClassname());
        map.put("pathName", tableEntity.getClassname().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("package", config.getString("package"));
        map.put("moduleName", config.getString("moduleName"));
        map.put("author", config.getString("author"));

        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = getTemplates();
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            // 1. 初始化 Velocity 引擎
            VelocityEngine velocityEngine = new VelocityEngine();

            // 关键配置：指定从类路径加载模板
            velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

            // 2. 初始化引擎（必须调用 init()）
            velocityEngine.init();

            Template tpl = velocityEngine.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);

            try {
                String fileName = getFileName(template, tableEntity.getClassName(), config.getString("package"), config.getString("moduleName"), tableEntity);
                //添加到zip
                zip.putNextEntry(new ZipEntry(fileName));
                IoUtil.writeUtf8(zip, false, sw.toString());
                zip.flush();
                IoUtil.close(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new RuntimeException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
            }
        }

    }

    /**
     * 获取配置信息
     */
    public static Configuration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties");
        } catch (Exception e) {
            throw new RuntimeException("获取配置文件失败，", e);
        }
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className, String packageName, String moduleName, TableEntity tableEntity) {
        String packagePath = "main" + File.separator + "java" + File.separator;
        if (StrUtil.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator;
        }

        if (template.contains("Entity.java.vm")) {
            return packagePath + "model" + File.separator + "entity" + File.separator + className + "Entity.java";
        }

        if (template.contains("Controller.java.vm")) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if (template.contains("Dao.java.vm")) {
            return packagePath + "dao" + File.separator + className + "Dao.java";
        }

        if (template.contains("Service.java.vm")) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }

        if (template.contains("ServiceImpl.java.vm")) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }

        if (template.contains("InfoTo.java.vm")) {
            return packagePath + "model" + File.separator + "to" + File.separator + tableEntity.getClassname() + File.separator + className + "InfoTo.java";
        }

        if (template.contains("ListTo.java.vm")) {
            return packagePath + "model" + File.separator + "to" + File.separator + tableEntity.getClassname() + File.separator + className + "ListTo.java";
        }

        if (template.contains("SaveVo.java.vm")) {
            return packagePath + "model" + File.separator + "vo" + File.separator + tableEntity.getClassname() + File.separator + className + "SaveVo.java";
        }

        if (template.contains("UpdateVo.java.vm")) {
            return packagePath + "model" + File.separator + "vo" + File.separator + tableEntity.getClassname() + File.separator + className + "UpdateVo.java";
        }

        return null;
    }

    /**
     * 将 primaryKeyMap 封装为 ColumnEntity 对象
     *
     * @param primaryKeyMap 表属性的 Map
     * @return 封装好的 ColumnEntity 对象
     */
    public static ColumnEntity mapToColumnEntity(Map<String, Object> primaryKeyMap) {
        ColumnEntity columnEntity = new ColumnEntity();

        // 设置列名
        if (primaryKeyMap.containsKey("columnName")) {
            columnEntity.setColumnName((String) primaryKeyMap.get("columnName"));
        }

        // 设置列名类型
        if (primaryKeyMap.containsKey("dataType")) {
            columnEntity.setDataType((String) primaryKeyMap.get("dataType"));
        }

        // 设置列名备注
        if (primaryKeyMap.containsKey("columnComment")) {
            columnEntity.setComments((String) primaryKeyMap.get("columnComment"));
        }

        // 设置属性名称（第一个字母大写）
        if (primaryKeyMap.containsKey("columnName")) {
            String columnName = (String) primaryKeyMap.get("columnName");
            String attrName = toUpperCamelCase(columnName); // 转换为大驼峰
            columnEntity.setAttrName(attrName);
        }

        // 设置属性名称（第一个字母小写）
        if (primaryKeyMap.containsKey("columnName")) {
            String columnName = (String) primaryKeyMap.get("columnName");
            String attrname = toLowerCamelCase(columnName); // 转换为小驼峰
            columnEntity.setAttrNameSmall(attrname);
        }

        // 设置属性类型
        if (primaryKeyMap.containsKey("dataType")) {
            String dataType = (String) primaryKeyMap.get("dataType");
            String attrType = convertDataTypeToJavaType(dataType); // 转换为 Java 类型
            columnEntity.setAttrType(attrType);
        }

        // 设置 auto_increment
        if (primaryKeyMap.containsKey("extra")) {
            columnEntity.setExtra((String) primaryKeyMap.get("extra"));
        }

        return columnEntity;
    }

    /**
     * 将数据库字段类型转换为 Java 类型
     *
     * @param dataType 数据库字段类型
     * @return Java 类型
     */
    private static String convertDataTypeToJavaType(String dataType) {
        if (dataType == null || dataType.isEmpty()) {
            return "String"; // 默认返回 String
        }
        switch (dataType.toLowerCase()) {
            case "int":
            case "integer":
            case "tinyint":
                return "Integer";
            case "bigint":
                return "Long";
            case "varchar":
            case "text":
            case "char":
                return "String";
            case "datetime":
            case "timestamp":
                return "Date";
            case "decimal":
            case "numeric":
                return "BigDecimal";
            case "boolean":
                return "Boolean";
            case "float":
                return "Float";
            case "double":
                return "Double";
            default:
                return "String"; // 默认返回 String
        }
    }

    /**
     * 将表名转换为大驼峰格式
     *
     * @param tableName 表名（如：user_info）
     * @return 大驼峰格式的字符串（如：UserInfo）
     */
    public static String toUpperCamelCase(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            return tableName;
        }

        // 将表名按分隔符（如下划线）拆分为单词数组
        String[] words = tableName.split("_");
        StringBuilder result = new StringBuilder();

        // 每个单词首字母大写
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase());
            }
        }

        return result.toString();
    }

    /**
     * 将表名转换为小驼峰格式
     *
     * @param tableName 表名（如：user_info）
     * @return 小驼峰格式的字符串（如：userInfo）
     */
    public static String toLowerCamelCase(String tableName) {
        if (tableName == null || tableName.isEmpty()) {
            return tableName;
        }

        // 将表名按分隔符（如下划线）拆分为单词数组
        String[] words = tableName.split("_");
        StringBuilder result = new StringBuilder();

        // 第一个单词全小写
        result.append(words[0].toLowerCase());

        // 后续单词首字母大写
        for (int i = 1; i < words.length; i++) {
            if (!words[i].isEmpty()) {
                result.append(words[i].substring(0, 1).toUpperCase())
                        .append(words[i].substring(1).toLowerCase());
            }
        }

        return result.toString();
    }
}
