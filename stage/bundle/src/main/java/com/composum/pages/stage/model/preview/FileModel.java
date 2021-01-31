package com.composum.pages.stage.model.preview;

import com.composum.pages.commons.model.File;
import com.composum.pages.stage.model.edit.FrameModel;

/**
 * a model of a frame component to handle file resources
 */
public class FileModel extends FrameModel {

    private transient File file;

    public File getFile() {
        if (file == null) {
            file = new File(getContext(), getResource());
        }
        return file;
    }

    public File.Type getFileType() {
        return getFile().getFileType();
    }
}
