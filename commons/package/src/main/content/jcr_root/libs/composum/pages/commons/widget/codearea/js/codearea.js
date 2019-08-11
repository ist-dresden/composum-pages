(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.widgets = window.composum.pages.widgets || {};

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            codearea: {
                cssBase: 'codearea-widget',
                editorSelector: '.code-editor',
                textareaSelector: '.codearea-value',
                textarea: '<textarea class="codearea-value" style="display:none"></textarea>',
                type: {
                    js: 'javascript',
                    txt: 'text'
                }
            }
        });

        widgets.CodeAreaWidget = window.widgets.Widget.extend({

            initialize: function (options) {
                this.$editor = this.codeArea();
                this.$editor.css('height', this.$el.data('height') || '150px');
                this.initEditor();
                this.$el.append(widgets.const.codearea.textarea);
                window.widgets.Widget.prototype.initialize.apply(this, [options]);
                this.$input.attr('name', this.name);
                this.onChange();
                this.ace.getSession().on('change', _.bind(this.onChange, this));
                this.$el.resize(_.bind(this.resize, this));
            },

            codeArea: function () {
                return this.$el.is(widgets.const.codearea.editorSelector)
                    ? this.$el
                    : this.$(widgets.const.codearea.editorSelector);
            },

            retrieveInput: function () {
                return this.$el.is(widgets.const.codearea.textareaSelector)
                    ? this.$el
                    : this.$(widgets.const.codearea.textareaSelector);
            },

            retrieveName: function () {
                return this.$el.data('name');
            },

            getValue: function () {
                return this.ace.getValue();
            },

            setValue: function (value, triggerChange) {
                this.ace.setValue(value);
                this.onChange();
                if (triggerChange) {
                    this.$el.trigger('change');
                }
            },

            reset: function () {
                this.setValue('');
            },

            resize: function () {
                this.ace.resize();
            },

            onChange: function () {
                this.$input.val(this.getValue());
            },

            initEditor: function () {
                this.ace = ace.edit(this.$editor[0]);
                this.ace.setTheme('ace/theme/clouds');
                this.setType();
            },

            setType: function (type) {
                if (!type) {
                    type = this.$editor.data('language');
                }
                type = widgets.const.codearea.type[type] || type || 'text';
                this.ace.getSession().setMode({path: 'ace/mode/' + type, v: Date.now()});
            },

            saveAs: function (path, onSuccess, onError, onComplete) {
                if (path) {
                    core.ajaxPut('/bin/cpm/pages/develop.updateFile.json' + path,
                        this.getValue(), {}, onSuccess, onError, onComplete);
                }
            }
        });

        window.widgets.register('.widget.codearea-widget', widgets.CodeAreaWidget);

    })(window.composum.pages.widgets, window.composum.pages, window.core);
})(window);
