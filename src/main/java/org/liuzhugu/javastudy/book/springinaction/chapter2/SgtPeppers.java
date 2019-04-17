package org.liuzhugu.javastudy.book.springinaction.chapter2;

import org.springframework.stereotype.Component;

//使用该标志 使得该实现类被扫描到
@Component
public class SgtPeppers implements CompactDisc{

    private String title = "Sgt. Peppers's lonely Hearts Club Band";
    private String artist = "The Beatles";

    @Override
    public void play() {
        System.out.println("Playing " + title + " by " + artist);
    }
}
