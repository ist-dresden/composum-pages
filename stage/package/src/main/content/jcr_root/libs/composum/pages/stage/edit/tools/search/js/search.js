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
                uri: '/bin/cpm/pages/search.page.tile.html',
                q: {
                    term: 'q.term',
                    limit: 'q.limit'
                }
            }
        });

        search.SearchPanel = Backbone.View.extend({

            initialize: function (options) {
                var e = pages.const.event;
                var c = search.const.css;
                this.$input = this.$('.' + c.base + c._input);
                this.$scope = this.$('.' + c.base + c._scope);
                this.$result = this.$('.' + c.base + c._result);
                this.$scope.val(pages.profile.get('tree-search', 'scope', 'site'));
                this.$input.val(pages.profile.get('tree-search', 'term', undefined));
                this.$input.change(_.bind(this.onChange, this));
                this.$scope.change(_.bind(this.onChange, this));
                $(document).on(e.page.selected + '.tree-search', _.bind(this.onSelected, this));
            },

            onShown: function () {
                this.onChange();
            },

            onChange: function () {
                var term = this.$input.val();
                var scope = this.$scope.val();
                if (term && (term = term.trim()).length > 1) {
                    this.search(scope, term);
                }
                pages.profile.set('tree-search', 'scope', scope);
                pages.profile.set('tree-search', 'term', term);
            },

            search: function (scope, term) {
                var path;
                switch (scope) {
                    case 'content':
                        path = '/content';
                        break;
                    case 'path':
                        path = pages.current.page;
                        break;
                    default:
                    case 'site':
                        path = pages.current.site;
                        break;
                }
                if (path) {
                    var c = search.const.css;
                    var h = search.const.http;
                    var url = h.uri + path
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
                    console.log('search.trigger.' + pages.const.event.page.select + '(' + path + ')');
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
