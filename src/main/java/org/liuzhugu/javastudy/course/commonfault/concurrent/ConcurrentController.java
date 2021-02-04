package org.liuzhugu.javastudy.course.commonfault.concurrent;


import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@RestController
@RequestMapping("/commonfault")
@Slf4j
public class ConcurrentController {
    private static final ThreadLocal<Integer> currentUser = ThreadLocal.withInitial(() -> null);

    //总元素容量
    private static final int ITEM_COUNT = 10;
    //线程总数
    private static final int THREAD_COUNT = 10;
    //循环次数
    private static final int LOOP_COUNT = 10000000;
    //总元素容量
    //private static final int ITEM_COUNT = 1000;


//    //适合大量I/O的线程池
//    private static ThreadPoolExecutor asyncCalcThreadPool = new ThreadPoolExecutor(
//            200, 200,
//            1, TimeUnit.HOURS,
//            new ArrayBlockingQueue<>(1000),
//            new ThreadFactoryBuilder().setNameFormat("asynccalc-threadpool-%d").get());
//    private Callable<Integer> calcTask() {
//        return () -> {
//            TimeUnit.MILLISECONDS.sleep(10);
//            return 1;
//        };
//    }
//    @GetMapping("right")
//    public int right() throws ExecutionException, InterruptedException {
//        return asyncCalcThreadPool.submit(calcTask()).get();
//    }

//    private static ThreadPoolExecutor batchFileProcessThreadPool = new ThreadPoolExecutor(
//            2, 2,
//            1, TimeUnit.HOURS,
//            new ArrayBlockingQueue<>(100),
//            new ThreadFactoryBuilder().setNameFormat("batchfileprocess-threadpool-%d").get(),
//            new ThreadPoolExecutor.CallerRunsPolicy());
//    @PostConstruct
//    public void init() {
//        printStats(threadPool);
//
//        //启动线程往文件写入大量数据
//        new Thread(() -> {
//            //模拟需要写入的大量数据
//            String payload = IntStream.rangeClosed(1,1_000_000)
//                    .mapToObj(__ -> "a")
//                    .collect(Collectors.joining(""));
//            while (true) {
//                batchFileProcessThreadPool.execute(() -> {
//                    try {
//                        Files.write(Paths.get("demo.txt"),
//                                Collections.singletonList(LocalTime.now().toString() + ":" + payload)
//                        ,UTF_8,CREATE,TRUNCATE_EXISTING);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    log.info("batch file processing done");
//                });
//            }
//        }).start();
//    }

    @GetMapping("/threadpool")
    public int threadPool() throws InterruptedException{
        //计数器跟踪完成的任务数
        AtomicInteger atomicInteger = new AtomicInteger();
        //创建线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                //2条核心线程 最大5条
                2,5,
                5,TimeUnit.SECONDS,
                //容量为10的阻塞队列
                new ArrayBlockingQueue<>(10),
                new ThreadFactoryBuilder().setNameFormat("demo-threadpool-%d").get(),
                //拒绝策略
                new ThreadPoolExecutor.AbortPolicy()
        );
        printStats(threadPool);
        //每隔1秒提交一次,一共提交20次任务
        IntStream.rangeClosed(1,20).forEach(i -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int id = atomicInteger.incrementAndGet();
            try {
                threadPool.submit(() -> {
                    log.info("{} started",id);
                    //每个任务耗时10s
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {

                    }
                    log.info("{} finished",id);
                });
            } catch (Exception ex) {
                //提交异常的话  打印错误信息并为计数器减1
                log.error("error submitting task {}",id,ex);
                atomicInteger.decrementAndGet();
            }
        });

        TimeUnit.SECONDS.sleep(60);
        return atomicInteger.intValue();
    }

    @GetMapping("/oom1")
    public void oom1() throws InterruptedException{
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(1);
        //打印线程池信息
        for (int i = 0;i < 100000000;i ++) {
            threadPool.execute(() -> {
                String payload = IntStream.rangeClosed(1,100000000)
                        .mapToObj(__ -> "a")
                        .collect(Collectors.joining("")) + UUID.randomUUID().toString();
                try {
                    TimeUnit.HOURS.sleep(1);
                } catch (InterruptedException e) {

                }
                log.info(payload);
            });
        }
        threadPool.shutdown();
        threadPool.awaitTermination(1,TimeUnit.HOURS);
    }


    //打印线程池信息
    private void printStats(ThreadPoolExecutor threadPool) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            log.info("=========================");
            log.info("Pool Size: {}", threadPool.getPoolSize());
            log.info("Active Threads: {}", threadPool.getActiveCount());
            log.info("Number of Tasks Completed: {}", threadPool.getCompletedTaskCount());
            log.info("Number of Tasks in Queue: {}", threadPool.getQueue().size());

            log.info("=========================");
        }, 0, 1, TimeUnit.SECONDS);
    }

    @GetMapping("/lockwrong")
    @ResponseBody
    public int lockwrong(@RequestParam(value = "count",defaultValue = "1000000") int count) {
        Data.reset();
        //synchronized修饰的是普通方法 同步的是一个实例
        //Data data = new Data();
        //IntStream.rangeClosed(1,count).parallel().forEach(i -> data.wrong());

        //但如果是不同实例的话 那么能同时调用该方法  会出现同步问题
        IntStream.rangeClosed(1,count).parallel().forEach(i -> new Data().wrong());
        return Data.getCount();
    }

    @GetMapping("/normaluse")
    @ResponseBody
    public Map<String,Long> normaluse() throws InterruptedException{
        ConcurrentHashMap<String,LongAdder> freqs = new ConcurrentHashMap<>(ITEM_COUNT);
        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        forkJoinPool.execute(() -> IntStream.rangeClosed(1, LOOP_COUNT).parallel().
                forEach(i -> {
                    String key = "item" + ThreadLocalRandom.current().nextInt(ITEM_COUNT);
                    //利用computeIfAbsent()方法来实例化LongAdder，然后利用LongAdder来进行线程安全计数
                    freqs.computeIfAbsent(key, k -> new LongAdder()).increment();

//                    //线程安全  但性能不好
//                    synchronized (freqs) {
//                    if (freqs.contains(key)) {
//                        freqs.put(key,freqs.get(key) + 1);
//                    } else {
//                        freqs.put(key,1);
//                    }
        }
        ));

        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1,TimeUnit.HOURS);
        //因为我们的Value是LongAdder而不是Long，所以需要做一次转换才能返回
        return freqs.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().longValue())
                );
    }

    //帮助方法  用来获得一个指定元素数量模拟数据的concurrentHashMap
    private ConcurrentHashMap<String,Long> getData(int count) {
        return LongStream.range(0,count)
                .boxed()
                .collect(Collectors.toConcurrentMap(i -> UUID.randomUUID().toString(),
                        Function.identity(),(o1,o2) -> o1,ConcurrentHashMap::new));
    }

    @GetMapping("/containerwrong")
    @ResponseBody
    public String containerwrong() throws InterruptedException{
        ConcurrentHashMap<String,Long> concurrentHashMap = getData(ITEM_COUNT - 100);
        //初始化900个元素
        log.info("init size:{}",concurrentHashMap.size());

        ForkJoinPool forkJoinPool = new ForkJoinPool(THREAD_COUNT);
        //使用线程池并发处理逻辑
        forkJoinPool.execute(() -> IntStream.rangeClosed(1,10).parallel().forEach((i -> {
            //整体加锁
            synchronized (concurrentHashMap) {
               //查询还要补充多少个元素
               int gap = ITEM_COUNT - concurrentHashMap.size();
               log.info("gap size:{}",gap);
               //补充元素
               concurrentHashMap.putAll(getData(gap));
           }

           //获取到的只是一个中间状态
//            int gap = ITEM_COUNT - concurrentHashMap.size();
//            log.info("gap size:{}",gap);
//            //补充元素
//            concurrentHashMap.putAll(getData(gap));
        })));
        //等待所有任务完成
        forkJoinPool.shutdown();
        forkJoinPool.awaitTermination(1, TimeUnit.HOURS);
        log.info("finish size:{}",concurrentHashMap.size());
        return "OK";
    }

    @GetMapping("/threadlocalwrong")
    @ResponseBody
    public Map threadlocalwrong(@RequestParam("userId") Integer userId) {
        //设置用户信息之前先查询一次ThreadLocal中的用户信息
        //因为线程复用  ThreadLocal中的数据可能为其他用户请求时设置的信息  而不是初始化的信息
        String before = Thread.currentThread().getName() + ":" + currentUser.get();
        //设置信息
        currentUser.set(userId);
        try {
            //设置用户信息之后再查询一次ThreadLocal中的用户信息
            String after = Thread.currentThread().getName() + ":" + currentUser.get();
            //汇总输出两次查询结果
            Map ans = new HashMap();
            ans.put("before",before);
            ans.put("after",after);
            return ans;
        } finally {
            //在finally代码块中删除ThreadLocal中的数据，确保数据不串
            currentUser.remove();
        }
    }

    @lombok.Data
    @RequiredArgsConstructor
    static  class Item {
        final String name;//商品名
        int remaining = 1000;//库存
        ReentrantLock lock = new ReentrantLock();
    }
}
