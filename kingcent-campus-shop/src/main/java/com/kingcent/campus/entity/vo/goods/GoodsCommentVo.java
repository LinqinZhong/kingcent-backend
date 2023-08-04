package com.kingcent.campus.entity.vo.goods;

import com.kingcent.campus.common.entity.GoodsCommentEntity;
import lombok.Data;

import java.util.Date;

@Data
public class GoodsCommentVo {
    private String username;
    private Date time;
    private String specInfo;
    private String images;
    private String headImage;
    private String content;

    public GoodsCommentVo(String username, String headImage, GoodsCommentEntity entity){
        if(username != null) {
            int len = username.length();
            this.username = username.charAt(0) + "**" + (len > 2 ? username.charAt(len - 1) : "");
        }
        this.time = entity.getCreateTime();
        this.content = entity.getVal();
        this.headImage = headImage;
        this.images = entity.getImages();
        this.specInfo = entity.getSpecInfo();
    }
}
