package com.composum.pages.stage.model.edit;

import com.composum.pages.commons.model.Component;
import com.composum.sling.core.BeanContext;
import org.apache.sling.api.resource.Resource;

public class FrameComponent extends Component {

    public FrameComponent() {
    }

    public FrameComponent(BeanContext context, Resource resource) {
        super(context, resource);
    }

    @Override
    protected Resource determineResource(Resource initialResource) {
        String path = FrameElement.getElementPath(context);
        Resource frameResource = resolver.resolve(path);
        return super.determineResource(frameResource != null ? frameResource : initialResource);
    }
}
