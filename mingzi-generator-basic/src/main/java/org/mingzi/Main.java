package org.mingzi;

import java.io.File;

import static org.mingzi.generator.StaticGenerator.copyFilesByHutool;

public class Main {

    public static void main(String[] args) {
        //获取到整个项目的根路径
        String projectPath = System.getProperty("user.dir");
        File file = new File(projectPath);

        String inputPath = new File(file, "mingzi-generator-demo-projects/acm-template").getAbsolutePath();

        String outputPath = projectPath;

        copyFilesByHutool(inputPath, outputPath);
    }
}
