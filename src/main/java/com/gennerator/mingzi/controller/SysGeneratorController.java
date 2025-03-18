package com.gennerator.mingzi.controller;


import cn.hutool.core.io.IoUtil;
import com.gennerator.mingzi.service.SysGeneratorService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys/generator")
public class SysGeneratorController {

    @Resource
    private SysGeneratorService service;

    @GetMapping("/sayHi")
    public ResponseEntity<Object> sayHi(@RequestParam(value = "name")String name){
        List<Map<String, Object>> demo = service.tableList();
        return ResponseEntity.ok(demo);
    }

    @GetMapping("/generatorCode")
    public void generatorCode(HttpServletResponse response) throws IOException {
        byte[] data = service.generatorCode(Arrays.asList("plan", "task"));

        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"min.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IoUtil.write(response.getOutputStream(), false, data);
    }
}
