(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.main = window.composum.pages.main || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            components: {
                css: {
                    base: 'composum-pages-stage-edit-tools-main-components',
                    tools: 'composum-pages-tools',
                    component: 'composum-pages-component',
                    _: {
                        item: '_item',
                        name: '_name',
                        path: '_path',
                        type: '_type',
                        actions: '_actions',
                        content: '_components-view',
                        search: '_search-field',
                        searchRes: '_search-reset',
                        searchAct: '_search-action',
                        filter: '_filter-menu',
                        category: '_category'
                    },
                    filterBtn: 'filter-toggle'
                },
                log: {
                    prefix: 'main.Components.'
                },
                url: {
                    get: {
                        components: '/bin/cpm/pages/edit.pageComponents.content.html'
                    }
                }
            }
        });

        tools.Component = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.components.css;
                this.log = {
                    tools: pages.contextTools.log,
                    dnd: log.getLogger('dnd')
                };
                this.reference = new pages.Reference(this);
                this.$el
                    .on('dragstart', _.bind(this.onDragStart, this))
                    .on('dragend', _.bind(this.onDragEnd, this));
            },

            onDragStart: function (event) {
                var e = pages.const.event;
                var $component = $(event.currentTarget);
                var object = {
                    type: 'component',
                    reference: this.reference
                };
                $(document).trigger(e.dnd.object, [object]);
                var jsonData = JSON.stringify(object);
                var dndEvent = event.originalEvent;
                dndEvent.dataTransfer.setData('application/json', jsonData);
                dndEvent.dataTransfer.effectAllowed = 'copy';
                if (this.log.dnd.getLevel() <= log.levels.DEBUG) {
                    this.log.dnd.debug(tools.const.components.log.prefix + 'dndStart(' + jsonData + ')');
                }
            },

            onDragEnd: function (event) {
                var e = pages.const.event;
                if (this.log.dnd.getLevel() <= log.levels.DEBUG) {
                    this.log.dnd.debug('components.trigger.' + e.dnd.finished + '(...)');
                }
                $(document).trigger(e.dnd.finished, [event]);
            }
        });

        tools.Components = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.components.css;
                $(document).on(pages.const.event.page.containerRefs + '.Components', _.bind(this.onPageContainerRefs, this));
            },

            onPageContainerRefs: function (event, containerRefs) {
                this.containerRefs = containerRefs;
                this.reload();
            },

            reload: function () {
                var c = tools.const.components.css;
                var u = tools.const.components.url;
                var params = [];
                if (this.filter && this.filter.length > 0) {
                    params.push('filter=' + encodeURIComponent(this.filter.join(',')));
                }
                if (this.searchTerm) {
                    params.push('query=' + encodeURIComponent(this.searchTerm));
                }
                var url = u.get.components + pages.current.page;
                if (params.length > 0) {
                    url += '?' + params.join('&');
                }
                this.items = [];
                core.ajaxPut(url, JSON.stringify(this.containerRefs), {}, undefined, undefined,
                    _.bind(function (data) {
                        if (data.status >= 200 && data.status < 300) {
                            this.$el.html(data.responseText);
                        } else {
                            this.$el.html("");
                        }
                        var items = this.items;
                        this.$el.find('.' + c.base + c._.item).each(function () {
                            var item = core.getWidget(undefined, this, tools.Component);
                            items.push(item);
                        });
                    }, this));
            }
        });

        tools.ComponentsTab = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.components.css;
                var p = pages.const.profile.components.list;
                this.components = core.getWidget(this.el, '.' + c.base + c._.content, tools.Components);
                this.$searchField = this.$('.' + c.tools + c._.actions + ' .' + c.tools + c._.search);
                this.$filterButton = this.$('.' + c.tools + c._.actions + ' .' + c.filterBtn);
                this.$filterContent = this.$('.' + c.base + c._.filter);
                this.$filterButton.popover({
                    html: true,
                    sanitize: false,
                    content: this.$filterContent.html(),
                    placement: 'bottom',
                    viewport: {
                        selector: '.' + tools.const.components.css.base,
                        padding: 4
                    }
                });
                this.components.filter = pages.profile.get(p.aspect, p.filter, []);
                this.components.searchTerm = pages.profile.get(p.aspect, p.search, '');
                this.$searchField.val(this.components.searchTerm);
                this.$filterButton.on('inserted.bs.popover', _.bind(this.initFilter, this));
                this.$searchField.change(_.bind(this.searchTermChanged, this));
                this.$('.' + c.tools + c._.actions + ' .' + c.tools + c._.searchAct)
                    .click(_.bind(this.searchTermChanged, this));
                this.$('.' + c.tools + c._.actions + ' .' + c.tools + c._.searchRes)
                    .click(_.bind(this.resetSearchTerm, this));
            },

            popoverContent: function () {
                var $popover = this.$filterButton.parent().find('.popover');
                return $popover.find('.popover-content');
            },

            initFilter: function (event) {
                var $content = this.popoverContent();
                if (this.components.filter) {
                    for (var i = 0; i < this.components.filter.length; i++) {
                        $content.find('input[value="' + this.components.filter[i] + '"]').prop('checked', true);
                    }
                }
                $content.find('input').change(_.bind(this.filterChanged, this))
            },

            filterChanged: function (event) {
                var p = pages.const.profile.components.list;
                var $content = this.popoverContent();
                var filter = [];
                $content.find('input:checked').each(function () {
                    filter.push($(this).attr('value'));
                });
                pages.profile.set(p.aspect, p.filter, this.components.filter = filter);
                this.components.reload();
            },

            searchTermChanged: function (event) {
                var p = pages.const.profile.components.list;
                pages.profile.set(p.aspect, p.search, this.components.searchTerm = this.$searchField.val());
                this.components.reload();
            },

            resetSearchTerm: function (event) {
                this.$searchField.val('');
                this.searchTermChanged(event);
            }
        });

        tools.componentsTab = core.getView('.' + tools.const.components.css.base, tools.ComponentsTab);

    })(window.composum.pages.main, window.composum.pages, window.core);
})(window);
