package org.liuzhugu.javastudy.practice.work;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
    public static void main(String[] args) throws Exception{
        int start=1014;
        int count=14;
        for(int i=1;i<count;i++){
            String str="INSERT INTO `template_attribute_config` (`id`,`template_id`,`attribute_id`,`depth`,`parent_id`,`add_time`," +
                    "`update_time`,`op_uid`,`op_name`,`attr_config_type`,`attr_is_required`,`sort_order`,`is_book_attr`,`using_state`,`show_flag`,`is_spec_attr`,`del_flag`) " +
                    "VALUES ("+(start+i)+","+i+",18,2,132,'2019-01-05 09:32:16','2019-01-05 09:32:16',21900,'刘挺',2,0,8000,0,1,1,0,0);";
            System.out.println(str);
        }
    }




}
