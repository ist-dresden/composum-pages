/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.service;

import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.ValueMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

/**
 * a service to embed dynamic values specified in a text as placeholders ('${...}')
 */
public interface PlaceholderService {

    /**
     * a service interface to register placeholder value providers
     */
    interface ValueProvider {

        /**
         * the provider with the highest rank has precedence
         */
        @Nonnull
        Integer valueProviderRank();

        /**
         * @return the value ot the key of the provider can retrieve it
         */
        @Nullable
        <T> T getValue(@Nonnull BeanContext context, @Nonnull String key, Class<T> type);
    }

    /**
     * @return the dynamic map of all provided values
     */
    @Nonnull
    ValueMap getValueMap(@Nonnull BeanContext context, @Nullable Map<String, Object> base);

    /**
     * @return the text value with embedded placeholder values
     */
    @Nonnull
    String applyPlaceholders(@Nonnull BeanContext context,
                             @Nonnull String text, @Nonnull Map<String, Object> values);

    /**
     * writes the text to the writer with replacing of all placeholders in the text
     */
    void applyPlaceholders(@Nonnull BeanContext context, @Nonnull Writer writer,
                           @Nonnull String text, @Nonnull Map<String, Object> values);

    /**
     * @return a reader which is replacing all placeholders during read
     */
    @Nonnull
    Reader getEmbeddingReader(@Nonnull BeanContext context,
                              @Nonnull Reader reader, @Nonnull Map<String, Object> values);
}
