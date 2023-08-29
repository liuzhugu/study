package org.liuzhugu.javastudy.course.designpattern.objectoriented;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShoppingCart {
    //购物车中商品数量
    private int itemCount;
    //购物车中商品总价格
    private double totalPrice;
    //商品列表
    private List<ShoppingCartItem> items = new ArrayList<>();

    //只提供getter方法   不提供setter方法
    // 否则可以随意地修改这两个值  导致与商品列表不一致
    public int getItemCount() {
        return itemCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    //如果直接返回  那么返回的是一个引用  可以借由引用修改商品列表
    //因此返回一个不能修改的集合容器  但即使这样 虽然不能修改对象  却还是可以修改对象的状态
    public List<ShoppingCartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    //提供了封装逻辑后的方法  不暴露内部细节给外部
    //一方面容易维护和使用  另一方面也避免了外界的干扰  定位问题比较容易

    //清空购物车
    public void clear() {
        items.clear();
        //这两个字段的维护工作放在内部  直观容易维护
        itemCount = 0;
        totalPrice = 0.0;
    }

    //添加商品
    public void addItem(ShoppingCartItem item) {
        items.add(item);
    }

}
