(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            releases: {
                css: {
                    site: {
                        base: 'composum-pages-stage-edit-site-releases'
                    },
                    release: {
                        base: 'composum-pages-stage-edit-site-releases-release',
                        _listentry: '_listentry',
                        _entry: '_entry'
                    },
                    tools: {
                        base: 'composum-pages-stage-edit-tools-site-releases'
                    }
                },
                uri: {
                    load: '/libs/composum/pages/stage/edit/tools/site/releases.content.html',
                    release: {
                        view: '/libs/composum/pages/stage/edit/site/release.html'
                    }
                }
            }
        });

        tools.SiteReleases = pages.releases.SiteReleases.extend({

            initialize: function () {
                this.initContent();
                var c = pages.const.event;
                $(document).on(c.site.state + '.ModifiedPages', _.bind(this.reload, this));
                $(document).on(c.site.changed + '.ModifiedPages', _.bind(this.reload, this));
            },

            initContent: function () {
                var c = tools.const.releases.css;
                pages.releases.SiteReleases.prototype.initialize.apply(this);
                this.sitePath = this.$('.' + c.site.base).data('path');
                this.$releaseView = [];
                this.$('.' + c.release.base + c.release._entry).click(_.bind(this.releaseView, this));
            },

            releaseView: function (event) {
                var c = tools.const.releases.css.release;
                var $entry = $(event.currentTarget);
                var path = $entry.data('path');
                var $listEntry = $entry.closest('.' + c.base + c._listentry);
                if ((this.$releaseView.length > 0 && this.$releaseView[0] === $listEntry[0])
                    || core.getNameFromPath(path) === 'current') {
                    this.closeReleaseView();
                } else {
                    if (this.$releaseView.length > 0) {
                        this.$releaseView.removeClass('selected');
                    }
                    this.$releaseView = $listEntry;
                    $('body').addClass('context-driven-view');
                    $listEntry.addClass('selected');
                    pages.log.debug('site.trigger.' + pages.const.event.page.view + '(' + path + ',preview)');
                    $(document).trigger(pages.const.event.page.view, [path, {'pages.view': 'preview'},
                        tools.const.releases.uri.release.view]);
                }
            },

            closeReleaseView: function () {
                if (this.$releaseView.length > 0) {
                    $('body').removeClass('context-driven-view');
                    this.$releaseView.removeClass('selected');
                    this.$releaseView = [];
                    pages.log.debug('site.trigger.' + pages.const.event.page.view + '()');
                    $(document).trigger(pages.const.event.page.view, [null, {}]);
                }
            },

            beforeClose: function () {
                this.beforeHideTab();
            },

            beforeHideTab: function () {
                this.closeReleaseView();
            },

            onTabSelected: function () {
                this.reload();
            },

            reload: function () {
                this.closeReleaseView();
                var c = tools.const.releases.uri;
                core.getHtml(c.load + this.contextTabs.reference.path,
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
            var c = tools.const.releases.css;
            var panel = core.getWidget(contextTabs.el, '.' + c.tools.base, tools.SiteReleases);
            if (panel) {
                panel.contextTabs = contextTabs;
            }
            return panel;
        });

    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
