package com.composum.pages.commons.setup;

import com.composum.sling.core.service.RepositorySetupService;
import com.composum.sling.core.setup.util.SetupUtil;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.vault.fs.io.Archive;
import org.apache.jackrabbit.vault.fs.spi.NodeTypeSet;
import org.apache.jackrabbit.vault.packaging.InstallContext;
import org.apache.jackrabbit.vault.packaging.InstallHook;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeManager;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static java.util.Arrays.asList;

@SuppressWarnings({"Duplicates"})
public class SetupHook implements InstallHook {

    private static final Logger LOG = LoggerFactory.getLogger(SetupHook.class);

    private static final String SETUP_ACLS = "/conf/composum/pages/commons/acl/setup.json";

    public static final String PAGES_USERS_PATH = "composum/pages/";
    public static final String PAGES_SYSTEM_USERS_PATH = "system/composum/pages/";

    public static final String PAGES_SERVICE_USER = "composum-pages-service";
    public static final String PAGES_TOKEN_SERVICE_USER = "composum-pages-token-service";

    public static final String ADMINISTRATORS_GROUP = "administrators";
    public static final String PAGES_ADMINISTRATORS = "composum-pages-administrators";
    public static final String PAGES_AUTHORS = "composum-pages-authors";

    public static final Map<String, List<String>> PAGES_USERS;
    public static final Map<String, List<String>> PAGES_SYSTEM_USERS;
    public static final Map<String, List<String>> PAGES_GROUPS;

    static {
        PAGES_USERS = new LinkedHashMap<>();
        PAGES_SYSTEM_USERS = new LinkedHashMap<>();
        PAGES_SYSTEM_USERS.put(PAGES_SYSTEM_USERS_PATH + PAGES_SERVICE_USER, asList(
                ADMINISTRATORS_GROUP,
                PAGES_ADMINISTRATORS
        ));
        PAGES_SYSTEM_USERS.put(PAGES_SYSTEM_USERS_PATH + PAGES_TOKEN_SERVICE_USER, Collections.singletonList(
                "composum-platform-users"
        ));
        PAGES_GROUPS = new LinkedHashMap<>();
        PAGES_GROUPS.put(PAGES_USERS_PATH + PAGES_ADMINISTRATORS, asList(
                "admin",
                PAGES_SERVICE_USER
        ));
        PAGES_GROUPS.put(PAGES_USERS_PATH + PAGES_AUTHORS, new ArrayList<>());
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
            NodeType siteType = nodeTypeManager.getNodeType("cpp:Site");
            if (!siteType.isNodeType("cpl:releaseRoot")) {
                LOG.warn("cpp:Site does not contain cpl:releaseRoot even after package installation - updating it.");
                Archive archive = ctx.getPackage().getArchive();
                Archive.Entry root = archive.getRoot();
                Archive.Entry metainf = root.getChild("META-INF");
                Archive.Entry vault = metainf.getChild("vault");
                Archive.Entry nodetypes = vault.getChild("nodetypes.cnd");
                try (InputStream stream = archive.openInputStream(archive.getEntry("/META-INF/vault/nodetypes.cnd"))) {
                    InputStreamReader cndReader = new InputStreamReader(stream);
                    CndImporter.registerNodeTypes(cndReader, session, true);
                }
                siteType = nodeTypeManager.getNodeType("cpp:Site");
                if (!siteType.isNodeType("cpl:releaseRoot")) {
                    LOG.warn("OK - we migrated the nodetypes so that cpp:Site does now contain cpl:releaseRoot");
                }
            } else {
                LOG.info("OK: cpp:Site contains cpl:releaseRoot");
            }
        } catch (Exception rex) {
            LOG.error(rex.getMessage(), rex);
            throw new PackageException(rex);
        }
    }


}
