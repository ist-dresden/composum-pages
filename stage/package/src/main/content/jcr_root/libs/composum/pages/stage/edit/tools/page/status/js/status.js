(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            pageStatus: {
                css: {
                    base: 'composum-pages-stage-edit-tools-page-status',
                    _: {
                        icon: '_icon'
                    }
                },
                uri: {
                    base: '/libs/composum/pages/stage/edit/tools/page/status',
                    _: {
                        release: '.releaseStatus.html'
                    }
                }
            }
        });

        tools.PageStatus = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.pageStatus.css;
                var id = 'PageStatus';
                var e = pages.const.event;
                $(document).on(e.page.state + '.' + id, _.bind(this.reload, this));
                $(document).on(e.page.changed + '.' + id, _.bind(this.reload, this));
            },

            reload: function () {
                if (pages.current.page) {
                    var u = tools.const.pageStatus.uri;
                    core.getHtml(u.base + u._.release + pages.current.page, _.bind(function (content) {
                        this.$el.html(content);
                    }, this));
                } else {
                    this.$el.html('');
                }
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
