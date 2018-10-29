package org.liuzhugu.javastudy.practice.work;


public class Test {
    public static void main(String[] args) {
        String str="19年11月";
        String[] strs=str.split("年");
        System.out.println(strs[1]);
    }

    public int binarySearch(int[] nums,int goal){
        int index=-1;

        if(nums==null||nums.length==0){
            return index;
        }
        int start=0,end=nums.length-1;
        while(start<=end){
            int middle=(start+end)/2;
            if(nums[middle]<goal){
                start=middle+1;
            }else if(nums[middle]==goal){
                index=middle;
                break;
            }else {
                end=middle-1;
            }

        }
        return index;
    }


}
