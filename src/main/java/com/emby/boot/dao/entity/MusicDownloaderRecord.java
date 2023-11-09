package com.emby.boot.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author laojian
 * @date 2023/10/10 19:03
 */
@Schema(description = "muisc下载工具用户下载记录成员变量")
@Data
@TableName("muisc_downloader_record")
public class MusicDownloaderRecord {
    @Schema(description = "记录id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    @Schema(description = "电报用户id")
    private String chatid;
    @Schema(description = "音乐标题")
    private String title;
    @Schema(description = "音乐作者")
    private String singer;
    @Schema(description = "音乐专辑")
    private String album;
    @Schema(description = "创建时间")
    private Long createTime;
    @Schema(description = "更新时间")
    private Long updateTime;
    @Schema(description = "删除时间")
    private Long deleteTime;
}
