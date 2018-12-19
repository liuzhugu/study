package org.liuzhugu.javastudy.book.logicjava.mapandset;


public class LinkedHashMapStudy {
    //内部有一个双向链表

    public static void main(String[] args){

        //比hashmap多了顺序功能,可以把所有节点按访问顺序串成双向链表,遍历的时候会体现这种顺序
//        Map_<String,Integer> linkedHashMap=new LinkedHashMap_<>(16,0.75f,false);
//        linkedHashMap.put("a",100);
//        linkedHashMap.put("c",200);
//        linkedHashMap.put("b",300);
//        linkedHashMap.put("d",400);
//        linkedHashMap.get("a");
//        linkedHashMap.get("b");
//        linkedHashMap.get("c");
//        linkedHashMap.get("d");
//        for(HashMap_.Entry entry:linkedHashMap.entrySet()){
//            System.out.println(entry.getKey()+" "+entry.getValue());
//        }

        //使用LinkedHashMap加上淘汰策略实现容量满了之后淘汰最老的元素,即LRU
        LRUCache<String,Integer> lruCache=new LRUCache<>(3);
        lruCache.put("a",100);
        lruCache.put("c",200);
        lruCache.put("b",300);
        lruCache.get("a");
        lruCache.put("d",400);
        //c最久没被访问,删除它
        System.out.println(lruCache);

    }

}
