package org.liuzhugu.javastudy.sourcecode.springmvc;

import javax.servlet.http.HttpServletRequest;

public interface HandlerMapping {

    /**
     * Name of the {@link HttpServletRequest} attribute that contains the path
     * within the handler mapping, in case of a pattern match, or the full
     * relevant URI (typically within the DispatcherServlet's mapping) else.
     * <p>Note: This attribute is not required to be supported by all
     * HandlerMapping implementations. URL-based HandlerMappings will
     * typically support it, but handlers should not necessarily expect
     * this request attribute to be present in all scenarios.
     */
    String PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE = org.springframework.web.servlet.HandlerMapping.class.getName() + ".pathWithinHandlerMapping";

    /**
     * Name of the {@link HttpServletRequest} attribute that contains the
     * best matching pattern within the handler mapping.
     * <p>Note: This attribute is not required to be supported by all
     * HandlerMapping implementations. URL-based HandlerMappings will
     * typically support it, but handlers should not necessarily expect
     * this request attribute to be present in all scenarios.
     */
    String BEST_MATCHING_PATTERN_ATTRIBUTE = org.springframework.web.servlet.HandlerMapping.class.getName() + ".bestMatchingPattern";

    /**
     * Name of the boolean {@link HttpServletRequest} attribute that indicates
     * whether type-level mappings should be inspected.
     * <p>Note: This attribute is not required to be supported by all
     * HandlerMapping implementations.
     */
    String INTROSPECT_TYPE_LEVEL_MAPPING = org.springframework.web.servlet.HandlerMapping.class.getName() + ".introspectTypeLevelMapping";

    /**
     * Name of the {@link HttpServletRequest} attribute that contains the URI
     * templates map, mapping variable names to values.
     * <p>Note: This attribute is not required to be supported by all
     * HandlerMapping implementations. URL-based HandlerMappings will
     * typically support it, but handlers should not necessarily expect
     * this request attribute to be present in all scenarios.
     */
    String URI_TEMPLATE_VARIABLES_ATTRIBUTE = org.springframework.web.servlet.HandlerMapping.class.getName() + ".uriTemplateVariables";

    /**
     * Name of the {@link HttpServletRequest} attribute that contains a map with
     * URI matrix variables.
     * <p>Note: This attribute is not required to be supported by all
     * HandlerMapping implementations and may also not be present depending on
     * whether the HandlerMapping is configured to keep matrix variable content
     * in the request URI.
     */
    String MATRIX_VARIABLES_ATTRIBUTE = org.springframework.web.servlet.HandlerMapping.class.getName() + ".matrixVariables";

    /**
     * Name of the {@link HttpServletRequest} attribute that contains the set of
     * producible MediaTypes applicable to the mapped handler.
     * <p>Note: This attribute is not required to be supported by all
     * HandlerMapping implementations. Handlers should not necessarily expect
     * this request attribute to be present in all scenarios.
     */
    String PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE = org.springframework.web.servlet.HandlerMapping.class.getName() + ".producibleMediaTypes";

    /**
     * Return a handler and any interceptors for this request. The choice may be made
     * on request URL, session state, or any factor the implementing class chooses.
     * <p>The returned HandlerExecutionChain contains a handler Object, rather than
     * even a tag interface, so that handlers are not constrained in any way.
     * For example, a HandlerAdapter could be written to allow another framework's
     * handler objects to be used.
     * <p>Returns {@code null} if no match was found. This is not an error.
     * The DispatcherServlet will query all registered HandlerMapping beans to find
     * a match, and only decide there is an error if none can find a handler.
     * @param request current HTTP request
     * @return a HandlerExecutionChain instance containing handler object and
     * any interceptors, or {@code null} if no mapping found
     * @throws Exception if there is an internal error
     */
    HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;

}

