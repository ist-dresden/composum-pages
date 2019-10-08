(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.components = window.composum.pages.components || {};

    (function (components, pages, core) {
        'use strict';

        components.const = _.extend(components.const || {}, {
            iframe: {
                css: {
                    base: '.composum-pages-components-element-iframe'
                },
                calc: {
                    'body': 'bodyOffset',
                    'elements': 'lowestElement'
                }
            }
        });

        components.IFrame = Backbone.View.extend({

            initialize: function (options) {
                var c = components.const.iframe.css;
                this.$frame = this.$(c.base + "_frame");
                this.$expand = this.$(c.base + "_expand");
                this.$collapse = this.$(c.base + "_collapse");
                iFrameResize({
                    scrolling: true,
                    heightCalculationMethod: components.const.iframe.calc[this.$frame.data('mode')]
                }, this.$frame[0]);
                var height = this.$frame.data('height');
                if (height) {
                    if (!/.+(px|%)$/.exec(height)) {
                        height += 'px';
                    }
                    this.$frame.css('max-height', height);
                }
                this.$expand.click(_.bind(this.expand, this));
                this.$collapse.click(_.bind(this.collapse, this));
            },

            expand: function () {
                this.$el.removeClass("collapsed").addClass("expanded");
            },

            collapse: function () {
                this.$el.removeClass("expanded").addClass("collapsed");
            }
        });

        $(document).ready(function () {
            $(components.const.iframe.css.base).each(function () {
                core.getView(this, components.IFrame);
            });
        });

    })(window.composum.pages.components, window.composum.pages, window.core);
})
(window);
