package com.gennerator.mingzi.utils;

//import cn.hutool.core.io.IoUtil;
//import cn.hutool.core.util.StrUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.configuration.Configuration;
//import org.apache.commons.configuration.ConfigurationException;
//import org.apache.commons.configuration.PropertiesConfiguration;
//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.lang.WordUtils;
//import org.apache.velocity.Template;
//import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.gennerator.mingzi.entity.TableEntity;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import javax.naming.ConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class GenUtils {

    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<>();
        templates.add("template/Entity.java.vm" );
        return templates;
    }

    /**
     * 生成代码
     */
    public static void generatorCode(TableEntity tableEntity, ZipOutputStream zip) {
        Configuration config = getConfig();

        Iterator<String> keys = config.getKeys();
        Map<String, Object> map = new HashMap<>();
        map.put("tableName" , tableEntity.getTableName());
        map.put("comments" , tableEntity.getComments());
        map.put("pk" , tableEntity.getPk());
        map.put("className" , tableEntity.getClassName());
        map.put("classname" , tableEntity.getClassname());
        map.put("pathName" , tableEntity.getClassname().toLowerCase());
        map.put("columns" , tableEntity.getColumns());
        map.put("package" , config.getString("package" ));
        map.put("moduleName" , config.getString("moduleName" ));
        map.put("author" , config.getString("author" ));

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

            // 3. 加载模板（路径相对于 resources 根目录）
            String templatePath = "template/Entity.java.vm"; // 不要以斜杠开头
            Template tpl = velocityEngine.getTemplate(templatePath, "UTF-8");
            tpl.merge(context, sw);

            try {
                //添加到zip
                zip.putNextEntry(new ZipEntry(getFileName(template, tableEntity.getClassName(), config.getString("package" ), config.getString("moduleName" ))));
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
            return new PropertiesConfiguration("generator.properties" );
        } catch (Exception e) {
            throw new RuntimeException("获取配置文件失败，" , e);
        }
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className, String packageName, String moduleName) {
        String packagePath = "main" + File.separator + "java" + File.separator;
        if (StrUtil.isNotBlank(packageName)) {
            packagePath += packageName.replace("." , File.separator) + File.separator + "modules" + File.separator + moduleName + File.separator;
        }

        if (template.contains("Entity.java.vm" )) {
            return packagePath + "entity" + File.separator + className + "Entity.java";
        }

        if (template.contains("Excel.java.vm" )) {
            return packagePath + "excel" + File.separator + className + "Excel.java";
        }

        if (template.contains("Dao.java.vm" )) {
            return packagePath + "dao" + File.separator + className + "Dao.java";
        }

        if (template.contains("Service.java.vm" )) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }

        if (template.contains("ServiceImpl.java.vm" )) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }

        if (template.contains("Controller.java.vm" )) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if (template.contains("Dao.xml.vm" )) {
            return "main" + File.separator + "resources" + File.separator + "mapper" + File.separator + moduleName + File.separator + className + "Dao.xml";
        }

        if (template.contains("DTO.java.vm" )) {
            return packagePath + "dto" + File.separator + className + "DTO.java";
        }

        if (template.contains("index.vue.vm" )) {
            return "vue" + File.separator + "views" + File.separator + moduleName + File.separator + className.toLowerCase() + ".vue";
        }

        if (template.contains("add-or-update.vue.vm" )) {
            return "vue" + File.separator + "views" + File.separator + moduleName + File.separator + className.toLowerCase() + "-add-or-update.vue";
        }

        if (template.contains("mysql.vm" )) {
            return className.toLowerCase() + ".mysql.sql";
        }

        if (template.contains("oracle.vm" )) {
            return className.toLowerCase() + ".oracle.sql";
        }

        if (template.contains("sqlserver.vm" )) {
            return className.toLowerCase() + ".sqlserver.sql";
        }

        if (template.contains("postgresql.vm" )) {
            return className.toLowerCase() + ".postgresql.sql";
        }

        return null;
    }
}
