package com.kingcent.plant.service.impl;

import com.kingcent.plant.service.AskService;
import org.neo4j.driver.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.neo4j.driver.*;
import java.util.*;

@Service
public class AskServiceImpl implements AskService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Driver neo4jDriver;

    @Override
    public String ask(String text) {
        // 调用外部接口获取意图和槽位信息
        Map<String, Object> apiResponse = getApiResponse(text);
        String intent = (String) apiResponse.get("intent");
        Map<String, List<String>> slots = (Map<String, List<String>>) apiResponse.get("slots");

        // 根据意图执行不同的查询逻辑
        switch (intent) {
            case "Q_VAR":
                return getVarietyInfo(slots.get("variety"));
            case "Q_VAR_HAB":
                return getVarietyHabit(slots.get("variety"));
            case "Q_VAR_DST":
                return getVarietyOrigin(slots.get("variety"));
            case "Q_DIS":
                return getDiseaseInfo(slots.get("disease"));
            case "Q_DIS_SYM":
                return getDiseaseSymptoms(slots.get("disease"));
            case "Q_DIS_PES":
                return getPesticidesForDisease(slots.get("disease"));
            case "Q_DIS_PRE":
                return getDiseasePrevention(slots.get("disease"));
            case "Q_FET":
                return getFertilizerInfo(slots.get("fertilizer"));
            case "Q_FET_ELE":
                return getFertilizerElements(slots.get("fertilizer"));
            case "Q_FET_USE":
                return getFertilizerUsage(slots.get("fertilizer"));
            case "Q_FET_PRI":
                return getFertilizerPrice(slots.get("fertilizer"));
            case "Q_PES":
                return getPesticideInfo(slots.get("pesticide"));
            case "Q_PES_DIS":
                return getDiseasesForPesticide(slots.get("pesticide"));
            case "Q_PES_USE":
                return getPesticideUsage(slots.get("pesticide"));
            case "IF_PES_DIS":
                return checkPesticideEffectiveness(slots.get("pesticide"), slots.get("disease"));
            case "IF_VAR_DIS":
                return checkVarietySusceptibility(slots.get("variety"), slots.get("disease"));
            default:
                return "抱歉，我不理解您的问题。";
        }
    }

    // 调用API获取意图和槽位信息
    private Map<String, Object> getApiResponse(String text) {
        String apiUrl = "http://localhost:9999/ask?text=" + text;
        ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);

        Map body = response.getBody();
        Map data = (Map) body.get("data");

        return Map.of(
                "intent", data.get("intent"),
                "slots", data.getOrDefault("slots", Collections.emptyMap())
        );
    }

    // 查询陈皮柑品种信息
    private String getVarietyInfo(List<String> varietyName) {
        System.out.println(varietyName);
        if (varietyName == null) return "请指定陈皮柑品种";
        try (Session session = neo4jDriver.session()) {

            Result result = session.run(
                    "MATCH (v:Variety {name: $name}) " +
                            "OPTIONAL MATCH (v)-[:HAS_HABIT]->(h:Habit) " +
                            "OPTIONAL MATCH (c:city)-[:CAN_GROW]->(v) " +
                            "RETURN v.name as name, v.oil_content as oil_content, v.sugar_content as sugar_content, v.aroma as aroma, v.quality as quality, v.peel_thickness as peel_thickness, v.description AS description, h.name AS habit, c.name AS origin",
                    Values.parameters("name", varietyName.get(0))
            );

            List<Record> list = result.list();
            List<String> info = new ArrayList<>();
            info.add("你问我【"+varietyName.get(0)+"】这个品种的相关信息");
            if(list.size() > 0){
                for (Record record : list) {

                    if (!record.get("name").isNull()) {
                        info.add("名称："+record.get("name")+"");
                    }
                    if (!record.get("description").isNull()) {
                        info.add("介绍："+record.get("description")+"");
                    }
                    if (!record.get("quality").isNull()) {
                        info.add("含糖量："+record.get("sugar_content")+"");
                    }
                    if (!record.get("quality").isNull()) {
                        info.add("等级："+record.get("quality")+"");
                    }

                    if (!record.get("aroma").isNull()) {
                        info.add("味道："+record.get("aroma")+"");
                    }

                    if (!record.get("peel_thickness").isNull()) {
                        info.add("皮厚度："+record.get("peel_thickness")+"");
                    }

                    if (!record.get("aroma").isNull()) {
                        info.add("味道："+record.get("aroma")+"");
                    }

                    if (!record.get("oil_content").isNull()) {
                        info.add("含油量："+record.get("oil_content")+"");
                    }
                    if (!record.get("habit").isNull()) {
                        info.add("习性："+record.get("habit")+"");
                    }

                    if (!record.get("origin").isNull()) {
                        info.add("产地："+record.get("origin")+"");
                    }
                }
            }else {
                info.add("未找到该品种信息");
            }
            return String.join(",", info);
        }
    }

    // 查询品种习性
    private String getVarietyHabit(List<String> varietyName) {
        if (varietyName == null) return "请指定陈皮柑品种";

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (v:Variety {name: $name})-[:HAS_HABIT]->(h:Habit) " +
                            "RETURN h.name AS habit",
                    Values.parameters("name", varietyName)
            );

            if (result.hasNext()) {
                return result.single().get("habit").asString();
            }
        }
        return "未找到该品种的习性信息";
    }

    // 查询品种产地
    private String getVarietyOrigin(List<String> varietyName) {
        if (varietyName == null) return "请指定陈皮柑品种";

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (c:City)-[:CAN_GROW]->(v:Variety {name: $name}) " +
                            "RETURN c.name AS origin",
                    Values.parameters("name", varietyName)
            );

            if (result.hasNext()) {
                return "主要产自：" + result.single().get("origin").asString();
            }
        }
        return "未找到该品种的产地信息";
    }

    // 查询病虫害信息
    private String getDiseaseInfo(List<String> diseaseName) {
        if (diseaseName == null) return "请指定病虫害名称";

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (d:Disease {name: $name}) " +
                            "OPTIONAL MATCH (d)-[:HAS_SYMPTOM]->(s:Symptom) " +
                            "OPTIONAL MATCH (p:Pesticide)-[:CONTROLS]->(d) " +
                            "RETURN d.description AS description, " +
                            "collect(s.content) AS symptoms, " +
                            "collect(p.name) AS pesticides",
                    Values.parameters("name", diseaseName)
            );

            if (result.hasNext()) {
                Record record = result.single();
                StringBuilder sb = new StringBuilder();
                sb.append(record.get("description").asString());

                List<String> symptoms = record.get("symptoms").asList(v -> v.asString());
                if (!symptoms.isEmpty()) {
                    sb.append("\n症状：").append(String.join("、", symptoms));
                }

                List<String> pesticides = record.get("pesticides").asList(v -> v.asString());
                if (!pesticides.isEmpty()) {
                    sb.append("\n推荐农药：").append(String.join("、", pesticides));
                }

                return sb.toString();
            }
        }
        return "未找到该病虫害信息";
    }

    // 查询病虫害症状
    private String getDiseaseSymptoms(List<String> diseaseName) {
        if (diseaseName == null) return "请指定病虫害名称";

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (d:Disease {name: $name})-[:HAS_SYMPTOM]->(s:Symptom) " +
                            "RETURN collect(s.content) AS symptoms",
                    Values.parameters("name", diseaseName)
            );

            if (result.hasNext()) {
                List<String> symptoms = result.single().get("symptoms").asList(v -> v.asString());
                if (!symptoms.isEmpty()) {
                    return String.join("、", symptoms);
                }
            }
        }
        return "未找到该病虫害的症状信息";
    }

    // 查询治疗病虫害的农药
    private String getPesticidesForDisease(List<String> diseaseName) {
        if (diseaseName == null) return "请指定病虫害名称";

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (p:Pesticide)-[:CONTROLS]->(d:Disease {name: $name}) " +
                            "RETURN collect(p.name) AS pesticides",
                    Values.parameters("name", diseaseName)
            );

            if (result.hasNext()) {
                List<String> pesticides = result.single().get("pesticides").asList(v -> v.asString());
                if (!pesticides.isEmpty()) {
                    return "可使用以下农药治疗：" + String.join("、", pesticides);
                }
            }
        }
        return "未找到适合的农药信息";
    }

    // 查询病虫害预防方法
    private String getDiseasePrevention(List<String> diseaseName) {
        if (diseaseName == null) return "请指定病虫害名称";

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (d:Disease {name: $name})-[:HAS_PREVENTION]->(p:DiseasePrevent) " +
                            "RETURN p.content AS prevention",
                    Values.parameters("name", diseaseName)
            );

            if (result.hasNext()) {
                return result.single().get("prevention").asString();
            }
        }
        return "未找到该病虫害的预防方法";
    }

    // 查询化肥信息
    private String getFertilizerInfo(List<String> fertilizerName) {
        if (fertilizerName == null) return "请指定化肥名称";

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (f:Fertilizer {name: $name}) " +
                            "OPTIONAL MATCH (f)-[:HAS_ELEMENT]->(e:Element) " +
                            "OPTIONAL MATCH (f)-[:USED_FOR]->(u:Using) " +
                            "RETURN f.description AS description, " +
                            "collect(e.name) AS elements, " +
                            "collect(u.content) AS usage",
                    Values.parameters("name", fertilizerName)
            );

            if (result.hasNext()) {
                Record record = result.single();
                StringBuilder sb = new StringBuilder();
                sb.append(record.get("description").asString());

                List<String> elements = record.get("elements").asList(v -> v.asString());
                if (!elements.isEmpty()) {
                    sb.append("\n主要成分：").append(String.join("、", elements));
                }

                List<String> usage = record.get("usage").asList(v -> v.asString());
                if (!usage.isEmpty()) {
                    sb.append("\n适用场景：").append(String.join("、", usage));
                }

                return sb.toString();
            }
        }
        return "未找到该化肥信息";
    }

    // 查询化肥成分
    private String getFertilizerElements(List<String> fertilizerName) {
        if (fertilizerName == null) return "请指定化肥名称";

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (f:Fertilizer {name: $name})-[:HAS_ELEMENT]->(e:Element) " +
                            "RETURN collect(e.name) AS elements",
                    Values.parameters("name", fertilizerName)
            );

            if (result.hasNext()) {
                List<String> elements = result.single().get("elements").asList(v -> v.asString());
                if (!elements.isEmpty()) {
                    return "主要成分：" + String.join("、", elements);
                }
            }
        }
        return "未找到该化肥的成分信息";
    }

    // 查询化肥使用方法
    private String getFertilizerUsage(List<String> fertilizerName) {
        if (fertilizerName == null) return "请指定化肥名称";

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (f:Fertilizer {name: $name})-[:USED_FOR]->(u:Using) " +
                            "RETURN collect(u.content) AS usage",
                    Values.parameters("name", fertilizerName)
            );

            if (result.hasNext()) {
                List<String> usage = result.single().get("usage").asList(v -> v.asString());
                if (!usage.isEmpty()) {
                    return String.join("；", usage);
                }
            }
        }
        return "未找到该化肥的使用方法";
    }

    // 查询化肥价格
    private String getFertilizerPrice(List<String> fertilizerName) {
        if (fertilizerName == null) return "请指定化肥名称";

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (f:Fertilizer {name: $name}) " +
                            "RETURN f.price AS price",
                    Values.parameters("name", fertilizerName)
            );

            if (result.hasNext()) {
                return "价格约为：" + result.single().get("price").asString() + "元/公斤";
            }
        }
        return "未找到该化肥的价格信息";
    }

    // 查询农药信息
    private String getPesticideInfo(List<String> pesticideName) {
        if (pesticideName == null) return "请指定农药名称";

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (p:Pesticide {name: $name}) " +
                            "OPTIONAL MATCH (p)-[:CAN_TREAT]->(d:Disease) " +
                            "OPTIONAL MATCH (p)-[:USAGE]->(u:Usage) " +
                            "RETURN p.description AS description, " +
                            "collect(d.name) AS diseases, " +
                            "u.content AS usage",
                    Values.parameters("name", pesticideName)
            );

            if (result.hasNext()) {
                Record record = result.single();
                StringBuilder sb = new StringBuilder();
                sb.append(record.get("description").asString());

                List<String> diseases = record.get("diseases").asList(v -> v.asString());
                if (!diseases.isEmpty()) {
                    sb.append("\n可治疗的病虫害：").append(String.join("、", diseases));
                }

                if (!record.get("usage").isNull()) {
                    sb.append("\n使用方法：").append(record.get("usage").asString());
                }

                return sb.toString();
            }
        }
        return "未找到该农药信息";
    }

    // 查询农药可治疗的病虫害
    private String getDiseasesForPesticide(List<String> pesticideName) {
        if (pesticideName == null) return "请指定农药名称";

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (p:Pesticide {name: $name})-[:CAN_TREAT]->(d:Disease) " +
                            "RETURN collect(d.name) AS diseases",
                    Values.parameters("name", pesticideName)
            );

            if (result.hasNext()) {
                List<String> diseases = result.single().get("diseases").asList(v -> v.asString());
                if (!diseases.isEmpty()) {
                    return "可治疗的病虫害：" + String.join("、", diseases);
                }
            }
        }
        return "未找到该农药可治疗的病虫害信息";
    }

    // 查询农药使用方法
    private String getPesticideUsage(List<String> pesticideName) {
        if (pesticideName == null) return "请指定农药名称";

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (p:Pesticide {name: $name})-[:USAGE]->(u:Usage) " +
                            "RETURN u.content AS usage",
                    Values.parameters("name", pesticideName)
            );

            if (result.hasNext()) {
                return result.single().get("usage").asString();
            }
        }
        return "未找到该农药的使用方法";
    }

    // 检查农药是否能治疗病虫害
    private String checkPesticideEffectiveness(List<String> pesticideName, List<String> diseaseName) {
        if (pesticideName == null || diseaseName == null) {
            return "请同时指定农药和病虫害名称";
        }

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (p:Pesticide {name: $pesticide})-[:CAN_TREAT]->(d:Disease {name: $disease}) " +
                            "RETURN count(*) > 0 AS effective",
                    Values.parameters("pesticide", pesticideName, "disease", diseaseName)
            );

            if (result.hasNext() && result.single().get("effective").asBoolean()) {
                return "是的，" + pesticideName + "可以用于治疗" + diseaseName;
            }
        }
        return "抱歉，" + pesticideName + "不能用于治疗" + diseaseName;
    }

    // 检查品种是否易患某种病虫害
    private String checkVarietySusceptibility(List<String> varietyName, List<String> diseaseName) {
        if (varietyName == null || diseaseName == null) {
            return "请同时指定品种和病虫害名称";
        }

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                    "MATCH (v:Variety {name: $variety})-[:IS_SUSCEPTIBLE_TO]->(d:Disease {name: $disease}) " +
                            "RETURN count(*) > 0 AS susceptible",
                    Values.parameters("variety", varietyName, "disease", diseaseName)
            );

            if (result.hasNext() && result.single().get("susceptible").asBoolean()) {
                return "是的，" + varietyName + "品种较易感染" + diseaseName;
            }
        }
        return "根据现有数据，" + varietyName + "品种对" + diseaseName + "的抗性较强";
    }
}