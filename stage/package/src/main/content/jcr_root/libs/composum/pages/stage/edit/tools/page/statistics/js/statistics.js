(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            statistics: {
                cssBase: 'composum-pages-stage-edit-tools-page-statistics',
                _content: '_content',
                uri: {
                    base: '/libs/composum/pages/stage/edit/tools/page/statistics',
                    _content: '.content.html',
                    _data: '/_jcr_content.statistics'
                }
            }
        });

        tools.Statistics = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.statistics;
                this.$statisticsContent = this.$('.' + c.cssBase + c._content);
            },

            onTabSelected: function () {
                this.reload();
            },

            reload: function () {
                var c = tools.const.statistics;
                core.ajaxGet(c.uri.base + c.uri._content + this.contextTabs.data.path, {},
                    _.bind(function (content) {
                        this.$statisticsContent.html(content);
                        this.loadData();
                    }, this), _.bind(function () {
                        this.$statisticsContent.html("");
                    }, this));
            },

            loadData: function () {
                var c = tools.const.statistics;
                this.$chartist = this.$statisticsContent.find('.' + c.cssBase + '_chart .' + c.cssBase + '_chartist');
                this.$refTable = this.$statisticsContent.find('.' + c.cssBase + '_referrers table tbody');
                if (this.$chartist.length === 1 && this.$refTable.length === 1) {
                    this.$chartist.html('');
                    this.$refTable.html('');
                    core.ajaxGet(this.contextTabs.data.path + c.uri._data + '.json', {},
                        _.bind(function (data) {
                            this.displayChart(data);
                            this.displayRefTable(data);
                        }, this));
                }
            },

            displayChart: function (data) {
                var labels = [];
                var series = [[], []];
                for (var i = 0; i < data.entries.length; i++) {
                    labels.push(data.entries[i].label);
                    series[0].push(data.entries[i].summary.unique);
                    series[1].push(data.entries[i].summary.total);
                }
                this.chart = new Chartist.Line(this.$chartist[0], {
                    labels: labels,
                    series: series
                }, {
                    fullWidth: true
                });
            },

            displayRefTable: function (data) {
                var labels = [];
                var series = [[], []];
                for (var i = 0; i < data.referrers.length; i++) {
                    this.$refTable.append('<tr><td class="url" title="' + data.referrers[i].url
                        + '"><a href="' + data.referrers[i].url
                        + '">' + data.referrers[i].url.replace(/^(https?:\/\/[^\/]+\/).+/, "$1...")
                        + '</a></td><td class="unique">' + data.referrers[i].summary.unique
                        + '</td><td class="total">' + data.referrers[i].summary.total + '</td></tr>')
                }
            }
        });

        /**
         * register this tool as a pages context tool for initialization after load of the context tools set
         */
        pages.contextTools.addTool(function (contextTabs) {
            var tool = core.getWidget(contextTabs.el, '.' + tools.const.statistics.cssBase, tools.Statistics);
            if (tool) {
                tool.contextTabs = contextTabs;
            }
            return tool;
        });

    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
