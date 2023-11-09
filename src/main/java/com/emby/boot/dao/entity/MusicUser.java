package com.emby.boot.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 音乐服Navidrome用户成员变量
 * @author laojian
 * @date 2023/10/9 14:08
 */
@Data
@TableName("musicuser")
public class MusicUser {

    @Schema(description = "电报用户id",example = "123")
    private String chatid;

    @Schema(description = "navidrome用户id",example = "123")
    private String musicUserid;
}
