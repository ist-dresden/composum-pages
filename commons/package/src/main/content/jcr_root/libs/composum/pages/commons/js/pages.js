(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};

    (function (pages, core) {
        'use strict';

        pages.url = pages.url || {};

        (function (url) {

            url.withLocale = function (href, locale) {
                if (!locale) {
                    locale = $('html').data('locale');
                }
                if (locale) {
                    var url = new core.SlingUrl(href);
                    if (!url.parameters['pages.locale']) {
                        url.parameters['pages.locale'] = locale;
                        href = url.build();
                    }
                }
                return href;
            };

        })(pages.url);

    })(window.composum.pages, window.core);
})(window);
