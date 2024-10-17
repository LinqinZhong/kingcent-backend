package com.kingcent.afast.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.kingcent.afast.entity.ProjectEntityEntity;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2024/10/17 23:33
 */
public class ProjectEntityUtil {
    public static String generate(
            String packageName,
            ProjectEntityEntity entityEntity,
            boolean useLombok,
            boolean useMybatisPlus
    ){
        String className = entityEntity.getName();
        String authorName = "小明";
        String description = entityEntity.getDescription();
        List<String> fields = new ArrayList<>();
        String value = entityEntity.getValue();
        String date = entityEntity.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<JSONObject> list = JSON.parseArray(value, JSONObject.class);
        String tableName = useMybatisPlus && entityEntity.getTableName() != null ? "@TableName(\""+ entityEntity.getTableName()+"\")\n" : "";
        for (JSONObject jsonObject : list) {
            String name = jsonObject.getString("name");
            String type = jsonObject.getString("type");
            fields.add("    private "+type+" "+name+";");
        }
        return "package "+packageName+";\n" +
                "\n" +
                "import lombok.Data;\n" +
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
