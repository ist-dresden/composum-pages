(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';


        tools.const = _.extend(tools.const || {}, {
            unversioned: {
                cssBase: 'composum-pages-stage-edit-tools-site',
                unversionedWrapper: '-unversioned_tools-panel',
                siteLoadUri: '/libs/composum/pages/stage/edit/tools/site/',
                contentSelector: '.content.html'
            }
        });

        tools.Unversioned = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.unversioned;
                this.$selectAllBox = $('input.' + c.cssBase + '-unversioned_tools-selectall');
                this.$selectAllBox.on("change", _.bind(this.selectAll, this));
                this.$checkpointButton = $('button.composum-pages-stage-edit-tools-site-unversioned_tools-ckeckpoint');
                this.$checkpointButton.off('click');
                this.$checkpointButton.on('click', _.bind(this.doCheckpoint, this));
                this.$('.' + c.cssBase + '-unversioned-openobject_entry').click(_.bind(function (event) {
                    var entry = event.currentTarget;
                    var path = entry.dataset.path;
                    tools.contextTabsHook.suppressReplace = true;
                    $(document).trigger("page:select", [path, {'pages.mode': 'preview'}]);
                }, this));
            },

            selectAll: function (event) {
                var c = tools.const.unversioned;
                event.preventDefault();
                $.each($('input.' + c.cssBase + '-unversioned-openobject_select'), function (index, value) {
                    value.checked = event.currentTarget.checked;
                });
            },

            onTabSelected: function () {
                this.reload();
            },

            reload: function () {
                var c = tools.const.unversioned;
                core.getHtml(c.siteLoadUri + 'unversioned' + c.contentSelector + this.contextTabs.data.path,
                    undefined, undefined, _.bind(function (data) {
                        if (data.status == 200) {
                            this.$el.html(data.responseText);
                        } else {
                            this.$el.html("");
                        }
                        this.initialize();
                    }, this));
            },

            doCheckpoint: function (event) {
                var c = tools.const.unversioned;
                event.preventDefault();
                var objects = $('input.' + c.cssBase + '-unversioned-openobject_select')
                    .filter(function (index, element) {
                        return element.checked;
                    })
                    .map(function (index, element) {
                        return element.dataset.path;
                    })
                    .toArray();

                var path = pages.current.site;

                console.log('tools.Unversioned.doCheckpoint: "' + objects + '", "' + path + '"');

                core.ajaxPost('/bin/cpm/pages/edit.checkpoint.json',
                    "paths=" + objects.toString(),
                    {},
                    _.bind(function (result) {
                        pages.editFrame.reloadPage();
                    }, this), _.bind(function (result) {
                        this.error('on creating checkpoint', result);
                    }, this)
                );


            }

        });

        pages.contextTools.addTool(function (contextTabs) {
            var c = tools.const.unversioned;
            var panel = core.getWidget(contextTabs.el, '.' + c.cssBase + c.unversionedWrapper, tools.Unversioned);
            if (panel) {
                panel.contextTabs = contextTabs;
            }
            return panel;
        });


    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
