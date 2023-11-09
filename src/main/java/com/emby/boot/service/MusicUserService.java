package com.emby.boot.service;

import com.emby.boot.dao.entity.MusicUser;
import com.emby.boot.dto.resp.MusicUserInfoResp;
import com.emby.boot.dto.resp.RestResp;

/**
 * @author laojian
 * @date 2023/10/9 14:22
 */
public interface MusicUserService {
    /**
     * navidrome注册用户
     * @param name 用户名
     * @param chatid 电报id
     * @return
     */
    RestResp<MusicUser> userNew(String name, String chatid);

    /**
     * navidrome用户个人信息
     * @param chatid 电报id
     * @return
     */
    RestResp<MusicUserInfoResp> userInfo(String chatid);

    /**
     * navidrome用户重置密码
     * @param chatid 电报id
     * @return
     */
    RestResp<Void> userPassword(String chatid);

    /**
     * navidrome用户删除
     * @param chatid 电报id
     * @return
     */
    RestResp<Void> userDelete( String chatid);
}
