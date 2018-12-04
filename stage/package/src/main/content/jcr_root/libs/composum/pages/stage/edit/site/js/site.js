(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.site = window.composum.pages.site || {};

    (function (site, pages, core) {
        'use strict';

        site.const = _.extend(site.const || {}, {
            css: {
                base: "composum-pages-stage-sites",
                _create: "_create",
                _site: "_site",
                _templates: "_templates",
                _tile: "_tile"
            }
        });

        site.Sites = Backbone.View.extend({

            initialize: function (options) {
                this.$createSite = this.$('.' + site.const.css.base + site.const.css._create);
                this.$createSite.click(_.bind(this.createSite, this));
                $(document).on('site:created.Home', _.bind(function (event, pathOrRef) {
                    var path = pathOrRef.path ? pathOrRef.path : pathOrRef;
                    window.location.href = core.getContextUrl('/bin/pages.html' + path);
                }, this));
            },

            createSite: function (event) {
                event.preventDefault();
                pages.actions.site.create(event);
                return false;
            }
        });

        site.sites = core.getView('.' + site.const.css.base, site.Sites);

    })(window.composum.pages.site, window.composum.pages, window.core);
})(window);
