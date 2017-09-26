(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.releases = window.composum.pages.releases || {};

    (function (releases, pages, core) {
        'use strict';

        releases.FinishedPages = Backbone.View.extend({

            initialize: function (options) {
                this.$button = this.$('button.release');
                this.resourcepath = this.$button[0].dataset.path;
                this.$button.click(_.bind(this.doRelease, this));
                this.$selectAllBox = this.$('input[name="composum-pages-stage-edit-site-finishedobjects_select"]');
                this.$selectAllBox.on("change", _.bind(this.selectAll, this));
            },

            selectAll: function(event) {
                event.preventDefault();
                $.each($('input.composum-pages-stage-edit-site-finishedobjects-finishedobject_select'), function(index, value) {
                    value.checked = event.currentTarget.checked;
                });
            },

            openDialog: function (url, type, id, init, callback) {
                var dialog = core.getWidget(this.el, id, type);
                if (!dialog) {
                    core.getHtml(url,
                        _.bind(function (data) {
                            this.$el.append(data);
                            dialog = core.getWidget(this.el, id, type);
                            if (dialog) {
                                dialog.show(init, callback);
                            }
                        }, this));
                } else {
                    dialog.show(init, callback);
                }
            },

            doRelease: function (event) {
                event.preventDefault();
                var objects = $('input.composum-pages-stage-edit-site-finishedobjects-finishedobject_select')
                    .filter(function(index, element) {
                        return element.checked;})
                    .map(function(index, element) {
                        return element.dataset.path;})
                    .toArray();

                var path = this.resourcepath.substring(0, this.resourcepath.lastIndexOf('/jcr:content'));

                this.openDialog(
                    "/libs/composum/pages/stage/edit/site/finishedobjects/createrelease.html",
                    releases.CreateReleaseDialog,
                    '#createrelease-dialog',
                    function() {
                        this.setPath(path);
                        this.setObjects(objects.toString());
                    });
            }

        });

        releases.finishedPagesHead = core.getView('.finishedPages', releases.FinishedPages);

    })(window.composum.pages.releases, window.composum.pages, window.core);
})(window);
