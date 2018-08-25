(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};

    (function (pages, core) {
        'use strict';

        pages.PageView = Backbone.View.extend({

            initialize: function (options) {
                this.$frame = this.$('.' + this.getCssBase());
                this.registerEventHandlers();
            },

            getCssBase: function () {
                return pages.const.frameClass;
            },

            registerEventHandlers: function () {
            }
        });

        pages.EditFrame = pages.PageView.extend({

            initialize: function (options) {
                this.log = log.getLogger('frame');
                pages.PageView.prototype.initialize.apply(this, [options]);
            },

            registerEventHandlers: function () {
                var e = pages.const.event;
                $(document).on(e.element.select + '.EditFrame', _.bind(this.selectElement, this));
                $(document).on(e.element.selected + '.EditFrame', _.bind(this.onElementSelected, this));
                $(document).on(e.element.inserted + '.EditFrame', _.bind(this.onElementChanged, this));
                $(document).on(e.element.changed + '.EditFrame', _.bind(this.onElementChanged, this));
                $(document).on(e.element.deleted + '.EditFrame', _.bind(this.onElementDeleted, this));
                $(document).on(e.content.inserted + '.EditFrame', _.bind(this.onElementChanged, this));
                $(document).on(e.content.changed + '.EditFrame', _.bind(this.onElementChanged, this));
                $(document).on(e.content.deleted + '.EditFrame', _.bind(this.onElementDeleted, this));
                $(document).on(e.page.selected + '.EditFrame', _.bind(this.onPageSelected, this));
                $(document).on(e.page.select + '.EditFrame', _.bind(this.selectPage, this));
                $(document).on(e.page.view + '.EditFrame', _.bind(this.onViewPage, this));
                $(document).on(e.page.inserted + '.EditFrame', _.bind(this.onElementChanged, this));
                $(document).on(e.page.changed + '.EditFrame', _.bind(this.onElementChanged, this));
                $(document).on(e.page.deleted + '.EditFrame', _.bind(this.onElementDeleted, this));
                $(document).on(e.site.select + '.EditFrame', _.bind(this.selectSite, this));
                $(document).on(e.site.created + '.EditFrame', _.bind(this.onSiteChanged, this));
                $(document).on(e.site.changed + '.EditFrame', _.bind(this.onSiteChanged, this));
                $(document).on(e.site.deleted + '.EditFrame', _.bind(this.onElementDeleted, this));
                pages.PageView.prototype.registerEventHandlers.apply(this);
                this.$frame.on('load.EditFrame', _.bind(this.onFrameLoad, this));
                core.unauthorizedDelegate = pages.handleUnauthorized;
            },

            ready: function () {
                var e = pages.const.event;
                pages.tools.navigationTabs.ready();
                var initialPath = this.$el.data('path');
                if (initialPath) {
                    if (this.log.getLevel() <= log.levels.DEBUG) {
                        this.log.debug('frame.trigger.' + e.page.select + '(' + initialPath + ')');
                    }
                    $(document).trigger(e.page.select, [initialPath]);
                }
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug('frame.trigger.' + e.ready + '(' + initialPath + ')');
                }
                $(document).trigger(e.ready);
            },

            onPageSelected: function (event, path) {
                if (this.currentPath !== path) {
                    if (this.log.getLevel() <= log.levels.DEBUG) {
                        this.log.debug('pages.EditFrame.onPageSelected(' + path + ')');
                    }
                    this.currentPath = path;
                    pages.getPageData(path, _.bind(function (data) {
                        if (data.meta && data.meta.site !== pages.current.site) {
                            pages.current.site = data.meta.site;
                            if (this.log.getLevel() <= log.levels.DEBUG) {
                                this.log.debug('frame.trigger.' + pages.const.event.site.selected + '(' + data.meta.site + ')');
                            }
                            $(document).trigger(pages.const.event.site.selected, [data.meta.site]);
                        }
                    }, this));
                    if (history.replaceState) {
                        history.replaceState(path, 'pages', core.getContextUrl('/bin/pages.html' + path));
                    }
                }
            },

            onFrameLoad: function (event) {
                if (!this.busy) {
                    this.busy = true;
                    var frameUrl = event.currentTarget.contentDocument.URL;
                    var url;
                    core.ajaxGet('/bin/cpm/nodes/node.resolve.json', {
                            data: {
                                url: frameUrl
                            }
                        }, _.bind(function (data) {
                            if (this.currentPath !== data.path) {
                                if (this.log.getLevel() <= log.levels.DEBUG) {
                                    this.log.debug('pages.EditFrame.onFrameLoad(' + frameUrl + '): ' + data.path);
                                }
                                url = new core.SlingUrl(frameUrl);
                                var displayMode = url.parameters ? url.parameters['pages.view'] : undefined;
                                if (!displayMode && pages.isEditMode()) {
                                    // reload with the right display mode if no mode specified in the URL
                                    // this is generally happens if the content navigation is used
                                    this.reloadPage(url.parameters, data.path);
                                } else {
                                    // no event for explicit 'preview' - probably site page scanning
                                    if (!displayMode || displayMode !== 'preview') {
                                        // trigger all necessary events after loading a different page
                                        pages.current.page = data.path;
                                        var eventData = [data.path, url.parameters];
                                        if (this.log.getLevel() <= log.levels.DEBUG) {
                                            this.log.debug('frame.trigger.' + pages.const.event.page.selected + '(' + data.path + ')');
                                        }
                                        $(document).trigger(pages.const.event.page.selected, eventData);
                                    }
                                }
                            } else {
                                url = new core.SlingUrl(frameUrl);
                                var locale = url.parameters ? url.parameters['pages.locale'] : undefined;
                                if (locale) {
                                    // get current locale from request if present to keep locale switching
                                    pages.current.locale = locale;
                                }
                                var select = this.selectOnLoad;
                                if (!select) {
                                    select = pages.toolbars.pageToolbar.getSelectedComponent();
                                }
                                if (select) {
                                    this.selectElement(undefined, select.name, select.path, select.type);
                                }
                            }
                            this.selectOnLoad = undefined;
                        }, this), undefined, _.bind(function (data) {
                            this.busy = false;
                        }, this)
                    );
                } else {
                    this.busy = false;
                }
            },

            selectSite: function (event, path) {
                if (pages.current.site !== path) {
                    if (this.log.getLevel() <= log.levels.DEBUG) {
                        this.log.debug('pages.EditFrame.selectSite(' + path + ')');
                    }
                    pages.current.site = path;
                    if (this.log.getLevel() <= log.levels.DEBUG) {
                        this.log.debug('frame.trigger.' + pages.const.event.site.selected + '(' + path + ')');
                    }
                    $(document).trigger(pages.const.event.site.selected, [path]);
                }
                this.selectPage(event, path);
            },

            selectPage: function (event, path, parameters) {
                if (pages.current.page !== path) {
                    if (this.log.getLevel() <= log.levels.DEBUG) {
                        this.log.debug('pages.EditFrame.selectPage(' + path + ')');
                    }
                    pages.current.page = path;
                    this.reloadPage(parameters);
                    if (this.log.getLevel() <= log.levels.DEBUG) {
                        this.log.debug('frame.trigger.' + pages.const.event.page.selected + '(' + path + ')');
                    }
                    $(document).trigger(pages.const.event.page.selected, [path]);
                } else {
                    if (this.log.getLevel() <= log.levels.DEBUG) {
                        this.log.debug('frame.trigger.' + pages.const.event.path.selected + '(' + path + ')');
                    }
                    $(document).trigger(pages.const.event.path.selected, [path]);
                }
            },

            reloadPage: function (parameters, path) {
                var pagePath = path || pages.current.page;
                if (pagePath) {
                    var frameUrl = new core.SlingUrl(core.getContextUrl(pagePath + '.html'));
                    if (parameters) {
                        frameUrl.parameters = parameters;
                    }
                    if (!frameUrl.parameters['pages.view']) {
                        frameUrl.parameters['pages.view'] = pages.current.mode.toLowerCase();
                    }
                    if (!frameUrl.parameters['pages.locale']) {
                        frameUrl.parameters['pages.locale'] = pages.current.locale;
                    }
                    frameUrl.build();
                    if (this.log.getLevel() <= log.levels.DEBUG) {
                        this.log.debug('pages.EditFrame.reloadPage(' + path + '): ' + frameUrl.url);
                    }
                    pages.retryIfUnauthorized(_.bind(function () {
                        this.$frame.attr('src', frameUrl.url);
                    }, this), frameUrl.url);
                }
            },

            reloadFrame: function () {
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug('pages.EditFrame.reloadFrame()');
                }
                window.location.reload();
            },

            selectElement: function (event, name, path, type) {
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug('pages.EditFrame.selectElement(' + path + ')');
                }
                if (path) {
                    this.$frame[0].contentWindow.postMessage(pages.const.event.element.select
                        + JSON.stringify({name: name, path: path, type: type}), '*');
                } else {
                    this.$frame[0].contentWindow.postMessage(pages.const.event.element.select
                        + JSON.stringify({}), '*');
                }
            },

            onSiteChanged: function (event, path) {
                if (pages.current.page === path) {
                    this.reloadPage();
                }
            },

            onViewPage: function (event, path, parameters) {
                this.reloadPage(parameters, path);
            },

            onElementSelected: function (event, name, path, type) {
                if (!pages.current.element || pages.current.element.path !== path) {
                    pages.current.element = {name: name, path: path, type: type};
                    if (this.log.getLevel() <= log.levels.DEBUG) {
                        this.log.debug('pages.EditFrame.onElementSelected(' + path + ')');
                    }
                    if (path) {
                        this.$frame[0].contentWindow.postMessage(pages.const.event.element.selected
                            + JSON.stringify({name: name, path: path, type: type}), '*');
                    } else {
                        this.$frame[0].contentWindow.postMessage(pages.const.event.element.selected
                            + JSON.stringify({}), '*');
                    }
                }
            },

            onElementChanged: function (event, path) {
                if (path) {
                    if (this.log.getLevel() <= log.levels.DEBUG) {
                        this.log.debug('pages.EditFrame.onElementChanged(' + path + ')');
                    }
                    if (path === pages.current.site) {
                        // FIXME this.reloadFrame();
                    } else if (path.indexOf(pages.current.page) === 0) {
                        this.reloadPage();
                    }
                }
            },

            onElementDeleted: function (event, path) {
                if (path) {
                    if (this.log.getLevel() <= log.levels.DEBUG) {
                        this.log.debug('pages.EditFrame.onElementDeleted(' + path + ')');
                    }
                    if (path === pages.current.site || path === pages.current.page) {
                        // FIXME this.reloadFrame();
                    } else if (path.indexOf(pages.current.page) === 0) {
                        this.reloadPage();
                    }
                }
            }
        });

        pages.editFrame = core.getView('.' + pages.const.frameWrapperClass, pages.EditFrame);

    })(window.composum.pages, window.core);
})(window);
