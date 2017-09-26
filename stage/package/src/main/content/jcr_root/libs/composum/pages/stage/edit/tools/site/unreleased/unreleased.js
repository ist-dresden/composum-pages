(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            unreleased: {
                cssBase: 'composum-pages-stage-edit-tools-site',
                unreleasedWrapper: '-unreleased_tools-panel',
                siteLoadUri: '/libs/composum/pages/stage/edit/tools/site/',
                contentSelector: '.content.html'
            }
        });

        tools.Unreleased = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.unreleased;
                this.$selectAllBox = $('input.composum-pages-stage-edit-tools-site-unreleased_tools-selectall');
                this.$selectAllBox.on("change", _.bind(this.selectAll, this));
                this.$releaseButton = $('button.composum-pages-stage-edit-tools-site-unreleased_tools-release');
                this.$releaseButton.click(_.bind(this.doRelease, this));
                // composum-pages-stage-edit-tools-site-unversioned-openobject
                this.$('.composum-pages-stage-edit-tools-site-unreleased-finishedobject_entry').click(_.bind(function (event) {
                    var entry = event.currentTarget;
                    var path = entry.dataset.path;
                    tools.contextTabsHook.suppressReplace = true;
                    $(document).trigger("page:select", [path, {'pages.mode': 'preview'}]);
                }, this));
            },

            selectAll: function (event) {
                event.preventDefault();
                $.each($('input.composum-pages-stage-edit-tools-site-unreleased-finishedobject_select'), function (index, value) {
                    value.checked = event.currentTarget.checked;
                });
            },

            onTabSelected: function () {
                this.reload();
            },

            reload: function () {
                var c = tools.const.unreleased;
                core.getHtml(c.siteLoadUri + 'unreleased' + c.contentSelector + this.contextTabs.data.path,
                    undefined, undefined, _.bind(function (data) {
                        if (data.status == 200) {
                            this.$el.html(data.responseText);
                        } else {
                            this.$el.html("");
                        }
                        this.initialize();
                    }, this));
            },

            doRelease: function (event) {
                event.preventDefault();
                var objects = $('input.composum-pages-stage-edit-tools-site-unreleased-finishedobject_select')
                    .filter(function (index, element) {
                        return element.checked;
                    })
                    .map(function (index, element) {
                        return element.dataset.path;
                    })
                    .toArray();

                var path = pages.current.site;

                console.log('tools.Unreleased.doRelease: "' + objects + '", "' + path + '"');

                pages.dialogHandler.openDialog(
                    'createrelease-dialog',
                    '/libs/composum/pages/stage/edit/site/finishedobjects/createrelease.html',
                    pages.releases.CreateReleaseDialog,
                    function () {
                        this.setPath(path);
                        this.setObjects(objects.toString());
                        this.onReloadCallback(function () {
                            pages.editFrame.reloadPage();
                        });
                    });
            }
        });


        /**
         * register these tools as a pages context tool for initialization after load of the context tools set
         */
        pages.contextTools.addTool(function (contextTabs) {
            var c = tools.const.unreleased;
            var panel = core.getWidget(contextTabs.el, '.' + c.cssBase + c.unreleasedWrapper, tools.Unreleased);
            if (panel) {
                panel.contextTabs = contextTabs;
            }
            return panel;
        });


    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
