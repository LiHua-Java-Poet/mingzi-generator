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
            tableEntity.setClassname(GenUtils.toLowerCamelCase(tableEntity.getTableName()));
            tableEntity.setClassName(GenUtils.toUpperCamelCase(tableEntity.getTableName()));

            Map<String, Object> primaryKeyMap = columnList.stream().filter(b -> "PRI".equals(b.get("columnKey"))).findFirst().orElse(null);
            ColumnEntity columnEntity = GenUtils.mapToColumnEntity(primaryKeyMap);
            tableEntity.setPk(columnEntity);

            List<ColumnEntity> columnEntities = columnList.stream().map(columnItem -> {
                return GenUtils.mapToColumnEntity(columnItem);
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

}
