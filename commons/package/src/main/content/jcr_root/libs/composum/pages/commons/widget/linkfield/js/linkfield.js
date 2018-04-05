(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.widgets = window.composum.pages.widgets || {};

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            link: {
                cssBase: 'linkfield-widget'
            }
        });

        widgets.LinkFieldWidget = core.components.PathWidget.extend({});

        window.widgets.register('.widget.linkfield-widget', widgets.LinkFieldWidget);


    })(window.composum.pages.widgets, window.composum.pages, window.core);
})(window);
