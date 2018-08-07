(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tree = window.composum.pages.tree || {};

    (function (tree, pages, core) {
        'use strict';

        tree.const = _.extend(tree.const || {}, {
            pagesCssBase: 'composum-pages-stage-edit-tools-main-pages',
            developCssBase: 'composum-pages-stage-edit-tools-main-develop',
            treeClass: 'composum-pages-tools_tree',
            treeActionsClass: 'composum-pages-tools_actions',
            treePanelClass: 'composum-pages-tools_tree-panel',
            searchPanelClass: 'composum-pages-tools_search-panel',
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
                    this.initialSelect = pages.profile.get('page-tree', 'current', "/");
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
                this.initializeFilter();
                tree.ToolsTree.prototype.initialize.apply(this, [options]);
                var c = pages.const.event;
                $(document).on(c.element.selected + '.' + id, _.bind(this.onPathSelected, this));
                $(document).on(c.element.inserted + '.' + id, _.bind(this.onContentInserted, this));
                $(document).on(c.element.changed + '.' + id, _.bind(this.onPathChanged, this));
                $(document).on(c.element.deleted + '.' + id, _.bind(this.onPathDeleted, this));
                $(document).on(c.element.moved + '.' + id, _.bind(this.onPathMoved, this));
                $(document).on(c.content.selected + '.' + id, _.bind(this.onPathSelected, this));
                $(document).on(c.content.inserted + '.' + id, _.bind(this.onContentInserted, this));
                $(document).on(c.content.changed + '.' + id, _.bind(this.onPathChanged, this));
                $(document).on(c.content.deleted + '.' + id, _.bind(this.onPathDeleted, this));
                $(document).on(c.content.moved + '.' + id, _.bind(this.onPathMoved, this));
                $(document).on(c.page.selected + '.' + id, _.bind(this.onPathSelected, this));
                $(document).on(c.page.inserted + '.' + id, _.bind(this.onContentInserted, this));
                $(document).on(c.page.changed + '.' + id, _.bind(this.onPathChanged, this));
                $(document).on(c.page.deleted + '.' + id, _.bind(this.onPathDeleted, this));
                $(document).on(c.site.created + '.' + id, _.bind(this.onContentInserted, this));
                $(document).on(c.site.changed + '.' + id, _.bind(this.onPathChanged, this));
                $(document).on(c.site.deleted + '.' + id, _.bind(this.onPathDeleted, this));
            },

            initializeFilter: function () {
                this.filter = pages.profile.get('tree', 'filter', undefined);
            },

            dataUrlForPath: function (path) {
                var params = this.filter ? '?filter=' + this.filter : '';
                return '/bin/cpm/pages/edit.pageTree.json' + path + params;
            },

            nodeIsDraggable: function (selection, event) {
                return true;
            },

            dropNode: function (draggedNode, targetNode, index) {
                var targetPath = targetNode.path;
                var oldPath = draggedNode.path;
                var before = this.getNodeOfIndex(targetNode, index);
                if (this.isElementType(targetNode) && this.isElementType(draggedNode)) {
                    // move elements on a page or between pages via tree...
                    core.ajaxPost(pages.const.url.edit.move + oldPath, {
                        targetPath: targetPath,
                        targetType: targetNode.type,
                        before: before ? before.original.path : undefined
                    }, {}, function (data) {
                        $(document).trigger(pages.const.event.element.moved, [oldPath, data.path]);
                    }, function (xhr) {
                        core.alert('error', 'Error', 'Error on moving element', xhr);
                    });
                } else {
                    // move folders, pages, files...
                    var oldParentPath = core.getParentPath(oldPath);
                    if (oldParentPath === targetPath) {
                        // reordering in the same parent resource - keep that simple...
                        core.ajaxPost('/bin/cpm/pages/edit.moveContent.json' + core.encodePath(oldPath), {
                            targetPath: targetPath,
                            before: before ? before.original.path : undefined
                        }, {}, _.bind(function (data) {
                            $(document).trigger(pages.const.event.content.moved, [oldPath, data.path]);
                        }, this), _.bind(function (xhr) {
                            core.alert('error', 'Error', 'Error on moving content', xhr);
                        }, this));
                    } else {
                        // move to another parent - check and confirm...
                        pages.dialogs.openMoveContentDialog(draggedNode.name, draggedNode.path, draggedNode.type,
                            _.bind(function (dialog) {
                                dialog.setValues(draggedNode.path, targetNode.path, before ? before.original.name : undefined);
                            }, this));
                    }
                }
            },

            renameNode: function (node, oldName, newName) {
                var nodePath = node.path;
                var parentPath = core.getParentPath(nodePath);
                var oldPath = core.buildContentPath(parentPath, oldName);
                core.ajaxPost('/bin/cpm/pages/edit.renameContent.json' + core.encodePath(oldPath), {
                    name: newName
                }, {}, _.bind(function (data) {
                    if (this.isElementType(node)) {
                        $(document).trigger(pages.const.event.element.moved, [oldPath, data.path]);
                    } else {
                        $(document).trigger(pages.const.event.content.moved, [oldPath, data.path]);
                    }
                }, this), _.bind(function (result) {
                    this.refreshNodeById(node.id);
                    pages.dialogs.openRenameContentDialog(oldName, oldPath, node.type,
                        _.bind(function (dialog) {
                            dialog.setValues(oldPath, newName);
                            dialog.errorMessage('Rename Content', result);
                        }, this));
                }, this));
            },

            isElementType: function (node) {
                return 'container' === node.type || 'element' === node.type;
            }
        });

        tree.DevelopTree = tree.ToolsTree.extend({

            nodeIdPrefix: 'PD_',

            initialize: function (options) {
                this.initialSelect = this.$el.attr('data-selected');
                if (!this.initialSelect || this.initialSelect === '/') {
                    this.initialSelect = pages.profile.get('page-tree', 'current', "/");
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
                this.$treePanel = this.$('.' + tree.const.treePanelClass);
                this.searchPanel = core.getWidget(this.el, '.' + pages.search.const.css.base + pages.search.const.css._panel,
                    pages.search.SearchPanel);
                this.$viewToggle = this.$('.' + tree.const.pagesCssBase + '_toggle-view');
                this.setView(pages.profile.get('page-tree', 'view', 'tree'));
                this.$viewToggle.click(_.bind(this.toggleView, this));
                this.selectFilter(pages.profile.get('page-tree', 'filter', undefined));
                this.$('.' + tree.const.pagesCssBase + '_filter-value a').click(_.bind(this.setFilter, this));
                $(document).on('path:selected.' + this.treePanelId, _.bind(this.onPathSelected, this));
                $(document).on('element:selected.' + this.treePanelId, _.bind(this.onPathSelected, this));
                $(document).on('page:selected.' + this.treePanelId, _.bind(this.onPathSelected, this));
            },

            onTabSelected: function () {
                if (this.currentView === 'search') {
                    this.searchPanel.onShown();
                }
            },

            setView: function (key) {
                if (!key) {
                    key = !this.currentView || this.currentView === 'search' ? 'tree' : 'search';
                }
                if (this.currentView !== key) {
                    this.currentView = key;
                    pages.profile.set('page-tree', 'view', key);
                    switch (key) {
                        default:
                        case 'tree':
                            this.$viewToggle.removeClass('active');
                            this.searchPanel.$el.addClass('hidden');
                            this.$treePanel.removeClass('hidden');
                            break;
                        case 'search':
                            this.$viewToggle.addClass('active');
                            this.$treePanel.addClass('hidden');
                            this.searchPanel.$el.removeClass('hidden');
                            this.searchPanel.onShown();
                            break;
                    }
                }
            },

            toggleView: function (event) {
                event.preventDefault();
                this.setView();
                return false;
            },

            selectDefaultNode: function () {
                if (pages.current.page) {
                    this.tree.selectNode(pages.current.page, _.bind(function () {
                        this.setNodeActions();
                    }, this));
                }
            },

            setFilter: function (event) {
                event.preventDefault();
                var $link = $(event.currentTarget);
                var filter = $link.parent().data('value');
                this.tree.setFilter(filter);
                this.selectFilter(filter);
                pages.profile.set('page-tree', 'filter', filter);
            },

            selectFilter: function (filter) {
                this.$('.' + tree.const.pagesCssBase + '_filter-value').removeClass('active');
                if (filter) {
                    this.$('.' + tree.const.pagesCssBase + '_filter-value[data-value="' + filter + '"]')
                        .addClass('active');
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
