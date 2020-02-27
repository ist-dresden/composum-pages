(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.site = window.composum.pages.site || {};

    (function (site, pages, core) {
        'use strict';

        site.const = _.extend(site.const || {}, {
            tabs: {
                profile: {
                    aspect: 'page',
                    key: 'tab'
                },
                css: {
                    base: "composum-pages-site-view"
                }
            },
            sites: {
                css: {
                    base: "composum-pages-stage-sites",
                    _create: "_create",
                    _site: "_site",
                    _templates: "_templates",
                    _tile: "_tile"
                }
            }
        });

        site.profile = new core.LocalProfile('composum.pages.site');

        site.PageTabs = Backbone.View.extend({

            initialize: function (options) {
                var p = site.const.tabs.profile;
                this.$tabs = this.$('.nav-tabs a');
                var initialTab = site.profile.get(p.aspect, p.key, '#current-tab');
                if (initialTab) {
                    this.$('.nav-tabs a[href="' + initialTab + '"]').tab('show');
                }
                this.$tabs.on('shown.bs.tab', _.bind(function (event) {
                    site.profile.set(p.aspect, p.key, $(event.target).attr('href'));
                    site.profile.save(p.aspect);
                }, this));
                window.addEventListener("message", _.bind(this.onMessage, this), false);
            },

            onMessage: function (event) {
                var e = pages.elements.const.event;
                var message = e.messagePattern.exec(event.data);
                if (message) {
                    var args = JSON.parse(message[2]); // argument object|array
                    switch (message[1]) { // operation
                        case e.site.changed:
                        case e.content.state:
                            window.location.reload();
                            break;
                    }
                }
            }
        });

        site.pageTabs = core.getView('.' + site.const.tabs.css.base, site.PageTabs);

        site.Sites = Backbone.View.extend({

            initialize: function (options) {
                this.$createSite = this.$('.' + site.const.sites.css.base + site.const.sites.css._create);
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

        site.sites = core.getView('.' + site.const.sites.css.base, site.Sites);

    })(window.composum.pages.site, window.composum.pages, window.core);
})(window);
