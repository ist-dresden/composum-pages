(function () {
    'use strict';
    CPM.namespace('pages.tools.site');

    (function (site, pages, core) {

        site.const = _.extend(site.const || {}, {
            releases: {
                event: {
                    id: '.tools.SiteReleases'
                },
                css: {
                    site: {
                        base: 'composum-pages-site-tools_releases',
                        _release: '_release',
                        _select: '-select'
                    },
                    release: {
                        base: 'composum-pages-site-tools_releases_release',
                        _listentry: '-listentry',
                        _entry: '-entry'
                    },
                    tools: {
                        base: 'composum-pages-stage-edit-tools-site-releases'
                    },
                    dialog: {
                        base: 'composum-pages-stage-edit-dialog',
                        _number: '_number',
                        _publish: '_publish',
                        _current: '_current'
                    }
                },
                uri: {
                    load: '/libs/composum/pages/stage/edit/tools/site/releases.content.html',
                    release: {
                        view: '/libs/composum/pages/stage/edit/site/release.html'
                    }
                }
            }
        });

        pages.releases.profile = pages.releases.profile || new core.LocalProfile('composum.pages.releases');

        site.FinalizeDialog = core.components.FormDialog.extend({

            initialize: function (options) {
                var c = site.const.releases.css.dialog;
                core.components.FormDialog.prototype.initialize.call(this, options);
                this.number = core.getWidget(this.$el, '.' + c.base + c._number, core.components.RadioGroupWidget);
                this.$publish = this.$('.' + c.base + c._publish);
                this.$current = this.$('.' + c.base + c._current);
                this.public = [
                    core.getWidget(this.$publish, '[value="public"]', core.components.CheckboxWidget),
                    core.getWidget(this.$current, '[value="public"]', core.components.CheckboxWidget)
                ];
                this.preview = [
                    core.getWidget(this.$publish, '[value="preview"]', core.components.CheckboxWidget),
                    core.getWidget(this.$current, '[value="preview"]', core.components.CheckboxWidget)
                ];
                this.number.setValue(pages.releases.profile.get('finalize', 'number', 'BUGFIX'));
                this.number.changed('finalize', _.bind(function () {
                    pages.releases.profile.set('finalize', 'number', this.number.getValue());
                }, this));
                this.public.forEach(function (widget, index) {
                    widget.changed('finalize', _.bind(function (event, value) {
                        this.likeARadio(this.public, event, value);
                    }, this));
                }, this);
                this.preview.forEach(function (widget, index) {
                    widget.changed('finalize', _.bind(function (event, value) {
                        this.likeARadio(this.preview, event, value);
                    }, this));
                }, this);
            },

            likeARadio: function (set, event, value) {
                if (value) {
                    var view = event.currentTarget.view;
                    if (view) {
                        set.forEach(function (widget, index) {
                            if (view !== widget) {
                                widget.setValue(false);
                            }
                        }, this);
                    }
                }
            }
        });

        site.SiteReleases = pages.releases.SiteReleases.extend({

            initialize: function (options) {
                this.initContent(options);
                var e = pages.const.event;
                var id = site.const.releases.event.id;
                $(document)
                    .on(e.site.state + id, _.bind(this.reload, this))
                    .on(e.site.changed + id, _.bind(this.reload, this));
            },

            beforeClose: function () {
                this.beforeHideTab();
                var e = pages.const.event;
                var id = site.const.releases.event.id;
                $(document)
                    .off(e.site.state + id)
                    .off(e.site.changed + id);
            },

            initContent: function (options) {
                var c = site.const.releases.css;
                pages.releases.SiteReleases.prototype.initialize.call(this, _.extend(options || {}, {
                    cssBase: site.const.releases.css.site
                }));
                this.sitePath = this.$('.' + c.site.base).data('path');
                this.$releaseView = [];
                this.$('.' + c.release.base + c.release._entry).click(_.bind(this.releaseView, this));
            },

            releaseView: function (event) {
                var c = site.const.releases.css.release;
                var $entry = $(event.currentTarget);
                var path = $entry.data('path');
                var $listEntry = $entry.closest('.' + c.base + c._listentry);
                if ((this.$releaseView.length > 0 && this.$releaseView[0] === $listEntry[0])
                    || core.getNameFromPath(path) === 'current') {
                    this.closeReleaseView();
                } else {
                    if (this.$releaseView.length > 0) {
                        this.$releaseView.removeClass('selected');
                    }
                    this.$releaseView = $listEntry;
                    $('body').addClass('context-driven-view');
                    $listEntry.addClass('selected');
                    pages.trigger('site.release.view', pages.const.event.content.view, [path, {'pages.view': 'preview'},
                        site.const.releases.uri.release.view]);
                }
            },

            closeReleaseView: function () {
                if (this.$releaseView.length > 0) {
                    $('body').removeClass('context-driven-view');
                    this.$releaseView.removeClass('selected');
                    this.$releaseView = [];
                    pages.trigger('site.release.close', pages.const.event.content.view, [null, {}]);
                }
            },

            beforeHideTab: function () {
                this.closeReleaseView();
            },

            onTabSelected: function () {
                this.reload();
            },

            reload: function () {
                this.closeReleaseView();
                var c = site.const.releases.uri;
                core.getHtml(c.load + this.contextTabs.reference.path,
                    undefined, undefined, _.bind(function (data) {
                        if (data.status === 200) {
                            this.$el.html(data.responseText);
                        } else {
                            this.$el.html("");
                        }
                        this.initContent({});
                    }, this));
            }
        });

        /**
         * register these tools as a pages context tool for initialization after load of the context tools set
         */
        pages.contextTools.addTool(function (contextTabs) {
            var c = site.const.releases.css;
            var panel = core.getWidget(contextTabs.el, '.' + c.tools.base, site.SiteReleases);
            if (panel) {
                panel.contextTabs = contextTabs;
            }
            return panel;
        });

    })(CPM.pages.tools.site, CPM.pages, CPM.core);
})();
