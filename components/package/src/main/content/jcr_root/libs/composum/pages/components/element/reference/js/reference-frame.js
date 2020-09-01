(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.components = window.composum.pages.components || {};

    (function (components, pages, core) {
        'use strict';

        components.const = _.extend(components.const || {}, {
            reference: {
                css: {
                    base: 'composum-pages-components-element-reference',
                    _: {
                        preview: '_preview',
                        frame: '-frame'
                    },
                    dlg: {
                        base: 'composum-pages-stage-edit-dialog',
                        _: {
                            content: '_content'
                        }
                    },
                    wgt: {
                        base: 'composum-pages-edit-widget',
                        _: {
                            ref: '_contentReference'
                        },
                        field: '.widget.pathfield-widget'
                    }
                }
            }
        });

        components.ReferencePreview = Backbone.View.extend({

            initialize: function (options) {
                var c = components.const.reference.css;
                this.$frame = this.$('.' + c.base + c._.preview + c._.frame);
                this.widget = core.getView(this.$el.closest('.' + c.dlg.base + c.dlg._.content)
                    .find('.' + c.wgt.base + c.wgt._.ref + ' ' + c.wgt.field), core.components.PathWidget);
                this.widget.$input.change(_.bind(function () {
                    this.$frame.attr('src', core.getContextUrl(
                        this.$el.data('path') + '.preview.html' + this.widget.getValue()));
                }, this));
            }
        });

        window.widgets.register('.widget-addon.reference-preview-widget-addon', components.ReferencePreview);

    })(window.composum.pages.components, window.composum.pages, window.core);
})(window);
