(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            versions: {
                event: {
                    id: '.tools.Versions'
                },
                cssBase: 'composum-pages-stage-edit-tools-page-versions',
                version: '_version',
                selection: '_selection',
                content: '_content',
                _name: '-name',
                _time: '-time',
                timeHint: new RegExp('^..(.*):..$'),
                isCheckedOut: 'is-checked-out',
                primaryAvailable: 'primary-available',
                versionsComparable: 'versions-comparable',
                selectedPrimary: 'selected-primary',
                selectedSecondary: 'selected-secondary',
                selectionPrimary: '_selection-primary',
                selectionSecondary: '_selection-secondary',
                primarySelection: '_primary-selection',
                secondarySelection: '_secondary-selection',
                display: {
                    controls: '_display-controls'
                },
                slider: {
                    cssKey: '_version-slider',
                    options: {
                        tooltip: 'hide'
                    }
                },
                compare: {
                    controls: '_compare-controls',
                    filter: '_property-filter',
                    highlight: '_option-highlight',
                    equal: '_option-equal'
                },
                actions: 'composum-pages-tools_actions',
                actionKey: '_action_',
                viewAction: 'view',
                compareAction: 'compare',
                reloadAction: 'reload',
                activateAction: 'activate',
                revertAction: 'revert',
                deactivateAction: 'deactivate',
                checkpointAction: 'checkpoint',
                menuKey: '_menu',
                purgeAction: 'purge',
                checkInAction: 'check-in',
                checkOutAction: 'check-out',
                rollbackAction: 'rollback',
                disabled: 'disabled',
                hidden: 'hidden',
                uri: {
                    version: {
                        base: '/bin/cpm/pages/version',
                        platform: '/bin/cpm/platform/versions',
                        _: {
                            list: '.list.versionList.html',
                            checkpoint: '.checkpoint.json',
                            checkin: '.checkin.jason',
                            checkout: '.checkout.json',
                            purge: '.purge.json',
                            rollback: '.rollbackVersion.json'
                        }
                    }
                }
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
                this.$primSelector = this.$('.' + c.cssBase + c.selectionPrimary);
                this.$sdrySelector = this.$('.' + c.cssBase + c.selectionSecondary);
                this.$primSelector.click(_.bind(this.togglePrimSelection, this));
                this.$sdrySelector.click(_.bind(this.toggleSdrySelection, this));
            },

            togglePrimSelection: function (event) {
                event.preventDefault();
                this.versions.togglePrimSelection(this);
            },

            toggleSdrySelection: function (event) {
                event.preventDefault();
                this.versions.toggleSdrySelection(this);
            }
        });

        tools.VersionsActions = Backbone.View.extend({
            initialize: function (options) {
                var c = tools.const.versions;
                // left
                this.$viewAction = this.$('.' + c.cssBase + c.actionKey + c.viewAction);
                this.$compareAction = this.$('.' + c.cssBase + c.actionKey + c.compareAction);
                this.$reloadAction = this.$('.' + c.cssBase + c.actionKey + c.reloadAction);
                this.left = [
                    this.$viewAction,
                    this.$compareAction,
                    this.$reloadAction
                ];
                this.$viewAction.click(_.bind(function (event) {
                    this.versions.toggleVersionsView('view');
                }, this));
                this.$compareAction.click(_.bind(function (event) {
                    this.versions.toggleVersionsView('compare');
                }, this));
                this.$reloadAction.click(_.bind(function (event) {
                    this.versions.refreshVersionsView();
                }, this));
                // right
                this.$activateAction = this.$('.' + c.cssBase + c.actionKey + c.activateAction);
                this.$revertAction = this.$('.' + c.cssBase + c.actionKey + c.revertAction);
                this.$deactivateAction = this.$('.' + c.cssBase + c.actionKey + c.deactivateAction);
                this.$checkpointAction = this.$('.' + c.cssBase + c.actionKey + c.checkpointAction);
                this.$moreMenu = this.$('.' + c.cssBase + c.menuKey);
                this.$purgeAction = this.$('.' + c.cssBase + c.actionKey + c.purgeAction);
                this.$checkInAction = this.$('.' + c.cssBase + c.actionKey + c.checkInAction);
                this.$checkOutAction = this.$('.' + c.cssBase + c.actionKey + c.checkOutAction);
                this.$rollbackAction = this.$('.' + c.cssBase + c.actionKey + c.rollbackAction);
                this.right = [
                    this.$activateAction,
                    this.$revertAction,
                    this.$deactivateAction,
                    this.$checkpointAction,
                    this.$moreMenu
                ];
                this.$activateAction.click(_.bind(this.activatePage, this));
                this.$revertAction.click(_.bind(this.revertPage, this));
                this.$deactivateAction.click(_.bind(this.deactivatePage, this));
                this.$checkpointAction.click(_.bind(this.createCheckpoint, this));
                this.$purgeAction.click(_.bind(this.purgeVersions, this));
                this.$checkInAction.click(_.bind(this.checkIn, this));
                this.$checkOutAction.click(_.bind(this.checkOut, this));
                this.$rollbackAction.click(_.bind(this.rollbackVersion, this));
            },

            setActionsState: function () {
                var c = tools.const.versions;
                if (this.versions.primSelection || this.versions.sdrySelection || this.versions.versionsVisible) {
                    this.left.forEach(function ($action) {
                        $action.prop(c.disabled, false);
                    });
                    if (!this.versions.versionsVisible) {
                        this.$reloadAction.prop(c.disabled, true);
                    }
                } else {
                    this.left.forEach(function ($action) {
                        $action.prop(c.disabled, true);
                    });
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
                if (this.versions.primSelection) {
                    this.$rollbackAction.prop(c.disabled, false);
                } else {
                    this.$rollbackAction.prop(c.disabled, true);
                }
            },

            activatePage: function (event) {
                var ref = this.versions.data.reference;
                pages.actions.page.activate(event, ref.name, ref.path, ref.type);
            },

            revertPage: function (event) {
                var ref = this.versions.data.reference;
                pages.actions.page.revert(event, ref.name, ref.path, ref.type);
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
                var u = tools.const.versions.uri.version;
                var ref = this.versions.data.reference;
                core.ajaxPost(u.platform + u._.purge + core.encodePath(ref.path), {
                        _charset_: 'UTF-8'
                    }, {},
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

            rollbackVersion: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var u = tools.const.versions.uri.version;
                var path = this.versions.data.jcrContent.path;
                var version = this.versions.primSelection.name;
                core.ajaxPut(u.base + u._.rollback + path, JSON.stringify({
                        path: path,
                        version: version
                    }), {}, _.bind(function (result) {
                        var e = pages.const.event;
                        pages.trigger('versions.rollback', e.page.changed, [this.versions.data.reference]);
                    }, this), _.bind(function (result) {
                        this.error('on rollback version', result);
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
                this.$versionContent = this.$('.' + c.cssBase + c.content);
                this.actions = core.getWidget(this.el, '.' + c.actions, tools.VersionsActions);
                this.actions.versions = this;
                this.$primSelection = this.$('.' + c.cssBase + c.primarySelection);
                this.$sdrySelection = this.$('.' + c.cssBase + c.secondarySelection);
                this.display = {
                    $controls: this.$('.' + c.cssBase + c.display.controls),
                    $slider: this.$('.' + c.cssBase + c.slider.cssKey)
                };
                this.display.$slider.slider(c.slider.options);
                this.display.$slider.on('slide', _.bind(this.compareView, this));
                this.compare = {
                    $controls: this.$('.' + c.cssBase + c.compare.controls),
                    $filter: this.$('.' + c.cssBase + c.compare.filter),
                    $highlight: this.$('.' + c.cssBase + c.compare.highlight),
                    $equal: this.$('.' + c.cssBase + c.compare.equal)
                };
                var profile = pages.profile.get('versions', 'compare', {
                    filter: 'properties',
                    highlight: true,
                    equal: true
                });
                this.compare.$filter.val(profile.filter);
                this.compare.$highlight.prop('checked', profile.highlight);
                this.compare.$equal.prop('checked', profile.equal);
                this.compare.$filter.change(_.bind(this.comparisionOptions, this));
                this.compare.$highlight.change(_.bind(this.comparisionOptions, this));
                this.compare.$equal.change(_.bind(this.comparisionOptions, this));
                var id = tools.const.versions.event.id;
                var e = pages.const.event;
                $(document)
                    .on(e.content.state + '.' + id, _.bind(this.reload, this))
                    .on(e.page.changed + '.' + id, _.bind(this.reload, this));
            },

            beforeClose: function () {
                var e = pages.const.event;
                var id = tools.const.versions.event.id;
                $(document)
                    .off(e.content.state + id)
                    .off(e.page.changed + id);
            },

            onTabSelected: function () {
                this.reload();
            },

            reload: function () {
                var c = tools.const.versions;
                var u = tools.const.versions.uri.version;
                pages.editFrame.getPageData(this.contextTabs.reference.path, _.bind(function (data) {
                    this.data = data;
                    this.state = data.jcrContent.jcrState;
                    if (this.state.checkedOut) {
                        this.$el.addClass(c.isCheckedOut);
                    } else {
                        this.$el.removeClass(c.isCheckedOut);
                    }
                    this.currentVersion = undefined;
                    this.primSelection = undefined;
                    this.sdrySelection = undefined;
                    this.display.$slider.slider('disable');
                    core.ajaxGet(u.base + u._.list + core.encodePath(this.contextTabs.reference.path), {},
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

            compareView: function (event) {
                pages.versionsView.primView.setOpacity((100.0 - event.value) / 100.0);
            },

            setComparable: function () {
                if ((this.primSelection || this.sdrySelection) && this.primSelection !== this.sdrySelection) {
                    this.$el.addClass(tools.const.versions.versionsComparable);
                    this.display.$slider.slider('enable');
                    pages.versionsView.primView.setOpacity((100.0 - this.display.$slider.slider('getValue')) / 100.0);
                } else {
                    this.display.$slider.slider('disable');
                    this.$el.removeClass(tools.const.versions.versionsComparable);
                    pages.versionsView.primView.setOpacity(1.0);
                }
            },

            togglePrimSelection: function (version) {
                this.$versionList.removeClass(tools.const.versions.selectedPrimary);
                if (this.primSelection === version) {
                    this.primSelection = undefined;
                    switch (this.versionsVisible) {
                        case'view':
                            pages.versionsView.primView.reset();
                            break;
                        case 'compare':
                            this.refreshVersionComparision();
                            break;
                    }
                } else {
                    this.primSelection = version;
                    this.primSelection.$el.addClass(tools.const.versions.selectedPrimary);
                    switch (this.versionsVisible) {
                        case'view':
                            pages.versionsView.primView.view(this.data.path, {
                                release: version.release,
                                version: version.id
                            });
                            break;
                        case 'compare':
                            this.refreshVersionComparision();
                            break;
                    }
                }
                this.showSelection();
            },

            toggleSdrySelection: function (version) {
                this.$versionList.removeClass(tools.const.versions.selectedSecondary);
                if (this.sdrySelection === version) {
                    this.sdrySelection = undefined;
                    switch (this.versionsVisible) {
                        case'view':
                            pages.versionsView.sdryView.reset();
                            break;
                        case 'compare':
                            this.refreshVersionComparision();
                            break;
                    }
                } else {
                    this.sdrySelection = version;
                    this.sdrySelection.$el.addClass(tools.const.versions.selectedSecondary);
                    switch (this.versionsVisible) {
                        case'view':
                            pages.versionsView.sdryView.view(this.data.path, {
                                release: version.release,
                                version: version.id
                            });
                            break;
                        case 'compare':
                            this.refreshVersionComparision();
                            break;
                    }
                }
                this.showSelection();
            },

            showSelection: function () {
                var c = tools.const.versions;
                this.showSelectionValues(this.$primSelection, this.primSelection, c.primaryAvailable);
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

            comparisionOptions: function (refresh) {
                var options = {
                    filter: this.compare.$filter.val(),
                    highlight: this.compare.$highlight.prop('checked'),
                    equal: this.compare.$equal.prop('checked')
                };
                pages.profile.set('versions', 'compare', options);
                if (this.versionsVisible === 'compare' && refresh) {
                    this.refreshVersionComparision(options);
                }
                return options;
            },

            refreshVersionComparision: function (options) {
                if (this.versionsVisible) {
                    var scope = this.currentScope();
                    pages.versionsView.showComparision(scope.path, scope.primary, scope.secondary, _.extend({
                        locale: pages.getLocale()
                    }, options ? options : this.comparisionOptions(false)));
                }
            },

            refreshVersionsView: function () {
                if (this.versionsVisible) {
                    switch (this.versionsVisible) {
                        case'view':
                            var scope = this.currentScope();
                            pages.versionsView.showVersions(scope.path, scope.primary, scope.secondary);
                            break;
                        case'compare':
                            this.refreshVersionComparision();
                            break;
                    }
                }
            },

            toggleVersionsView: function (type) {
                var c = tools.const.versions;
                if (this.versionsVisible === type) {
                    this.versionsVisible = undefined;
                    pages.versionsView.reset();
                    this.actions.$viewAction.removeClass('active');
                    this.actions.$compareAction.removeClass('active');
                    this.actions.$reloadAction.prop(c.disabled, true);
                    this.contextTabs.lockTabs(false);
                    this.actions.right.forEach(function ($el) {
                        $el.prop(c.disabled, false);
                    });
                } else {
                    if (!this.versionsVisible) {
                        this.actions.$reloadAction.prop(c.disabled, false);
                        this.actions.right.forEach(function ($el) {
                            $el.prop(c.disabled, true);
                        });
                        this.contextTabs.lockTabs(true);
                    } else {
                        pages.versionsView.reset();
                        switch (this.versionsVisible) {
                            case'view':
                                this.actions.$viewAction.removeClass('active');
                                break;
                            case'compare':
                                this.actions.$compareAction.removeClass('active');
                                break;
                        }
                    }
                    this.versionsVisible = type;
                    switch (this.versionsVisible) {
                        case'view':
                            var scope = this.currentScope();
                            this.compare.$controls.css('visibility', 'hidden');
                            this.display.$controls.css('visibility', 'visible');
                            pages.versionsView.showVersions(scope.path, scope.primary, scope.secondary);
                            this.actions.$viewAction.addClass('active');
                            break;
                        case'compare':
                            this.display.$controls.css('visibility', 'hidden');
                            this.compare.$controls.css('visibility', 'visible');
                            this.refreshVersionComparision();
                            pages.versionsView.primView.setOpacity(100);
                            this.actions.$compareAction.addClass('active');
                            break;
                    }
                }
            },

            currentScope: function () {
                var result = {
                    path: this.data.path,
                    primary: undefined,
                    secondary: undefined
                };
                if (this.primSelection) {
                    result.primary = {
                        release: this.primSelection.release,
                        version: this.primSelection.id
                    };
                }
                if (this.sdrySelection) {
                    result.secondary = {
                        release: this.sdrySelection.release,
                        version: this.sdrySelection.id
                    };
                }
                return result;
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
