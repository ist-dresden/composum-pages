/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
package com.composum.pages.commons.service;

import com.composum.sling.core.BeanContext;
import com.composum.sling.core.util.ValueEmbeddingReader;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The service to replace placeholders ('${...}') in text properties.
 */
@Component(
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Placeholder Service"
        }
)
public class PagesPlaceholderService implements PlaceholderService {

    private static final Logger LOG = LoggerFactory.getLogger(PagesPlaceholderService.class);

    public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(\\$\\{([^}]+)})");

    protected static final Object NULL = "";

    /**
     * a caching value map (i don't know how expensive the retrieval by the providers is)
     */
    protected class ProviderValueMap extends ValueMapDecorator {

        protected final BeanContext context;
        protected final Map<String, Object> cache;

        public ProviderValueMap(@Nonnull final BeanContext context, Map<String, Object> base) {
            super(base);
            this.context = context;
            cache = new HashMap<>();
        }

        @Nullable
        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(@Nonnull final String name, @Nonnull final Class<T> type) {
            T value = (T) cache.get(name);
            if (value == null) {
                value = super.get(name, type);
                if (value == null) {
                    value = getProviderValue(context, name, type);
                }
                cache.put(name, value != null ? value : NULL); // cache 'null' also
            }
            return value == NULL ? null : value;
        }

        @Nonnull
        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(@Nonnull final String name, @Nonnull final T defaultValue) {
            Class<?> type = defaultValue.getClass();
            T value = get(name, (Class<T>) type);
            return value != null ? value : defaultValue;
        }
    }

    @Override
    @Nonnull
    public ValueMap getValueMap(@Nonnull final BeanContext context, @Nullable final Map<String, Object> base) {
        return new ProviderValueMap(context, base != null ? base : Collections.<String, Object>emptyMap());
    }

    @Override
    @Nonnull
    public String applyPlaceholders(@Nonnull final BeanContext context,
                                    @Nonnull final String text, @Nonnull final Map<String, Object> values) {
        StringWriter writer = new StringWriter();
        applyPlaceholders(context, writer, text, values);
        return writer.toString();
    }

    @Override
    public void applyPlaceholders(@Nonnull final BeanContext context, @Nonnull final Writer writer,
                                  @Nonnull final String text, @Nonnull final Map<String, Object> values) {
        try {
            ValueMap valueMap = getValueMap(context, values);
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
            int len = text.length();
            int pos = 0;
            while (matcher.find(pos)) {
                String key = matcher.group(2);
                writer.write(text, pos, matcher.start() - pos);
                writer.write(valueMap.get(key, ""));
                pos = matcher.end();
            }
            if (pos >= 0 && pos < len) {
                writer.write(text, pos, len - pos);
            }

        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * @return the appropriate value embedding reader
     * @see com.composum.sling.core.util.ValueEmbeddingReader
     */
    @Override
    @Nonnull
    public Reader getEmbeddingReader(@Nonnull final BeanContext context,
                                     @Nonnull final Reader reader, @Nonnull final Map<String, Object> values) {
        return new ValueEmbeddingReader(reader, getValueMap(context, values));
    }

    // value provider services

    public static final Comparator<ValueProvider> VALUE_PROVIDER_COMPARATOR = new Comparator<ValueProvider>() {
        @Override
        public int compare(ValueProvider o1, ValueProvider o2) {
            return o2.valueProviderRank().compareTo(o1.valueProviderRank()); // sort descending
        }
    };

    protected List<ValueProvider> valueProviders = Collections.synchronizedList(new ArrayList<ValueProvider>());

    protected <T> T getProviderValue(@Nonnull final BeanContext context,
                                     @Nonnull final String key, @Nonnull final Class<T> type) {
        T value;
        for (ValueProvider provider : valueProviders) {
            if ((value = provider.getValue(context, key, type)) != null) {
                return value;
            }
        }
        return null;
    }

    @Reference(
            service = ValueProvider.class,
            policy = ReferencePolicy.DYNAMIC,
            cardinality = ReferenceCardinality.MULTIPLE
    )
    protected void bindValueProvider(@Nonnull final ValueProvider service) {
        LOG.info("bindValueProvider: {}", service.getClass());
        valueProviders.add(service);
        Collections.sort(valueProviders, VALUE_PROVIDER_COMPARATOR);
    }

    protected void unbindValueProvider(@Nonnull final ValueProvider service) {
        LOG.info("unbindValueProvider: {}", service);
        valueProviders.remove(service);
    }
}
