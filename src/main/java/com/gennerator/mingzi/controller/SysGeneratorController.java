package com.gennerator.mingzi.controller;


import cn.hutool.core.io.IoUtil;
import com.gennerator.mingzi.service.SysGeneratorService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys/generator")
public class SysGeneratorController {

    @Resource
    private SysGeneratorService service;

    @GetMapping("/tableList")
    public ResponseEntity<Object> tableList(Integer page,Integer pageSize){
        List<Map<String, Object>> tableList = service.tableList();
        return ResponseEntity.ok(tableList);
    }

    @PostMapping("/generatorCode")
    public void generatorCode(HttpServletResponse response,@RequestBody List<String> tableList) throws IOException {
        byte[] data = service.generatorCode(tableList);

        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"min.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IoUtil.write(response.getOutputStream(), false, data);
    }
}
