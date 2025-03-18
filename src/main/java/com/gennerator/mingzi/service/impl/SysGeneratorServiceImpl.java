package com.gennerator.mingzi.service.impl;

import com.gennerator.mingzi.dao.MySQLGeneratorDao;
import com.gennerator.mingzi.entity.ColumnEntity;
import com.gennerator.mingzi.entity.TableEntity;
import com.gennerator.mingzi.service.SysGeneratorService;
import com.gennerator.mingzi.utils.GenUtils;
import jakarta.annotation.Resource;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;


@Service
public class SysGeneratorServiceImpl implements SysGeneratorService {

    @Resource
    private MySQLGeneratorDao mySQLGeneratorDao;

    @Override
    public List<Map<String, Object>> tableList() {
        List<Map<String, Object>> maps = mySQLGeneratorDao.selectAllTables();
        return maps.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("Name", item.get("Name"));
            map.put("Comment", item.get("Comment"));
            map.put("Update_time", item.get("Update_time"));
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public byte[] generatorCode(List<String> tableList) {
        List<Map<String, Object>> allTables = mySQLGeneratorDao.selectAllTables();
        List<Map<String, Object>> targetTable = allTables.stream().filter(b -> tableList.contains(b.get("Name"))).collect(Collectors.toList());

        List<TableEntity> tableEntities = targetTable.stream().map(item -> {
            TableEntity tableEntity = new TableEntity();
            Object tableName = item.get("Name");
            List<Map<String, Object>> columnList = mySQLGeneratorDao.selectTableInfo(tableName.toString());
            tableEntity.setTableName(tableName.toString());
            tableEntity.setComments(item.get("Comment").toString());
            tableEntity.setClassname(toLowerCamelCase(tableEntity.getTableName()));
            tableEntity.setClassName(toUpperCamelCase(tableEntity.getTableName()));

            Map<String, Object> primaryKeyMap = columnList.stream().filter(b -> "PRI".equals(b.get("columnKey"))).findFirst().orElse(null);
            ColumnEntity columnEntity = mapToColumnEntity(primaryKeyMap);
            tableEntity.setPk(columnEntity);
            columnList.remove(primaryKeyMap);

            List<ColumnEntity> columnEntities = columnList.stream().map(columnItem -> {
                return mapToColumnEntity(columnItem);
            }).collect(Collectors.toList());
            tableEntity.setColumns(columnEntities);

            //循环得到其他的列属性
            return tableEntity;
        }).collect(Collectors.toList());

        //拿到输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        for (TableEntity tableEntity : tableEntities) {
            GenUtils.generatorCode(tableEntity,zip);
        }
        IOUtils.closeQuietly(zip);
        return outputStream.toByteArray();
    }

    /**
     * 将 primaryKeyMap 封装为 ColumnEntity 对象
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
            case "tinyint":
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
