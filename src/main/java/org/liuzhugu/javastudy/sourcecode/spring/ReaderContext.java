package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.parsing.*;
import org.springframework.core.io.Resource;

public class ReaderContext {
    private final Resource resource;
    private final ProblemReporter problemReporter;
    private final ReaderEventListener eventListener;
    private final SourceExtractor sourceExtractor;

    public ReaderContext(Resource resource, ProblemReporter problemReporter, ReaderEventListener eventListener, SourceExtractor sourceExtractor) {
        this.resource = resource;
        this.problemReporter = problemReporter;
        this.eventListener = eventListener;
        this.sourceExtractor = sourceExtractor;
    }

    public final Resource getResource() {
        return this.resource;
    }

    public void fatal(String message, Object source) {
        this.fatal(message, source, (ParseState)null, (Throwable)null);
    }

    public void fatal(String message, Object source, Throwable ex) {
        this.fatal(message, source, (ParseState)null, ex);
    }

    public void fatal(String message, Object source, ParseState parseState) {
        this.fatal(message, source, parseState, (Throwable)null);
    }

    public void fatal(String message, Object source, ParseState parseState, Throwable cause) {
        Location location = new Location(this.getResource(), source);
        this.problemReporter.fatal(new Problem(message, location, parseState, cause));
    }

    public void error(String message, Object source) {
        this.error(message, source, (ParseState)null, (Throwable)null);
    }

    public void error(String message, Object source, Throwable ex) {
        this.error(message, source, (ParseState)null, ex);
    }

    public void error(String message, Object source, ParseState parseState) {
        this.error(message, source, parseState, (Throwable)null);
    }

    public void error(String message, Object source, ParseState parseState, Throwable cause) {
        Location location = new Location(this.getResource(), source);
        this.problemReporter.error(new Problem(message, location, parseState, cause));
    }

    public void warning(String message, Object source) {
        this.warning(message, source, (ParseState)null, (Throwable)null);
    }

    public void warning(String message, Object source, Throwable ex) {
        this.warning(message, source, (ParseState)null, ex);
    }

    public void warning(String message, Object source, ParseState parseState) {
        this.warning(message, source, parseState, (Throwable)null);
    }

    public void warning(String message, Object source, ParseState parseState, Throwable cause) {
        Location location = new Location(this.getResource(), source);
        this.problemReporter.warning(new Problem(message, location, parseState, cause));
    }

    public void fireDefaultsRegistered(DefaultsDefinition defaultsDefinition) {
        this.eventListener.defaultsRegistered(defaultsDefinition);
    }

    public void fireComponentRegistered(ComponentDefinition componentDefinition) {
        this.eventListener.componentRegistered(componentDefinition);
    }

    public void fireAliasRegistered(String beanName, String alias, Object source) {
        this.eventListener.aliasRegistered(new AliasDefinition(beanName, alias, source));
    }

    public void fireImportProcessed(String importedResource, Object source) {
        this.eventListener.importProcessed(new ImportDefinition(importedResource, source));
    }

    public void fireImportProcessed(String importedResource, Resource[] actualResources, Object source) {
        this.eventListener.importProcessed(new ImportDefinition(importedResource, actualResources, source));
    }

    public SourceExtractor getSourceExtractor() {
        return this.sourceExtractor;
    }

    public Object extractSource(Object sourceCandidate) {
        return this.sourceExtractor.extractSource(sourceCandidate, this.resource);
    }
}