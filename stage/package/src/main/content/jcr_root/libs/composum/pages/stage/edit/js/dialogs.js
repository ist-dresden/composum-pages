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
                    _insert: '.insertComponent.html',
                    _dialog: {
                        load: '.editDialog.html',
                        new: '.newDialog.html',
                        create: '.editDialog.create.html',
                        delete: '.editDialog.delete.html'
                    }
                },
                css: {
                    base: 'composum-pages-stage-edit-dialog',
                    _form: '_form',
                    _tab: '_tab',
                    _tabbed: '_tabbed',
                    _tabList: '_tabs',
                    _tabContent: '_tabbed-content',
                    _pathField: '_path',
                    _deleteButton: '_button-delete',
                    _submitButton: '_button-submit'
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
                var c = dialogs.const.edit.css;
                this.initTabs();
                this.$('.' + c.base + c._deleteButton).click(_.bind(this.doDelete, this));
            },

            initTabs: function () {
                var c = dialogs.const.edit.css;
                var tabsFound = false;
                var $tabList = this.$tabList = this.$('.' + c.base + c._tabList);
                this.$('.' + c.base + c._tab).each(function () {
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
                    this.$('.' + c.base + c._tabContent).addClass('tab-content');
                    this.$el.addClass(c.base + c._tabbed);
                }
            },

            doSubmit: function () {
                this.submitForm(_.bind(function () {
                    $(document).trigger('component:changed', [this.data.path]);
                }, this));
            },

            doDelete: function () {
                var c = dialogs.const.edit.css;
                this.$('.' + c.base + c._pathField)
                    .before('<input name=":operation" type="hidden" value="delete"/>');
                this.submitForm(undefined, undefined, _.bind(function () {
                    $(document).trigger('component:selected', []);
                    $(document).trigger('component:deleted', [this.data.path]);
                }, this));
            }
        });

        dialogs.openEditDialog = function (name, path, type, url) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(url ? url : c.base + c._dialog.load,
                dialogs.EditDialog, name, path, type);
        };

        /**
         * Edit with redirect support (form.submit without AJAX)
         */
        dialogs.FormSubmitEditDialog = dialogs.EditDialog.extend({

            /**
             * click on submit button instead of form.submit to support redirect responses
             */
            initSubmit: function () {
                var c = dialogs.const.edit.css;
                this.$('.' + c.base + c._submitButton).on('click', _.bind(this.onSubmit, this));
            },

            /**
             * use normal form submit, prevent from AJAX requests to support redirect answers
             */
            doSubmit: function () {
                this.form.$el.submit();
            }
        });

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
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(url ? url : c.base + c._dialog.create,
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
                var c = dialogs.const.edit.url;
                if (!type) {
                    type = this.elementType.getValue();
                    this.hide();
                }
                if (type) {
                    dialogs.openCreateDialog('*', this.data.path, type, undefined,
                        // if no create dialog exists (not found) create a new instance directly
                        _.bind(function (name, path, type) {
                            core.ajaxPost(c.base + c._insert, {
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
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.base + c._dialog.new,
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
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.base + c._dialog.delete,
                dialogs.DeleteElementDialog, name, path, type);
        };

        //
        // Sites...
        //

        /**
         * Create Site
         */
        dialogs.CreateSiteDialog = dialogs.FormSubmitEditDialog.extend({});

        dialogs.openCreateSiteDialog = function () {
            var c = dialogs.const.edit;
            pages.dialogHandler.openEditDialog(c.url.base + c.url._dialog.create,
                dialogs.CreateSiteDialog, undefined, undefined, c.type.site);
        };

        /**
         * Delete Site
         */
        dialogs.DeleteSiteDialog = dialogs.FormSubmitEditDialog.extend({});

        dialogs.openDeleteSiteDialog = function (name, path, type) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.base + c._dialog.delete,
                dialogs.DeleteSiteDialog, name, path, type);
        };

    })(window.composum.pages.dialogs, window.composum.pages, window.core);
})(window);
