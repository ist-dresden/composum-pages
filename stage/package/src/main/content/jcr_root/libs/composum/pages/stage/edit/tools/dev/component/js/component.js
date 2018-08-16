(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            component: {
                cssBase: 'composum-pages-stage-edit-tools-dev-component',
                wrapper: '_component-panel',
                thumbnailKey: 'composum-pages-edit-thumbnail',
                dialog: '-dialog',
                dialogEdit: '-dialog_edit',
                dialogThumbnail: '-dialog_thumbnail',
                componentLoadUri: '/libs/composum/pages/stage/edit/tools/dev/component.content.html',
                thumbnailWidth: 600.0,
                maxThumbnailScale: 0.5
            }
        });

        tools.Thumbnail = Backbone.View.extend({

            initialize: function (options) {
                this.$content = this.$('.' + tools.const.component.thumbnailKey);
                if (this.$content.length > 0) {
                    this.adjustSize();
                    $(document).on('sidebarResized:contextTools.thumbnail', _.bind(this.adjustSize, this));
                }
            },

            adjustSize: function () {
                var c = tools.const.component;
                this.width = this.$el.width();
                this.$content.css('width', c.thumbnailWidth + 'px');
                var scale = Math.min(this.width / c.thumbnailWidth, c.maxThumbnailScale);
                var width = this.$content.outerWidth();
                var height = this.$content.outerHeight();
                var tx = (scale < c.maxThumbnailScale ? (this.width - width) / scale : -width) / 2.0;
                var ty = (scale < c.maxThumbnailScale ? ((this.width * (height / width) - height) / scale) : -height) / 2.0;
                this.$content.css('transform', 'scale(' + scale + ') translate(' + tx + 'px, ' + ty + 'px)');
                this.$el.css('height', height * scale);
            }
        });

        tools.ComponentDialog = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.component;
                this.editButton = this.$('.' + c.cssBase + c.dialogEdit);
                core.getWidget(this.el, '.' + c.cssBase + c.dialogThumbnail, tools.Thumbnail);
                this.editButton.click(_.bind(this.openDialogEditor, this));
            },

            openDialogEditor: function (event) {
                var self = this;
                var path = this.$el.data('path');
                var type = this.$el.data('type');
                pages.dialogs.getDialog('text-edit-dialog',
                    '/libs/composum/nodes/console/components/codeeditor/editdialog.html', {},
                    core.components.CodeEditorDialog, _.bind(function (dialog) {
                        dialog.editFile(path + '/jcr:content', type);
                    }, this));
            }
        });

        tools.Component = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.component;
                core.getWidget(this.el, '.' + c.cssBase + c.dialog, tools.ComponentDialog);
            },

            onTabSelected: function () {
                this.reload();
            },

            reload: function () {
                var c = tools.const.component;
                core.getHtml(c.componentLoadUri + this.contextTabs.data.path,
                    undefined, undefined, _.bind(function (data) {
                        if (data.status == 200) {
                            this.$el.html(data.responseText);
                        } else {
                            this.$el.html("");
                        }
                        this.initialize();
                    }, this));
            }
        });

        /**
         * register this tool as a pages context tool for initialization after load of the context tools set
         */
        pages.contextTools.addTool(function (contextTabs) {
            var c = tools.const.component;
            var component = core.getWidget(contextTabs.el, '.' + c.cssBase + c.wrapper, tools.Component);
            if (component) {
                component.contextTabs = contextTabs;
            }
            return component;
        });

    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
