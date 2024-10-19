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
    static class Type{
        public String javaType;
        public String sqlType;
        public String tsType;
        public String javaPackage;
    }
    private static final Map<String,Type> baseType = new HashMap<>();

    static {
        baseType.put("int",new Type("Integer","INT","number",null));
        baseType.put("long",new Type("Integer","LONG","number",null));
        baseType.put("varchar",new Type("String","VARCHAR","string",null));
        baseType.put("text",new Type("String","TEXT","string",null));
        baseType.put("boolean",new Type("Boolean","TINYINT","boolean",null));
        baseType.put("tinyint",new Type("Integer","TINYINT","number",null));
        baseType.put("date",new Type("LocalDate","DATE","string","java.time.LocalDate"));
        baseType.put("datetime",new Type("LocalDateTime","DATETIME","string","java.time.LocalDateTime"));
        baseType.put("color",new Type("String","VARCHAR","string",null));
        baseType.put("object",new Type("Object","VARCHAR","any",null));
        baseType.put("decimal",new Type("BigDecimal","decimal","string","java.math.BigDecimal"));
        baseType.put("void",new Type("void","varchar","string",null));
    }

    private final static Type UNKNOWN_TYPE = new Type(
            "Unknown",
            "??",
            "unknown",
            null
    );
    public static Type parse(String type){
        if(!baseType.containsKey(type)) return UNKNOWN_TYPE;
        return baseType.get(type);
    }
}
