package com.composum.pages.stage.model.edit.page;

import com.composum.sling.core.filter.StringFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Versions extends PageElement {

    private static final Logger LOG = LoggerFactory.getLogger(Versions.class);

    public static final String VERSION_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final StringFilter LABEL_FILTER = new StringFilter.BlackList("^composum-pages-.*$");

    public class VersionItem {

        public final Version version;

        private transient List<String> labels;

        public VersionItem(Version version) {
            this.version = version;
        }

        public String getName() {
            try {
                return version.getName();
            } catch (RepositoryException rex) {
                LOG.error(rex.getMessage(), rex);
                return ("???");
            }
        }

        public String getTime() {
            try {
                Calendar cal = version.getCreated();
                final SimpleDateFormat dateFormat = new SimpleDateFormat(VERSION_DATE_FORMAT);
                dateFormat.setTimeZone(cal.getTimeZone());
                return dateFormat.format(cal.getTime());
            } catch (RepositoryException rex) {
                LOG.error(rex.getMessage(), rex);
                return ("???");
            }
        }

        public List<String> getLabels() {
            if (labels == null) {
                labels = new ArrayList<>();
                try {
                    String[] versionLabels = versionHistory.getVersionLabels(version);
                    for (String label : versionLabels) {
                        if (LABEL_FILTER.accept(label)) {
                            labels.add(label);
                        }
                    }
                } catch (RepositoryException rex) {
                    LOG.error(rex.getMessage(), rex);
                }
            }
            return labels;
        }

        public String getLabelsString() {
            List<String> labels = getLabels();
            return labels.size() > 0 ? StringUtils.join(labels, ", ") : "no custom label";
        }
    }

    private transient VersionManager versionManager;
    private transient VersionHistory versionHistory;
    private transient VersionItem currentVersion;
    private transient List<VersionItem> versionList;

    public VersionManager getVersionManager() {
        if (versionManager == null) {
            try {
                final JackrabbitSession session = (JackrabbitSession) resolver.adaptTo(Session.class);
                versionManager = session.getWorkspace().getVersionManager();
            } catch (RepositoryException rex) {
                LOG.error(rex.getMessage(), rex);
            }
        }
        return versionManager;
    }

    public VersionHistory getVersionHistory() {
        if (versionHistory == null) {
            VersionManager manager = getVersionManager();
            if (manager != null) {
                try {
                    versionHistory = manager.getVersionHistory(getPage().getContent().getPath());
                } catch (RepositoryException rex) {
                    LOG.error(rex.getMessage(), rex);
                }
            }
        }
        return versionHistory;
    }

    public VersionItem getCurrentVersion() {
        if (currentVersion == null) {
            VersionManager manager = getVersionManager();
            if (manager != null) {
                try {
                    currentVersion = new VersionItem(manager.getBaseVersion(getPage().getContent().getPath()));
                } catch (RepositoryException rex) {
                    LOG.error(rex.getMessage(), rex);
                }
            }
        }
        return currentVersion;
    }

    public List<VersionItem> getVersionList() {
        if (versionList == null) {
            versionList = new ArrayList<>();
            VersionHistory history = getVersionHistory();
            if (history != null) {
                try {
                    final VersionIterator allVersions = history.getAllVersions();
                    while (allVersions.hasNext()) {
                        final Version version = allVersions.nextVersion();
                        if (!"jcr:rootVersion".equals(version.getName())) {
                            versionList.add(0, new VersionItem(version));
                        }
                    }
                } catch (RepositoryException rex) {
                    LOG.error(rex.getMessage(), rex);
                }
            }
        }
        return versionList;
    }
}
