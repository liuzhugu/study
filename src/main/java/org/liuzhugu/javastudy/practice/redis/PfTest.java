package org.liuzhugu.javastudy.practice.redis;

import java.util.concurrent.ThreadLocalRandom;

public class PfTest {

    static class BitKeeper{
        private int maxbits;

        public void random(){
            long value= ThreadLocalRandom.current().nextLong(2L<<32);
            int bits =
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
}
