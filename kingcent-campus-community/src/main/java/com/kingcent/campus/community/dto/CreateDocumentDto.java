package com.kingcent.campus.community.dto;

import lombok.Data;

/**
 * @author rainkyzhong
 * @date 2023/12/13 0:35
 */
@Data
public class CreateDocumentDto {
    private String title;
    private String description;
    private String content;
    private String images;
}
