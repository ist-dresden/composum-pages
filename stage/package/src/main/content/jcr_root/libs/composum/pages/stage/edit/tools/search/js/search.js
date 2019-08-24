(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.search = window.composum.pages.search || {};

    (function (search, pages, core) {
        'use strict';

        search.const = _.extend(search.const || {}, {
            css: {
                base: 'composum-pages-tools',
                _panel: '_search-panel',
                _input: '_search-field',
                _scope: '_search-scope',
                _result: '_search-result',
                searching: 'searching',
                selected: 'selected'
            },
            http: {
                uri: '/bin/cpm/pages/search.',
                q: {
                    term: 'q.term',
                    limit: 'q.limit',
                    filter: 'filter'
                }
            }
        });

        search.SearchPanel = Backbone.View.extend({

            initialize: function (options) {
                this.type = this.$el.data('type');
                var c = search.const.css;
                var e = pages.const.event;
                var p = pages.const.profile[this.type].search;
                this.log = {
                    pages: pages.log,
                    dnd: log.getLogger('dnd')
                };
                this.$input = this.$('.' + c.base + c._input);
                this.$scope = this.$('.' + c.base + c._scope);
                this.$result = this.$('.' + c.base + c._result);
                this.$scope.val(this.getScope(this.$scope));
                this.$input.val(pages.profile.get(p.aspect, p.term));
                this.$input.keypress(_.bind(function (event) {
                    if (event.which === 13) {
                        var term = this.$input.val();
                        var scope = this.$scope.val();
                        this.search(scope, term ? term : '*');
                    }
                }, this));
                this.$input.change(_.bind(this.onChange, this));
                this.$scope.change(_.bind(this.onChange, this));
                $(document).on(e[this.type].selected + '.' + p.aspect, _.bind(this.onSelected, this));
            },

            setFilter: function (filter) {
                if (this.filter !== filter) {
                    this.filter = filter;
                    if (this.visible) {
                        this.onChange();
                    }
                }
            },

            onShown: function () {
                this.visible = true;
                this.onChange();
            },

            onHidden: function () {
                this.visible = false;
            },

            onScopeChanged: function () {
                var s = this.$scope.val();
                this.$scope.val(this.getScope(this.$scope));
                if (!this.path || s !== this.$scope.val()) {
                    // no search done before or search query changed...
                    this.onChange();
                }
            },

            getScope: function ($select) {
                var p = pages.const.profile[this.type].search;
                if ($select) {
                    $select.find('option').removeAttr('disabled');
                }
                var scope = pages.profile.get(p.aspect, p.scope, 'site');
                if (scope === 'content') {
                    if (pages.getScope() === 'site') {
                        scope = 'site';
                        if ($select) {
                            $select.find('option[value="content"]').attr('disabled', 'disabled');
                        }
                    }
                }
                return scope;
            },

            refresh: function () {
                var term = this.$input.val();
                var scope = this.$scope.val();
                if (term && (term = term.trim()).length > 1) {
                    this.search(scope, term);
                }
            },

            onChange: function (event) {
                this.refresh();
                if (event) {
                    var p = pages.const.profile[this.type].search;
                    pages.profile.set(p.aspect, p.scope, this.$scope.val());
                    pages.profile.set(p.aspect, p.term, this.$input.val());
                }
            },

            search: function (scope, term) {
                this.path = undefined;
                switch (scope) {
                    case 'content':
                        this.path = '/content';
                        break;
                    case 'path':
                        this.path = pages.current.page;
                        break;
                    default:
                    case 'site':
                        this.path = pages.current.site;
                        break;
                }
                if (this.path) {
                    var c = search.const.css;
                    var h = search.const.http;
                    var url = h.uri + this.type + '.tile.html' + this.path
                        + '?' + h.q.term + '=' + encodeURIComponent(term)
                        + '&' + h.q.limit + '=50';
                    if (this.filter) {
                        url += '&' + h.q.filter + '=' + this.filter;
                    }
                    this.$el.addClass(c.searching);
                    core.ajaxGet(url, {}, _.bind(function (result) {
                        this.$result.html(result);
                        this.$result.find('a').click(_.bind(this.onSelect, this))
                            .find('[draggable="true"]')
                            .on('dragstart', _.bind(this.onDragStart, this))
                            .on('dragend', _.bind(this.onDragEnd, this));
                        this.setSelection();
                    }, this), undefined, _.bind(function () {
                        this.$el.removeClass(c.searching);
                    }, this));
                }
            },

            onSelect: function (event) {
                event.preventDefault();
                var $el = $(event.currentTarget);
                var path = $el.data('path');
                if (path) {
                    pages.trigger('search.select', pages.const.event[this.type].select, [path]);
                }
                return false;
            },

            onSelected: function (event, path) {
                if (pages.log.getLevel() <= log.levels.DEBUG) {
                    pages.log.debug('search.selected[' + this.type + ']: ' + path);
                }
                this.setSelection(path);
            },

            setSelection: function (path) {
                if (!path) {
                    path = this.selected;
                    if (!path && this.type === 'page') {
                        path = pages.current.page;
                    }
                }
                this.selected = path;
                var c = search.const.css;
                this.$result.find('a').removeClass(c.selected);
                if (path) {
                    this.$result.find('a[data-path="' + path + '"]').addClass(c.selected);
                }
            },

            // DnD (page: link..., asset: reference..., element: move..., component: insert...)

            onDragStart: function (event) {
                var e = pages.const.event;
                var $el = $(event.currentTarget);
                var reference = new pages.Reference($el);
                if (reference.path) {
                    var object = {
                        type: this.type,
                        reference: reference
                    };
                    pages.trigger('search.dnd.start', e.dnd.object, [object]);
                    var jsonData = JSON.stringify(object);
                    var dndEvent = event.originalEvent;
                    dndEvent.dataTransfer.setData('application/json', jsonData);
                    dndEvent.dataTransfer.effectAllowed = 'copy';
                    if (this.log.dnd.getLevel() <= log.levels.DEBUG) {
                        this.log.dnd.debug('search.dndStart(' + jsonData + ')');
                    }
                }
            },

            onDragEnd: function (event) {
                var e = pages.const.event;
                pages.trigger('search.dnd.end', e.dnd.finished, [event], ['...']);
            }
        });

    })(window.composum.pages.search, window.composum.pages, window.core);
})(window);
