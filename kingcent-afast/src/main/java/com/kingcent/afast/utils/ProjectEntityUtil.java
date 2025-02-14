package com.kingcent.afast.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kingcent.afast.entity.ProjectEntityEntity;
import com.kingcent.common.result.Result;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2024/10/17 23:33
 */
public class ProjectEntityUtil {


    public static String formatEntityName(String name){
        return name;
    }

    //获取字段类型对应sql的类型
    public static String generateSql(ProjectEntityEntity entity){
        List<String> fields = new ArrayList<>();
        String value = entity.getValue();
        try{
            List<Map<String,Object>> data = JSONObject.parseObject(value,List.class);
            for (Map<String, Object> field : data) {
                if((boolean) field.getOrDefault("isTableField", false)){
                    String name = (String) field.get("name");
                    String type = FieldTypeUtil.parse((String) field.get("type")).sqlType;
                    if(name == null || type == null){
                        throw new RuntimeException("字段内容错误");
                    }
                    String nullStr = ((boolean) field.getOrDefault("isNull",false)) ? " NULL " : "";
                    String description = (String) field.getOrDefault("description", "");
                    String comment =  description.trim().length() > 0 ? " COMMENT '"+field.getOrDefault("description","")+"'" : "";
                    fields.add("\t`"+ StringUtils.camelToUnderline(name) +"` "+nullStr+type+comment);
                }
            }
        }catch (Exception e){
            throw new RuntimeException("实体内容错误");
        }
        return "/*\n"+entity.getDescription()+"\n*/\nCREATE TABLE `"+entity.getTableName()+"`(\n" +
                String.join(",\n",fields)+
                "\n)";
    }

    public static String generate(
            String packageName,
            ProjectEntityEntity entityEntity,
            boolean useLombok,
            boolean useMybatisPlus
    ){
        List<String> imports = new ArrayList<>();
        String className = entityEntity.getName();
        String authorName = "小明";
        String description = entityEntity.getDescription();
        List<String> fields = new ArrayList<>();
        String value = entityEntity.getValue();
        String date = entityEntity.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<JSONObject> list = JSON.parseArray(value, JSONObject.class);
        String tableName = "";

        if(useLombok){
            imports.add("import lombok.Data;");
        }

        if(useMybatisPlus && entityEntity.getTableName() != null){
            imports.add("import com.baomidou.mybatisplus.annotation.TableName;");
            tableName = "@TableName(\""+ entityEntity.getTableName()+"\")\n";
        }

        for (JSONObject jsonObject : list) {
            String desc = (String) jsonObject.getOrDefault("description","");
            String comment = desc.trim().length() > 0 ? "\t// "+desc : "";
            String name = jsonObject.getString("name");
            FieldTypeUtil.Type typeInfo = FieldTypeUtil.parse(jsonObject.getString("type"));
            String type = typeInfo.javaType;
            if(typeInfo.javaPackage != null){
                imports.add("import "+typeInfo.javaPackage+";");
            }
            fields.add("    private "+type+" "+name+";"+comment);
        }
        String importStr = imports.size() > 0
                ? String.join("\n", imports)
                : "";
        return "package "+packageName+".afast.entity;\n" +
                "\n" +
                importStr+"\n" +
                "\n" +
                "/**\n" +
                " * "+description+"\n" +
                " * @author "+authorName+"\n" +
                " * @date "+date+"\n" +
                " */\n" +
                "@Data\n" +
                tableName+
                "public class "+className+" {\n" +
                String.join("\n",fields)+
                "\n}\n";
    }
}
