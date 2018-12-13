package org.liuzhugu.javastudy.book.logicjava.chapter10;


import java.util.*;

class Spec{

    String size;

    String color;

    public Spec(String size,String color){
        this.size=size;
        this.color=color;
    }

    @Override
    public String toString() {
        return "[size="+size+",color="+color+"]";
    }

    //

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.color.hashCode();
        result = 31 * result + this.size.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj==this){
            return true;
        }

        if(obj instanceof Spec){
            Spec spec=(Spec)obj;
            if(spec.size.equals(this.size)&&spec.color.equals(this.color)){
                return true;
            }
        }

        return false;
    }
}

public class HashMapStudy {
    public static void main(String[] args){

        //count();

        //HashMap的key可以为null
//        HashMap<Integer,Integer> hashMap=new HashMap<>();
//        hashMap.put(null,1);
//        System.out.println(hashMap.get(null));
        //Hashtable的key不可以为null
//        Hashtable<Integer,Integer> hashtable=new Hashtable<>();
//        hashtable.put(null,1);

        //需要自定义equals，否则不是同一个对象都认为不相等
        Set<Spec> specs=new HashSet<>();
        specs.add(new Spec("L","RED"));
        specs.add(new Spec("L","RED"));
        Iterator<Spec> iterator=specs.iterator();
        while(iterator.hasNext()){
            Spec spec=iterator.next();
            System.out.println(spec.toString());
        }


    }

    private static void count(){
        Random random=new Random();
        Map<Integer,Integer> countMap=new HashMap<>();
        for(int i=0;i<1000;i++){
            int num=random.nextInt(4);
            Integer count=countMap.get(num);
            if(count==null){
                countMap.put(num,1);
            }else {
                countMap.put(num,count+1);
            }
        }

        for(Map.Entry<Integer,Integer> entry:countMap.entrySet()){
            System.out.println("key:"+entry.getKey()+" and value:"+entry.getValue());
        }
    }
}
