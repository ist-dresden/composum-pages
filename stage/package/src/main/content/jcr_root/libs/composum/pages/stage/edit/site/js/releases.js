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
                    _finished: '-finished',
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
                modified: {
                    checkpoint: '/bin/cpm/pages/edit.checkpoint.json'
                },
                release: {
                    servlet: '/bin/cpm/pages/release.',
                    root: '/libs/composum/pages/stage/edit/site/releases/',
                    _create: 'create.html',
                    _delete: 'delete.html'
                }
            }
        });

        // page finalization

        releases.ModifiedPages = Backbone.View.extend({

            initialize: function () {
                var c = releases.const.css.page;
                this.sitePath = this.$el.data('path');
                this.$button = this.$('button.checkpoint');
                this.$button.click(_.bind(this.doCheckpoint, this));
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

            doCheckpoint: function (event) {
                var c = releases.const.css.page;
                var u = releases.const.url.modified;
                event.preventDefault();
                if (this.sitePath) {
                    var objects = $('.' + c.base + c._modified + c._select)
                        .filter(function (index, element) {
                            return element.checked;
                        })
                        .map(function (index, element) {
                            return element.dataset.path;
                        })
                        .toArray().toString();
                    core.ajaxPost(u.checkpoint, {
                            paths: objects
                        }, {},
                        _.bind(function () {
                            window.location.reload();
                        }, this), _.bind(function (result) {
                            var fn = pages.elements ? pages.elements.alertMessage : core.alert;
                            fn.call(this, 'danger', 'Error', 'Error on creating checkpoint', result);
                        }, this)
                    );
                }
            }

        });

        releases.modifiedPages = core.getView('.modifiedPages', releases.ModifiedPages);

        // release creation

        releases.FinishedPages = Backbone.View.extend({

            initialize: function () {
                var c = releases.const.css.page;
                this.sitePath = this.$el.data('path');
                this.$button = this.$('button.release');
                this.$button.click(_.bind(this.doRelease, this));
                this.$selectAllBox = this.$('.' + c.base + c._finished + c._selectAll);
                this.$selectAllBox.on("change", _.bind(this.selectAll, this));
            },

            selectAll: function (event) {
                var c = releases.const.css.page;
                event.preventDefault();
                this.$('.' + c.base + c._finished + c._select).each(function (index, value) {
                    value.checked = event.currentTarget.checked;
                });
            },

            doRelease: function (event) {
                var c = releases.const.css.page;
                var u = releases.const.url.release;
                event.preventDefault();
                if (this.sitePath) {
                    var objects = $('.' + c.base + c._finished + c._select)
                        .filter(function (index, element) {
                            return element.checked;
                        })
                        .map(function (index, element) {
                            return element.dataset.path;
                        })
                        .toArray().toString();
                    if (pages.elements) { // context is a page
                        pages.elements.openEditDialog({
                            path: this.sitePath
                        }, {
                            url: u.root + u._create
                        }, {
                            objects: objects
                        });
                    } else { // context is the stage edit frame
                        pages.dialogs.openEditDialog(undefined, this.sitePath, undefined, u.root + u._create,
                            function (dialog) {
                                if (objects) {
                                    dialog.applyData({
                                        objects: objects
                                    });
                                }
                            });
                    }
                }
            }

        });

        releases.finishedPages = core.getView('.finishedPages', releases.FinishedPages);

        // release manipulation

        releases.SiteReleases = Backbone.View.extend({

            initialize: function (options) {
                this.sitePath = this.$el.data('path');
                this.$buttonPublic = this.$('button.release-public');
                this.$buttonPreview = this.$('button.release-preview');
                this.$buttonDelete = this.$('button.release-delete');
                this.$buttonPublic.click(_.bind(this.publicRelease, this));
                this.$buttonPreview.click(_.bind(this.previewRelease, this));
                this.$buttonDelete.click(_.bind(this.deleteRelease, this));
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
                                window.location.reload();
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
                            pages.dialogs.openEditDialog(value.value, path, undefined, u.root + u._delete);
                        }
                    }
                }, this));
                return false;
            }
        });

        releases.siteReleases = core.getView('.releasesList', releases.SiteReleases);


    })(window.composum.pages.releases, window.composum.pages, window.core);
})(window);
