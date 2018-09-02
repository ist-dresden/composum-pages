/**
 * the Pages edit frame UI component with the embedded content page
 */
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
                this.log = {
                    frame: log.getLogger('frame'),
                    dnd: log.getLogger("dnd")
                };
                pages.PageView.prototype.initialize.apply(this, [options]);
            },

            registerEventHandlers: function () {
                var e = pages.const.event;
                var id = '.EditFrame';
                $(document).on(e.dnd.object + id, _.bind(this.onDndObject, this));
                $(document).on(e.dnd.finished + id, _.bind(this.onDndFinished, this));
                $(document).on(e.element.select + id, _.bind(this.selectElement, this));
                $(document).on(e.element.selected + id, _.bind(this.onElementSelected, this));
                $(document).on(e.element.inserted + id, _.bind(this.onElementChanged, this));
                $(document).on(e.element.changed + id, _.bind(this.onElementChanged, this));
                $(document).on(e.element.deleted + id, _.bind(this.onElementDeleted, this));
                $(document).on(e.content.inserted + id, _.bind(this.onElementChanged, this));
                $(document).on(e.content.changed + id, _.bind(this.onElementChanged, this));
                $(document).on(e.content.deleted + id, _.bind(this.onElementDeleted, this));
                $(document).on(e.page.selected + id, _.bind(this.onPageSelected, this));
                $(document).on(e.page.select + id, _.bind(this.selectPage, this));
                $(document).on(e.page.view + id, _.bind(this.onViewPage, this));
                $(document).on(e.page.inserted + id, _.bind(this.onElementChanged, this));
                $(document).on(e.page.changed + id, _.bind(this.onElementChanged, this));
                $(document).on(e.page.deleted + id, _.bind(this.onElementDeleted, this));
                $(document).on(e.site.select + id, _.bind(this.selectSite, this));
                $(document).on(e.site.created + id, _.bind(this.onSiteChanged, this));
                $(document).on(e.site.changed + id, _.bind(this.onSiteChanged, this));
                $(document).on(e.site.deleted + id, _.bind(this.onElementDeleted, this));
                pages.PageView.prototype.registerEventHandlers.apply(this);
                this.$frame.on('load' + id, _.bind(this.onFrameLoad, this));
                // register the central re-authentication callback
                core.unauthorizedDelegate = pages.handleUnauthorized;
            },

            ready: function () {
                var e = pages.const.event;
                pages.tools.navigationTabs.ready();
                var initialPath = this.$el.data('path');
                if (initialPath) {
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('frame.trigger.' + e.page.select + '(' + initialPath + ')');
                    }
                    $(document).trigger(e.page.select, [initialPath]);
                }
                if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                    this.log.frame.debug('frame.trigger.' + e.ready + '(' + initialPath + ')');
                }
                $(document).trigger(e.ready);
                window.addEventListener("message", _.bind(this.onMessage, this), false);
            },

            onPageSelected: function (event, path) {
                if (this.currentPath !== path) {
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.onPageSelected(' + path + ')');
                    }
                    this.currentPath = path;
                    pages.getPageData(path, _.bind(function (data) {
                        if (data.meta && data.meta.site !== pages.current.site) {
                            pages.current.site = data.meta.site;
                            if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                                this.log.frame.debug('frame.trigger.' + pages.const.event.site.selected + '(' + data.meta.site + ')');
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
                                if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                                    this.log.frame.debug('pages.EditFrame.onFrameLoad(' + frameUrl + '): ' + data.path);
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
                                        if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                                            this.log.frame.debug('frame.trigger.' + pages.const.event.page.selected + '(' + data.path + ')');
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
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.selectSite(' + path + ')');
                    }
                    pages.current.site = path;
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('frame.trigger.' + pages.const.event.site.selected + '(' + path + ')');
                    }
                    $(document).trigger(pages.const.event.site.selected, [path]);
                }
                this.selectPage(event, path);
            },

            selectPage: function (event, path, parameters) {
                if (pages.current.page !== path) {
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.selectPage(' + path + ')');
                    }
                    pages.current.page = path;
                    this.reloadPage(parameters);
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('frame.trigger.' + pages.const.event.page.selected + '(' + path + ')');
                    }
                    $(document).trigger(pages.const.event.page.selected, [path]);
                } else {
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('frame.trigger.' + pages.const.event.path.selected + '(' + path + ')');
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
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.reloadPage(' + path + '): ' + frameUrl.url);
                    }
                    pages.retryIfUnauthorized(_.bind(function () {
                        this.$frame.attr('src', frameUrl.url);
                    }, this), frameUrl.url);
                }
            },

            reloadFrame: function () {
                if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                    this.log.frame.debug('pages.EditFrame.reloadFrame()');
                }
                window.location.reload();
            },

            selectElement: function (event, name, path, type) {
                if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                    this.log.frame.debug('pages.EditFrame.selectElement(' + path + ')');
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
                    pages.current.element = new pages.Reference(name, path, type);
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.onElementSelected(' + path + ')');
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
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.onElementChanged(' + path + ')');
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
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.onElementDeleted(' + path + ')');
                    }
                    if (path === pages.current.site || path === pages.current.page) {
                        // FIXME this.reloadFrame();
                    } else if (path.indexOf(pages.current.page) === 0) {
                        this.reloadPage();
                    }
                }
            },

            //
            // DnD event handler
            //

            onDndObject: function (event, object, origin) {
                pages.current.dnd.object = object;
                if (origin !== 'message') {
                    var e = pages.const.event;
                    if (this.log.dnd.getLevel() <= log.levels.DEBUG) {
                        this.log.dnd.debug('frame.message.send.' + e.dnd.object + JSON.stringify(object));
                    }
                    this.$frame[0].contentWindow.postMessage(e.dnd.object + JSON.stringify(object), '*');
                }
            },

            onDndFinished: function (event, origin) {
                pages.current.dnd.object = undefined;
                if (origin !== 'message') {
                    var e = pages.const.event;
                    if (this.log.dnd.getLevel() <= log.levels.DEBUG) {
                        this.log.dnd.debug('frame.message.send.' + e.dnd.finished);
                    }
                    this.$frame[0].contentWindow.postMessage(e.dnd.finished + '{}', '*');
                }
            },

            //
            // content iframe message handler
            //

            onMessage: function (event) {
                var e = pages.const.event;
                var message = e.messagePattern.exec(event.data);
                if (this.log.frame.getLevel() <= log.levels.TRACE) {
                    this.log.frame.trace('frame.message.on: "' + event.data + '"...');
                }
                if (message) {
                    var args = JSON.parse(message[2]);
                    switch (message[1]) {
                        case e.element.selected:
                            // transform selection messages into the corresponding event for the edit frame components
                            if (args.path) {
                                this.log.frame.debug('frame.trigger.' + e.path.selected + '(' + args.path + ')');
                                $(document).trigger(e.path.selected, [args.path]);
                                var eventData = [
                                    args.name,
                                    args.path,
                                    args.type
                                ];
                                this.log.frame.debug('frame.trigger.' + e.element.selected + '(' + args.path + ')');
                                $(document).trigger(e.element.selected, eventData);
                            } else {
                                this.log.frame.debug('frame.trigger.' + e.path.selected + '()');
                                $(document).trigger(e.path.selected, []);
                                this.log.frame.debug('frame.trigger.' + e.element.selected + '()');
                                $(document).trigger(e.element.selected, []);
                            }
                            break;
                        case e.page.containerRefs:
                            // forward container references list to the edit frame components
                            this.log.frame.trace('frame.message.on.' + e.page.containerRefs + '(' + message[2] + ')');
                            $(document).trigger(e.page.containerRefs, [args]);
                            break;
                        case e.dialog.edit:
                            // opens an edit dialog to perform editing of the content of the path transmitted
                            this.log.frame.trace('frame.message.on.dialog.edit(' + message[2] + ')');
                            if (args.target) {
                                var url = undefined;
                                if (args.dialog) {
                                    url = args.dialog.url;
                                }
                                pages.dialogs.openEditDialog(args.target.name, args.target.path, args.target.type,
                                    undefined/*context*/, url,
                                    function (dialog) {
                                        if (args.values) {
                                            dialog.applyData(args.values);
                                        }
                                    });
                            }
                            break;
                        case e.trigger:
                            // triggers an event in the frame document context
                            this.log.frame.debug('frame.message.on.' + args.event + '(' + message[2] + ')');
                            $(document).trigger(args.event, args.data);
                            break;
                        case e.dialog.alert:
                            // displays an alert message by opening an alert dialog
                            this.log.frame.trace('frame.message.on.dialog.alert(' + message[2] + ')');
                            core.alert(args.type, args.title, args.message, args.data);
                            break;
                        //
                        //  DnD operations triggered by the content page and sent to the frame
                        //
                        case e.dnd.object:
                            // triggers a 'dnd:object' event eith the data of an object dragged on the content page
                            if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                                this.log.frame.debug('frame.message.on.' + e.dnd.object + JSON.stringify(args));
                            }
                            $(document).trigger(e.dnd.object, [args, 'message']);
                            break;
                        case e.dnd.drop:
                            // executes the DnD drop operation triggered in the content page using the data from the content
                            this.log.frame.info('frame.message.on.' + e.element.drop + '(' + message[2] + ')');
                            if (args.object && args.target) {
                                pages.actions.dnd.doDrop({
                                    container: {
                                        reference: new pages.Reference(args.target.container.reference)
                                    },
                                    before: args.target.before ? {
                                        reference: new pages.Reference(args.target.before.reference)
                                    } : undefined
                                }, {
                                    type: args.object.type,
                                    reference: new pages.Reference(args.object.reference)
                                });
                            }
                            break;
                        case e.dnd.finished:
                            // triggers the 'dnd:finished' event in the frames context if the end is detected in the content
                            if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                                this.log.frame.debug('frame.message.on.' + e.dnd.finished);
                            }
                            $(document).trigger(e.dnd.finished, [event, 'message']);
                            break;
                    }
                }
            }
        });

        pages.editFrame = core.getView('.' + pages.const.frameWrapperClass, pages.EditFrame);

    })(window.composum.pages, window.core);
})(window);
