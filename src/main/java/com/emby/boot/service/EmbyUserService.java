package com.emby.boot.service;

import com.emby.boot.dao.entity.EmbyUser;
import com.emby.boot.dto.resp.EmbyUserInfoResp;
import com.emby.boot.dto.resp.RestResp;

/**
 * @author laojian
 * @date 2023/10/9 14:21
 */
public interface EmbyUserService {
     /**
      * emby新建用户
      * @param name 用户名称
      * @param chatid 电报id
      * @return
      */
     RestResp<EmbyUser> userNew(String name,String chatid);

     /**
      * emby删除用户
      * @param chatid 电报id
      * @return
      */
     RestResp<Void> userDelete(String chatid);

     /**
      * emby去除密码
      * @param chatid 电报id
      * @return
      */
     RestResp<Void> userPassword(String chatid);

     /**
      * emby个人信息
      * @param chatid 电报id
      * @return
      */
     RestResp<EmbyUserInfoResp> userInfo(String chatid);
}
