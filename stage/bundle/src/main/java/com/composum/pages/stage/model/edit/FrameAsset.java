package com.composum.pages.stage.model.edit;

import com.composum.pages.commons.model.ContentVersion;
import com.composum.pages.commons.model.File;
import com.composum.pages.commons.model.Model;

public class FrameAsset extends FrameModel {

    public File getFile() {
        Model delegate = getDelegate();
        return delegate instanceof File ? (File) delegate : null;
    }

    public boolean isVersionable() {
        File file = getFile();
        return file != null && file.isVersionable();
    }

    public ContentVersion.StatusModel getReleaseStatus() {
        File file = getFile();
        return file != null ? file.getReleaseStatus() : null;
    }

    public boolean isLocked() {
        File file = getFile();
        return file != null && file.isLocked();
    }

    public String getLockOwner() {
        File file = getFile();
        return file != null ? file.getLockOwner() : null;
    }

    public boolean isCheckedOut() {
        File file = getFile();
        return file != null && file.isCheckedOut();
    }
}
