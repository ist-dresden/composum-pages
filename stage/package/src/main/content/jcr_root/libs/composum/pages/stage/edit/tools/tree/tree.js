(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tree = window.composum.pages.tree || {};

    (function (tree, pages, core) {
        'use strict';

        tree.const = _.extend(tree.const || {}, {
            pagesCssBase: 'composum-pages-stage-edit-tools-tree-pages',
            developCssBase: 'composum-pages-stage-edit-tools-tree-develop',
            treeClass: 'composum-pages-tools_tree',
            treePanelClass: 'composum-pages-tools_panel',
            treeActionsClass: 'composum-pages-tools_actions',
            contextActionsClass: 'composum-pages-tools_left-actions',
            contextActionsLoadUrl: '/bin/cpm/pages/edit.treeActions.html'
        });

        tree.ToolsTree = core.components.Tree.extend({

            initialize: function (options) {
                 core.components.Tree.prototype.initialize.apply(this, [options]);
            },

            onPathSelectedFailed: function (path) {
                var node = this.getSelectedTreeNode();
                while (!node && (path = core.getParentPath(path)) && path !== '/') {
                    var busy = this.busy;
                    this.busy = true;
                    this.selectNode(path);
                    this.busy = busy;
                    node = this.getSelectedTreeNode();
                }
                this.onPathSelected(undefined, path);
            },

            onContentInserted: function (event, path) {
                var parentAndName = core.getParentAndName(path);
                this.onPathInserted(event, parentAndName.path, parentAndName.name);
            }
        });

        tree.PageTree = tree.ToolsTree.extend({

            nodeIdPrefix: 'PP_',

            initialize: function (options) {
                var id = this.nodeIdPrefix + 'Tree';
                this.initialSelect = this.$el.attr('data-selected');
                if (!this.initialSelect || this.initialSelect === '/') {
                    this.initialSelect = pages.profile.get('tree', 'current', "/");
                }
                options = _.extend(options || {}, {
                    dragAndDrop: {
                        is_draggable: _.bind(this.nodeIsDraggable, this),
                        inside_pos: 'last',
                        copy: false,
                        check_while_dragging: false,
                        drag_selection: false,
                        touch: false, //'selection',
                        large_drag_target: true,
                        large_drop_target: true,
                        use_html5: false
                    }
                });
                tree.ToolsTree.prototype.initialize.apply(this, [options]);
                $(document).on('component:selected.' + id, _.bind(this.onPathSelected, this));
                $(document).on('component:changed.' + id, _.bind(this.onPathChanged, this));
                $(document).on('content:selected.' + id, _.bind(this.onPathSelected, this));
                $(document).on('content:inserted.' + id, _.bind(this.onContentInserted, this));
                $(document).on('content:changed.' + id, _.bind(this.onPathChanged, this));
                $(document).on('content:deleted.' + id, _.bind(this.onPathDeleted, this));
                $(document).on('content:moved.' + id, _.bind(this.onPathMoved, this));
                $(document).on('page:selected.' + id, _.bind(this.onPathSelected, this));
                $(document).on('page:inserted.' + id, _.bind(this.onContentInserted, this));
                $(document).on('page:changed.' + id, _.bind(this.onPathChanged, this));
                $(document).on('page:deleted.' + id, _.bind(this.onPathDeleted, this));
                $(document).on('site:created.' + id, _.bind(this.onContentInserted, this));
                $(document).on('site:changed.' + id, _.bind(this.onPathChanged, this));
                $(document).on('site:deleted.' + id, _.bind(this.onPathDeleted, this));
            },

            dataUrlForPath: function (path) {
                return '/bin/cpm/pages/edit.pageTree.json' + path;
            },

            nodeIsDraggable: function (selection, event) {
                return true;
            },

            dropNode: function (draggedNode, targetNode, index) {
                var targetPath = targetNode.path;
                var oldPath = draggedNode.path;
                var before = this.getNodeOfIndex(targetNode, index);
                var oldParentPath = core.getParentPath(oldPath);
                if (oldParentPath === targetPath) {
                    // reordering in the same parent resource - keep that simple...
                    core.ajaxPost('/bin/cpm/pages/edit.moveContent.json' + core.encodePath(oldPath), {
                        targetPath: targetPath,
                        before: before ? before.original.path : undefined
                    }, {}, _.bind(function (data) {
                        $(document).trigger('content:moved', [oldPath, data.path]);
                    }, this));
                } else {
                    // move to another parent - check and confirm...
                    pages.dialogs.openMoveContentDialog(draggedNode.name, draggedNode.path, draggedNode.type,
                        _.bind(function (dialog) {
                            dialog.setValues(draggedNode, targetNode, before ? before.original : undefined);
                        }, this));
                }
            }
        });

        tree.DevelopTree = tree.ToolsTree.extend({

            nodeIdPrefix: 'PD_',

            initialize: function (options) {
                this.initialSelect = this.$el.attr('data-selected');
                if (!this.initialSelect || this.initialSelect === '/') {
                    this.initialSelect = pages.profile.get('tree', 'current', "/");
                }
                tree.ToolsTree.prototype.initialize.apply(this, [options]);
            },

            dataUrlForPath: function (path) {
                return '/bin/cpm/pages/edit.developTree.json' + path;
            }
        });

        tree.ToolsTreePanel = Backbone.View.extend({

            initialize: function (options) {
                this.$actions = this.$('.' + tree.const.contextActionsClass);
                this.treePanelId = this.tree.nodeIdPrefix + 'treePanel';
            },

            onPathSelected: function (event, path) {
                var treePath = this.tree.getSelectedPath();
                if (this.actions) {
                    if (treePath && this.actions.data.path === treePath && treePath === path) {
                        return;
                    }
                }
                console.log(this.treePanelId + '.onPathSelected(' + path + ') <- ' + treePath);
                if (!path) {
                    this.doSelectDefaultNode();
                } else if (path === treePath) {
                    this.setNodeActions(path);
                } else {
                    this.tree.selectNode(path, _.bind(function () {
                        var node = this.tree.getSelectedTreeNode();
                        if (node) {
                            this.setNodeActions();
                        } else {
                            this.doSelectDefaultNode();
                        }
                    }, this));
                }
            },

            doSelectDefaultNode: function () {
                var busy = this.tree.busy;
                this.tree.busy = true;
                this.selectDefaultNode();
                this.tree.busy = busy;
            },

            setNodeActions: function (path) {
                if (this.actions) {
                    this.actions.dispose();
                    this.actions = undefined;
                }
                if (!path) {
                    var node = this.tree.getSelectedTreeNode();
                    if (node) {
                        path = node.original.path;
                    }
                }
                if (path) {
                    if (this.$actions.length > 0) {
                        // load tree actions for the selected resource (if actions are used in the tree)
                        core.ajaxGet(tree.const.contextActionsLoadUrl + path, {},
                            _.bind(function (data) {
                                this.$actions.html(data);
                                this.actions = core.getWidget(this.$actions[0],
                                    '.' + pages.toolbars.const.editToolbarClass, pages.toolbars.EditToolbar);
                                this.actions.data = {
                                    path: path
                                };
                            }, this));
                    }
                }
            }
        });

        tree.PageTreePanel = tree.ToolsTreePanel.extend({

            initialize: function (options) {
                this.tree = core.getWidget(this.el, '.' + tree.const.treeClass, tree.PageTree);
                this.tree.panel = this;
                tree.ToolsTreePanel.prototype.initialize.apply(this, [options]);
                $(document).on('path:selected.' + this.treePanelId, _.bind(this.onPathSelected, this));
                $(document).on('component:selected.' + this.treePanelId, _.bind(this.onPathSelected, this));
                $(document).on('page:selected.' + this.treePanelId, _.bind(this.onPathSelected, this));
            },

            selectDefaultNode: function () {
                if (pages.current.page) {
                    this.tree.selectNode(pages.current.page, _.bind(function () {
                        this.setNodeActions();
                    }, this));
                }
            }
        });

        tree.DevelopTreePanel = tree.ToolsTreePanel.extend({

            initialize: function (options) {
                this.tree = core.getWidget(this.el, '.' + tree.const.treeClass, tree.DevelopTree);
                this.tree.panel = this;
                tree.ToolsTreePanel.prototype.initialize.apply(this, [options]);
            },

            selectDefaultNode: function () {
            }
        });

        tree.pageTreePanel = core.getView('.' + tree.const.pagesCssBase, tree.PageTreePanel);
        tree.developTreePanel = core.getView('.' + tree.const.developCssBase, tree.DevelopTreePanel);

    })(window.composum.pages.tree, window.composum.pages, window.core);
})(window);
