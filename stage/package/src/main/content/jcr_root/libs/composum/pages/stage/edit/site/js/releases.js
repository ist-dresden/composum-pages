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
                    _selectAll: '_select-all',
                    _select: '_select'
                },
                release: {
                    base: 'composum-pages-stage-edit-site-releases',
                    _release: '-release',
                    _select: '_select'
                }
            },
            url: { // servlet URLs for GET and change requests
                release: {
                    root: '/libs/composum/pages/stage/edit/site/releases/',
                    _create: 'create.html',
                    _delete: 'delete.html'
                },
                servlet: '/bin/cpm/pages/release.'
            }
        });

        // release creation

        releases.FinishedPages = Backbone.View.extend({

            initialize: function (options) {
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
                $.each($('.' + c.base + c._finished + c._select), function (index, value) {
                    value.checked = event.currentTarget.checked;
                });
            },

            doRelease: function (event) {
                var c = releases.const.css.page;
                var u = releases.const.url.release;
                event.preventDefault();
                var objects = $('.' + c.base + c._finished + c._select)
                    .filter(function (index, element) {
                        return element.checked;
                    })
                    .map(function (index, element) {
                        return element.dataset.path;
                    })
                    .toArray();
                pages.elements.openEditDialog({
                    path: this.sitePath
                }, {
                    url: u.root + u._create
                }, {
                    objects: objects.toString()
                });
            }

        });

        releases.finishedPagesHead = core.getView('.finishedPages', releases.FinishedPages);

        // release manipulation

        releases.Releases = Backbone.View.extend({

            initialize: function (options) {
                this.sitePath = this.$el.data('path');
                this.$buttonPublic = this.$('button.public');
                this.$buttonPreview = this.$('button.preview');
                this.$buttonDelete = this.$('button.delete');
                this.$buttonPublic.click(_.bind(this.publicRelease, this));
                this.$buttonPreview.click(_.bind(this.previewRelease, this));
                this.$buttonDelete.click(_.bind(this.deleteRelease, this));
            },

            changeRelease: function (action) {
                var c = releases.const.css.release;
                this.releaseRadios = $('.' + c.base + c._release + c._select);
                $.each(this.releaseRadios, _.bind(function (index, value) {
                    if (value.checked) {
                        var key = value.value;
                        var path = this.sitePath;
                        // call server with path and key
                        core.ajaxPost(
                            releases.const.url.servlet + action + '.html', {
                                path: path,
                                releaseName: key
                            }, {},
                            _.bind(function (result) { // onSuccess
                                location.reload();
                            }, this),
                            _.bind(function (result) { // onError
                                pages.elements.alertMessage('danger', 'Error',
                                    'Error on change release state', result)
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
                        pages.elements.openEditDialog({
                            path: path
                        }, {
                            url: u.root + u._delete
                        });
                    }
                }, this));
                return false;
            }
        });

        releases.releases = core.getView('.releasesList', releases.Releases);


    })(window.composum.pages.releases, window.composum.pages, window.core);
})(window);
