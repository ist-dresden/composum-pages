(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.releases = window.composum.pages.releases || {};

    (function (releases, pages, core) {
        'use strict';

        releases.DeleteReleaseDialog = core.components.Dialog.extend({
            initialize: function (options) {
                core.components.Dialog.prototype.initialize.apply(this, [options]);
                this.form = core.getWidget(this.el, 'form.widget-form', core.components.FormWidget);
                this.$inputPath = this.$('input[name="path"]');
                this.$releaseName = this.$('input[name="releaseName"]');
                this.$('button.create').click(_.bind(this.onSubmit, this))
            },

            setPath: function(path) {
                this.$inputPath.val(path);
            },

            onSubmit: function (event) {
                event.preventDefault();
                var serializedData = this.form.$el.serialize();
                core.ajaxPost(
                    '/bin/cpm/pages/release.delete.html',
                    serializedData,
                    {
                        dataType: 'post'
                    },
                    _.bind(function (result) {              //success
                        this.hide();
                        location.reload();
                    }, this),
                    _.bind(function (result) {              //error
                        this.alert('danger', 'Error deleting release', result)
                    }, this)
                );
                return false;
            }
        });

    })(window.composum.pages.releases, window.composum.pages, window.core);
})(window);
