package com.kingcent.afast.utils;

import com.alibaba.nacos.common.utils.MD5Utils;
import com.kingcent.afast.entity.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2024/10/19 16:32
 */
public class ProjectUtil {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static class BuildProjectException extends Exception{
        public BuildProjectException(String message){
            super(message);
        }
    }

    public static File buildProjectDir(ProjectEntity project) throws BuildProjectException {
        File projectDir = new File("C://test/"+project.getName());
        if(!projectDir.exists() && !projectDir.mkdirs()) throw new BuildProjectException("工程目录构建失败");
        return projectDir;
    }

    public static File buildSrcDir(File projectDir) throws BuildProjectException {
        File javaDir = new File(projectDir,"src");
        if(!javaDir.exists() && !javaDir.mkdirs()) throw new BuildProjectException("资源目录创建失败");
        return javaDir;
    }

    public static File buildCoreDir(File srcDir, String packageName) throws BuildProjectException {
        File javaDir = new File(srcDir,"main/java/"+packageName.replace(".","/")+"/afast");
        if(!javaDir.exists() && !javaDir.mkdirs()) throw new BuildProjectException("代码目录创建失败");
        return javaDir;
    }

    public static File buildEntityDir(File javaDir) throws BuildProjectException {
        File entityDir = new File(javaDir,"entity");
        if(!entityDir.exists() && !entityDir.mkdirs()) throw new BuildProjectException("实体目录创建失败");
        return entityDir;
    }

    public static File buildDaoDir(File javaDir) throws BuildProjectException {
        File daoDir = new File(javaDir,"dao");
        if(!daoDir.exists() && !daoDir.mkdirs()) throw new BuildProjectException("数据层目录创建失败");
        return daoDir;
    }

    public static void buildEntityFiles(File entityDir,String packageName, List<ProjectEntityEntity> entityList) throws BuildProjectException {
        for (ProjectEntityEntity entityEntity : entityList) {

            String javaFileName = ProjectEntityUtil.formatEntityName(entityEntity.getName())+".java";
            File javaFile = new File(entityDir,javaFileName);
            try {
                if(!javaFile.exists() && !javaFile.createNewFile()){
                    throw new BuildProjectException("创建失败");
                }

                String code = ProjectEntityUtil.generate(
                        packageName,
                        entityEntity,
                        true,
                        true
                );
                String sign = MD5Utils.md5Hex(code.getBytes());
                code = getHeadCommentOfJava(sign)+"\n\n"+code;
                FileWriter fileWriter = new FileWriter(javaFile);
                fileWriter.write(code);
                fileWriter.close();
            } catch (IOException e) {
                throw new BuildProjectException("创建失败");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void buildPom(File srcDir, ProjectEntity project, List<ProjectMvnDepEntity> dependencyList) throws BuildProjectException {

        File pomFile = new File(srcDir,"pom.xml");
        try {
            if(!pomFile.exists() && !pomFile.createNewFile()){
                throw new BuildProjectException("POM文件创建失败");
            }
            List<String> dependencies = new ArrayList<>();
            for (ProjectMvnDepEntity dep : dependencyList) {
                String scopeStr = dep.getScope() != null && !dep.getScope().equals("default")
                        ? "           <scope>"+dep.getScope()+"</scope>\n"
                        : "";
                dependencies.add(
                        "        <dependency>\n" +
                                "           <groupId>"+dep.getGroupId()+"</groupId>\n"+
                                "           <artifactId>"+dep.getArtifactId()+"</artifactId>\n"+
                                "           <version>"+dep.getVersion()+"</version>\n"+
                                scopeStr+
                                "        </dependency>"
                );
            }
            String dependenciesStr = dependencies.size() > 0 ?
                    "    <dependencies>\n" +String.join("\n",dependencies)+ "\n    </dependencies>\n"
                    : "";
            String code = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                    "    <modelVersion>4.0.0</modelVersion>\n" +
                    "    <artifactId>"+project.getName()+"</artifactId>\n" +
                    dependenciesStr+
                    "    <build>\n" +
                    "        <plugins>\n" +
                    "            <plugin>\n" +
                    "                <groupId>org.apache.maven.plugins</groupId>\n" +
                    "                <artifactId>maven-compiler-plugin</artifactId>\n" +
                    "                <configuration>\n" +
                    "                    <source>17</source>\n" +
                    "                    <target>17</target>\n" +
                    "                </configuration>\n" +
                    "            </plugin>\n" +
                    "        </plugins>\n" +
                    "    </build>"+
                    "    <groupId>"+project.getPackageName()+"</groupId>\n" +
                    "    <version>1.0</version>\n" +
                    "    <name>"+project.getName()+"</name>\n" +
                    "    <url>http://maven.apache.org</url>\n" +
                    "</project>";
            FileWriter fileWriter = new FileWriter(pomFile);
            fileWriter.write(code);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void buildDaoFiles(
            File daoDir,
            String packageName,
            List<ProjectDaoEntity> daoList,
            Map<Long,ProjectEntityEntity> entityMap,
            Map<Long, List<ProjectDaoFuncEntity>> daoFuncMap
    ) throws BuildProjectException {
        for (ProjectDaoEntity daoEntity : daoList) {
            String javaFileName = ProjectDaoUtil.formatDaoName(daoEntity.getName())+".java";
            File javaFile = new File(daoDir,javaFileName);
            try {
                if(!javaFile.exists() && !javaFile.createNewFile()){
                    throw new BuildProjectException("文件创建失败");
                }
                ProjectEntityEntity entityEntity = entityMap.get(daoEntity.getEntityId());
                if(entityEntity == null) throw new BuildProjectException("引用了一个不存在的实体");
                String code = ProjectDaoUtil.generateJava(
                        packageName,
                        entityEntity,
                        daoEntity,
                        daoFuncMap.get(daoEntity.getId()),
                        true,
                        true,
                        17
                );
                String sign = MD5Utils.md5Hex(code.getBytes());
                code = getHeadCommentOfJava(sign)+"\n\n"+code;
                FileWriter fileWriter = new FileWriter(javaFile);
                fileWriter.write(code);
                fileWriter.close();
            } catch (IOException | NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String getHeadCommentOfJava(String sign){
        String generateTime = LocalDateTime.now().format(dateTimeFormatter);
        return String.format("""
                /**
                 * 本文件由Afast代码生成器自动生成，请勿修改，以免造成不良影响。
                 * This file is generated by Afast，keep it unmodified for avoiding side effects.
                 * 自/marked by：rainkyzhong
                 * 生成于/generated at %s
                 * signature: %s
                 */
                """,
                generateTime,
                sign
        );
    }
}
