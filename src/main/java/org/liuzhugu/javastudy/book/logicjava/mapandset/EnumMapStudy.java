package org.liuzhugu.javastudy.book.logicjava.mapandset;


import lombok.Data;
import org.liuzhugu.javastudy.sourcecode.jdk8.container.EnumMap_;
import org.liuzhugu.javastudy.sourcecode.jdk8.container.Map_;

import java.util.List;

enum Size{
    SMALL,MEDIUM,LARGE
}

@Data
class Clother{
     private Size size;
     private String id;
}

public class EnumMapStudy {

    /**
     * 按枚举值统计
     * */
    public static Map_<Size,Integer> countBySize(List<Clother> clothers){
        Map_<Size,Integer> map = new EnumMap_<Size, Integer>(Size.class);
        for(Clother clother:clothers){
            if(map.containsKey(clother.getSize())){
                map.put(clother.getSize(),map.get(clother.getSize())+1);
            }else{
                map.put(clother.getSize(),1);
            }
        }
        return map;
    }
}
