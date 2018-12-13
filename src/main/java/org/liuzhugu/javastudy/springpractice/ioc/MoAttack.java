package org.liuzhugu.javastudy.springpractice.ioc;

public class MoAttack implements ActorArrangable{
    public MoAttack(){}


    //3.由导演决定演员往电影里塞,在1中由演员决定角色的控制权,到3的由导演决定控制权
    //某一接口具体实现类的选择控制权从调用类中移除,转交给第三方决定,更具体点说就是依赖注入
    //也就是说让调用类对某一接口的实现类的依赖关系由第三方注入
    private GeLi geLi;

    //注入方式
    //1.构造方法注入
    public MoAttack(GeLi geLi){
        this.geLi=geLi;
    }
    //2.setter方法注入
    public void setGeLi(GeLi geLi){
        this.geLi=geLi;
    }
    //3.接口注入
    //声明一个专门用来注入的方法


    @Override
    public void injectGeli(GeLi geLi) {
        this.geLi=geLi;
    }

    public void CityGateAsk(){
        //1.演员侵入剧本
        //LiuDeHua ldh=new LiuDeHua();
        //ldh.responseAsk("墨者革离");

        //2.演员与角色隔离
        //GeLi geLi=new LiuDeHua();
        //geLi.responseAsk("墨者革离");

        //3.演员直到拍戏才注入
        geLi.responseAsk("墨者革离");
    }
}
