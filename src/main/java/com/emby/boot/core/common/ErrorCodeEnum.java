package com.emby.boot.core.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举类。
 * <p>
 * 错误码为字符串类型，共 5 位，分成两个部分：错误产生来源+四位数字编号。 错误产生来源分为 A/B/C， A 表示错误来源于用户，比如参数错误，用户安装版本过低，用户支付 超时等问题； B
 * 表示错误来源于当前系统，往往是业务逻辑出错，或程序健壮性差等问题； C 表示错误来源 于第三方服务，比如 CDN 服务出错，消息投递超时等问题；四位数字编号从 0001 到 9999，大类之间的
 * 步长间距预留 100。
 * <p>
 * 错误码分为一级宏观错误码、二级宏观错误码、三级宏观错误码。 在无法更加具体确定的错误场景中，可以直接使用一级宏观错误码。
 *
 * @author laojian
 * @date 2022/5/11
 */
@Getter
@AllArgsConstructor
public enum ErrorCodeEnum {

    /**
     * 正确执行后的返回
     */
    OK("00000", "成功"),

    /**
     * 一级宏观错误码，用户端错误
     */
    USER_ERROR("A0001", "用户端错误"),

    /**
     * 二级宏观错误码，用户注册错误
     */
    USER_REGISTER_ERROR("A0100", "用户注册错误"),
    /**
     * 二级宏观错误码，emby已经注册
     */
    USER_EMBY_REGISTER_ERROR("A0104", "存在emby账户，无法重复注册"),
    /**
     * 二级宏观错误码，emby注册失败，可能是用户名已存在
     */
    USER_EMBY_REPEAT_ERROR("A0106", "emby注册失败，可能是用户名已存在"),
    /**
     * 二级宏观错误码，音乐服已经注册
     */
    USER_MUSIC_REGISTER_ERROR("A0107", "存在音乐服账户，无法重复注册"),

    /**
     *
     * 二级宏观错误码，用户注册错误
     */
    USER_REGISTER_EMBY_ERROR("A0102", "未注册Emby"),
    /**
     * 二级宏观错误码，用户注册错误
     */
    USER_REGISTER_NAVIDROME_ERROR("A0103", "未注册Navidrome"),
    /**
     * 用户未同意隐私协议
     */
    USER_NO_AGREE_PRIVATE_ERROR("A0101", "用户未同意隐私协议"),

    /**
     * 注册国家或地区受限
     */
    USER_REGISTER_AREA_LIMIT_ERROR("A0102", "注册国家或地区受限"),

    /**
     * 用户验证码错误
     */
    USER_VERIFY_CODE_ERROR("A0240", "用户验证码错误"),

    /**
     * 用户名已存在
     */
    USER_NAME_EXIST("A0111", "用户名已存在"),

    /**
     * 用户账号不存在
     */
    USER_ACCOUNT_NOT_EXIST("A0201", "用户账号不存在"),

    /**
     * 用户密码错误
     */
    USER_PASSWORD_ERROR("A0210", "用户密码错误"),
    /**
     * navidrome用户更新密码错误
     */
    USER_PASSWORD_NAVIDROME_ERROR("A0211", "navidrome用户更新密码错误"),
    /**
     * navidrome用户删除错误
     */
    USER_DELETE_NAVIDROME_ERROR("A0212", "navidrome用户删除错误"),
    /**
     * 二级宏观错误码，用户请求参数错误
     */
    USER_REQUEST_PARAM_ERROR("A0400", "用户请求参数错误"),

    /**
     * 用户登录已过期
     */
    USER_LOGIN_EXPIRED("A0230", "用户登录已过期"),

    /**
     * 访问未授权
     */
    USER_UN_AUTH("A0301", "访问未授权"),

    /**
     * 用户请求服务异常
     */
    USER_REQ_EXCEPTION("A0500", "用户请求服务异常"),

    /**
     * 请求超出限制
     */
    USER_REQ_MANY("A0501", "请求超出限制"),

    /**
     * 用户上传文件异常
     */
    USER_UPLOAD_FILE_ERROR("A0700", "用户上传文件异常"),

    /**
     * 用户上传文件类型不匹配
     */
    USER_UPLOAD_FILE_TYPE_NOT_MATCH("A0701", "用户上传文件类型不匹配"),

    /**
     * 一级宏观错误码，系统执行出错
     */
    SYSTEM_ERROR("B0001", "系统执行出错"),

    /**
     * 二级宏观错误码，系统执行超时
     */
    SYSTEM_TIMEOUT_ERROR("B0100", "系统执行超时"),
    /**
     *  音乐已存在，无法重复下载
     */
    MUISC_DOWN_ERROR("D0100", "音乐已存在，无法重复下载"),
    /**
     *  音乐下载今天超过5次
     */
    MUISC_DOWN_DATE_ERROR("D0101", "音乐下载今天超过5次"),

    /**
     * 一级宏观错误码，调用第三方服务出错
     */
    THIRD_SERVICE_ERROR("C0001", "调用第三方服务出错"),

    /**
     * 一级宏观错误码，中间件服务出错
     */
    MIDDLEWARE_SERVICE_ERROR("C0100", "中间件服务出错");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 中文描述
     */
    private final String message;

}
