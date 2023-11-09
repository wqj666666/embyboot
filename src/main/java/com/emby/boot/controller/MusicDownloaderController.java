package com.emby.boot.controller;

import com.emby.boot.dto.req.MusicDownReq;
import com.emby.boot.dto.resp.RestResp;
import com.emby.boot.service.MusicDownloaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

/**
 * @author laojian
 * @date 2023/10/10 17:56
 */
@Tag(name = "MusiDownloaderController",description = "muisc下载工具控制类")
@RestController
@RequestMapping("/musiDownloader")
@RequiredArgsConstructor
public class MusicDownloaderController {
    private final MusicDownloaderService musiDownloader;
    @Operation(summary = "muisc下载工具搜索接口")
    @PostMapping("/search")
    public JSONObject search(  String keywords, @RequestParam(value = "pageNo",defaultValue = "1",required = false) Integer  pageNo, @RequestParam(value = "pageSize",defaultValue = "5",required = false) Integer  pageSize){
        return musiDownloader.search(keywords,pageNo,pageSize);
    }

    @Operation(summary = "muisc下载工具下载接口")
    @PostMapping("/download")
    public RestResp<Void> download(@RequestBody MusicDownReq musicDownReq,@RequestParam String chatid){
        return musiDownloader.download(musicDownReq,chatid);
    }
}
