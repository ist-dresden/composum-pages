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

            onSubmit: function (event) {
                if (event) {
                    event.preventDefault();
                }
                this.form.prepare();
                this.validationReset();
                if (this.form.validate(_.bind(function (type, label, message, hint) {
                        this.message('warning', label, message, hint)
                    }, this))) {
                    this.doSubmit();
                } else {
                    this.onValidationFault();
                }
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

            doSubmit: function () {
                this.submitForm(_.bind(function () {
                    $(document).trigger('component:changed', [this.data.path]);
                }, this));
            },

            doDelete: function () {
                var c = dialogs.const.edit.css;
                this.$('.' + c.base + c._pathField)
                    .before('<input name=":operation" type="hidden" value="delete"/>');
                this.submitForm(_.bind(function () {
                    $(document).trigger('component:selected', []);
                    $(document).trigger('component:deleted', [this.data.path]);
                }, this));
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

        dialogs.openEditDialog = function (name, path, type, url) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(url ? url : c.base + c._dialog.load,
                dialogs.EditDialog, name, path, type);
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
                //this.$('[name="' + dialogs.const.pages.const.sling.resourceType + '"]').attr('value',type);
            }
        });

        dialogs.openCreateDialog = function (name, path, type, url, onNotFound) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(url ? url : c.base + c._dialog.create,
                dialogs.CreateDialog, name, path, type, onNotFound);
        };

        /**
         * the dialog to select the element type of a new element to insert in a container
         */
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

        /**
         * the dialog to delete an element
         */
        dialogs.DeleteElementDialog = dialogs.ElementDialog.extend({

            doSubmit: function () {
                this.submitForm(_.bind(function () {
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
        dialogs.CreateSiteDialog = dialogs.EditDialog.extend({

            doSubmit: function () {
                this.submitForm(_.bind(function (result) {
                    window.location.href = result.editUrl;
                }, this));
            }
        });

        dialogs.openCreateSiteDialog = function () {
            var c = dialogs.const.edit;
            pages.dialogHandler.openEditDialog(c.url.base + c.url._dialog.create,
                dialogs.CreateSiteDialog, undefined, undefined, c.type.site);
        };

        /**
         * Delete Site
         */
        dialogs.DeleteSiteDialog = dialogs.EditDialog.extend({

            doSubmit: function () {
                this.submitForm(_.bind(function () {
                    $(document).trigger('component:selected', []);
                    $(document).trigger('component:deleted', [this.data.path]);
                }, this));
            }
        });

        dialogs.openDeleteSiteDialog = function (name, path, type) {
            var c = dialogs.const.edit.url;
            pages.dialogHandler.openEditDialog(c.base + c._dialog.delete,
                dialogs.DeleteSiteDialog, name, path, type);
        };

    })(window.composum.pages.dialogs, window.composum.pages, window.core);
})(window);
