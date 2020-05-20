(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            pageStatus: {
                event: {
                    id: '.tools.PageStatus'
                },
                css: {
                    base: 'composum-pages-stage-edit-tools-page-status',
                    _: {
                        icon: '_icon'
                    }
                },
                uri: {
                    base: '/libs/composum/pages/stage/edit/tools/page/status',
                    _: {
                        release: '.releaseStatus.html',
                        popover: '.releasePopover.html'
                    }
                }
            }
        });

        tools.PageStatus = Backbone.View.extend({

            initialize: function (options) {
                var e = pages.const.event;
                var id = tools.const.pageStatus.event.id;
                $(document)
                    .on(e.content.state + id, _.bind(this.reload, this))
                    .on(e.page.changed + id, _.bind(this.reload, this))
                    .on(e.element.inserted + id, _.bind(this.reload, this))
                    .on(e.element.changed + id, _.bind(this.reload, this))
                    .on(e.element.moved + id, _.bind(this.reload, this))
                    .on(e.element.deleted + id, _.bind(this.reload, this));
                this.reload();
            },

            beforeClose: function () {
                var e = pages.const.event;
                var id = tools.const.pageStatus.event.id;
                $(document)
                    .off(e.content.state + id)
                    .off(e.page.changed + id)
                    .off(e.element.inserted + id)
                    .off(e.element.changed + id)
                    .off(e.element.moved + id)
                    .off(e.element.deleted + id);
            },

            reload: function () {
                delete this.$icon;
                delete this.popover;
                if (pages.contextTools.log.getLevel() <= log.levels.DEBUG) {
                    pages.contextTools.log.debug('status.reload(' + pages.current.page + ')');
                }
                if (pages.current.page) {
                    var u = tools.const.pageStatus.uri;
                    core.getHtml(u.base + u._.release + core.encodePath(pages.current.page),
                        _.bind(function (content) {
                            this.$el.html(content);
                            var c = tools.const.pageStatus.css;
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
                    var u = tools.const.pageStatus.uri;
                    core.getHtml(u.base + u._.popover + core.encodePath(pages.current.page),
                        _.bind(function (content) {
                            this.popover = true;
                            this.$icon.popover({
                                placement: 'bottom',
                                animation: false,
                                html: true,
                                sanitize: false,
                                content: content
                            });
                            this.$icon.popover('show');
                        }, this));
                }
                return false;
            }
        });

        /**
         * register this tool as a context tool for initialization after load of the context tools set
         */
        pages.contextTools.addTool(function (contextTabs) {
            return core.getWidget(contextTabs.el, '.' + tools.const.pageStatus.css.base, tools.PageStatus);
        });

    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
