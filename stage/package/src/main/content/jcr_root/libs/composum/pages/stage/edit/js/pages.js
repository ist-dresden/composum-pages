(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};

    (function (pages, core) {
        'use strict';

        pages.const = _.extend(pages.const || {}, {
            modes: {
                none: 'NONE',
                preview: 'PREVIEW', browse: 'BROWSE',
                edit: 'EDIT', develop: 'DEVELOP'
            },
            data: {
                body: {
                    mode: 'pages-mode',
                    locale: 'pages-locale'
                }
            },
            frameWrapperClass: 'composum-pages-stage-edit-frame_wrapper',
            frameClass: 'composum-pages-stage-edit-frame',
            editDialogsClass: 'composum-pages-stage-edit-dialogs',
            versionViewCssClass: 'composum-pages-version-view',
            event: {
                messagePattern: new RegExp('^([^\\{\\[]+)([\\{\\[].*[\\}\\]])$'),
                pageContainerRefs: 'page:containerRefs',
                componentSelected: 'component:selected',
                pathSelected: 'path:selected',
                insertComponent: 'component:insert',
                moveComponent: 'component:move',
                openEditDialog: 'dialog:edit',
                triggerEvent: 'event:trigger',
                alertMessage: 'dialog:alert'
            },
            url: {
                get: {
                    pageData: '/bin/cpm/pages/edit.pageData.json'
                },
                edit: {
                    insert: '/bin/cpm/pages/edit.insertComponent.json',
                    move: '/bin/cpm/pages/edit.moveComponent.json'
                }
            },
            sling: {
                resourceType: 'sling:resourceType'
            },
            login: {
                id: 'composum-platform-commons-login-dialog',
                url: '/libs/composum/platform/security/login/dialog.html'
            }
        });

        pages.profile = {

            aspectBase: 'composum.pages.',
            aspects: {},

            get: function (aspect, key, defaultValue) {
                var object = pages.profile.aspects[aspect];
                if (!object) {
                    var item = localStorage.getItem(pages.profile.aspectBase + aspect);
                    if (item) {
                        object = JSON.parse(item);
                        pages.profile.aspects[aspect] = object;
                    }
                }
                var value = undefined;
                if (object) {
                    value = key ? object[key] : object;
                }
                return value !== undefined ? value : defaultValue;
            },

            set: function (aspect, key, value) {
                var object = pages.profile.get(aspect, undefined, {});
                if (key) {
                    object[key] = value;
                } else {
                    object = value;
                }
                pages.profile.aspects[aspect] = object;
                pages.profile.save(aspect);
            },

            save: function (aspect) {
                var value = pages.profile.aspects[aspect];
                if (value) {
                    localStorage.setItem(pages.profile.aspectBase + aspect, JSON.stringify(value));
                }
            }
        };

        pages.$body = $('body');
        pages.current = {
            mode: pages.$body.data(pages.const.data.body.mode),
            locale: pages.$body.data(pages.const.data.body.locale),
            folder: undefined,
            site: undefined,
            page: undefined
        };

        switch (pages.current.mode) {
            case 'PREVIEW':
            case 'BROWSE':
                pages.profile.set('mode', 'preview', pages.current.mode.toLowerCase());
                break;
            case 'EDIT':
            case 'DEVELOP':
                pages.profile.set('mode', 'edit', pages.current.mode.toLowerCase());
                break;
        }

        pages.getPageData = function (path, callback) {
            core.ajaxGet(pages.const.url.get.pageData + path, {}, callback);
        };

        pages.versionsVisible = function () {
            return $('body').hasClass(pages.const.versionViewCssClass);
        };

        pages.contextTools = {

            initializers: [],

            getInitializers: function () {
                return pages.contextTools.initializers;
            },

            addTool: function (initializer) {
                if (!_.contains(pages.contextTools.initializers, initializer)) {
                    pages.contextTools.initializers.push(initializer);
                }
            }
        };

        //
        // Dialog container
        //

        pages.DialogHandler = Backbone.View.extend({

            openEditDialog: function (url, viewType, name, path, type, setupDialog, onNotFound) {
                core.ajaxGet(url + (path ? path : ''), {
                        data: {
                            name: name ? name : '',
                            type: type ? type : ''
                        }
                    },
                    _.bind(function (data) {
                        this.$el.append(data);
                        var $dialog = this.$el.children(':last-child');
                        var dialog = core.getWidget(this.el, $dialog[0], viewType);
                        if (dialog) {
                            dialog.data = {
                                name: name,
                                path: path,
                                type: type
                            };
                            if (_.isFunction(dialog.afterLoad)) {
                                dialog.afterLoad(name, path, type);
                            }
                            if (_.isFunction(setupDialog)) {
                                setupDialog(dialog);
                            }
                            if (dialog.useDefault) {
                                dialog.doSubmit(dialog.useDefault);
                            } else {
                                dialog.show();
                            }
                        }
                    }, this), _.bind(function (xhr) {
                        if (xhr.status === 404) {
                            if (_.isFunction(onNotFound)) {
                                onNotFound(name, path, type);
                            }
                        }
                    }, this));
            },

            openDialog: function (id, url, viewType, initView, callback) {
                this.getDialog(id, url, {}, viewType, _.bind(function (dialog) {
                    dialog.show(initView, callback);
                }, this));
            },

            getDialog: function (id, url, config, viewType, callback) {
                var dialog = core.getWidget(this.el, '#' + id, viewType);
                if (!dialog) {
                    core.ajaxGet(url, _.extend({dataType: 'html'}, config),
                        _.bind(function (data) {
                            this.$el.append(data);
                            dialog = core.getWidget(this.el, '#' + id, viewType);
                            if (dialog) {
                                if (_.isFunction(callback)) {
                                    callback(dialog);
                                }
                            }
                        }, this));
                } else {
                    if (_.isFunction(callback)) {
                        callback(dialog);
                    }
                }
                return dialog;
            }
        });

        pages.dialogHandler = core.getView('.' + pages.const.editDialogsClass, pages.DialogHandler);

        //
        // page frame message handler
        //

        window.addEventListener("message", function (event) {
            var message = pages.const.event.messagePattern.exec(event.data);
            if (message) {
                var args = JSON.parse(message[2]);
                switch (message[1]) {
                    case pages.const.event.componentSelected:
                        // transform selection messages into the corresponding event for the edit frame components
                        if (args.path) {
                            console.log('pages.trigger.' + pages.const.event.pathSelected + '(' + args.path + ')');
                            $(document).trigger(pages.const.event.pathSelected, [args.path]);
                            var eventData = [
                                args.name,
                                args.path,
                                args.type
                            ];
                            console.log('pages.trigger.' + pages.const.event.componentSelected + '(' + args.path + ')');
                            $(document).trigger(pages.const.event.componentSelected, eventData);
                        } else {
                            console.log('pages.trigger.' + pages.const.event.pathSelected + '()');
                            $(document).trigger(pages.const.event.pathSelected, []);
                            console.log('pages.trigger.' + pages.const.event.componentSelected + '()');
                            $(document).trigger(pages.const.event.componentSelected, []);
                        }
                        break;
                    case pages.const.event.pageContainerRefs:
                        // forward container references list to the edit frame components
                        console.log('pages.event.pageContainerRefs(' + message[2] + ')');
                        $(document).trigger(pages.const.event.pageContainerRefs, [args]);
                        break;
                    case pages.const.event.insertComponent:
                        // apply insert action messages from the edited page
                        console.log('pages.event.insertComponent(' + message[2] + ')');
                        if (args.type && args.target) {
                            core.ajaxPost(pages.const.url.edit.insert + args.target.path, {
                                elementType: args.type,
                                targetType: args.target.type,
                                before: args.before ? args.before : ''
                            }, {}, function (result) {
                                pages.editFrame.reloadPage();
                            }, function (xhr) {
                                core.alert('error', 'Error', 'Error on inserting component', xhr);
                            });
                        }
                        break;
                    case pages.const.event.moveComponent:
                        // apply move action messages from the edited page
                        console.log('pages.event.moveComponent(' + message[2] + ')');
                        if (args.source && args.target) {
                            core.ajaxPost(pages.const.url.edit.move + args.source, {
                                targetPath: args.target.path,
                                targetType: args.target.type,
                                before: args.before ? args.before : ''
                            }, {}, function (result) {
                                pages.editFrame.reloadPage();
                            }, function (xhr) {
                                core.alert('error', 'Error', 'Error on moving component', xhr);
                            });
                        }
                        break;
                    case pages.const.event.openEditDialog:
                        // opens an edit dialog to perform editing of the content of the path transmitted
                        console.log('pages.event.openEditDialog(' + message[2] + ')');
                        if (args.target) {
                            var url = undefined;
                            if (args.dialog) {
                                url = args.dialog.url;
                            }
                            pages.dialogs.openEditDialog(args.target.name, args.target.path, args.target.type, url,
                                function (dialog) {
                                    if (args.values) {
                                        dialog.applyData(args.values);
                                    }
                                });
                        }
                        break;
                    case pages.const.event.triggerEvent:
                        // triggers an event in the frame document context
                        console.log('pages.event.' + args.event + '(' + message[2] + ')');
                        $(document).trigger(args.event, args.data);
                        break;
                    case pages.const.event.alertMessage:
                        // displays an alert message by opening an alert dialog
                        console.log('pages.event.alertMessage(' + message[2] + ')');
                        core.alert(args.type, args.title, args.message, args.data);
                        break;
                }
            }
        }, false);

        //
        // login dialog and session expired (unauthorized) fallback
        //

        pages.LoginDialog = core.components.Dialog.extend({

            initialize: function (options) {
                core.components.Dialog.prototype.initialize.apply(this, [options]);
                this.form = core.getWidget(this.el, "form", core.components.FormWidget);
                this.form.$el.on('submit', _.bind(this.onSubmit, this));
                this.callsToRetry = [];
                this.showing = false;
            },

            resetOnShown: function () {
                core.components.Dialog.prototype.resetOnShown.apply(this);
                this.alert('warning', this.$el.data('message'));
            },

            onSubmit: function (event) {
                if (event) {
                    event.preventDefault();
                }
                this.submitForm();
                return false;
            },

            /**
             * collect all failed request calls to retry after successful login
             * @param retryThisFailedCall the call to retry after login
             */
            handleUnauthorized: function (retryThisFailedCall) {
                if (this.showing) {
                    // collect all failed calls during login
                    this.callsToRetry.push(retryThisFailedCall);
                } else {
                    // show login dialog and collect failed calls...
                    this.showing = true;
                    this.callsToRetry = [retryThisFailedCall];
                    this.show(undefined, _.bind(function () {
                        // retry after login all collected calls
                        this.callsToRetry.forEach(function (retryThisFailedCall) {
                            retryThisFailedCall();
                        });
                        this.showing = false;
                        this.callsToRetry = [];
                    }, this));
                }
            }
        });

        /**
         * the Pages Stage handler to resolve unauthorized request calls
         * @param retryThisFailedCall the call to retry after login
         * @see core.unauthorizedDelegate
         * @see core.ajaxCall
         */
        pages.handleUnauthorized = function (retryThisFailedCall) {
            var c = pages.const.login;
            pages.dialogHandler.getDialog(c.id, c.url, {}, pages.LoginDialog,
                _.bind(function (dialog) {
                    dialog.handleUnauthorized(retryThisFailedCall);
                }, this));
        };

        /**
         * check accessibility of the url as condition to execute a function
         * @param functionToCall the function to execute if url is accessible
         * @param urlToCheck the url to use as indicator
         */
        pages.retryIfUnauthorized = function (functionToCall, urlToCheck) {
            core.ajaxHead(urlToCheck, {}, _.bind(function () {
                functionToCall();
            }, this), _.bind(function () {
                pages.handleUnauthorized(functionToCall);
            }, this));
        };

    })(window.composum.pages, window.core);
})(window);
