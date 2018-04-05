(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.widgets = window.composum.pages.widgets || {};

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            checkselect: {
                cssBase: 'checkselect-widget'
            }
        });

        widgets.CheckSelectWidget = core.components.CheckboxWidget.extend({

            initialize: function (options) {
                core.components.CheckboxWidget.prototype.initialize.apply(this, [options]);
                this.options = this.$el.data('options');
                this.$second = this.$('.' + widgets.const.checkselect.cssBase + '_second');
                this.$input.change(_.bind(this.adjustForm, this));
            },

            retrieveName: function () {
                // using an additional data 'name' because the 'name' attribute is moved between the options
                return this.$el.data('name');
            },

            /**
             * returns the current value from the input field
             */
            getValue: function () {
                return this.$input.prop('checked') ? this.options[0].value
                    : this.options.length > 1 ? this.options[1] : undefined;
            },

            /**
             * defines the (initial) value of the input field
             */
            setValue: function (value) {
                this.$input.prop('checked', value === this.options[0]);
            },

            /**
             * adjust the widget state (form element names) to the checkbox state
             */
            adjustForm: function (event) {
                if (this.$input.prop('checked')) {
                    this.$second.removeAttr('name');
                    this.$input.attr('name', this.name);
                } else {
                    this.$input.removeAttr('name');
                    this.$second.attr('name', this.name);
                }
            }
        });

        window.widgets.register('.widget.' + widgets.const.checkselect.cssBase, widgets.CheckSelectWidget);


    })(window.composum.pages.widgets, window.composum.pages, window.core);
})(window);
