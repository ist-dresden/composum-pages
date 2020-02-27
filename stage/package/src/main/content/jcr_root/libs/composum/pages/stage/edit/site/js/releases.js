(function () {
    'use strict';
    CPM.namespace('pages.releases');

    (function (releases, pages, components, core) {

        releases.const = _.extend(releases.const || {}, {
            css: { // edit UI CSS selector keys
                page: {
                    base: 'composum-pages-site-view-page',
                    _activated: '-activated',
                    _modified: '-modified',
                    _selectAll: '_page-select-all',
                    _select: '_page-select'
                },
                release: {
                    base: 'composum-pages-site-view_releases',
                    _release: '_release',
                    _select: '-select'
                }
            },
            url: { // servlet URLs for GET and change requests
                edit: {
                    servlet: '/bin/cpm/pages/edit',
                    _resource: '.editResource.html',
                    _params: '?attr=pages'
                },
                dialog: {
                    base: '/libs/composum/pages/stage/edit/default/page/dialog',
                    _activate: '/activate',
                    _revert: '/revert'
                },
                release: {
                    servlet: '/bin/cpm/pages/release.',
                    root: '/libs/composum/pages/stage/edit/site/releases/',
                    _edit: 'edit.html',
                    _finalize: 'finalize.html',
                    _delete: 'delete.html'
                }
            }
        });

        // page finalization

        releases.ModifiedPages = Backbone.View.extend({

            initialize: function (options) {
                var c = releases.const.css.page;
                this.sitePath = this.$el.data('path');
                this.$contentType = this.$('.composum-pages-site-view-page_type');
                this.$contentType.change(_.bind(this.changeScope, this));
                this.$filter = this.$('.composum-pages-site-view-page_filter');
                this.$filter.change(_.bind(this.changeScope, this));
                this.$buttonActivate = this.$('button.activate');
                this.$buttonActivate.click(_.bind(this.doActivate, this));
                this.$buttonReload = this.$('button.reload');
                this.$buttonReload.click(_.bind(this.reload, this));
                this.$selectAllBox = this.$('.' + c.base + c._modified + c._selectAll);
                this.$selectAllBox.on("change", _.bind(this.selectAll, this));
            },

            changeScope: function (event) {
                event.preventDefault();
                var type = this.$contentType.val();
                var filter = this.$filter.val();
                var url = window.location.href
                    .replace(/&type=[^&]*/, '')
                    .replace(/&filter=[^&]*/, '');
                window.location.href = url + (type ? ('&type=' + type) : '') + (filter ? ('&filter=' + filter) : '');
                return false;
            },

            selectAll: function (event) {
                var c = releases.const.css.page;
                event.preventDefault();
                this.$('.' + c.base + c._modified + c._select).each(function (index, value) {
                    value.checked = event.currentTarget.checked;
                });
            },

            reload: function () {
            },

            doActivate: function (event) {
                var c = releases.const.css.page;
                var u = releases.const.url;
                event.preventDefault();
                if (this.sitePath) {
                    var objects = $('.' + c.base + c._modified + c._select)
                        .filter(function (index, element) {
                            return element.checked;
                        })
                        .map(function (index, element) {
                            return {path: element.dataset.path};
                        })
                        .toArray();
                    var url = u.edit.servlet + u.edit._resource + u.dialog.base + u.dialog._activate + u.edit._params;
                    if (pages.elements) { // context is a page
                        pages.elements.openGenericDialog({
                            path: this.sitePath
                        }, {
                            url: url,
                            type: 'window.composum.pages.dialogs.ActivateContentDialog'
                        }, objects);
                    } else { // context is the stage edit frame
                        pages.dialogs.openGenericDialog(url, pages.dialogs.ActivateContentDialog, objects);
                    }
                }
            }
        });

        releases.modifiedContent = core.getView('.modifiedContent', releases.ModifiedPages);

        // release creation

        releases.ReleaseChanges = Backbone.View.extend({

            initialize: function (options) {
                var c = releases.const.css.page;
                this.sitePath = this.$el.data('path');
                this.$contentType = this.$('.composum-pages-site-view-page_type');
                this.$contentType.change(_.bind(this.changeScope, this));
                this.$filter = this.$('.composum-pages-site-view-page_filter');
                this.$filter.change(_.bind(this.changeScope, this));
                this.$buttonRevert = this.$('button.revert');
                this.$buttonRevert.click(_.bind(this.doRevert, this));
                this.$buttonReload = this.$('button.reload');
                this.$buttonReload.click(_.bind(this.reload, this));
                this.$selectAllBox = this.$('.' + c.base + c._activated + c._selectAll);
                this.$selectAllBox.on("change", _.bind(this.selectAll, this));
            },

            changeScope: function (event) {
                event.preventDefault();
                var type = this.$contentType.val();
                var filter = this.$filter.val();
                var url = window.location.href
                    .replace(/&type=[^&]*/, '')
                    .replace(/&filter=[^&]*/, '');
                window.location.href = url + (type ? ('&type=' + type) : '') + (filter ? ('&filter=' + filter) : '');
                return false;
            },

            selectAll: function (event) {
                var c = releases.const.css.page;
                event.preventDefault();
                this.$('.' + c.base + c._activated + c._select).each(function (index, value) {
                    value.checked = event.currentTarget.checked;
                });
            },

            reload: function () {
            },

            doRevert: function (event) {
                var c = releases.const.css.page;
                var u = releases.const.url;
                event.preventDefault();
                if (this.sitePath) {
                    var objects = $('.' + c.base + c._activated + c._select)
                        .filter(function (index, element) {
                            return element.checked;
                        })
                        .map(function (index, element) {
                            return {path: element.dataset.path};
                        })
                        .toArray();
                    var url = u.edit.servlet + u.edit._resource + u.dialog.base + u.dialog._revert + u.edit._params;
                    if (pages.elements) { // context is a page
                        pages.elements.openGenericDialog({
                            path: this.sitePath
                        }, {
                            url: url,
                            type: 'window.composum.pages.dialogs.RevertContentDialog'
                        }, objects);
                    } else { // context is the stage edit frame
                        pages.dialogs.openGenericDialog(url, pages.dialogs.RevertContentDialog, objects);
                    }
                }
            }

        });

        releases.releaseChanges = core.getView('.releaseChanges', releases.ReleaseChanges);

        // release manipulation

        releases.SiteReleases = Backbone.View.extend({

            initialize: function (options) {
                var c = (options || {}).cssBase || releases.const.css.release;
                this.releaseRadios = $('.' + c.base + c._release + c._select);
                this.sitePath = this.$el.data('path');
                this.$buttonEdit = this.$('button.release-edit');
                this.$buttonPublic = this.$('button.release-public');
                this.$buttonPreview = this.$('button.release-preview');
                this.$buttonDelete = this.$('button.release-delete');
                this.$buttonReload = this.$('button.reload');
                this.$buttonFinalize = this.$('button.release-finalize');
                this.$buttonEdit.click(_.bind(this.editRelease, this));
                this.$buttonPublic.click(_.bind(this.publicRelease, this));
                this.$buttonPreview.click(_.bind(this.previewRelease, this));
                this.$buttonDelete.click(_.bind(this.deleteRelease, this));
                this.$buttonReload.click(_.bind(this.reload, this));
                this.$buttonFinalize.click(_.bind(this.doFinalize, this));
            },

            reload: function () {
            },

            getSelection: function (stage) {
                var result = undefined;
                var that = this;
                $.each(this.releaseRadios, _.bind(function (index, value) {
                    var $value = $(value);
                    if ((stage && $value.is('is-' + stage)) || (!stage && value.checked)) {
                        result = {
                            key: value.value,
                            path: $value.data('path'),
                            label: $value.data('label')
                        };
                    }
                }));
                return result;
            },

            changeRelease: function (stage) {
                var selection = this.getSelection();
                if (selection) {
                    var current = this.getSelection(stage);
                    var config = {
                        path: this.sitePath,
                        stage: stage,
                        targetKey: selection.key,
                        targetLabel: selection.label
                    };
                    if (current) {
                        config.currentKey = current.key;
                        config.currentLabel = current.label;
                    }
                    var replication = CPM.platform.services.replication;
                    var u = replication.const.url;
                    var url = u.base + u._dialog + '.' + stage + '.html' + this.sitePath;
                    core.openLoadedDialog(url, replication.PublishDialog, config, undefined,
                        _.bind(function () {
                            if (pages.elements) {
                                pages.elements.triggerEvent(pages.elements.const.event.site.changed, [this.sitePath]);
                            } else {
                                pages.trigger('releases.change', pages.const.event.site.changed, [this.sitePath]);
                            }
                        }, this));
                }
            },

            openDialog: function (type, useSelection) {
                var u = releases.const.url.release;
                event.preventDefault();
                var selection = useSelection ? this.getSelection() : {key: undefined, path: this.sitePath};
                if (selection && selection.path) {
                    if (pages.elements) { // context is a page
                        pages.elements.openEditDialog({
                            path: selection.path
                        }, {
                            url: u.root + type
                        });
                    } else { // context is the stage edit frame
                        pages.dialogs.openEditDialog(selection.key, selection.path, undefined/*type*/,
                            undefined/*context*/, u.root + type);
                    }
                }
                return false;
            },

            doFinalize: function (event) {
                event.preventDefault();
                this.openDialog(releases.const.url.release._finalize, false);
                return false;
            },

            publicRelease: function (event) {
                event.preventDefault();
                this.changeRelease('public');
                return false;
            },


            previewRelease: function (event) {
                event.preventDefault();
                this.changeRelease('preview');
                return false;
            },

            editRelease: function (event) {
                event.preventDefault();
                this.openDialog(releases.const.url.release._edit, true);
                return false;
            },

            deleteRelease: function (event) {
                event.preventDefault();
                this.openDialog(releases.const.url.release._delete, true);
                return false;
            }
        });

        releases.siteReleases = core.getView('.releasesList', releases.SiteReleases);

    })(CPM.pages.releases, CPM.pages, CPM.core.components, CPM.core);

})();
