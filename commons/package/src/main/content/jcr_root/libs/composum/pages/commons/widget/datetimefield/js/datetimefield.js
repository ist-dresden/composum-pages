(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.widgets = window.composum.pages.widgets || {};

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            datetime: {
                css: {
                    base: 'composum-pages-edit-widget',
                    _widget: '_datetimefield',
                    _submit: '_submit',
                    widget: 'datetimefield-widget'
                }
            }
        });

        widgets.DateTimeFieldWidget = core.components.DateTimeWidget.extend({

            /**
             * retrieves the hidden 'submit' input field 'lazy' (triggered during 'core.Widget.initialize()'
             */
            hiddenSubmit: function () {
                if (!this.$submit) {
                    var c = widgets.const.datetime.css;
                    this.$submit = this.$el.closest('.' + c.base + c._widget).find('.' + c.base + c._submit);
                }
                return this.$submit;
            },

            /**
             * applied during 'initialize' (!)
             */
            retrieveName: function () {
                return this.hiddenSubmit().attr(window.widgets.const.attr.name);
            },

            /**
             * prepare date/time value for submit to the POST servlet
             */
            finalize: function () {
                var date = this.datetimepicker.date();
                var value = date ? date.toISOString(true) : null;
                this.hiddenSubmit().attr('value', value);
            }
        });

        window.widgets.register('.widget.' + widgets.const.datetime.css.widget, widgets.DateTimeFieldWidget);


    })(window.composum.pages.widgets, window.composum.pages, window.core);
})(window);
