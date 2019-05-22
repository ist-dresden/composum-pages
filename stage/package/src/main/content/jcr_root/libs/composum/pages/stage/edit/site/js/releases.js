(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.releases = window.composum.pages.releases || {};

    (function (releases, pages, core) {
        'use strict';

        releases.const = _.extend(releases.const || {}, {
            css: { // edit UI CSS selector keys
                page: {
                    base: 'composum-pages-stage-edit-site-page',
                    _activated: '-activated',
                    _modified: '-modified',
                    _selectAll: '_page-select-all',
                    _select: '_page-select'
                },
                release: {
                    base: 'composum-pages-stage-edit-site-releases',
                    _release: '-release',
                    _select: '_select'
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

            initialize: function () {
                var c = releases.const.css.page;
                this.sitePath = this.$el.data('path');
                this.$button = this.$('button.activate');
                this.$button.click(_.bind(this.doActivate, this));
                this.$selectAllBox = this.$('.' + c.base + c._modified + c._selectAll);
                this.$selectAllBox.on("change", _.bind(this.selectAll, this));
            },

            selectAll: function (event) {
                var c = releases.const.css.page;
                event.preventDefault();
                this.$('.' + c.base + c._modified + c._select).each(function (index, value) {
                    value.checked = event.currentTarget.checked;
                });
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
                            type: 'pages.dialogs.ActivatePageDialog'
                        }, objects);
                    } else { // context is the stage edit frame
                        pages.dialogs.openGenericDialog(url, pages.dialogs.ActivatePageDialog, objects);
                    }
                }
            }
        });

        releases.modifiedPages = core.getView('.modifiedPages', releases.ModifiedPages);

        // release creation

        releases.ReleaseChanges = Backbone.View.extend({

            initialize: function () {
                var c = releases.const.css.page;
                this.sitePath = this.$el.data('path');
                this.$button = this.$('button.release');
                this.$button.click(_.bind(this.doRevert, this));
                this.$selectAllBox = this.$('.' + c.base + c._activated + c._selectAll);
                this.$selectAllBox.on("change", _.bind(this.selectAll, this));
            },

            selectAll: function (event) {
                var c = releases.const.css.page;
                event.preventDefault();
                this.$('.' + c.base + c._activated + c._select).each(function (index, value) {
                    value.checked = event.currentTarget.checked;
                });
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
                            return element.dataset.path;
                        })
                        .toArray().toString();
                    var url = u.edit.servlet + u.edit._resource + u.dialog.base + u.dialog._revert + u.edit._params;
                    if (pages.elements) { // context is a page
                        pages.elements.openGenericDialog({
                            path: this.sitePath
                        }, {
                            url: url,
                            type: 'pages.dialogs.RevertPageDialog'
                        }, objects);
                    } else { // context is the stage edit frame
                        pages.dialogs.openGenericDialog(url, pages.dialogs.RevertPageDialog, objects);
                    }
                }
            }

        });

        releases.releaseChanges = core.getView('.releaseChanges', releases.ReleaseChanges);

        // release manipulation

        releases.SiteReleases = Backbone.View.extend({

            initialize: function (options) {
                this.sitePath = this.$el.data('path');
                this.$buttonPublic = this.$('button.release-public');
                this.$buttonPreview = this.$('button.release-preview');
                this.$buttonDelete = this.$('button.release-delete');
                this.$buttonFinalize = this.$('button.release-finalize');
                this.$buttonPublic.click(_.bind(this.publicRelease, this));
                this.$buttonPreview.click(_.bind(this.previewRelease, this));
                this.$buttonDelete.click(_.bind(this.deleteRelease, this));
                this.$buttonFinalize.click(_.bind(this.doFinalize, this));
            },

            doFinalize: function (event) {
                var c = releases.const.css.page;
                var u = releases.const.url.release;
                event.preventDefault();
                if (this.sitePath) {
                    if (pages.elements) { // context is a page
                        pages.elements.openEditDialog({
                            path: this.sitePath
                        }, {
                            url: u.root + u._finalize
                        });
                    } else { // context is the stage edit frame
                        pages.dialogs.openEditDialog(undefined, this.sitePath, undefined, undefined/*context*/,
                            u.root + u._finalize);
                    }
                }
            },

            changeRelease: function (action) {
                var c = releases.const.css.release;
                var u = releases.const.url.release;
                this.releaseRadios = $('.' + c.base + c._release + c._select);
                $.each(this.releaseRadios, _.bind(function (index, value) {
                    if (value.checked) {
                        var key = value.value;
                        var path = this.sitePath;
                        // call server with path and key
                        core.ajaxPost(u.servlet + action + '.html', {
                                path: path,
                                releaseName: key
                            }, {},
                            _.bind(function (result) { // onSuccess
                                if (pages.elements) {
                                    pages.elements.triggerEvent(pages.elements.const.event.site.changed, [this.sitePath]);
                                } else {
                                    pages.log.debug('site.trigger.' + pages.const.event.site.changed + '(' + this.sitePath + ')');
                                    $(document).trigger(pages.const.event.site.changed, [this.sitePath]);
                                }
                            }, this),
                            _.bind(function (result) { // onError
                                var fn = pages.elements ? pages.elements.alertMessage : core.alert;
                                fn.call(this, 'danger', 'Error', 'Error on change release state', result);
                            }, this)
                        );
                    }
                }, this));

            },

            publicRelease: function (event) {
                event.preventDefault();
                this.changeRelease('setpublic');
                return false;
            },


            previewRelease: function (event) {
                event.preventDefault();
                this.changeRelease('setpreview');
                return false;
            },

            deleteRelease: function (event) {
                var c = releases.const.css.release;
                var u = releases.const.url.release;
                event.preventDefault();
                this.releaseRadios = $('.' + c.base + c._release + c._select);
                $.each(this.releaseRadios, _.bind(function (index, value) {
                    if (value.checked) {
                        var path = $(value).data('path');
                        if (pages.elements) { // context is a page
                            pages.elements.openEditDialog({
                                path: path
                            }, {
                                url: u.root + u._delete
                            });
                        } else { // context is the stage edit frame
                            pages.dialogs.openEditDialog(value.value, path, undefined/*type*/,
                                undefined/*context*/, u.root + u._delete);
                        }
                    }
                }, this));
                return false;
            }
        });

        releases.siteReleases = core.getView('.releasesList', releases.SiteReleases);


    })(window.composum.pages.releases, window.composum.pages, window.core);
})(window);
