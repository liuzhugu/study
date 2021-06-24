package org.liuzhugu.javastudy.course.ruyuanconcurrent.promise;

public class NoPromise {
    public static void main(String[] args) throws InterruptedException {

        long start = System.currentTimeMillis();

        Thread t = new Thread(() -> {
            System.out.println("任务1开始:烧水，需要15分钟");

            try {
                BoilWater boilWater = new BoilWater();
                Thread.sleep(15000);
                boilWater.setStatus(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("任务1结束:烧水结束，当前用时" + (System.currentTimeMillis() - start) + " ms");
        });

        //等待任务完成
        t.start();
        t.join();

        System.out.println("任务2开始:准备茶叶茶杯，需要3分钟，当前用时: " +
                (System.currentTimeMillis() - start) + " ms");


        try {
            TeaAndCup teaAndCup = new TeaAndCup();
            Thread.sleep(3000);
            teaAndCup.setStatus(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("任务2结束:茶杯茶叶结束，总共用时" + (System.currentTimeMillis() - start) + " ms");

        System.out.println("准备工作结束，开始泡茶makeTea！");
        System.out.println("总共用时: " + (System.currentTimeMillis() - start) + " ms");
    }

    static class BoilWater {
        boolean status = false;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }

    static class TeaAndCup {
        boolean status = false;

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }
    }
}
