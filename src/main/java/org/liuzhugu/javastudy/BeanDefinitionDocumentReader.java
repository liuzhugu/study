package org.liuzhugu.javastudy;

import org.liuzhugu.javastudy.sourcecode.spring.XmlReaderContext;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.w3c.dom.Document;

public interface BeanDefinitionDocumentReader {
    void registerBeanDefinitions(Document var1, XmlReaderContext var2) throws BeanDefinitionStoreException;
}