package com.composum.pages.commons.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.composum.pages.commons.service.PagesPlugin;
import com.composum.pages.commons.service.PagesPluginService;

/**
 * A service that collects PagesPlugins so that they could hook into Pages.
 */
// FIXME(hps,27.04.23) How could we make sure that user packages cannot install a plugin for security?
@Component(service = PagesPluginService.class, immediate = true)
public class PagesPluginServiceImpl implements PagesPluginService {

    private static final Logger LOG = LoggerFactory.getLogger(PagesPluginServiceImpl.class);

    protected volatile List<PagesPlugin> pagesPlugins = Collections.emptyList();

    // lazily computed values
    protected volatile List<String> widgetLabelExtensions;

    @Reference(
            service = PagesPlugin.class,
            policy = ReferencePolicy.DYNAMIC,
            cardinality = ReferenceCardinality.MULTIPLE,
            policyOption = ReferencePolicyOption.GREEDY
    )
    protected synchronized void bindPagesPlugin(@NotNull final PagesPlugin plugin) {
        List<PagesPlugin> newPagesPlugins = new ArrayList<>(pagesPlugins);
        newPagesPlugins.add(plugin);
        newPagesPlugins.sort(Comparator.comparingInt(PagesPlugin::getRank));
        pagesPlugins = Collections.unmodifiableList(newPagesPlugins);
        LOG.info("Added PagesPlugin: {}", plugin);
        resetLazyVariables();
    }

    /**
     * Reset lazily computed values whenever a new plugin appears / one disappears.
     */
    protected void resetLazyVariables() {
        widgetLabelExtensions = null;
    }

    protected synchronized void unbindPagesPlugin(@NotNull final PagesPlugin plugin) {
        pagesPlugins = pagesPlugins.stream().filter(p -> p != plugin).collect(Collectors.toList());
        LOG.info("Removed PagesPlugin: {}", plugin);
        resetLazyVariables();
    }

    @Nonnull
    public List<String> getWidgetLabelExtensions() {
        if (widgetLabelExtensions == null) {
            widgetLabelExtensions = Collections.unmodifiableList(
                    pagesPlugins.stream()
                            .flatMap(plugin -> plugin.getWidgetLabelExtensions().stream())
                            .collect(Collectors.toList())
            );
        }
        return widgetLabelExtensions;
    }

}
