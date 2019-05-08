(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            versions: {
                cssBase: 'composum-pages-stage-edit-tools-page-versions',
                version: '_version',
                selection: '_selection',
                content: '_content',
                _name: '-name',
                _time: '-time',
                timeHint: new RegExp('^..(.*):..$'),
                isCheckedOut: 'is-checked-out',
                mainAvailable: 'main-available',
                versionsComparable: 'versions-comparable',
                selectedMain: 'selected-main',
                selectedSecondary: 'selected-secondary',
                selectionMain: '_selection-main',
                selectionSecondary: '_selection-secondary',
                mainSelection: '_main-selection',
                secondarySelection: '_secondary-selection',
                slider: {
                    cssKey: '_version-slider',
                    options: {
                        tooltip: 'hide'
                    }
                },
                actions: 'composum-pages-tools_actions',
                actionKey: '_action_',
                viewAction: 'view',
                compareAction: 'compare',
                activateAction: 'activate',
                deactivateAction: 'deactivate',
                checkpointAction: 'checkpoint',
                menuKey: '_menu',
                purgeAction: 'purge',
                checkInAction: 'check-in',
                checkOutAction: 'check-out',
                restoreAction: 'restore',
                versionContentUri: '/bin/cpm/pages/edit.versions.versionList.html',
                versionRestoreUri: '/bin/cpm/pages/edit.restoreVersion.json',
                platformVersionsUri: '/bin/cpm/platform/versions.',
                nodesVersionsUri: '/bin/cpm/nodes/version.',
                disabled: 'disabled',
                hidden: 'hidden'
            }
        });

        tools.Version = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.versions;
                this.id = this.$el.data('version');
                this.release = this.$el.data('release');
                this.name = this.$('.' + c.cssBase + c.version + c._name).text();
                this.time = this.$('.' + c.cssBase + c.version + c._time).text();
                this.timeHint = c.timeHint.exec(this.time)[1];
                this.$mainSelector = this.$('.' + c.cssBase + c.selectionMain);
                this.$sdrySelector = this.$('.' + c.cssBase + c.selectionSecondary);
                this.$mainSelector.click(_.bind(this.toggleMainSelection, this));
                this.$sdrySelector.click(_.bind(this.toggleSdrySelection, this));
            },

            toggleMainSelection: function (event) {
                event.preventDefault();
                this.versions.toggleMainSelection(this);
            },

            toggleSdrySelection: function (event) {
                event.preventDefault();
                this.versions.toggleSdrySelection(this);
            }
        });

        tools.VersionsActions = Backbone.View.extend({
            initialize: function (options) {
                var c = tools.const.versions;
                this.$viewAction = this.$('.' + c.cssBase + c.actionKey + c.viewAction);
                this.$activateAction = this.$('.' + c.cssBase + c.actionKey + c.activateAction);
                this.$deactivateAction = this.$('.' + c.cssBase + c.actionKey + c.deactivateAction);
                this.$checkpointAction = this.$('.' + c.cssBase + c.actionKey + c.checkpointAction);
                this.$moreMenu = this.$('.' + c.cssBase + c.menuKey);
                this.$purgeAction = this.$('.' + c.cssBase + c.actionKey + c.purgeAction);
                this.$checkInAction = this.$('.' + c.cssBase + c.actionKey + c.checkInAction);
                this.$checkOutAction = this.$('.' + c.cssBase + c.actionKey + c.checkOutAction);
                this.$restoreAction = this.$('.' + c.cssBase + c.actionKey + c.restoreAction);
                this.right = [
                    this.$activateAction,
                    this.$deactivateAction,
                    this.$checkpointAction,
                    this.$moreMenu
                ];
                this.$viewAction.click(_.bind(this.toggleView, this));
                this.$activateAction.click(_.bind(this.activatePage, this));
                this.$deactivateAction.click(_.bind(this.deactivatePage, this));
                this.$checkpointAction.click(_.bind(this.createCheckpoint, this));
                this.$purgeAction.click(_.bind(this.purgeVersions, this));
                this.$checkInAction.click(_.bind(this.checkIn, this));
                this.$checkOutAction.click(_.bind(this.checkOut, this));
                this.$restoreAction.click(_.bind(this.restoreVersion, this));
            },

            setActionsState: function () {
                var c = tools.const.versions;
                if (this.versions.mainSelection || this.versions.versionsVisible) {
                    this.$viewAction.prop(c.disabled, false);
                } else {
                    this.$viewAction.prop(c.disabled, true);
                }
                if (this.versions.state.checkedOut) {
                    this.$checkpointAction.prop(c.disabled, this.versions.versionsVisible);
                    this.$checkOutAction.addClass(c.hidden);
                    this.$checkInAction.removeClass(c.hidden);
                } else {
                    this.$checkpointAction.prop(c.disabled, true);
                    this.$checkOutAction.removeClass(c.hidden);
                    this.$checkInAction.addClass(c.hidden);
                }
                if (this.versions.mainSelection) {
                    this.$restoreAction.prop(c.disabled, false);
                } else {
                    this.$restoreAction.prop(c.disabled, true);
                }
            },

            toggleView: function (event) {
                if (event) {
                    event.preventDefault();
                }
                this.versions.toggleVersionsView();
            },

            activatePage: function (event) {
                var ref = this.versions.data.reference;
                pages.actions.page.activate(event, ref.name, ref.path, ref.type);
            },

            deactivatePage: function (event) {
                var ref = this.versions.data.reference;
                pages.actions.page.deactivate(event, ref.name, ref.path, ref.type);
            },

            createCheckpoint: function (event) {
                var ref = this.versions.data.reference;
                var path = this.versions.data.jcrContent.path;
                pages.actions.page.checkpoint(event, ref.name, path, ref.type);
            },

            purgeVersions: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var ref = this.versions.data.reference;
                core.ajaxPost(tools.const.versions.platformVersionsUri + 'purge.json' + ref.path, {}, {},
                    _.bind(function (result) {
                        this.versions.reload();
                    }, this), _.bind(function (result) {
                        this.error('on versions purge', result);
                    }, this)
                );
            },

            checkIn: function (event) {
                var ref = this.versions.data.reference;
                var path = this.versions.data.jcrContent.path;
                pages.actions.page.checkin(event, ref.name, path, ref.type);
            },

            checkOut: function (event) {
                var ref = this.versions.data.reference;
                var path = this.versions.data.jcrContent.path;
                pages.actions.page.checkout(event, ref.name, path, ref.type);
            },

            restoreVersion: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var path = this.versions.data.jcrContent.path;
                var version = this.versions.mainSelection.name;
                core.ajaxPut(tools.const.versions.versionRestoreUri + path, JSON.stringify({
                        path: path,
                        version: version
                    }), {}, _.bind(function (result) {
                        this.versions.contextTabs.reloadPage();
                    }, this), _.bind(function (result) {
                        this.error('on restoring version', result);
                    }, this)
                );
            },

            error: function (hint, result) {
                core.alert('danger', 'Error', 'Error ' + hint, result);
            }
        });

        tools.Versions = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.versions;
                this.$mainSelection = this.$('.' + c.cssBase + c.mainSelection);
                this.$sdrySelection = this.$('.' + c.cssBase + c.secondarySelection);
                this.$slider = this.$('.' + c.cssBase + c.slider.cssKey);
                this.$versionContent = this.$('.' + c.cssBase + c.content);
                this.actions = core.getWidget(this.el, '.' + c.actions, tools.VersionsActions);
                this.actions.versions = this;
                this.$slider.slider(c.slider.options);
                this.$slider.on('slide', _.bind(this.compare, this));
                var id = 'Versions';
                var e = pages.const.event;
                $(document).on(e.page.state + '.' + id, _.bind(this.reload, this));
                $(document).on(e.page.changed + '.' + id, _.bind(this.reload, this));
            },

            onTabSelected: function () {
                this.reload();
            },

            reload: function () {
                var c = tools.const.versions;
                pages.getPageData(this.contextTabs.reference.path, _.bind(function (data) {
                    this.data = data;
                    this.state = data.jcrContent.jcrState;
                    if (this.state.checkedOut) {
                        this.$el.addClass(c.isCheckedOut);
                    } else {
                        this.$el.removeClass(c.isCheckedOut);
                    }
                    this.currentVersion = undefined;
                    this.mainSelection = undefined;
                    this.sdrySelection = undefined;
                    this.$slider.slider('disable');
                    core.ajaxGet(c.versionContentUri + this.contextTabs.reference.path, {},
                        undefined, undefined, _.bind(function (data) {
                            if (data.status === 200) {
                                this.$versionContent.html(data.responseText);
                            } else {
                                this.$versionContent.html("");
                            }
                            this.$versionList = this.$versionContent.find('.' + c.cssBase + c.version);
                            var versionsTool = this;
                            this.$versionList.each(function () {
                                var version = core.getWidget(undefined, this, tools.Version);
                                version.versions = versionsTool;
                                if (!versionsTool.currentVersion) {
                                    versionsTool.currentVersion = version;
                                }
                            });
                            this.showSelection();
                        }, this));
                }, this));
            },

            compare: function (event) {
                pages.versionsView.mainView.setOpacity(100 - event.value);
            },

            setComparable: function () {
                if (this.mainSelection && this.sdrySelection && this.mainSelection !== this.sdrySelection) {
                    this.$el.addClass(tools.const.versions.versionsComparable);
                    this.$slider.slider('enable');
                    pages.versionsView.mainView.setOpacity(100 - this.$slider.slider('getValue'));
                } else {
                    this.$slider.slider('disable');
                    this.$el.removeClass(tools.const.versions.versionsComparable);
                    pages.versionsView.mainView.setOpacity(100);
                }
            },

            toggleMainSelection: function (version) {
                this.$versionList.removeClass(tools.const.versions.selectedMain);
                if (this.mainSelection === version) {
                    this.mainSelection = undefined;
                    if (this.versionsVisible) {
                        pages.versionsView.mainView.reset();
                    }
                } else {
                    this.mainSelection = version;
                    this.mainSelection.$el.addClass(tools.const.versions.selectedMain);
                    if (this.versionsVisible) {
                        pages.versionsView.mainView.view(this.data.path, {
                            release: version.release,
                            version: version.id
                        });
                    }
                }
                this.showSelection();
            },

            toggleSdrySelection: function (version) {
                this.$versionList.removeClass(tools.const.versions.selectedSecondary);
                if (this.sdrySelection === version) {
                    this.sdrySelection = undefined;
                    if (this.versionsVisible) {
                        pages.versionsView.sdryView.reset();
                    }
                } else {
                    this.sdrySelection = version;
                    this.sdrySelection.$el.addClass(tools.const.versions.selectedSecondary);
                    if (this.versionsVisible) {
                        pages.versionsView.sdryView.view(this.data.path, {
                            release: version.release,
                            version: version.id
                        });
                    }
                }
                this.showSelection();
            },

            showSelection: function () {
                var c = tools.const.versions;
                this.showSelectionValues(this.$mainSelection, this.mainSelection, c.mainAvailable);
                this.showSelectionValues(this.$sdrySelection, this.sdrySelection);
                this.actions.setActionsState();
                this.setComparable();
            },

            showSelectionValues: function ($slot, version, indicatorClass) {
                var c = tools.const.versions;
                if (version) {
                    $slot.find('.' + c.cssBase + c.selection + c._name).text(version.name);
                    $slot.find('.' + c.cssBase + c.selection + c._time).text(version.timeHint);
                    if (indicatorClass) {
                        this.$el.addClass(indicatorClass);
                    }
                } else {
                    if (indicatorClass) {
                        this.$el.removeClass(indicatorClass);
                    }
                    $slot.find('.' + c.cssBase + c.selection + c._name).text('');
                    $slot.find('.' + c.cssBase + c.selection + c._time).text('');
                }
            },

            toggleVersionsView: function () {
                var c = tools.const.versions;
                if (this.versionsVisible) {
                    this.versionsVisible = false;
                    pages.versionsView.reset();
                    this.actions.$viewAction.removeClass('active');
                    this.contextTabs.lockTabs(false);
                    this.actions.right.forEach(function ($el) {
                        $el.prop(c.disabled, false);
                    });
                } else {
                    this.actions.right.forEach(function ($el) {
                        $el.prop(c.disabled, true);
                    });
                    this.contextTabs.lockTabs(true);
                    this.versionsVisible = true;
                    var path = this.data.path;
                    var primary = undefined;
                    var secondary = undefined;
                    if (this.mainSelection) {
                        primary = {
                            release: this.mainSelection.release,
                            version: this.mainSelection.id
                        };
                    }
                    if (this.sdrySelection) {
                        secondary = {
                            release: this.sdrySelection.release,
                            version: this.sdrySelection.id
                        };
                    }
                    pages.versionsView.showVersions(path, primary, secondary);
                    this.actions.$viewAction.addClass('active');
                }
            }
        });

        /**
         * register this tool as a pages context tool for initialization after load of the context tools set
         */
        pages.contextTools.addTool(function (contextTabs) {
            var versions = core.getWidget(contextTabs.el, '.' + tools.const.versions.cssBase, tools.Versions);
            if (versions) {
                versions.contextTabs = contextTabs;
            }
            return versions;
        });

    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
