(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            fileStatus: {
                event: {
                    id: '.tools.FileStatus'
                },
                css: {
                    base: 'composum-pages-stage-file-status',
                    _: {
                        icon: '_icon'
                    }
                },
                uri: {
                    base: '/libs/composum/pages/stage/edit/default/file/status',
                    _: {
                        release: '.releaseStatus.html',
                        popover: '.releasePopover.html'
                    }
                }
            }
        });

        tools.FileStatus = Backbone.View.extend({

            initialize: function (options) {
                var e = pages.const.event;
                var id = tools.const.fileStatus.event.id;
                $(document)
                    .on(e.content.changed + id, _.bind(this.reload, this))
                    .on(e.content.state + id, _.bind(this.reload, this));
                this.reload();
            },

            reload: function () {
                delete this.$icon;
                delete this.popover;
                if (pages.contextTools.log.getLevel() <= log.levels.DEBUG) {
                    pages.contextTools.log.debug('status.reload(' + pages.current.page + ')');
                }
                if (pages.current.file) {
                    var u = tools.const.fileStatus.uri;
                    core.getHtml(u.base + u._.release + pages.current.file, _.bind(function (content) {
                        this.$el.html(content);
                        var c = tools.const.fileStatus.css;
                        this.$icon = this.$('.' + c.base + c._.icon);
                        this.$icon.click(_.bind(this.initPopover, this));
                    }, this));
                } else {
                    this.$el.html('');
                }
            },

            initPopover: function (event) {
                event.preventDefault();
                if (!this.popover) {
                    var u = tools.const.fileStatus.uri;
                    core.getHtml(u.base + u._.popover + pages.current.file, _.bind(function (content) {
                        this.popover = true;
                        this.$icon.popover({
                            placement: 'left',
                            animation: false,
                            html: true,
                            sanitize: false,
                            content: content,
                            viewport: {
                                selector: 'body .composum-pages-stage-edit-tools-main-assets',
                                padding: 8
                            }
                        });
                        this.$icon.popover('show');
                    }, this));
                }
                return false;
            }
        });

    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
