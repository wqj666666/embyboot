package com.emby.boot.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.emby.boot.core.common.ErrorCodeEnum;
import com.emby.boot.core.exception.BusinessException;
import com.emby.boot.dao.entity.MusicUser;
import com.emby.boot.dao.mapper.MusicUserMapper;
import com.emby.boot.dto.resp.MusicUserInfoResp;
import com.emby.boot.dto.resp.RestResp;
import com.emby.boot.service.MusicUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author laojian
 * @date 2023/10/9 14:22
 */
@Service
@RequiredArgsConstructor
public class MusicUserServiceImpl implements MusicUserService {

    @Value("${spring.navidrome.config.url}")
    private String url;
    @Value("${spring.navidrome.config.username}")
    private String username;
    @Value("${spring.navidrome.config.password}")
    private String password;

    private final MusicUserMapper musicUserMapper;
    /**
     * navidrome注册用户实现方法
     * @param name 用户名
     * @param chatid 电报id
     * @return
     */
    @Override
    public RestResp<MusicUser> userNew(String name, String chatid) {
        //判断数据库是否已经注册过navidrome
        QueryWrapper<MusicUser> musicUserQueryWrapper = new QueryWrapper<>();
        musicUserQueryWrapper.eq("chatid",chatid);
        MusicUser musicUserInfo = musicUserMapper.selectOne(musicUserQueryWrapper);
        //如果有数据，说明已经注册过
        if (musicUserInfo!=null){
            throw new BusinessException(ErrorCodeEnum.USER_MUSIC_REGISTER_ERROR);
        }
        //navidrome管理员的token
        String token = navidromeLogin();
        //新建navidrome用户
        JSONObject jsonObject = navidromeNew(token, name);
        if (jsonObject.containsKey("errors")){
            throw new BusinessException(ErrorCodeEnum.USER_REGISTER_ERROR);
        }
        String id = jsonObject.getString("id");
        //把注册数据写入数据库
        MusicUser musicUser = new MusicUser();
        musicUser.setChatid(chatid);
        musicUser.setMusicUserid(id);
        musicUserMapper.insert(musicUser);
        return RestResp.ok(musicUser);
    }

    /**
     * navidrome用户个人信息实现方法
     * @param chatid 电报id
     * @return
     */
    @Override
    public RestResp<MusicUserInfoResp> userInfo(String chatid) {
        //判断数据库是否已经注册过navidrome
        QueryWrapper<MusicUser> musicUserQueryWrapper = new QueryWrapper<>();
        musicUserQueryWrapper.eq("chatid",chatid);
        MusicUser musicUserInfo = musicUserMapper.selectOne(musicUserQueryWrapper);
        //判断是否已经存在用户
        if (musicUserInfo==null){
            throw new BusinessException(ErrorCodeEnum.USER_REGISTER_NAVIDROME_ERROR);
        }
        //获取navidrome的用户id
        String musicUserid = musicUserInfo.getMusicUserid();
        //navidrome管理员的token
        String token = navidromeLogin();
        JSONObject jsonObject = navidromeInfo(token, musicUserid);
        String userName = jsonObject.getString("userName");
        MusicUserInfoResp musicUserInfoResp = new MusicUserInfoResp();
        musicUserInfoResp.setChatid(chatid);
        musicUserInfoResp.setMusicUserid(musicUserid);
        musicUserInfoResp.setNavidromName(userName);
        return RestResp.ok(musicUserInfoResp);
    }

    /**
     * navidrome重置密码接口实现方法
     * @param chatid 电报id
     * @return
     */
    @Override
    public RestResp<Void> userPassword(String chatid) {
        //判断数据库是否已经注册过navidrome
        QueryWrapper<MusicUser> musicUserQueryWrapper = new QueryWrapper<>();
        musicUserQueryWrapper.eq("chatid",chatid);
        MusicUser musicUserInfo = musicUserMapper.selectOne(musicUserQueryWrapper);
        //判断是否已经存在用户
        if (musicUserInfo==null){
            throw new BusinessException(ErrorCodeEnum.USER_REGISTER_NAVIDROME_ERROR);
        }
        //获取navidrome的用户id
        String musicUserid = musicUserInfo.getMusicUserid();
        //navidrome管理员的token
        String token = navidromeLogin();
        //查询现在navidrome个人信息
        JSONObject navidromeInfoJson = navidromeInfo(token,musicUserid);
        //添加更新的密码,这里我重置密码和用户名一样
        navidromeInfoJson.put("changePassword",true);
        navidromeInfoJson.put("password",navidromeInfoJson.getString("userName"));
        //调用navidrome更新接口，重置密码
        JSONObject jsonObject = navidromeUpInfo(token, musicUserid, navidromeInfoJson);
        if (jsonObject.containsKey("errors")){
            throw new BusinessException(ErrorCodeEnum.USER_PASSWORD_NAVIDROME_ERROR);
        }
        return RestResp.ok();
    }

    /**
     * navidrome 用户删除实现方法
     * @param chatid 电报id
     * @return
     */
    @Override
    public RestResp<Void> userDelete(String chatid) {
        //判断数据库是否已经注册过navidrome
        QueryWrapper<MusicUser> musicUserQueryWrapper = new QueryWrapper<>();
        musicUserQueryWrapper.eq("chatid",chatid);
        MusicUser musicUserInfo = musicUserMapper.selectOne(musicUserQueryWrapper);
        //判断是否存在用户
        if (musicUserInfo==null){
            throw new BusinessException(ErrorCodeEnum.USER_REGISTER_NAVIDROME_ERROR);
        }
        //获取navidrome的用户id
        String musicUserid = musicUserInfo.getMusicUserid();
        //navidrome管理员的token
        String token = navidromeLogin();
        JSONObject jsonObject = navidromeDelete(token, musicUserid);
        if (jsonObject.containsKey("errors")){
            throw new BusinessException(ErrorCodeEnum.USER_DELETE_NAVIDROME_ERROR);
        }
        //删除数据库的
        musicUserMapper.delete(musicUserQueryWrapper);
        return RestResp.ok();
    }

    /**
     * navidrome网站管理员登录接口
     * @return token
     */
    public String navidromeLogin(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username",username);
        jsonObject.put("password",password);
        try {
            HttpResponse<String> response = Unirest.post(url+"/auth/login")
                    .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                    .header("Content-Type", "application/json")
                    .body(jsonObject.toJSONString())
                    .asString();
            String body = response.getBody();
            JSONObject jsonObjects = JSONObject.parseObject(body);
            String token = jsonObjects.getString("token");
            return token;
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * navidrome网站新建用户接口
     * @param token navidrome的管理员token
     * @param name 新建的用户名
     * @return
     */
    public JSONObject navidromeNew(String token,String name){
        JSONObject jsonObjectInfo = new JSONObject();
        jsonObjectInfo.put("isAdmin",false);
        jsonObjectInfo.put("userName",name);
        jsonObjectInfo.put("name",name);
        jsonObjectInfo.put("email","123456@qq.com");
        jsonObjectInfo.put("password",name);
        try {
            HttpResponse<String> response = Unirest.post(url+"/api/user")
                    .header("X-Nd-Authorization", "Bearer " +token)
                    .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                    .header("Content-Type", "application/json")
                    .body(jsonObjectInfo.toJSONString())
                    .asString();
            String body = response.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            return jsonObject;
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * navidrome网站个人信息接口
     * @param token navidrome的管理员token
     * @param musicUserid navidrome的用户id
     * @return
     */
    public JSONObject navidromeInfo(String token,String musicUserid){
        try {
            HttpResponse<String> response = Unirest.get(url+"/api/user/"+musicUserid)
                    .header("X-Nd-Authorization", "Bearer "+token)
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
     * navidrome更新信息接口
     * @param token navidrome的管理员token
     * @param musicUserid navidrome用户id
     * @param json navidrome个人信息
     * @return
     */
    public JSONObject navidromeUpInfo(String token,String musicUserid,JSONObject json){
        try {
            HttpResponse<String> response = Unirest.put(url+"/api/user/"+musicUserid)
                    .header("X-Nd-Authorization", "Bearer "+token)
                    .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                    .header("Content-Type", "application/json")
                    .body(json.toJSONString())
                    .asString();
            String body = response.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            return jsonObject;
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * navidrome删除接口
     * @param token navidrome的管理员token
     * @param musicUserid navidrome用户id
     */
    public JSONObject navidromeDelete(String token,String musicUserid){
        try {
            HttpResponse<String> response = Unirest.delete(url+"/api/user/"+musicUserid)
                    .header("X-Nd-Authorization", "Bearer "+token)
                    .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                    .asString();
            String body = response.getBody();
            JSONObject jsonObject = JSONObject.parseObject(body);
            return jsonObject;
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }
}
