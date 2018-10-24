(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tree = window.composum.pages.tree || {};

    (function (tree, pages, core) {
        'use strict';

        tree.const = _.extend(tree.const || {}, {
            treeClass: 'composum-pages-tools_tree',
            treeActionsClass: 'composum-pages-tools_actions',
            treePanelClass: 'composum-pages-tools_tree-panel',
            treePanelPreviewClass: 'tree-panel-preview',
            searchPanelClass: 'composum-pages-tools_search-panel',
            actions: {
                css: 'composum-pages-tools_left-actions',
                url: '/bin/cpm/pages/edit.treeActions.html'
            },
            tile: {
                url: '/bin/cpm/pages/edit.editTile.html'
            },
            pages: {
                css: {
                    base: 'composum-pages-stage-edit-tools-main-pages'
                }
            },
            assets: {
                css: {
                    base: 'composum-pages-stage-edit-tools-main-assets'
                }
            },
            develop: {
                css: {
                    base: 'composum-pages-stage-edit-tools-main-develop'
                }
            }
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

            onPathSelectedFailed: function (path) {
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug(this.nodeIdPrefix + 'tree.onPathSelectedFailed(' + path + ')');
                }
                var node;
                if (!(node = this.getSelectedTreeNode()) && (path = core.getParentPath(path)) && path !== '/') {
                    this.selectedNode(path, _.bind(this.checkSelectedPath, this));
                }
            },

            onPathSelected: function (event, path) {
                if (core.components.Tree.prototype.onPathSelected.apply(this, [event, path])) {
                    this.$el.trigger("node:selected", [path]);
                }
            }
        });

        tree.ContentTree = tree.ToolsTree.extend({

            initialize: function (options) {
                tree.ToolsTree.prototype.initialize.apply(this, [options]);
            },

            onContentInserted: function (event, refOrPath) {
                var path = refOrPath && refOrPath.path ? refOrPath.path : refOrPath;
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug(this.nodeIdPrefix + 'tree.onContentInserted(' + path + ')');
                }
                var parentAndName = core.getParentAndName(path);
                this.onPathInserted(event, parentAndName.path, parentAndName.name);
            },

            onContentMoved: function (event, refOrPath) {
                var path = refOrPath && refOrPath.path ? refOrPath.path : refOrPath;
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug(this.nodeIdPrefix + 'tree.onContentMoved(' + path + ')');
                }
                this.onPathMoved(event, path);
            },

            onContentChanged: function (event, refOrPath) {
                var path = refOrPath && refOrPath.path ? refOrPath.path : refOrPath;
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug(this.nodeIdPrefix + 'tree.onContentChanged(' + path + ')');
                }
                this.onPathChanged(event, path);
            },

            onContentDeleted: function (event, refOrPath) {
                var path = refOrPath && refOrPath.path ? refOrPath.path : refOrPath;
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug(this.nodeIdPrefix + 'tree.onContentDeleted(' + path + ')');
                }
                this.onPathDeleted(event, path);
            },

            setRootPath: function (rootPath, refresh) {
                rootPath = this.adjustRootPath(rootPath);
                if (refresh === undefined) {
                    refresh = (this.rootPath && this.rootPath !== rootPath);
                }
                core.components.Tree.prototype.setRootPath.apply(this, [rootPath, refresh]);
            },

            adjustRootPath: function (rootPath) {
                var scope = pages.getScope();
                if (scope === 'site' && pages.current.site && rootPath.indexOf(pages.current.site) !== 0) {
                    rootPath = pages.current.site;
                }
                return rootPath;
            },

            nodeIsDraggable: function (selection, event) {
                return true;
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

        tree.PageTree = tree.ContentTree.extend({

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
                tree.ContentTree.prototype.initialize.apply(this, [options]);
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

            initializeFilter: function () {
                var p = pages.const.profile.page.tree;
                this.filter = pages.profile.get(p.aspect, p.filter, undefined);
            },

            dataUrlForPath: function (path) {
                var params = this.filter ? '?filter=' + this.filter : '';
                return '/bin/cpm/pages/edit.pageTree.json' + path + params;
            },

            onNodeSelected: function (path, node) {
                if (!this.suppressEvent) {
                    $(document).trigger("path:select", [path, node.original.name, node.original.type]);
                } else {
                    this.$el.trigger("node:selected", [path]);
                }
            },

            onContentSelected: function (event, refOrPath) {
                var path = refOrPath && refOrPath.path ? refOrPath.path : refOrPath;
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug(this.nodeIdPrefix + 'tree.onContentSelected(' + path + ')');
                }
                this.onPathSelected(event, path);
            },

            onElementSelected: function (event, reference) {
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug(this.nodeIdPrefix + 'tree.onElementSelected(' + (reference ? reference.path : '') + ')');
                }
                this.onPathSelected(event, reference ? reference.path : undefined);
            },

            onElementInserted: function (event, reference) {
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug(this.nodeIdPrefix + 'tree.onElementSelected(' + (reference ? reference.path : '') + ')');
                }
                var parentAndName = core.getParentAndName(reference.path);
                this.onPathInserted(event, parentAndName.path, parentAndName.name);
            },

            onElementMoved: function (event, reference) {
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug(this.nodeIdPrefix + 'tree.onElementSelected(' + reference.path + ')');
                }
                this.onPathMoved(event, reference.path);
            },

            onElementChanged: function (event, reference) {
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug(this.nodeIdPrefix + 'tree.onElementChanged(' + reference.path + ')');
                }
                this.onPathChanged(event, reference.path);
            },

            onElementDeleted: function (event, reference) {
                if (this.log.getLevel() <= log.levels.DEBUG) {
                    this.log.debug(this.nodeIdPrefix + 'tree.onElementDeleted(' + reference.path + ')');
                }
                this.onPathDeleted(event, reference.path);
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
            }
        });

        tree.AssetsTree = tree.ContentTree.extend({

            nodeIdPrefix: 'PA_',

            initialize: function (options) {
                var p = pages.const.profile.assets.tree;
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
                tree.ContentTree.prototype.initialize.apply(this, [options]);
                var e = pages.const.event;
                $(document).on(e.content.inserted + '.' + id, _.bind(this.onContentInserted, this));
                $(document).on(e.content.changed + '.' + id, _.bind(this.onContentChanged, this));
                $(document).on(e.content.deleted + '.' + id, _.bind(this.onContentDeleted, this));
                $(document).on(e.content.moved + '.' + id, _.bind(this.onContentMoved, this));
                $(document).on(e.ready + '.' + id, _.bind(this.onReady, this));
            },

            initializeFilter: function () {
                var p = pages.const.profile.assets.tree;
                this.filter = pages.profile.get(p.aspect, p.filter, undefined);
            },

            dataUrlForPath: function (path) {
                var params = this.filter ? '?filter=' + this.filter : '';
                return '/bin/cpm/pages/assets.assetTree.json' + path + params;
            },

            onReady: function () {
                var p = pages.const.profile.assets.tree;
                var path = pages.profile.get(p.aspect, p.path, '');
                if (path) {
                    this.selectNode(path);
                }
            },

            onNodeSelected: function (path, node) {
                var p = pages.const.profile.assets.tree;
                core.components.Tree.prototype.onNodeSelected.apply(this, [path, node]);
                pages.profile.set(p.aspect, p.path, path);
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
                this.$actions = this.$('.' + tree.const.actions.css);
                this.treePanelId = this.tree.nodeIdPrefix + 'treePanel';
            },

            onNodeSelected: function (event, path) {
                if (this.path !== path) {
                    if (pages.log.getLevel() <= log.levels.DEBUG) {
                        pages.log.debug(this.treePanelId + '.onNodeSelected(' + path + ')');
                    }
                    this.path = path;
                    if (!path) {
                        this.doSelectDefaultNode();
                    } else {
                        this.setNodeActions();
                    }
                }
            },

            doSelectDefaultNode: function () {
                var busy = this.tree.busy;
                this.tree.busy = true;
                this.selectDefaultNode();
                this.tree.busy = busy;
            },

            setNodeActions: function () {
                if (this.actions) {
                    this.actions.dispose();
                    this.actions = undefined;
                }
                var node = this.tree.getSelectedTreeNode();
                if (node.original.path) {
                    if (this.$actions.length > 0) {
                        // load tree actions for the selected resource (if actions are used in the tree)
                        core.ajaxGet(tree.const.actions.url + node.original.path, {},
                            _.bind(function (data) {
                                this.$actions.html(data);
                                this.actions = core.getWidget(this.$actions[0],
                                    '.' + pages.toolbars.const.editToolbarClass, pages.toolbars.EditToolbar);
                                this.actions.data = {
                                    path: node.original.path
                                };
                            }, this));
                    }
                }
                this.showPreview(node);
            },

            showPreview: function (node) {
            }
        });

        tree.ContentTreePanel = tree.ToolsTreePanel.extend({

            initialize: function (options) {
                tree.ToolsTreePanel.prototype.initialize.apply(this, [options]);
                this.$treePanel = this.$('.' + tree.const.treePanelClass);
                this.$treePanelPreview = this.$('.' + tree.const.treePanelPreviewClass);
                this.searchPanel = core.getWidget(this.el, '.' + pages.search.const.css.base + pages.search.const.css._panel,
                    pages.search.SearchPanel);
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
                            this.$treePanelPreview.removeClass('hidden');
                            break;
                        case 'search':
                            this.$viewToggle.addClass('active');
                            this.$treePanelPreview.addClass('hidden');
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
            }
        });

        tree.PageTreePanel = tree.ContentTreePanel.extend({

            initialize: function (options) {
                var e = pages.const.event;
                var p = pages.const.profile.page.tree;
                this.tree = core.getWidget(this.el, '.' + tree.const.treeClass, tree.PageTree);
                this.tree.panel = this;
                tree.ContentTreePanel.prototype.initialize.apply(this, [options]);
                this.$viewToggle = this.$('.' + tree.const.pages.css.base + '_toggle-view');
                this.$viewToggle.click(_.bind(this.toggleView, this));
                this.setView(pages.profile.get(p.aspect, p.view, 'tree'));
                this.selectFilter(pages.profile.get(p.aspect, p.filter, undefined));
                this.$('.' + tree.const.pages.css.base + '_filter-value a').click(_.bind(this.setFilter, this));
                this.tree.$el.on('node:selected.' + this.treePanelId, _.bind(this.onNodeSelected, this));
                $(document).on(e.site.selected + '.' + this.treePanelId, _.bind(this.onSiteSelected, this));
                $(document).on(e.scope.changed + '.' + this.treePanelId, _.bind(this.onScopeChanged, this));
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
                this.$('.' + tree.const.pages.css.base + '_filter-value').removeClass('active');
                if (filter) {
                    this.$('.' + tree.const.pages.css.base + '_filter-value[data-value="' + filter + '"]')
                        .addClass('active');
                }
            }
        });

        tree.AssetsTreePanel = tree.ContentTreePanel.extend({

            initialize: function (options) {
                var e = pages.const.event;
                var p = pages.const.profile.assets.tree;
                this.tree = core.getWidget(this.el, '.' + tree.const.treeClass, tree.AssetsTree);
                this.tree.panel = this;
                tree.ContentTreePanel.prototype.initialize.apply(this, [options]);
                this.$viewToggle = this.$('.' + tree.const.assets.css.base + '_toggle-view');
                this.$viewToggle.click(_.bind(this.toggleView, this));
                this.setView(pages.profile.get(p.aspect, p.view, 'tree'));
                this.selectFilter(pages.profile.get(p.aspect, p.filter, undefined));
                this.$('.' + tree.const.assets.css.base + '_filter-value a').click(_.bind(this.setFilter, this));
                this.tree.$el.on('node:selected.' + this.treePanelId, _.bind(this.onNodeSelected, this));
                $(document).on(e.site.selected + '.' + this.treePanelId, _.bind(this.onSiteSelected, this));
                $(document).on(e.scope.changed + '.' + this.treePanelId, _.bind(this.onScopeChanged, this));
            },

            selectDefaultNode: function () {
            },

            setFilter: function (event) {
                event.preventDefault();
                var p = pages.const.profile.assets.tree;
                var $link = $(event.currentTarget);
                var filter = $link.parent().data('value');
                this.tree.setFilter(filter);
                this.selectFilter(filter);
                pages.profile.set(p.aspect, p.filter, filter);
            },

            selectFilter: function (filter) {
                this.$('.' + tree.const.assets.css.base + '_filter-value').removeClass('active');
                if (filter) {
                    this.$('.' + tree.const.assets.css.base + '_filter-value[data-value="' + filter + '"]')
                        .addClass('active');
                }
            },

            showPreview: function (node) {
                if (node && node.original.path && node.original.type.indexOf('file') >= 0) {
                    core.ajaxGet(tree.const.tile.url + node.original.path, {},
                        _.bind(function (data) {
                            if (data) {
                                this.$treePanelPreview.html(data);
                                this.$el.addClass('preview-available');
                            } else {
                                this.$el.removeClass('preview-available');
                                this.$treePanelPreview.html('');
                            }
                        }, this));
                } else {
                    this.$el.removeClass('preview-available');
                    this.$treePanelPreview.html('');
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

        tree.pageTreePanel = core.getView('.' + tree.const.pages.css.base, tree.PageTreePanel);
        tree.assetsTreePanel = core.getView('.' + tree.const.assets.css.base, tree.AssetsTreePanel);
        tree.developTreePanel = core.getView('.' + tree.const.develop.css.base, tree.DevelopTreePanel);

    })(window.composum.pages.tree, window.composum.pages, window.core);
})(window);
