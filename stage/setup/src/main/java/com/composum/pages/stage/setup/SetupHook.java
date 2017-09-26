package com.composum.pages.stage.setup;

import org.apache.jackrabbit.vault.packaging.InstallContext;
import org.apache.jackrabbit.vault.packaging.InstallHook;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class SetupHook implements InstallHook {

    private static final Logger LOG = LoggerFactory.getLogger(SetupHook.class);

    private static final String PAGES_STAGE_INDEX = "/libs/composum/pages/stage/home.html";
    private static final String DEFAULT_SLING_INDEX = "/index.html";

    private static final String PROP_SLING_TARGET = "sling:target";

    @Override
    public void execute(InstallContext ctx) throws PackageException {
        switch (ctx.getPhase()) {
            case INSTALLED:
                LOG.info("installed: execute...");

                setupIndexPage(ctx);

                LOG.info("installed: execute ends.");
        }
    }

    protected void setupIndexPage(InstallContext ctx) {
        try {
            Session session = ctx.getSession();
            Node rootNode = session.getNode("/");
            Property redirect = rootNode.getProperty(PROP_SLING_TARGET);
            if (redirect != null && DEFAULT_SLING_INDEX.equals(redirect.getString())) {
                // replace the redirect target only if this property is not changed
                rootNode.setProperty(PROP_SLING_TARGET, PAGES_STAGE_INDEX);
                session.save();
            }
        } catch (RepositoryException rex) {
            LOG.error(rex.getMessage(), rex);
        }
    }
}
