package org.liuzhugu.javastudy.practice.netty.study.util;

import org.liuzhugu.javastudy.practice.netty.study.common.IdTypeEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 获取分布式id
 */
public class DistributedIdUtil {
    private static final Map<Integer,Integer> ids = new HashMap<>();

    private static final int randomSeed = 1000000;

    public synchronized static int getId(IdTypeEnum idTypeEnum){
        if(ids.get(idTypeEnum.ordinal())==null){
            int initValue = new Random().nextInt(randomSeed);
            ids.put(idTypeEnum.ordinal(),initValue);
            return initValue;
        }else {
            int value = ids.get(idTypeEnum.ordinal())+1;
            ids.put(idTypeEnum.ordinal(),value);
            return value;
        }
    }

    public static void main(String[] args){
        for(int i=0;i<10;i++){
            new Thread(){
                @Override
                public void run() {
                    for(int j=0;j<10000;j++){
                        if(j%2==0){
                            System.out.println("获取到的userId为:"+DistributedIdUtil.getId(IdTypeEnum.userId));
                        }else {
                            System.out.println("获取到的sessionId为:"+DistributedIdUtil.getId(IdTypeEnum.sessionId));
                        }
                    }
                }
            }.start();
        }
    }
}
