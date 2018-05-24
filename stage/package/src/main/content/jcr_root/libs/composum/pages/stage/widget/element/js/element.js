(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.widgets = window.composum.pages.widgets || {};

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            element: {
                type: {
                    css: {
                        base: 'composum-pages-edit-widget',
                        _widget: '_element-type-select',
                        tile: 'composum-pages-component-tile',
                        _type: '_element-type'
                    }
                }
            }
        });

        widgets.ElementTypeSelectWidget = core.components.RadioGroupWidget.extend({

            initialize: function (options) {
                var c = widgets.const.element.type.css;
                core.components.RadioGroupWidget.prototype.initialize.apply(this, [options]);
                this.$('.' + c.tile).click(_.bind(this.selectType, this));
            },

            selectType: function (event) {
                event.preventDefault();
                var c = widgets.const.element.type.css;
                var $link = $(event.currentTarget);
                var $radio = $link.closest('.' + c.base + c._type).find('input[type="radio"]');
                this.setValue($radio.val());
                return false;
            }
        });

        window.widgets.register('.widget.element-type-select-widget', widgets.ElementTypeSelectWidget);

    })(window.composum.pages.widgets, window.composum.pages, window.core);
})(window);
