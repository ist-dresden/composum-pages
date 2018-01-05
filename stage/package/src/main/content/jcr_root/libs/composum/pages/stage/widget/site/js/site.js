(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.widgets = window.composum.pages.widgets || {};

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            site: {
                css: {
                    tile: "composum-pages-stage-site_tile",
                    site: "composum-pages-edit-widget_site"
                }
            }
        });

        widgets.SiteTemplateWidget = core.components.RadioGroupWidget.extend({

            initialize: function (options) {
                core.components.RadioGroupWidget.prototype.initialize.apply(this, [options]);
                this.$('a.' + widgets.const.site.css.tile).click(_.bind(this.selectTemplate, this));
            },

            selectTemplate: function (event) {
                event.preventDefault();
                var $link = $(event.currentTarget);
                var $radio = $link.closest('.' + widgets.const.site.css.site).find('input[type="radio"]');
                this.setValue($radio.val());
                return false;
            }
        });

        window.widgets.register('.widget.site-templates-widget', widgets.SiteTemplateWidget);

    })(window.composum.pages.widgets, window.composum.pages, window.core);
})(window);
