package com.emby.boot.controller;

import com.emby.boot.dao.entity.MusicUser;
import com.emby.boot.dto.resp.MusicUserInfoResp;
import com.emby.boot.dto.resp.RestResp;
import com.emby.boot.service.MusicUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author laojian
 * @date 2023/10/10 11:12
 */
@Tag(name = "MusicUserController",description = "navidrome用户控制类")
@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicUserController {
    private final MusicUserService musicUserService;

    /**
     * navidrome用户注册接口
     * @param name
     * @return
     */
    @Operation(summary = "navidrome用户注册接口")
    @PostMapping("/userNew")
    public RestResp<MusicUser> userNew(String name, String chatid){
        return musicUserService.userNew(name,chatid);
    }
    /**
     * navidrome用户个人信息接口
     * @param chatid
     * @return
     */
    @Operation(summary = "navidrome用户个人信息接口")
    @PostMapping("/userInfo")
    public RestResp<MusicUserInfoResp> userInfo(String chatid){
        return musicUserService.userInfo(chatid);

    }

    /**
     * navidrome用户去除密码接口
     * @param chatid
     * @return
     */
    @Operation(summary = "navidrome用户重置密码接口")
    @PostMapping("/userPassword")
    public RestResp<Void> userPassword(String chatid){
        return musicUserService.userPassword(chatid);
    }

    /**
     * navidrome用户删除接口
     * @param chatid
     * @return
     */
    @Operation(summary = "navidrome用户删除接口")
    @PostMapping("/userDelete")
    public RestResp<Void> userDelete( String chatid){
        return musicUserService.userDelete(chatid);
    }

}
