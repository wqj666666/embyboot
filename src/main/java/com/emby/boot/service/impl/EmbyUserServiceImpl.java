package com.emby.boot.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.emby.boot.core.common.ErrorCodeEnum;
import com.emby.boot.core.exception.BusinessException;
import com.emby.boot.dao.entity.EmbyUser;
import com.emby.boot.dao.mapper.EmbyUserMapper;
import com.emby.boot.dto.resp.EmbyUserInfoResp;
import com.emby.boot.dto.resp.RestResp;
import com.emby.boot.service.EmbyUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/**
 * emby用户相关实现类
 * @author laojian
 * @date 2023/10/9 14:22
 */
@Service
@RequiredArgsConstructor
public class EmbyUserServiceImpl implements EmbyUserService {

    @Value("${spring.emby.config.url}")
    private String url;
    @Value("${spring.emby.config.apiKey}")
    private String apiKey;
    @Value("${spring.emby.config.copyFromUserId}")
    private String copyFromUserId;

    private final EmbyUserMapper embyUserMapper;
    /**
     * emby新建用户实现方法
     * @return
     */
    @Override
    public RestResp<EmbyUser> userNew(String name,String chatid) {
        //判断用户是否已经注册过
        QueryWrapper<EmbyUser> embyUserQueryWrapper = new QueryWrapper<>();
        embyUserQueryWrapper.eq("chatid",chatid);
        EmbyUser embyUserInfo = embyUserMapper.selectOne(embyUserQueryWrapper);
        if (embyUserInfo!=null){
            throw new BusinessException(ErrorCodeEnum.USER_EMBY_REGISTER_ERROR);
        }
        //emby注册接口注册
        JSONObject jsonObject = embyUsersNew(name);
        if (jsonObject==null){
            throw new BusinessException(ErrorCodeEnum.USER_EMBY_REPEAT_ERROR);
        }
        //获取emby用户id
        String id = jsonObject.getString("Id");

        // 注册成功，保存用户信息
        EmbyUser embyUser = new EmbyUser();
        embyUser.setChatid(chatid);
        embyUser.setEmbyUserid(id);
        embyUserMapper.insert(embyUser);

        return RestResp.ok(embyUser);
    }

    /**
     * 删除emby用户实现方法
     * @param chatid 电报用户id
     * @return
     */
    @Override
    public RestResp<Void> userDelete(String chatid) {
        //电报id查询对应的
        QueryWrapper<EmbyUser> embyUserQueryWrapper = new QueryWrapper<>();
        embyUserQueryWrapper.eq("chatid",chatid);
        EmbyUser embyUserInfo = embyUserMapper.selectOne(embyUserQueryWrapper);
        //判断是否注册过
        if (embyUserInfo==null){
            throw new BusinessException(ErrorCodeEnum.USER_REGISTER_EMBY_ERROR);
        }
        //删除emby用户
        String embyUserid = embyUserInfo.getEmbyUserid();
        embyUserDelete(embyUserid);
        //删除数据库的emby用户
        embyUserMapper.delete(embyUserQueryWrapper);
        return RestResp.ok();
    }

    /**
     * 重置emby用户实现方法
     * @param chatid
     * @return
     */
    @Override
    public RestResp<Void> userPassword(String chatid) {
        //电报id查询对应的
        QueryWrapper<EmbyUser> embyUserQueryWrapper = new QueryWrapper<>();
        embyUserQueryWrapper.eq("chatid",chatid);
        EmbyUser embyUserInfo = embyUserMapper.selectOne(embyUserQueryWrapper);
        //判断是否注册过
        if (embyUserInfo==null){
            throw new BusinessException(ErrorCodeEnum.USER_REGISTER_EMBY_ERROR);
        }
        //重置emby用户密码
        String embyUserid = embyUserInfo.getEmbyUserid();
        embyUserPassword(embyUserid);
        return RestResp.ok();
    }

    /**
     * emby用户个人信息接口
     * @param chatid
     * @return
     */
    @Override
    public RestResp<EmbyUserInfoResp> userInfo(String chatid) {
        //电报id查询对应的
        QueryWrapper<EmbyUser> embyUserQueryWrapper = new QueryWrapper<>();
        embyUserQueryWrapper.eq("chatid",chatid);
        EmbyUser embyUserInfo = embyUserMapper.selectOne(embyUserQueryWrapper);
        //判断是否注册过
        if (embyUserInfo==null){
            throw new BusinessException(ErrorCodeEnum.USER_REGISTER_EMBY_ERROR);
        }
        //查询emby用户信息
        String embyUserid = embyUserInfo.getEmbyUserid();
        JSONObject jsonObject = embyUserInfo(embyUserid);
        if (jsonObject==null){
            throw new BusinessException(ErrorCodeEnum.USER_REGISTER_EMBY_ERROR);

        }
        String name = jsonObject.getString("Name");
        EmbyUserInfoResp embyUserInfoResp = new EmbyUserInfoResp();
        embyUserInfoResp.setChatid(chatid);
        embyUserInfoResp.setEmbyUserid(embyUserid);
        embyUserInfoResp.setEmbyName(name);
        return RestResp.ok(embyUserInfoResp);

    }

    /**
     * emby网站注册接口
     * @param Name 用户名
     * @return
     */
    public JSONObject embyUsersNew(String Name ){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Name",Name);
        jsonObject.put("CopyFromUserId",copyFromUserId);
        jsonObject.put("UserCopyOptions","UserPolicy,UserConfiguration");
        JSONObject json=null;
        try {
            HttpResponse<String> response = Unirest.post(url+"/emby/Users/New?api_key="+apiKey)
                    .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                    .header("Content-Type", "application/json")
                    .body(jsonObject.toJSONString())
                    .asString();
            String body = response.getBody();
            if (response.getStatus()!=200){
                return null;
            }
            json = JSONObject.parseObject(body);
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    /**
     * emby网站删除接口
     * @param embyUserId
     */
    public void embyUserDelete(String embyUserId){
        try {
            HttpResponse<String> response = Unirest.delete(url+"/emby/Users/"+embyUserId+"?api_key="+apiKey)
                    .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * emby网站去除密码接口
     * @param embyUserId
     */
    public void embyUserPassword(String embyUserId){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Id",embyUserId);
        jsonObject.put("ResetPassword",true);
        try {
            HttpResponse<String> response = Unirest.post(url+"/emby/Users/"+embyUserId+"/Password?api_key="+apiKey)
                    .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                    .header("Content-Type", "application/json")
                    .body(jsonObject.toJSONString())
                    .asString();
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * emby网站个人信息接口
     * @param embyUserId
     */
    public JSONObject embyUserInfo(String embyUserId){
        try {
            HttpResponse<String> response = Unirest.get("http://emby.imetyou.top:8096/emby/Users/"+embyUserId+"?api_key="+apiKey)
                    .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                    .asString();
            String body = response.getBody();
            if (response.getStatus()!=200){
                return null;
            }
            JSONObject json = JSONObject.parseObject(body);
            return json;
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }

    }
}
