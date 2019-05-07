package com.composum.pages.stage.model.edit.page;

import com.composum.pages.commons.PagesConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.sling.api.resource.ResourceResolver;
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
import java.util.Objects;
import java.util.regex.Matcher;

import static com.composum.pages.commons.PagesConstants.VERSION_DATE_FORMAT;

public class Versions extends PageModel {

    private static final Logger LOG = LoggerFactory.getLogger(Versions.class);

    public class VersionItem {

        public final Version version;

        private transient List<String> labels;

        public VersionItem(Version version) {
            this.version = version;
        }

        public String getId() {
            try {
                return version.getIdentifier();
            } catch (RepositoryException rex) {
                LOG.error(rex.getMessage(), rex);
                return null;
            }
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
                        Matcher matcher = PagesConstants.RELEASE_LABEL_PATTERN.matcher(label);
                        if (matcher.matches()) {
                            labels.add(matcher.group(1));
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
            return StringUtils.join(labels, ", ");
        }
    }

    private transient VersionManager versionManager;
    private transient VersionHistory versionHistory;
    private transient VersionItem currentVersion;
    private transient List<VersionItem> versionList;

    public VersionManager getVersionManager() {
        if (versionManager == null) {
            try {
                ResourceResolver resolver = getDelegate().getContext().getResolver();
                final JackrabbitSession session = Objects.requireNonNull((JackrabbitSession) resolver.adaptTo(Session.class));
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
