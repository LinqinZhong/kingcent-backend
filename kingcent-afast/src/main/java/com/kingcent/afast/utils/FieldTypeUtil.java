package com.kingcent.afast.utils;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rainkyzhong
 * @date 2024/10/18 22:34
 */
public class FieldTypeUtil {

    @AllArgsConstructor
    private static class Type{
        public String javaType;
        public String sqlType;
        public String tsType;
    }
    private static final Map<String,Type> baseType = new HashMap<>();

    static {
        baseType.put("int",new Type("Integer","INT","number"));
        baseType.put("long",new Type("Integer","LONG","number"));
        baseType.put("varchar",new Type("String","VARCHAR","string"));
        baseType.put("text",new Type("String","TEXT","string"));
        baseType.put("boolean",new Type("Boolean","TINYINT","boolean"));
        baseType.put("tinyint",new Type("Integer","TINYINT","number"));
        baseType.put("date",new Type("LocalDate","DATE","string"));
        baseType.put("datetime",new Type("LocalDatetime","DATETIME","string"));
        baseType.put("color",new Type("String","VARCHAR","string"));
        baseType.put("object",new Type("Object","VARCHAR","any"));
        baseType.put("decimal",new Type("BigDecimal","decimal","string"));
        baseType.put("void",new Type("void","decimal","string"));
    }
    public static String parseJava(String type){
        if(!baseType.containsKey(type)) return type;
        return baseType.get(type).javaType;
    }

    public static String parseSql(String type){
        if(!baseType.containsKey(type)) return type;
        return baseType.get(type).sqlType;
    }
}
