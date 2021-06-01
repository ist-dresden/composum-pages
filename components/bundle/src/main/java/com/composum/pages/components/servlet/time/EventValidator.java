package com.composum.pages.components.servlet.time;

import com.composum.pages.commons.servlet.edit.AbstractValidationServlet;
import com.composum.pages.commons.servlet.edit.PostServletProperties;
import com.composum.sling.core.servlet.Status;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import java.util.Calendar;

@Component(service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Composum Pages Components Event Validator",
                ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=composum/pages/components/time/event/page",
                ServletResolverConstants.SLING_SERVLET_SELECTORS + "=validate",
                ServletResolverConstants.SLING_SERVLET_EXTENSIONS + "=json",
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
                ServletResolverConstants.SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST
        })
public class EventValidator extends AbstractValidationServlet {

    @Override
    protected void doValidate(@Nonnull SlingHttpServletRequest request,
                              @Nonnull SlingHttpServletResponse response,
                              @Nonnull final Status status,
                              @Nonnull final PostServletProperties properties) {
        ValueMap values = properties.getValueMap();
        Calendar date = values.get("date", Calendar.class);
        if (date == null) {
            status.validationError("", "Date", "a date must be specified");
        } else {
            Calendar dateEnd = values.get("dateEnd", Calendar.class);
            if (dateEnd != null) {
                if (date.after(dateEnd)) {
                    status.validationError("", "to", "start date must be before end date");
                }
            }
        }
    }
}
