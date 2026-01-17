package com.gennerator.mingzi.service;

import java.util.List;
import java.util.Map;

public interface SysGeneratorService {

    List<Map<String,Object>> tableList();

    byte[] generatorCode(List<String> tableList);
}
