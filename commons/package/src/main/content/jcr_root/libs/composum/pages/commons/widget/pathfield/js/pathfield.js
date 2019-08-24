(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.widgets = window.composum.pages.widgets || {};

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            pathfield: {
                css: {
                    base: 'pathfield-widget'
                },
                profile: {
                    aspect: 'pathfield',
                    parent: 'parent'
                }
            }
        });

        widgets.PathFieldWidget = core.components.PathWidget.extend({

            initialize: function (options) {
                var e = pages.const.event;
                core.components.PathWidget.prototype.initialize.apply(this, [options]);
                $(document).on(e.scope.changed + '.pathField', _.bind(this.onScopeChanged, this));
            },

            profileAspect: function () {
                return widgets.const.pathfield.profile.aspect;
            },

            /**
             * extension for dialog initialization with profile value
             */
            getPath: function (callback) {
                var value = this.getValue();
                if (_.isFunction(callback)) {
                    if (!value) {
                        var p = widgets.const.pathfield.profile;
                        value = widgets.profile.get(this.profileAspect(), p.parent);
                    }
                    callback(value);
                } else {
                    return value;
                }
            },

            /**
             * store value on profile for the next dialog initialization
             */
            setValue: function (value, triggerChange) {
                core.components.PathWidget.prototype.setValue.apply(this, [value, triggerChange]);
                if (value) {
                    var p = widgets.const.pathfield.profile;
                    widgets.profile.set(this.profileAspect(), p.parent, core.getParentPath(value));
                    widgets.profile.save(this.profileAspect());
                }
            },

            onScopeChanged: function () {
                this.setRootPath(this.config.rootPath); // adjust configured root path
            },

            adjustRootPath: function (path) {
                var g = pages.const.profile.pages;
                if (pages.current.site) {
                    // replace a ${site} placeholder in the configured root with the path of the current site
                    path = path.replace(/\${site}/, pages.current.site);
                }
                if (pages.current.page) {
                    // replace a ${page} placeholder in the configured root with the path of the current page
                    path = path.replace(/\${page}/, pages.current.page);
                }
                path = core.components.PathWidget.prototype.adjustRootPath.apply(this, [path]);
                var scope = pages.getScope();
                if (scope === 'site' && pages.current.site) {
                    if (path.indexOf(pages.current.site) !== 0) {
                        // restrict the path to the current site if 'site' scope is set
                        path = pages.current.site;
                    }
                }
                return path;
            }
        });

        window.widgets.register('.widget.' + widgets.const.pathfield.css.base, widgets.PathFieldWidget);


    })(window.composum.pages.widgets, window.composum.pages, window.core);
})(window);
