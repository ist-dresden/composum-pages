(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            finished: {
                page: {
                    base: 'composum-pages-stage-edit-site-page-finished',
                    _listentry: '_listentry',
                    _entry: '_page-entry'
                },
                tools: {
                    base: 'composum-pages-stage-edit-tools-site-finished',
                    _panel: '_tools-panel'
                },
                uri: {
                    load: '/libs/composum/pages/stage/edit/tools/site/finished.content.html'
                }
            }
        });

        tools.FinishedPages = pages.releases.FinishedPages.extend({

            initialize: function (options) {
                this.initContent();
                var c = pages.const.event;
                $(document).on(c.site.changed + '.FinishedPages', _.bind(this.reload, this));
            },

            initContent: function (options) {
                var c = tools.const.finished.page;
                pages.releases.FinishedPages.prototype.initialize.apply(this);
                this.sitePath = this.$('.' + c.base).data('path');
                this.$previewEntry = [];
                this.$('.' + c.base + c._entry).click(_.bind(this.pagePreview, this));
            },

            pagePreview: function (event) {
                var c = tools.const.finished.page;
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
                    $listEntry.addClass('selected');
                    pages.log.debug('site.trigger.' + pages.const.event.page.view + '(' + path + ',preview)');
                    $(document).trigger(pages.const.event.page.view, [path, {'pages.view': 'preview'}]);
                }
            },

            closePreview: function () {
                if (this.$previewEntry.length > 0) {
                    this.$previewEntry.removeClass('selected');
                    this.$previewEntry = [];
                    pages.log.debug('site.trigger.' + pages.const.event.page.view + '()');
                    $(document).trigger(pages.const.event.page.view, [null, {}]);
                }
            },

            beforeClose: function () {
                this.beforeHideTab();
            },

            beforeHideTab: function () {
                this.closePreview();
            },

            onTabSelected: function () {
                this.reload();
            },

            reload: function () {
                var c = tools.const.finished.uri;
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
            var c = tools.const.finished;
            var panel = core.getWidget(contextTabs.el, '.' + c.tools.base, tools.FinishedPages);
            if (panel) {
                panel.contextTabs = contextTabs;
            }
            return panel;
        });

    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
