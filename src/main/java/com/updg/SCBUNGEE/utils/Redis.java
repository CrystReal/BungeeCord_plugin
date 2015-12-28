package com.updg.SCBUNGEE.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Created by Alex
 * Date: 28.01.14  14:47
 */
public class Redis {
    private static JedisPool pool;

    public Redis(String server, int port) {
        pool = new JedisPool(server, port);
    }

    private static Jedis getJedis() {
        Jedis j = pool.getResource();
        j.select(10);
        return j;
    }

    public static String get(String s) {
        Jedis j = getJedis();
        String o = null;
        try {
            o = j.get(s);
        } catch (JedisConnectionException e) {
            if (null != j) {
                pool.returnBrokenResource(j);
                j = null;
            }
        } finally {
            if (null != j)
                pool.returnResource(j);
        }
        return o;
    }

    public static String set(String s, String val) {
        Jedis j = getJedis();
        String o = null;
        try {
            o = j.set(s, val);
        } catch (JedisConnectionException e) {
            if (null != j) {
                pool.returnBrokenResource(j);
                j = null;
            }
        } finally {
            if (null != j)
                pool.returnResource(j);
        }
        return o;
    }

    public static boolean exists(String s) {
        Jedis j = getJedis();
        boolean o = false;
        try {
            o = j.exists(s);
        } catch (JedisConnectionException e) {
            if (null != j) {
                pool.returnBrokenResource(j);
                j = null;
            }
        } finally {
            if (null != j)
                pool.returnResource(j);
        }
        return o;
    }

    public static long del(String s) {
        Jedis j = getJedis();
        long o = 0;
        try {
            o = j.del(s);
        } catch (JedisConnectionException e) {
            if (null != j) {
                pool.returnBrokenResource(j);
                j = null;
            }
        } finally {
            if (null != j)
                pool.returnResource(j);
        }
        return o;
    }
}
