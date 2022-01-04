(function () {
    'use strict';
    CPM.namespace('pages.widgets');

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

            initialize: function (options) {
                core.components.DateTimeWidget.prototype.initialize.apply(this, [options]);
                this.data.fieldtype = this.$el.data('fieldtype');
            },

            /**
             * retrieves the hidden 'submit' input field 'lazy' (triggered during 'core.Widget.initialize()')
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
                return this.hiddenSubmit().attr(CPM.widgets.const.attr.name);
            },

            /**
             * prepare date/time value for validation and for submit to the POST servlet
             */
            prepare: function () {
                var date = this.datetimepicker.date();
                var value = null;
                if (date) {
                    switch (this.data.fieldtype) {
                        default:
                            value = date.toISOString(true);
                            break;
                        case 'date':
                            value = date.format(this.$el.data('format') || 'YYYY-MM-DD');
                            break;
                    }
                }
                this.hiddenSubmit().attr('value', value);
            }
        });

        CPM.widgets.register('.widget.' + widgets.const.datetime.css.widget, widgets.DateTimeFieldWidget);

    })(CPM.pages.widgets, CPM.pages, CPM.core);
})();