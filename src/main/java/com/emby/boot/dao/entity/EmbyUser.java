package com.emby.boot.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 *
 * @author laojian
 * @date 2023/10/9 14:02
 */
@Schema(description = "emby用户成员变量")
@Data
@TableName("user")
public class EmbyUser {
    @Schema(description = "电报用户id",example = "123")
    private String chatid;

    @Schema(description = "emby用户id",example = "123")
    private String embyUserid;

}
