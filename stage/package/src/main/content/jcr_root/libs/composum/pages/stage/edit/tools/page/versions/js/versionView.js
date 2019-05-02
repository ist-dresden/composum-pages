(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};

    (function (pages, core) {
        'use strict';

        pages.const = _.extend(pages.const || {}, {
            versionCssBase: 'composum-pages-stage-version-frame',
            _wrapper: '_wrapper',
            _main: '_main',
            _secondary: '_secondary'
        });

        pages.VersionFrame = pages.PageView.extend({

            initialize: function (options) {
                pages.PageView.prototype.initialize.apply(this, [options]);
            },

            getCssBase: function () {
                return pages.const.versionCssBase;
            },

            reset: function () {
                this.$el.addClass('hidden');
                this.$frame.attr('src', '');
            },

            view: function (path, version) {
                if (path && version && version) {
                    this.$frame.attr('src', core.getContextUrl(path + ".html?cpm.version=" + version));
                    this.$el.removeClass('hidden');
                } else {
                    this.reset();
                }
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
                if (value > 1.0) {
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
                this.mainView = core.getWidget(this.el, '.' + pages.const.versionCssBase
                    + pages.const._main, pages.MainVersion);
                this.sdryView = core.getWidget(this.el, '.' + pages.const.versionCssBase
                    + pages.const._secondary, pages.SecondaryVersion);
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

            showVersions: function (path, mainVersion, secondaryVersion) {
                this.sdryView.view(path, secondaryVersion);
                this.mainView.view(path, mainVersion);
                this.show();
            }
        });

        pages.versionsView = core.getView('.' + pages.const.versionCssBase
            + pages.const._wrapper, pages.VersionsView);

    })(window.composum.pages, window.core);
})(window);
