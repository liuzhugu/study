package org.liuzhugu.javastudy.framestudy.mybatis.like;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlSessionFactoryBuilder {
    public DefaultSqlSessionFactory build(Reader reader) {
        SAXReader saxReader = new SAXReader();
        try {
            //解析xml生成document
            Document document = saxReader.read(new InputSource(reader));
            //根据document生成配置
            Configuration configuration = parseConfiguration(document.getRootElement());
            //根据配置设置session工厂
            return new DefaultSqlSessionFactory(configuration);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    //解析配置
    private Configuration parseConfiguration(Element root) {
        Configuration configuration = new Configuration();
        //设置数据源
        configuration.setDataSource(dataSource(root.selectNodes("//dataSource")));
        //根据数据源建立连接
        configuration.setConnection(connection(configuration.getDataSource()));
        //设置mapper的映射关系
        configuration.setMapperElement(mapperElement(root.selectNodes("mappers")));
        return configuration;
    }

    private Map<String, String> dataSource(List<Element> list) {
        Map<String, String> dataSource = new HashMap<>(4);
        Element element = list.get(0);
        List content = element.content();
        for (Object o : content) {
            Element e = (Element) o;
            String name = e.attributeValue("name");
            String value = e.attributeValue("value");
            dataSource.put(name,value);
        }
        return dataSource;
    }

    private Connection connection(Map<String, String> dataSource) {
        try {
            //判断是否能获取到驱动类
            Class.forName(dataSource.get("driver"));
            //建立连接
            return DriverManager.getConnection(dataSource.get("url"),dataSource.get("username"),
                    dataSource.get("password"));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, XNode> mapperElement(List<Element> list) {
        Map<String, XNode> map = new HashMap<>();
        Element element = list.get(0);
        List content = element.content();
        //解析所有的sql语句
        for(Object o : content) {
            Element ele = (Element) o;
            String resource = ele.attributeValue("resource");
            try {
                Reader reader = Resources.getResourceAsReader(resource);
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(new InputSource(reader));
                Element root = document.getRootElement();
                //命名空间
                String nameSpace = root.attributeValue("namespace");
                //暂时只处理SELECT
                List<Element> selectNodes = root.selectNodes("select");
                //从select语句从提取字段
                for (Element node :selectNodes) {
                    String id = node.attributeValue("id");
                    String parameterType = node.attributeValue("parameterType");
                    String resultType = node.attributeValue("resultType");
                    String sql = node.getText();
                    Map<Integer, String> parameter = new HashMap<>();
                    //找出所有 #{字段名}
                    Pattern pattern = Pattern.compile("(#\\{(.*?)})");
                    Matcher matcher = pattern.matcher(sql);
                    for (int i = 1;matcher.find();i ++) {
                        String g1 = matcher.group(1);
                        String g2 = matcher.group(2);
                        //设置参数位置和字段名的映射
                        parameter.put(i,g2);
                        //将 #{字段名} 替换为 占位符 ?
                        sql = sql.replace(g1,"?");
                    }
                    XNode xNode = new XNode();
                    xNode.setId(id);
                    xNode.setNamespace(nameSpace);
                    xNode.setParameterType(parameterType);
                    xNode.setParameter(parameter);
                    xNode.setResultType(resultType);
                    xNode.setSql(sql);
                    //将每个select放入map中  加上namespace作为key  确保唯一
                    map.put(nameSpace + "." + id,xNode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

}
