(function () {
    'use strict';
    CPM.namespace('pages.tools.site');

    (function (site, pages, core) {

        site.const = _.extend(site.const || {}, {
            activated: {
                event: {
                    id: '.tools.ActivatedPages'
                },
                page: {
                    base: 'composum-pages-site-view-page-activated',
                    _listentry: '_listentry',
                    _entry: '_page-entry'
                },
                tools: {
                    base: 'composum-pages-stage-edit-tools-site-activated',
                    _panel: '_tools-panel'
                },
                uri: {
                    load: '/libs/composum/pages/stage/edit/tools/site/activated.content.html'
                }
            }
        });

        site.ActivatedPages = pages.releases.ReleaseChanges.extend({

            initialize: function (options) {
                this.initContent();
                var id = site.const.activated.event.id;
                var e = pages.const.event;
                $(document)
                    .on(e.content.state + id, _.bind(this.reload, this))
                    .on(e.site.changed + id, _.bind(this.reload, this));
            },

            beforeClose: function () {
                this.beforeHideTab();
                var e = pages.const.event;
                var id = site.const.activated.event.id;
                $(document)
                    .off(e.content.state + id)
                    .off(e.site.changed + id);
            },

            initContent: function (options) {
                var c = site.const.activated.page;
                pages.releases.ReleaseChanges.prototype.initialize.call(this, options);
                this.sitePath = this.$('.' + c.base).data('path');
                this.$filter = this.$('.composum-pages-site-view-page_filter');
                this.$filter.find('a').click(_.bind(this.doFilter, this));
                this.$previewEntry = [];
                this.$('.' + c.base + c._entry).click(_.bind(this.pagePreview, this));
            },

            changeScope: function (event) {
                event.preventDefault();
                this.reload(event, this.$contentType.val());
                return false;
            },

            doFilter: function (event) {
                this.reload(event, this.$contentType.val(), $(event.currentTarget).data('value'));
            },

            pagePreview: function (event) {
                var c = site.const.activated.page;
                var $entry = $(event.currentTarget);
                var path = $entry.data('path');
                var $listEntry = $entry.closest('.' + c.base + c._listentry);
                if (this.$previewEntry.length > 0 && this.$previewEntry[0] === $listEntry[0]) {
                    this.closePreview();
                } else {
                    if (this.$previewEntry.length > 0) {
                        this.$previewEntry.removeClass('selected');
                    }
                    this.$previewEntry = $listEntry;
                    $('body').addClass('context-driven-view');
                    $listEntry.addClass('selected');
                    pages.trigger('site.activated.view', pages.const.event.content.view,
                        [path, {'pages.view': 'preview'}, $entry.data('viewer')]);
                }
            },

            closePreview: function () {
                if (this.$previewEntry.length > 0) {
                    $('body').removeClass('context-driven-view');
                    this.$previewEntry.removeClass('selected');
                    this.$previewEntry = [];
                    pages.trigger('site.activated.close', pages.const.event.content.view, [null, {}]);
                }
            },

            beforeHideTab: function () {
                this.closePreview();
            },

            onTabSelected: function () {
                this.reload();
            },

            reload: function (event, type, filter) {
                this.closePreview();
                var params = type ? '?type=' + type : '';
                params += filter ? ((type ? '&' : '?') + 'filter=' + filter) : '';
                var c = site.const.activated.uri;
                core.getHtml(c.load + core.encodePath(this.contextTabs.reference.path) + params,
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
            var c = site.const.activated;
            var panel = core.getWidget(contextTabs.el, '.' + c.tools.base, site.ActivatedPages);
            if (panel) {
                panel.contextTabs = contextTabs;
            }
            return panel;
        });

    })(CPM.pages.tools.site, CPM.pages, CPM.core);
})();
