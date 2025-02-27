package com.kingcent.plant.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kingcent.plant.entity.PlanEntity;
import com.kingcent.plant.entity.TaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author rainkyzhong
 * @date 2025/2/5 23:06
 */
@Mapper
public interface TaskMapper extends BaseMapper<TaskEntity> {


    @Select("<script>" +
            "SELECT COUNT(DISTINCT t.id) FROM kc_agrc_task t " +
            "LEFT JOIN kc_agrc_task_member tm ON t.id = tm.task_id " +
            "LEFT JOIN kc_agrc_task_land tl ON t.id = tl.task_id " +
            "WHERE 1 = 1 " +
            "<if test='planId != null'>" +
            "AND t.plan_id = #{planId} " +
            "</if>" +
            "<if test='memberIds != null and memberIds.size() > 0'>" +
            "AND tm.member_id IN " +
            "<foreach item='item' index='index' collection='memberIds' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach> " +
            "</if>" +
            "<if test='landIds != null and landIds.size() > 0'>" +
            "AND tl.member_id IN " +
            "<foreach item='item' index='index' collection='landIds' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach> " +
            "</if>" +
            "<if test='nameLike != null and nameLike != \"\"'>" +
            "AND t.name LIKE CONCAT('%', #{nameLike}, '%') " +
            "</if>" +
            "<if test='statuses != null'>" +
            "AND t.status IN " +
            "<foreach item='item' index='index' collection='statuses' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach> " +
            "</if>" +
            "<if test='types != null'>" +
            "AND t.type IN " +
            "<foreach item='item' index='index' collection='types' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach> " +
            "</if>" +
            "<if test='startTimeFrom != null'>" +
            "AND t.start_time &gt;= #{startTimeFrom} " +
            "</if>" +
            "<if test='startTimeEnd != null'>" +
            "AND t.start_time &lt;= #{startTimeEnd} " +
            "</if>" +
            "<if test='endTimeFrom != null'>" +
            "AND t.end_time &gt;= #{endTimeFrom} " +
            "</if>" +
            "<if test='endTimeEnd != null'>" +
            "AND t.end_time &lt;= #{endTimeEnd} " +
            "</if> "+
            "</script>")
    Integer selectCount(
            @Param("planId") Long planId,
            @Param("memberIds") Collection<Long> memberIds,
            @Param("landIds") Collection<Long> landIds,
            @Param("nameLike") String nameLike,
            @Param("statuses") Collection<Integer> statuses,
            @Param("types") Collection<Integer> types,
            @Param("startTimeFrom") LocalDateTime startTimeFrom,
            @Param("startTimeEnd") LocalDateTime startTimeEnd,
            @Param("endTimeFrom") LocalDateTime endTimeFrom,
            @Param("endTimeEnd") LocalDateTime endTimeEnd
    );

    @Select("<script>" +
            "SELECT t.* FROM kc_agrc_task t " +
            "LEFT JOIN kc_agrc_task_member tm ON t.id = tm.task_id " +
            "LEFT JOIN kc_agrc_task_land tl ON t.id = tl.task_id " +
            "WHERE 1 = 1 " +
            "<if test='planId != null'>" +
            "AND t.plan_id = #{planId} " +
            "</if>" +
            "<if test='memberIds != null and memberIds.size() > 0'>" +
            "AND tm.member_id IN " +
            "<foreach item='item' index='index' collection='memberIds' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach> " +
            "</if>" +
            "<if test='landIds != null and landIds.size() > 0'>" +
            "AND tl.member_id IN " +
            "<foreach item='item' index='index' collection='landIds' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach> " +
            "</if>" +
            "<if test='nameLike != null and nameLike != \"\"'>" +
            "AND t.name LIKE CONCAT('%', #{nameLike}, '%') " +
            "</if>" +
            "<if test='statuses != null'>" +
            "AND t.status IN " +
            "<foreach item='item' index='index' collection='statuses' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach> " +
            "</if>" +
            "<if test='types != null'>" +
            "AND t.type IN " +
            "<foreach item='item' index='index' collection='types' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach> " +
            "</if>" +
            "<if test='startTimeFrom != null'>" +
            "AND t.start_time &gt;= #{startTimeFrom} " +
            "</if>" +
            "<if test='startTimeEnd != null'>" +
            "AND t.start_time &lt;= #{startTimeEnd} " +
            "</if>" +
            "<if test='endTimeFrom != null'>" +
            "AND t.end_time &gt;= #{endTimeFrom} " +
            "</if>" +
            "<if test='endTimeEnd != null'>" +
            "AND t.end_time &lt;= #{endTimeEnd} " +
            "</if>" +
            "GROUP BY t.id "+
            "LIMIT #{pageNum}, #{pageSize}" +
            "</script>")
    List<TaskEntity> selectPage(
            @Param("pageNum") Integer pageNum,
            @Param("pageSize") Integer pageSize,
            @Param("planId") Long planId,
            @Param("memberIds") Collection<Long> memberIds,
            @Param("landIds") Collection<Long> landIds,
            @Param("nameLike") String nameLike,
            @Param("statuses") Collection<Integer> statuses,
            @Param("types") Collection<Integer> types,
            @Param("startTimeFrom") LocalDateTime startTimeFrom,
            @Param("startTimeEnd") LocalDateTime startTimeEnd,
            @Param("endTimeFrom") LocalDateTime endTimeFrom,
            @Param("endTimeEnd") LocalDateTime endTimeEnd
    );
}
