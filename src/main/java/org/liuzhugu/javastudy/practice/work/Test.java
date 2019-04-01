package org.liuzhugu.javastudy.practice.work;


import java.util.*;

public class Test {
    public static void main(String[] args) throws Exception {
        //System.out.println(str.replace("\n",","));
        String s = "<br>每份支持出游人:1人\n" +
                "预订时间:最晚需在【出行前1天16:00(当地时间)】前购买，预订成功后可立即使用\n" +
                "有效期:指定出行日期当天有效。\n";
        String a = "<br>" + s.replace("\n","</br>");
        System.out.println(a);
    }

    public static void productSql(){
        int idIndex=1286,templateIndex =1,attrId=164;
        for (int i = 0;i < 13;i ++) {
            String str = "INSERT INTO `template_attribute_config` (`id`,`template_id`,`attribute_id`,`depth`,`parent_id`,`add_time`,\n" +
                    "\t`update_time`,`op_uid`,`op_name`,`attr_config_type`,`attr_is_required`,`sort_order`,`is_book_attr`,\n" +
                    "\t`using_state`,`show_flag`,`is_spec_attr`,`del_flag`) \n" +
                    "VALUES (" + (idIndex ++) + "," + (templateIndex++) + "," + attrId + ",3,47,'2019-01-05 09:32:16','2019-01-05 09:32:16',21900,'刘挺',1,0,8000,1,1,1,0,0);";
            System.out.println(str);
        }
    }

}
