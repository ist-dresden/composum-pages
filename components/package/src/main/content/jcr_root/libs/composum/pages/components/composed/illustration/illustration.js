(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.components = window.composum.pages.components || {};

    (function (components, pages, core) {
        'use strict';

        components.const = _.extend(components.const || {}, {
            illustration: {
                cssBase: 'composum-pages-components-composed-illustration',
                shapeKey: '-annotation_shape',
                contentKey: '-annotation_content',
                nextKey: '-annotation_next'
            }
        });

        components.IllustrationShape = Backbone.View.extend({

            initialize: function (options) {
                var c = components.const.illustration;
                this.annotationId = this.$el.data('id');
                this.$annotation = $('#' + this.annotationId);
                this.$content = this.$annotation.find('.' + c.cssBase + c.contentKey);
                this.$next = this.$annotation.find('.' + c.cssBase + c.nextKey);
                this.$next.click(_.bind(this.next, this));
                this.$content.click(_.bind(this.drop, this));
                this.$el.click(_.bind(this.toggle, this));
            },

            drop: function (event) {
                if (event) {
                    event.preventDefault();
                }
                return false;
            },

            toggle: function (event) {
                if (event) {
                    event.preventDefault();
                }
                if (this.$annotation.hasClass('visible')) {
                    this.hide();
                } else {
                    if (this.illustration.behavior == 'accordion') {
                        this.illustration.hide();
                    }
                    this.show();
                }
                return false;
            },

            show: function () {
                this.$annotation.addClass('visible');
            },

            hide: function () {
                this.$annotation.removeClass('visible');
            },

            next: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var id = this.$next.data('id');
                if (id) {
                    var next = this.illustration.getShape(id);
                    if (next) {
                        this.hide();
                        next.show();
                    }
                }
                return false;
            }
        });

        components.Illustration = Backbone.View.extend({

            initialize: function (options) {
                var c = components.const.illustration;
                this.behavior = this.$el.data('behavior');
                var self = this;
                self.shapes = [];
                this.$('.' + c.cssBase + c.shapeKey).each(function () {
                    var shape = core.getWidget(self, this, components.IllustrationShape);
                    shape.illustration = self;
                    self.shapes.push(shape);
                });
                this.$el.click(_.bind(this.hide, this));
            },

            getShape: function (id) {
                for (var i = 0; i < this.shapes.length; i++) {
                    if (this.shapes[i].annotationId == id) {
                        return this.shapes[i];
                    }
                }
                return undefined;
            },

            hide: function (event) {
                for (var i = 0; i < this.shapes.length; i++) {
                    this.shapes[i].hide();
                }
                return true;
            }
        });

        $(document).ready(function () {
            $('.' + components.const.illustration.cssBase).each(function () {
                core.getView(this, components.Illustration);
            });
        });

    })(window.composum.pages.components, window.composum.pages, window.core);
})(window);
