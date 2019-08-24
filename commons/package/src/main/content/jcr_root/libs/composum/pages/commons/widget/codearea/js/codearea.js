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
                this.searchOptions = {
                    wrap: true,
                    caseSensitive: false,
                    regExp: false
                };
                this.$editor = this.codeArea();
                this.$editor.css('height', this.$el.data('height') || '150px');
                this.initEditor();
                this.$el.append(widgets.const.codearea.textarea);
                window.widgets.Widget.prototype.initialize.apply(this, [options]);
                this.$input.attr('name', this.name);
                var encoded = this.$el.data('encoded');
                if (encoded) {
                    this.setValue(atob(encoded));
                }
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
                this.ace.clearSelection();
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
                this.ace.commands.addCommand({
                    name: 'save',
                    bindKey: {
                        win: 'Ctrl-S',
                        mac: 'Command-S'
                    },
                    exec: _.bind(this.save, this)
                });
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

            focus: function () {
                this.ace.focus();
            },

            open: function (path, onSuccess, onError, onComplete) {
                if (path) {
                    core.ajaxGet(path, {
                        contentType: 'text/plain;charset=UTF-8',
                        dataType: 'text'
                    }, _.bind(function (content) {
                        this.setValue(content);
                        var ext = /^.*\.([^./]+)$/.exec(path);
                        if (ext) {
                            this.setType(ext[1]);
                        }
                        if (_.isFunction(onSuccess)) {
                            onSuccess();
                        }
                    }, this), onError, onComplete);
                }
            },

            save: function () {
                this.saveAs(this.$el.data('path'));
            },

            saveAs: function (path, onSuccess, onError, onComplete) {
                if (path) {
                    this.$el.data('path', path);
                    core.ajaxPut('/bin/cpm/pages/develop.updateFile.json' + path,
                        this.getValue(), {
                            contentType: 'text/plain;charset=UTF-8',
                            dataType: 'text'
                        }, onSuccess, onError, onComplete);
                }
            },

            // actions...

            findText: function (text) {
                if (text) {
                    this.searchTerm = text;
                }
                if (this.searchTerm) {
                    this.searchOptions.backwards = false;
                    this.ace.findAll(this.searchTerm, this.searchOptions, false);
                }
            },

            findNext: function () {
                this.searchOptions.backwards = false;
                this.ace.findNext(this.searchOptions, false);
            },

            findPrev: function () {
                this.searchOptions.backwards = true;
                this.ace.findPrevious(this.searchOptions, false);
            },

            toggleCaseSensitive: function (event) {
                this.searchOptions.caseSensitive = event
                    ? $(event.currentTarget).prop('checked')
                    : !this.searchOptions.caseSensitive;
                this.findText();
            },

            toggleRegExp: function (event) {
                this.searchOptions.regExp = event
                    ? $(event.currentTarget).prop('checked')
                    : !this.searchOptions.regExp;
                this.findText();
            },

            replace: function (text) {
                if (text) {
                    this.ace.replace(text, this.searchOptions);
                }
            },

            replaceAll: function (text) {
                if (text) {
                    this.ace.replaceAll(text, this.searchOptions);
                }
            },

            undo: function () {
                this.ace.undo();
            },

            redo: function () {
                this.ace.redo();
            }
        });

        window.widgets.register('.widget.codearea-widget', widgets.CodeAreaWidget);

    })(window.composum.pages.widgets, window.composum.pages, window.core);
})(window);
