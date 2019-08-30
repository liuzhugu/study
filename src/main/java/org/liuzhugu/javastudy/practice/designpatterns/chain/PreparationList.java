package org.liuzhugu.javastudy.practice.designpatterns.chain;

public class PreparationList {

    /**
     * 是否洗脸
     */
    private boolean washFace;

    /**
     * 是否刷牙
     */
    private boolean brushTeeth;

    /**
     * 是否吃早餐
     */
    private boolean haveBreakfast;

    /**
     * 是否吃早餐
     */
    private boolean sayHello;


    public boolean isSayHello() {
        return sayHello;
    }

    public void setSayHello(boolean sayHello) {
        this.sayHello = sayHello;
    }

    public boolean isWashFace() {
        return washFace;
    }

    public void setWashFace(boolean washFace) {
        this.washFace = washFace;
    }



    public boolean isHaveBreakfast() {
        return haveBreakfast;
    }

    public void setHaveBreakfast(boolean haveBreakfast) {
        this.haveBreakfast = haveBreakfast;
    }

    public boolean isBrushTeeth() {
        return brushTeeth;
    }

    public void setBrushTeeth(boolean brushTeeth) {
        this.brushTeeth = brushTeeth;
    }

    @Override
    public String toString() {
        return "ThingList [washFace=" + washFace + ", brushTeeth=" + brushTeeth + ", haveBreakfast=" + haveBreakfast + "]";
    }

}
