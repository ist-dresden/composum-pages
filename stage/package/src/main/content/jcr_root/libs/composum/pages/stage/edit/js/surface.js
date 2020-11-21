(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.surface = window.composum.pages.surface || {};

    (function (surface, pages, core) {
        'use strict';

        surface.const = _.extend(surface.const || {}, {
            toolsPanelClass: 'composum-pages-stage-edit-tools',
            sidebarClass: 'composum-pages-stage-edit-sidebar',
            logoClass: 'composum-pages-stage-edit-sidebar-logo',
            navigationClass: 'composum-pages-stage-edit-tools_navigation',
            standaloneClass: 'composum-pages-stage-edit-tools_standalone',
            contextToolsClass: 'composum-pages-stage-edit-tools_context',
            handleClass: 'composum-pages-stage-edit-sidebar_handle',
            handleIconClass: 'composum-pages-stage-edit-sidebar_handle-icon',
            modeIconClass: 'composum-pages-stage-edit-sidebar_mode-icon',
            modeOverlapClass: 'composum-pages-stage-edit-sidebar_overlap',
            versionPrimaryClass: 'composum-pages-stage-version-frame_primary',
            versionSecondaryClass: 'composum-pages-stage-version-frame_secondary',
            css: {
                tools: {
                    base: 'composum-pages-stage-edit-tools',
                    _: {
                        context: '_context'
                    }
                }
            }
        });

        surface.Surface = Backbone.View.extend({

            initialize: function (options) {
                this.$versionPrimary = $('.' + surface.const.versionPrimaryClass);
                this.$versionSecondary = $('.' + surface.const.versionSecondaryClass);
                window.addEventListener('resize', _.bind(function () {
                    this.bodySync();
                }, this));
                this.bodySync();
            },

            bodySyncOn: function () {
                if (!this.$body) {
                    this.$body = $('body');
                }
            },

            bodySyncOff: function () {
                if (this.$body) {
                    this.$body.css('margin-left', 0);
                    this.$body.css('width', '100%');
                    this.$versionPrimary.css('left', 0);
                    this.$versionPrimary.css('width', '100%');
                    this.$versionSecondary.css('left', 0);
                    this.$versionSecondary.css('width', '100%');
                    this.width = this.$body.width();
                    this.$body = undefined;
                }
                this.width = $('body').width();
                $(document).trigger("body:size", [this, this.width]);
            },

            bodySync: function (sidebar) {
                if (this.isSyncedSidebar(surface.navigation) ||
                    this.isSyncedSidebar(surface.standalone) ||
                    this.isSyncedSidebar(surface.contextTools)) {

                    if (!this.$body || !sidebar ||
                        this.isSyncedSidebar(surface.navigation, sidebar) ||
                        this.isSyncedSidebar(surface.standalone, sidebar) ||
                        this.isSyncedSidebar(surface.contextTools, sidebar)) {

                        this.bodySyncOn();
                        var versionsVisible = pages.versionsVisible();
                        var margin = 0;
                        var width = window.innerWidth;
                        var navWidth;
                        if (surface.navigation && (!surface.navigation.profile.overlap || versionsVisible)) {
                            navWidth = 0;
                            if (!versionsVisible) {
                                navWidth = surface.navigation.$el.width();
                            }
                            margin += navWidth;
                            width -= navWidth;
                        }
                        if (surface.standalone && (!surface.standalone.profile.overlap || versionsVisible)) {
                            navWidth = 0;
                            if (!versionsVisible) {
                                navWidth = surface.standalone.$el.width();
                            }
                            margin += navWidth;
                            width -= navWidth;
                        }
                        if (surface.contextTools && !surface.contextTools.profile.overlap) {
                            var contextWidth = surface.contextTools.$el.width();
                            width -= contextWidth;
                        }
                        this.width = width = Math.round(width);
                        this.$body.css('margin-left', margin + 'px');
                        this.$body.css('width', width + 'px');
                        this.$versionPrimary.css('left', margin + 'px');
                        this.$versionPrimary.css('width', width + 'px');
                        this.$versionSecondary.css('left', margin + 'px');
                        this.$versionSecondary.css('width', width + 'px');

                    } else {
                        this.width = $('body').width();
                    }
                    $(document).trigger("body:size", [this, this.width]);
                } else {
                    this.bodySyncOff();
                }
            },

            isSyncedSidebar: function (sidebar, target) {
                return sidebar && sidebar.profile && !sidebar.profile.overlap && (!target || target === sidebar);
            },

            enter: function (handle) {
                if (this.handle !== handle) {
                    this.leave();
                    this.handle = handle;
                    if (this.handle) {
                        this.$el.css('width', '100%');
                        this.$el.on('touchmove.surface mousemove.surface', _.bind(this.mouseMove, this));
                        this.$el.on('touchend.surface touchleave.surface touchcancel.surface mouseup.surface mouseleave.surface', _.bind(this.mouseUp, this));
                    }
                }
            },

            leave: function () {
                if (this.handle) {
                    this.$el.off('touchend.surface touchleave.surface touchcancel.surface mouseup.surface mouseleave.surface');
                    this.$el.off('touchmove.surface mousemove.surface');
                    this.handle = undefined;
                    this.$el.css('width', '0');
                }
            },

            mouseMove: function (event) {
                if (this.handle) {
                    if (event) {
                        event.preventDefault();
                    }
                    this.handle.move(event);
                    return false;
                }
            },

            mouseUp: function (event) {
                if (this.handle) {
                    if (event) {
                        event.preventDefault();
                    }
                    this.handle.save();
                    this.leave();
                    return false;
                }
            }
        });

        surface.SurfaceHandle = Backbone.View.extend({

            initialize: function (options) {
                this.$handle = this.getHandle();
                this.$handle.on('touchstart.handle mousedown.handle', _.bind(this.mouseDown, this));
            },

            getHandle: function () {
                return this.$el;
            },

            canStart: function () {
                return true;
            },

            getPosition: function (event) {
                var position = {
                    x: event.clientX,
                    y: event.clientY
                };
                if (!position.x) {
                    if (event.originalEvent) {
                        event = event.originalEvent;
                    }
                    if (event.changedTouches) {
                        var touch = event.changedTouches[0];
                        position.x = parseInt(touch.clientX);
                        position.y = parseInt(touch.clientY);
                    }
                }
                return position;
            },

            getMove: function (event, start) {
                var position = this.getPosition(event);
                return {
                    x: position.x - start.x,
                    y: position.y - start.y
                };
            },

            mouseOn: function () {
                this.$handle.on('touchend.handle mouseup.handle', _.bind(this.mouseUp, this));
                this.$handle.on('touchmove.handle mousemove.handle', _.bind(this.mouseMove, this));
            },

            mouseOff: function () {
                this.$handle.off('touchmove.handle mousemove.handle');
                this.$handle.off('touchend.handle mouseup.handle');
            },

            mouseDown: function (event) {
                if (event) {
                    event.preventDefault();
                }
                if (this.canStart()) {
                    this.start = this.moveStart(event);
                    this.mouseOn();
                } else {
                    this.$handle.on('touchend.handle mouseup.handle', _.bind(this.mouseUp, this));
                }
                return false;
            },

            mouseMove: function (event) {
                if (event) {
                    event.preventDefault();
                }
                if (this.start) {
                    var move = this.getMove(event, this.start);
                    if (Math.abs(move.x) > 5 || Math.abs(move.y) > 5) {
                        surface.surface.enter(this);
                        this.mouseOff();
                    }
                }
                return false;
            },

            mouseUp: function (event) {
                if (event) {
                    event.preventDefault();
                }
                this.mouseOff();
                this.onClick();
                return false;
            }
        });

        surface.SidebarHandle = surface.SurfaceHandle.extend({

            initialize: function (options) {
                surface.SurfaceHandle.prototype.initialize.apply(this, [options]);
                this.$mode = this.$('.' + surface.const.modeIconClass);
                this.$mode.on('click.handle', _.bind(this.toggleMode, this));
            },

            getHandle: function () {
                return this.$('.' + surface.const.handleIconClass);
            },

            canStart: function () {
                return this.sidebar.profile && this.sidebar.profile.open;
            },

            moveStart: function (event) {
                return _.extend(this.getPosition(event), {
                    width: this.sidebar.$el.width(),
                    handle: this.$el.position().top
                });
            },

            adjustTop: function (top) {
                return Math.max(Math.min(top, this.sidebar.$el.height() - this.$el.height() - 20), 20);
            },

            move: function (event) {
                var move = this.getMove(event, this.start);
                var width = Math.max(this.start.width + (move.x * this.sidebar.sizeDirection()), 320);
                this.sidebar.$el.css('width', width + 'px');
                this.$el.css('top', this.adjustTop(this.start.handle + move.y) + 'px');
                surface.surface.bodySync(this.sidebar);
                this.sidebar.onResize();
            },

            save: function () {
                this.sidebar.profile.width = this.sidebar.$el.width();
                this.sidebar.profile.handle = Math.round((this.$el.position().top * 100) / (this.sidebar.$el.height() - this.$el.height()));
                this.sidebar.saveProfile();
            },

            toggleMode: function () {
                if (this.sidebar.profile) {
                    this.sidebar.profile.overlap = !this.sidebar.profile.overlap;
                    this.sidebar.saveProfile();
                    this.showMode();
                    surface.surface.bodySync();
                }
            },

            showMode: function () {
                if (this.sidebar.profile) {
                    if (this.sidebar.profile.overlap) {
                        this.sidebar.$el.addClass(surface.const.modeOverlapClass);
                    } else {
                        this.sidebar.$el.removeClass(surface.const.modeOverlapClass);
                    }
                }
            },

            onClick: function (event) {
                if (event) {
                    event.preventDefault();
                }
                this.sidebar.toggleView();
                return false;
            }
        });

        surface.Sidebar = Backbone.View.extend({

            initialize: function (options) {
                this.$tools = this.$el.closest('.' + surface.const.toolsPanelClass);
                this.$sidebar = this.$('.' + surface.const.sidebarClass);
                this.handle = core.getWidget(this.el, '.' + surface.const.handleClass, surface.SidebarHandle);
                this.handle.sidebar = this;
                this.loadProfile();
                this.initView();
            },

            initView: function () {
                if (this.profile) {
                    this.$sidebar.removeClass(surface.const.sidebarClass + '_open');
                    this.$sidebar.removeClass(surface.const.sidebarClass + '_closed');
                    this.$el.css('width', this.profile.open ? this.profile.width + 'px' : 0);
                    this.$sidebar.addClass(surface.const.sidebarClass + (this.profile.open ? '_open' : '_closed'));
                    this.handle.$el.css('top', this.handle.adjustTop(this.profile.handle * (this.$el.height() - this.handle.$el.height()) / 100) + 'px');
                    this.handle.showMode();
                }
            },

            toggleView: function (event) {
                if (event) {
                    event.preventDefault();
                }
                if (this.profile) {
                    if (this.profile.open) {
                        this.$sidebar.removeClass(surface.const.sidebarClass + '_open');
                        this.$el.css('width', 0);
                        this.$sidebar.addClass(surface.const.sidebarClass + '_closed');
                    } else {
                        this.$sidebar.removeClass(surface.const.sidebarClass + '_closed');
                        this.$el.css('width', this.profile.width + 'px');
                        this.$sidebar.addClass(surface.const.sidebarClass + '_open');
                    }
                    this.profile.open = !this.profile.open;
                    surface.surface.bodySync(this);
                    this.saveProfile();
                }
            },

            loadProfile: function () {
                this.profile = {
                    open: pages.profile.get(this.profileAspect(), 'open', true),
                    width: pages.profile.get(this.profileAspect(), 'width', 300),
                    handle: pages.profile.get(this.profileAspect(), 'handle', 30),
                    overlap: pages.profile.get(this.profileAspect(), 'overlap', true)
                };
            },

            saveProfile: function () {
                if (this.profile) {
                    pages.profile.set(this.profileAspect(), 'overlap', this.profile.overlap);
                    pages.profile.set(this.profileAspect(), 'handle', this.profile.handle);
                    pages.profile.set(this.profileAspect(), 'width', this.profile.width);
                    pages.profile.set(this.profileAspect(), 'open', this.profile.open);
                }
            },

            onResize: function () {
                var width = this.$el.width();
                if (!this.lastWidth || Math.abs(this.lastWidth - width) > 5) {
                    //pages.log.debug('surface.trigger.sidebarResized:' + this.profileAspect() + '(' + this + ',' + width + ')');
                    $(document).trigger("sidebarResized:" + this.profileAspect(), [this, width]);
                }
            }
        });

        surface.Logo = Backbone.View.extend({

            initialize: function (options) {
                this.popover = false;
                this.$link = this.$('.' + surface.const.logoClass + '_link');
                this.$link.click(_.bind(this.initPopover, this));
            },

            initPopover: function (event) {
                event.preventDefault();
                if (!this.popover) {
                    pages.loadFrameContent('/libs/composum/pages/stage/edit/sidebar/logo/popover.html',
                        _.bind(function (content) {
                            this.popover = true;
                            this.$link.popover({
                                placement: 'bottom',
                                animation: false,
                                html: true,
                                sanitize: false,
                                content: content
                            });
                            this.$link.popover('show');
                        }, this));
                }
                return false;
            }
        });

        surface.Navigation = surface.Sidebar.extend({

            initialize: function (options) {
                surface.Sidebar.prototype.initialize.apply(this, [options]);
            },

            profileAspect: function () {
                return 'navigation'
            },

            sizeDirection: function () {
                return 1;
            }
        });

        surface.Standalone = surface.Sidebar.extend({

            initialize: function (options) {
                surface.Sidebar.prototype.initialize.apply(this, [options]);
            },

            profileAspect: function () {
                return 'standalone'
            },

            sizeDirection: function () {
                return 1;
            }
        });

        surface.ContextTools = surface.Sidebar.extend({

            initialize: function (options) {
                surface.Sidebar.prototype.initialize.apply(this, [options]);
            },

            profileAspect: function () {
                return 'contextTools'
            },

            sizeDirection: function () {
                return -1;
            }
        });

        surface.logo = core.getView('.' + surface.const.logoClass, surface.Logo);
        surface.navigation = core.getView('.' + surface.const.navigationClass, surface.Navigation);
        surface.standalone = core.getView('.' + surface.const.standaloneClass, surface.Standalone);
        surface.contextTools = core.getView('.' + surface.const.css.tools.base + surface.const.css.tools._.context, surface.ContextTools);
        surface.surface = core.getView('.' + surface.const.toolsPanelClass, surface.Surface);

    })(window.composum.pages.surface, window.composum.pages, window.core);
})(window);
