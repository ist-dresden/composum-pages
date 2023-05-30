/*
 * copyright (c) 2015ff IST GmbH Dresden, Germany - https://www.ist-software.com
 *
 * This software may be modified and distributed under the terms of the MIT license.
 */
(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.dialogs = window.composum.pages.dialogs || {};

    (function (dialogs, pages, core) {
        'use strict';

        dialogs.const = _.extend(dialogs.const || {}, {
            edit: {
                url: {
                    base: '/bin/cpm/pages/edit',
                    path: '/libs/composum/pages/stage/edit/default',
                    _edit: {
                        _folder: '/folder/dialog.html',
                        _source: '/file/dialog.html'
                    },
                    _add: {
                        path: '/content/dialog/add',
                        _page: '.page.html',
                        _folder: '.folder.html',
                        _file: '.file.html',
                        _source: '.source.html'
                    },
                    _remove: {
                        path: '/content/dialog/remove',
                        _page: '.page.html',
                        _folder: '.folder.html',
                        _file: '.file.html',
                        _component: '.component.html'
                    },
                    _move: {
                        dialog: '/content/dialog/move.html',
                        check: '/bin/cpm/pages/edit.isAllowedChild.json',
                        action: '/bin/cpm/pages/edit.moveContent.json'
                    },
                    _copy: {
                        dialog: '/content/dialog/copy.html',
                        check: '/bin/cpm/pages/edit.isAllowedChild.json',
                        action: '/bin/cpm/pages/edit.copyContent.json'
                    },
                    _rename: {
                        dialog: '/content/dialog/rename.html',
                        action: '/bin/cpm/pages/edit.renameContent.json'
                    },
                    _create: {
                        page: '.createPage.json'
                    },
                    _delete: {
                        page: '.deletePage.json'
                    },
                    _insert: '.insertElement.html',
                    _isTemplate: '.isTemplate.json',
                    _dialog: {
                        load: '.editDialog',
                        new: '.newDialog',
                        create: '.editDialog.create',
                        delete: '.editDialog.delete',
                        manage: '.editDialog.manage'
                    },
                    _upload: {
                        dialog: '/file/dialog/upload.html'
                    },
                    version: {
                        base: '/bin/cpm/platform/versions',
                        page: {
                            activate: {
                                _dialog: '/page/dialog/activate.html',
                                _action: '.activate.json'
                            },
                            revert: {
                                _dialog: '/page/dialog/revert.html',
                                _action: '.revert.json'
                            },
                            deactivate: {
                                _dialog: '/page/dialog/deactivate.html',
                                _action: '.deactivate.json'
                            }
                        },
                        folder: {
                            activate: {
                                _dialog: '/folder/dialog/activate.html',
                                _action: '.activate.json'
                            },
                            revert: {
                                _dialog: '/folder/dialog/revert.html',
                                _action: '.revert.json'
                            }
                        }
                    },
                    sites: {
                        list: '/libs/composum/pages/stage/edit/site/manager.html'
                    }
                },
                css: {
                    base: 'composum-pages-stage-edit-dialog',
                    _wizard: '_selector_wizard',
                    _form: '_form',
                    _hints: '_hints',
                    _tab: '_tab',
                    _tabbed: '_tabbed',
                    _tabList: '_tabs',
                    _tabContent: '_tabbed-content',
                    _parentPrimType: '_parent-primaryType',
                    _parentResType: '_parent-resourceType',
                    _order: '_child-order',
                    _pathField: '_path',
                    _addButton: '_button-add',
                    _removeButton: '_button-remove',
                    _openButton: '_button-open',
                    _deleteButton: '_button-delete',
                    _submitButton: '_button-submit',
                    _prevButton: '_button-prev',
                    _nextButton: '_button-next',
                    site: {
                        base: 'composum-pages-stage-site',
                        _tile: '_tile'
                    },
                    sites: {
                        base: 'composum-pages-stage-sites',
                        _list: '_sites-list',
                        _radio: '_radio',
                        _site: '_site'
                    },
                    code: {
                        editor: {
                            base: 'code-editor-dialog',
                            _toolbar: '_toolbar'
                        }
                    }
                },
                data: {
                    name: 'pages-edit-name',
                    path: 'pages-edit-path',
                    type: 'pages-edit-type',
                    prim: 'pages-edit-prim'
                },
                type: {
                    site: 'cpp:Site',
                    page: 'cpp:Page'
                }
            }
        });
        /** List where plugins can register themselves. Each should offer the method dialogInitializeView(dialog, element). */
        dialogs.const.dialogplugins = dialogs.const.dialogplugins || [];

        dialogs.ElementDialog = core.components.FormDialog.extend({

            initView: function () {
                core.components.FormDialog.prototype.initView.apply(this);
                for (const plugin of dialogs.const.dialogplugins) {
                    if (_.isFunction(plugin.dialogInitializeView)) {
                        plugin.dialogInitializeView(this, this.$el);
                    }
                }
            },

            initSubmit: function () {
                var c = dialogs.const.edit.css;
                this.$('.' + c.base + c._form).on('submit', _.bind(this.onSubmit, this));
            },

            getInitialFocus: function () {
                var initialFocus = this.$('[autofocus]').closest('.widget');
                if (initialFocus.length < 1) {
                    initialFocus = this.$('input:text:visible,textarea:visible').closest('.widget');
                }
                var widget;
                initialFocus.each(function () {
                    if (this.view && (_.isFunction(this.view.grabFocus))) {
                        return $(this);
                    }
                });
                initialFocus = this.$('input:text:visible,textarea:visible');
                return initialFocus.first();
            },

            onShown: function () {
                core.components.FormDialog.prototype.onShown.apply(this);
                var initialFocus = this.getInitialFocus();
                if (initialFocus && initialFocus.length > 0) {
                    var widget;
                    if ((widget = initialFocus[0].view) && _.isFunction(widget.grabFocus)) {
                        widget.grabFocus();
                    } else {
                        initialFocus.focus();
                    }
                }
            },

            /**
             * the validation strategy with support for an asynchronous validation call
             */
            doValidate: function (onSuccess, onError) {
                var valid = this.validateForm();
                var validationUrl = this.$el.data('pages-edit-validation');
                if (validationUrl) {
                    if (validationUrl.indexOf('/') !== 0) { // asuming a selector as 'validation' option
                        validationUrl = this.data.path + '.' + validationUrl + '.json';
                    }
                    this.form.prepare();
                    // FIXME use prepared data instead of raw form data
                    var formData = new FormData(this.form.el);
                    $.ajax({
                        type: 'POST',
                        url: core.getContextUrl(validationUrl),
                        data: formData,
                        cache: false,
                        contentType: false,
                        processData: false,
                        complete: _.bind(function (xhr) {
                            var result = xhr.responseJSON;
                            if (result) {
                                if (result.messages) {
                                    for (var i = 0; i < result.messages.length; i++) {
                                        this.validationHint(result.messages[i].level, result.messages[i].label, result.messages[i].text);
                                    }
                                }
                                if (valid && result.success) {
                                    onSuccess();
                                } else {
                                    onError();
                                }
                            } else {
                                this.onError(xhr);
                            }
                        }, this)
                    });
                } else {
                    if (valid) {
                        onSuccess();
                    } else {
                        onError();
                    }
                }
            }
        });

        /**
         * the EditDialog adds tabs and wizard control to an element dialog
         */
        dialogs.EditDialog = dialogs.ElementDialog.extend({

            initView: function () {
                dialogs.ElementDialog.prototype.initView.apply(this);
                var c = dialogs.const.edit.css;
                this.$submitButton = this.$('.' + c.base + c._submitButton);
                this.initTabs();
                this.$('.' + c.base + c._deleteButton).click(_.bind(this.doDelete, this));
                var init = this.$el.data('init');
                if (init) {
                    init = eval(init);
                    if (_.isFunction(init)) {
                        init.call(this);
                    }
                }
            },

            initTabs: function () {
                var c = dialogs.const.edit.css;
                // in case of a wizard set up button and tab control
                if (this.$el.is('.' + c.base + c._wizard)) {
                    this.$prevButton = this.$('.' + c.base + c._prevButton);
                    this.$nextButton = this.$('.' + c.base + c._nextButton);
                    this.$prevButton.click(_.bind(this.prevStep, this));
                    this.$nextButton.click(_.bind(this.nextStep, this));
                    if (this.form.tabbed) {
                        this.form.tabbed.$nav.find('a').on('shown.bs.tab', _.bind(this.tabChanged, this));
                    }
                }
                if (this.form.tabbed) {
                    this.$('.' + c.base + c._tabContent).addClass('tab-content');
                    this.$el.addClass(c.base + c._tabbed);
                    $(this.form.tabbed.$nav.find('li a')[0]).tab('show');
                }
            },

            applyData: function (data) {
                if (data) {
                    for (var name in data) {
                        if (data.hasOwnProperty(name)) {
                            this.$('input[name="' + name + '"]').val(data[name]);
                        }
                    }
                }
            },

            /**
             * adjust the wizard state on tab change
             */
            tabChanged: function () {
                var $links = this.form.tabbed.$nav.find('li');
                var index = $links.index(this.form.tabbed.$nav.find('li.active'));
                if (index < $links.length - 1) {
                    this.$nextButton.removeClass('btn-default').addClass('btn-primary');
                    this.$submitButton.removeClass('btn-primary').addClass('btn-default');
                    this.$submitButton.prop('disabled', true);
                    this.$nextButton.prop('disabled', false);
                } else {
                    this.$nextButton.removeClass('btn-primary').addClass('btn-default');
                    this.$submitButton.removeClass('btn-default').addClass('btn-primary');
                    this.$submitButton.prop('disabled', false);
                    this.$nextButton.prop('disabled', true);
                }
                this.$prevButton.prop('disabled', index === 0);
            },

            /**
             * trigger the switch to the previous tab if 'prev' button is clicked
             */
            prevStep: function () {
                this.form.tabbed.$nav.find('li.active').prev().find('a').tab('show');
            },

            /**
             * trigger the switch to the next tab if 'next' button is clicked
             */
            nextStep: function () {
                this.form.tabbed.$nav.find('li.active').next().find('a').tab('show');
            },

            /**
             * the submit handler called after a successful validation
             */
            doSubmit: function () {
                this.submitForm(_.bind(this.triggerEvents, this));
            },

            doDelete: function (event) {
                event.preventDefault();
                this.hide();
                pages.actions.element.delete(event, this.data.name, this.data.path, this.data.type);
                return false;
            },

            triggerEvents: function (result, defaultEvents) {
                pages.actions.dialog.triggerEvents(this, result, defaultEvents);
            },

            getDefaultSuccessEvents: function () {
                return pages.const.event.element.changed;
            },

            validationReset: function () {
                dialogs.ElementDialog.prototype.validationReset.apply(this);
            },

            onValidationFault: function () {
                dialogs.ElementDialog.prototype.onValidationFault.apply(this);
                var dialog = this;
                var $first = undefined;
                if (this.tabs) {
                    this.tabs.forEach(function (item) {
                        var $tab = $(item);
                        if ($tab.find('.has-error').length > 0) {
                            var $tabLink = dialog.$tabList.find('[href="#' + $tab[0].id + '"]');
                            $tabLink.parent().addClass('has-error');
                            if (!$first) {
                                $first = $tabLink;
                                $first.tab('show');
                            }
                        }
                    });
                }
            }
        });

        dialogs.getEditDialogUrl = function (type, selectors) {
            var c = dialogs.const.edit.url;
            var url = c.base + c._dialog[type ? type : 'load'];
            if (selectors) {
                url += '.' + selectors;
            }
            return url + ".html";
        };

        dialogs.openEditDialog = function (name, path, type, context, url, setupDialog) {
            pages.dialogHandler.openEditDialog(url ? url : dialogs.getEditDialogUrl(),
                dialogs.EditDialog, name, path, type, context, setupDialog);
        };

        /**
         * open a dialog rendered as response of a PUT request with JSON data
         */
        dialogs.openGenericDialog = function (url, viewType, data, name, path, type, context, setupDialog) {
            core.ajaxPut(url, JSON.stringify(data), {},
                _.bind(function (content, status, xhr) {
                    pages.dialogHandler.showDialogContent(content, viewType, name, path, type, context, setupDialog);
                }, this)
            );
        };

        //
        // elements in containers...
        //

        /**
         * the dialog to edit (and validate) the initial properties of a new element
         */
        dialogs.CreateDialog = dialogs.EditDialog.extend({

            initView: function () {
                dialogs.ElementDialog.prototype.initView.apply(this);
                this.initTabs();
            },

            afterLoad: function (name, path, type, context) {
                var c = dialogs.const.edit.css;
                if (context) {
                    if (context.parent && context.parent.synthetic) {
                        // set parent resource types if such (hidden) fields are available
                        // the parant data values are transmitted from the new element dialog
                        if (context.parent.type) {
                            var $parentResType = this.$('.' + c.base + c._parentResType);
                            $parentResType.attr('name',
                                context.parent.path + '/sling:resourceType').attr('value', context.parent.type);
                        }
                        if (context.parent.prim) {
                            var $parentPrimType = this.$('.' + c.base + c._parentPrimType);
                            $parentPrimType.attr('name',
                                context.parent.path + '/jcr:primaryType').attr('value', context.parent.prim);
                        }
                    }
                    if (context.before && context.before.path) {
                        var siblingName = core.getNameFromPath(context.before.path);
                        if (siblingName) {
                            var $order = this.$('.' + c.base + c._order);
                            $order.attr('value', 'before ' + siblingName);
                        }
                    }
                }
            },

            getDefaultSuccessEvents: function () {
                return pages.const.event.element.inserted;
            }
        });

        dialogs.openCreateDialog = function (name, path, type, context, url, setupDialog, onNotFound) {
            pages.dialogHandler.openEditDialog(url ? url : dialogs.getEditDialogUrl('create'),
                dialogs.CreateDialog, name, path, type, context, setupDialog, onNotFound);
        };

        /**
         * the dialog to select the element type of a new element to insert into a container;
         * the create dialog of the selected type is opened to complete the creation of the element
         */
        dialogs.NewElementDialog = dialogs.ElementDialog.extend({

            initView: function () {
                dialogs.ElementDialog.prototype.initView.apply(this);
                this.elementType = core.getWidget(this.el, '.element-type-select-widget',
                    pages.widgets.ElementTypeSelectWidget, {
                        callback: _.bind(this.showOrDefault, this)
                    });
                // after initialization of the element-type-select-widget to set up the reload callback
                dialogs.ElementDialog.prototype.setUpWidgets.apply(this);
                this.elementType.reload();
            },

            setUpWidgets: function (root) {
                // avoid initializing during construction; widget initialization is done later - see: initView()
            },

            show: function () {
                // the show() is suppressed if only one type is allowed; create an instance of this type immediately
            },

            showOrDefault: function () {
                this.elementType.callback = undefined; // reset 'show' callback
                if (this.elementType.getCount() === 1) {
                    var selection = this.elementType.getOnlyOne();
                    // use the one option instead of show and select if no more options are available
                    if (selection) {
                        this.doSubmit(undefined, selection);
                        // dispose the dialog to avoid reuse of a dialog which is not initialized during shown
                        this.destroy();
                        return;
                    }
                }
                // the normal show() if more than one option available or the filter is used...
                dialogs.ElementDialog.prototype.show.apply(this);
            },

            doSubmit: function (event, type) {
                var u = dialogs.const.edit.url;
                var d = dialogs.const.edit.data;
                if (!type) {
                    type = this.elementType.getValue();
                    this.hide();
                }
                // prepare parent (container) data of the new element for the create dialog
                // to create the right parent type if the parent is a synthetic resource
                var context = {
                    parent: new pages.Reference(this.$el)
                };
                if (type) {
                    dialogs.openCreateDialog('*', this.data.path, type, context, undefined, undefined,
                        // if no create dialog exists (not found) create a new instance directly
                        _.bind(function (name, path, type) {
                            core.ajaxPost(u.base + u._insert, {
                                _charset_: 'UTF-8',
                                resourceType: type,
                                targetPath: path,
                                targetType: this.data.type
                            }, {}, _.bind(function (result) {
                                pages.trigger('dialog.element.new', pages.const.event.element.inserted, [
                                    new pages.Reference(name, path, type),
                                    new pages.Reference(result.name, result.path)]);
                            }, this));
                        }, this));
                }
                return false;
            }
        });

        dialogs.openNewElementDialog = function (name, path, type) {
            pages.dialogHandler.openEditDialog(dialogs.getEditDialogUrl('new'),
                dialogs.NewElementDialog, name, path, type);
        };

        /**
         * the dialog to delete an element
         */
        dialogs.DeleteElementDialog = dialogs.ElementDialog.extend({

            doSubmit: function () {
                this.submitForm(_.bind(function () {
                    pages.trigger('dialog.element.delete', pages.const.event.element.deleted, [
                        new pages.Reference(this.data.name, this.data.path, this.data.type)]);
                }, this));
            }
        });

        dialogs.openDeleteElementDialog = function (name, path, type) {
            pages.dialogHandler.openEditDialog(dialogs.getEditDialogUrl('delete'),
                dialogs.DeleteElementDialog, name, path, type);
        };

        //
        // Content...
        //

        /**
         * the dialog to select the content type of new content to insert in the tree
         */
        dialogs.NewPageDialog = dialogs.EditDialog.extend({

            initView: function () {
                dialogs.EditDialog.prototype.initView.apply(this);
                this.pageTemplate = core.getWidget(this.el, '.widget-name_template', pages.widgets.PageTemplateWidget);
                this.pageName = core.getWidget(this.el, '.widget-name_name', core.components.TextFieldWidget);
                this.pageTitle = core.getWidget(this.el, '.widget-name_jcr_title', core.components.TextFieldWidget);
                this.description = core.getWidget(this.el, '.widget-name_jcr_description', core.components.TextFieldWidget);
            },

            getDefaultSuccessEvents: function () {
                return pages.const.event.content.inserted;
            },

            doSubmit: function () {
                var c = dialogs.const.edit.url;
                var template = this.pageTemplate.getValue();
                var name = this.pageName.getValue();
                var title = this.pageTitle.getValue();
                var description = this.description.getValue();
                var postData = {
                    _charset_: 'UTF-8',
                    name: name,
                    'jcr:title': title,
                    'jcr:description': description
                };
                this.hide(); // this is resetting the dialog
                if (template) {
                    core.ajaxGet(c.base + c._isTemplate + core.encodePath(template), {}, _.bind(function (result) {
                        if (result.isTemplate) {
                            postData.template = template;
                            // create page as a copy of the template
                            core.ajaxPost(c.base + c._create.page + core.encodePath(this.data.path), postData, {},
                                _.bind(function (result) {
                                    pages.trigger('dialog.page.new', pages.const.event.content.inserted, [
                                        new pages.Reference(this.data.name, this.data.path, this.data.type),
                                        new pages.Reference(result.name, result.path)]);
                                }, this));
                        } else {
                            // create page using resource type by opening the page create dialog of the designated type
                            dialogs.openCreateDialog(this.pageName.getValue() || '*', this.data.path, template, undefined, undefined,
                                // if no create dialog exists (not found) create a new instance directly
                                _.bind(function (name, path, type) {
                                    postData.resourceType = type;
                                    core.ajaxPost(c.base + c._create.page + core.encodePath(path), postData, {},
                                        _.bind(function (result) {
                                            pages.trigger('dialog.page.new', pages.const.event.content.inserted, [
                                                new pages.Reference(name, path, type),
                                                new pages.Reference(result.name, result.path)]);
                                        }, this));
                                }, this)
                            )
                            ;
                        }
                    }, this));
                }
                return false;
            }
        });

        dialogs.openNewPageDialog = function (name, path, type) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c._add.path + c._add._page,
                dialogs.NewPageDialog, name, path, type);
        };

        /**
         * the dialog to add a new folder as child of the current selection
         */
        dialogs.NewFolderDialog = dialogs.EditDialog.extend({

            initView: function () {
                dialogs.EditDialog.prototype.initView.apply(this);
                this.$primaryType = this.$('.widget-name_jcr_primaryType');
                this.pathfield = core.getWidget(this.el, '.widget-name_path', core.components.TextFieldWidget);
                this.ordered = core.getWidget(this.el, '.widget-name_ordered', core.components.CheckboxWidget);
            },

            getDefaultSuccessEvents: function () {
                return this.pathfield
                    ? pages.const.event.folder.inserted + '#' + this.data.path + ',' + this.pathfield.getValue()
                    : pages.const.event.content.inserted;
            },

            doSubmit: function () {
                this.$primaryType.attr('value', this.ordered.getValue() ? 'sling:OrderedFolder' : 'sling:Folder');
                dialogs.EditDialog.prototype.doSubmit.apply(this);
            }
        });

        dialogs.openNewFolderDialog = function (name, path, type, url) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(url ? url : (c.path + c._add.path + c._add._folder),
                dialogs.NewFolderDialog, name, path, type);
        };

        /**
         * the dialog to upload a file as child of the current selection
         */
        dialogs.NewFileDialog = dialogs.EditDialog.extend({

            initView: function () {
                dialogs.EditDialog.prototype.initView.apply(this);
                this.file = core.getWidget(this.el, '.widget-name_file', core.components.FileUploadWidget);
                if (!this.file) {
                    this.file = core.getWidget(this.el, '.widget-name_STAR', core.components.FileUploadWidget);
                }
                this.name = core.getWidget(this.el, '.widget-name_name', core.components.TextFieldWidget);
                this.source = core.getWidget(this.el, '.widget-name_code', pages.widgets.CodeAreaWidget);
                this.type = core.getWidget(this.el, '.widget-name_type', core.components.SelectWidget);
                if (this.source && this.type) {
                    this.type.$input.on('change.NewFileDialog', _.bind(function () {
                        this.source.setType(this.type.getValue());
                    }, this));
                }
            },

            getDefaultSuccessEvents: function () {
                return pages.const.event.content.inserted;
            },

            doSubmit: function () {
                var fileName = this.file.getFileName();
                var name = this.name.getValue();
                if (!name) {
                    name = fileName;
                }
                if (name && (name = core.mangleNameValue(name))) {
                    var type;
                    if (this.type && (type = this.type.getValue())) {
                        if (new RegExp('^.*\\.' + type + '$', 'i').exec(name) === null) {
                            name += '.' + type;
                        }
                    }
                }
                if (name) {
                    if (!fileName && this.source) {
                        this.source.saveAs(this.data.path + '/' + name, _.bind(function () {
                            this.triggerEvents();
                            this.hide();
                        }, this), _.bind(this.onError, this));
                    } else {
                        this.name.setValue(name); // apply name mangling
                        dialogs.EditDialog.prototype.doSubmit.apply(this);
                    }
                } else {
                    core.i18n.get(['Validation Error', 'Name', 'the Name or a File must be specified'],
                        _.bind(function (msgs) {
                            this.messages('danger', msgs[0], [{level: 'danger', label: msgs[1], text: msgs[2]}]);
                        }, this));
                }
            }
        });

        dialogs.openNewFileDialog = function (name, path, type, dialogKey) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c._add.path + (dialogKey ? dialogKey : c._add._file),
                dialogs.NewFileDialog, name, path, type);
        };

        dialogs.openUploadFileDialog = function (name, path, type) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c._upload.dialog,
                dialogs.EditDialog, name, path, type);
        };

        /**
         * the dialog to edit a source file
         */
        dialogs.EditSourceFileDialog = dialogs.EditDialog.extend({

            initView: function () {
                dialogs.EditDialog.extend.prototype.initView.apply(this);
                var c = dialogs.const.edit.css;
                dialogs.EditDialog.prototype.initView.apply(this);
                this.$title = this.$('.modal-title');
                this.source = core.getWidget(this.el, '.widget-name_code', pages.widgets.CodeAreaWidget);
                var $hints = this.$('.' + c.base + c._hints);
                var $tools = this.$('.' + c.code.editor.base + c.code.editor._toolbar);
                $hints.append($tools);
                $tools.removeClass('hidden');
                $tools = this.$('.' + c.code.editor.base + c.code.editor._toolbar);
                this.$findText = $tools.find('.search .find-text');
                this.$findNext = $tools.find('.search .find-next');
                this.$findPrev = $tools.find('.search .find-prev');
                this.$matchCase = $tools.find('.match-case');
                this.$findRegEx = $tools.find('.find-regex');
                this.$replText = $tools.find('.replace .replace-text');
                this.$replCurrent = $tools.find('.replace .replace');
                this.$replAll = $tools.find('.replace .replace-all');
                this.$undo = $tools.find('.undo');
                this.$redo = $tools.find('.redo');
                this.$findText.on('input', _.bind(function (event) {
                    this.source.findText(this.$findText.val());
                }, this));
                this.$findText.keypress(_.bind(function (event) {
                    this.source.findText(this.$findText.val());
                }, this));
                this.$findNext.click(_.bind(this.source.findNext, this.source));
                this.$findPrev.click(_.bind(this.source.findPrev, this.source));
                this.$matchCase.change(_.bind(this.source.toggleCaseSensitive, this.source));
                this.$findRegEx.change(_.bind(this.source.toggleRegExp, this.source));
                this.$replCurrent.click(_.bind(function () {
                    this.source.replace(this.$replText.val());
                }, this));
                this.$replAll.click(_.bind(function () {
                    this.source.replaceAll(this.$replText.val());
                }, this));
                this.$undo.click(_.bind(this.source.undo, this.source));
                this.$redo.click(_.bind(this.source.redo, this.source));
            },

            getInitialFocus: function () {
                return undefined;
            },

            onShown: function () {
                dialogs.EditDialog.prototype.onShown.apply(this);
                this.$title.text(this.data.path);
                this.source.open(this.data.path);
                this.source.focus();
            },

            getDefaultSuccessEvents: function () {
                return pages.const.event.content.changed;
            },

            doSubmit: function () {
                this.source.saveAs(this.data.path, _.bind(function () {
                    this.triggerEvents();
                    this.hide();
                }, this), _.bind(this.onError, this));
            }
        });

        dialogs.openEditSourceFileDialog = function (name, path, type) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c._edit._source,
                dialogs.EditSourceFileDialog, name, path, type);
        };

        /**
         * the dialog to delete a content resource (page, folder, file) from the repository
         */
        dialogs.DeleteContentDialog = dialogs.EditDialog.extend({

            getDefaultSuccessEvents: function () {
                return pages.const.event.content.deleted;
            }
        });

        dialogs.openDeleteContentDialog = function (contentType, name, path, type) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c._remove.path + c._remove['_' + contentType],
                dialogs.DeleteContentDialog, name, path, type);
        };

        /**
         * the dialog to move a content element to a new parent path
         */
        dialogs.MoveContentDialog = dialogs.EditDialog.extend({

            initView: function () {
                dialogs.EditDialog.prototype.initView.apply(this);
                this.$path = this.$('.hidden-widget.widget-name_path');
                this.toLabel = this.$('.composum-pages-edit-widget_newPath .label-text').text();
                this.oldPath = core.getWidget(this.el, '.widget-name_oldPath', core.components.PathWidget);
                this.newPath = core.getWidget(this.el, '.widget-name_newPath', core.components.PathWidget);
                this.name = core.getWidget(this.el, '.widget-name_name', core.components.TextFieldWidget);
                this.before = core.getWidget(this.el, '.widget-name_before', core.components.TextFieldWidget);
                this.index = core.getWidget(this.el, '.widget-name_index', core.components.NumberFieldWidget);
            },

            getConfig: function () {
                return dialogs.const.edit.url._move;
            },

            setValues: function (sourcePath, targetPath, beforeName) {
                var parentAndName = core.getParentAndName(sourcePath);
                this.oldPath.setValue(parentAndName.path);
                this.name.setValue(parentAndName.name);
                this.newPath.setValue(targetPath);
                this.before.setValue(beforeName);
                this.index.setValue(undefined);
            },

            /**
             * the validation strategy with support for an asynchronous validation call
             * @param onSuccess called after successful validation
             * @param onError called if a validation fault registered
             */
            doValidate: function (onSuccess, onError) {
                if (this.validateForm()) {
                    var config = this.getConfig();
                    core.getJson(config.check + core.encodePath(this.$path.val())
                        + '?_charset_=UTF-8&path=' + core.encodePath(this.newPath.getValue()),
                        _.bind(function (data) {
                            if (data.isAllowed) {
                                onSuccess();
                            } else {
                                this.validationHint('danger', this.toLabel, data.messages[0].text, data.messages[0].hint);
                                onError();
                            }
                        }, this), _.bind(function (result) {
                            this.validationHint('danger', this.toLabel, 'Error on validation', result.responseText);
                            onError();
                        }, this));
                } else {
                    onError();
                }
            },

            doSubmit: function () {
                var config = this.getConfig();
                var oldPath = this.$path.val();
                var newPath = this.newPath.$input.val();    // after finalize()...
                core.ajaxPost(config.action + core.encodePath(oldPath), {
                    _charset_: 'UTF-8',
                    targetPath: newPath,
                    name: this.name.getValue(),
                    before: this.before.getValue(),

                    index: this.index.getValue()
                }, {}, _.bind(function (result) {
                    var reference = result.reference || result.data.reference;
                    pages.trigger('dialog.content.move', pages.const.event.content.moved,
                        [oldPath, reference.path]);
                    this.hide();
                }, this));
            }
        });

        dialogs.openMoveContentDialog = function (name, path, type, setupDialog) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c._move.dialog,
                dialogs.MoveContentDialog, name, path, type, undefined/*context*/, setupDialog);
        };

        /**
         * the dialog to rename a content element
         */
        dialogs.RenameContentDialog = dialogs.EditDialog.extend({

            initView: function () {
                dialogs.EditDialog.prototype.initView.apply(this);
                this.$path = this.$('.widget-name_path');
                this.name = core.getWidget(this.el, '.widget-name_name', core.components.TextFieldWidget);
            },

            setValues: function (pathToRename, newName) {
                this.$path.val(pathToRename);
                this.name.setValue(newName);
            },

            doSubmit: function () {
                var c = dialogs.const.edit.url;
                var oldPath = this.$path.val();
                core.ajaxPost(c._rename.action + core.encodePath(oldPath), {
                    _charset_: 'UTF-8',
                    name: this.name.getValue()
                }, {}, _.bind(function (result) {
                    var reference = result.reference || result.data.reference;
                    pages.trigger('dialog.content.rename', pages.const.event.content.moved,
                        [oldPath, reference.path]);
                    this.hide();
                }, this));
            }
        });

        dialogs.openRenameContentDialog = function (name, path, type, setupDialog) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c._rename.dialog,
                dialogs.RenameContentDialog, name, path, type, undefined/*context*/, setupDialog);
        };

        /**
         * the dialog to move a content element to a new parent path
         */
        dialogs.CopyContentDialog = dialogs.MoveContentDialog.extend({

            getConfig: function () {
                return dialogs.const.edit.url._copy;
            }
        });

        dialogs.openCopyContentDialog = function (name, path, type, setupDialog) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c._copy.dialog,
                dialogs.CopyContentDialog, name, path, type, undefined/*context*/, setupDialog);
        };

        //
        // Releases & Versions...
        //

        dialogs.ManageReleaseStatusDialog = dialogs.ElementDialog.extend({

            initialize: function (options) {
                dialogs.ElementDialog.prototype.initialize.call(this, options);
                this.refs = {};
            },

            hasReferences: function () {
                return (this.refs.page && this.refs.page.isNotEmpty()) ||
                    (this.refs.asset && this.refs.asset.isNotEmpty());
            },

            submitActionKey: function () {
                // abstract: return the concrete servlet action selector
            },

            doSubmit: function (ignoredOnSuccess, onError) {
                var u = dialogs.const.edit.url.version;
                var data = this.getActionData();
                core.ajaxPost(u.base + this.submitActionKey(), data, {},
                    _.bind(function (result) {
                        this.triggerStateChange(data);
                        this.hide();
                    }, this), _.bind(function (xhr) {
                        if (_.isFunction(onError)) {
                            onError(xhr);
                        } else {
                            this.onError(xhr);
                        }
                    }, this));
            },

            /**
             * a manage pages dialog is used in a single (e.g. tree) and a multiple (e.g. site management form) context
             * in the single mode the this.data.path references the target; in the multiple mode are input fields
             * named 'target' embedded in the dialog form, one for each selected target
             */
            getActionData: function () {
                var data = {
                    _charset_: 'UTF-8',
                    target: []
                };
                this.$('input[name="target"]').each(function () { // multi mode
                    var target = $(this).val();
                    if (target && !_.contains(data.target, target)) {
                        data.target.push(target);
                    }
                });
                if (data.target.length < 1 && this.data.path) {
                    data.target.push(this.data.path); // single mode
                }
                if (this.refs.page && this.refs.page.isNotEmpty()) {
                    data.pageRef = this.refs.page.getValue();
                }
                if (this.refs.asset && this.refs.asset.isNotEmpty()) {
                    data.assetRef = this.refs.asset.getValue();
                }
                return data;
            },

            triggerStateChange: function (data) {
                var e = pages.const.event;
                data.target.forEach(function (path) {
                    pages.trigger('dialog.state.change', e.content.state, [new pages.Reference(undefined, path)]);
                });
                if (data.pageRef) {
                    data.pageRef.forEach(function (path) {
                        pages.trigger('dialog.state.change', e.content.state, [new pages.Reference(undefined, path)]);
                    });
                }
            }
        });

        dialogs.ActivateContentDialog = dialogs.ManageReleaseStatusDialog.extend({

            initialize: function (options) {
                dialogs.ManageReleaseStatusDialog.prototype.initialize.call(this, options);
                this.refs = {
                    page: core.getWidget(this.el, '.widget-name_page-references', pages.widgets.PageReferencesWidget),
                    asset: core.getWidget(this.el, '.widget-name_asset-references', pages.widgets.PageReferencesWidget)
                };
            },

            submitActionKey: function () {
                return dialogs.const.edit.url.version.page.activate._action;
            },

            show: function () {
                if (this.hasReferences()) {
                    // the normal show() if unresolved references found
                    dialogs.ElementDialog.prototype.show.apply(this);
                } else {
                    // the show() is suppressed if no unresolved references found
                    this.doSubmit(undefined, _.bind(function (xhr) {
                        core.alert(xhr);
                    }, this));
                    this.destroy();
                }
            }
        });

        dialogs.openActivateContentDialog = function (name, path, type, setupDialog) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c.version.page.activate._dialog,
                dialogs.ActivateContentDialog, name, path, type, undefined/*context*/, setupDialog);
        };

        dialogs.RevertContentDialog = dialogs.ManageReleaseStatusDialog.extend({

            initialize: function (options) {
                dialogs.ManageReleaseStatusDialog.prototype.initialize.call(this, options);
                this.refs = {
                    page: core.getWidget(this.el, '.widget-name_page-referrers', pages.widgets.PageReferrersWidget)
                };
            },

            submitActionKey: function () {
                return dialogs.const.edit.url.version.page.revert._action;
            }
        });

        dialogs.openRevertContentDialog = function (name, path, type, setupDialog) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c.version.page.revert._dialog,
                dialogs.RevertContentDialog, name, path, type, undefined/*context*/, setupDialog);
        };

        dialogs.DeactivateContentDialog = dialogs.ManageReleaseStatusDialog.extend({

            initialize: function (options) {
                dialogs.ManageReleaseStatusDialog.prototype.initialize.call(this, options);
                this.refs = {
                    page: core.getWidget(this.el, '.widget-name_page-referrers', pages.widgets.PageReferrersWidget)
                };
            },

            submitActionKey: function () {
                return dialogs.const.edit.url.version.page.deactivate._action;
            }
        });

        dialogs.openDeactivateContentDialog = function (name, path, type, setupDialog) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c.version.page.deactivate._dialog,
                dialogs.DeactivateContentDialog, name, path, type, undefined/*context*/, setupDialog);
        };

        dialogs.ActivateTreeDialog = dialogs.ManageReleaseStatusDialog.extend({

            initialize: function (options) {
                dialogs.ManageReleaseStatusDialog.prototype.initialize.call(this, options);
                this.refs = {
                    page: core.getWidget(this.el, '.widget-name_page-references', pages.widgets.PageReferencesWidget),
                    asset: core.getWidget(this.el, '.widget-name_asset-references', pages.widgets.PageReferencesWidget)
                };
            },

            submitActionKey: function () {
                return dialogs.const.edit.url.version.folder.activate._action;
            },

            show: function () {
                if (this.hasReferences()) {
                    // the normal show() if unresolved references found
                    dialogs.ElementDialog.prototype.show.apply(this);
                } else {
                    // the show() is suppressed if no unresolved references found
                    this.doSubmit(undefined, _.bind(function (xhr) {
                        core.alert(xhr);
                    }, this));
                    this.destroy();
                }
            }
        });

        dialogs.openActivateTreeDialog = function (name, path, type, setupDialog) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c.version.folder.activate._dialog,
                dialogs.ActivateTreeDialog, name, path, type, undefined/*context*/, setupDialog);
        };

        //
        // Sites...
        //

        /**
         * Create Site
         */
        dialogs.CreateSiteDialog = dialogs.EditDialog.extend({

            doSubmit: function () {
                this.submitForm(_.bind(function (result) {
                    pages.trigger('dialog.site.create', pages.const.event.site.created,
                        [new pages.Reference(result.name, result.path)]);
                }, this));
            }
        });

        dialogs.openCreateSiteDialog = function (name, path, type) {
            var c = dialogs.const.edit;
            pages.dialogHandler.openEditDialog(dialogs.getEditDialogUrl('create'),
                dialogs.CreateSiteDialog, undefined, path, c.type.site);
        };

        /**
         * Delete Site
         */
        dialogs.DeleteSiteDialog = dialogs.EditDialog.extend({

            doSubmit: function () {
                this.submitForm(_.bind(function (result) {
                    pages.trigger('dialog.site.delete', pages.const.event.site.deleted,
                        [new pages.Reference(result.name, result.path)]);
                }, this));
            }
        });

        dialogs.openDeleteSiteDialog = function (name, path, type) {
            pages.dialogHandler.openEditDialog(dialogs.getEditDialogUrl('delete'),
                dialogs.DeleteSiteDialog, name, path, type);
        };

        /**
         * Manage Sites
         */
        dialogs.ManageSitesDialog = core.components.Dialog.extend({

            initialize: function (options) {
                var c = dialogs.const.edit.css;
                core.components.Dialog.prototype.initialize.apply(this, [options]);
                this.$list = this.$('.' + c.base + c.sites._list);
                this.$('.' + c.base + c._addButton).click(_.bind(this.onCreate, this));
                this.$('.' + c.base + c._removeButton).click(_.bind(this.onDelete, this));
                this.$('.' + c.base + c._openButton).click(_.bind(this.onOpen, this));
                this.initContent();
                var id = '.SiteManager';
                var e = pages.const.event;
                $(document).on(e.site.created + id, _.bind(this.reloadContent, this));
                $(document).on(e.site.changed + id, _.bind(this.reloadContent, this));
                $(document).on(e.site.deleted + id, _.bind(this.reloadContent, this));
            },

            initContent: function () {
                var c = dialogs.const.edit.css;
                this.$list.find('a.' + c.sites.base + c.sites._radio).click(_.bind(this.selectSite, this));
                this.$list.find('a.' + c.site.base + c.site._tile).click(_.bind(this.selectSite, this));
            },

            reloadContent: function () {
                core.ajaxGet(dialogs.const.edit.url.sites.list, {}, _.bind(function (content) {
                    this.$list.html(content);
                    this.initContent();
                }, this));
            },

            selectSite: function (event) {
                event.preventDefault();
                var c = dialogs.const.edit.css.sites;
                var $link = $(event.currentTarget);
                var $radio = $link.closest('.' + c.base + c._site).find('.' + c.base + c._radio);
                $radio.prop("checked", true);
                this.selectedSite = $radio.length === 1 ? $radio.val() : undefined;
                return false;
            },

            onCreate: function (event) {
                event.preventDefault();
                pages.actions.site.create(event);
                return false;
            },

            onDelete: function (event) {
                event.preventDefault();
                if (this.selectedSite) {
                    pages.actions.site.delete(event, undefined, this.selectedSite);
                }
                return false;
            },

            onOpen: function (event) {
                event.preventDefault();
                if (this.selectedSite) {
                    pages.actions.site.open(event, undefined, this.selectedSite);
                }
                this.hide();
                return false;
            }
        });

        dialogs.openManageSitesDialog = function () {
            pages.dialogHandler.openEditDialog(dialogs.getEditDialogUrl('manage'),
                dialogs.ManageSitesDialog);
        };

    })(window.composum.pages.dialogs, window.composum.pages, window.core);
})(window);
