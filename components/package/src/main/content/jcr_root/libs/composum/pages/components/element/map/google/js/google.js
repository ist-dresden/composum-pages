(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.components = window.composum.pages.components || {};

    (function (components, pages, core) {
        'use strict';

        components.const = _.extend(components.const || {}, {
            google: {
                map: {
                    css: {
                        base: 'composum-pages-components-element-map-google'
                    }
                }
            }
        });

        components.GoogleMap = Backbone.View.extend({

            initialize: function (options) {
            }
        });

        $(document).ready(function () {
            $('.' + components.const.google.map.css.base).each(function () {
                core.getView(this, components.GoogleMap);
            });
        });

    })(window.composum.pages.components, window.composum.pages, window.core);
})(window);
