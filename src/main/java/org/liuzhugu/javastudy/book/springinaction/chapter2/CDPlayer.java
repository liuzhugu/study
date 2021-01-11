package org.liuzhugu.javastudy.book.springinaction.chapter2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CDPlayer {

    @Autowired
    private CompactDisc compactDisc;

    public void run() {
        compactDisc.play();
    }
}
