(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            modified: {
                page: {
                    base: 'composum-pages-stage-edit-site-page-modified',
                    _listentry: '_listentry',
                    _entry: '_page-entry'
                },
                tools: {
                    base: 'composum-pages-stage-edit-tools-site-modified',
                    _panel: '_tools-panel'
                },
                uri: {
                    load: '/libs/composum/pages/stage/edit/tools/site/modified.content.html'
                }
            }
        });

        tools.ModifiedPages = pages.releases.ModifiedPages.extend({

            initialize: function (options) {
                this.initContent();
                $(document).on('site:changed.ModifiedPages', _.bind(this.reload, this));
            },

            initContent: function (options) {
                var c = tools.const.modified.page;
                pages.releases.ModifiedPages.prototype.initialize.apply(this);
                this.sitePath = this.$('.' + c.base).data('path');
                this.$previewEntry = [];
                this.$('.' + c.base + c._entry).click(_.bind(this.pagePreview, this));
            },

            pagePreview: function (event) {
                var c = tools.const.modified.page;
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
                    console.log('site.trigger.page:view(' + path + ',preview)');
                    $(document).trigger("page:view", [path, {'pages.mode': 'preview'}]);
                }
            },

            closePreview: function () {
                if (this.$previewEntry.length > 0) {
                    $('body').removeClass('context-driven-view');
                    this.$previewEntry.removeClass('selected');
                    this.$previewEntry = [];
                    console.log('site.trigger.page:view()');
                    $(document).trigger("page:view", [null, {}]);
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
                var c = tools.const.modified.uri;
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
            var c = tools.const.modified;
            var panel = core.getWidget(contextTabs.el, '.' + c.tools.base, tools.ModifiedPages);
            if (panel) {
                panel.contextTabs = contextTabs;
            }
            return panel;
        });

    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
