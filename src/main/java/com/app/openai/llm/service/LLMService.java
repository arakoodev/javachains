package com.app.openai.llm.service;

import com.app.openai.llm.provider.LLMProvider;
import com.app.rxjava.transformer.observable.EdgeChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LLMService implements LLMProvider {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final LLMProvider provider;

    public LLMService(LLMProvider provider) {
        log.info("Selected the following LLM Provider: "+provider.getClass().getSimpleName());
        this.provider = provider;
    }

    @Override
    public EdgeChain<String> request(String prompt) {
        return provider.request(prompt);
    }
}
