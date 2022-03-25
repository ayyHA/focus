package com.focus.auth.common.model;

public class SysConstant {
    // key(in hash) hk:url hv:roles
    public final static String OAUTH_URLS="oauth2:oauth_urls";
    // 角色前缀，加上前缀后为：ROLE_admin、ROLE_user，目前还没有权限管理所以都放行
    public final static String ROLE_PREFIX="ROLE_";
    public final static String ROLE_ADMIN_CODE = "ROLE_admin";
    public final static String ROLE_USER_CODE = "ROLE_user";
    // 客户端配置
    public final static String CLIENT_ID = "ayyHA";
    public final static String CLIENT_SECRET = "123456";
    // 密码模式
    public final static String GRANT_TYPE_PASSWORD = "password";

}
