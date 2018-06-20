package com.mmall.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @title 本地缓存设置
 * @Author huangjiarui
 * @date: 2018-06-20
 */
public class TokenCache {

    public static final String TOKEN_PREFIX = "token_";

    private static Logger  logger = LoggerFactory.getLogger(TokenCache.class);

    //LRU算法
    private static LoadingCache<String,String> localCache = CacheBuilder
            .newBuilder()//构建本地cache
            .initialCapacity(1000)//设置缓存的初始化容量
            .maximumSize(100000)//设置缓存的最大容量当超过最大值时使用LRU算法
            .expireAfterAccess(12, TimeUnit.HOURS)//12小时过期
            .build(new CacheLoader<String, String>() {
                //默认的数据加载实现，当调用get取值,没有key,就用这个方法进行加载
                @Override
                public String load(String s) throws Exception {
                    return "null";//直接返回null会报空指针，因为 null.equals()会导致
                }
            });

    public static void setKey(String key ,String value){
        localCache.put(key,value);
    }

    public static String getKey(String key){
        String value = null;

        try {
            value=localCache.get(key);
            if ("null".equals(value)){
                return null;
            }
            return value;
        }catch (Exception e){
            logger.error("localCache get error",e);
        }
        return null;
    }
}
