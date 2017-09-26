(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            context: {
                cssBase: 'composum-pages-stage-edit-tools-site',
                contextWrapper: '-context_tools-panel',
                siteLoadUri: '/libs/composum/pages/stage/edit/tools/site/',
                contentSelector: '.content.html'
            }
        });

        tools.Context = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.context;
                this.$deleteButton = $('button.composum-pages-stage-edit-tools-site-context_tools-deleterelease');
                this.$deleteButton.click(_.bind(this.deleteRelease, this));
                this.$previewButton = $('button.composum-pages-stage-edit-tools-site-context_tools-previewrelease');
                this.$previewButton.click(_.bind(this.previewRelease, this));
                this.$publicButton = $('button.composum-pages-stage-edit-tools-site-context_tools-publicrelease');
                this.$publicButton.click(_.bind(this.publicRelease, this));
            },

            onTabSelected: function () {
                this.reload();
            },

            reload: function () {
                var c = tools.const.context;
                core.getHtml(c.siteLoadUri + 'context' + c.contentSelector + this.contextTabs.data.path,
                    undefined, undefined, _.bind(function (data) {
                        if (data.status == 200) {
                            this.$el.html(data.responseText);
                        } else {
                            this.$el.html("");
                        }
                        this.initialize();
                    }, this));
            },

            xxRelease: function (selector) {
                var releaseRadios = $('input.composum-pages-stage-edit-tools-site-context-release_select');
                $.each(releaseRadios, _.bind(function (index, value) {
                    if (value.checked) {
                        var key = value.value;
                        var path = pages.current.site;
                        // call server with path and key
                        core.ajaxPost(
                            '/bin/cpm/pages/release.' + selector + '.html',
                            "path=" + path + "&releaseName=" + key,
                            {
                                dataType: 'post'
                            },
                            _.bind(function (result) {              //success
                                pages.editFrame.reloadPage();
                            }, this),
                            _.bind(function (result) {              //error
                                this.alert('danger', 'Error setting public release', result)
                            }, this)
                        );
                    }
                }, this));

            },

            deleteRelease: function (event) {
                event.preventDefault();
                this.releaseRadios = $('input.composum-pages-stage-edit-tools-site-context-release_select');
                $.each(this.releaseRadios, _.bind(function (index, value) {
                    if (value.checked) {
                        var key = value.value;
                        var path = pages.current.site;

                        pages.dialogHandler.openDialog(
                            'deleterelease-dialog',
                            '/libs/composum/pages/stage/edit/site/releases/deleterelease.html',
                            pages.releases.DeleteReleaseDialog,
                            function () {
                                this.$inputPath.val(path);
                                this.$releaseName.val(key);
                                if (_.isFunction(this.onReloadCallback)) {
                                    this.onReloadCallback(function () {
                                        pages.editFrame.reloadPage();
                                    });
                                }
                            });
                    }
                }, this));

                return false;

            },

            previewRelease: function (event) {
                event.preventDefault();
                this.xxRelease('setpreview');
                return false;
            },

            publicRelease: function (event) {
                event.preventDefault();
                this.xxRelease('setpublic');
                return false;
            }
        });


        /**
         * register these tools as a pages context tool for initialization after load of the context tools set
         */
        pages.contextTools.addTool(function (contextTabs) {
            var c = tools.const.context;
            var panel = core.getWidget(contextTabs.el, '.' + c.cssBase + c.contextWrapper, tools.Context);
            if (panel) {
                panel.contextTabs = contextTabs;
            }
            return panel;
        });


    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
