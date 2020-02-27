(function () {
    'use strict';
    CPM.namespace('pages.tools');

    (function (tools, pages, core) {

        tools.const = _.extend(tools.const || {}, {
            releases: {
                event: {
                    id: '.tools.SiteReleases'
                },
                css: {
                    site: {
                        base: 'composum-pages-site-tools_releases',
                        _release: '_release',
                        _select: '-select'
                    },
                    release: {
                        base: 'composum-pages-site-tools_releases_release',
                        _listentry: '-listentry',
                        _entry: '-entry'
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

        tools.FinalizeDialog = core.components.FormDialog.extend({

            initialize: function (options) {
                core.components.FormDialog.prototype.initialize.call(this, options);
            }
        });

        tools.SiteReleases = pages.releases.SiteReleases.extend({

            initialize: function (options) {
                this.initContent(options);
                var e = pages.const.event;
                var id = tools.const.releases.event.id;
                $(document)
                    .on(e.site.state + id, _.bind(this.reload, this))
                    .on(e.site.changed + id, _.bind(this.reload, this));
            },

            beforeClose: function () {
                this.beforeHideTab();
                var e = pages.const.event;
                var id = tools.const.releases.event.id;
                $(document)
                    .off(e.site.state + id)
                    .off(e.site.changed + id);
            },

            initContent: function (options) {
                var c = tools.const.releases.css;
                pages.releases.SiteReleases.prototype.initialize.call(this, _.extend(options || {}, {
                    cssBase: tools.const.releases.css.site
                }));
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
                    pages.trigger('site.release.view', pages.const.event.content.view, [path, {'pages.view': 'preview'},
                        tools.const.releases.uri.release.view]);
                }
            },

            closeReleaseView: function () {
                if (this.$releaseView.length > 0) {
                    $('body').removeClass('context-driven-view');
                    this.$releaseView.removeClass('selected');
                    this.$releaseView = [];
                    pages.trigger('site.release.close', pages.const.event.content.view, [null, {}]);
                }
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
                        this.initContent({});
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

    })(CPM.pages.tools, CPM.pages, CPM.core);
})();
