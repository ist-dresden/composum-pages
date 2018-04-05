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
                        _folder: '/folder/dialog.html'
                    },
                    _add: {
                        path: '/content/dialog/add',
                        _page: '.page.html',
                        _folder: '.folder.html',
                        _file: '.file.html'
                    },
                    _remove: {
                        path: '/content/dialog/remove',
                        _page: '.page.html',
                        _folder: '.folder.html',
                        _file: '.file.html'
                    },
                    _move: {
                        dialog: '/content/dialog/move.html',
                        check: '/bin/cpm/pages/edit.isAllowedChild.json',
                        action: '/bin/cpm/pages/edit.moveContent.json'
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
                    _insert: '.insertComponent.html',
                    _isTemplate: '.isTemplate.json',
                    _dialog: {
                        load: '.editDialog',
                        new: '.newDialog',
                        create: '.editDialog.create',
                        delete: '.editDialog.delete'
                    }
                },
                css: {
                    base: 'composum-pages-stage-edit-dialog',
                    _wizard: '_selector_wizard',
                    _form: '_form',
                    _tab: '_tab',
                    _tabbed: '_tabbed',
                    _tabList: '_tabs',
                    _tabContent: '_tabbed-content',
                    _pathField: '_path',
                    _deleteButton: '_button-delete',
                    _submitButton: '_button-submit',
                    _prevButton: '_button-prev',
                    _nextButton: '_button-next'
                },
                type: {
                    site: 'cpp:Site',
                    page: 'cpp:Page'
                }
            }
        });

        dialogs.ElementDialog = core.components.Dialog.extend({

            initialize: function (options) {
                core.components.Dialog.prototype.initialize.apply(this, [options]);
                this.form = core.getWidget(this.el, "form", core.components.FormWidget);
                this.initView();
                this.initSubmit();
                this.$el.on('hidden.bs.modal', _.bind(this.onClose, this));
            },

            initSubmit: function () {
                var c = dialogs.const.edit.css;
                this.$('.' + c.base + c._form).on('submit', _.bind(this.onSubmit, this));
            },

            initView: function () {
            },

            resetOnShown: function () {
            },

            validationReset: function () {
                this.$alert.addClass('alert-hidden');
                this.$alert.html('');
                this.validationHints = [];
                this.form.validationReset();
            },

            onValidationFault: function () {
            },

            message: function (type, label, message, hint) {
                if (message) {
                    this.alert(type, '<div class="text-danger"><span class="label">' + label
                        + '</span><span class="message">'
                        + message + (hint ? " (" + hint + ")" : '') + '</span></div>');
                }
            },

            validationHint: function (type, label, message, hint) {
                if (message) {
                    this.validationHints.push({level: type, label: label, text: message, hint: hint});
                }
            },

            validateForm: function () {
                this.validationReset();
                return this.form.validate(_.bind(function (type, label, message, hint) {
                    this.validationHint(type, label, message, hint)
                }, this));
            },

            /**
             * the validation strategy with support for an asynchronous validation call
             * @param onSuccess called after successful validation
             * @param onError called if a validation fault registered
             */
            doValidate: function (onSuccess, onError) {
                if (this.validateForm()) {
                    onSuccess();
                } else {
                    onError();
                }
            },

            /**
             * triggered if the submit button is clicked or activated somewhere else
             */
            onSubmit: function (event) {
                if (event) {
                    event.preventDefault();
                }
                this.form.prepare();
                this.doValidate(_.bind(function () {
                    this.doSubmit();
                }, this), _.bind(function () {
                    this.messages('warning', this.validationHints.length < 1 ? 'validation error' : undefined,
                        this.validationHints);
                    this.onValidationFault();
                }, this));
                return false;
            },

            onClose: function (event) {
                this.$el.remove();
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
            },

            initTabs: function () {
                var c = dialogs.const.edit.css;
                var $tabList = this.$tabList = this.$('.' + c.base + c._tabList);
                var tabs = this.tabs = [];
                this.$('.' + c.base + c._tab).each(function () {
                    var $tab = $(this);
                    $tabList.append('<li><a data-toggle="tab" href="#' + $tab.attr('id') + '">'
                        + $tab.data('label') + '</a></li>');
                    tabs.push($tab);
                });
                // in case of a wizard set up button and tab control
                if (this.$el.is('.' + c.base + c._wizard)) {
                    this.$prevButton = this.$('.' + c.base + c._prevButton);
                    this.$nextButton = this.$('.' + c.base + c._nextButton);
                    this.$prevButton.click(_.bind(this.prevStep, this));
                    this.$nextButton.click(_.bind(this.nextStep, this));
                    this.$tabList.find('a').on('shown.bs.tab', _.bind(this.tabChanged, this));
                }
                if (this.tabs.length > 0) {
                    this.$('.' + c.base + c._tabContent).addClass('tab-content');
                    this.$el.addClass(c.base + c._tabbed);
                    $(this.$tabList.find('li a')[0]).tab('show');
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
                var $links = this.$tabList.find('li');
                var index = $links.index(this.$tabList.find('li.active'));
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
                this.$tabList.find('li.active').prev().find('a').tab('show');
            },

            /**
             * trigger the switch to the next tab if 'next' button is clicked
             */
            nextStep: function () {
                this.$tabList.find('li.active').next().find('a').tab('show');
            },

            /**
             * the submit handler called after a successful validation
             */
            doSubmit: function () {
                this.submitForm(_.bind(this.triggerEvents, this));
            },

            doDelete: function () {
                var c = dialogs.const.edit.css;
                this.$('.' + c.base + c._pathField)
                    .before('<input name=":operation" type="hidden" value="delete"/>');
                this.submitForm(_.bind(function () {
                    console.log('pages.trigger.component:deleted(' + this.data.path + ')');
                    $(document).trigger('component:deleted', [this.data.path]);
                }, this));
            },

            triggerEvents: function (result, defaultEvents) {
                var event = (this.$el.data('pages-edit-success') || defaultEvents
                    || this.getDefaultSuccessEvents()).split(';');
                for (var i = 0; i < event.length; i++) {
                    switch (event[i]) {
                        case 'messages':
                            if (_.isObject(result) && _.isObject(result.response)) {
                                var response = result.response;
                                var messages = result.messages;
                                core.messages(response.level, response.text, messages);
                            }
                            break;
                        default:
                            console.log('pages.trigger.' + event[i] + '(' + this.data.path + ')');
                            $(document).trigger(event[i], [this.data.path]);
                            break;
                    }
                }
            },

            getDefaultSuccessEvents: function () {
                return 'component:changed';
            },

            validationReset: function () {
                dialogs.ElementDialog.prototype.validationReset.apply(this);
                this.$tabList.find('li').removeClass('has-error');
            },

            onValidationFault: function () {
                dialogs.ElementDialog.prototype.onValidationFault.apply(this);
                var dialog = this;
                var $first = undefined;
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
        });

        dialogs.getEditDialogUrl = function (type, selectors) {
            var c = dialogs.const.edit.url;
            var url = c.base + c._dialog[type ? type : 'load'];
            if (selectors) {
                url += '.' + selectors;
            }
            return url + ".html";
        };

        dialogs.openEditDialog = function (name, path, type, url, setupDialog) {
            pages.dialogHandler.openEditDialog(url ? url : dialogs.getEditDialogUrl(),
                dialogs.EditDialog, name, path, type, setupDialog);
        };

        //
        // elements in containers...
        //

        /**
         * the dialog to edit the initial properties of a new element
         */
        dialogs.CreateDialog = dialogs.EditDialog.extend({

            initView: function () {
                this.initTabs();
            },

            afterLoad: function (name, path, type) {
                // set resource Type if such a hidden field is available
                //this.$('[name="' + dialogs.const.pages.const.sling.resourceType + '"]').attr('value',type); ??? FIXME
            }
        });

        dialogs.openCreateDialog = function (name, path, type, url, setupDialog, onNotFound) {
            pages.dialogHandler.openEditDialog(url ? url : dialogs.getEditDialogUrl('create'),
                dialogs.CreateDialog, name, path, type, setupDialog, onNotFound);
        };

        /**
         * the dialog to select the element type of a new element to insert in a container
         */
        dialogs.NewElementDialog = dialogs.ElementDialog.extend({

            initView: function () {
                this.elementType = core.getWidget(this.el, '.radio-group-widget', core.components.RadioGroupWidget);
                if (this.elementType.getCount() === 1) {
                    this.useDefault = this.elementType.getOnlyOne();
                }
            },

            doSubmit: function (type) {
                var c = dialogs.const.edit.url;
                if (!type) {
                    type = this.elementType.getValue();
                    this.hide();
                }
                if (type) {
                    dialogs.openCreateDialog('*', this.data.path, type, undefined, undefined,
                        // if no create dialog exists (not found) create a new instance directly
                        _.bind(function (name, path, type) {
                            core.ajaxPost(c.base + c._insert, {
                                resourceType: type,
                                targetPath: path,
                                targetType: this.data.type
                            }, {}, _.bind(function () {
                                console.log('pages.trigger.component:changed' + path + ')');
                                $(document).trigger('component:changed', [path]);
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
                    console.log('pages.trigger.component:deleted(' + this.data.path + ')');
                    $(document).trigger('component:deleted', [this.data.path]);
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
                return 'content:inserted';
            },

            doSubmit: function () {
                var c = dialogs.const.edit.url;
                var template = this.pageTemplate.getValue();
                var name = this.pageName.getValue();
                var title = this.pageTitle.getValue();
                var description = this.description.getValue();
                this.hide(); // this is resetting the dialog
                if (template) {
                    core.ajaxGet(c.base + c._isTemplate + template, {}, _.bind(function (result) {
                        if (result.isTemplate) {
                            // create page as a copy of the template
                            core.ajaxPost(c.base + c._create.page + this.data.path, {
                                template: template,
                                name: name,
                                title: title,
                                description: description
                            }, {}, _.bind(function () {
                                console.log('pages.trigger.component:changed(' + this.data.path + ')');
                                $(document).trigger('component:changed', [this.data.path]);
                            }, this));
                        } else {
                            // create page using resource type by opening the page create dialog of the designated type
                            dialogs.openCreateDialog(this.pageName.getValue() || '*', this.data.path, template, undefined, undefined,
                                // if no create dialog exists (not found) create a new instance directly
                                _.bind(function (name, path, type) {
                                    core.ajaxPost(c.base + c._create.page + path, {
                                        resourceType: type,
                                        name: name,
                                        title: title,
                                        description: description
                                    }, {}, _.bind(function () {
                                        console.log('pages.trigger.component:changed(' + this.data.path + ')');
                                        $(document).trigger('component:changed', [this.data.path]);
                                    }, this));
                                }, this));
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
                this.ordered = core.getWidget(this.el, '.widget-name_ordered', core.components.CheckboxWidget);
            },

            getDefaultSuccessEvents: function () {
                return 'content:inserted';
            },

            doSubmit: function () {
                this.$primaryType.attr('value', this.ordered.getValue() ? 'sling:OrderedFolder' : 'sling:Folder');
                dialogs.EditDialog.prototype.doSubmit.apply(this);
            }
        });

        dialogs.openNewFolderDialog = function (name, path, type) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c._add.path + c._add._folder,
                dialogs.NewFolderDialog, name, path, type);
        };

        /**
         * the dialog to upload a file as child of the current selection
         */
        dialogs.NewFileDialog = dialogs.EditDialog.extend({

            initView: function () {
                dialogs.EditDialog.prototype.initView.apply(this);
                this.file = core.getWidget(this.el, '.widget-name_STAR', core.components.FileUploadWidget);
                this.name = core.getWidget(this.el, '.widget-name_name', core.components.TextFieldWidget);
            },

            getDefaultSuccessEvents: function () {
                return 'content:inserted';
            },

            doSubmit: function () {
                var name = this.name.getValue();
                if (!name) {
                    name = this.file.getFileName();
                }
                if (name && (name = core.mangleNameValue(name))) {
                    this.file.setName(name);
                }
                dialogs.EditDialog.prototype.doSubmit.apply(this);
            }
        });

        dialogs.openNewFileDialog = function (name, path, type) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c._add.path + c._add._file,
                dialogs.NewFileDialog, name, path, type);
        };

        /**
         * the dialog to delete a content resource (page, folder, file) from the repository
         */
        dialogs.DeleteContentDialog = dialogs.EditDialog.extend({

            getDefaultSuccessEvents: function () {
                return 'content:deleted';
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
                this.$path = this.$('.widget-name_path');
                this.toLabel = this.$('.composum-pages-edit-widget_newPath .label-text').text();
                this.oldPath = core.getWidget(this.el, '.widget-name_oldPath', core.components.PathWidget);
                this.newPath = core.getWidget(this.el, '.widget-name_newPath', core.components.PathWidget);
                this.name = core.getWidget(this.el, '.widget-name_name', core.components.TextFieldWidget);
                this.before = core.getWidget(this.el, '.widget-name_before', core.components.TextFieldWidget);
                this.index = core.getWidget(this.el, '.widget-name_index', core.components.NumberFieldWidget);
            },

            setValues: function (nodeToMove, moveTarget, beforeNode) {
                var parentAndName = core.getParentAndName(nodeToMove.path);
                this.oldPath.setValue(parentAndName.path);
                this.name.setValue(parentAndName.name);
                this.newPath.setValue(moveTarget.path);
                this.before.setValue(beforeNode ? beforeNode.name : undefined);
                this.index.setValue(undefined);
            },

            /**
             * the validation strategy with support for an asynchronous validation call
             * @param onSuccess called after successful validation
             * @param onError called if a validation fault registered
             */
            doValidate: function (onSuccess, onError) {
                if (this.validateForm()) {
                    var c = dialogs.const.edit.url;
                    core.ajaxGet(c._move.check + core.encodePath(this.$path.val()), {
                        data: {
                            path: this.newPath.getValue()
                        }
                    }, _.bind(function (data) {
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
                var c = dialogs.const.edit.url;
                var oldPath = this.$path.val();
                core.ajaxPost(c._move.action + core.encodePath(oldPath), {
                    targetPath: this.newPath.getValue(),
                    name: this.name.getValue(),
                    before: this.before.getValue(),
                    index: this.index.getValue()
                }, {}, _.bind(function (data) {
                    $(document).trigger('content:moved', [oldPath, data.path]);
                    this.hide();
                }, this));
            }
        });

        dialogs.openMoveContentDialog = function (name, path, type, setupDialog) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c._move.dialog,
                dialogs.MoveContentDialog, name, path, type, setupDialog);
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
                    name: this.name.getValue()
                }, {}, _.bind(function (data) {
                    $(document).trigger('content:moved', [oldPath, data.path]);
                    this.hide();
                }, this));
            }
        });

        dialogs.openRenameContentDialog = function (name, path, type, setupDialog) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.path + c._rename.dialog,
                dialogs.RenameContentDialog, name, path, type, setupDialog);
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
                    this.data.name = result.name;
                    this.data.path = result.path;
                    this.triggerEvents(result, 'site:created');
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
                    this.triggerEvents(result, 'site:deleted');
                }, this));
            }
        });

        dialogs.openDeleteSiteDialog = function (name, path, type) {
            pages.dialogHandler.openEditDialog(dialogs.getEditDialogUrl('delete'),
                dialogs.DeleteSiteDialog, name, path, type);
        };

    })(window.composum.pages.dialogs, window.composum.pages, window.core);
})(window);
