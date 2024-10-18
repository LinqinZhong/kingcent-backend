package com.kingcent.afast.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kingcent.afast.entity.ProjectDaoEntity;

import com.kingcent.afast.entity.ProjectDaoFuncEntity;
import com.kingcent.afast.entity.ProjectEntityEntity;
import com.kingcent.afast.entity.ProjectServiceFuncEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author rainkyzhong
 * @date 2024/10/18 21:05
 */
public class ProjectDaoUtil {
    public static String generateJava(
            String packageName,
            ProjectEntityEntity entityEntity,
            ProjectDaoEntity dao,
            List<ProjectDaoFuncEntity> functions,
            boolean useMybatis,
            boolean useMyBatisPlus,
            int javaVersion
    ){
        //导入
        Set<String> imports = new HashSet<>();
        //类名
        String name = dao.getName();
        //类注解
        List<String> classAnnotation = new ArrayList<>();
        //类描述
        String description = dao.getDescription();
        //继承
        String extendsStr = "";
        //实体类名
        String entityName = ProjectEntityUtil.formatEntityName(entityEntity.getName());
        if (useMyBatisPlus){
            extendsStr = " extends BaseMapper<"+entityName+">";
            imports.add("import com.baomidou.mybatisplus.core.mapper.BaseMapper;");
        }

        if(useMybatis){
            imports.add("import org.apache.ibatis.annotations.Mapper;");
            classAnnotation.add("@Mapper");
        }

        List<String> functionList = new ArrayList<>();
        for (ProjectDaoFuncEntity function : functions) {
            List<String> paramList = new ArrayList<>();
            List<String> commentList = new ArrayList<>();
            List<String> funcAnnotations = new ArrayList<>();
            String funcName = function.getName();
            String funcDesc = function.getDescription();
            String returnParam = FieldTypeUtil.parseJava(function.getReturnParam());
            String params = function.getParams();

            //Mybatis注解生成
            if(useMybatis && function.getSourceType() != null && function.getSourceType().equals(1)){

                if(function.getExecutionType() == null){
                    throw new RuntimeException("操作类型为空");
                }
                if (function.getExecution() == null){
                    throw new RuntimeException("操作语句为空");
                }

                String annotationName;
                if(function.getExecutionType().equals(0)){
                    //增
                    annotationName = "@Insert";
                    imports.add("import org.apache.ibatis.annotations.Insert;");
                }else if(function.getExecutionType().equals(1)){
                    //删
                    annotationName = "@Delete";
                    imports.add("import org.apache.ibatis.annotations.Delete;");
                }else if(function.getExecutionType().equals(2)){
                    //改
                    annotationName = "@Update";
                    imports.add("import org.apache.ibatis.annotations.Update;");
                }else{
                    //查
                    annotationName = "@Select";
                    imports.add("import org.apache.ibatis.annotations.Select;");
                }
                String execution = function.getExecution();
                execution = execution.replace("[TABLE_NAME]",entityEntity.getTableName());
                if(execution.contains("\n")){
                    //美化代码
                    if(javaVersion >= 13){
                        //Java13以后可以使用文本块
                        execution = execution.replace("\n","\n\t\t");
                        execution = execution.replace(" \n","\\040\n");
                        funcAnnotations.add("\t"+annotationName+"(\"\"\"\n\t\t"+execution+"\n\t\"\"\")");
                    }else{
                        execution = execution.replace("\"","\\\"");
                        execution = execution.replace("\n","\"+\n\t\t\t\"");
                        funcAnnotations.add("\t"+annotationName+"(\""+execution+"\"\n\t)");
                    }
                }else{
                    execution = execution.replace("\"","\\\"");
                    funcAnnotations.add("\t"+annotationName+"(\""+execution+"\")");
                }
            }

            if(funcDesc != null && funcDesc.trim().length() > 0){
                commentList.add("\t * "+funcDesc);
            }
            if (params != null) {
                List<JSONObject> ps = JSON.parseArray(params, JSONObject.class);
                for (JSONObject p : ps) {
                    String paramName = p.getString("name");
                    String paramType = FieldTypeUtil.parseJava(p.getString("type"));
                    checkTypeImport(paramType,imports);
                    String paramDesc = p.getString("description");
                    paramList.add(paramType+" "+paramName);
                    if(paramDesc != null && paramDesc.trim().length() > 0){
                        commentList.add("\t * @param "+paramName+" "+paramDesc);
                    }
                }
            }

            String paramsStr = String.join(",", paramList);
            String commentStr;

            if(commentList.size() > 0){
                commentStr = "\n\t/**\n"+String.join("\n",commentList)+"\n\t */";
            }else{
                commentStr = "";
            }
            String funcAnnotationStr;
            if(funcAnnotations.size() > 0){
                funcAnnotationStr = "\n"+String.join("\n",funcAnnotations);
            }else{
                funcAnnotationStr = "";
            }
            functionList.add(commentStr+funcAnnotationStr+"\n\t"+returnParam+" "+funcName+"("+paramsStr+");");
        }
        String importStr = String.join("\n",imports);
        String funcStr = String.join("\n",functionList);
        String annotationStr = String.join("\n",classAnnotation);
        return "package "+packageName+";\n\n"+importStr+"\n\n"+annotationStr+"\n"+"public interface "+name+extendsStr+" {\n"+ funcStr+"\n"+"}";
    }

    /**
     * 检查类型导入
     * @param paramType java类型
     * @param imports 导入记录
     */
    private static void checkTypeImport(String paramType, Set<String> imports) {
        if(paramType.equals("BigDecimal")){
            imports.add("import java.math.BigDecimal;");
        }
    }
}
