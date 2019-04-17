package org.liuzhugu.javastudy.book.springinaction.chapter2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

//创建上下文
@RunWith(SpringJUnit4ClassRunner.class)
//加载第1步:上下文使用CDPlayerConfig来加载配置
@ContextConfiguration(classes = CDPlayerConfig.class)
public class CDPlayerTest {

    @Autowired
    private CompactDisc cd;

    @Autowired
    private CDPlayer cdPlayer;

    @Test
    public void cdShouldNotBeNull() {
        assertNotNull(cd);
    }

    @Test
    public void cdIsSet() {
        cdPlayer.run();
    }
}
