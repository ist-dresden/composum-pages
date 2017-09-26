(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.releases = window.composum.pages.releases || {};

    (function (releases, pages, core) {
        'use strict';

        releases.Releases = Backbone.View.extend({

            initialize: function (options) {
                this.$buttonPublic = this.$('button.public');
                this.$buttonPreview = this.$('button.preview');
                this.$buttonDelete = this.$('button.delete');
                this.publicPath = this.$buttonPublic[0].dataset.path;

                this.$buttonPublic.click(_.bind(this.publicRelease, this));
                this.$buttonPreview.click(_.bind(this.previewRelease, this));
                this.$buttonDelete.click(_.bind(this.deleteRelease, this));
            },

            xxRelease: function (selector) {
                this.releaseRadios = $('input.composum-pages-stage-edit-site-releases-release_select');
                $.each(this.releaseRadios, _.bind(function(index, value) {
                    if (value.checked) {
                        var key = value.value;
                        var path = this.publicPath.substring(0, this.publicPath.lastIndexOf('/jcr:content'));
                        // call server with path and key
                        core.ajaxPost(
                            '/bin/cpm/pages/release.'+selector+'.html',
                            "path="+path+"&releaseName="+key,
                            {
                                dataType: 'post'
                            },
                            _.bind(function (result) {              //success
                                location.reload();
                            }, this),
                            _.bind(function (result) {              //error
                                this.alert('danger', 'Error setting public release', result)
                            }, this)
                        );
                    }
                }, this));

            },

            publicRelease: function (event) {
                event.preventDefault();
                this.xxRelease('setpublic');
                return false;
            },


            previewRelease: function (event) {
                event.preventDefault();
                this.xxRelease('setpreview');
                return false;
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

            deleteRelease: function (event) {
                event.preventDefault();
                // this.xxRelease('delete');
                this.releaseRadios = $('input.composum-pages-stage-edit-site-releases-release_select');
                $.each(this.releaseRadios, _.bind(function(index, value) {
                    if (value.checked) {
                        var key = value.value;
                        var path = this.publicPath.substring(0, this.publicPath.lastIndexOf('/jcr:content'));

                        var that = this;
                        this.openDialog(
                            "/libs/composum/pages/stage/edit/site/releases/deleterelease.html",
                            releases.DeleteReleaseDialog,
                            '#deleterelease-dialog',
                            function() {
                                this.$inputPath.val(path);
                                this.$releaseName.val(key);
                            });
                    }
                }, this));

                return false;
            }
        });

        releases.releases = core.getView('.releasesList', releases.Releases);


    })(window.composum.pages.releases, window.composum.pages, window.core);
})(window);
