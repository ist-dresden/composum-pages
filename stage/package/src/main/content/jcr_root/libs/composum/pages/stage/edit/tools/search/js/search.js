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
                    limit: 'q.limit'
                }
            }
        });

        search.SearchPanel = Backbone.View.extend({

            initialize: function (options) {
                var c = search.const.css;
                var e = pages.const.event;
                var p = pages.const.profile.page.search;
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
                $(document).on(e.page.selected + '.' + p.aspect, _.bind(this.onSelected, this));
            },

            onShown: function () {
                this.onChange();
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
                var p = pages.const.profile.page.search;
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

            onChange: function (event) {
                var p = pages.const.profile.page.search;
                var term = this.$input.val();
                var scope = this.$scope.val();
                if (term && (term = term.trim()).length > 1) {
                    this.search(scope, term);
                }
                if (event) {
                    pages.profile.set(p.aspect, p.scope, scope);
                    pages.profile.set(p.aspect, p.term, term);
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
                    var url = h.uri + this.$el.data('type') + '.tile.html' + this.path
                        + '?' + h.q.term + '=' + encodeURIComponent(term)
                        + '&' + h.q.limit + '=50';
                    this.$el.addClass(c.searching);
                    core.ajaxGet(url, {}, _.bind(function (result) {
                        this.$result.html(result);
                        this.$result.find('a').click(_.bind(this.onSelect, this));
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
                    pages.log.debug('search.trigger.' + pages.const.event.page.select + '(' + path + ')');
                    $(document).trigger(pages.const.event.page.select, [path]);
                }
                return false;
            },

            onSelected: function (event, path) {
                this.setSelection(path);
            },

            setSelection: function (path) {
                if (!path) {
                    path = pages.current.page;
                }
                var c = search.const.css;
                this.$result.find('a').removeClass(c.selected);
                if (path) {
                    this.$result.find('a[data-path="' + path + '"]').addClass(c.selected);
                }
            }
        });

    })(window.composum.pages.search, window.composum.pages, window.core);
})(window);
