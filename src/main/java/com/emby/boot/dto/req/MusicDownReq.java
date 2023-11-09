package com.emby.boot.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author laojian
 * @date 2023/10/11 10:11
 */
@Schema(description = "muisc下载成员变量")
@Data
public class MusicDownReq {

    private String album;
    private String albumMid;
    private String extra;
    private String mid;
    private Integer musicid;
    private String notice;
    private String prefix;
    private String readableText;
    private String singer;
    private String size;
    private String songmid;
    private String time_publish;
    private String title;

}
