package org.liuzhugu.javastudy.course.ruyuanconcurrent.masterslave;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class HotCommodityTest {
    public static void main(String[] args) throws IOException {
        HotCommodityAnalysisMaster master = new HotCommodityAnalysisMaster("D:\\study\\javastudy\\src\\main\\java\\org\\liuzhugu\\javastudy\\course\\ruyuanconcurrent\\masterslave\\");
        BufferedReader  fileNameReader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(master.getLogFileRootPath() + "filename.log")));
        ConcurrentMap<Long, AtomicInteger> result = master.analysisHotCommodity(fileNameReader);

        //正常是每隔一段时间去统计一次统计出来的数据
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //统计的商品数量
        for (long commodityId : result.keySet()) {
            System.out.println(commodityId + " ==> " + result.get(commodityId).get());
        }
    }
}
