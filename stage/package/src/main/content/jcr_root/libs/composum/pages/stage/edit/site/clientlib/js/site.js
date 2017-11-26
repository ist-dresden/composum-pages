(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.site = window.composum.pages.site || {};

    (function (site, pages, core) {
        'use strict';

        site.const = _.extend(site.const || {}, {
            css: {
                sites: "composum-pages-stage-sites",
                create: "composum-pages-stage-sites_create"
            }
        });

        site.Sites = Backbone.View.extend({

            initialize: function (options) {
                this.$createSite = this.$('.' + site.const.css.create);
                this.$createSite.click(_.bind(this.createSite, this));
            },

            createSite: function (event) {
                event.preventDefault();
                pages.actions.site.create(event);
                return false;
            }
        });

        site.sites = core.getView('.' + site.const.css.sites, site.Sites);

    })(window.composum.pages.site, window.composum.pages, window.core);
})(window);
