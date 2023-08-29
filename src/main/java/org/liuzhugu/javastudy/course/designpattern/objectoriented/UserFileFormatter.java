package org.liuzhugu.javastudy.course.designpattern.objectoriented;

import java.util.ArrayList;
import java.util.List;

/**
 * 面向对象将任务按模型分成一个个类  通过类之间的交互来共同完成任务   各个类之间平等而独立   适合网状结构
 * 而面向过程想算法一样  按步骤完成所有任务  有先后顺序  单线模式  只适合线性
 * */
public class UserFileFormatter {

    public void format(String userFile,String formattedUserFile) {
        //Open files..
        String userText = "";
        List<User> users = new ArrayList<>();
        while (true) {
            //read until file is empty
            //read from file into userText...
            User user = User.praseFrom(userText);
            users.add(user);
            boolean emptyFlag = true;
            if (emptyFlag) {
                break;
            }
        }

        for (int i = 0;i < users.size();i ++) {
            String formattedUserText = users.get(i).formatToText();
            //write to new file...
        }
        //close files...
    }
}
