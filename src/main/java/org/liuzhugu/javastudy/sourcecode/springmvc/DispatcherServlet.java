package org.liuzhugu.javastudy.sourcecode.springmvc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.ui.context.ThemeSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.*;
import org.springframework.web.util.NestedServletException;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class DispatcherServlet extends FrameworkServlet {

    /**
     * 核心方法
     * */

    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (this.logger.isDebugEnabled()) {
            String resumed = WebAsyncUtils.getAsyncManager(request).hasConcurrentResult() ? " resumed" : "";
            this.logger.debug("DispatcherServlet with name '" + this.getServletName() + "'" + resumed + " processing " + request.getMethod() + " request for [" + getRequestUri(request) + "]");
        }

        Map<String, Object> attributesSnapshot = null;
//        if (WebUtils.isIncludeRequest(request)) {
//            attributesSnapshot = new HashMap();
//            Enumeration attrNames = request.getAttributeNames();
//
//            label108:
//            while(true) {
//                String attrName;
//                do {
//                    if (!attrNames.hasMoreElements()) {
//                        break label108;
//                    }
//
//                    attrName = (String)attrNames.nextElement();
//                } while(!this.cleanupAfterInclude && !attrName.startsWith("org.springframework.web.servlet"));
//
//                attributesSnapshot.put(attrName, request.getAttribute(attrName));
//            }
//        }

        //1.设置Attribute
        request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.getWebApplicationContext());
        request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, this.localeResolver);
        request.setAttribute(THEME_RESOLVER_ATTRIBUTE, this.themeResolver);
        request.setAttribute(THEME_SOURCE_ATTRIBUTE, this.getThemeSource());
        FlashMap inputFlashMap = this.flashMapManager.retrieveAndUpdate(request, response);
        if (inputFlashMap != null) {
            request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE, Collections.unmodifiableMap(inputFlashMap));
        }

        request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
        request.setAttribute(FLASH_MAP_MANAGER_ATTRIBUTE, this.flashMapManager);

        try {
            //DispatcherServlet核心步骤
            this.doDispatch(request, response);
        } finally {
            if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted() && attributesSnapshot != null) {
                this.restoreAttributesAfterInclude(request, attributesSnapshot);
            }

        }

    }



    public static final String MULTIPART_RESOLVER_BEAN_NAME = "multipartResolver";
    public static final String LOCALE_RESOLVER_BEAN_NAME = "localeResolver";
    public static final String THEME_RESOLVER_BEAN_NAME = "themeResolver";
    public static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";
    public static final String HANDLER_ADAPTER_BEAN_NAME = "handlerAdapter";
    public static final String HANDLER_EXCEPTION_RESOLVER_BEAN_NAME = "handlerExceptionResolver";
    public static final String REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME = "viewNameTranslator";
    public static final String VIEW_RESOLVER_BEAN_NAME = "viewResolver";
    public static final String FLASH_MAP_MANAGER_BEAN_NAME = "flashMapManager";
    public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = org.springframework.web.servlet.DispatcherServlet.class.getName() + ".CONTEXT";
    public static final String LOCALE_RESOLVER_ATTRIBUTE = org.springframework.web.servlet.DispatcherServlet.class.getName() + ".LOCALE_RESOLVER";
    public static final String THEME_RESOLVER_ATTRIBUTE = org.springframework.web.servlet.DispatcherServlet.class.getName() + ".THEME_RESOLVER";
    public static final String THEME_SOURCE_ATTRIBUTE = org.springframework.web.servlet.DispatcherServlet.class.getName() + ".THEME_SOURCE";
    public static final String INPUT_FLASH_MAP_ATTRIBUTE = org.springframework.web.servlet.DispatcherServlet.class.getName() + ".INPUT_FLASH_MAP";
    public static final String OUTPUT_FLASH_MAP_ATTRIBUTE = org.springframework.web.servlet.DispatcherServlet.class.getName() + ".OUTPUT_FLASH_MAP";
    public static final String FLASH_MAP_MANAGER_ATTRIBUTE = org.springframework.web.servlet.DispatcherServlet.class.getName() + ".FLASH_MAP_MANAGER";
    public static final String EXCEPTION_ATTRIBUTE = org.springframework.web.servlet.DispatcherServlet.class.getName() + ".EXCEPTION";
    public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";
    private static final String DEFAULT_STRATEGIES_PATH = "DispatcherServlet.properties";
    private static final String DEFAULT_STRATEGIES_PREFIX = "org.springframework.web.servlet";
    protected static final Log pageNotFoundLogger = LogFactory.getLog("org.springframework.web.servlet.PageNotFound");
    private static final Properties defaultStrategies;
    private boolean detectAllHandlerMappings = true;
    private boolean detectAllHandlerAdapters = true;
    private boolean detectAllHandlerExceptionResolvers = true;
    private boolean detectAllViewResolvers = true;
    private boolean throwExceptionIfNoHandlerFound = false;
    private boolean cleanupAfterInclude = true;
    private MultipartResolver multipartResolver;
    private LocaleResolver localeResolver;
    private ThemeResolver themeResolver;
    private List<HandlerMapping> handlerMappings;
    private List<HandlerAdapter> handlerAdapters;
    private List<HandlerExceptionResolver> handlerExceptionResolvers;
    private RequestToViewNameTranslator viewNameTranslator;
    private FlashMapManager flashMapManager;
    //视图解析
    private List<ViewResolver> viewResolvers;

    public DispatcherServlet() {
        this.setDispatchOptionsRequest(true);
    }

    public DispatcherServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
        this.setDispatchOptionsRequest(true);
    }

    public void setDetectAllHandlerMappings(boolean detectAllHandlerMappings) {
        this.detectAllHandlerMappings = detectAllHandlerMappings;
    }

    public void setDetectAllHandlerAdapters(boolean detectAllHandlerAdapters) {
        this.detectAllHandlerAdapters = detectAllHandlerAdapters;
    }

    public void setDetectAllHandlerExceptionResolvers(boolean detectAllHandlerExceptionResolvers) {
        this.detectAllHandlerExceptionResolvers = detectAllHandlerExceptionResolvers;
    }

    public void setDetectAllViewResolvers(boolean detectAllViewResolvers) {
        this.detectAllViewResolvers = detectAllViewResolvers;
    }

    public void setThrowExceptionIfNoHandlerFound(boolean throwExceptionIfNoHandlerFound) {
        this.throwExceptionIfNoHandlerFound = throwExceptionIfNoHandlerFound;
    }

    public void setCleanupAfterInclude(boolean cleanupAfterInclude) {
        this.cleanupAfterInclude = cleanupAfterInclude;
    }

    protected void onRefresh(ApplicationContext context) {
        this.initStrategies(context);
    }

    protected void initStrategies(ApplicationContext context) {
        this.initMultipartResolver(context);
        this.initLocaleResolver(context);
        this.initThemeResolver(context);
        this.initHandlerMappings(context);
        this.initHandlerAdapters(context);
        this.initHandlerExceptionResolvers(context);
        this.initRequestToViewNameTranslator(context);
        this.initViewResolvers(context);
        this.initFlashMapManager(context);
    }

    private void initMultipartResolver(ApplicationContext context) {
        try {
            this.multipartResolver = (MultipartResolver)context.getBean("multipartResolver", MultipartResolver.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using MultipartResolver [" + this.multipartResolver + "]");
            }
        } catch (NoSuchBeanDefinitionException var3) {
            this.multipartResolver = null;
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Unable to locate MultipartResolver with name 'multipartResolver': no multipart request handling provided");
            }
        }

    }

    private void initLocaleResolver(ApplicationContext context) {
        try {
            this.localeResolver = (LocaleResolver)context.getBean("localeResolver", LocaleResolver.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using LocaleResolver [" + this.localeResolver + "]");
            }
        } catch (NoSuchBeanDefinitionException var3) {
            this.localeResolver = (LocaleResolver)this.getDefaultStrategy(context, LocaleResolver.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Unable to locate LocaleResolver with name 'localeResolver': using default [" + this.localeResolver + "]");
            }
        }

    }

    private void initThemeResolver(ApplicationContext context) {
        try {
            this.themeResolver = (ThemeResolver)context.getBean("themeResolver", ThemeResolver.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using ThemeResolver [" + this.themeResolver + "]");
            }
        } catch (NoSuchBeanDefinitionException var3) {
            this.themeResolver = (ThemeResolver)this.getDefaultStrategy(context, ThemeResolver.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Unable to locate ThemeResolver with name 'themeResolver': using default [" + this.themeResolver + "]");
            }
        }

    }

    private void initHandlerMappings(ApplicationContext context) {
        this.handlerMappings = null;
        if (this.detectAllHandlerMappings) {
            Map<String, HandlerMapping> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.handlerMappings = new ArrayList(matchingBeans.values());
                AnnotationAwareOrderComparator.sort(this.handlerMappings);
            }
        } else {
            try {
                HandlerMapping hm = (HandlerMapping)context.getBean("handlerMapping", HandlerMapping.class);
                this.handlerMappings = Collections.singletonList(hm);
            } catch (NoSuchBeanDefinitionException var3) {
                ;
            }
        }

        if (this.handlerMappings == null) {
            this.handlerMappings = this.getDefaultStrategies(context, HandlerMapping.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("No HandlerMappings found in servlet '" + this.getServletName() + "': using default");
            }
        }

    }

    private void initHandlerAdapters(ApplicationContext context) {
        this.handlerAdapters = null;
        if (this.detectAllHandlerAdapters) {
            Map<String, HandlerAdapter> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerAdapter.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.handlerAdapters = new ArrayList(matchingBeans.values());
                AnnotationAwareOrderComparator.sort(this.handlerAdapters);
            }
        } else {
            try {
                HandlerAdapter ha = (HandlerAdapter)context.getBean("handlerAdapter", HandlerAdapter.class);
                this.handlerAdapters = Collections.singletonList(ha);
            } catch (NoSuchBeanDefinitionException var3) {
                ;
            }
        }

        if (this.handlerAdapters == null) {
            this.handlerAdapters = this.getDefaultStrategies(context, HandlerAdapter.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("No HandlerAdapters found in servlet '" + this.getServletName() + "': using default");
            }
        }

    }

    private void initHandlerExceptionResolvers(ApplicationContext context) {
        this.handlerExceptionResolvers = null;
        if (this.detectAllHandlerExceptionResolvers) {
            Map<String, HandlerExceptionResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerExceptionResolver.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.handlerExceptionResolvers = new ArrayList(matchingBeans.values());
                AnnotationAwareOrderComparator.sort(this.handlerExceptionResolvers);
            }
        } else {
            try {
                HandlerExceptionResolver her = (HandlerExceptionResolver)context.getBean("handlerExceptionResolver", HandlerExceptionResolver.class);
                this.handlerExceptionResolvers = Collections.singletonList(her);
            } catch (NoSuchBeanDefinitionException var3) {
                ;
            }
        }

        if (this.handlerExceptionResolvers == null) {
            this.handlerExceptionResolvers = this.getDefaultStrategies(context, HandlerExceptionResolver.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("No HandlerExceptionResolvers found in servlet '" + this.getServletName() + "': using default");
            }
        }

    }

    private void initRequestToViewNameTranslator(ApplicationContext context) {
        try {
            this.viewNameTranslator = (RequestToViewNameTranslator)context.getBean("viewNameTranslator", RequestToViewNameTranslator.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using RequestToViewNameTranslator [" + this.viewNameTranslator + "]");
            }
        } catch (NoSuchBeanDefinitionException var3) {
            this.viewNameTranslator = (RequestToViewNameTranslator)this.getDefaultStrategy(context, RequestToViewNameTranslator.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Unable to locate RequestToViewNameTranslator with name 'viewNameTranslator': using default [" + this.viewNameTranslator + "]");
            }
        }

    }

    /**
     * 初始化视图解析器
     * */
    private void initViewResolvers(ApplicationContext context) {
        this.viewResolvers = null;
        if (this.detectAllViewResolvers) {
            Map<String, ViewResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, ViewResolver.class, true, false);
            if (!matchingBeans.isEmpty()) {
                this.viewResolvers = new ArrayList(matchingBeans.values());
                AnnotationAwareOrderComparator.sort(this.viewResolvers);
            }
        } else {
            try {
                ViewResolver vr = (ViewResolver)context.getBean("viewResolver", ViewResolver.class);
                this.viewResolvers = Collections.singletonList(vr);
            } catch (NoSuchBeanDefinitionException var3) {

            }
        }

        if (this.viewResolvers == null) {
            //获取默认视图解析器
            this.viewResolvers = this.getDefaultStrategies(context, ViewResolver.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("No ViewResolvers found in servlet '" + this.getServletName() + "': using default");
            }
        }

    }

    private void initFlashMapManager(ApplicationContext context) {
        try {
            this.flashMapManager = (FlashMapManager)context.getBean("flashMapManager", FlashMapManager.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Using FlashMapManager [" + this.flashMapManager + "]");
            }
        } catch (NoSuchBeanDefinitionException var3) {
            this.flashMapManager = (FlashMapManager)this.getDefaultStrategy(context, FlashMapManager.class);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Unable to locate FlashMapManager with name 'flashMapManager': using default [" + this.flashMapManager + "]");
            }
        }

    }

    public final ThemeSource getThemeSource() {
        return this.getWebApplicationContext() instanceof ThemeSource ? (ThemeSource)this.getWebApplicationContext() : null;
    }

    public final MultipartResolver getMultipartResolver() {
        return this.multipartResolver;
    }

    protected <T> T getDefaultStrategy(ApplicationContext context, Class<T> strategyInterface) {
        List<T> strategies = this.getDefaultStrategies(context, strategyInterface);
        if (strategies.size() != 1) {
            throw new BeanInitializationException("DispatcherServlet needs exactly 1 strategy for interface [" + strategyInterface.getName() + "]");
        } else {
            return strategies.get(0);
        }
    }

    protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {
        String key = strategyInterface.getName();
        String value = defaultStrategies.getProperty(key);
        if (value == null) {
            return new LinkedList();
        } else {
            String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
            List<T> strategies = new ArrayList(classNames.length);
            String[] var7 = classNames;
            int var8 = classNames.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                String className = var7[var9];

                try {
                    Class<?> clazz = ClassUtils.forName(className, org.springframework.web.servlet.DispatcherServlet.class.getClassLoader());
                    Object strategy = this.createDefaultStrategy(context, clazz);
                    strategies.add((T)strategy);
                } catch (ClassNotFoundException var13) {
                    throw new BeanInitializationException("Could not find DispatcherServlet's default strategy class [" + className + "] for interface [" + key + "]", var13);
                } catch (LinkageError var14) {
                    throw new BeanInitializationException("Error loading DispatcherServlet's default strategy class [" + className + "] for interface [" + key + "]: problem with class file or dependent class", var14);
                }
            }

            return strategies;
        }
    }

    protected Object createDefaultStrategy(ApplicationContext context, Class<?> clazz) {
        return context.getAutowireCapableBeanFactory().createBean(clazz);
    }



    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpServletRequest processedRequest = request;
        HandlerExecutionChain mappedHandler = null;
        boolean multipartRequestParsed = false;
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);

        try {
            try {
                ModelAndView mv = null;
                Object dispatchException = null;

                try {
                    //1.检查multipart，如果是的话处理
                    processedRequest = this.checkMultipart(request);
                    multipartRequestParsed = processedRequest != request;
                    //2.获取Handler(根据request获取url,
                    // 再根据url + request获取bean和方法)
                    mappedHandler = this.getHandler(processedRequest);
                    if (mappedHandler == null || mappedHandler.getHandler() == null) {
                        this.noHandlerFound(processedRequest, response);
                        return;
                    }
                    //适配handler
                    HandlerAdapter ha = this.getHandlerAdapter(mappedHandler.getHandler());
//                    String method = request.getMethod();
//                    boolean isGet = "GET".equals(method);
//                    if (isGet || "HEAD".equals(method)) {
//                        long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
//                        if (this.logger.isDebugEnabled()) {
//                            this.logger.debug("Last-Modified value for [" + getRequestUri(request) + "] is: " + lastModified);
//                        }
//
//                        if ((new ServletWebRequest(request, response)).checkNotModified(lastModified) && isGet) {
//                            return;
//                        }
//                    }

                    //3.执行处理器前置逻辑
                    if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                        return;
                    }
                    //4.执行处理器逻辑
                    mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
                    if (asyncManager.isConcurrentHandlingStarted()) {
                        return;
                    }
                    //5.如果没有视图,设置默认视图
                    this.applyDefaultViewName(processedRequest, mv);
                    //6.执行处理器逻辑
                    mappedHandler.applyPostHandle(processedRequest, response, mv);
                } catch (Exception var20) {
                    dispatchException = var20;
                } catch (Throwable var21) {
                    dispatchException = new NestedServletException("Handler dispatch failed", var21);
                }
                //7.处理返回结果
                this.processDispatchResult(processedRequest, response, mappedHandler, mv, (Exception)dispatchException);
            } catch (Exception var22) {
                this.triggerAfterCompletion(processedRequest, response, mappedHandler, var22);
            } catch (Throwable var23) {
                this.triggerAfterCompletion(processedRequest, response, mappedHandler, new NestedServletException("Handler processing failed", var23));
            }

        } finally {
            if (asyncManager.isConcurrentHandlingStarted()) {
                if (mappedHandler != null) {
                    mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
                }
            } else if (multipartRequestParsed) {
                this.cleanupMultipart(processedRequest);
            }

        }
    }

    private void applyDefaultViewName(HttpServletRequest request, ModelAndView mv) throws Exception {
        if (mv != null && !mv.hasView()) {
            mv.setViewName(this.getDefaultViewName(request));
        }

    }

    private void processDispatchResult(HttpServletRequest request, HttpServletResponse response, HandlerExecutionChain mappedHandler, ModelAndView mv, Exception exception) throws Exception {
        boolean errorView = false;
        if (exception != null) {
            if (exception instanceof ModelAndViewDefiningException) {
                this.logger.debug("ModelAndViewDefiningException encountered", exception);
                mv = ((ModelAndViewDefiningException)exception).getModelAndView();
            } else {
                Object handler = mappedHandler != null ? mappedHandler.getHandler() : null;
                mv = this.processHandlerException(request, response, handler, exception);
                errorView = mv != null;
            }
        }

        if (mv != null && !mv.wasCleared()) {
            //视图渲染
            this.render(mv, request, response);
            if (errorView) {
                WebUtils.clearErrorRequestAttributes(request);
            }
        } else if (this.logger.isDebugEnabled()) {
            this.logger.debug("Null ModelAndView returned to DispatcherServlet with name '" + this.getServletName() + "': assuming HandlerAdapter completed request handling");
        }

        if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
            if (mappedHandler != null) {
                mappedHandler.triggerAfterCompletion(request, response, (Exception)null);
            }

        }
    }

    protected LocaleContext buildLocaleContext(final HttpServletRequest request) {
        return this.localeResolver instanceof LocaleContextResolver ? ((LocaleContextResolver)this.localeResolver).resolveLocaleContext(request) : new LocaleContext() {
            public Locale getLocale() {
                return DispatcherServlet.this.localeResolver.resolveLocale(request);
            }
        };
    }

    protected HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
        if (this.multipartResolver != null && this.multipartResolver.isMultipart(request)) {
            if (WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class) != null) {
                this.logger.debug("Request is already a MultipartHttpServletRequest - if not in a forward, this typically results from an additional MultipartFilter in web.xml");
            } else if (this.hasMultipartException(request)) {
                this.logger.debug("Multipart resolution failed for current request before - skipping re-resolution for undisturbed error rendering");
            } else {
                try {
                    return this.multipartResolver.resolveMultipart(request);
                } catch (MultipartException var3) {
                    if (request.getAttribute("javax.servlet.error.exception") == null) {
                        throw var3;
                    }
                }
            }
        }

        return request;
    }

    private boolean hasMultipartException(HttpServletRequest request) {
        for(Throwable error = (Throwable)request.getAttribute("javax.servlet.error.exception"); error != null; error = error.getCause()) {
            if (error instanceof MultipartException) {
                return true;
            }
        }

        return false;
    }

    protected void cleanupMultipart(HttpServletRequest request) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        if (multipartRequest != null) {
            this.multipartResolver.cleanupMultipart(multipartRequest);
        }

    }

    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        Iterator var2 = this.handlerMappings.iterator();

        HandlerExecutionChain handler;
        //匹配mapping
        do {
            if (!var2.hasNext()) {
                return null;
            }

            HandlerMapping hm = (HandlerMapping)var2.next();
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Testing handler map [" + hm + "] in DispatcherServlet with name '" + this.getServletName() + "'");
            }

            handler = hm.getHandler(request);
        } while(handler == null);

        return handler;
    }

    protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (pageNotFoundLogger.isWarnEnabled()) {
            pageNotFoundLogger.warn("No mapping found for HTTP request with URI [" + getRequestUri(request) + "] in DispatcherServlet with name '" + this.getServletName() + "'");
        }

        if (this.throwExceptionIfNoHandlerFound) {
            throw new NoHandlerFoundException(request.getMethod(), getRequestUri(request), (new ServletServerHttpRequest(request)).getHeaders());
        } else {
            response.sendError(404);
        }
    }

    protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
        Iterator var2 = this.handlerAdapters.iterator();

        HandlerAdapter ha;
        do {
            if (!var2.hasNext()) {
                throw new ServletException("No adapter for handler [" + handler + "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
            }

            ha = (HandlerAdapter)var2.next();
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("Testing handler adapter [" + ha + "]");
            }
        } while(!ha.supports(handler));

        return ha;
    }

    protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ModelAndView exMv = null;
        Iterator var6 = this.handlerExceptionResolvers.iterator();

        while(var6.hasNext()) {
            HandlerExceptionResolver handlerExceptionResolver = (HandlerExceptionResolver)var6.next();
            exMv = handlerExceptionResolver.resolveException(request, response, handler, ex);
            if (exMv != null) {
                break;
            }
        }

        if (exMv != null) {
            if (exMv.isEmpty()) {
                request.setAttribute(EXCEPTION_ATTRIBUTE, ex);
                return null;
            } else {
                if (!exMv.hasView()) {
                    exMv.setViewName(this.getDefaultViewName(request));
                }

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Handler execution resulted in exception - forwarding to resolved error view: " + exMv, ex);
                }

                WebUtils.exposeErrorRequestAttributes(request, ex, this.getServletName());
                return exMv;
            }
        } else {
            throw ex;
        }
    }

    protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Locale locale = this.localeResolver.resolveLocale(request);
        response.setLocale(locale);
        View view;
        if (mv.isReference()) {
            view = this.resolveViewName(mv.getViewName(), mv.getModelInternal(), locale, request);
            if (view == null) {
                throw new ServletException("Could not resolve view with name '" + mv.getViewName() + "' in servlet with name '" + this.getServletName() + "'");
            }
        } else {
            view = mv.getView();
            if (view == null) {
                throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a View object in servlet with name '" + this.getServletName() + "'");
            }
        }

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Rendering view [" + view + "] in DispatcherServlet with name '" + this.getServletName() + "'");
        }

        try {
            if (mv.getStatus() != null) {
                response.setStatus(mv.getStatus().value());
            }
            //视图呈现
            view.render(mv.getModelInternal(), request, response);
        } catch (Exception var7) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Error rendering view [" + view + "] in DispatcherServlet with name '" + this.getServletName() + "'", var7);
            }

            throw var7;
        }
    }

    protected String getDefaultViewName(HttpServletRequest request) throws Exception {
        return this.viewNameTranslator.getViewName(request);
    }

    protected View resolveViewName(String viewName, Map<String, Object> model, Locale locale, HttpServletRequest request) throws Exception {
        Iterator var5 = this.viewResolvers.iterator();

        View view;
        do {
            if (!var5.hasNext()) {
                return null;
            }

            ViewResolver viewResolver = (ViewResolver)var5.next();
            view = viewResolver.resolveViewName(viewName, locale);
        } while(view == null);

        return view;
    }

    private void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerExecutionChain mappedHandler, Exception ex) throws Exception {
        if (mappedHandler != null) {
            mappedHandler.triggerAfterCompletion(request, response, ex);
        }

        throw ex;
    }

    private void restoreAttributesAfterInclude(HttpServletRequest request, Map<?, ?> attributesSnapshot) {
        Set<String> attrsToCheck = new HashSet();
        Enumeration attrNames = request.getAttributeNames();

        while(true) {
            String attrName;
            do {
                if (!attrNames.hasMoreElements()) {
                    //attrsToCheck.addAll(attributesSnapshot.keySet());
                    Iterator var8 = attrsToCheck.iterator();

                    while(var8.hasNext()) {
                        String attrName1 = (String)var8.next();
                        Object attrValue = attributesSnapshot.get(attrName1);
                        if (attrValue == null) {
                            request.removeAttribute(attrName1);
                        } else if (attrValue != request.getAttribute(attrName1)) {
                            request.setAttribute(attrName1, attrValue);
                        }
                    }

                    return;
                }

                attrName = (String)attrNames.nextElement();
            } while(!this.cleanupAfterInclude && !attrName.startsWith("org.springframework.web.servlet"));

            attrsToCheck.add(attrName);
        }
    }

    private static String getRequestUri(HttpServletRequest request) {
        String uri = (String)request.getAttribute("javax.servlet.include.request_uri");
        if (uri == null) {
            uri = request.getRequestURI();
        }

        return uri;
    }

    static {
        try {
            ClassPathResource resource = new ClassPathResource("DispatcherServlet.properties", org.springframework.web.servlet.DispatcherServlet.class);
            defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException var1) {
            throw new IllegalStateException("Could not load 'DispatcherServlet.properties': " + var1.getMessage());
        }
    }
}

