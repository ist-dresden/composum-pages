(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.widgets = window.composum.pages.widgets || {};

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            linkfield: {
                cssBase: 'linkfield-widget',
                profile: 'linkfield'
            }
        });

        widgets.LinkFieldWidget = widgets.PathFieldWidget.extend({

            profileAspect: function () {
                return widgets.const.linkfield.profile;
            }
        });

        window.widgets.register('.widget.' + widgets.const.linkfield.cssBase, widgets.LinkFieldWidget);

    })(window.composum.pages.widgets, window.composum.pages, window.core);
})(window);
