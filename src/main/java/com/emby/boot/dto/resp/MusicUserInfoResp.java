package com.emby.boot.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author laojian
 * @date 2023/10/10 16:24
 */
@Schema(description = "navidrome用户个人信息成员变量")
@Data
public class MusicUserInfoResp {
    @Schema(description = "电报用户id",example = "123")
    private String chatid;

    @Schema(description = "navidrom用户id",example = "123")
    private String musicUserid;

    @Schema(description = "navidrom用户名称",example = "123")
    private String navidromName;
}
