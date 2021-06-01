package com.composum.pages.stage.setup;

import org.apache.jackrabbit.vault.packaging.InstallContext;
import org.apache.jackrabbit.vault.packaging.InstallHook;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.List;

public class SetupHook implements InstallHook {

    private static final Logger LOG = LoggerFactory.getLogger(SetupHook.class);

    private static final String PAGES_STAGE_INDEX = "/libs/composum/pages/home.html";
    private static final String DEFAULT_SLING_INDEX = "/starter/index.html";

    private static final String TYPE_SLING_REDIRECT = "sling:redirect";

    private static final String PROP_SLING_RESOURCE_TYPE = "sling:resourceType";
    private static final String PROP_SLING_TARGET = "sling:target";

    private static final List<String> ROOT_NODES_ORDER = new ArrayList<String>() {{
        add("public");
        add("preview");
        add("content");
        add("conf");
        add("etc");
        add("apps");
        add("libs");
        add("var");
    }};

    @Override
    public void execute(InstallContext ctx) throws PackageException {
        //noinspection SwitchStatementWithTooFewBranches
        switch (ctx.getPhase()) {
            case INSTALLED:
                LOG.info("installed: execute...");
                setupIndexPage(ctx);
                LOG.info("installed: execute ends.");
        }
    }

    protected void setupIndexPage(InstallContext ctx) throws PackageException {
        try {
            Session session = ctx.getSession();
            Node rootNode = session.getNode("/");
            setPropertyIfNotChanged(rootNode, PROP_SLING_RESOURCE_TYPE, TYPE_SLING_REDIRECT, null);
            setPropertyIfNotChanged(rootNode, PROP_SLING_TARGET, PAGES_STAGE_INDEX, DEFAULT_SLING_INDEX);
            createEtcIfNotPresent(session, rootNode);
            nodeOrdering(rootNode, ROOT_NODES_ORDER);
            session.save();
        } catch (RepositoryException | RuntimeException rex) {
            LOG.error(rex.getMessage(), rex);
            throw new PackageException(rex);
        }
    }

    /**
     * If this is installed with the sling feature launcher, the /etc might not have been created yet, but it would be created later, anyway, when packages are installed etc, but with an ordering different from {@link #ROOT_NODES_ORDER}. So we create it right now to be able to set the order with {@link #nodeOrdering(Node, List)}.
     */
    protected void createEtcIfNotPresent(Session session, Node rootNode) throws RepositoryException {
        if (!rootNode.hasNode("etc")) {
            rootNode.addNode("etc", "sling:Folder");
        }
    }

    protected void setPropertyIfNotChanged(Node node, String name, String value, String def)
            throws RepositoryException {
        try {
            Property current = node.getProperty(name);
            if (def == null || def.equals(current.getString())) {
                // replace the property only if not changed (is the original default value)
                node.setProperty(name, value);
            }
        } catch (PathNotFoundException ignore) {
            // no target property set...
            node.setProperty(name, value);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    protected void nodeOrdering(Node node, List<String> namesOrder)
            throws RepositoryException {
        String refNodeName = null;
        NodeIterator nodes = node.getNodes();
        // find first node that isn't contained in namesOrder into refNodeName
        while (nodes.hasNext() && namesOrder.contains(refNodeName = nodes.nextNode().getName())) ;
        // insert the nodes contained in namesOrder immediately before refNodeName.
        for (String name : namesOrder) {
            if (node.hasNode(name)) {
                node.orderBefore(name, refNodeName);
            } else {
                LOG.warn("Node not found for reordering: {}/{}", node.getPath(), name);
            }
        }
    }
}
