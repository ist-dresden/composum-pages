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
                        content: '_components-view'
                    }
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
                $(document).on(pages.const.event.page.containerRefs + '.Components', _.bind(this.onPageContainerRefs, this));
            },

            onPageContainerRefs: function (event, containerRefs) {
                this.containerRefs = containerRefs;
                this.reload();
            },

            reload: function () {
                var c = tools.const.components.css;
                var u = tools.const.components.url;
                this.items = [];
                core.ajaxPut(u.get.components + pages.current.page,
                    JSON.stringify(this.containerRefs), {},
                    undefined, undefined, _.bind(function (data) {
                        if (data.status === 200) {
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

        tools.ComponentsActions = Backbone.View.extend({
            initialize: function (options) {
                var c = tools.const.components;
            }
        });

        tools.ComponentsTab = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.components;
                this.components = core.getWidget(this.el, '.' + c.css.base + c.css._.content, tools.Components);
                this.actions = core.getWidget(this.el, '.' + c.css.tools + c.css._.actions, tools.ComponentsActions);
                this.actions.components = this;
            }
        });

        tools.componentsTab = core.getView('.' + tools.const.components.css.base, tools.ComponentsTab);

    })(window.composum.pages.main, window.composum.pages, window.core);
})(window);
