/**
 * the Pages edit frame UI component with the embedded content page
 */
(function () {
    'use strict';
    CPM.namespace('pages');

    (function (pages, core) {

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
                $(document).on(e.content.state + id, _.bind(this.onPageState, this));
                $(document).on(e.content.view + id, _.bind(this.onViewPage, this));
                $(document).on(e.page.selected + id, _.bind(this.onPageSelected, this));
                $(document).on(e.page.select + id, _.bind(this.selectPage, this));
                $(document).on(e.page.reload + id, _.bind(this.onPageReload, this));
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
                    pages.trigger('frame.ready', e.page.select, [initialPath]);
                }
                window.addEventListener("message", _.bind(this.onMessage, this), false);
                // signal initialization end and let components switch to 'normal' mode...
                window.setTimeout(function () {
                    pages.trigger('frame.ready', e.pages.ready);
                }, 800);
            },

            getPageData: function (path, callback, failure, frameUrl) {
                var suffix = '', data;
                if (path) {
                    suffix = path;
                    data = {
                        'pages.locale': pages.getLocale()
                    };
                } else {
                    if (!frameUrl) {
                        frameUrl = this.$frame.attr('src');
                    }
                    if (frameUrl) {
                        var url = new core.SlingUrl(core.decodeUri(frameUrl));
                        data = {
                            'url': frameUrl
                        };
                        if (url.parameters && url.parameters['pages.locale']) {
                            data['pages.locale'] = url.parameters['pages.locale'];
                        }
                    }
                }
                if (suffix || data) {
                    core.ajaxGet(pages.const.url.get.pageData + (suffix ? core.encodePath(suffix) : ''),
                        {
                            data: data
                        }, callback, failure);
                } else {
                    if (_.isFunction(failure)) {
                        failure();
                    }
                }
            },

            onPageSelected: function (event, path) {
                if (this.currentPath !== path) {
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.onPageSelected(' + path + ')');
                    }
                    this.getPageData(undefined, _.bind(function (data) {
                        this.currentPath = data.path;
                        pages.setLocale(this.currentLocale = data.meta.language, data.meta.defaultLanguage);
                        if (data.meta && data.meta.site !== pages.current.site) {
                            pages.current.site = data.meta.site;
                            pages.trigger('frame.on.page.selected', pages.const.event.site.selected,
                                [data.meta.site]);
                        }
                    }, this));
                    if (history.replaceState) {
                        history.replaceState(path, 'pages', core.getContextUrl('/bin/pages.html' + core.encodePath(path)));
                        //history.replaceState(path, 'pages', this.$frame.attr('src'));
                    }
                }
            },

            onFrameLoad: function (event) {
                if (!this.busy) {
                    this.busy = true;
                    if (event.currentTarget.contentDocument) {
                        var frameUrl = event.currentTarget.contentDocument.URL;
                        this.getPageData(undefined, _.bind(function (data) {
                            if (this.currentPath !== data.path || this.currentLocale !== data.meta.language) {
                                var url = new core.SlingUrl(frameUrl);
                                if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                                    this.log.frame.debug('pages.EditFrame.onFrameLoad(' + frameUrl + '): ' + data.path);
                                }
                                var displayMode = url.parameters ? url.parameters['pages.view'] : undefined;
                                if (!displayMode && pages.isEditMode()) {
                                    // reload with the right display mode if no mode specified in the URL
                                    // this is generally happens if the content navigation is used
                                    var parameters = url.parameters || {};
                                    parameters['pages.locale'] = data.meta.language;
                                    this.reloadPage(parameters, data.path);
                                } else {
                                    // no event for explicit 'preview' - probably site page scanning
                                    if (!displayMode || displayMode !== 'preview') {
                                        // trigger all necessary events after loading a different page
                                        pages.current.page = data.path;
                                        var eventData = [data.path, url.parameters];
                                        pages.trigger('frame.on.load', pages.const.event.page.selected, eventData);
                                    }
                                }
                            } else {
                                var select = this.selectOnLoad;
                                if (!select) {
                                    select = pages.toolbars.pageToolbar.getSelectedComponent();
                                }
                                if (select) {
                                    pages.trigger('frame.on.load', pages.const.event.element.select,
                                        [new pages.Reference(select)]);
                                }
                            }
                            // get current locale from request if present to keep locale switching
                            pages.setLocale(this.currentLocale = data.meta.language, data.meta.defaultLanguage);
                            this.selectOnLoad = undefined;
                            this.busy = false;
                        }, this), _.bind(function (data) {
                            this.busy = false;
                        }, this), frameUrl);
                    }
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
                    pages.trigger('frame.site.select', pages.const.event.site.selected, [path]);
                }
                this.selectPage(event, path);
            },

            selectPage: function (event, path, parameters, elementRef) {
                if (pages.current.page !== path) {
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('frame.page.select(' + path + ')');
                    }
                    this.getPageData(path, _.bind(function (data) {
                        if (data.meta && data.meta.site !== pages.current.site) {
                            pages.current.site = data.meta.site;
                            pages.trigger('frame.page.select', pages.const.event.site.selected, [data.meta.site]);
                        }
                        pages.current.page = path;
                        if (elementRef) {
                            this.selectOnLoad = elementRef;
                        }
                        this.reloadPage(parameters);
                        pages.trigger('frame.page.select', pages.const.event.page.selected, [path]);
                    }, this), _.bind(function () {
                        pages.trigger('frame.page.select', pages.const.event.content.selected, ["/"]);
                        pages.trigger('frame.page.select', pages.const.event.site.selected);
                    }, this));
                }
            },

            onPageReload: function (event, pathOrRef) {
                var path = pathOrRef && pathOrRef.path ? pathOrRef.path : pathOrRef;
                this.reloadPage(undefined, path);
            },

            reloadPage: function (parameters, path, servicePath) {
                var pagePath = path || pages.current.page;
                if (pagePath) {
                    var pageUri = core.encodePath(pagePath);
                    var frameUrl = new core.SlingUrl(servicePath ? servicePath + pageUri : pageUri + '.html');
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
                        frameUrl.parameters['pages.locale'] = pages.getLocale();
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

            onPageState: function (event, reference) {
                this.$frame[0].contentWindow.postMessage(pages.const.event.content.state
                    + JSON.stringify(reference), '*');
            },

            onSiteChanged: function (event, path) {
                this.$frame[0].contentWindow.postMessage(pages.const.event.site.changed
                    + JSON.stringify([path]), '*');
            },

            onViewPage: function (event, path, parameters, servicePath) {
                this.reloadPage(parameters, path, servicePath);
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

            onElementInserted: function (event, parentRef, /*optional*/ resultRef) {
                if (parentRef && parentRef.path) {
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.onElementInserted(' + parentRef.path + "," + (resultRef ? resultRef.name : '*') + ')');
                    }
                    this.$frame[0].contentWindow.postMessage(pages.const.event.element.inserted
                        + JSON.stringify([parentRef, resultRef]), '*');
                }
            },

            onElementChanged: function (event, changedRef) {
                if (changedRef && changedRef.path) {
                    if (this.log.frame.getLevel() <= log.levels.DEBUG) {
                        this.log.frame.debug('pages.EditFrame.onElementChanged(' + changedRef.path + ')');
                    }
                    this.$frame[0].contentWindow.postMessage(pages.const.event.element.changed
                        + JSON.stringify({reference: changedRef}), '*');
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
                                pages.trigger('frame.msg.element.selected', e.element.selected, [args.reference]);
                            } else {
                                pages.trigger('frame.msg.element.selected', e.element.selected, []);
                            }
                            break;
                        case e.page.containerRefs:
                            // forward container references list to the edit frame components
                            if (this.log.frame.getLevel() <= log.levels.TRACE) {
                                this.log.frame.trace('frame.message.on.' + e.page.containerRefs + '(' + message[2] + ')');
                            }
                            pages.trigger('frame.msg.container.refs', e.page.containerRefs, [args]);
                            break;
                        case t.event:
                            // triggers an event in the frame document context
                            this.log.frame.debug('frame.message.on.' + args.event + '(' + message[2] + ')');
                            pages.trigger('frame.msg.event', args.event, args.data);
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
                        case t.dialog.generic:
                            // opens an edit dialog to perform editing of the content of the path transmitted
                            this.log.frame.trace('frame.message.on.dialog.generic(' + message[2] + ')');
                            if (args.target) {
                                pages.dialogs.openGenericDialog(args.dialog.url, eval(args.dialog.type), args.values,
                                    args.target.name, args.target.path, args.target.type);
                            }
                            break;
                        case t.dialog.custom:
                            // opens an custom dialog with an evaluated dialog type ('class')
                            this.log.frame.trace('frame.message.on.dialog.custom(' + message[2] + ')');
                            pages.dialogHandler.openLoadedDialog(args.url, eval(args.type), args.config,
                                args.init ? eval(args.init) : undefined,
                                args.trigger ? function () {
                                    pages.trigger(args.trigger.context, args.trigger.event, args.trigger.args);
                                } : undefined);
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
                            pages.trigger('frame.msg.dnd.object', e.dnd.object, [args, 'message']);
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
                            pages.trigger('frame.msg.dnd.finished', e.dnd.finished, [event, 'message']);
                            break;
                    }
                }
            }
        });

        pages.editFrame = core.getView('.' + pages.const.frameWrapperClass, pages.EditFrame);

    })(CPM.pages, CPM.core);
})();
