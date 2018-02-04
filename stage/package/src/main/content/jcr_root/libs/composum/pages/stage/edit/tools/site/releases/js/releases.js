(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            releases: {
                page: {
                    base: 'composum-pages-stage-edit-site-releases'
                },
                tools: {
                    base: 'composum-pages-stage-edit-tools-site-releases'
                },
                uri: {
                    load: '/libs/composum/pages/stage/edit/tools/site/releases.content.html'
                }
            }
        });

        tools.SiteReleases = pages.releases.SiteReleases.extend({

            initialize: function () {
                this.initContent();
                $(document).on('site:changed.SiteReleases', _.bind(this.reload, this));
            },

            initContent: function () {
                var c = tools.const.releases.page;
                pages.releases.SiteReleases.prototype.initialize.apply(this);
                this.sitePath = this.$('.' + c.base).data('path');
            },

            onTabSelected: function () {
                this.reload();
            },

            reload: function () {
                var c = tools.const.releases.uri;
                core.getHtml(c.load + this.contextTabs.data.path,
                    undefined, undefined, _.bind(function (data) {
                        if (data.status === 200) {
                            this.$el.html(data.responseText);
                        } else {
                            this.$el.html("");
                        }
                        this.initContent();
                    }, this));
            }
        });

        /**
         * register these tools as a pages context tool for initialization after load of the context tools set
         */
        pages.contextTools.addTool(function (contextTabs) {
            var c = tools.const.releases;
            var panel = core.getWidget(contextTabs.el, '.' + c.tools.base, tools.SiteReleases);
            if (panel) {
                panel.contextTabs = contextTabs;
            }
            return panel;
        });

    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
