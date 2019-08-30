package com.ljtao3.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Resource;

@Service("myRedisPool")
@Slf4j
public class MyRedisPool {
    @Resource(name="shardedJedisPool")
    private ShardedJedisPool shardedJedisPool;
    //获得一个实例
    public ShardedJedis instance(){
        return shardedJedisPool.getResource();
    }
    //安全关闭这个连接
    public void safeClose(ShardedJedis shardedJedis){
        try{
            if(shardedJedis!=null){
                shardedJedis.close();
            }
        }catch (Exception e){
            log.error("return redis resource exception",e);
        }
    }
}
