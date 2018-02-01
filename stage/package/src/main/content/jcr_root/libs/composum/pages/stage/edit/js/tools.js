(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            tabsCssBase: 'tabbed-widget',
            tabsClass: 'tabbed-widget_tabs',
            handleClass: 'tabbed-widget_handle',
            linkClass: 'tabbed-widget_link',
            contentClass: 'tabbed-widget_content',
            panelClass: 'tabbed-widget_panel',

            navigationTabs: 'composum-pages-stage-edit-sidebar-navigation',
            navigationContext: 'composum-pages-stage-edit-sidebar-navigation-context',
            contextSidebar: 'composum-pages-stage-edit-tools_context',
            contextTabsHook: 'composum-pages-stage-edit-sidebar_content',
            contextTabs: 'composum-pages-stage-edit-sidebar-context',
            contextLoadUrl: '/bin/cpm/pages/edit.contextTools.html',

            navigation: {
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
                this.$handles = this.$tabs.find('.' + tools.const.handleClass);
                this.$content = this.$('.' + tools.const.contentClass);
                this.$panels = this.$content.find('.' + tools.const.panelClass);
                this.$tabs.find('.' + tools.const.linkClass).click(_.bind(this.selectTab, this));
            },

            initialKey: function () {
                return undefined;
            },

            keyChanged: function () {
            },

            findTabKey: function ($element) {
                var $panel = $element.closest('.' + tools.const.panelClass);
                var classes = $panel.attr('class').split(/\s+/);
                for (var i = 0; i < classes.length; i++) {
                    var $handle = this.$tabs.find('[data-tab="' + classes[i] + '"]');
                    if ($handle.length === 1) {
                        return classes[i];
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

            getTabPanel: function (key) {
                return this.$content.find('.' + tools.const.panelClass + '.' + key);
            },

            selectTab: function (event, key) {
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
                    if (!$handle || $handle.length < 1) {
                        $handle = $(this.$handles[0]);
                        key = $handle.data('tab');
                    }
                    if (this.currentKey !== key) {
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
                            this.keyChanged(key, $nextPanel);
                        }
                    }
                }
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
                this.selectTab(undefined, this.initialKey());
                $(document).on('path:select.Navigation', _.bind(this.selectPath, this));
            },

            initialKey: function () {
                return pages.profile.get('tabs', 'navigation', undefined);
            },

            keyChanged: function (key) {
                pages.profile.set('tabs', 'navigation', key)
            },

            selectPath: function (event, path, node) {
                if (path && node) {
                    console.log('tools.Navigation.selectPath(' + path + ')');
                    // noinspection FallThroughInSwitchStatementJS
                    switch (node.original.type) {
                        case 'siteconfiguration':
                            path = core.getParentPath(path);
                        case 'site':
                            $(document).trigger("site:select", [path]);
                            break;
                        case 'pagecontent':
                            path = core.getParentPath(path);
                        case 'page':
                            if (path === pages.current.page) {
                                $(document).trigger("component:select", []);
                            } else {
                                $(document).trigger("page:select", [path]);
                            }
                            break;
                        case 'container':
                        case 'element':
                            pages.getPageData(path, _.bind(function (data) {
                                if (data.path !== pages.current.page) {
                                    pages.editFrame.selectOnLoad = {
                                        name: node.original.name,
                                        path: path,
                                        type: node.original.type
                                    };
                                    $(document).trigger("page:select", [data.path]);
                                } else {
                                    $(document).trigger("component:select",
                                        [node.original.name, path, node.original.type]);
                                }
                            }, this));
                            break;
                    }
                    $(document).trigger("path:selected", [path]);
                }
            }
        });

        tools.NavigationContext = Backbone.View.extend({

            initialize: function (options) {
                $(document).on('site:selected.Navigation', _.bind(this.onSiteChanges, this));
                $(document).on('site:changed.Navigation', _.bind(this.onSiteChanges, this));
            },

            initContent: function (options) {
                this.$gotoSite = this.$('.goto-site');
                this.$manageSites = this.$('.manage-sites');
                this.$gotoSite.click(_.bind(this.selectSite, this));
                this.$manageSites.click(_.bind(this.manageSites, this));
            },

            onSiteChanges: function (event, path) {
                var u = tools.const.navigation.context.url;
                var url = u.base + (path ? u._site + path : u._general);
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

            selectSite: function (event) {
                if (this.sitePath) {
                    $(document).trigger("site:select", [this.sitePath]);
                }
            },

            manageSites: function (event) {
            }
        });

        tools.ContextTabs = tools.TabPanel.extend({

            initialize: function (options) {
                this.tools = {};
                this.componentType = this.$el.data('component-type');
                tools.TabPanel.prototype.initialize.apply(this, [options]);
            },

            initTools: function () {
                this.tools = {};
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
                this.selectTab(undefined, this.initialKey());
            },

            initialKey: function () {
                return pages.profile.get('tabs', 'context.' + this.componentType, undefined);
            },

            keyChanged: function (key, $panel) {
                // if there is a view registered for the key trigger the tab selection
                var view = this.tools[key];
                if (view) {
                    if (_.isFunction(view.onTabSelected)) {
                        view.onTabSelected.call(view);
                    }
                }
                pages.profile.set('tabs', 'context.' + this.componentType, key)
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

            beforeClose: function () {
                var tools = this.tools;
                _.each(_.keys(tools), function (key) {
                    var view = tools[key];
                    if (view && _.isFunction(view.beforeClose)) {
                        view.beforeClose.call(view);
                    }
                });
            }
        });

        tools.ContextTabsHook = Backbone.View.extend({

            initialize: function (options) {
                $(document).on('page:selected.Context', _.bind(this.onPageSelected, this));
                $(document).on('component:selected.Context', _.bind(this.onComponentSelected, this));
            },

            onPageSelected: function (event, path, parameters) {
                console.log('tools.Context.onPageSelected(' + path + ')');
                this.changeTools(undefined, path, undefined);
            },

            onComponentSelected: function (event, name, path, type) {
                console.log('tools.Context.onComponentSelected(' + path + ')');
                this.changeTools(name, path, type);
            },

            changeTools: function (name, path, type) {
                if (!path) {
                    // default view...
                    path = pages.current.page;
                }
                if (path) {
                    core.ajaxGet(tools.const.contextLoadUrl + path + '?pages.mode=' + pages.current.mode, {
                            data: {
                                type: type
                            }
                        },
                        _.bind(function (data) {
                            this.closeCurrent();
                            this.$el.html(data);
                            this.contextTabs = core.getWidget(this.el,
                                '.' + tools.const.contextTabs, tools.ContextTabs);
                            this.contextTabs.data = {
                                name: name,
                                path: path,
                                type: type
                            };
                            this.contextTabs.initTools();
                        }, this));
                } else {
                    // nothing selected (!?)...
                    this.closeCurrent();
                    this.$el.html('');
                }
            },

            closeCurrent: function () {
                if (this.contextTabs) {
                    this.contextTabs.beforeClose.call(this.contextTabs);
                }
                this.contextTabs = undefined;
            }
        });

        tools.navigationTabs = core.getView('.' + tools.const.navigationTabs, tools.NavigationTabs);
        tools.navigationContext = core.getView('.' + tools.const.navigationContext, tools.NavigationContext);
        tools.contextTabsHook = core.getView('.' + tools.const.contextSidebar
            + ' .' + tools.const.contextTabsHook, tools.ContextTabsHook);

    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
