package com.gennerator.mingzi.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MySQLGeneratorDao {

    List<Map<String, Object>> selectAllTables();

    List<Map<String, Object>> selectTable(@Param("tableName") String tableName);

    List<Map<String, Object>> selectTableInfo(@Param("tableName") String tableName);

}
