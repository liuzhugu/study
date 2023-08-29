package org.liuzhugu.javastudy.course.designpattern.objectoriented;

public class Ostrich implements Tweetable,EggLayable {
    private Tweetablity tweetablity = new Tweetablity();   //组合
    private EggLayAbility eggLayAbility = new EggLayAbility(); //组合

    @Override
    public void layEgg() {
        //委托
        eggLayAbility.layEgg();
    }

    @Override
    public void tweet() {
        //委托
        tweetablity.tweet();
    }
}
