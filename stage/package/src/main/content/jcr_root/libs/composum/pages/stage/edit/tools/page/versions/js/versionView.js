(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};

    (function (pages, core) {
        'use strict';

        pages.const = _.extend(pages.const || {}, {
            versionView: {
                css: {
                    base: 'composum-pages-stage-version-frame',
                    _: {
                        wrapper: '_wrapper',
                        main: '_main',
                        secondary: '_secondary'
                    }
                },
                event: {
                    scroll: 'scroll.versionView'
                }
            }
        });

        pages.VersionFrame = pages.PageView.extend({

            initialize: function (options) {
                pages.PageView.prototype.initialize.apply(this, [options]);
                this.size = {width: 0, height: 0};
                this.$frame.load(_.bind(this.onLoad, this));
            },

            getCssBase: function () {
                return pages.const.versionView.css.base;
            },

            reset: function () {
                this.$el.addClass('hidden');
                this.$frame.attr('src', '');
            },

            view: function (path, scope) {
                if (path && scope) {
                    if (scope.release) {
                        this.$frame.attr('src', core.getContextUrl(path + ".html?cpm.release=" + scope.release));
                        this.$el.removeClass('hidden');
                    } else if (scope.version) {
                        this.$frame.attr('src', core.getContextUrl(path + ".html?cpm.version=" + scope.version));
                        this.$el.removeClass('hidden');
                    } else {
                        this.reset();
                    }
                } else {
                    this.reset();
                }
            },

            onLoad: function () {
                if (this.$frame.attr('src') !== '') {
                    this.$document = this.$frame.contents();
                    this.$body = this.$document.find('body');
                    this.size = {
                        width: this.$body.width(),
                        height: this.$body.height()
                    };
                    var e = pages.const.versionView.event;
                    this.$document.on(e.scroll, _.bind(pages.versionsView.scroll, pages.versionsView));
                } else {
                    delete this.$document;
                    delete this.$body;
                    this.size = {width: 0, height: 0};
                }
                pages.versionsView.onLoad();
            },

            error: function (hint, result) {
                core.alert('danger', 'Error', 'Error ' + hint, result);
            }
        });

        pages.MainVersion = pages.VersionFrame.extend({

            initialize: function (options) {
                pages.VersionFrame.prototype.initialize.apply(this, [options]);
            },

            setOpacity: function (value) {
                if (value < 0.0) {
                    value = 0.0;
                }
                if (value > 0.01 && value > 1.0) {
                    value = value / 100.0;
                }
                this.$frame.css('opacity', value);
            }
        });

        pages.SecondaryVersion = pages.VersionFrame.extend({

            initialize: function (options) {
                pages.VersionFrame.prototype.initialize.apply(this, [options]);
            }
        });

        pages.VersionsView = Backbone.View.extend({

            initialize: function (options) {
                var c = pages.const.versionView.css;
                this.mainView = core.getWidget(this.el, '.' + c.base + c._.main, pages.MainVersion);
                this.sdryView = core.getWidget(this.el, '.' + c.base + c._.secondary, pages.SecondaryVersion);
            },

            reset: function () {
                this.sdryView.reset();
                this.mainView.reset();
                this.hide();
            },

            show: function () {
                $('body').addClass(pages.const.versionViewCssClass);
                pages.surface.surface.bodySync();
            },

            hide: function () {
                $('body').removeClass(pages.const.versionViewCssClass);
                pages.surface.surface.bodySync();
            },

            showVersions: function (path, mainScope, sdryScope) {
                this.sdryView.view(path, sdryScope);
                this.mainView.view(path, mainScope);
                this.show();
            },

            /**
             * sync scroll position of secondary view to the main view (which the events target)
             */
            scroll: function () {
                if (this.sdryView.$document && this.mainView.$document) {
                    this.sdryView.$document.scrollTop(this.mainView.$document.scrollTop());
                    this.sdryView.$document.scrollLeft(this.mainView.$document.scrollLeft());
                }
            },

            /**
             * sync both views width and height for synchronous scroll up to the end
             */
            onLoad: function () {
                var width = Math.max(this.mainView.size.width, this.sdryView.size.width);
                var height = Math.max(this.mainView.size.height, this.sdryView.size.height);
                if (this.mainView.$body) {
                    this.mainView.$body.css('width', width + 'px');
                    this.mainView.$body.css('height', height + 'px');
                }
                if (this.sdryView.$body) {
                    this.sdryView.$body.css('width', width + 'px');
                    this.sdryView.$body.css('height', height + 'px');
                }
                this.scroll();
            }
        });

        pages.versionsView = core.getView('.' + pages.const.versionView.css.base
            + pages.const.versionView.css._.wrapper, pages.VersionsView);

    })(window.composum.pages, window.core);
})(window);
