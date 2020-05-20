/*
 * the behaviour of the element-type-select-widget
 *
 * /libs/composum/pages/stage/widget/element
 */
(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.widgets = window.composum.pages.widgets || {};

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            element: {
                type: {
                    css: {
                        base: 'composum-pages-edit-widget',
                        _widget: '_element-type-select',
                        tile: 'composum-pages-component-tile',
                        _type: '_element-type',
                        _: {
                            item: '_item',
                            name: '_name',
                            path: '_path',
                            type: '_type',
                            actions: '_actions',
                            content: '_select-content',
                            search: '_search-field',
                            searchRes: '_search-reset',
                            searchAct: '_search-action',
                            filterToggle: '_filter-toggle',
                            filterContent: '_filter-menu',
                            category: '_category'
                        }
                    },
                    profile: {
                        aspect: 'element-type-select',
                        filter: 'filter',
                        search: 'search'
                    },
                    url: {
                        get: {
                            components: '/bin/cpm/pages/edit.elementTypes.content.html'
                        }
                    }
                }
            }
        });

        widgets.ElementTypeSelectWidget = core.components.RadioGroupWidget.extend({

            initialize: function (options) {
                var c = widgets.const.element.type.css;
                var p = widgets.const.element.type.profile;
                core.components.RadioGroupWidget.prototype.initialize.apply(this, [options]);
                this.containerRef = JSON.parse(Base64.decode(this.$el.data('container')));
                this.$content = this.$('.' + c.base + c._.content);
                this.$searchField = this.$('.' + c.base + c._.search);
                this.$filterToggle = this.$('.' + c.base + c._.filterToggle);
                this.$filterContent = this.$('.' + c.base + c._.filterContent);
                this.$filterToggle.popover({
                    html: true,
                    sanitize: false,
                    content: this.$filterContent.html(),
                    placement: 'bottom',
                    viewport: {
                        selector: '.modal-dialog .widget-form',
                        padding: 4
                    }
                });
                this.filter = pages.profile.get(p.aspect, p.filter, []);
                this.searchTerm = pages.profile.get(p.aspect, p.search, '');
                this.$searchField.val(this.searchTerm);
                this.$filterToggle.on('inserted.bs.popover', _.bind(this.initFilter, this));
                this.$searchField.change(_.bind(this.searchTermChanged, this));
                this.$searchField.keydown(_.bind(this.searchInputKey, this));
                this.$('.' + c.base + c._.searchAct).click(_.bind(this.searchTermChanged, this));
                this.$('.' + c.base + c._.searchRes).click(_.bind(this.resetSearchTerm, this));
                if (_.isFunction(options.callback)) {
                    this.callback = options.callback; // an initializing parent can declare a reload callback
                } else {
                    this.reload();
                }
            },

            /**
             * @returns an 'only one' option if the filter is not used
             */
            getOnlyOne: function () {
                return this.probablyFiltered ? undefined
                    : core.components.RadioGroupWidget.prototype.getOnlyOne.apply(this, []);
            },

            selectType: function (event) {
                event.preventDefault();
                var c = widgets.const.element.type.css;
                var $link = $(event.currentTarget);
                var $radio = $link.closest('.' + c.base + c._type).find('input[type="radio"]');
                this.setValue($radio.val());
                return false;
            },

            reload: function () {
                var c = widgets.const.element.type.css;
                var u = widgets.const.element.type.url;
                this.probablyFiltered = false;
                var params = ['name=' + encodeURIComponent(this.$el.data('name'))];
                if (this.filter && this.filter.length > 0) {
                    params.push('filter=' + encodeURIComponent(this.filter.join(',')));
                    this.probablyFiltered = true;
                }
                if (this.searchTerm) {
                    params.push('query=' + encodeURIComponent(this.searchTerm));
                    this.probablyFiltered = true;
                }
                var url = u.get.components + pages.current.page;
                if (params.length > 0) {
                    url += '?' + params.join('&');
                }
                core.ajaxPut(url, JSON.stringify([this.containerRef]),
                    {}, undefined, undefined,
                    _.bind(function (data) {
                        if (data.status === 202) {
                            this.$content.html(data.responseText);
                            this.filterAccepted = true;
                        } else if (data.status === 200) {
                            this.$content.html(data.responseText);
                            this.filterAccepted = false;
                            this.probablyFiltered = false;
                        } else {
                            this.$content.html("");
                        }
                        this.$content.find('.' + c.tile).click(_.bind(this.selectType, this));
                        this.filterState();
                        if (_.isFunction(this.callback)) {
                            this.callback();
                        }
                    }, this));
            },

            popoverContent: function () {
                var $popover = this.$filterToggle.parent().find('.popover');
                return $popover.find('.popover-content');
            },

            initFilter: function (event) {
                var $content = this.popoverContent();
                if (this.filter) {
                    for (var i = 0; i < this.filter.length; i++) {
                        $content.find('input[value="' + this.filter[i] + '"]').prop('checked', true);
                    }
                }
                $content.find('input').change(_.bind(this.filterChanged, this));
                this.filterState();
            },

            filterChanged: function (event) {
                var p = widgets.const.element.type.profile;
                var $content = this.popoverContent();
                var filter = [];
                $content.find('input:checked').each(function () {
                    filter.push($(this).attr('value'));
                });
                pages.profile.set(p.aspect, p.filter, this.filter = filter);
                this.reload();
                this.filterState();
            },

            filterState: function () {
                if (this.filter.length > 0 && this.filterAccepted) {
                    this.$filterToggle.addClass("active");
                } else {
                    this.$filterToggle.removeClass("active");
                }
            },

            searchTermChanged: function (event) {
                var p = widgets.const.element.type.profile;
                pages.profile.set(p.aspect, p.search, this.searchTerm = this.$searchField.val());
                this.reload();
            },

            resetSearchTerm: function (event) {
                this.$searchField.val('');
                this.searchTermChanged(event);
            },

            searchInputKey: function (event) {
                if (event.which === 13) {
                    event.preventDefault(); // prevent from 'submit' on enter in the search field
                    this.searchTermChanged(event);
                    return false;
                }
            }
        });

        window.widgets.register('.widget.element-type-select-widget', widgets.ElementTypeSelectWidget);

    })(window.composum.pages.widgets, window.composum.pages, window.core);
})(window);
