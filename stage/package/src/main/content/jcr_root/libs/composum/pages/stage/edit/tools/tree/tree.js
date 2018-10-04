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

            /**
             * change tree selection according to a selected path (tree is secondary view)
             */
            selectedNode: function (path, callback) {
                this.suppressEvent = true;
                this.selectNode(path, _.bind(function (path) {
                    this.suppressEvent = false;
                    if (_.isFunction(callback)) {
                        callback(path);
                    }
                }, this));
            },

            /**
             * prevent from fire 'select' if tree is selecting a path as secondary view only
             */
            onNodeSelected: function (path, node) {
                if (!this.suppressEvent) {
                    core.components.Tree.prototype.onNodeSelected.apply(this, [path, node]);
                }
            },

            onPathSelectedFailed: function (path) {
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug(this.nodeIdPrefix + 'tree.onPathSelectedFailed(' + path + ')');
                }
                var node;
                while (!(node = this.getSelectedTreeNode()) && (path = core.getParentPath(path)) && path !== '/') {
                    this.selectedNode(path);
                }
            },

            onContentSelected: function (event, refOrPath) {
                this.onPathSelected(event, refOrPath && refOrPath.path ? refOrPath.path : refOrPath);
            },

            onContentInserted: function (event, refOrPath) {
                var path = refOrPath && refOrPath.path ? refOrPath.path : refOrPath;
                var parentAndName = core.getParentAndName(path);
                this.onPathInserted(event, parentAndName.path, parentAndName.name);
            },

            onContentMoved: function (event, refOrPath) {
                this.onPathMoved(event, refOrPath && refOrPath.path ? refOrPath.path : refOrPath);
            },

            onContentChanged: function (event, refOrPath) {
                this.onPathChanged(event, refOrPath && refOrPath.path ? refOrPath.path : refOrPath);
            },

            onContentDeleted: function (event, refOrPath) {
                this.onPathDeleted(event, refOrPath && refOrPath.path ? refOrPath.path : refOrPath);
            },

            onElementSelected: function (event, reference) {
                this.onPathSelected(event, reference ? reference.path : undefined);
            },

            onElementInserted: function (event, reference) {
                var parentAndName = core.getParentAndName(reference.path);
                this.onPathInserted(event, parentAndName.path, parentAndName.name);
            },

            onElementMoved: function (event, reference) {
                this.onPathMoved(event, reference ? reference.path : undefined);
            },

            onElementChanged: function (event, reference) {
                this.onPathChanged(event, reference ? reference.path : undefined);
            },

            onElementDeleted: function (event, reference) {
                this.onPathDeleted(event, reference ? reference.path : undefined);
            }
        });

        tree.PageTree = tree.ToolsTree.extend({

            nodeIdPrefix: 'PP_',

            initialize: function (options) {
                var p = pages.const.profile.page.tree;
                var id = this.nodeIdPrefix + 'Tree';
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
                var e = pages.const.event;
                $(document).on(e.element.selected + '.' + id, _.bind(this.onElementSelected, this));
                $(document).on(e.element.inserted + '.' + id, _.bind(this.onElementInserted, this));
                $(document).on(e.element.changed + '.' + id, _.bind(this.onElementChanged, this));
                $(document).on(e.element.deleted + '.' + id, _.bind(this.onElementDeleted, this));
                $(document).on(e.element.moved + '.' + id, _.bind(this.onElementMoved, this));
                $(document).on(e.content.selected + '.' + id, _.bind(this.onContentSelected, this));
                $(document).on(e.content.inserted + '.' + id, _.bind(this.onContentInserted, this));
                $(document).on(e.content.changed + '.' + id, _.bind(this.onContentChanged, this));
                $(document).on(e.content.deleted + '.' + id, _.bind(this.onContentDeleted, this));
                $(document).on(e.content.moved + '.' + id, _.bind(this.onContentMoved, this));
                $(document).on(e.page.selected + '.' + id, _.bind(this.onContentSelected, this));
                $(document).on(e.page.inserted + '.' + id, _.bind(this.onContentInserted, this));
                $(document).on(e.page.changed + '.' + id, _.bind(this.onContentChanged, this));
                $(document).on(e.page.deleted + '.' + id, _.bind(this.onContentDeleted, this));
                $(document).on(e.site.created + '.' + id, _.bind(this.onContentInserted, this));
                $(document).on(e.site.changed + '.' + id, _.bind(this.onContentSelected, this));
                $(document).on(e.site.deleted + '.' + id, _.bind(this.onContentDeleted, this));
            },

            setRootPath: function (rootPath, refresh) {
                rootPath = this.adjustRootPath(rootPath);
                if (refresh === undefined) {
                    refresh = (this.rootPath && this.rootPath !== rootPath);
                }
                tree.ToolsTree.prototype.setRootPath.apply(this, [rootPath, refresh]);
            },

            adjustRootPath: function (rootPath) {
                var scope = pages.getScope();
                if (scope === 'site' && pages.current.site && rootPath.indexOf(pages.current.site) !== 0) {
                    rootPath = pages.current.site;
                }
                return rootPath;
            },

            initializeFilter: function () {
                var p = pages.const.profile.page.tree;
                this.filter = pages.profile.get(p.aspect, p.filter, undefined);
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
                    }, {}, function (result) {
                        $(document).trigger(pages.const.event.element.moved, [
                            new pages.Reference(draggedNode.name, draggedNode.path, draggedNode.type),
                            result.reference]);
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
                        }, {}, _.bind(function (result) {
                            $(document).trigger(pages.const.event.content.moved, [
                                new pages.Reference(draggedNode.name, draggedNode.path, draggedNode.type),
                                result.reference]);
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
                    var p = pages.const.profile.develop.tree;
                    this.initialSelect = pages.profile.get(p.aspect, p.path, "/");
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

            onNodeSelected: function (event, path, triggerEvent) {
                if (this.path !== path) {
                    pages.log.debug(this.treePanelId + '.onPathSelected(' + path + ')');
                    this.path = path;
                    if (!path) {
                        this.doSelectDefaultNode();
                    } else {
                        this.setNodeActions(path);
                    }
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
                var e = pages.const.event;
                var p = pages.const.profile.page.tree;
                this.tree = core.getWidget(this.el, '.' + tree.const.treeClass, tree.PageTree);
                this.tree.panel = this;
                tree.ToolsTreePanel.prototype.initialize.apply(this, [options]);
                this.$treePanel = this.$('.' + tree.const.treePanelClass);
                this.searchPanel = core.getWidget(this.el, '.' + pages.search.const.css.base + pages.search.const.css._panel,
                    pages.search.SearchPanel);
                this.$viewToggle = this.$('.' + tree.const.pagesCssBase + '_toggle-view');
                this.setView(pages.profile.get(p.aspect, p.view, 'tree'));
                this.$viewToggle.click(_.bind(this.toggleView, this));
                this.selectFilter(pages.profile.get(p.aspect, p.filter, undefined));
                this.$('.' + tree.const.pagesCssBase + '_filter-value a').click(_.bind(this.setFilter, this));
                this.tree.$el.on(e.path.selected + '.' + this.treePanelId, _.bind(this.onNodeSelected, this));
                $(document).on(e.site.selected + '.' + this.treePanelId, _.bind(this.onSiteSelected, this));
                $(document).on(e.scope.changed + '.' + this.treePanelId, _.bind(this.onScopeChanged, this));
            },

            onScopeChanged: function () {
                if (this.tree.log.getLevel() <= log.levels.DEBUG) {
                    this.tree.log.debug(this.tree.nodeIdPrefix + 'treePanel.onScopeChanged()');
                }
                this.tree.setRootPath('/');
                this.searchPanel.onScopeChanged();
            },

            onSiteSelected: function () {
                if (this.tree.log.getLevel() <= log.levels.DEBUG) {
                    this.tree.log.debug(this.tree.nodeIdPrefix + 'treePanel.onSiteSelected()');
                }
                this.onScopeChanged();
            },

            onTabSelected: function () {
                if (this.tree.log.getLevel() <= log.levels.DEBUG) {
                    this.tree.log.debug(this.tree.nodeIdPrefix + 'treePanel.onTabSelected()');
                }
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
                    var p = pages.const.profile.page.tree;
                    pages.profile.set(p.aspect, p.view, key);
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
                var p = pages.const.profile.page.tree;
                var $link = $(event.currentTarget);
                var filter = $link.parent().data('value');
                this.tree.setFilter(filter);
                this.selectFilter(filter);
                pages.profile.set(p.aspect, p.filter, filter);
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
