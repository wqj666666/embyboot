package com.emby.boot.controller;

import com.emby.boot.dao.entity.EmbyUser;
import com.emby.boot.dto.resp.EmbyUserInfoResp;
import com.emby.boot.dto.resp.RestResp;
import com.emby.boot.service.EmbyUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author laojian
 * @date 2023/10/9 17:32
 */
@Tag(name = "EmbyUserController",description = "emby用户控制类")
@RestController
@RequestMapping("/emby")
@RequiredArgsConstructor
public class EmbyUserController {
    private final EmbyUserService embyUserService;
    /**
     * emby用户注册接口
     * @param name
     * @return
     */
    @Operation(summary = "emby用户注册接口")
    @PostMapping("/userNew")
    public RestResp<EmbyUser> userNew(String name, String chatid){
        return embyUserService.userNew(name,chatid);
    }

    /**
     * emby用户删除接口
     * @param chatid
     * @return
     */
    @Operation(summary = "emby用户删除接口")
    @PostMapping("/userDelete")
    public RestResp<Void> userDelete( String chatid){
        return embyUserService.userDelete(chatid);
    }
    /**
     * emby用户去除密码接口
     * @param chatid
     * @return
     */
    @Operation(summary = "emby用户去除密码接口")
    @PostMapping("/userPassword")
    public RestResp<Void> userPassword(String chatid){
        return embyUserService.userPassword(chatid);
    }

    /**
     * emby用户个人信息接口
     * @param chatid
     * @return
     */
    @Operation(summary = "emby用户个人信息接口")
    @PostMapping("/userInfo")
    public RestResp<EmbyUserInfoResp> userInfo(String chatid){
        return embyUserService.userInfo(chatid);

    }

}
