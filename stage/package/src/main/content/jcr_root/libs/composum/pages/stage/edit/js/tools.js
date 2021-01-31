(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            css: {
                tabs: {
                    base: 'tabbed-widget',
                    _tab: '_tab'
                },
                standalone: {
                    base: 'composum-pages-stage-edit-sidebar-standalone',
                    _: {
                        buffer: '_buffer',
                        current: '_current'
                    }
                },
                context: {
                    base: 'composum-pages-stage-edit-sidebar-context',
                    _: {}
                }
            },
            tabsClass: 'tabbed-widget_tabs',
            handleClass: 'tabbed-widget_handle',
            linkClass: 'tabbed-widget_link',
            contentClass: 'tabbed-widget_content',
            panelClass: 'tabbed-widget_panel',

            navigationTabs: 'composum-pages-stage-edit-sidebar-navigation',
            standaloneTabs: 'composum-pages-stage-edit-sidebar-standalone',
            navigationContext: 'composum-pages-stage-edit-sidebar-navigation-context',
            standaloneSidebar: 'composum-pages-stage-edit-tools_standalone',
            standaloneStaticUrl: '/bin/cpm/pages/edit.standaloneStaticTools.html',
            standaloneContextUrl: '/bin/cpm/pages/edit.standaloneContextTools.html',
            contextSidebar: 'composum-pages-stage-edit-tools_context',
            contextTabsHook: 'composum-pages-stage-edit-sidebar_content',
            contextTabs: 'composum-pages-stage-edit-sidebar-context',
            contextLoadUrl: '/bin/cpm/pages/edit.contextTools.html',

            navigation: {
                main: {
                    tabs: {
                        key: 'navigation',
                        default: 'tabbed-widget_tab_pagesTree'
                    }
                },
                context: {
                    url: {
                        base: '/libs/composum/pages/stage/edit/sidebar/navigation/context',
                        _site: '.site.html',
                        _general: '.general.html'
                    }
                }
            }
        });

        tools.TabPanel = Backbone.View.extend({

            initialize: function (options) {
                this.$tabs = this.$('.' + tools.const.tabsClass);
                this.$content = this.$('.' + tools.const.contentClass);
                this.initTabs();
            },

            initTabs: function () {
                this.$handles = this.$tabs.find('.' + tools.const.handleClass);
                this.$panels = this.$content.find('.' + tools.const.panelClass);
                this.$tabs.find('.' + tools.const.linkClass).off('click').click(_.bind(this.selectTab, this));
            },

            initialKey: function (key) {
                return undefined;
            },

            keyChanged: function (key, $panel, remember) {
            },

            findTabKey: function ($element) {
                var $panel = $element.closest('.' + tools.const.panelClass);
                var classes = $panel.attr('class');
                if (classes) {
                    var keys = classes.split(/\s+/);
                    for (var i = 0; i < keys.length; i++) {
                        var $handle = this.$tabs.find('[data-tab="' + keys[i] + '"]');
                        if ($handle.length === 1) {
                            return keys[i];
                        }
                    }
                }
                return undefined;
            },

            lockTabs: function (lock) {
                this.locked = lock;
                this.showLockState();
            },

            showLockState: function () {
                if (this.locked) {
                    this.$el.addClass('locked');
                } else {
                    this.$el.removeClass('locked');
                }
            },

            getTabIndex: function (key) {
                var result = -1;
                if (key) {
                    this.$handles.each(function (index) {
                        if ($(this).data('tab') === key) {
                            result = index;
                        }
                    });
                }
                return result;
            },

            getTabPanel: function (key) {
                return this.$content.find('.' + tools.const.panelClass + '.' + key);
            },

            appendTab: function ($handle, $panel) {
                $handle.appendTo(this.$tabs);
                $panel.appendTo(this.$content);
                this.initTabs();
            },

            prependTab: function ($handle, $panel) {
                $handle.prependTo(this.$tabs);
                $panel.prependTo(this.$content);
                this.initTabs();
            },

            removeTab: function (key) {
                if (!this.isLocked) {
                    var index = this.getTabIndex(key);
                    if (index >= 0) {
                        if (this.currentKey === key) {
                            var next = (index >= 0 && index < this.$handles.length - 1) ? index + 1 : index - 1;
                            if (next >= 0) {
                                this.selectTab(undefined, $(this.$handles[next]).data('tab'), undefined, true);
                            }
                        }
                        var $handle = $(this.$handles[index]);
                        var $panel = $(this.$panels[index]);
                        delete this.$handles[index];
                        delete this.$panels[index];
                        $handle.remove();
                        $panel.remove();
                        return true;
                    }
                }
                return false;
            },

            activateTab: function (shortKey, onlyIfKnown) {
                var c = tools.const.css.tabs;
                return this.selectTab(undefined, c.base + c._tab + '_' + shortKey, onlyIfKnown);
            },

            selectTab: function (event, key, onlyIfKnown, suppressRemember, force) {
                if (!this.locked) {
                    var $handle = undefined;
                    if (!key) {
                        if (event) {
                            $handle = $(event.currentTarget).closest('.' + tools.const.handleClass);
                            key = $handle.data('tab');
                        }
                    } else {
                        $handle = this.$tabs.find('[data-tab="' + key + '"]');
                    }
                    if ((!$handle || $handle.length < 1) && !onlyIfKnown) {
                        $handle = $(this.$handles[0]);
                        key = $handle.data('tab');
                    }
                    if ($handle && $handle.length > 0) {
                        if (this.currentKey !== key || force) {
                            if (this.currentKey) {
                                var $prevPanel = this.getTabPanel(this.currentKey);
                                var $prevHandle = this.$tabs.find('[data-tab="' + this.currentKey + '"]');
                                this.beforeHideTab(this.currentKey);
                                $prevHandle.removeClass('active');
                                $prevPanel.removeClass('active');
                            }
                            this.currentKey = key;
                            if (this.currentKey) {
                                var $nextPanel = this.getTabPanel(this.currentKey);
                                $nextPanel.addClass('active');
                                $handle.addClass('active');
                                this.stackHandles();
                                this.keyChanged(key, $nextPanel, !suppressRemember);
                            }
                        }
                        return true;
                    }
                }
                return false;
            },

            beforeHideTab: function (key) {
            },

            stackHandles: function () {
                var count = this.$handles.length;
                var zindex = parseInt(this.$tabs.css('z-index')) || 1;
                var increment = true;
                this.$handles.each(function () {
                    var $handle = $(this);
                    if ($handle.hasClass('active')) {
                        zindex += count;
                        increment = false;
                    }
                    $handle.css('z-index', zindex);
                    if (increment) {
                        zindex++;
                    } else {
                        zindex--;
                    }
                });
            }
        });

        tools.NavigationTabs = tools.TabPanel.extend({

            initialize: function (options) {
                tools.TabPanel.prototype.initialize.apply(this, [options]);
                var e = pages.const.event;
                $(document).on(e.path.select + '.Navigation', _.bind(this.selectPath, this));
                $(document).on(e.pages.open + '.Navigation', _.bind(this.onOpen, this));
                $(document).on(e.pages.ready + '.Navigation', _.bind(this.ready, this));
            },

            ready: function () {
                this.selectTab(undefined, this.initialKey());
            },

            initialKey: function (key) {
                var d = tools.const.navigation.main;
                if (key) {
                    pages.profile.set('tabs.' + pages.current.mode, d.tabs.key, key);
                } else {
                    key = pages.profile.get('tabs.' + pages.current.mode, d.tabs.key, d.tabs.default);
                }
                return key;
            },

            keyChanged: function (key, $panel, remember) {
                var d = tools.const.navigation.main;
                if ($panel && $panel.length === 1) {
                    var $content = $panel.children();
                    if ($content.length === 1) {
                        var view = $content[0].view;
                        if (view && _.isFunction(view.onTabSelected)) {
                            view.onTabSelected.call(view);
                        }
                    }
                }
                if (remember) {
                    this.initialKey(key);
                }
            },

            onOpen: function (event, key, pathOrRef) {
                if (key) {
                    var path = pathOrRef && pathOrRef.path ? pathOrRef.path : pathOrRef;
                    if (path) {
                        if (this.activateTab(key, true)) {
                            this.selectPath(event, path);
                        }
                    }
                }
            },

            selectPath: function (event, path, name, type) {
                if (!type) {
                    var ref = new pages.Reference(name, path, type);
                    ref.complete(_.bind(function (ref) {
                        if (ref && ref.path && ref.type) {
                            this.selectPath(event, ref.path, ref.name, ref.type);
                        }
                    }, this));
                    return;
                }
                if (path && type) {
                    pages.log.debug('tools.Navigation.selectPath(' + path + ')');
                    // noinspection FallThroughInSwitchStatementJS
                    switch (type) {
                        case 'siteconfiguration':
                            path = core.getParentPath(path);
                        case 'site':
                            pages.trigger('tools.select.path', pages.const.event.site.select, [path]);
                            break;
                        case 'pagecontent':
                            path = core.getParentPath(path);
                        case 'page':
                            if (path === pages.current.page) {
                                // trigger a 'page select again' to adjust all tools
                                pages.trigger('tools.select.path', pages.const.event.page.selected, [pages.current.page]);
                                pages.trigger('tools.select.path', pages.const.event.element.select, []);
                            } else {
                                pages.trigger('tools.select.path', pages.const.event.page.select, [path]);
                            }
                            break;
                        case 'container':
                        case 'element':
                            pages.editFrame.getPageData(path, _.bind(function (data) {
                                if (data.path !== pages.current.page) {
                                    pages.trigger('tools.select.path', pages.const.event.page.select,
                                        [data.path, undefined,
                                            new pages.Reference(name, path, type)]);
                                } else {
                                    pages.trigger('tools.select.path', pages.const.event.element.select,
                                        [new pages.Reference(name, path, type)]);
                                }
                            }, this));
                            break;
                        default:
                            pages.trigger('tools.select.path', pages.const.event.path.selected, [path]);
                            break;
                    }
                }
            }
        });

        tools.NavigationContext = Backbone.View.extend({

            initialize: function (options) {
                var e = pages.const.event;
                $(document).on(e.site.selected + '.Navigation', _.bind(this.onSiteChanged, this));
                $(document).on(e.site.changed + '.Navigation', _.bind(this.onSiteChanged, this));
                $(document).on(e.content.state + '.Navigation', _.bind(this.onSiteChanged, this));
                $(document).on(e.scope.changed + '.Navigation', _.bind(this.onScopeChanged, this));
            },

            initContent: function (options) {
                this.$changesBadge = this.$('.badge.changes');
                this.$modifiedBadge = this.$('.badge.modified');
                this.$restrictToSite = this.$('.restrict-to-site');
                this.$gotoSite = this.$('.goto-site');
                this.$manageSites = this.$('.manage-sites');
                this.$changesBadge.click(function (event) {
                    tools.contextTabsHook.contextTabs.activateTab('siteChanges');
                });
                this.$modifiedBadge.click(function (event) {
                    tools.contextTabsHook.contextTabs.activateTab('siteModified');
                });
                this.$restrictToSite.click(_.bind(this.toggleScope, this));
                this.$gotoSite.click(_.bind(this.selectSite, this));
                this.$manageSites.click(_.bind(this.manageSites, this));
                this.onScopeChanged();
            },

            onScopeChanged: function () {
                if (pages.getScope() === 'site') {
                    this.$restrictToSite.addClass('active');
                } else {
                    this.$restrictToSite.removeClass('active');
                }
            },

            onSiteChanged: function (event, pathOrRef) {
                var path = pathOrRef && pathOrRef.path ? pathOrRef.path : pathOrRef;
                var u = tools.const.navigation.context.url;
                var url = u.base + (path ? u._site + core.encodePath(path) : u._general);
                core.getHtml(url, undefined, undefined, _.bind(function (data) {
                    if (data.status === 200) {
                        this.sitePath = path;
                        this.$el.html(data.responseText);
                    } else {
                        this.sitePath = undefined;
                        this.$el.html("");
                    }
                    this.initContent();
                }, this));
            },

            toggleScope: function (event) {
                event.preventDefault();
                pages.setScope(pages.getScope() === 'site' ? 'content' : 'site');
                return false;
            },

            selectSite: function (event) {
                event.preventDefault();
                if (this.sitePath) {
                    pages.actions.site.open(event, undefined, this.sitePath);
                }
                return false;
            },

            manageSites: function (event) {
                event.preventDefault();
                pages.actions.site.manage(event);
                return false;
            }
        });

        tools.ContextTabs = tools.TabPanel.extend({

            initialize: function (options) {
                this.tools = {};
                this.componentType = this.$el.data('component-type');
                tools.TabPanel.prototype.initialize.apply(this, [options]);
            },

            /**
             * @override
             * @param key the tab key
             * @return {boolean} 'true' if removal done
             */
            removeTab: function (key) {
                if (!this.isLocked && this.getTabIndex(key) >= 0) {
                    this.beforeClose(key);
                    if (tools.TabPanel.prototype.removeTab.apply(this, [key])) {
                        delete this.tools[key];
                        return true;
                    }
                }
                return false;
            },

            clearTools: function () {
                var me = this;
                var tools = this.tools;
                _.each(_.keys(tools), function (key) {
                    var view = tools[key];
                    if (view && !view.$el.hasClass('static-tool')) {
                        me.removeTab(key);
                    }
                });
            },

            initTools: function () {
                this.clearTools();
                this.runInitializers();
                this.selectTab(undefined, this.initialKey());
            },

            runInitializers: function () {
                var contextToolsInitializers = pages.contextTools.getInitializers();
                for (var i = 0; i < contextToolsInitializers.length; i++) {
                    // each view returned by an initializer is triggered on tab selection
                    var view = contextToolsInitializers[i](this);
                    if (view) {
                        var key = this.findTabKey(view.$el);
                        if (key) {
                            this.tools[key] = view;
                        }
                    }
                }
            },

            initialKey: function (key) {
                if (key) {
                    pages.profile.set('tabs', 'context.' + this.componentType, key);
                } else {
                    key = pages.profile.get('tabs', 'context.' + this.componentType, undefined);
                }
                return key;
            },

            keyChanged: function (key, $panel, remember) {
                // if there is a view registered for the key trigger the tab selection
                var view = this.tools[key];
                if (view) {
                    if (_.isFunction(view.onTabSelected)) {
                        try {
                            view.onTabSelected.call(view);
                        } catch (ex) {
                            pages.contextTools.log.error('exception: onTabSelected[' + key + ']\n', ex);
                        }
                    }
                }
                if (remember) {
                    this.initialKey(key);
                }
            },

            reloadPage: function (parameters) {
                pages.editFrame.reloadPage(parameters);
            },

            beforeHideTab: function (key) {
                var view = this.tools[key];
                if (view && _.isFunction(view.beforeHideTab)) {
                    view.beforeHideTab.call(view);
                }
            },

            beforeClose: function (key) {
                var tools = this.tools;
                _.each(key ? [key] : _.keys(tools), function (key) {
                    var view = tools[key];
                    if (view && _.isFunction(view.beforeClose)) {
                        try {
                            view.beforeClose.call(view);
                        } catch (ex) {
                            pages.contextTools.log.error('exception: beforeClose[' + key + ']\n', ex);
                        }
                    }
                });
            }
        });

        tools.StandaloneTabs = tools.ContextTabs.extend({

            /**
             * @override
             */
            initialKey: function (key) {
                if (key) {
                    pages.profile.set('tabs', 'standalone.' + this.componentType, key);
                } else {
                    key = pages.profile.get('tabs', 'standalone.' + this.componentType, undefined);
                }
                return key;
            }
        });

        tools.ContextTabsHook = Backbone.View.extend({

            initialize: function (options) {
                var e = pages.const.event;
                $(document).on(e.page.selected + '.Context', _.bind(this.onPageSelected, this));
                $(document).on(e.element.selected + '.Context', _.bind(this.onElementSelected, this));
            },

            onPageSelected: function (event, refOrPath, parameters) {
                if (pages.contextTools.log.getLevel() <= log.levels.DEBUG) {
                    pages.contextTools.log.debug('tools.Context.onPageSelected(' + refOrPath + ')');
                }
                this.changeTools(refOrPath && refOrPath.path
                    ? refOrPath : new pages.Reference(undefined, refOrPath, undefined));
            },

            onElementSelected: function (event, refOrPath) {
                if (pages.contextTools.log.getLevel() <= log.levels.DEBUG) {
                    pages.contextTools.log.debug('tools.Context.onElementSelected(' + refOrPath + ')');
                }
                this.changeTools(refOrPath && refOrPath.path
                    ? refOrPath : new pages.Reference(undefined, refOrPath, undefined));
            },

            getTabs: function () {
                var c = tools.const.css.context;
                return core.getWidget(this.el, '.' + c.base, tools.ContextTabs);
            },

            getLoadUrl: function () {
                return tools.const.contextLoadUrl;
            },

            changeTools: function (reference) {
                var path = reference ? reference.path : undefined;
                if (!path) {
                    // default view...
                    path = pages.current.page;
                    reference = new pages.Reference(undefined, path);
                }
                if (path) {
                    var params = {
                        'pages.view': pages.current.mode,
                        'pages.locale': pages.getLocale()
                    };
                    if (reference && reference.type) {
                        params.type = reference.type;
                    }
                    var url = this.getLoadUrl() + core.encodePath(path);
                    core.ajaxGet(url, {data: params}, _.bind(function (data) {
                        this.closeCurrent();
                        this.loadTools(reference, data);
                    }, this));
                } else {
                    // nothing selected (!?)...
                    this.closeCurrent();
                    this.$el.html('');
                }
            },

            loadTools: function (reference, data) {
                this.$el.html(data);
                this.contextTabs = this.getTabs();
                this.contextTabs.reference = reference;
                this.contextTabs.initTools();
            },

            closeCurrent: function () {
                if (this.contextTabs) {
                    this.contextTabs.beforeClose.call(this.contextTabs);
                }
                this.contextTabs = undefined;
            }
        });

        tools.StandaloneTabsHook = tools.ContextTabsHook.extend({

            /**
             * @override
             */
            getTabs: function () {
                var c = tools.const.css.standalone;
                return core.getWidget(this.el, '.' + c.base, tools.StandaloneTabs);
            },

            /**
             * @override
             */
            getLoadUrl: function () {
                return this.staticInitialized ? tools.const.standaloneContextUrl : tools.const.standaloneStaticUrl;
            },

            /**
             * @override
             */
            loadTools: function (reference, data) {
                var c = tools.const.css.standalone;
                if (!this.staticInitialized) {
                    this.staticInitialized = true;
                    tools.ContextTabsHook.prototype.loadTools.apply(this, [reference, data]);
                    this.$current = this.$('.' + c.base + c._.current);
                    this.$buffer = this.$('.' + c.base + c._.buffer);
                    var e = pages.const.event;
                    pages.trigger('sidebar.ready', e.pages.ready);
                } else {
                    var me = this;
                    this.$buffer.html(data);
                    this.contextTabs.reference = reference;
                    this.contextTabs.componentType = this.$buffer.find('.' + c.base).data('component-type');
                    this.$buffer.find('.' + tools.const.tabsClass + ' .' + tools.const.handleClass).each(function () {
                        var $handle = $(this);
                        var key = $handle.data('tab');
                        var $panel = me.$buffer.find('.' + tools.const.contentClass
                            + ' .' + tools.const.panelClass + '.' + key);
                        if ($panel.length > 0) {
                            me.contextTabs.prependTab($handle, $panel);
                        }
                    });
                    this.$current.html(this.$buffer.find('.' + c.base + c._.current)[0].innerHTML);
                    this.$buffer.html('');
                    this.contextTabs.runInitializers();
                }
                this.contextTabs.selectTab(undefined, this.contextTabs.initialKey(), undefined, true, true);
            },

            /**
             * @override
             */
            closeCurrent: function () {
                if (this.contextTabs) {
                    this.contextTabs.clearTools();
                }
            }
        });

        tools.navigationTabs = core.getView('.' + tools.const.navigationTabs, tools.NavigationTabs);
        tools.navigationContext = core.getView('.' + tools.const.navigationContext, tools.NavigationContext);
        tools.contextTabsHook = core.getView('.' + tools.const.contextSidebar
            + ' .' + tools.const.contextTabsHook, tools.ContextTabsHook);
        if (!tools.contextTabsHook) {
            tools.contextTabsHook = core.getView('.' + tools.const.standaloneSidebar
                + ' .' + tools.const.contextTabsHook, tools.StandaloneTabsHook);
        }

    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
