(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.components = window.composum.pages.components || {};

    (function (components, pages, core) {
        'use strict';

        components.const = _.extend(components.const || {}, {
            codeblock: {
                cssBase: '.composum-pages-components-element-codeblock'
            }
        });

        components.CodeBlock = Backbone.View.extend({

            initialize: function (options) {
                this.$code = this.$(components.const.codeblock.cssBase + "_content");
                this.$expand = this.$(components.const.codeblock.cssBase + "_expand");
                this.$collapse = this.$(components.const.codeblock.cssBase + "_collapse");
                if (this.$code.length > 0) {
                    this.highlight = hljs.highlightBlock(this.$code[0]);
                }
                this.$expand.click(_.bind(this.expand, this));
                this.$collapse.click(_.bind(this.collapse, this));
            },

            expand: function () {
                this.$el.removeClass("collapsed");
                this.$el.addClass("expanded");
            },

            collapse: function () {
                this.$el.removeClass("expanded");
                this.$el.addClass("collapsed");
            }
        });

        $(document).ready(function () {
            $(components.const.codeblock.cssBase).each(function () {
                core.getView(this, components.CodeBlock);
            });
        });

    })(window.composum.pages.components, window.composum.pages, window.core);
})(window);
