package com.emby.boot.service;

import com.emby.boot.dto.req.MusicDownReq;
import com.emby.boot.dto.resp.RestResp;
import com.alibaba.fastjson.JSONObject;

/**
 * @author laojian
 * @date 2023/10/9 14:36
 */
public interface MusicDownloaderService {
    /**
     * 歌曲搜索
     * @param keywords 搜索关键字
     * @param pageNo 页码
     * @param pageSize 页码大小
     * @return 搜索结果
     */
   JSONObject search(String keywords,Integer  pageNo,Integer  pageSize);
    /**
     * 歌曲下载
     * @param musicDownReq
     * @return
     */
    RestResp<Void> download(MusicDownReq musicDownReq,String chatid);
}
