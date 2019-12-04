(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.components = window.composum.pages.components || {};

    (function (components, pages, core) {
        'use strict';

        components.time = components.time || {};
        components.time.const = _.extend(components.const.time || {}, {
            calendar: {
                css: {
                    base: 'composum-pages-components-time-calendar',
                    _move: '_move',
                    _month: '_month-header',
                    _items: '_day.items'
                }
            },
            navigator: {
                css: {
                    base: 'composum-pages-components-time-navigator',
                    _move: '_move'
                }
            },
            event: {
                css: {
                    base: 'composum-pages-components-time-event'
                }
            },
            news: {
                css: {
                    base: 'composum-pages-components-time-news'
                }
            }
        });

        components.time.Navigator = Backbone.View.extend({

            initialize: function (options) {
                this.initContent();
            },

            initContent: function () {
                var c = components.time.const.navigator.css;
                this.$move = this.$('.' + c.base + c._move);
                this.$move.click(_.bind(this.move, this));
            },

            getLocale: function () {
                return this.$el.data('locale');
            },

            getLoadUrl: function (range) {
                var $target = $(event.currentTarget);
                var url = core.encodePath(this.$el.data('path')) + '.reload.html';
                if (range) {
                    url += '?range=' + encodeURIComponent(range);
                }
                return url;
            },

            showRange: function (range) {
                if (range) {
                    var url = pages.url.withLocale(this.getLoadUrl(range), this.getLocale());
                    core.getHtml(url, _.bind(function (content) {
                        this.$el.html(content);
                        this.initContent();
                    }, this));
                }
            },

            move: function (event) {
                var $target = $(event.currentTarget);
                this.showRange($target.data('range'));
            }
        });

        components.time.Calendar = components.time.Navigator.extend({

            initContent: function () {
                var c = components.time.const.calendar.css;
                this.$move = this.$('.' + c.base + c._move);
                this.$months = this.$('.' + c.base + c._month);
                this.$items = this.$('.' + c.base + c._items);
                this.$move.click(_.bind(this.move, this));
                this.$months.click(_.bind(this.showDetail, this));
                this.$items.click(_.bind(this.showDetail, this));
            },

            showDetail: function (event) {
                var $target = $(event.currentTarget);
                var range = $target.data('range');
                var detailPage = this.$el.data('detail');
                if (detailPage) {
                    var url = new core.SlingUrl(detailPage);
                    url.parameters['range'] = range;
                    window.location.href = url.build();
                } else {
                    var $navigator = $('.' + components.time.const.navigator.css.base);
                    if ($navigator.length > 0) {
                        $navigator.each(function () {
                            if (this.view && _.isFunction(this.view.showRange)) {
                                this.view.showRange(range);
                            }
                        });
                    }
                }
            }
        });

        components.time.EventNavigator = components.time.Navigator.extend({

            initialize: function (options) {
                components.time.Navigator.prototype.initialize.apply(this, [options]);
                this.$calendar = $('.' + components.time.const.calendar.css.base);
            }
        });

        components.time.NewsNavigator = components.time.Navigator.extend({

            initialize: function (options) {
                components.time.Navigator.prototype.initialize.apply(this, [options]);
                this.$calendar = $('.' + components.time.const.calendar.css.base);
            }
        });

        $(document).ready(function () {
            $('.' + components.time.const.calendar.css.base).each(function () {
                core.getView(this, components.time.Calendar);
            });
            $('.' + components.time.const.event.css.base).each(function () {
                core.getView(this, components.time.EventNavigator);
            });
            $('.' + components.time.const.news.css.base).each(function () {
                core.getView(this, components.time.NewsNavigator);
            });
        });

    })(window.composum.pages.components, window.composum.pages, window.core);
})(window);
