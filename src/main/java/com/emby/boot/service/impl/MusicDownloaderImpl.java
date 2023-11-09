package com.emby.boot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.emby.boot.core.common.ErrorCodeEnum;
import com.emby.boot.core.exception.BusinessException;
import com.emby.boot.dao.entity.MusicDownloaderRecord;
import com.emby.boot.dao.mapper.MusicDownloaderRecordMapper;
import com.emby.boot.dto.req.MusicDownReq;
import com.emby.boot.dto.resp.RestResp;
import com.emby.boot.service.MusicDownloaderService;
import lombok.RequiredArgsConstructor;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * 音乐下载相关的实现类
 * @author laojian
 * @date 2023/10/9 14:36
 */
@Service
@RequiredArgsConstructor
public class MusicDownloaderImpl implements MusicDownloaderService {

    @Value("${spring.musicDownloader.config.url}")
    private String url;
    @Value("${spring.musicDownloader.config.downloadFolder}")
    private String downloadFolder;
    private final MusicDownloaderRecordMapper musicDownloaderRecordMapper;
    /**
     * 歌曲搜索接口
     * @param keywords 搜索关键字
     * @param pageNo 页码
     * @param pageSize 页码大小
     * @return
     */
    @Override
    public JSONObject search(String keywords,Integer  pageNo,Integer  pageSize) {
        String encode=null;
        try {
             encode = URLEncoder.encode(keywords, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        try {
            HttpResponse<String> response = Unirest.get(url+"/qq/search/"+encode+"/"+pageNo+"/"+pageSize)
                    .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                    .asString();
            String body = response.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            return jsonObject;
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 歌曲下载实现方法
     * @param musicDownReq
     * @return
     */
    @Override
    public RestResp<Void> download(MusicDownReq musicDownReq,String chatid) {
        //判断是否已经下载过或者已经存在
        QueryWrapper<MusicDownloaderRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title",musicDownReq.getTitle());
        queryWrapper.eq("singer",musicDownReq.getSinger());
        queryWrapper.eq("album",musicDownReq.getAlbum());
        queryWrapper.isNull("delete_time");

        Long count = musicDownloaderRecordMapper.selectCount(queryWrapper);
        if (count>0){
            throw new BusinessException(ErrorCodeEnum.MUISC_DOWN_ERROR);
        }
        //判断用户今天下次次数是否超过了10次
        LocalDate today = LocalDate.now();
        // 今天的开始时间
        ZonedDateTime startOfToday = today.atStartOfDay(ZoneId.systemDefault());
        long startTimestamp = startOfToday.toEpochSecond() * 1000;  // Unix时间戳
        // 今天的结束时间
        ZonedDateTime endOfToday = startOfToday.plusDays(1).minusSeconds(1);
        long endTimestamp = endOfToday.toEpochSecond() * 1000 ;  // Unix时间戳

        QueryWrapper<MusicDownloaderRecord> recordTimeQueryWrapper = new QueryWrapper<>();
        recordTimeQueryWrapper.ge("create_time",startTimestamp);
        recordTimeQueryWrapper.le("create_time",endTimestamp);
        recordTimeQueryWrapper.eq("chatid",chatid);
        Long dateCount = musicDownloaderRecordMapper.selectCount(recordTimeQueryWrapper);
        if (dateCount>5){
            throw new BusinessException(ErrorCodeEnum.MUISC_DOWN_DATE_ERROR);
        }

        JSONObject jsonObject = new JSONObject();
        JSONObject musicJsonObject = new JSONObject();
        musicJsonObject.put("readableText",musicDownReq.getReadableText());
        musicJsonObject.put("singer",musicDownReq.getSinger());
        musicJsonObject.put("time_publish",musicDownReq.getTime_publish());
        musicJsonObject.put("album",musicDownReq.getAlbum());
        musicJsonObject.put("prefix",musicDownReq.getPrefix());
        musicJsonObject.put("songmid",musicDownReq.getSongmid());
        musicJsonObject.put("albumMid",musicDownReq.getAlbumMid());
        musicJsonObject.put("mid",musicDownReq.getMid());
        musicJsonObject.put("title",musicDownReq.getTitle());
        musicJsonObject.put("musicid",musicDownReq.getMusicid());
        musicJsonObject.put("size",musicDownReq.getSize());
        musicJsonObject.put("extra",musicDownReq.getExtra());
        musicJsonObject.put("notice",musicDownReq.getNotice());
        JSONObject configJsonObject = new JSONObject();
        configJsonObject.put("onlyMatchSearchKey",false);
        configJsonObject.put("ignoreNoAlbumSongs",false);
        configJsonObject.put("classificationMusicFile",false);
        configJsonObject.put("disableFilterKey",false);
        configJsonObject.put("platform","qq");
        JSONObject concurrencyJsonObject = new JSONObject();
        concurrencyJsonObject.put("num",16);
        concurrencyJsonObject.put("downloadFolder",downloadFolder);

        jsonObject.put("music",musicJsonObject);
        jsonObject.put("config",configJsonObject);
        try {
            HttpResponse<String> response = Unirest.post(url+"/download")
                    .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                    .header("Content-Type", "application/json")
                    .body(jsonObject.toJSONString())
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
        //把下载记录写入数据库
        MusicDownloaderRecord musicDownloaderRecord = new MusicDownloaderRecord();
        musicDownloaderRecord.setChatid(chatid);
        musicDownloaderRecord.setAlbum(musicDownReq.getAlbum());
        musicDownloaderRecord.setSinger(musicDownReq.getSinger());
        musicDownloaderRecord.setTitle(musicDownReq.getTitle());
        musicDownloaderRecord.setCreateTime(System.currentTimeMillis());
       musicDownloaderRecordMapper.insert(musicDownloaderRecord);
        return RestResp.ok();
    }
}
