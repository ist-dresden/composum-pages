package com.composum.pages.commons.setup;

import com.composum.sling.core.usermanagement.core.UserManagementService;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.vault.packaging.InstallContext;
import org.apache.jackrabbit.vault.packaging.InstallHook;
import org.apache.jackrabbit.vault.packaging.PackageException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class SetupHook implements InstallHook {

    private static final Logger LOG = LoggerFactory.getLogger(SetupHook.class);

    public static final String PAGES_USERS_PATH = "composum/pages/";
    public static final String PAGES_SYSTEM_USERS_PATH = "system/composum/pages/";

    public static final String PAGES_SERVICE_USER = "composum-pages-service";

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
        PAGES_GROUPS = new LinkedHashMap<>();
        PAGES_GROUPS.put(PAGES_USERS_PATH + PAGES_ADMINISTRATORS, asList(
                "admin",
                PAGES_SERVICE_USER
        ));
        PAGES_GROUPS.put(PAGES_USERS_PATH + PAGES_AUTHORS, new ArrayList<String>());
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void execute(InstallContext ctx) throws PackageException {
        switch (ctx.getPhase()) {
            case INSTALLED:
                LOG.info("installed: execute...");

                setupGroupsAndUsers(ctx);

                LOG.info("installed: execute ends.");
        }
    }

    protected void setupGroupsAndUsers(InstallContext ctx) {
        UserManagementService userManagementService = getService(UserManagementService.class);
        try {
            JackrabbitSession session = (JackrabbitSession) ctx.getSession();
            UserManager userManager = session.getUserManager();
            for (Map.Entry<String, List<String>> entry : PAGES_GROUPS.entrySet()) {
                Group group = userManagementService.getOrCreateGroup(session, userManager, entry.getKey());
                if (group != null) {
                    for (String memberName : entry.getValue()) {
                        userManagementService.assignToGroup(session, userManager, memberName, group);
                    }
                }
            }
            session.save();
            for (Map.Entry<String, List<String>> entry : PAGES_SYSTEM_USERS.entrySet()) {
                Authorizable user = userManagementService.getOrCreateUser(session, userManager, entry.getKey(), true);
                if (user != null) {
                    for (String groupName : entry.getValue()) {
                        userManagementService.assignToGroup(session, userManager, user, groupName);
                    }
                }
            }
            session.save();
            for (Map.Entry<String, List<String>> entry : PAGES_USERS.entrySet()) {
                Authorizable user = userManagementService.getOrCreateUser(session, userManager, entry.getKey(), false);
                if (user != null) {
                    for (String groupName : entry.getValue()) {
                        userManagementService.assignToGroup(session, userManager, user, groupName);
                    }
                }
            }
            session.save();
        } catch (RepositoryException rex) {
            LOG.error(rex.getMessage(), rex);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> type)  {
        Bundle serviceBundle = FrameworkUtil.getBundle(type);
        BundleContext serviceBundleContext = serviceBundle.getBundleContext();
        ServiceReference serviceReference = serviceBundleContext.getServiceReference(type.getName());
        return (T) serviceBundleContext.getService(serviceReference);
    }
}
