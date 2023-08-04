package com.kingcent.campus.util;

import com.alibaba.fastjson.JSONObject;
import com.kingcent.campus.common.entity.GroupPointPathEntity;
import com.kingcent.campus.entity.vo.group.point.EdgeVo;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class GroupPointUtil {

    /**
     * 路径信息
     */
    @AllArgsConstructor
    public static class PathInfo{
        public String crossPoints;
        public Integer cost;
    }

    /**
     * 目的点
     */
    @AllArgsConstructor
    static class TargetPoint{
        //目的点的ID
        public Long pointId;

        //到达目的点的ID
        public Integer distance;
    }

    /**
     * 最短路径信息
     */
    @AllArgsConstructor
    static class FindSortPathResult{
        Map<Long, Integer> distances;
        Map<Long, Long> previousNodes;
    }

    /**
     * 计算各目标点之间的最短路径
     */
    public static List<GroupPointPathEntity> culPath(Map<String, Long> pointIdMap, List<EdgeVo> edges){

        List<GroupPointPathEntity> pathEntities = new LinkedList<>();

        //生成邻接表
        Map<Long, Set<TargetPoint>> map = new HashMap<>();
        for (EdgeVo edge : edges) {
            Long point1Id = pointIdMap.get(edge.getPoint1Code());
            Long point2Id = pointIdMap.get(edge.getPoint2Code());
            Set<TargetPoint> point1 = map.computeIfAbsent(point1Id, k -> new HashSet<>());
            point1.add(new TargetPoint(point2Id, edge.getDistance()));
            Set<TargetPoint> point2 = map.computeIfAbsent(point2Id, k -> new HashSet<>());
            point2.add(new TargetPoint(point1Id, edge.getDistance()));
        }

        for (Long startId : pointIdMap.values()) {
            FindSortPathResult findSortPathResult = findShortestPath(map, startId);
            for (Map.Entry<Long, Integer> entry : findSortPathResult.distances.entrySet()) {
                Long pointId = entry.getKey();
                if (pointId.equals(startId)) continue;
                Integer distance = entry.getValue();
                List<Long> shortestPath = getShortestPath(findSortPathResult.previousNodes, pointId);
                GroupPointPathEntity groupPointPathEntity = new GroupPointPathEntity();
                groupPointPathEntity.setPoint1(startId);
                groupPointPathEntity.setPoint2(pointId);
                String path = JSONObject.toJSONString(shortestPath);
                groupPointPathEntity.setPassPoints(path.substring(1,path.length() - 1));
                groupPointPathEntity.setCost(distance);
                pathEntities.add(groupPointPathEntity);
            }
        }

        return pathEntities;
    }

    /**
     * 计算起始点到其它各点的最短路径
     * @param map 邻接表
     * @param startId 起始点
     */
    public static FindSortPathResult findShortestPath(Map<Long, Set<TargetPoint>> map, Long startId) {
        Map<Long, Integer> distances = new HashMap<>();
        Map<Long, Long> previousNodes = new HashMap<>(); // 记录每个节点的前驱节点
        Set<Long> visited = new HashSet<>();
        PriorityQueue<TargetPoint> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p.distance));

        for (Long key : map.keySet()) {
            distances.put(key, Integer.MAX_VALUE);
        }

        distances.put(startId, 0);
        pq.offer(new TargetPoint(startId, 0));

        while (!pq.isEmpty()) {
            TargetPoint current = pq.poll();
            Long currentId = current.pointId;

            if (visited.contains(currentId)) {
                continue;
            }

            visited.add(currentId);

            if (!map.containsKey(currentId)) {
                continue;
            }

            Set<TargetPoint> neighbors = map.get(currentId);

            for (TargetPoint neighbor : neighbors) {
                Long neighborId = neighbor.pointId;
                Integer distance = neighbor.distance;

                if (!visited.contains(neighborId)) {
                    int newDistance = distances.get(currentId) + distance;

                    if (newDistance < distances.get(neighborId)) {
                        distances.put(neighborId, newDistance);
                        previousNodes.put(neighborId, currentId); // 更新前驱节点
                        pq.offer(new TargetPoint(neighborId, newDistance));
                    }
                }
            }
        }
        return new FindSortPathResult(distances, previousNodes);
    }

    /**
     * 获取路径信息
     * @param previousNodes 前驱节点集
     * @param endId 目的点
     */
    public static List<Long> getShortestPath(Map<Long, Long> previousNodes, Long endId) {
        List<Long> path = new ArrayList<>();
        Long currentId = endId;
        while (currentId != null) {
            path.add(currentId);
            currentId = previousNodes.get(currentId);
        }
        Collections.reverse(path);
        if (path.size() <= 2) return new ArrayList<>();
        path.remove(0);
        path.remove(path.size() - 1);
        return path;
    }

    /**
     * 逆转路径
     */
    public static String reversePath(String path){
        String[] arr = path.split(",");
        StringBuilder reversedStr = new StringBuilder();

        // 从最后一个元素开始，逆序拼接字符串
        for (int i = arr.length - 1; i >= 0; i--) {
            reversedStr.append(arr[i]);
            if (i > 0) {
                reversedStr.append(",");
            }
        }
        return reversedStr.toString();
    }


    @AllArgsConstructor
    static class RoadMsgInfo{
        //信息素
        public Integer tau;
        //能见度
        public Double eta;
    }

    /**
     * 蚁群算法，计算最佳巡回路径
     */
    public void getBestCrossPath(Map<Long,Map<Long, PathInfo>> paths, Long startPointId){

        double alpha = 1;
        double beta = 1;

        //初始化信息集
        Map<Long,Map<Long, RoadMsgInfo>> messages = new HashMap<>();
        paths.forEach((origin,desInfo)->{
            desInfo.forEach((destination,pathInfo)->{
                Map<Long, RoadMsgInfo> messageMap = messages.computeIfAbsent(origin, k -> new HashMap<>());
                messageMap.put(destination, new RoadMsgInfo(2, (double)1/pathInfo.cost));
            });
        });

        //
        for(int t = 0; t < 10; t++){
            //创建蚂蚁
            for(int k = 0; k < 10; k++){

                //遍历所有点
                boolean allVisited = false;
                List<Long> visited = new ArrayList<>();
                visited.add(startPointId);
                while (!allVisited){
                    Map<Long, RoadMsgInfo> path = messages.get(visited.get(visited.size()-1));
                    //寻找下一个要走的点
                    AtomicReference<Double> x = new AtomicReference<>((double) 0);
                    AtomicReference<Double> y = new AtomicReference<>((double) 0);
                    AtomicReference<Long> decideNextPoint = new AtomicReference<>(startPointId);
                    AtomicBoolean noneAllowedPoint = new AtomicBoolean(true);
                    path.forEach((next,info)->{
                        //从没有走过的点里找
                        if(!visited.contains(next)){
                            //计算转移到该点的概率
                            double p = Math.pow(info.tau, alpha) * Math.pow(info.eta, beta);
                            //统计最大值
                            if(p > x.get()){
                                x.set(p);
                                decideNextPoint.set(next);
                            }
                            //统计总量
                            y.set(y.get()+p);
                            //记录存在下一个点
                            noneAllowedPoint.set(false);
                        }
                    });
                    //记录禁忌表
                    visited.add(decideNextPoint.get());
                    //记录是否全部已访问
                    allVisited = !noneAllowedPoint.get();

                }
            }
        }
    }
}
