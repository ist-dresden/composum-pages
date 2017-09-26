(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.dialogs = window.composum.pages.dialogs || {};

    (function (dialogs, pages, core) {
        'use strict';

        dialogs.const = _.extend(dialogs.const || {}, {
            editDialogLoadUrl: '/bin/cpm/pages/edit.editDialog.html',
            editDialogFormClass: 'composum-pages-stage-edit-dialog_form',
            editDialogTabbedClass: 'composum-pages-stage-edit-dialog_tabbed',
            editDialogTabListCass: 'composum-pages-stage-edit-dialog_tabs',
            editDialogTabbedContentClass: 'composum-pages-stage-edit-dialog_tabbed-content',
            editDialogTabClass: 'composum-pages-stage-edit-dialog_tab',
            editDialogPathFieldClass: 'composum-pages-stage-edit-dialog_path',
            editDialogButtonDeleteClass: 'composum-pages-stage-edit-dialog_button-delete',
            edit: {
                url: {
                    newDialog: '/bin/cpm/pages/edit.newDialog.html',
                    createDialog: '/bin/cpm/pages/edit.editDialog.create.html',
                    deleteDialog: '/bin/cpm/pages/edit.deleteDialog.html',
                    insert: '/bin/cpm/pages/edit.insertComponent.html'
                }
            },
            site: {
                url: {
                    createDialog: '/libs/composum/pages/stage/edit/site/createsite.html'
                }
            }
        });

        dialogs.ElementDialog = core.components.Dialog.extend({

            initialize: function (options) {
                core.components.Dialog.prototype.initialize.apply(this, [options]);
                this.form = core.getWidget(this.el, "form", core.components.FormWidget);
                this.initView();
                this.$('.' + dialogs.const.editDialogFormClass).on('submit', _.bind(this.onSubmit, this));
                this.$el.on('hidden.bs.modal', _.bind(this.onClose, this));
            },

            setUpWidgets: function (root) {
                window.widgets.setUp(root);
            },

            initView: function () {
            },

            resetOnShown: function () {
            },

            validationReset: function () {
                this.$alert.addClass('alert-hidden');
                this.$alert.html('');
                this.form.validationReset();
            },

            alert: function (type, label, message, hint) {
                if (message) {
                    this.$alert.append('<div class="text-' +
                        type + '"><span class="label">' +
                        label + '</span><span class="message">' +
                        message + (hint ? " (" + hint + ")" : '') + '</span></div>');
                    this.$alert.removeClass('alert-hidden');
                }
            },

            onSubmit: function (event) {
                if (event) {
                    event.preventDefault();
                }
                this.form.prepare();
                this.validationReset();
                if (this.form.validate(_.bind(this.alert, this))) {
                    this.doSubmit();
                } else {

                }
                return false;
            },

            onClose: function (event) {
                this.$el.remove();
            }
        });

        //
        // Edit Dialog
        //

        dialogs.EditDialog = dialogs.ElementDialog.extend({

            initView: function () {
                this.initTabs();
                this.$('.' + dialogs.const.editDialogButtonDeleteClass).click(_.bind(this.doDelete, this));
            },

            initTabs: function () {
                var tabsFound = false;
                var $tabList = this.$tabList = this.$('.' + dialogs.const.editDialogTabListCass);
                this.$('.' + dialogs.const.editDialogTabClass).each(function () {
                    var $tab = $(this);
                    $tabList.append('<li' + (tabsFound ? '' : ' class="active"')
                        + '><a data-toggle="tab" href="#' + $tab.attr('id') + '">'
                        + $tab.data('label') + '</a></li>');
                    if (!tabsFound) {
                        tabsFound = true;
                        $tab.addClass('fade in active');
                    }
                });
                if (tabsFound) {
                    this.$('.' + dialogs.const.editDialogTabbedContentClass).addClass('tab-content');
                    this.$el.addClass(dialogs.const.editDialogTabbedClass);
                }
            },

            doSubmit: function () {
                this.submitForm(_.bind(function () {
                    $(document).trigger('component:changed', [this.data.path]);
                }, this));
            },

            doDelete: function () {
                this.$('.' + dialogs.const.editDialogPathFieldClass)
                    .before('<input name=":operation" type="hidden" value="delete"/>');
                this.submitForm(undefined, undefined, _.bind(function () {
                    $(document).trigger('component:selected', []);
                    $(document).trigger('component:deleted', [this.data.path]);
                }, this));
            }
        });

        dialogs.openEditDialog = function (name, path, type, url) {
            pages.dialogHandler.openEditDialog(url ? url : dialogs.const.editDialogLoadUrl,
                dialogs.EditDialog, name, path, type);
        };

        //
        // Create elements
        //

        dialogs.CreateDialog = dialogs.EditDialog.extend({

            initView: function () {
                this.initTabs();
            },

            afterLoad: function (name, path, type) {
                // set resource Type if such a hidden field is available
                //this.$('[name="' + dialogs.const.pages.const.sling.resourceType + '"]').attr('value',type);
            }
        });

        dialogs.openCreateDialog = function (name, path, type, url, onNotFound) {
            pages.dialogHandler.openEditDialog(url ? url : dialogs.const.edit.url.createDialog,
                dialogs.CreateDialog, name, path, type, onNotFound);
        };

        dialogs.NewElementDialog = dialogs.ElementDialog.extend({

            initView: function () {
                this.elementType = core.getWidget(this.el, '.radio-group-widget', core.components.RadioGroupWidget);
                if (this.elementType.getCount() == 1) {
                    this.useDefault = this.elementType.getOnlyOne();
                }
            },

            doSubmit: function (type) {
                if (!type) {
                    type = this.elementType.getValue();
                    this.hide();
                }
                if (type) {
                    dialogs.openCreateDialog('*', this.data.path, type, undefined,
                        // if no create dialog exists (not found) create a new instance directly
                        _.bind(function (name, path, type) {
                            core.ajaxPost(dialogs.const.edit.url.insert, {
                                resourceType: type,
                                targetPath: path,
                                targetType: this.data.type
                            }, {}, _.bind(function () {
                                $(document).trigger('component:changed', [path]);
                            }, this));
                        }, this));
                }
                return false;
            }
        });

        dialogs.openNewElementDialog = function (name, path, type) {
            pages.dialogHandler.openEditDialog(dialogs.const.edit.url.newDialog,
                dialogs.NewElementDialog, name, path, type);
        };

        //
        // Delete elements
        //

        dialogs.DeleteElementDialog = dialogs.ElementDialog.extend({

            doSubmit: function () {
                this.submitForm(undefined, undefined, _.bind(function () {
                    $(document).trigger('component:selected', []);
                    $(document).trigger('component:deleted', [this.data.path]);
                }, this));
            }
        });

        dialogs.openDeleteElementDialog = function (name, path, type) {
            pages.dialogHandler.openEditDialog(dialogs.const.edit.url.deleteDialog,
                dialogs.DeleteElementDialog, name, path, type);
        };

        //
        // Create Site
        //

        dialogs.CreateSiteDialog = dialogs.EditDialog.extend({

            doSubmit: function () {
                alert('site.create.dialog.submit... ' + JSON.stringify(this.form.getValues()));
            }
        });

        dialogs.openCreateSiteDialog = function () {
            pages.dialogHandler.openEditDialog(dialogs.const.site.url.createDialog,
                dialogs.CreateSiteDialog);
        };

    })(window.composum.pages.dialogs, window.composum.pages, window.core);
})(window);
