package org.liuzhugu.javastudy.practice.work;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GuavaTest {
    public static void main(String[] args) {
        CacheLoader<Long,String> stringCacheLoader=new CacheLoader<Long, String>() {
            @Override
            public String load(Long aLong) throws Exception {
                String str= "UUID:"+UUID.randomUUID();
                return str;
            }
        };

        LoadingCache<Long,String> stringLoadingCache= CacheBuilder.newBuilder()
                .expireAfterAccess(2, TimeUnit.SECONDS)
                .expireAfterWrite(2,TimeUnit.SECONDS)
                .refreshAfterWrite(3,TimeUnit.SECONDS)
                .maximumSize(10000L)
                .build(stringCacheLoader);

        stringLoadingCache.put(1L,"1");
    }
}
