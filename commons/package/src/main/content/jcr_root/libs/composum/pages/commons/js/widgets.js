(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.widgets = window.composum.pages.widgets || {};

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            profile: {
                aspect: 'widgets'
            }
        });

        widgets.profile = new core.LocalProfile('composum.pages.widgets');

    })(window.composum.pages.widgets, window.composum.pages, window.core);
})(window);
