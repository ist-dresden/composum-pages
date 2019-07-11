(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.debug = window.composum.pages.debug || {};

    (function (debug, elements, pages, core) {
        'use strict';

        debug.const = _.extend(debug.const || {}, {
            url: {
                content: '/libs/composum/pages/stage/edit/page/debug.html'
            }
        });

        debug.PageView = Backbone.View.extend({

            initialize: function (options) {
                this.$top = this.$('.value-top');
                this.$left = this.$('.value-left');
                this.$width = this.$('.value-width');
                this.$height = this.$('.value-height');
                window.addEventListener('scroll', _.bind(this.refresh, this));
            },

            refresh: function (event) {
                this.$top.text(document.body.scrollTop);
                this.$left.text(document.body.scrollLeft);
                this.$width.text(document.body.clientWidth);
                this.$height.text(document.body.clientHeight);
            }
        });

        debug.DndView = Backbone.View.extend({

            initialize: function (options) {
                this.$pointer = this.$('.pointer');
                this.$x = this.$('.value-x');
                this.$y = this.$('.value-y');
                this.$count = this.$('.value-count');
                this.$ipos = this.$('.value-ipos');
                this.$path = this.$('.path');
                debug.$body
                    .on('dragenter.Debug', _.bind(this.onDragEnter, this))
                    .on('dragover.Debug', _.bind(this.onDragOver, this))
                    .on('drop.Debug', _.bind(this.onDrop, this));
            },

            onDragEnter: function (event) {
                this.refresh(event);
            },

            onDragOver: function (event) {
                this.refresh(event);
            },

            onDrop: function (event) {
                this.refresh(event);
            },

            refresh: function (event) {
                this.$pointer.css('top', event.pageY - document.body.scrollTop - 10);
                this.$pointer.css('left', event.pageX - document.body.scrollLeft - 10);
                //this.$x.text(event.pageX);
                //this.$y.text(event.pageY);
                var pointer = elements.pageBody.getPointer(event);
                this.$x.text(pointer.x);
                this.$y.text(pointer.x);
                var insert = elements.pageBody.dnd.insert;
                this.$ipos.text(insert ? (insert.vertical
                    ? insert.y + insert.handlePos.top + 3 : insert.x + insert.handlePos.left + 3) : '');
                var target = elements.pageBody.getPointerComponent(event, '.' + elements.const.class.container);
                if (target) {
                    this.$count.text(target.elements.length);
                    this.$path.text(target.reference.path.replace(/^.*\/jcr:content\//, './'));
                } else {
                    this.$count.text('');
                    this.$path.text('???');
                }
            }
        });

        debug.PtrView = Backbone.View.extend({

            initialize: function (options) {
                this.$x = this.$('.value-x');
                this.$y = this.$('.value-y');
                this.$path = this.$('.path');
                this.$x1 = this.$('.value-x1');
                this.$y1 = this.$('.value-y1');
                this.$x2 = this.$('.value-x2');
                this.$y2 = this.$('.value-y2');
                debug.$body.on('mousemove.Debug', _.bind(this.refresh, this));
            },

            refresh: function (event) {
                this.$x.text(event.pageX);
                this.$y.text(event.pageY);
                var target = elements.pageBody.getPointerComponent(event, '.' + elements.const.class.component);
                if (target) {
                    this.$path.text(target.reference.path.replace(/^.*\/jcr:content\//, './'));
                    var rect = elements.pageBody.getViewRect(target.$el);
                    this.$x1.text(Math.round(rect.x1));
                    this.$y1.text(Math.round(rect.y1));
                    this.$x2.text(Math.round(rect.x2));
                    this.$y2.text(Math.round(rect.y2));
                } else {
                    this.$path.text('???');
                    this.$x1.text('');
                    this.$y1.text('');
                    this.$x2.text('');
                    this.$y2.text('');
                }
            }
        });

        debug.Canvas = Backbone.View.extend({

            initialize: function (options) {
                debug.pageView = core.getView(this.$('.composum-pages-debug-frame_page-view'), debug.PageView);
                debug.dndView = core.getView(this.$('.composum-pages-debug-frame_dnd-view'), debug.DndView);
                debug.ptrView = core.getView(this.$('.composum-pages-debug-frame_ptr-view'), debug.PtrView);
            }
        });

        debug.Frame = Backbone.View.extend({

            initialize: function (options) {
                debug.canvas = core.getView(this.$('.composum-pages-debug-frame_canvas'), debug.Canvas);
            }
        });

        core.ajaxGet(debug.const.url.content, {}, function (data) {
            debug.$body = $('body');
            debug.$body.append(data);
            debug.frame = core.getView('.composum-pages-debug-frame', debug.Frame);
        });

    })(window.composum.pages.debug, window.composum.pages.elements, window.composum.pages, window.core);
})(window);
