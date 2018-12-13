package org.liuzhugu.javastudy.book.effectivejava;


/**
 *  15.使可变性最小
 * */
public class Complex {

    /**
     *  私有并且不提供setter方法，那么外部就方法通过访问来修改
     *  声明final和并且在初始化时就赋值来使其不可改变
     * */
    private final double re;
    private final double im;
    public Complex(double re,double im){
        this.re=re;
        this.im=im;
    }

    public Complex add(Complex c){
        return new Complex(this.re+c.re,this.im+c.im);
    }

    public Complex subtract(Complex c){
        return new Complex(this.re-c.re,this.im-c.im);
    }

    public Complex multiply(Complex c){
        return new Complex(this.re*c.re,this.im*c.im);
    }

    public Complex divide(Complex c){
        double tmp=c.re*c.re+c.im*c.im;
        return new Complex((this.re*c.re+this.im*c.im)/tmp,(this.im*c.re+this.re*c.im)/tmp);
    }

    @Override
    public boolean equals(Object obj) {
        //引用相同，说明是同一个，那么肯定相等
        if(obj==this){
            return true;
        }
        //判断类型
        if(!(obj instanceof Complex)){
            return false;
        }
        //判断了类型之后就可以强制类型转换了
        Complex c=(Complex)obj;
        return Double.compare(this.re,c.re)==0&&
                Double.compare(this.im,c.im)==0;
    }

    //重写了equals的话那么hashcode也要重写
    //这样的话，equal中比较的字段如果没发生变化，那么hashcode也不会发生变化
    //所有基于散列的集合中存储的元素是该类的时候，可能无法正常工作
    @Override
    public int hashCode() {
        int result = 17+hashDouble(this.re);
        result = 31*result+hashDouble(this.im);
        return result;
    }

    private int hashDouble(double value){
        long longBits=Double.doubleToLongBits(re);
        return (int)(longBits^(longBits>>>32));
    }


    @Override
    public String toString() {
        return "("+re+"+"+im+"i)";
    }
}
