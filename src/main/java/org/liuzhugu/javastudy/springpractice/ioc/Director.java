package org.liuzhugu.javastudy.springpractice.ioc;

//导演,作为注入依赖关系的第三方
public class Director {

    public void direct(){
        //决定扮演者,然后注入

        GeLi geLi=new LiuDeHua();

        //1.开始拍电影的时候就注入演员
        // MoAttack moAttack=new MoAttack(geLi);
        //moAttack.CityGateAsk();

        //2.到该演员戏份的时候才注入
        //MoAttack moAttack=new MoAttack();
        //先拍其他戏份
        //到革离的戏的时候才注入演员,然后继续
        //moAttack.setGeLi(geLi);
        //moAttack.CityGateAsk();


        //3.通过专门的用于注入的方法注入,需要专门实现一个用来注入的接口,不提倡
        MoAttack moAttack=new MoAttack();
        moAttack.injectGeli(geLi);
        moAttack.CityGateAsk();
    }
}
