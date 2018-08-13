package com.composum.pages.stage.model.edit;

import com.composum.pages.commons.model.Container;
import com.composum.pages.commons.model.Element;

import java.util.List;

public class FrameContainer extends FrameModel {

    public List<Element> getElements() {
        return ((Container)getDelegate()).getElements();
    }
}
