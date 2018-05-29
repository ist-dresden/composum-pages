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
                pages.PageView.prototype.initialize.apply(this, [options]);
            },

            registerEventHandlers: function () {
                $(document).on('element:select.EditFrame', _.bind(this.selectComponent, this));
                $(document).on('element:selected.EditFrame', _.bind(this.onComponentSelected, this));
                $(document).on('component:changed.EditFrame', _.bind(this.onComponentChanged, this));
                $(document).on('component:deleted.EditFrame', _.bind(this.onComponentDeleted, this));
                $(document).on('content:inserted.EditFrame', _.bind(this.onComponentChanged, this));
                $(document).on('content:changed.EditFrame', _.bind(this.onComponentChanged, this));
                $(document).on('content:deleted.EditFrame', _.bind(this.onComponentDeleted, this));
                $(document).on('page:selected.EditFrame', _.bind(this.onPageSelected, this));
                $(document).on('page:select.EditFrame', _.bind(this.selectPage, this));
                $(document).on('page:view.EditFrame', _.bind(this.onViewPage, this));
                $(document).on('page:inserted.EditFrame', _.bind(this.onComponentChanged, this));
                $(document).on('page:changed.EditFrame', _.bind(this.onComponentChanged, this));
                $(document).on('page:deleted.EditFrame', _.bind(this.onComponentDeleted, this));
                $(document).on('site:select.EditFrame', _.bind(this.selectSite, this));
                $(document).on('site:created.EditFrame', _.bind(this.onSiteChanged, this));
                $(document).on('site:changed.EditFrame', _.bind(this.onSiteChanged, this));
                $(document).on('site:deleted.EditFrame', _.bind(this.onComponentDeleted, this));
                pages.PageView.prototype.registerEventHandlers.apply(this);
                var initialPath = this.$el.data('path');
                if (initialPath) {
                    console.log('frame.trigger.page:select(' + initialPath + ')');
                    $(document).trigger("page:select", [initialPath]);
                }
                this.$frame.on('load.EditFrame', _.bind(this.onFrameLoad, this));
                core.unauthorizedDelegate = pages.handleUnauthorized;
            },

            onPageSelected: function (event, path) {
                if (this.currentPath !== path) {
                    console.log('pages.EditFrame.onPageSelected(' + path + ')');
                    this.currentPath = path;
                    pages.getPageData(path, _.bind(function (data) {
                        if (data.meta && data.meta.site !== pages.current.site) {
                            pages.current.site = data.meta.site;
                            console.log('frame.trigger.page:selected(' + data.meta.site + ')');
                            $(document).trigger("site:selected", [data.meta.site]);
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
                                console.log('pages.EditFrame.onFrameLoad(' + frameUrl + '): ' + data.path);
                                url = new core.SlingUrl(frameUrl);
                                var displayMode = url.parameters ? url.parameters['pages.mode'] : undefined;
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
                                        console.log('frame.trigger.page:selected(' + data.path + ')');
                                        $(document).trigger("page:selected", eventData);
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
                                    this.selectComponent(undefined, select.name, select.path, select.type);
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
                    console.log('pages.EditFrame.selectSite(' + path + ')');
                    pages.current.site = path;
                    console.log('frame.trigger.site:selected(' + path + ')');
                    $(document).trigger("site:selected", [path]);
                }
                this.selectPage(event, path);
            },

            selectPage: function (event, path, parameters) {
                if (pages.current.page !== path) {
                    console.log('pages.EditFrame.selectPage(' + path + ')');
                    pages.current.page = path;
                    this.reloadPage(parameters);
                    console.log('frame.trigger.page:selected(' + path + ')');
                    $(document).trigger("page:selected", [path]);
                } else {
                    console.log('frame.trigger.path:selected(' + path + ')');
                    $(document).trigger("path:selected", [path]);
                }
            },

            reloadPage: function (parameters, path) {
                var pagePath = path || pages.current.page;
                if (pagePath) {
                    var frameUrl = new core.SlingUrl(core.getContextUrl(pagePath + '.html'));
                    if (parameters) {
                        frameUrl.parameters = parameters;
                    }
                    if (!frameUrl.parameters['pages.mode']) {
                        frameUrl.parameters['pages.mode'] = pages.current.mode.toLowerCase();
                    }
                    if (!frameUrl.parameters['pages.locale']) {
                        frameUrl.parameters['pages.locale'] = pages.current.locale;
                    }
                    frameUrl.build();
                    console.log('pages.EditFrame.reloadPage(' + path + '): ' + frameUrl.url);
                    pages.retryIfUnauthorized(_.bind(function () {
                        this.$frame.attr('src', frameUrl.url);
                    }, this), frameUrl.url);
                }
            },

            reloadFrame: function () {
                console.log('pages.EditFrame.reloadFrame()');
                window.location.reload();
            },

            selectComponent: function (event, name, path, type) {
                console.log('pages.EditFrame.selectComponent(' + path + ')');
                if (path) {
                    this.$frame[0].contentWindow.postMessage('element:select'
                        + JSON.stringify({name: name, path: path, type: type}), '*');
                } else {
                    this.$frame[0].contentWindow.postMessage('element:select'
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

            onComponentSelected: function (event, name, path, type) {
                console.log('pages.EditFrame.onComponentSelected(' + path + ')');
                if (path) {
                    this.$frame[0].contentWindow.postMessage('element:selected'
                        + JSON.stringify({name: name, path: path, type: type}), '*');
                } else {
                    this.$frame[0].contentWindow.postMessage('element:selected'
                        + JSON.stringify({}), '*');
                }
            },

            onComponentChanged: function (event, path) {
                if (path) {
                    console.log('pages.EditFrame.onComponentChanged(' + path + ')');
                    if (path === pages.current.site) {
                        // FIXME this.reloadFrame();
                    } else if (path.indexOf(pages.current.page) === 0) {
                        this.reloadPage();
                    }
                }
            },

            onComponentDeleted: function (event, path) {
                if (path) {
                    console.log('pages.EditFrame.onComponentDeleted(' + path + ')');
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
