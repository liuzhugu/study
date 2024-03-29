package org.liuzhugu.javastudy.sourcecode.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanMetadataAttribute;
import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanNameReference;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.parsing.*;
import org.springframework.beans.factory.support.*;
import org.springframework.beans.factory.support.ManagedArray;
import org.springframework.beans.factory.xml.DocumentDefaultsDefinition;
import org.springframework.core.env.Environment;
import org.springframework.util.*;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

public class BeanDefinitionParserDelegate {
    public static final String BEANS_NAMESPACE_URI = "http://www.springframework.org/schema/beans";
    public static final String MULTI_VALUE_ATTRIBUTE_DELIMITERS = ",; ";
    public static final String TRUE_VALUE = "true";
    public static final String FALSE_VALUE = "false";
    public static final String DEFAULT_VALUE = "default";
    public static final String DESCRIPTION_ELEMENT = "description";
    public static final String AUTOWIRE_NO_VALUE = "no";
    public static final String AUTOWIRE_BY_NAME_VALUE = "byName";
    public static final String AUTOWIRE_BY_TYPE_VALUE = "byType";
    public static final String AUTOWIRE_CONSTRUCTOR_VALUE = "constructor";
    public static final String AUTOWIRE_AUTODETECT_VALUE = "autodetect";
    public static final String DEPENDENCY_CHECK_ALL_ATTRIBUTE_VALUE = "all";
    public static final String DEPENDENCY_CHECK_SIMPLE_ATTRIBUTE_VALUE = "simple";
    public static final String DEPENDENCY_CHECK_OBJECTS_ATTRIBUTE_VALUE = "objects";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String BEAN_ELEMENT = "bean";
    public static final String META_ELEMENT = "meta";
    public static final String ID_ATTRIBUTE = "id";
    public static final String PARENT_ATTRIBUTE = "parent";
    public static final String CLASS_ATTRIBUTE = "class";
    public static final String ABSTRACT_ATTRIBUTE = "abstract";
    public static final String SCOPE_ATTRIBUTE = "scope";
    private static final String SINGLETON_ATTRIBUTE = "singleton";
    public static final String LAZY_INIT_ATTRIBUTE = "lazy-init";
    public static final String AUTOWIRE_ATTRIBUTE = "autowire";
    public static final String AUTOWIRE_CANDIDATE_ATTRIBUTE = "autowire-candidate";
    public static final String PRIMARY_ATTRIBUTE = "primary";
    public static final String DEPENDENCY_CHECK_ATTRIBUTE = "dependency-check";
    public static final String DEPENDS_ON_ATTRIBUTE = "depends-on";
    public static final String INIT_METHOD_ATTRIBUTE = "init-method";
    public static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";
    public static final String FACTORY_METHOD_ATTRIBUTE = "factory-method";
    public static final String FACTORY_BEAN_ATTRIBUTE = "factory-bean";
    public static final String CONSTRUCTOR_ARG_ELEMENT = "constructor-arg";
    public static final String INDEX_ATTRIBUTE = "index";
    public static final String TYPE_ATTRIBUTE = "type";
    public static final String VALUE_TYPE_ATTRIBUTE = "value-type";
    public static final String KEY_TYPE_ATTRIBUTE = "key-type";
    public static final String PROPERTY_ELEMENT = "property";
    public static final String REF_ATTRIBUTE = "ref";
    public static final String VALUE_ATTRIBUTE = "value";
    public static final String LOOKUP_METHOD_ELEMENT = "lookup-method";
    public static final String REPLACED_METHOD_ELEMENT = "replaced-method";
    public static final String REPLACER_ATTRIBUTE = "replacer";
    public static final String ARG_TYPE_ELEMENT = "arg-type";
    public static final String ARG_TYPE_MATCH_ATTRIBUTE = "match";
    public static final String REF_ELEMENT = "ref";
    public static final String IDREF_ELEMENT = "idref";
    public static final String BEAN_REF_ATTRIBUTE = "bean";
    public static final String LOCAL_REF_ATTRIBUTE = "local";
    public static final String PARENT_REF_ATTRIBUTE = "parent";
    public static final String VALUE_ELEMENT = "value";
    public static final String NULL_ELEMENT = "null";
    public static final String ARRAY_ELEMENT = "array";
    public static final String LIST_ELEMENT = "list";
    public static final String SET_ELEMENT = "set";
    public static final String MAP_ELEMENT = "map";
    public static final String ENTRY_ELEMENT = "entry";
    public static final String KEY_ELEMENT = "key";
    public static final String KEY_ATTRIBUTE = "key";
    public static final String KEY_REF_ATTRIBUTE = "key-ref";
    public static final String VALUE_REF_ATTRIBUTE = "value-ref";
    public static final String PROPS_ELEMENT = "props";
    public static final String PROP_ELEMENT = "prop";
    public static final String MERGE_ATTRIBUTE = "merge";
    public static final String QUALIFIER_ELEMENT = "qualifier";
    public static final String QUALIFIER_ATTRIBUTE_ELEMENT = "attribute";
    public static final String DEFAULT_LAZY_INIT_ATTRIBUTE = "default-lazy-init";
    public static final String DEFAULT_MERGE_ATTRIBUTE = "default-merge";
    public static final String DEFAULT_AUTOWIRE_ATTRIBUTE = "default-autowire";
    public static final String DEFAULT_DEPENDENCY_CHECK_ATTRIBUTE = "default-dependency-check";
    public static final String DEFAULT_AUTOWIRE_CANDIDATES_ATTRIBUTE = "default-autowire-candidates";
    public static final String DEFAULT_INIT_METHOD_ATTRIBUTE = "default-init-method";
    public static final String DEFAULT_DESTROY_METHOD_ATTRIBUTE = "default-destroy-method";
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final XmlReaderContext readerContext;
    private final DocumentDefaultsDefinition defaults = new DocumentDefaultsDefinition();
    private final ParseState parseState = new ParseState();
    private final Set<String> usedNames = new HashSet();

    public BeanDefinitionParserDelegate(XmlReaderContext readerContext) {
        Assert.notNull(readerContext, "XmlReaderContext must not be null");
        this.readerContext = readerContext;
    }

    public final XmlReaderContext getReaderContext() {
        return this.readerContext;
    }

    /** @deprecated */
    @Deprecated
    public final Environment getEnvironment() {
        return this.readerContext.getEnvironment();
    }

    protected Object extractSource(Element ele) {
        return this.readerContext.extractSource(ele);
    }

    protected void error(String message, Node source) {
        this.readerContext.error(message, source, this.parseState.snapshot());
    }

    protected void error(String message, Element source) {
        this.readerContext.error(message, source, this.parseState.snapshot());
    }

    protected void error(String message, Element source, Throwable cause) {
        this.readerContext.error(message, source, this.parseState.snapshot(), cause);
    }

    public void initDefaults(Element root) {
        this.initDefaults(root, (BeanDefinitionParserDelegate)null);
    }

    public void initDefaults(Element root, BeanDefinitionParserDelegate parent) {
        this.populateDefaults(this.defaults, parent != null ? parent.defaults : null, root);
        this.readerContext.fireDefaultsRegistered(this.defaults);
    }

    protected void populateDefaults(DocumentDefaultsDefinition defaults, DocumentDefaultsDefinition parentDefaults, Element root) {
        String lazyInit = root.getAttribute("default-lazy-init");
        if ("default".equals(lazyInit)) {
            lazyInit = parentDefaults != null ? parentDefaults.getLazyInit() : "false";
        }

        defaults.setLazyInit(lazyInit);
        String merge = root.getAttribute("default-merge");
        if ("default".equals(merge)) {
            merge = parentDefaults != null ? parentDefaults.getMerge() : "false";
        }

        defaults.setMerge(merge);
        String autowire = root.getAttribute("default-autowire");
        if ("default".equals(autowire)) {
            autowire = parentDefaults != null ? parentDefaults.getAutowire() : "no";
        }

        defaults.setAutowire(autowire);
        defaults.setDependencyCheck(root.getAttribute("default-dependency-check"));
        if (root.hasAttribute("default-autowire-candidates")) {
            defaults.setAutowireCandidates(root.getAttribute("default-autowire-candidates"));
        } else if (parentDefaults != null) {
            defaults.setAutowireCandidates(parentDefaults.getAutowireCandidates());
        }

        if (root.hasAttribute("default-init-method")) {
            defaults.setInitMethod(root.getAttribute("default-init-method"));
        } else if (parentDefaults != null) {
            defaults.setInitMethod(parentDefaults.getInitMethod());
        }

        if (root.hasAttribute("default-destroy-method")) {
            defaults.setDestroyMethod(root.getAttribute("default-destroy-method"));
        } else if (parentDefaults != null) {
            defaults.setDestroyMethod(parentDefaults.getDestroyMethod());
        }

        defaults.setSource(this.readerContext.extractSource(root));
    }

    public DocumentDefaultsDefinition getDefaults() {
        return this.defaults;
    }

    public BeanDefinitionDefaults getBeanDefinitionDefaults() {
        BeanDefinitionDefaults bdd = new BeanDefinitionDefaults();
        bdd.setLazyInit("TRUE".equalsIgnoreCase(this.defaults.getLazyInit()));
        bdd.setDependencyCheck(this.getDependencyCheck("default"));
        bdd.setAutowireMode(this.getAutowireMode("default"));
        bdd.setInitMethodName(this.defaults.getInitMethod());
        bdd.setDestroyMethodName(this.defaults.getDestroyMethod());
        return bdd;
    }

    public String[] getAutowireCandidatePatterns() {
        String candidatePattern = this.defaults.getAutowireCandidates();
        return candidatePattern != null ? StringUtils.commaDelimitedListToStringArray(candidatePattern) : null;
    }

    public BeanDefinitionHolder parseBeanDefinitionElement(Element ele) {
        return this.parseBeanDefinitionElement(ele, (BeanDefinition)null);
    }

    public BeanDefinitionHolder parseBeanDefinitionElement(Element ele, BeanDefinition containingBean) {
        String id = ele.getAttribute("id");
        String nameAttr = ele.getAttribute("name");
        List<String> aliases = new ArrayList();
        if (StringUtils.hasLength(nameAttr)) {
            String[] nameArr = StringUtils.tokenizeToStringArray(nameAttr, ",; ");
            aliases.addAll(Arrays.asList(nameArr));
        }

        String beanName = id;
        if (!StringUtils.hasText(id) && !aliases.isEmpty()) {
            beanName = (String)aliases.remove(0);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("No XML 'id' specified - using '" + beanName + "' as bean name and " + aliases + " as aliases");
            }
        }

        if (containingBean == null) {
            this.checkNameUniqueness(beanName, aliases, ele);
        }

        AbstractBeanDefinition beanDefinition = this.parseBeanDefinitionElement(ele, beanName, containingBean);
        if (beanDefinition != null) {
            if (!StringUtils.hasText(beanName)) {
                try {
                    if (containingBean != null) {
                        beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, this.readerContext.getRegistry(), true);
                    } else {
                        beanName = this.readerContext.generateBeanName(beanDefinition);
                        String beanClassName = beanDefinition.getBeanClassName();
                        if (beanClassName != null && beanName.startsWith(beanClassName) && beanName.length() > beanClassName.length() && !this.readerContext.getRegistry().isBeanNameInUse(beanClassName)) {
                            aliases.add(beanClassName);
                        }
                    }

                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Neither XML 'id' nor 'name' specified - using generated bean name [" + beanName + "]");
                    }
                } catch (Exception var9) {
                    this.error(var9.getMessage(), ele);
                    return null;
                }
            }

            String[] aliasesArray = StringUtils.toStringArray(aliases);
            return new BeanDefinitionHolder(beanDefinition, beanName, aliasesArray);
        } else {
            return null;
        }
    }

    protected void checkNameUniqueness(String beanName, List<String> aliases, Element beanElement) {
        String foundName = null;
        if (StringUtils.hasText(beanName) && this.usedNames.contains(beanName)) {
            foundName = beanName;
        }

        if (foundName == null) {
            foundName = (String) CollectionUtils.findFirstMatch(this.usedNames, aliases);
        }

        if (foundName != null) {
            this.error("Bean name '" + foundName + "' is already used in this <beans> element", beanElement);
        }

        this.usedNames.add(beanName);
        this.usedNames.addAll(aliases);
    }

    public AbstractBeanDefinition parseBeanDefinitionElement(Element ele, String beanName, BeanDefinition containingBean) {
        this.parseState.push(new BeanEntry(beanName));
        String className = null;
        if (ele.hasAttribute("class")) {
            className = ele.getAttribute("class").trim();
        }

        try {
            String parent = null;
            if (ele.hasAttribute("parent")) {
                parent = ele.getAttribute("parent");
            }

            AbstractBeanDefinition bd = this.createBeanDefinition(className, parent);
            this.parseBeanDefinitionAttributes(ele, beanName, containingBean, bd);
            bd.setDescription(DomUtils.getChildElementValueByTagName(ele, "description"));
            this.parseMetaElements(ele, bd);
            this.parseLookupOverrideSubElements(ele, bd.getMethodOverrides());
            this.parseReplacedMethodSubElements(ele, bd.getMethodOverrides());
            this.parseConstructorArgElements(ele, bd);
            this.parsePropertyElements(ele, bd);
            this.parseQualifierElements(ele, bd);
            bd.setResource(this.readerContext.getResource());
            bd.setSource(this.extractSource(ele));
            AbstractBeanDefinition var7 = bd;
            return var7;
        } catch (ClassNotFoundException var13) {
            this.error("Bean class [" + className + "] not found", ele, var13);
        } catch (NoClassDefFoundError var14) {
            this.error("Class that bean class [" + className + "] depends on not found", ele, var14);
        } catch (Throwable var15) {
            this.error("Unexpected failure during bean definition parsing", ele, var15);
        } finally {
            this.parseState.pop();
        }

        return null;
    }

    public AbstractBeanDefinition parseBeanDefinitionAttributes(Element ele, String beanName, BeanDefinition containingBean, AbstractBeanDefinition bd) {
        if (ele.hasAttribute("singleton")) {
            this.error("Old 1.x 'singleton' attribute in use - upgrade to 'scope' declaration", ele);
        } else if (ele.hasAttribute("scope")) {
            bd.setScope(ele.getAttribute("scope"));
        } else if (containingBean != null) {
            bd.setScope(containingBean.getScope());
        }

        if (ele.hasAttribute("abstract")) {
            bd.setAbstract("true".equals(ele.getAttribute("abstract")));
        }

        String lazyInit = ele.getAttribute("lazy-init");
        if ("default".equals(lazyInit)) {
            lazyInit = this.defaults.getLazyInit();
        }

        bd.setLazyInit("true".equals(lazyInit));
        String autowire = ele.getAttribute("autowire");
        bd.setAutowireMode(this.getAutowireMode(autowire));
        String dependencyCheck = ele.getAttribute("dependency-check");
        bd.setDependencyCheck(this.getDependencyCheck(dependencyCheck));
        String autowireCandidate;
        if (ele.hasAttribute("depends-on")) {
            autowireCandidate = ele.getAttribute("depends-on");
            bd.setDependsOn(StringUtils.tokenizeToStringArray(autowireCandidate, ",; "));
        }

        autowireCandidate = ele.getAttribute("autowire-candidate");
        String destroyMethodName;
        if (!"".equals(autowireCandidate) && !"default".equals(autowireCandidate)) {
            bd.setAutowireCandidate("true".equals(autowireCandidate));
        } else {
            destroyMethodName = this.defaults.getAutowireCandidates();
            if (destroyMethodName != null) {
                String[] patterns = StringUtils.commaDelimitedListToStringArray(destroyMethodName);
                bd.setAutowireCandidate(PatternMatchUtils.simpleMatch(patterns, beanName));
            }
        }

        if (ele.hasAttribute("primary")) {
            bd.setPrimary("true".equals(ele.getAttribute("primary")));
        }

        if (ele.hasAttribute("init-method")) {
            destroyMethodName = ele.getAttribute("init-method");
            if (!"".equals(destroyMethodName)) {
                bd.setInitMethodName(destroyMethodName);
            }
        } else if (this.defaults.getInitMethod() != null) {
            bd.setInitMethodName(this.defaults.getInitMethod());
            bd.setEnforceInitMethod(false);
        }

        if (ele.hasAttribute("destroy-method")) {
            destroyMethodName = ele.getAttribute("destroy-method");
            bd.setDestroyMethodName(destroyMethodName);
        } else if (this.defaults.getDestroyMethod() != null) {
            bd.setDestroyMethodName(this.defaults.getDestroyMethod());
            bd.setEnforceDestroyMethod(false);
        }

        if (ele.hasAttribute("factory-method")) {
            bd.setFactoryMethodName(ele.getAttribute("factory-method"));
        }

        if (ele.hasAttribute("factory-bean")) {
            bd.setFactoryBeanName(ele.getAttribute("factory-bean"));
        }

        return bd;
    }

    protected AbstractBeanDefinition createBeanDefinition(String className, String parentName) throws ClassNotFoundException {
        return BeanDefinitionReaderUtils.createBeanDefinition(parentName, className, this.readerContext.getBeanClassLoader());
    }

    public void parseMetaElements(Element ele, BeanMetadataAttributeAccessor attributeAccessor) {
        NodeList nl = ele.getChildNodes();

        for(int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (this.isCandidateElement(node) && this.nodeNameEquals(node, "meta")) {
                Element metaElement = (Element)node;
                String key = metaElement.getAttribute("key");
                String value = metaElement.getAttribute("value");
                BeanMetadataAttribute attribute = new BeanMetadataAttribute(key, value);
                attribute.setSource(this.extractSource(metaElement));
                attributeAccessor.addMetadataAttribute(attribute);
            }
        }

    }

    public int getAutowireMode(String attValue) {
        String att = attValue;
        if ("default".equals(attValue)) {
            att = this.defaults.getAutowire();
        }

        int autowire = 0;
        if ("byName".equals(att)) {
            autowire = 1;
        } else if ("byType".equals(att)) {
            autowire = 2;
        } else if ("constructor".equals(att)) {
            autowire = 3;
        } else if ("autodetect".equals(att)) {
            autowire = 4;
        }

        return autowire;
    }

    public int getDependencyCheck(String attValue) {
        String att = attValue;
        if ("default".equals(attValue)) {
            att = this.defaults.getDependencyCheck();
        }

        if ("all".equals(att)) {
            return 3;
        } else if ("objects".equals(att)) {
            return 1;
        } else {
            return "simple".equals(att) ? 2 : 0;
        }
    }

    public void parseConstructorArgElements(Element beanEle, BeanDefinition bd) {
        NodeList nl = beanEle.getChildNodes();

        for(int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (this.isCandidateElement(node) && this.nodeNameEquals(node, "constructor-arg")) {
                this.parseConstructorArgElement((Element)node, bd);
            }
        }

    }

    public void parsePropertyElements(Element beanEle, BeanDefinition bd) {
        NodeList nl = beanEle.getChildNodes();

        for(int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (this.isCandidateElement(node) && this.nodeNameEquals(node, "property")) {
                this.parsePropertyElement((Element)node, bd);
            }
        }

    }

    public void parseQualifierElements(Element beanEle, AbstractBeanDefinition bd) {
        NodeList nl = beanEle.getChildNodes();

        for(int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (this.isCandidateElement(node) && this.nodeNameEquals(node, "qualifier")) {
                this.parseQualifierElement((Element)node, bd);
            }
        }

    }

    public void parseLookupOverrideSubElements(Element beanEle,MethodOverrides overrides) {
        NodeList nl = beanEle.getChildNodes();

        for(int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (this.isCandidateElement(node) && this.nodeNameEquals(node, "lookup-method")) {
                Element ele = (Element)node;
                String methodName = ele.getAttribute("name");
                String beanRef = ele.getAttribute("bean");
                LookupOverride override = new LookupOverride(methodName, beanRef);
                override.setSource(this.extractSource(ele));
                overrides.addOverride(override);
            }
        }

    }

    public void parseReplacedMethodSubElements(Element beanEle, MethodOverrides overrides) {
        NodeList nl = beanEle.getChildNodes();

        for(int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (this.isCandidateElement(node) && this.nodeNameEquals(node, "replaced-method")) {
                Element replacedMethodEle = (Element)node;
                String name = replacedMethodEle.getAttribute("name");
                String callback = replacedMethodEle.getAttribute("replacer");
                ReplaceOverride replaceOverride = new ReplaceOverride(name, callback);
                List<Element> argTypeEles = DomUtils.getChildElementsByTagName(replacedMethodEle, "arg-type");
                Iterator var11 = argTypeEles.iterator();

                while(var11.hasNext()) {
                    Element argTypeEle = (Element)var11.next();
                    String match = argTypeEle.getAttribute("match");
                    match = StringUtils.hasText(match) ? match : DomUtils.getTextValue(argTypeEle);
                    if (StringUtils.hasText(match)) {
                        replaceOverride.addTypeIdentifier(match);
                    }
                }

                replaceOverride.setSource(this.extractSource(replacedMethodEle));
                overrides.addOverride(replaceOverride);
            }
        }

    }

    public void parseConstructorArgElement(Element ele, BeanDefinition bd) {
        String indexAttr = ele.getAttribute("index");
        String typeAttr = ele.getAttribute("type");
        String nameAttr = ele.getAttribute("name");
        if (StringUtils.hasLength(indexAttr)) {
            try {
                int index = Integer.parseInt(indexAttr);
                if (index < 0) {
                    this.error("'index' cannot be lower than 0", ele);
                } else {
                    try {
                        this.parseState.push(new ConstructorArgumentEntry(index));
                        Object value = this.parsePropertyValue(ele, bd, (String)null);
                        ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
                        if (StringUtils.hasLength(typeAttr)) {
                            valueHolder.setType(typeAttr);
                        }

                        if (StringUtils.hasLength(nameAttr)) {
                            valueHolder.setName(nameAttr);
                        }

                        valueHolder.setSource(this.extractSource(ele));
                        if (bd.getConstructorArgumentValues().hasIndexedArgumentValue(index)) {
                            this.error("Ambiguous constructor-arg entries for index " + index, ele);
                        } else {
                            bd.getConstructorArgumentValues().addIndexedArgumentValue(index, valueHolder);
                        }
                    } finally {
                        this.parseState.pop();
                    }
                }
            } catch (NumberFormatException var19) {
                this.error("Attribute 'index' of tag 'constructor-arg' must be an integer", ele);
            }
        } else {
            try {
                this.parseState.push(new ConstructorArgumentEntry());
                Object value = this.parsePropertyValue(ele, bd, (String)null);
                ConstructorArgumentValues.ValueHolder valueHolder = new ConstructorArgumentValues.ValueHolder(value);
                if (StringUtils.hasLength(typeAttr)) {
                    valueHolder.setType(typeAttr);
                }

                if (StringUtils.hasLength(nameAttr)) {
                    valueHolder.setName(nameAttr);
                }

                valueHolder.setSource(this.extractSource(ele));
                bd.getConstructorArgumentValues().addGenericArgumentValue(valueHolder);
            } finally {
                this.parseState.pop();
            }
        }

    }

    public void parsePropertyElement(Element ele, BeanDefinition bd) {
        String propertyName = ele.getAttribute("name");
        if (!StringUtils.hasLength(propertyName)) {
            this.error("Tag 'property' must have a 'name' attribute", ele);
        } else {
            this.parseState.push(new PropertyEntry(propertyName));

            try {
                if (!bd.getPropertyValues().contains(propertyName)) {
                    Object val = this.parsePropertyValue(ele, bd, propertyName);
                    PropertyValue pv = new PropertyValue(propertyName, val);
                    this.parseMetaElements(ele, pv);
                    pv.setSource(this.extractSource(ele));
                    bd.getPropertyValues().addPropertyValue(pv);
                    return;
                }

                this.error("Multiple 'property' definitions for property '" + propertyName + "'", ele);
            } finally {
                this.parseState.pop();
            }

        }
    }

    public void parseQualifierElement(Element ele, AbstractBeanDefinition bd) {
        String typeName = ele.getAttribute("type");
        if (!StringUtils.hasLength(typeName)) {
            this.error("Tag 'qualifier' must have a 'type' attribute", ele);
        } else {
            this.parseState.push(new QualifierEntry(typeName));

            try {
                AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(typeName);
                qualifier.setSource(this.extractSource(ele));
                String value = ele.getAttribute("value");
                if (StringUtils.hasLength(value)) {
                    qualifier.setAttribute("value", value);
                }

                NodeList nl = ele.getChildNodes();

                for(int i = 0; i < nl.getLength(); ++i) {
                    Node node = nl.item(i);
                    if (this.isCandidateElement(node) && this.nodeNameEquals(node, "attribute")) {
                        Element attributeEle = (Element)node;
                        String attributeName = attributeEle.getAttribute("key");
                        String attributeValue = attributeEle.getAttribute("value");
                        if (!StringUtils.hasLength(attributeName) || !StringUtils.hasLength(attributeValue)) {
                            this.error("Qualifier 'attribute' tag must have a 'name' and 'value'", attributeEle);
                            return;
                        }

                        BeanMetadataAttribute attribute = new BeanMetadataAttribute(attributeName, attributeValue);
                        attribute.setSource(this.extractSource(attributeEle));
                        qualifier.addMetadataAttribute(attribute);
                    }
                }

                bd.addQualifier(qualifier);
            } finally {
                this.parseState.pop();
            }
        }
    }

    public Object parsePropertyValue(Element ele, BeanDefinition bd, String propertyName) {
        String elementName = propertyName != null ? "<property> element for property '" + propertyName + "'" : "<constructor-arg> element";
        NodeList nl = ele.getChildNodes();
        Element subElement = null;

        for(int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (node instanceof Element && !this.nodeNameEquals(node, "description") && !this.nodeNameEquals(node, "meta")) {
                if (subElement != null) {
                    this.error(elementName + " must not contain more than one sub-element", ele);
                } else {
                    subElement = (Element)node;
                }
            }
        }

        boolean hasRefAttribute = ele.hasAttribute("ref");
        boolean hasValueAttribute = ele.hasAttribute("value");
        if (hasRefAttribute && hasValueAttribute || (hasRefAttribute || hasValueAttribute) && subElement != null) {
            this.error(elementName + " is only allowed to contain either 'ref' attribute OR 'value' attribute OR sub-element", ele);
        }

        if (hasRefAttribute) {
            String refName = ele.getAttribute("ref");
            if (!StringUtils.hasText(refName)) {
                this.error(elementName + " contains empty 'ref' attribute", ele);
            }

            RuntimeBeanReference ref = new RuntimeBeanReference(refName);
            ref.setSource(this.extractSource(ele));
            return ref;
        } else if (hasValueAttribute) {
            TypedStringValue valueHolder = new TypedStringValue(ele.getAttribute("value"));
            valueHolder.setSource(this.extractSource(ele));
            return valueHolder;
        } else if (subElement != null) {
            return this.parsePropertySubElement(subElement, bd);
        } else {
            this.error(elementName + " must specify a ref or value", ele);
            return null;
        }
    }

    public Object parsePropertySubElement(Element ele, BeanDefinition bd) {
        return this.parsePropertySubElement(ele, bd, (String)null);
    }

    public Object parsePropertySubElement(Element ele, BeanDefinition bd, String defaultValueType) {
        if (!this.isDefaultNamespace((Node)ele)) {
            return this.parseNestedCustomElement(ele, bd);
        } else if (this.nodeNameEquals(ele, "bean")) {
            BeanDefinitionHolder nestedBd = this.parseBeanDefinitionElement(ele, bd);
            if (nestedBd != null) {
                nestedBd = this.decorateBeanDefinitionIfRequired(ele, nestedBd, bd);
            }

            return nestedBd;
        } else if (this.nodeNameEquals(ele, "ref")) {
            String refName = ele.getAttribute("bean");
            boolean toParent = false;
            if (!StringUtils.hasLength(refName)) {
                refName = ele.getAttribute("local");
                if (!StringUtils.hasLength(refName)) {
                    refName = ele.getAttribute("parent");
                    toParent = true;
                    if (!StringUtils.hasLength(refName)) {
                        this.error("'bean', 'local' or 'parent' is required for <ref> element", ele);
                        return null;
                    }
                }
            }

            if (!StringUtils.hasText(refName)) {
                this.error("<ref> element contains empty target attribute", ele);
                return null;
            } else {
                RuntimeBeanReference ref = new RuntimeBeanReference(refName, toParent);
                ref.setSource(this.extractSource(ele));
                return ref;
            }
        } else if (this.nodeNameEquals(ele, "idref")) {
            return this.parseIdRefElement(ele);
        } else if (this.nodeNameEquals(ele, "value")) {
            return this.parseValueElement(ele, defaultValueType);
        } else if (this.nodeNameEquals(ele, "null")) {
            TypedStringValue nullHolder = new TypedStringValue((String)null);
            nullHolder.setSource(this.extractSource(ele));
            return nullHolder;
        } else if (this.nodeNameEquals(ele, "array")) {
            return this.parseArrayElement(ele, bd);
        } else if (this.nodeNameEquals(ele, "list")) {
            return this.parseListElement(ele, bd);
        } else if (this.nodeNameEquals(ele, "set")) {
            return this.parseSetElement(ele, bd);
        } else if (this.nodeNameEquals(ele, "map")) {
            return this.parseMapElement(ele, bd);
        } else if (this.nodeNameEquals(ele, "props")) {
            return this.parsePropsElement(ele);
        } else {
            this.error("Unknown property sub-element: [" + ele.getNodeName() + "]", ele);
            return null;
        }
    }

    public Object parseIdRefElement(Element ele) {
        String refName = ele.getAttribute("bean");
        if (!StringUtils.hasLength(refName)) {
            refName = ele.getAttribute("local");
            if (!StringUtils.hasLength(refName)) {
                this.error("Either 'bean' or 'local' is required for <idref> element", ele);
                return null;
            }
        }

        if (!StringUtils.hasText(refName)) {
            this.error("<idref> element contains empty target attribute", ele);
            return null;
        } else {
            RuntimeBeanNameReference ref = new RuntimeBeanNameReference(refName);
            ref.setSource(this.extractSource(ele));
            return ref;
        }
    }

    public Object parseValueElement(Element ele, String defaultTypeName) {
        String value = DomUtils.getTextValue(ele);
        String specifiedTypeName = ele.getAttribute("type");
        String typeName = specifiedTypeName;
        if (!StringUtils.hasText(specifiedTypeName)) {
            typeName = defaultTypeName;
        }

        try {
            TypedStringValue typedValue = this.buildTypedStringValue(value, typeName);
            typedValue.setSource(this.extractSource(ele));
            typedValue.setSpecifiedTypeName(specifiedTypeName);
            return typedValue;
        } catch (ClassNotFoundException var7) {
            this.error("Type class [" + typeName + "] not found for <value> element", ele, var7);
            return value;
        }
    }

    protected TypedStringValue buildTypedStringValue(String value, String targetTypeName) throws ClassNotFoundException {
        ClassLoader classLoader = this.readerContext.getBeanClassLoader();
        TypedStringValue typedValue;
        if (!StringUtils.hasText(targetTypeName)) {
            typedValue = new TypedStringValue(value);
        } else if (classLoader != null) {
            Class<?> targetType = ClassUtils.forName(targetTypeName, classLoader);
            typedValue = new TypedStringValue(value, targetType);
        } else {
            typedValue = new TypedStringValue(value, targetTypeName);
        }

        return typedValue;
    }

    public Object parseArrayElement(Element arrayEle, BeanDefinition bd) {
        String elementType = arrayEle.getAttribute("value-type");
        NodeList nl = arrayEle.getChildNodes();
        org.springframework.beans.factory.support.ManagedArray target = new ManagedArray(elementType, nl.getLength());
        target.setSource(this.extractSource(arrayEle));
        target.setElementTypeName(elementType);
        target.setMergeEnabled(this.parseMergeAttribute(arrayEle));
        this.parseCollectionElements(nl, target, bd, elementType);
        return target;
    }

    public List<Object> parseListElement(Element collectionEle, BeanDefinition bd) {
        String defaultElementType = collectionEle.getAttribute("value-type");
        NodeList nl = collectionEle.getChildNodes();
        ManagedList<Object> target = new ManagedList(nl.getLength());
        target.setSource(this.extractSource(collectionEle));
        target.setElementTypeName(defaultElementType);
        target.setMergeEnabled(this.parseMergeAttribute(collectionEle));
        this.parseCollectionElements(nl, target, bd, defaultElementType);
        return target;
    }

    public Set<Object> parseSetElement(Element collectionEle, BeanDefinition bd) {
        String defaultElementType = collectionEle.getAttribute("value-type");
        NodeList nl = collectionEle.getChildNodes();
        ManagedSet<Object> target = new ManagedSet(nl.getLength());
        target.setSource(this.extractSource(collectionEle));
        target.setElementTypeName(defaultElementType);
        target.setMergeEnabled(this.parseMergeAttribute(collectionEle));
        this.parseCollectionElements(nl, target, bd, defaultElementType);
        return target;
    }

    protected void parseCollectionElements(NodeList elementNodes, Collection<Object> target, BeanDefinition bd, String defaultElementType) {
        for(int i = 0; i < elementNodes.getLength(); ++i) {
            Node node = elementNodes.item(i);
            if (node instanceof Element && !this.nodeNameEquals(node, "description")) {
                target.add(this.parsePropertySubElement((Element)node, bd, defaultElementType));
            }
        }

    }

    public Map<Object, Object> parseMapElement(Element mapEle, BeanDefinition bd) {
        String defaultKeyType = mapEle.getAttribute("key-type");
        String defaultValueType = mapEle.getAttribute("value-type");
        List<Element> entryEles = DomUtils.getChildElementsByTagName(mapEle, "entry");
        ManagedMap<Object, Object> map = new ManagedMap(entryEles.size());
        map.setSource(this.extractSource(mapEle));
        map.setKeyTypeName(defaultKeyType);
        map.setValueTypeName(defaultValueType);
        map.setMergeEnabled(this.parseMergeAttribute(mapEle));

        Object key;
        Object value;
        for(Iterator var7 = entryEles.iterator(); var7.hasNext(); map.put(key, value)) {
            Element entryEle = (Element)var7.next();
            NodeList entrySubNodes = entryEle.getChildNodes();
            Element keyEle = null;
            Element valueEle = null;

            for(int j = 0; j < entrySubNodes.getLength(); ++j) {
                Node node = entrySubNodes.item(j);
                if (node instanceof Element) {
                    Element candidateEle = (Element)node;
                    if (this.nodeNameEquals(candidateEle, "key")) {
                        if (keyEle != null) {
                            this.error("<entry> element is only allowed to contain one <key> sub-element", entryEle);
                        } else {
                            keyEle = candidateEle;
                        }
                    } else if (!this.nodeNameEquals(candidateEle, "description")) {
                        if (valueEle != null) {
                            this.error("<entry> element must not contain more than one value sub-element", entryEle);
                        } else {
                            valueEle = candidateEle;
                        }
                    }
                }
            }

            key = null;
            boolean hasKeyAttribute = entryEle.hasAttribute("key");
            boolean hasKeyRefAttribute = entryEle.hasAttribute("key-ref");
            if (hasKeyAttribute && hasKeyRefAttribute || (hasKeyAttribute || hasKeyRefAttribute) && keyEle != null) {
                this.error("<entry> element is only allowed to contain either a 'key' attribute OR a 'key-ref' attribute OR a <key> sub-element", entryEle);
            }

            if (hasKeyAttribute) {
                key = this.buildTypedStringValueForMap(entryEle.getAttribute("key"), defaultKeyType, entryEle);
            } else if (hasKeyRefAttribute) {
                String refName = entryEle.getAttribute("key-ref");
                if (!StringUtils.hasText(refName)) {
                    this.error("<entry> element contains empty 'key-ref' attribute", entryEle);
                }

                RuntimeBeanReference ref = new RuntimeBeanReference(refName);
                ref.setSource(this.extractSource(entryEle));
                key = ref;
            } else if (keyEle != null) {
                key = this.parseKeyElement(keyEle, bd, defaultKeyType);
            } else {
                this.error("<entry> element must specify a key", entryEle);
            }

            value = null;
            boolean hasValueAttribute = entryEle.hasAttribute("value");
            boolean hasValueRefAttribute = entryEle.hasAttribute("value-ref");
            boolean hasValueTypeAttribute = entryEle.hasAttribute("value-type");
            if (hasValueAttribute && hasValueRefAttribute || (hasValueAttribute || hasValueRefAttribute) && valueEle != null) {
                this.error("<entry> element is only allowed to contain either 'value' attribute OR 'value-ref' attribute OR <value> sub-element", entryEle);
            }

            if (hasValueTypeAttribute && hasValueRefAttribute || hasValueTypeAttribute && !hasValueAttribute || hasValueTypeAttribute && valueEle != null) {
                this.error("<entry> element is only allowed to contain a 'value-type' attribute when it has a 'value' attribute", entryEle);
            }

            String refName;
            if (hasValueAttribute) {
                refName = entryEle.getAttribute("value-type");
                if (!StringUtils.hasText(refName)) {
                    refName = defaultValueType;
                }

                value = this.buildTypedStringValueForMap(entryEle.getAttribute("value"), refName, entryEle);
            } else if (hasValueRefAttribute) {
                refName = entryEle.getAttribute("value-ref");
                if (!StringUtils.hasText(refName)) {
                    this.error("<entry> element contains empty 'value-ref' attribute", entryEle);
                }

                RuntimeBeanReference ref = new RuntimeBeanReference(refName);
                ref.setSource(this.extractSource(entryEle));
                value = ref;
            } else if (valueEle != null) {
                value = this.parsePropertySubElement(valueEle, bd, defaultValueType);
            } else {
                this.error("<entry> element must specify a value", entryEle);
            }
        }

        return map;
    }

    protected final Object buildTypedStringValueForMap(String value, String defaultTypeName, Element entryEle) {
        try {
            TypedStringValue typedValue = this.buildTypedStringValue(value, defaultTypeName);
            typedValue.setSource(this.extractSource(entryEle));
            return typedValue;
        } catch (ClassNotFoundException var5) {
            this.error("Type class [" + defaultTypeName + "] not found for Map key/value type", entryEle, var5);
            return value;
        }
    }

    protected Object parseKeyElement(Element keyEle, BeanDefinition bd, String defaultKeyTypeName) {
        NodeList nl = keyEle.getChildNodes();
        Element subElement = null;

        for(int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                if (subElement != null) {
                    this.error("<key> element must not contain more than one value sub-element", keyEle);
                } else {
                    subElement = (Element)node;
                }
            }
        }

        return this.parsePropertySubElement(subElement, bd, defaultKeyTypeName);
    }

    public Properties parsePropsElement(Element propsEle) {
        ManagedProperties props = new ManagedProperties();
        props.setSource(this.extractSource(propsEle));
        props.setMergeEnabled(this.parseMergeAttribute(propsEle));
        List<Element> propEles = DomUtils.getChildElementsByTagName(propsEle, "prop");
        Iterator var4 = propEles.iterator();

        while(var4.hasNext()) {
            Element propEle = (Element)var4.next();
            String key = propEle.getAttribute("key");
            String value = DomUtils.getTextValue(propEle).trim();
            TypedStringValue keyHolder = new TypedStringValue(key);
            keyHolder.setSource(this.extractSource(propEle));
            TypedStringValue valueHolder = new TypedStringValue(value);
            valueHolder.setSource(this.extractSource(propEle));
            props.put(keyHolder, valueHolder);
        }

        return props;
    }

    public boolean parseMergeAttribute(Element collectionElement) {
        String value = collectionElement.getAttribute("merge");
        if ("default".equals(value)) {
            value = this.defaults.getMerge();
        }

        return "true".equals(value);
    }

    public BeanDefinition parseCustomElement(Element ele) {
        return this.parseCustomElement(ele, (BeanDefinition)null);
    }

    public BeanDefinition parseCustomElement(Element ele, BeanDefinition containingBd) {
        String namespaceUri = this.getNamespaceURI(ele);
        NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
        if (handler == null) {
            this.error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", ele);
            return null;
        } else {
            return handler.parse(ele, new ParserContext(this.readerContext, this, containingBd));
        }
    }

    public BeanDefinitionHolder decorateBeanDefinitionIfRequired(Element ele, BeanDefinitionHolder definitionHolder) {
        return this.decorateBeanDefinitionIfRequired(ele, definitionHolder, (BeanDefinition)null);
    }

    public BeanDefinitionHolder decorateBeanDefinitionIfRequired(Element ele, BeanDefinitionHolder definitionHolder, BeanDefinition containingBd) {
        BeanDefinitionHolder finalDefinition = definitionHolder;
        NamedNodeMap attributes = ele.getAttributes();

        for(int i = 0; i < attributes.getLength(); ++i) {
            Node node = attributes.item(i);
            finalDefinition = this.decorateIfRequired(node, finalDefinition, containingBd);
        }

        NodeList children = ele.getChildNodes();

        for(int i = 0; i < children.getLength(); ++i) {
            Node node = children.item(i);
            if (node.getNodeType() == 1) {
                finalDefinition = this.decorateIfRequired(node, finalDefinition, containingBd);
            }
        }

        return finalDefinition;
    }

    public BeanDefinitionHolder decorateIfRequired(Node node, BeanDefinitionHolder originalDef, BeanDefinition containingBd) {
        String namespaceUri = this.getNamespaceURI(node);
        if (!this.isDefaultNamespace(namespaceUri)) {
            NamespaceHandler handler = this.readerContext.getNamespaceHandlerResolver().resolve(namespaceUri);
            if (handler != null) {
                return handler.decorate(node, originalDef, new ParserContext(this.readerContext, this, containingBd));
            }

            if (namespaceUri != null && namespaceUri.startsWith("http://www.springframework.org/")) {
                this.error("Unable to locate Spring NamespaceHandler for XML schema namespace [" + namespaceUri + "]", node);
            } else if (this.logger.isDebugEnabled()) {
                this.logger.debug("No Spring NamespaceHandler found for XML schema namespace [" + namespaceUri + "]");
            }
        }

        return originalDef;
    }

    private BeanDefinitionHolder parseNestedCustomElement(Element ele, BeanDefinition containingBd) {
        BeanDefinition innerDefinition = this.parseCustomElement(ele, containingBd);
        if (innerDefinition == null) {
            this.error("Incorrect usage of element '" + ele.getNodeName() + "' in a nested manner. This tag cannot be used nested inside <property>.", ele);
            return null;
        } else {
            String id = ele.getNodeName() + "#" + ObjectUtils.getIdentityHexString(innerDefinition);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using generated bean name [" + id + "] for nested custom element '" + ele.getNodeName() + "'");
            }

            return new BeanDefinitionHolder(innerDefinition, id);
        }
    }

    public String getNamespaceURI(Node node) {
        return node.getNamespaceURI();
    }

    public String getLocalName(Node node) {
        return node.getLocalName();
    }

    public boolean nodeNameEquals(Node node, String desiredName) {
        return desiredName.equals(node.getNodeName()) || desiredName.equals(this.getLocalName(node));
    }

    public boolean isDefaultNamespace(String namespaceUri) {
        return !StringUtils.hasLength(namespaceUri) || "http://www.springframework.org/schema/beans".equals(namespaceUri);
    }

    public boolean isDefaultNamespace(Node node) {
        return this.isDefaultNamespace(this.getNamespaceURI(node));
    }

    private boolean isCandidateElement(Node node) {
        return node instanceof Element && (this.isDefaultNamespace(node) || !this.isDefaultNamespace(node.getParentNode()));
    }
}
