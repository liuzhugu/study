package org.liuzhugu.javastudy.sourcecode.springmvc;

import org.springframework.util.Assert;

import javax.servlet.ServletException;

public class ModelAndViewDefiningException extends ServletException {

    private ModelAndView modelAndView;


    /**
     * Create new ModelAndViewDefiningException with the given ModelAndView,
     * typically representing a specific error page.
     * @param modelAndView ModelAndView with view to forward to and model to expose
     */
    public ModelAndViewDefiningException(ModelAndView modelAndView) {
        Assert.notNull(modelAndView, "ModelAndView must not be null in ModelAndViewDefiningException");
        this.modelAndView = modelAndView;
    }

    /**
     * Return the ModelAndView that this exception contains for forwarding to.
     */
    public ModelAndView getModelAndView() {
        return modelAndView;
    }

}

