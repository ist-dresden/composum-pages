(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.widgets = window.composum.pages.widgets || {};

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            page: {
                css: {
                    tile: "composum-pages-stage-page_tile",
                    page: "composum-pages-edit-widget_page"
                }
            }
        });

        widgets.PageTemplateWidget = core.components.RadioGroupWidget.extend({

            initialize: function (options) {
                core.components.RadioGroupWidget.prototype.initialize.apply(this, [options]);
                this.$('a.' + widgets.const.page.css.tile).click(_.bind(this.selectTemplate, this));
            },

            selectTemplate: function (event) {
                event.preventDefault();
                var $link = $(event.currentTarget);
                var $radio = $link.closest('.' + widgets.const.page.css.page).find('input[type="radio"]');
                this.setValue($radio.val());
                return false;
            }
        });

        window.widgets.register('.widget.page-templates-widget', widgets.PageTemplateWidget);

        widgets.PageReferencesWidget = core.components.TableSelectWidget.extend({});

        window.widgets.register('.widget.page-references-widget', widgets.PageReferencesWidget);

        widgets.PageReferrersWidget = core.components.TableSelectWidget.extend({});

        window.widgets.register('.widget.page-referrers-widget', widgets.PageReferrersWidget);

    })(window.composum.pages.widgets, window.composum.pages, window.core);
})(window);
