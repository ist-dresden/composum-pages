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
                        name: '_name',
                        path: '_path',
                        type: '_type',
                        actions: '_actions',
                        content: '_components-view'
                    }
                },
                url: {
                    get: {
                        components: '/bin/cpm/pages/edit.pageComponents.content.html'
                    }
                },
                event: {
                    pageContainerRefs: 'page:containerRefs'
                }
            }
        });

        tools.Component = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.components;
                this.$name = this.$('.' + c.css.component + c.css._.name);
                this.$path = this.$('.' + c.css.component + c.css._.path);
                this.$type = this.$('.' + c.css.component + c.css._.type);
                if (this.$path.length == 0) {
                    this.$path = undefined;
                }
                this.data = {
                    name: this.$name.text(),
                    path: this.$path ? this.$path.text() : null,
                    type: this.$type.text()
                };
            }
        });

        tools.Components = Backbone.View.extend({

            initialize: function (options) {
                var c = tools.const.components;
                $(document).on(c.event.pageContainerRefs + '.Components', _.bind(this.onPageContainerRefs, this));
            },

            onPageContainerRefs: function (event, containerRefs) {
                this.containerRefs = containerRefs;
                this.reload();
            },

            reload: function () {
                var c = tools.const.components;
                this.elements = [];
                core.ajaxPut(c.url.get.components + pages.current.page,
                    JSON.stringify(this.containerRefs), {},
                    undefined, undefined, _.bind(function (data) {
                        if (data.status == 200) {
                            this.$el.html(data.responseText);
                        } else {
                            this.$el.html("");
                        }
                        var elements = this.elements;
                        this.$el.find('.' + c.css.base + c.css.component).each(function () {
                            var component = core.getWidget(undefined, this, tools.Component);
                            elements.push(component);
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
