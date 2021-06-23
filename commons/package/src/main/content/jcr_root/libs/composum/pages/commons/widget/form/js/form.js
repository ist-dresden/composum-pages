(function () {
    'use strict';
    CPM.namespace('pages.widgets');

    (function (widgets, pages, core) {

        widgets.const = _.extend(widgets.const || {}, {
            form: {
                css: {
                    base: 'composum-pages-widgets-form',
                    _form: '_form'
                }
            }
        });

        widgets.WidgetForm = core.components.FormDialog.extend({

            initialize: function (options) {
                var formType = options ? (options.formType || core.components.FormWidget) : core.components.FormWidget;
                this.form = core.getWidget(this.el, "form", formType);
                core.components.Dialog.prototype.initialize.call(this, options);
                this.validationHints = [];
                this.initView();
                this.initSubmit();
            },

            /**
             * generic (method driven) submit
             */
            doSubmit: function (onSuccess) {
                switch (this.form.$el.attr('method')) {
                    case 'GET':
                        this.loadFormGet();
                        break;
                    default:
                    case 'POST':
                        this.submitForm(onSuccesse);
                        break;
                    case 'PUT':
                        this.submitFormPut(onSuccess);
                        break;
                }
            },

            loadFormGet: function () {
                var url = new core.SlingUrl(this.form.$el.attr("action"));
                var formData = new FormData(this.form.el);
                formData.forEach(function (value, key) {
                    if (value) {
                        var param = url.parameters[key];
                        if (param) {
                            if (!_.isArray(param)) {
                                url.parameters[key] = param = [param];
                            }
                            param.push(value);
                        } else {
                            url.parameters[key] = value;
                        }
                    }
                });
                window.location.href = url.build();
            }
        });

        CPM.widgets.register('.' + widgets.const.form.css.base, widgets.WidgetForm);

    })(CPM.pages.widgets, CPM.pages, CPM.core);
})();
