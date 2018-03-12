package com.composum.pages.commons.service;

import com.composum.pages.commons.model.ContentDriven;

import javax.annotation.Nonnull;

public interface ResourceManager<ModelType extends ContentDriven> {

    boolean isTemplate(@Nonnull ModelType model);
}
