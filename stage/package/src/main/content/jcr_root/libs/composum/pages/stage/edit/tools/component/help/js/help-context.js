(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            help: {
                css: {
                    tools: 'composum-pages-tools',
                    _help: '_help-context',
                    page: 'composum-pages-stage-edit-tools-component-help-page',
                    _link: '_link',
                    _path: '_path',
                    _template: '_template',
                    _design: '_design'
                }
            }
        });

        tools.HelpView = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.help.css;
                this.$path = this.$('.' + c.page + c._path);
                this.$template = this.$('.' + c.page + c._template);
                this.$design = this.$('.' + c.page + c._design);
                this.$path.click(_.bind(this.openDevPath, this));
                this.$template.click(_.bind(this.openDevPath, this));
                this.$design.click(_.bind(this.openDevPath, this));
            },

            openDevPath: function (event) {
                var $link = $(event.currentTarget);
                var path = $link.data('path');
                if (path) {
                    var e = pages.const.event;
                    pages.trigger('context.help.open', e.pages.open, ['developmentTree', path]);
                }
            }
        });

        /**
         * register this tool as a pages context tool for initialization after load of the context tools set
         */
        pages.contextTools.addTool(function (contextTabs) {
            var c = tools.const.help.css;
            var tool = core.getWidget(contextTabs.el, '.' + c.tools + c._help, tools.HelpView);
            if (tool) {
                tool.contextTabs = contextTabs;
            }
            return tool;
        });

    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
