package org.liuzhugu.javastudy.course.ruyuanconcurrent.dataanalysissystem;

public class DataAnalysisSystemTest {

    public static void main(String[] args) throws InterruptedException {
        DataAnalysisSystem dataAnalysisSystem = new DataAnalysisSystem();
        dataAnalysisSystem.init();

        dataAnalysisSystem.exportFile("123.excel");
        dataAnalysisSystem.exportFile("456.excel");
        dataAnalysisSystem.exportFile("789.excel");

        Thread.sleep(100);
    }
}