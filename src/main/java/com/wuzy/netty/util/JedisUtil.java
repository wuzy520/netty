package com.wuzy.netty.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by wuzhengyun on 16/7/17.
 */
public class JedisUtil {

    private static JedisPool jedisPool;

    static {
        jedisPool = new JedisPool("localhost",6379);
    }

    public Jedis getJedis(){
        return jedisPool.getResource();
    }

    public void set(byte[] key,byte[] value){
        Jedis jedis = getJedis();
        jedis.set(key,value);
        jedis.close();
    }

    public byte[] get(byte[] key){
        Jedis jedis = getJedis();
        byte[] val = jedis.get(key);
        jedis.close();
        return val;
    }

    public void del(byte[] key){
        Jedis jedis = getJedis();
        jedis.del(key);
        jedis.close();
    }
}
