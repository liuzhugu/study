package org.liuzhugu.javastudy.practice.redis;

import java.util.concurrent.ThreadLocalRandom;

/**
 * HyperLogLog 实现原理
 * 给定一系列的随机整数，记录下低位连续零位的最大长度k
 * 即可通过这个k值可以估算出所有随机数的数量
 */
public class PfTest {

    static class BitKeeper{
        private int maxbits;

        public void random(){
            long value= ThreadLocalRandom.current().nextLong(2L<<32);
            int bits =lowZeros(value);
            if(bits>this.maxbits){
                this.maxbits=bits;
            }
        }

        private int lowZeros(long value){
            int i=1;
            for(;i<32;i++){
                //右移之后左移，仍然相等，那说明移动的位数都是0
                if(value>>i<<i!=value){
                    break;
                }
            }
            return i-1;
        }
    }

    static class Experiment{
        private int n;
        private BitKeeper keeper;

        public Experiment(int n){
            this.n=n;
            this.keeper=new BitKeeper();
        }

        public void work(){
            for(int i=0;i<n;i++){
                this.keeper.random();
            }
        }

        public void debug(){
            //给定一系列的随机整数，记录下低位连续零位的最大长度 k
            //约等于 N=2^K
            System.out.printf("%d %.2f %d\n", this.n, Math.log(this.n) / Math.log(2), this.keeper.maxbits);
        }

    }

    public static void main(String[] args){
        for(int i=1000;i<100000;i+=100){
            Experiment exp=new Experiment(i);
            exp.work();
            exp.debug();
        }
    }
}
