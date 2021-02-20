package org.liuzhugu.javastudy.framestudy.mybatis.mylike2;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;


import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlSessionFactoryBuilder {
   public SqlSessionFactory build(Reader reader) {
      SAXReader saxReader = new SAXReader();
      try {
         //2.解析XML 生成Document
         Document document = saxReader.read(new InputSource(reader));
         Element root = document.getRootElement();
         //3.根据Document生成Configuration
         Configuration configuration = parseConfiguration(root);
         //4.根据Configuration设置SqlSessionFactory
            //4.1 根据Configuration配置数据源
            //4.2 从数据源中获取连接
            //4.2 为所有mapper建立映射
         return new DefaultSqlSessionFactory(configuration);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   private Configuration parseConfiguration(Element root) {
      Configuration configuration = new Configuration();
      configuration.setDataSource(dataSource(root.selectNodes("//dataSource")));
      configuration.setConnection(connection(configuration.getDataSource()));
      configuration.setMappers(mappers(root.selectNodes("//mappers")));
      return configuration;
   }

   private Map<String, String> dataSource(List<Element> list) {
      Map<String, String> dataSource = new HashMap<>(4);
      Element element = list.get(0);
      List content = element.content();
      for (Object o : content) {
         Element ele = (Element) o;
         String name = ele.attributeValue("name");
         String value = ele.attributeValue("value");
         dataSource.put(name,value);
      }
      return dataSource;
   }

   private Connection connection(Map<String, String> dataSource) {
      try {
         //先加载驱动类
         Class.forName(dataSource.get("driver"));
         //建立连接
         return DriverManager.getConnection(dataSource.get("url"),dataSource.get("username")
            ,dataSource.get("password"));
      } catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   private Map<String,XNode> mappers(List<Element> list) {
      Map<String,XNode> mappers = new HashMap<>();
      Element element = list.get(0);
      List content = element.content();
      for (Object o : content) {
         Element ele = (Element) o;
         //mapper下面是mapper文件路径  因此相当于换个文件源继续解析
         String resource = ele.attributeValue("resource");
         try {
            Reader reader = Resources.getResourceAsReader(resource);
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(reader);
            Element root = document.getRootElement();
            //命名空间
            String nameSpace = root.attributeValue("namespace");
            //暂时只处理该命名空间下的select语句
            List<Element> selectNodes = root.selectNodes("select");
            //提取每个select语句的配置
            for(Element selectNode : selectNodes) {
               String id = selectNode.attributeValue("id");
               String parameterType = selectNode.attributeValue("parameterType");
               String resultType = selectNode.attributeValue("resultType");
               String sql = selectNode.getText();

               Map<Integer, String> parameter = new HashMap<>();
               //处理 #字段名 到 占位符的映射关系   然后当获得参数时根据该映射关系组装sql
               Pattern pattern = Pattern.compile("(#\\{(.*?)})");
               Matcher matcher = pattern.matcher(sql);
               int index = 1;
               while (matcher.find()) {
                  //位置
                  String g1 = matcher.group(1);
                  //字段名
                  String g2 = matcher.group(2);
                  parameter.put(index,g2);
                  //原来的 #字段名 替换为 ?
                  sql = sql.replace(g1,"?");
                  index ++;
               }
               XNode xNode = new XNode();
               xNode.setNameSpace(nameSpace);
               xNode.setId(id);
               xNode.setParameterType(parameterType);
               xNode.setParameter(parameter);
               xNode.setResultType(resultType);
               xNode.setSql(sql);
               //命名空间 + id 作为XNode的唯一标识
               String key = nameSpace + "." + id;
               mappers.put(key,xNode);
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      return mappers;
   }
}
