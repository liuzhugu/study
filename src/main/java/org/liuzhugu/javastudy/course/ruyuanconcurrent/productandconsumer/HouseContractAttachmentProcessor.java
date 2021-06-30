package org.liuzhugu.javastudy.course.ruyuanconcurrent.productandconsumer;

import java.util.concurrent.ArrayBlockingQueue;

public class HouseContractAttachmentProcessor {
    public static void main(String[] args) {
        HouseContractAttachmentProcessor processor = new HouseContractAttachmentProcessor();
        long start = System.currentTimeMillis();
        for (int i = 0;i < 20;i ++) {
            //processor.synUploadHouseContraAttachment(new HouseContractFile("附件" + i));
            processor.asynUploadHouseContraAttachment(new HouseContractFile("附件" + i));
        }
        System.out.println("共耗时:" + (System.currentTimeMillis() - start));
        while (true) {

        }
    }

    /**
     * 同步 处理用户上传电子合同
     * @param houseContractFile 电子合同附件
     * */
    public void synUploadHouseContraAttachment(HouseContractFile houseContractFile) {
        //存储业务数据
        saveBizData(houseContractFile);
        //分析电子合同附件
        saveAttachmentFile(houseContractFile);
        //分析附件并生成索引
        createIndex(houseContractFile);
    }

    /**
     * 异步 处理用户上传电子合同
     * @param houseContractFile 电子合同附件
     * */
    public void asynUploadHouseContraAttachment(HouseContractFile houseContractFile) {
        //存储业务数据
        saveBizData(houseContractFile);
        //分析电子合同附件
        saveAttachmentFile(houseContractFile);
        //异步分析附件并生成索引
        pushTask(houseContractFile);
    }

    //任务队列
    private ArrayBlockingQueue<HouseContractFile> blockingQueue;
    //消费线程
    private IndexingThread indexingThread;
    public HouseContractAttachmentProcessor() {
        //容量为200的任务队列
        blockingQueue = new ArrayBlockingQueue<>(200);
        indexingThread = new IndexingThread(blockingQueue);
        //启动消费者线程
        indexingThread.start();
    }

    private void pushTask(HouseContractFile houseContractFile) {
        try {
            blockingQueue.put(houseContractFile);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveBizData(HouseContractFile houseContractFile) {
        System.out.println("=== " + houseContractFile.getName() + " 存储业务数据完成 ===");
    }

    private void saveAttachmentFile(HouseContractFile houseContractFile) {
        System.out.println("=== " + houseContractFile.getName() + " 存储业务数据完成 ===");
    }

    private void  createIndex(HouseContractFile houseContractFile) {
        try {
            //耗时两分钟
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("=== " + houseContractFile.getName() + " 分析附件并生产索引完成 ===");
    }

    class IndexingThread extends Thread {
        //任务队列
        private ArrayBlockingQueue<HouseContractFile> blockingQueue;
        public IndexingThread(ArrayBlockingQueue<HouseContractFile> blockingQueue) {
            this.blockingQueue = blockingQueue;
        }

        @Override
        public void run() {
            try {
                //死循环   不断从任务队列中取数据处理
                while (true) {
                    HouseContractFile houseContractFile = blockingQueue.take();
                    //分析附件并生成索引
                    createIndex(houseContractFile);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class HouseContractFile {
        private String name;
        public HouseContractFile(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
