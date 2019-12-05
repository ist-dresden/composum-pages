package com.composum.pages.commons.servlet.edit;

import com.composum.pages.commons.util.DateConverter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.servlets.post.SlingPostConstants;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * the strategy to collect properties from reaquest parameters
 * extracted from org.apache.sling.servlets.post.impl.operations.AbstractCreateOperation
 */
public class PostServletProperties {

    public static class RequestValueMap extends ValueMapDecorator {

        public RequestValueMap(Map<String, Object> base) {
            super(base);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T get(String name, Class<T> type) {
            T value = super.get(name, type);
            Object object;
            if (value == null && (object = get(name)) != null) {
                if (Calendar.class.equals(type)) {
                    value = (T) DateConverter.convert(object);
                }
            }
            return value;
        }
    }

    protected final LinkedHashMap<String, PostServletProperties.RequestProperty> values = new LinkedHashMap<>();
    protected final Resource resource;

    private transient RequestValueMap valueMap;

    public ValueMap getValueMap() {
        if (valueMap == null) {
            HashMap<String, Object> map = new HashMap<>();
            for (Map.Entry<String, RequestProperty> entry : values.entrySet()) {
                RequestProperty property = entry.getValue();
                if (property.hasValues()) {
                    String key = entry.getKey();
                    if (key.startsWith(resource.getPath() + "/")) {
                        key = key.substring(resource.getPath().length() + 1);
                    }
                    String[] asString = property.getStringValues();
                    map.put(key, property.hasMultiValueTypeHint() ? asString
                            : (asString == null || asString.length < 1 ? null : asString[0]));
                }
            }
            valueMap = new RequestValueMap(map);
        }
        return valueMap;
    }

    /**
     * Collects the properties that form the content to be written back to the
     * resource tree.
     */
    public PostServletProperties(@Nonnull final SlingHttpServletRequest request) {

        this.resource = request.getResource();

        final boolean requireItemPrefix = requireItemPathPrefix(request);

        // walk the request parameters and collect the properties
        for (final Map.Entry<String, RequestParameter[]> e : request.getRequestParameterMap().entrySet()) {
            final String paramName = e.getKey();

            if (ignoreParameter(paramName)) {
                continue;
            }

            // skip parameters that do not start with the save prefix
            if (requireItemPrefix && !hasItemPathPrefix(paramName)) {
                continue;
            }

            // ensure the paramName is an absolute property name
            final String propPath = toPropertyPath(paramName, resource);

            // @TypeHint example
            // <input type="text" name="./age" />
            // <input type="hidden" name="./age@TypeHint" value="long" />
            // causes the setProperty using the 'long' property type
            if (propPath.endsWith(SlingPostConstants.TYPE_HINT_SUFFIX)) {
                final RequestProperty prop = getOrCreateRequestProperty(propPath,
                        SlingPostConstants.TYPE_HINT_SUFFIX);

                final RequestParameter[] rp = e.getValue();
                if (rp.length > 0) {
                    prop.setTypeHintValue(rp[0].getString());
                }

                continue;
            }

            // @DefaultValue
            if (propPath.endsWith(SlingPostConstants.DEFAULT_VALUE_SUFFIX)) {
                final RequestProperty prop = getOrCreateRequestProperty(propPath,
                        SlingPostConstants.DEFAULT_VALUE_SUFFIX);

                prop.setDefaultValues(e.getValue());

                continue;
            }

            // SLING-130: VALUE_FROM_SUFFIX means take the value of this
            // property from a different field
            // @ValueFrom example:
            // <input name="./Text@ValueFrom" type="hidden" value="fulltext" />
            // causes the JCR Text property to be set to the value of the
            // fulltext form field.
            if (propPath.endsWith(SlingPostConstants.VALUE_FROM_SUFFIX)) {
                final RequestProperty prop = getOrCreateRequestProperty(propPath,
                        SlingPostConstants.VALUE_FROM_SUFFIX);

                // @ValueFrom params must have exactly one value, else ignored
                if (e.getValue().length == 1) {
                    final String refName = e.getValue()[0].getString();
                    final RequestParameter[] refValues = request.getRequestParameters(refName);
                    if (refValues != null) {
                        prop.setValues(refValues);
                    }
                }

                continue;
            }

            // SLING-458: Allow Removal of properties prior to update
            // @Delete example:
            // <input name="./Text@Delete" type="hidden" />
            // causes the JCR Text property to be deleted before update
            if (propPath.endsWith(SlingPostConstants.SUFFIX_DELETE)) {
                final RequestProperty prop = getOrCreateRequestProperty(propPath, SlingPostConstants.SUFFIX_DELETE);

                prop.setDelete(true);

                continue;
            }

            // SLING-455: @MoveFrom means moving content to another location
            // @MoveFrom example:
            // <input name="./Text@MoveFrom" type="hidden" value="/tmp/path" />
            // causes the JCR Text property to be set by moving the /tmp/path
            // property to Text.
            if (propPath.endsWith(SlingPostConstants.SUFFIX_MOVE_FROM)) {
                final RequestProperty prop = getOrCreateRequestProperty(propPath,
                        SlingPostConstants.SUFFIX_MOVE_FROM);

                // @MoveFrom params must have exactly one value, else ignored
                if (e.getValue().length == 1) {
                    final String sourcePath = e.getValue()[0].getString();
                    prop.setRepositorySource(sourcePath, true);
                }

                continue;
            }

            // SLING-455: @CopyFrom means moving content to another location
            // @CopyFrom example:
            // <input name="./Text@CopyFrom" type="hidden" value="/tmp/path" />
            // causes the JCR Text property to be set by copying the /tmp/path
            // property to Text.
            if (propPath.endsWith(SlingPostConstants.SUFFIX_COPY_FROM)) {
                final RequestProperty prop = getOrCreateRequestProperty(propPath,
                        SlingPostConstants.SUFFIX_COPY_FROM);

                // @MoveFrom params must have exactly one value, else ignored
                if (e.getValue().length == 1) {
                    final String sourcePath = e.getValue()[0].getString();
                    prop.setRepositorySource(sourcePath, false);
                }

                continue;
            }

            // SLING-1412: @IgnoreBlanks
            // @Ignore example:
            // <input name="./Text" type="hidden" value="test" />
            // <input name="./Text" type="hidden" value="" />
            // <input name="./Text@String[]" type="hidden" value="true" />
            // <input name="./Text@IgnoreBlanks" type="hidden" value="true" />
            // causes the JCR Text property to be set by copying the /tmp/path
            // property to Text.
            if (propPath.endsWith(SlingPostConstants.SUFFIX_IGNORE_BLANKS)) {
                final RequestProperty prop = getOrCreateRequestProperty(propPath,
                        SlingPostConstants.SUFFIX_IGNORE_BLANKS);

                if (e.getValue().length == 1) {
                    prop.setIgnoreBlanks(true);
                }

                continue;
            }

            if (propPath.endsWith(SlingPostConstants.SUFFIX_USE_DEFAULT_WHEN_MISSING)) {
                final RequestProperty prop = getOrCreateRequestProperty(propPath,
                        SlingPostConstants.SUFFIX_USE_DEFAULT_WHEN_MISSING);

                if (e.getValue().length == 1) {
                    prop.setUseDefaultWhenMissing(true);
                }

                continue;
            }
            // @Patch
            // Example:
            // <input name="tags@TypeHint" value="String[]" type="hidden" />
            // <input name="tags@Patch"    value="true" type="hidden" />
            // <input name="tags"          value="+apple" type="hidden" />
            // <input name="tags"          value="-orange" type="hidden" />
            if (propPath.endsWith(SlingPostConstants.SUFFIX_PATCH)) {
                final RequestProperty prop = getOrCreateRequestProperty(propPath, SlingPostConstants.SUFFIX_PATCH);

                prop.setPatch(true);

                continue;
            }

            // plain property, create from values
            final RequestProperty prop = getOrCreateRequestProperty(propPath, null);
            prop.setValues(e.getValue());
        }
    }

    /**
     * Encapsulates all infos from the respective request parameters that are needed
     * to create the repository property
     */
    public static class RequestProperty {

        private static final RequestParameter[] EMPTY_PARAM_ARRAY = new RequestParameter[0];

        public static final String DEFAULT_IGNORE = SlingPostConstants.RP_PREFIX
                + "ignore";

        public static final String DEFAULT_NULL = SlingPostConstants.RP_PREFIX
                + "null";

        private final String path;

        private final String name;

        private final String parentPath;

        private RequestParameter[] values;

        private String[] stringValues;

        private String typeHint;

        private boolean hasMultiValueTypeHint;

        private RequestParameter[] defaultValues = EMPTY_PARAM_ARRAY;

        private boolean isDelete;

        private String repositoryResourcePath;

        private boolean isRepositoryResourceMove;

        private boolean ignoreBlanks;

        private boolean useDefaultWhenMissing;

        private boolean patch = false;

        public RequestProperty(String path) {
            assert path.startsWith("/");
            this.path = ResourceUtil.normalize(path);
            this.parentPath = ResourceUtil.getParent(path);
            this.name = ResourceUtil.getName(path);
        }

        public String getTypeHint() {
            return typeHint;
        }

        public boolean hasMultiValueTypeHint() {
            return this.hasMultiValueTypeHint;
        }

        public void setTypeHintValue(String typeHint) {
            if (typeHint != null && typeHint.endsWith("[]")) {
                this.typeHint = typeHint.substring(0, typeHint.length() - 2);
                this.hasMultiValueTypeHint = true;
            } else {
                this.typeHint = typeHint;
                this.hasMultiValueTypeHint = false;
            }
        }

        public String getPath() {
            return path;
        }

        public String getName() {
            return name;
        }

        public String getParentPath() {
            return parentPath;
        }

        public boolean hasValues() {
            if (useDefaultWhenMissing && defaultValues != null && defaultValues.length > 0) {
                return true;
            } else {
                if (ignoreBlanks) {
                    return (values != null && getStringValues().length > 0);
                } else {
                    return values != null;
                }
            }
        }

        public RequestParameter[] getValues() {
            return values;
        }

        public void setValues(RequestParameter[] values) {
            this.values = values;
        }

        public RequestParameter[] getDefaultValues() {
            return defaultValues;
        }

        public void setDefaultValues(RequestParameter[] defaultValues) {
            if (defaultValues == null) {
                this.defaultValues = EMPTY_PARAM_ARRAY;
            } else {
                this.defaultValues = defaultValues;
            }
        }

        public boolean isFileUpload() {
            return values != null && !values[0].isFormField();
        }

        /**
         * Checks if this property provides any values. this is the case if one of
         * the values is not empty or if the default handling is not 'ignore'
         *
         * @return <code>true</code> if this property provides values
         */
        public boolean providesValue() {
            // should void double creation of string values
            String[] sv = getStringValues();
            if (sv == null) {
                // is missleading return type. but means that property should not
                // get auto-create values
                return true;
            }
            for (String s : sv) {
                if (!s.equals("")) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Returns the assembled string array out of the provided request values and
         * default values.
         *
         * @return a String array or <code>null</code> if the property needs to be
         * removed.
         */
        public String[] getStringValues() {
            if (stringValues == null) {
                if (values == null && useDefaultWhenMissing) {
                    stringValues = new String[]{defaultValues[0].getString()};
                } else if (values != null) {
                    if (values.length > 1) {
                        // TODO: how the default values work for MV props is not very
                        // clear
                        List<String> stringValueList = new ArrayList<>(values.length);
                        for (RequestParameter requestParameter : values) {
                            String value = requestParameter.getString();
                            if ((!ignoreBlanks) || value.length() > 0) {
                                stringValueList.add(value);
                            }
                        }
                        stringValues = stringValueList.toArray(new String[0]);
                    } else {
                        String value = values[0].getString();
                        if (value.equals("")) {
                            if (ignoreBlanks) {
                                return new String[0];
                            } else {
                                if (defaultValues.length == 1) {
                                    String defValue = defaultValues[0].getString();
                                    if (defValue.equals(DEFAULT_IGNORE)) {
                                        // ignore means, do not create empty values
                                        return new String[0];
                                    } else if (defValue.equals(DEFAULT_NULL)) {
                                        // null means, remove property if exist
                                        return null;
                                    }
                                    value = defValue;
                                }
                            }
                        }
                        stringValues = new String[]{value};
                    }
                }
            }
            return stringValues;
        }

        /**
         * Specifies whether this property should be deleted before any new content
         * is to be set according to the values stored.
         *
         * @param isDelete <code>true</code> if the repository item described by
         *                 this is to be deleted before any other operation.
         */
        public void setDelete(boolean isDelete) {
            this.isDelete = isDelete;
        }

        /**
         * Returns <code>true</code> if the repository item described by this is
         * to be deleted before setting new content to it.
         */
        public boolean isDelete() {
            return isDelete;
        }

        /**
         * Sets the path of the repository item from which the content for this
         * property is to be copied or moved. The path may be relative in which case
         * it will be resolved relative to the absolute path of this property.
         *
         * @param sourcePath The path of the repository item to get the content from
         * @param isMove     <code>true</code> if the source content is to be moved,
         *                   otherwise the source content is copied from the repository
         *                   item.
         */
        public void setRepositorySource(String sourcePath, boolean isMove) {

            // make source path absolute
            if (!sourcePath.startsWith("/")) {
                sourcePath = getParentPath() + "/" + sourcePath;
                sourcePath = ResourceUtil.normalize(sourcePath);
            }

            this.repositoryResourcePath = sourcePath;
            this.isRepositoryResourceMove = isMove;
        }

        /**
         * Returns <code>true</code> if the content of this property is to be set
         * by moving content from another repository item.
         *
         * @see #getRepositorySource()
         */
        public boolean hasRepositoryMoveSource() {
            return isRepositoryResourceMove;
        }

        /**
         * Returns <code>true</code> if the content of this property is to be set
         * by copying content from another repository item.
         *
         * @see #getRepositorySource()
         */
        public boolean hasRepositoryCopySource() {
            return getRepositorySource() != null && !hasRepositoryMoveSource();
        }

        /**
         * Returns the absolute path of the repository item from which the content
         * for this property is to be copied or moved.
         *
         * @see #hasRepositoryCopySource()
         * @see #hasRepositoryMoveSource()
         * @see #setRepositorySource(String, boolean)
         */
        public String getRepositorySource() {
            return repositoryResourcePath;
        }

        public void setIgnoreBlanks(boolean b) {
            ignoreBlanks = b;
        }

        public void setUseDefaultWhenMissing(boolean b) {
            useDefaultWhenMissing = b;
        }

        public void setPatch(boolean b) {
            patch = b;
        }

        /**
         * Returns whether this property is to be handled as a multi-value property
         * seen as set.
         */
        public boolean isPatch() {
            return patch;
        }
    }

    /**
     * Returns true if any of the request parameters starts with
     * {@link SlingPostConstants#ITEM_PREFIX_RELATIVE_CURRENT <code>./</code>}.
     * In this case only parameters starting with either of the prefixes
     * {@link SlingPostConstants#ITEM_PREFIX_RELATIVE_CURRENT <code>./</code>},
     * {@link SlingPostConstants#ITEM_PREFIX_RELATIVE_PARENT <code>../</code>}
     * and {@link SlingPostConstants#ITEM_PREFIX_ABSOLUTE <code>/</code>} are
     * considered as providing content to be stored. Otherwise all parameters
     * not starting with the command prefix <code>:</code> are considered as
     * parameters to be stored.
     *
     * @param request The http request
     * @return If a prefix is required.
     */
    protected boolean requireItemPathPrefix(
            SlingHttpServletRequest request) {

        boolean requirePrefix = false;

        Enumeration<?> names = request.getParameterNames();
        while (names.hasMoreElements() && !requirePrefix) {
            String name = (String) names.nextElement();
            requirePrefix = name.startsWith(SlingPostConstants.ITEM_PREFIX_RELATIVE_CURRENT);
        }

        return requirePrefix;
    }

    /**
     * Returns <code>true</code> if the <code>name</code> starts with either
     * of the prefixes
     * {@link SlingPostConstants#ITEM_PREFIX_RELATIVE_CURRENT <code>./</code>},
     * {@link SlingPostConstants#ITEM_PREFIX_RELATIVE_PARENT <code>../</code>}
     * and {@link SlingPostConstants#ITEM_PREFIX_ABSOLUTE <code>/</code>}.
     *
     * @param name The name
     * @return {@code true} if the name has a prefix
     */
    protected boolean hasItemPathPrefix(String name) {
        return name.startsWith(SlingPostConstants.ITEM_PREFIX_ABSOLUTE)
                || name.startsWith(SlingPostConstants.ITEM_PREFIX_RELATIVE_CURRENT)
                || name.startsWith(SlingPostConstants.ITEM_PREFIX_RELATIVE_PARENT);
    }

    /**
     * Returns the <code>paramName</code> as an absolute (unnormalized) property
     * path by prepending the response path (<code>response.getPath</code>) to
     * the parameter name if not already absolute.
     */
    protected String toPropertyPath(String paramName, Resource resource) {
        if (!paramName.startsWith("/")) {
            paramName = ResourceUtil.normalize(resource.getPath() + '/' + paramName);
        }

        return paramName;
    }

    /**
     * Returns the request property for the given property path. If such a
     * request property does not exist yet it is created and stored in the
     * <code>props</code>.
     *
     * @param paramName The absolute path of the property including the
     *                  <code>suffix</code> to be looked up.
     * @param suffix    The (optional) suffix to remove from the
     *                  <code>paramName</code> before looking it up.
     * @return The {@link RequestProperty} for the <code>paramName</code>.
     */
    protected RequestProperty getOrCreateRequestProperty(String paramName, String suffix) {
        if (suffix != null && paramName.endsWith(suffix)) {
            paramName = paramName.substring(0, paramName.length()
                    - suffix.length());
        }

        RequestProperty prop = values.get(paramName);
        if (prop == null) {
            prop = new RequestProperty(paramName);
            values.put(paramName, prop);
        }

        return prop;
    }

    /**
     * Returns <code>true</code> if the parameter of the given name should be
     * ignored.
     */
    protected boolean ignoreParameter(final String paramName) {
        // do not store parameters with names starting with sling:post
        if (paramName.startsWith(SlingPostConstants.RP_PREFIX)) {
            return true;
        }

        // SLING-298: skip form encoding parameter
        if (paramName.equals("_charset_")) {
            return true;
        }

        // SLING-2120: ignore parameter match ignoredParameterNamePattern
        return DEFAULT_IGNORED_PARAMETER_PATTERN.matcher(paramName).matches();
    }

    protected static final Pattern DEFAULT_IGNORED_PARAMETER_PATTERN = Pattern.compile("^j_.*");
}
