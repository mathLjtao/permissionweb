package com.ljtao3.service;

import com.google.common.base.Joiner;
import com.ljtao3.beans.CacheKeyConstants;
import com.ljtao3.common.MyRedisPool;
import com.ljtao3.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;

@Service
@Slf4j
public class SysCacheService {
    @Resource(name="myRedisPool")
    private MyRedisPool redisPool;
    /*
    toSavedValue 要保存的值
    timeoutSeconds 缓存的时间
    prefix 一个前缀
     */
    public void saveCache(String toSavedValue , int timeoutSeconds, CacheKeyConstants prefix){
        saveCache(toSavedValue,timeoutSeconds,prefix,null);
    }
    public void saveCache(String toSavedValue , int timeoutSeconds, CacheKeyConstants prefix,String... keys){
        if (toSavedValue == null) {
            return ;
        }
        //取出redis连接
        ShardedJedis shardedJedis=null;
        try{
            String cacheKey = generateCacheKey(prefix, keys);
            //获取实例
            shardedJedis = redisPool.instance();
            //保存
            shardedJedis.setex(cacheKey,timeoutSeconds,toSavedValue);

        }catch (Exception e){
            log.error("save cache exception!,prefix:{},keys:{}",prefix.name(), JsonMapper.obj2String(keys));
        }
        finally {
            redisPool.safeClose(shardedJedis);
        }
    }
    //
    public String getFormCache(CacheKeyConstants prefix,String... keys){
        ShardedJedis shardedJedis=null;
        String cacheKey=generateCacheKey(prefix,keys);
        try{
            shardedJedis=redisPool.instance();
            String value=shardedJedis.get(cacheKey);
            return value;
        }catch (Exception e){
            e.printStackTrace();
            log.error("get from cache exception,prefix:{},keys:{}",prefix.name(),JsonMapper.obj2String(keys));
            return null;
        }
        finally {
            redisPool.safeClose(shardedJedis);
        }
    }
    //缓存名称
    private String generateCacheKey(CacheKeyConstants prefix,String... keys){
        String key=prefix.name();
        if(keys!=null && keys.length>0){
            key += "_"+ Joiner.on("_").join(keys);
        }
        return key;
    }
}
