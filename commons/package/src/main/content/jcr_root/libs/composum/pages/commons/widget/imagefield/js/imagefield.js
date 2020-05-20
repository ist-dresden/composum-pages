(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.widgets = window.composum.pages.widgets || {};

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            imagefield: {
                css: {
                    base: 'imagefield-widget',
                    edit: 'composum-pages-edit-widget',
                    _preview: '_preview',
                    _picture: '_picture',
                    _data: '_data'
                },
                url: {
                    base: '/bin/cpm/pages/assets',
                    _data: '.assetData.json'
                },
                profile: 'imagefield'
            }
        });

        widgets.ImageFieldWidget = widgets.PathFieldWidget.extend({

            initialize: function (options) {
                var c = widgets.const.imagefield.css;
                widgets.PathFieldWidget.prototype.initialize.apply(this, [options]);
                this.$preview = this.$('.' + c.edit + c._preview);
                this.$previewImage = this.$preview.find('.' + c.edit + c._picture);
                this.$data = this.$preview.find('.' + c.edit + c._data);
                this.$input.on('change.imagefield', _.bind(this.onChange, this));
                this.onPreviewShown();
            },

            profileAspect: function () {
                return widgets.const.imagefield.profile;
            },

            onChange: function () {
                this.$previewImage.attr('src', this.getValue());
                this.onPreviewShown();
            },

            onPreviewShown: function () {
                var c = widgets.const.imagefield.css;
                var path = this.getValue();
                this.$preview.removeClass().addClass(c.edit + c._preview);
                this.$data.html('');
                if (path) {
                    var u = widgets.const.imagefield.url;
                    core.ajaxGet(u.base + u._data + core.encodePath(path), {}, _.bind(function (data) {
                        var mimeType = data.asset.mimeType;
                        if (mimeType) {
                            this.$preview.addClass(mimeType.replace(/[/+]/g, ' '));
                            this.$data.append('<div>' + mimeType + '</div>')
                        }
                        if (data.asset.lastModified) {
                            this.$data.append('<div>' + data.asset.lastModified + '</div>')
                        }
                    }, this));
                }
            }
        });

        window.widgets.register('.widget.' + widgets.const.imagefield.css.base, widgets.ImageFieldWidget);


    })(window.composum.pages.widgets, window.composum.pages, window.core);
})(window);
