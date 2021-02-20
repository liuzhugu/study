package org.liuzhugu.javastudy.framestudy.mybatis.mylike;

import org.dom4j.Document;
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

    public SqlSessionFactory build(Reader reader) {
        SAXReader saxReader = new SAXReader();
        try {
            //解析XML  生成Document
            Document document = saxReader.read(new InputSource(reader));
            Element root = document.getRootElement();
            //解析document生成配置
            Configuration configaration = parseConfiguration(root);
            //根据配置配置SqlSessionFactory
            return new DefaultSqlSessionFactory(configaration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Configuration parseConfiguration(Element root) {
        Configuration configuration = new Configuration();
        //从document中获取数据源配置
        configuration.setDataSouce(dataSource(root.selectNodes("//dataSource")));
        //根据配置获取连接
        configuration.setConnection(connection(configuration.getDataSouce()));
        //保存mapper到map中
        configuration.setMappers(mapperElement(root.selectNodes("mappers")));
        return configuration;
    }

    private Map<String,String> dataSource(List<Element> list) {
        Map<String,String> dataSource = new HashMap<>(4);
        Element element = list.get(0);
        List content = element.content();
        for (Object o : content) {
            Element ele = (Element) o;
            String value = ele.attributeValue("value");
            String name = ele.attributeValue("name");
            dataSource.put(name,value);
        }
        return dataSource;
    }
    private Connection connection(Map<String,String> dataSource) {
        try {
            //先加载驱动类
            Class.forName(dataSource.get("driver"));
            //建立连接
            return DriverManager.getConnection(dataSource.get("url"),
                    dataSource.get("username"),dataSource.get("password"));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    private Map<String,XNode> mapperElement(List<Element> list) {
        Map<String,XNode> mappers = new HashMap<>();
        Element element = list.get(0);
        List content = element.content();
        //解析所有sql语句
        for (Object o : content) {
            Element ele = (Element) o;
            //mapper下面是mapper文件路径  因此需要继续解析
            String resource = ele.attributeValue("resource");
            try {
                Reader reader = Resources.getResourceAsReader(resource);
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(new InputSource(reader));
                Element root = document.getRootElement();
                //命名空间
                String nameSpace = root.attributeValue("namespace");
                //暂时只处理select
                List<Element> selectNodes = root.selectNodes("select");
                //提取每个select语句的id 参数   返回值  以及sql标签中的内容
                for (Element selectNode : selectNodes) {
                    String id = selectNode.attributeValue("id");
                    String parameterType = selectNode.attributeValue("parameterType");
                    String resultType = selectNode.attributeValue("resultType");
                    String sql = selectNode.getText();

                    //将原来sql中的注入 #{字段名} 改为sql的 ?
                    // 并记住位置 在组装参数时将每个字段放到该有的位置去
                    Map<Integer, String> parameter = new HashMap<>();
                    //取出  #{字段名} 格式
                    Pattern pattern = Pattern.compile("(#\\{(.*?)})");

                    Matcher matcher = pattern.matcher(sql);
                    int index = 1;
                    while (matcher.find()) {
                        String g1 = matcher.group(1);
                        String g2 = matcher.group(2);
                        parameter.put(index,g2);
                        //将原来的 #{字段名} 替换成 ?
                        sql = sql.replace(g1,"?");
                        index++;
                    }

                    XNode xNode = new XNode();
                    xNode.setNameSpace(nameSpace);
                    xNode.setId(id);
                    xNode.setParameterType(parameterType);
                    xNode.setParameter(parameter);
                    xNode.setResultType(resultType);
                    xNode.setSql(sql);
                    //将select保存到map中  key为 命名空间 + id来唯一确定
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
