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
                $(document).on(e.element.inserted + id, _.bind(this.onElementInserted, this));
                $(document).on(e.element.changed + id, _.bind(this.onElementChanged, this));
                $(document).on(e.element.moved + id, _.bind(this.onElementMoved, this));
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

            /**
             * called from the edit frame template at the end of frame load (see: frame.jsp)
             */
            ready: function () {
                var e = pages.const.event;
                var initialPath = this.$el.data('path');
                if (initialPath) {
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('frame.trigger.' + e.page.select + '(' + initialPath + ')');
                    }
                    $(document).trigger(e.page.select, [initialPath]);
                }
                window.addEventListener("message", _.bind(this.onMessage, this), false);
                if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                    this.log.frame.debug('frame.trigger.' + e.ready + '(' + initialPath + ')');
                }
                // signal initialization end and let components switch to 'normal' mode...
                window.setTimeout(function () {
                    $(document).trigger(e.ready);
                }, 800);
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
                                    pages.log.debug('frame.trigger.' + pages.const.event.element.select + '(' + select.path + ')');
                                    $(document).trigger(pages.const.event.element.select, [new pages.Reference(select)]);
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

            selectPage: function (event, path, parameters, elementRef) {
                if (pages.current.page !== path) {
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.selectPage(' + path + ')');
                    }
                    pages.getPageData(path, _.bind(function (data) {
                        if (data.meta && data.meta.site !== pages.current.site) {
                            pages.current.site = data.meta.site;
                            if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                                this.log.frame.debug('frame.trigger.' + pages.const.event.site.selected + '(' + data.meta.site + ')');
                            }
                            $(document).trigger(pages.const.event.site.selected, [data.meta.site]);
                        }
                        pages.current.page = path;
                        if (elementRef) {
                            this.selectOnLoad = elementRef;
                        }
                        this.reloadPage(parameters);
                        if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                            this.log.frame.debug('frame.trigger.' + pages.const.event.page.selected + '(' + path + ')');
                        }
                        $(document).trigger(pages.const.event.page.selected, [path]);
                    }, this), _.bind(function () {
                        $(document).trigger(pages.const.event.content.selected, ["/"]);
                        $(document).trigger(pages.const.event.site.selected);
                    }, this));
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
                    if (!frameUrl.parameters['cpm.access']) {
                        frameUrl.parameters['cpm.access'] = 'AUTHOR'; // inside edit frame use always 'AUTHOR' mode
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

            selectElement: function (event, reference) {
                if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                    this.log.frame.debug('pages.EditFrame.selectElement(' + (reference ? reference.path : '') + ')');
                }
                if (reference && reference.path) {
                    this.$frame[0].contentWindow.postMessage(pages.const.event.element.select
                        + JSON.stringify({reference: reference}), '*');
                } else {
                    this.$frame[0].contentWindow.postMessage(pages.const.event.element.select
                        + JSON.stringify({}), '*');
                }
            },

            onSiteChanged: function (event, path) {
            },

            onViewPage: function (event, path, parameters) {
                this.reloadPage(parameters, path);
            },

            onElementSelected: function (event, reference) {
                if (!pages.current.element || !reference || pages.current.element.path !== reference.path) {
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.onElementSelected(' + (reference ? reference.path : '') + ')');
                    }
                    if (reference && reference.path) {
                        pages.current.element = reference;
                        this.$frame[0].contentWindow.postMessage(pages.const.event.element.selected
                            + JSON.stringify({reference: pages.current.element}), '*');
                    } else {
                        pages.current.element = undefined;
                        this.$frame[0].contentWindow.postMessage(pages.const.event.element.selected
                            + JSON.stringify({}), '*');
                    }
                }
            },

            onElementInserted: function (event, reference) {
                if (reference && reference.path) {
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.onElementInserted(' + reference.path + "," + reference.name + ')');
                    }
                    this.$frame[0].contentWindow.postMessage(pages.const.event.element.inserted
                        + JSON.stringify({reference: reference}), '*');
                }
            },

            onElementChanged: function (event, reference) {
                if (reference && reference.path) {
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.onElementChanged(' + reference.path + ')');
                    }
                    this.$frame[0].contentWindow.postMessage(pages.const.event.element.changed
                        + JSON.stringify({reference: reference}), '*');
                }
            },

            onElementMoved: function (event, oldRefOrPath, newRefOrPath) {
                var oldPath = oldRefOrPath && oldRefOrPath.path ? oldRefOrPath.path : oldRefOrPath;
                var newPath = newRefOrPath && newRefOrPath.path ? newRefOrPath.path : newRefOrPath;
                if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                    this.log.frame.debug('pages.EditFrame.onElementMoved(' + oldPath + ' -> ' + newPath + ')');
                }
                var oldParent = core.getParentPath(oldPath);
                var newParent = core.getParentPath(newPath);
                if (oldParent.indexOf(newParent) < 0) {
                    if (newParent.indexOf(oldParent) < 0) {
                        this.$frame[0].contentWindow.postMessage(pages.const.event.element.changed
                            + JSON.stringify({reference: {path: newParent}}), '*');
                    }
                    this.$frame[0].contentWindow.postMessage(pages.const.event.element.changed
                        + JSON.stringify({reference: {path: oldParent}}), '*');
                } else {
                    this.$frame[0].contentWindow.postMessage(pages.const.event.element.changed
                        + JSON.stringify({reference: {path: newParent}}), '*');
                }
            },

            onElementDeleted: function (event, reference) {
                if (reference && reference.path) {
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.onElementDeleted(' + reference.path + ')');
                    }
                    this.$frame[0].contentWindow.postMessage(pages.const.event.element.deleted
                        + JSON.stringify({reference: reference}), '*');
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
                var t = pages.const.trigger;
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
                            if (args.reference && args.reference.path) {
                                this.log.frame.debug('frame.trigger.' + e.element.selected + '(' + args.reference.path + ')');
                                $(document).trigger(e.element.selected, [args.reference]);
                            } else {
                                this.log.frame.debug('frame.trigger.' + e.element.selected + '()');
                                $(document).trigger(e.element.selected, []);
                            }
                            break;
                        case e.page.containerRefs:
                            // forward container references list to the edit frame components
                            this.log.frame.trace('frame.message.on.' + e.page.containerRefs + '(' + message[2] + ')');
                            $(document).trigger(e.page.containerRefs, [args]);
                            break;
                        case t.event:
                            // triggers an event in the frame document context
                            this.log.frame.debug('frame.message.on.' + args.event + '(' + message[2] + ')');
                            $(document).trigger(args.event, args.data);
                            break;
                        case t.action:
                            // triggers an event in the frame document context
                            this.log.frame.debug('frame.message.on.' + t.action + '(' + message[2] + ')');
                            pages.actions.trigger(event, args.action, args.reference);
                            break;
                        case t.dialog.edit:
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
                        case t.dialog.alert:
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
                            if (args.object) {
                                if (args.target) {
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
                                } else if (args.zone) {
                                    pages.actions.dnd.doZoneDrop(args.zone, {
                                        type: args.object.type,
                                        reference: new pages.Reference(args.object.reference)
                                    });
                                }
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
