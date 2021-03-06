package com.composum.pages.commons.setup;

import com.composum.sling.core.service.RepositorySetupService;
import com.composum.sling.core.setup.util.SetupUtil;
import com.composum.sling.core.util.CoreConstants;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.vault.fs.io.Archive;
import org.apache.jackrabbit.vault.packaging.InstallContext;
import org.apache.jackrabbit.vault.packaging.InstallHook;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@SuppressWarnings({"Duplicates"})
public class SetupHook implements InstallHook {

    private static final Logger LOG = LoggerFactory.getLogger(SetupHook.class);

    private static final String SETUP_ACLS = "/conf/composum/pages/commons/acl/setup.json";

    public static final String PAGES_USERS_PATH = "composum/pages/";
    public static final String PAGES_SYSTEM_USERS_PATH = "system/composum/pages/";

    public static final String PAGES_SERVICE_USER = "composum-pages-service";
    public static final String PAGES_TOKEN_SERVICE_USER = "composum-pages-token-service";

    public static final Map<String, List<String>> PAGES_USERS;
    public static final Map<String, List<String>> PAGES_SYSTEM_USERS;
    public static final Map<String, List<String>> PAGES_GROUPS;

    public static final String SITE_CONFIGURATION_QUERY = "/jcr:root//element(*,cpp:SiteConfiguration)";

    static {
        PAGES_USERS = new LinkedHashMap<>();
        PAGES_SYSTEM_USERS = new LinkedHashMap<>();
        PAGES_SYSTEM_USERS.put(PAGES_SYSTEM_USERS_PATH + PAGES_SERVICE_USER, emptyList());
        PAGES_SYSTEM_USERS.put(PAGES_SYSTEM_USERS_PATH + PAGES_TOKEN_SERVICE_USER, emptyList());
        PAGES_GROUPS = new LinkedHashMap<>();
    }

    @Override
    public void execute(InstallContext ctx) throws PackageException {
        switch (ctx.getPhase()) {
            case PREPARE:
                LOG.info("prepare: execute...");
                SetupUtil.setupGroupsAndUsers(ctx, PAGES_GROUPS, PAGES_SYSTEM_USERS, PAGES_USERS);
                LOG.info("prepare: execute ends.");
                break;
            case INSTALLED:
                LOG.info("installed: execute...");
                setupAcls(ctx);
                refreshLucene(ctx);
                migrateTables(ctx);
                // updateNodeTypes should be the last actions since we need a session.save() there.
                updateNodeTypes(ctx);
                LOG.info("installed: execute ends.");
                break;
        }
    }

    protected void setupAcls(InstallContext ctx) throws PackageException {
        RepositorySetupService setupService = SetupUtil.getService(RepositorySetupService.class);
        try {
            Session session = ctx.getSession();
            setupService.addJsonAcl(session, SETUP_ACLS, null);
            session.save();
        } catch (Exception rex) {
            LOG.error(rex.getMessage(), rex);
            throw new PackageException(rex);
        }
    }

    /** Sets the 'refresh' property for the lucene index since we updated some settings. */
    protected void refreshLucene(InstallContext ctx) throws PackageException {
        try {
            Session session = ctx.getSession();
            Node lucenecfg = session.getNode("/oak:index/lucene");
            lucenecfg.setProperty("refresh", true);
            session.save();
        } catch (Exception rex) {
            LOG.error(rex.getMessage(), rex);
            throw new PackageException(rex);
        }
    }

    protected void updateNodeTypes(InstallContext ctx) throws PackageException {
        try {
            Session session = ctx.getSession();
            NodeTypeManager nodeTypeManager = session.getWorkspace().getNodeTypeManager();
            NodeType siteConfigType = nodeTypeManager.getNodeType("cpp:SiteConfiguration");
            boolean siteConfigVersionable = siteConfigType.isNodeType(JcrConstants.MIX_VERSIONABLE);
            if (!siteConfigVersionable) {
                addVersionableMixinToSiteConfigurations(session);
            }
            boolean updateNeeded =
                    siteConfigType.isNodeType("cpl:releaseConfig") || !siteConfigVersionable;
            if (updateNeeded) {
                LOG.warn("Update pages nodetypes neccesary.");

                Archive archive = ctx.getPackage().getArchive();
                try (InputStream stream = archive.openInputStream(archive.getEntry("/META-INF/vault/nodetypes.cnd"))) {
                    InputStreamReader cndReader = new InputStreamReader(stream);
                    CndImporter.registerNodeTypes(cndReader, session, true);
                }

                siteConfigType = nodeTypeManager.getNodeType("cpp:SiteConfiguration");
                if (siteConfigType.isNodeType("cpl:releaseConfig") || !siteConfigType.isNodeType(JcrConstants.MIX_VERSIONABLE)) {
                    LOG.error("Something went wrong when updating nodetypes: cpp:SiteConfig does still contain " +
                            "cpl:releaseConfig or does not contain mix:versionable even after attempted migration!");
                }
            } else {
                LOG.info("OK: no pages nodetype update needed");
            }
        } catch (Exception rex) {
            LOG.error(rex.getMessage(), rex);
            throw new PackageException(rex);
        }
    }

    /**
     * Strangely, adding mix:versionable through the nodetype update fails with a CommitFailedException "Mandatory
     * property jcr:predecessors not found in a new node". So we have to add the mixin by hand first. :-(
     */
    protected void addVersionableMixinToSiteConfigurations(Session session) throws RepositoryException {
        LOG.info("Adding mix:versionable to all cpp:SiteConfiguration");
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        Query query = queryManager.createQuery("SELECT * from [cpp:SiteConfiguration]", Query.JCR_SQL2);
        NodeIterator nodeIterator = query.execute().getNodes();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.nextNode();
            node.addMixin(JcrConstants.MIX_VERSIONABLE);
        }
        session.save();
    }

    /** Execute the renaming of the "column" component within tables to "cell", since that's the actual use. */
    protected void migrateTables(InstallContext ctx) throws PackageException {
        try {
            replaceResourceType(ctx, "composum/pages/components/composed/table/column", "composum/pages/components/composed/table/cell");
        } catch (Exception rex) {
            LOG.error(rex.getMessage(), rex);
            throw new PackageException(rex);
        }
    }

    private void replacePropertyName(InstallContext ctx, String resourceType, String oldPropertyName, String newPropertyName) throws RepositoryException {
        Session session = ctx.getSession();
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        Query query = queryManager.createQuery("/jcr:root/content//*[sling:resourceType='" + resourceType + "'][" + oldPropertyName + "]", Query.XPATH);
        QueryResult result = query.execute();
        NodeIterator it = result.getNodes();
        while (it.hasNext()) {
            Node node = it.nextNode();
            try {
                if (!node.hasProperty(oldPropertyName)) { continue; }
                Property prop = node.getProperty(oldPropertyName);
                String propPath = prop.getPath();
                String value = prop.getString();
                prop.remove();
                node.setProperty(newPropertyName, value);
                LOG.info("Migrated {}", propPath);
            } catch (RepositoryException e) {
                LOG.error("Trouble writing to prop {}", node.getPath());
                throw e;
            }
        }
    }

    protected void replaceResourceType(InstallContext ctx, String oldResourceType, String newResourceType) throws RepositoryException {
        Session session = ctx.getSession();
        QueryManager queryManager = session.getWorkspace().getQueryManager();
        Query query = queryManager.createQuery("/jcr:root/content//*[sling:resourceType='" + oldResourceType + "']", Query.XPATH);
        QueryResult result = query.execute();
        NodeIterator it = result.getNodes();
        while (it.hasNext()) {
            Node node = it.nextNode();
            Property prop = node.getProperty(CoreConstants.PROP_RESOURCE_TYPE);
            if (oldResourceType.equals(prop.getString())) {
                try {
                    prop.setValue(newResourceType);
                    LOG.info("Migrated {}", prop.getName());
                } catch (RepositoryException e) {
                    LOG.error("Trouble writing to prop {}", prop.getPath());
                    throw e;
                }
            }
        }
    }
}
