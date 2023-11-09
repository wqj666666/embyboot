package com.emby.boot.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author laojian
 * @date 2023/10/9 19:42
 */
@Schema(description = "emby用户个人信息成员变量")
@Data
public class EmbyUserInfoResp {
    @Schema(description = "电报用户id",example = "123")
    private String chatid;

    @Schema(description = "emby用户id",example = "123")
    private String embyUserid;

    @Schema(description = "emby用户名称",example = "123")
    private String embyName;
}
