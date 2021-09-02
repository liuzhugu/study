package org.liuzhugu.javastudy.sourcecode.spring;

import org.springframework.beans.factory.parsing.*;

public class EmptyReaderEventListener implements ReaderEventListener {
    public EmptyReaderEventListener() {
    }

    public void defaultsRegistered(DefaultsDefinition defaultsDefinition) {
    }

    public void componentRegistered(ComponentDefinition componentDefinition) {
    }

    public void aliasRegistered(AliasDefinition aliasDefinition) {
    }

    public void importProcessed(ImportDefinition importDefinition) {
    }
}
