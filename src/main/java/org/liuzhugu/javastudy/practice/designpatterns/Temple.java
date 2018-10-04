package org.liuzhugu.javastudy.practice.designpatterns;

/**
 * Created by liuting6 on 2018/2/27.
 * 模板模式
 */
abstract class Client{
    public void templeMethod(){
        before();
        appetite();
        after();
    }
    //钩子方法(回调函数),在盛饭前
    protected void before(){}
    //抽象方法,告诉服务员饭量
    public  abstract void appetite();
    //具体方法,吃饭后
    private void after(){
        System.out.println("拿筷子，找桌子，开吃...");
    }
}
class Restaurant{
    //打饭方法
    public void dozenRice(Client client){
        client.templeMethod();
    }
}
public class Temple {
    public static void main(String[] args){
        Restaurant waiter=new Restaurant();
        //因为只执行一次，所以可以用匿名内部类
        //把执行实体传进来，然后回调其钩子方法，从而调用者可以根据自己需要灵活
        // 定义实际执行的实体，而不会只能执行被调用者事先完全写死的实现，
        // 然后又可以通过模板方法对执行实体加以一定限制
        waiter.dozenRice(new Client() {
            //在模板方法中虽然方法执行顺序不可以改变，但其中的步骤可以灵活订制
            @Override
            protected void before(){
                System.out.println("对服务员吹胡子瞪眼！");
            }
            @Override
            public void appetite() {
                System.out.println("盛了一锅米饭");
            }
        });
    }
}
