(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.releases = window.composum.pages.releases || {};

    (function (releases, pages, core) {
        'use strict';

        releases.CreateReleaseDialog = pages.dialogs.EditDialog.extend({
            initialize: function (options) {
                core.components.Dialog.prototype.initialize.apply(this, [options]);
                this.form = core.getWidget(this.el, 'form.widget-form', core.components.FormWidget);
                this.$inputPath = this.$('input[name="path"]');
                this.$inputObjects = this.$('input[name="objects"]');
                this.$('button[type="submit"]').click(_.bind(this.onSubmit, this));
                this.$charset = this.$('input[name="_charset_"]');
            },

            // reset: function () {
            //     core.components.Dialog.prototype.reset.apply(this);
            // },

            setPath: function(path) {
                this.$inputPath.val(path);
            },

            setObjects: function(objects) {
                this.$inputObjects.val(objects);
            },

            onReloadCallback: function(callback) {
                this.orCallback = callback;
            },

            resetOnShown: function() {
                core.components.Dialog.prototype.resetOnShown.apply(this, []);
                this.$charset.val('UTF-8');
            },

            onSubmit: function (event) {
                event.preventDefault();
                var serializedData = this.form.$el.serialize();
                core.ajaxPost(
                    '/bin/cpm/pages/release.release.html',
                    serializedData,
                    {
                        dataType: 'post'
                    },
                    _.bind(function (result) {              //success
                        this.hide();
                        if (_.isFunction(this.orCallback)) {
                            //pages.editFrame.reloadPage()
                            this.orCallback();
                        } else {
                            location.reload();
                        }
                    }, this),
                    _.bind(function (result) {              //error
                        this.alert('danger', 'Error creating release', result)
                    }, this)
                );
                return false;
            }
        });

    })(window.composum.pages.releases, window.composum.pages, window.core);
})(window);
