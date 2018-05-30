(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            statistics: {
                cssBase: 'composum-pages-stage-edit-tools-page-statistics',
                _range: '_range',
                _reload: '_reload',
                _content: '_content',
                uri: {
                    base: '/libs/composum/pages/stage/edit/tools/page/statistics',
                    _content: '.content.html',
                    _data: '/_jcr_content.statistics'
                }
            }
        });

        tools.const.widget = _.extend(tools.const.widget || {}, {
            time: {
                range: {
                    css: {
                        base: 'time-range-select',
                        _prev: '_prev',
                        _next: '_next',
                        _current: '_current',
                        _type: '_type'
                    }
                }
            }
        });

        tools.TimeRangeSelector = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.widget.time.range;
                this.$prev = this.$('.' + c.css.base + c.css._prev);
                this.$next = this.$('.' + c.css.base + c.css._next);
                this.$current = this.$('.' + c.css.base + c.css._current);
                this.$type = this.$('.' + c.css.base + c.css._type);
                this.$prev.click(_.bind(this.selectPrev, this));
                this.$next.click(_.bind(this.selectNext, this));
                this.$current.click(_.bind(this.selectCurrent, this));
                this.$type.find('a').click(_.bind(this.selectType, this));
            },

            initRangeAndTarget: function (consumer) {
                this.consumer = consumer;
                this.loadProfileValue();
                this.rangeChanged();
            },

            loadProfileValue: function () {
                this.range = pages.profile.get('statistics', 'range', {date: moment(), type: 'months'})
                if (_.isString(this.range.date)) {
                    this.range.date = moment(this.range.date);
                }
            },

            saveProfileValue: function () {
                pages.profile.set('statistics', 'range', this.range);
            },

            drawSelectorLabels: function () {
                this.$type.find('.dropdown-toggle').text(this.getSelectorLabel(this.range.date, this.range.type));
                this.$type.find('a.days').text(this.getSelectorLabel(this.range.date, 'days'));
                this.$type.find('a.weeks').text(this.getSelectorLabel(this.range.date, 'weeks'));
                this.$type.find('a.months').text(this.getSelectorLabel(this.range.date, 'months'));
                this.$type.find('a.years').text(this.getSelectorLabel(this.range.date, 'years'));
            },

            getSelectorLabel: function (date, type) {
                switch (type) {
                    case 'days':
                        return 'D: ' + moment(date).format('YYYY-MM-DD');
                    case 'weeks':
                        return 'W: ' + date.format('YYYY, W (') + moment(date).weekday(0).format('DD')
                            + ' - ' + moment(date).weekday(6).format('DD/MM') + ')';
                    case 'months':
                    default:
                        return 'M: ' + date.format('YYYY, MM');
                    case 'years':
                        return 'Y: ' + date.format('YYYY');
                }
            },

            getRequestSelector: function (date, type) {
                switch (type) {
                    case 'days':
                        return moment(date).format('[y-]YYYY.[m-]MM.[d-]DD');
                    case 'weeks':
                        return moment(date).format('[y-]YYYY.[w-]WW');
                    case 'months':
                    default:
                        return moment(date).format('[y-]YYYY.[m-]MM');
                    case 'years':
                        return moment(date).format('[y-]YYYY[.m-]');
                }
            },

            rangeChanged: function () {
                this.saveProfileValue();
                this.drawSelectorLabels();
                if (this.consumer) {
                    this.consumer.rangeChanged(this.getRequestSelector(this.range.date, this.range.type));
                }
            },

            selectType: function (event) {
                this.range.type = $(event.currentTarget).attr('class');
                this.rangeChanged();
            },

            selectNext: function (event) {
                this.range.date = moment(this.range.date).add(1, this.range.type);
                this.rangeChanged();
            },

            selectPrev: function (event) {
                this.range.date = moment(this.range.date).subtract(1, this.range.type);
                this.rangeChanged();
            },

            selectCurrent: function (event) {
                this.range.date = moment();
                this.rangeChanged();
            }
        });

        tools.Statistics = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.statistics;
                this.$statisticsContent = this.$('.' + c.cssBase + c._content);
                this.rangeSelector = core.getWidget(this.el, '.' + c.cssBase + c._range, tools.TimeRangeSelector);
                this.$reload = this.$('.' + c.cssBase + c._reload);
                this.$reload.click(_.bind(this.reload, this));
            },

            onTabSelected: function () {
                this.reload();
            },

            reload: function () {
                var c = tools.const.statistics;
                core.ajaxGet(c.uri.base + c.uri._content + this.contextTabs.data.path, {},
                    _.bind(function (content) {
                        this.$statisticsContent.html(content);
                        this.rangeSelector.initRangeAndTarget(this);
                    }, this), _.bind(function () {
                        this.$statisticsContent.html("");
                        this.rangeSelector.initRangeAndTarget(undefined);
                    }, this));
            },

            rangeChanged: function (requestSelector) {
                var c = tools.const.statistics;
                this.$wrapper = this.$statisticsContent.find('.' + c.cssBase + '_chart .' + c.cssBase + '_canvas-wrapper');
                this.$canvas = this.$wrapper.find('.' + c.cssBase + '_canvas');
                this.$refTable = this.$statisticsContent.find('.' + c.cssBase + '_referrers table tbody');
                $(document).on('sidebarResized:contextTools.statistics', _.bind(this.adjustSize, this));
                if (this.$canvas.length === 1 && this.$refTable.length === 1) {
                    this.$canvas.html('');
                    this.$refTable.html('');
                    var uri = this.contextTabs.data.path + c.uri._data + '.' + requestSelector + '.json';
                    core.ajaxGet(uri, {},
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
                    series[1].push(data.entries[i].summary.unique);
                    series[0].push(data.entries[i].summary.total);
                }
                this.chart = new Chartist.Line(this.$canvas[0], {
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
                        + '">' + data.referrers[i].url
                            .replace(/^(https?:\/\/[^\/]+\/[^\/]+\/[^\/]+\/).+$/, "$1...")
                            .replace(/^([^?]+\?).*$/, "$1...")
                        + '</a></td><td class="unique">' + data.referrers[i].summary.unique
                        + '</td><td class="total">' + data.referrers[i].summary.total + '</td></tr>')
                }
            },

            adjustSize: function (event) {
                if (this.chart) {
                    this.chart.update();
                }
            },

            beforeClose: function () {
                this.beforeHideTab();
            },

            beforeHideTab: function () {
                if (this.$wrapper && this.$wrapper.length > 0) {
                    $(document).off('sidebarResized:contextTools.statistics');
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
