(function () {
    'use strict';
    CPM.namespace('pages.widgets');

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            videofield: {
                css: {
                    base: 'videofield-widget',
                    edit: 'composum-pages-edit-widget',
                    _preview: '_preview',
                    _frame: '_frame',
                    _player: '_player',
                    _data: '_data'
                },
                url: {
                    base: '/bin/cpm/pages/assets',
                    _data: '.assetData.json',
                    player: '/bin/cpm/pages/get.include.player.html',
                    type: 'composum/pages/commons/widget/videofield'
                },
                profile: 'videofield'
            }
        });

        widgets.VideoFieldWidget = widgets.PathFieldWidget.extend({

            initialize: function (options) {
                var c = widgets.const.videofield.css;
                widgets.PathFieldWidget.prototype.initialize.apply(this, [options]);
                this.$preview = this.$('.' + c.edit + c._preview);
                this.$videoFrame = this.$preview.find('.' + c.edit + c._frame);
                this.$data = this.$preview.find('.' + c.edit + c._data);
                this.$input.on('change.videofield', _.bind(this.onChange, this));
                this.onChange();
            },

            profileAspect: function () {
                return widgets.const.videofield.profile;
            },

            onChange: function () {
                var c = widgets.const.videofield.css;
                var path = this.getValue();
                this.$preview.removeClass().addClass(c.edit + c._preview);
                this.$data.html('');
                if (path) {
                    var u = widgets.const.videofield.url;
                    var params = {resourceType: u.type};
                    if (/^https?:\/\//.exec(path)) { // fill players 'src' with external URL
                        this.$videoFrame.find('video').attr('src', path);
                    } else { // replace player by 'include' via Ajax with 'player' selector
                        core.ajaxGet(u.player + core.encodePath(path), {
                            data: {
                                resourceType: u.type
                            }
                        }, _.bind(function (content) {
                            this.$videoFrame.html(content);
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
                        }, this));
                    }
                }
            }
        });

        CPM.widgets.register('.widget.' + widgets.const.videofield.css.base, widgets.VideoFieldWidget);

    })(CPM.pages.widgets, CPM.pages, CPM.core);
})();
