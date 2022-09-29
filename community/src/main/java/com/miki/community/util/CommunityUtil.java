package com.miki.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    //生成随机字符串
    public static String generateUUID(){

        return UUID.randomUUID().toString().replace("-","");
    }

    //MD5加密，只能加密不能解密
    //hello———>abc123def456
    //hello +3e5b8f(数据库里的salt)——>abc123def456abc,更随机，不易破解
    public static String md5(String key){
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

}
