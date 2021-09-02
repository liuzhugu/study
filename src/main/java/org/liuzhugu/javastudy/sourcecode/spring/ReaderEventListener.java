package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.parsing.AliasDefinition;
import org.springframework.beans.factory.parsing.DefaultsDefinition;
import org.springframework.beans.factory.parsing.ImportDefinition;

import java.util.EventListener;

public interface ReaderEventListener extends EventListener {
    void defaultsRegistered(DefaultsDefinition var1);

    void componentRegistered(ComponentDefinition var1);

    void aliasRegistered(AliasDefinition var1);

    void importProcessed(ImportDefinition var1);
}

