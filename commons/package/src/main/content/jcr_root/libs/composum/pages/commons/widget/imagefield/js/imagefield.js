(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.widgets = window.composum.pages.widgets || {};

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            imagefield: {
                css: {
                    base: 'imagefield-widget'
                }
            }
        });

        widgets.ImageFieldWidget = widgets.PathFieldWidget.extend({

            initialize: function (options) {
                widgets.PathFieldWidget.prototype.initialize.apply(this, [options]);
            }
        });

        window.widgets.register('.widget.' + widgets.const.imagefield.css.base, widgets.ImageFieldWidget);


    })(window.composum.pages.widgets, window.composum.pages, window.core);
})(window);
