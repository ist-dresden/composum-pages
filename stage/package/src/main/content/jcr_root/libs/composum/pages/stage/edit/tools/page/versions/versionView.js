(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};

    (function (pages, core) {
        'use strict';

        pages.const = _.extend(pages.const || {}, {
            versionCssBase: 'composum-pages-stage-version-frame',
            _wrapper: '_wrapper',
            _main: '_main',
            _secondary: '_secondary',
            setVersionLabelUri: '/bin/cpm/pages/edit.setVersionLabel.json'
        });

        pages.VersionFrame = pages.PageView.extend({

            initialize: function (options) {
                pages.PageView.prototype.initialize.apply(this, [options]);
            },

            getCssBase: function () {
                return pages.const.versionCssBase;
            },

            getVersionLabel: function () {
                return undefined;
            },

            reset: function () {
                this.$frame.attr('src', '');
                this.$el.addClass('hidden');
            },

            view: function (path, version) {
                var label = this.getVersionLabel();
                if (path && version && label) {
                    core.ajaxPut(pages.const.setVersionLabelUri + path, JSON.stringify({
                            path: path + '/jcr:content',
                            version: version,
                            label: label
                        }), {}, _.bind(function (result) {
                            this.$frame.attr('src', core.getContextUrl(path + ".html?cpm.release=" + label));
                            this.$el.removeClass('hidden');
                        }, this), _.bind(function (result) {
                            this.error('on set display label', result);
                            this.reset();
                        }, this)
                    );
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

            getVersionLabel: function () {
                return 'composum-pages-show-version-primary';
            },

            setOpacity: function(value) {
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
            },

            getVersionLabel: function () {
                return 'composum-pages-show-version-secondary';
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
