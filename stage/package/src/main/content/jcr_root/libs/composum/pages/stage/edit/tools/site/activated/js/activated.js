(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            activated: {
                event: {
                    id: 'tools.ActivatedPages'
                },
                page: {
                    base: 'composum-pages-stage-edit-site-page-activated',
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

        tools.ActivatedPages = pages.releases.ReleaseChanges.extend({

            initialize: function (options) {
                this.initContent();
                var id = tools.const.activated.event.id;
                var e = pages.const.event;
                $(document)
                    .on(e.page.state + id, _.bind(this.reload, this))
                    .on(e.site.changed + id, _.bind(this.reload, this));
            },

            beforeClose: function () {
                this.beforeHideTab();
                var e = pages.const.event;
                var id = tools.const.activated.event.id;
                $(document)
                    .off(e.page.state + id)
                    .off(e.site.changed + id);
            },

            initContent: function (options) {
                var c = tools.const.activated.page;
                pages.releases.ReleaseChanges.prototype.initialize.apply(this);
                this.sitePath = this.$('.' + c.base).data('path');
                this.$previewEntry = [];
                this.$('.' + c.base + c._entry).click(_.bind(this.pagePreview, this));
            },

            pagePreview: function (event) {
                var c = tools.const.activated.page;
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
                    pages.trigger('site.activated.view', pages.const.event.page.view, [path, {'pages.view': 'preview'}]);
                }
            },

            closePreview: function () {
                if (this.$previewEntry.length > 0) {
                    $('body').removeClass('context-driven-view');
                    this.$previewEntry.removeClass('selected');
                    this.$previewEntry = [];
                    pages.trigger('site.activated.close', pages.const.event.page.view, [null, {}]);
                }
            },

            beforeHideTab: function () {
                this.closePreview();
            },

            onTabSelected: function () {
                this.reload();
            },

            reload: function () {
                this.closePreview();
                var c = tools.const.activated.uri;
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
            var c = tools.const.activated;
            var panel = core.getWidget(contextTabs.el, '.' + c.tools.base, tools.ActivatedPages);
            if (panel) {
                panel.contextTabs = contextTabs;
            }
            return panel;
        });

    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
