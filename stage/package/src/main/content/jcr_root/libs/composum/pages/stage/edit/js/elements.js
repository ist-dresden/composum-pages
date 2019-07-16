/**
 * edit user interface functions embedded in a content page to support edit interaction
 * strong dependency to: 'invoke.js' ('commons.js'; libs: 'backbone.js', 'underscore.js', 'loglevel.js', 'jquery.js')
 */
(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.elements = window.composum.pages.elements || {};

    (function (elements, pages, core) {
        'use strict';

        elements.const = _.extend(elements.const || {}, {
            handle: { // selection handle CSS classes
                css: {
                    root: 'composum-pages-stage-edit-handles',
                    base: 'composum-pages-component-handle',
                    active: 'composum-pages-active-handle',
                    _pointer: '_pointer',
                    _selection: '_selection',
                    _visible: '_visible',
                    action: 'composum-pages-stage-edit-toolbar_button'
                }
            },
            dnd: { // DnD handle CSS classes
                css: {
                    base: 'composum-pages-stage-edit-dnd',
                    _handle: '_handle',
                    _visible: '_visible',
                    _disabled: '_disabled',
                    _target: '_target',
                    _targetOver: '_target-over',
                    insert: {
                        _vertical: '_vertical',
                        _horizontal: '_horizontal'
                    }
                },
                id: '.pagesElements'
            },
            edit: { // editing interface urls and keys
                url: {
                    targets: '/bin/cpm/pages/edit.targetContainers.json',
                    dropzones: '/bin/cpm/pages/edit.filterDropZones.json',
                    toolbar: '/bin/cpm/pages/edit.editToolbar.html'
                }
            }
        });

        //
        // drop zones
        //

        elements.DropZone = Backbone.View.extend({

            initialize: function (options) {
                var d = pages.const.commons.data;
                // set up the parent component DOM element (container or element)
                this.$parent = this.$el.parent().closest('.' + elements.const.class.component);
                // drop zone property data
                var encoded = this.$el.data(d.encoded);
                this.data = JSON.parse(atob(encoded));
            }
        });

        //
        // component view classes
        //

        /**
         * the general Component view base class (for elements and containers)
         */
        elements.Component = Backbone.View.extend({

            initialize: function (options) {
                // collect the component reference data
                this.reference = new pages.Reference(this);
                // set up the parent component DOM element
                this.$parent = this.$el.parent().closest('.' + elements.const.class.component);
                // determine the draggable settings an set up the DnD event handling
                this.draggable = core.parseBool(this.$el.attr('draggable'));
                if (this.draggable) {
                    this.$el
                        .on('dragstart', _.bind(this.onDragStart, this))
                        .on('dragend', _.bind(this.onDragEnd, this));
                }
                // set up the component selection handling
                this.$el
                    .on('mouseover', _.bind(elements.pageBody.onMouseOver, elements.pageBody))
                    .on('mouseout', _.bind(elements.pageBody.onMouseOver, elements.pageBody))
                    .on('click', _.bind(this.onClick, this));
            },

            /**
             * returns the resource name for visualization
             */
            getName: function () {
                return this.reference.name;
            },

            /**
             * returns the shortened path for visualization
             */
            getPathHint: function () {
                var path = this.reference.path;
                path = path.replace(/^\/content\/.*\/jcr:content\//, './');
                path = path.replace(/\/[^\/]*$/, '/');
                return path;
            },

            /**
             * returns the shortened resource type for visualization
             */
            getTypeHint: function () {
                var type = this.reference.type;
                type = type.replace(/^(.*\/)?composum\/(.*\/)?pages\//, '$2');
                type = type.replace(/\/components?\//, '/');
                type = type.replace(/\/containers?\//, '/');
                type = type.replace(/\/composites?\//, '/');
                type = type.replace(/\/elements?\//, '/');
                return type;
            },

            /**
             * returns the current dimensions for visualization
             */
            getSizeHint: function () {
                return Math.round(this.$el.width()) + 'x' + Math.round(this.$el.height()) + 'px';
            },

            /**
             * returns the 'draggable' component of a component (probably one of the parents)
             * @param component  the component to check (undefined: use this component)
             */
            getDraggable: function (component) {
                if (!component) {
                    component = this;
                }
                while (component && !component.draggable) {
                    component = component.parent;
                }
                return component;
            },

            // event handling

            onClick: function (event) {
                if (event) {
                    event.preventDefault();
                }
                if (elements.pageBody.pointer.component) {
                    elements.pageBody.setSelection(elements.pageBody.pointer.component);
                }
                return false;
            },

            // DnD forwarding

            onDragStart: function (event) {
                // determine the best matching draggable component as the drag source to use
                // necessary if the component itself is selectable but not draggable (static include)
                var draggable = elements.pageBody.getPointerComponent(event,
                    '.' + elements.const.class.component + '[draggable="true"]');
                if (draggable) {
                    elements.pageBody.onDragStart(event, draggable);
                } else {
                    event.preventDefault();
                    return false;
                }
            },

            onDragEnd: function (event) {
                elements.pageBody.onDragEnd(event);
            },

            // editing

            /**
             * calls the callback(component,html) function with the toolbar HTML snippet
             */
            getToolbar: function (callback) {
                if (this.toolbar) {
                    callback(this, this.toolbar);
                } else {
                    core.ajaxGet(elements.const.edit.url.toolbar + this.reference.path, {
                            data: {
                                type: this.reference.type
                            }
                        },
                        _.bind(function (data) {
                            this.toolbar = data;
                            callback(this, this.toolbar);
                        }, this));
                }
            }
        });

        /**
         * the Element view class derived from the Component class
         */
        elements.Element = elements.Component.extend({

            initialize: function (options) {
                elements.Component.prototype.initialize.apply(this, [options]);
            }
        });

        /**
         * the Container view class derived from the Component class
         */
        elements.Container = elements.Component.extend({

            initialize: function (options) {
                elements.Component.prototype.initialize.apply(this, [options]);
                this.elements = [];
            }
        });

        //
        // component selection
        //

        /**
         * The general Handle view (base) class for visualization of component selections.
         */
        elements.Handle = Backbone.View.extend({

            initialize: function (options) {
                var c = elements.const.handle.css;
                this.$top = this.$('.' + c.base + '_top');
                this.$left = this.$('.' + c.base + '_left');
                this.$right = this.$('.' + c.base + '_right');
                this.$bottom = this.$('.' + c.base + '_bottom');
                this.$head = this.$('.' + c.base + '_head');
                this.$toolbar = this.$('.' + c.base + '_toolbar');
                this.$path = this.$('.' + c.base + '_path');
                this.$name = this.$('.' + c.base + '_name');
                this.$type = this.$('.' + c.base + '_type');
                this.$size = this.$('.' + c.base + '_size');
                $(window).resize(_.bind(this.onResize, this));
            },

            /**
             * binds the handle to a component
             */
            setComponent: function (component, force) {
                if (component) {
                    if (this.component !== component || force) {
                        var c = elements.const.handle.css;
                        this.component = component;
                        this.setBounds(component);
                        this.$name.text(component.getName());
                        this.$path.text(component.getPathHint());
                        this.$type.text(component.getTypeHint());
                        this.$size.text(component.getSizeHint());
                        this.$el.addClass(c.base + c._visible);
                        var isDraggable = core.parseBool(component.getDraggable());
                        this.$top.attr('draggable', isDraggable);
                        this.$head.attr('draggable', isDraggable);
                        this.$left.attr('draggable', isDraggable);
                        this.$right.attr('draggable', isDraggable);
                        this.$bottom.attr('draggable', isDraggable);
                        component.getToolbar(_.bind(function (component, html) {
                            this.$toolbar.html(html);
                            this.$toolbar.find('.' + elements.const.handle.css.action)
                                .on('mouseover', _.bind(this.onMouseOver, this))
                                .on('mouseout', _.bind(this.onMouseOver, this))
                                .on('click', _.bind(this.onActionClick, this));
                        }, this));
                        return true;
                    }
                    return false;
                } else {
                    this.hide();
                }
            },

            /**
             * adapts the bounds of the handle to the bounds of the component
             */
            setBounds: function (component) {
                this.toolsBounds = elements.pageBody.getViewRect(component.$el);
                this.toolsBounds.y2 = this.toolsBounds.y1 + (this.toolsBounds.h = 27);
                var handlePos = elements.pageBody.$handles.offset();
                var b = elements.pageBody.getViewRect(component.$el, {
                    dx: -handlePos.left,
                    dy: -handlePos.top
                });
                this.$top.css('top', b.y1).css('left', b.x1 + 4).css('width', b.w - 8);
                this.$left.css('top', b.y1).css('left', b.x1).css('height', b.h);
                this.$right.css('top', b.y1).css('left', b.x1 + b.w - 4).css('height', b.h);
                this.$bottom.css('left', b.x1 + 4).css('top', b.y1 + b.h - 4).css('width', b.w - 8);
            },

            hide: function () {
                if (this.component) {
                    var c = elements.const.handle.css;
                    this.$el.removeClass(c.base + c._visible);
                    this.component = undefined;
                }
            },

            /**
             * returns the DOM element of the handles component if 'domEl' is one of the handles elements;
             * returns 'undefined' if the 'domEl' does not match to the handle
             */
            getComponentEl: function (domEl) {
                if (domEl) {
                    if (domEl === this.el || domEl === this.$top[0] || domEl === this.$left[0] ||
                        domEl === this.$right[0] || domEl === this.$bottom[0] || domEl === this.$path[0] ||
                        domEl === this.$name[0] || domEl === this.$type[0] || domEl === this.$size[0]) {
                        return this.component.el;
                    }
                }
                return undefined;
            },

            setHeadVisibility: function (visible) {
                if (visible) {
                    elements.pageBody.$handles.addClass(elements.const.handle.css.active);
                } else {
                    elements.pageBody.$handles.removeClass(elements.const.handle.css.active);
                }
            },

            // event handling

            onActionClick: function (event) {
                if (event) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                if (this.component) {
                    var action = $(event.currentTarget).data('action');
                    elements.triggerAction(action, this.component.reference);
                }
                return false;
            },

            onMouseOver: function (event) {
                if (event) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                return false;
            },

            onResize: function (event) {
                if (this.component) {
                    this.setBounds(this.component);
                }
            },

            // DnD forwarding

            onDragStart: function (event) {
                if (this.component) {
                    this.component.onDragStart(event);
                }
            }
        });

        elements.Pointer = elements.Handle.extend({});

        elements.Selection = elements.Handle.extend({});

        //
        // Drag and Drop handling
        //

        /**
         * The view for the drag and drop behavior of the page editing UI
         *   - generates the visual content for the drag operations
         *   - handles all drag and drop events
         *   - controls the DnD target visualization
         *   - controls the insert marker rendering
         */
        elements.DndHandle = Backbone.View.extend({

            initialize: function (options) {
                var c = elements.const.dnd.css;
                this.$insert = this.$('.' + c.base + '_insert');
                this.$image = this.$('.' + c.base + '_image');
                this.$content = this.$image.find('.' + c.base + '_content');
                this.$overlay = this.$image.find('.' + c.base + '_overlay');
                this.$path = this.$overlay.find('.' + c.base + '_path');
                this.$name = this.$overlay.find('.' + c.base + '_name');
                this.$type = this.$overlay.find('.' + c.base + '_type');
            },

            reset: function () {
                var c = elements.const.dnd.css;
                this.clearDropZones();
                this.clearTargets();
                this.$el.removeClass(c.base + c._visible);
                this.currentReference = undefined;
            },

            // event handlers

            onDragStart: function (event, object) {
                var self = elements.pageBody.dnd;
                var dnd = core.dnd.getDndData(event);
                var reference = object.reference;
                if (!self.currentReference) {
                    var lgr = elements.log.dnd;
                    var c = elements.const.dnd.css;
                    self.currentReference = reference;
                    var data = object;
                    if (object instanceof elements.Component) {
                        if (lgr.getLevel() <= log.levels.DEBUG) {
                            lgr.debug('elements.dnd.onDragStart(' + object.reference.path + ')');
                        }
                        self.reset();
                        pages.current.dnd.object = data = {
                            type: 'element',
                            reference: reference
                        };
                        var jsonData = JSON.stringify(data);
                        dnd.ev.dataTransfer.setData('application/json', jsonData);
                        dnd.ev.dataTransfer.effectAllowed = 'move';
                        parent.postMessage(elements.const.event.dnd.object + jsonData, '*');
                        if (_.isFunction(dnd.ev.dataTransfer.setDragImage)) {
                            var pos = object.$el.offset();
                            self.$image.css({
                                width: Math.max(100, object.$el.width()) + 'px'
                            });
                            self.$content.html('').append(object.$el.clone());
                            self.$image.addClass(c.base + c._visible);
                            dnd.ev.dataTransfer.setDragImage(self.$image[0], Math.max(0, dnd.ev.pageX - pos.left), 50);
                            window.setTimeout(_.bind(function () {
                                self.$content.html('');
                                self.$image.removeClass(c.base + c._visible);
                            }, self), 100);
                        }
                    }
                    window.setTimeout(_.bind(function () {
                        if (data.type === 'component' || data.type === 'element') {
                            self.markTargets(reference);
                        } else {
                            self.markDropZones(reference);
                        }
                    }, self), 150);
                }
            },

            onDragEnd: function (event) {
                var lgr = elements.log.dnd;
                var self = elements.pageBody.dnd;
                if (lgr.getLevel() <= log.levels.DEBUG) {
                    lgr.debug('elements.dnd.onDragEnd()');
                }
                self.reset();
                parent.postMessage(elements.const.event.dnd.finished + '{}', '*');
            },

            onDrop: function (event) {
                elements.pageBody.onDrop(event);
            },

            // zone markers

            markDropZones: function (reference) {
                var c = elements.const.dnd.css;
                var candidates = [];
                elements.pageBody.dropZones.forEach(function (candidate) {
                    candidates.push({
                        id: candidate.$el.attr('id'),
                        path: candidate.data.path,
                        property: candidate.data.property,
                        filter: candidate.data.filter
                    });
                    candidate.$el.addClass(c.base + c._disabled);
                });
                var path = reference.path;
                core.ajaxPut(elements.const.edit.url.dropzones + path, JSON.stringify(candidates), {},
                    _.bind(function (result) {
                        this.dropZones = [];
                        result.forEach(function (dropZone) {
                            var $target = elements.pageBody.$('.' + elements.const.class.dropzone + '[id="' + dropZone.id + '"]');
                            if ($target.length === 1) {
                                var view = $target[0].view;
                                if (view) {
                                    this.dropZones.push(view);
                                    view.$el.addClass(c.base + c._target);
                                    view.$el.removeClass(c.base + c._disabled);
                                }
                            }
                        }, this);
                    }, this));
            },

            clearDropZones: function () {
                var c = elements.const.dnd.css;
                this.setDragZone();
                elements.pageBody.dropZones.forEach(function (dropZone) {
                    dropZone.$el.removeClass(c.base + c._targetOver);
                    dropZone.$el.removeClass(c.base + c._target);
                }, this);
                this.dropZones = undefined;
            },

            getDragZone: function (event) {
                return elements.pageBody.getPointerComponent(event,
                    '.' + elements.const.class.dropzone, _.bind(this.isDropZone, this));
            },

            isDropZone: function (view) {
                if (this.dropZones) {
                    for (var i = 0; i < this.dropZones.length; i++) {
                        if (this.dropZones[i].data.path === view.data.path) {
                            return true;
                        }
                    }
                }
                return false;
            },

            setDragZone: function (zone, event) {
                var lgr = elements.log.dnd;
                var c = elements.const.dnd.css;
                if (this.dragZone && this.dragZone !== zone) {
                    if (lgr.getLevel() <= log.levels.DEBUG) {
                        lgr.debug('elements.dnd.dragZone.clear! (' + this.dragZone.$el.attr('id') + ')');
                    }
                    this.dragZone.$el.removeClass(c.base + c._targetOver);
                    this.dragZone = undefined;
                }
                if (zone && this.dragZone !== zone) {
                    if (lgr.getLevel() <= log.levels.DEBUG) {
                        var pointer = elements.pageBody.getPointer(event);
                        lgr.debug('elements.dnd.dragZone.set: ' + zone.$el.attr('id') + ' ' + JSON.stringify(pointer));
                    }
                    this.dragZone = zone;
                    zone.$el.addClass(c.base + c._targetOver);
                }
            },

            // target markers

            /**
             * returns 'true' if the container is element of the list of allowed targets
             * used directly as condition in '' to determine the drop target during drag
             */
            isTarget: function (container) {
                if (this.dropTargets) {
                    for (var i = 0; i < this.dropTargets.length; i++) {
                        if (this.dropTargets[i].reference.path === container.reference.path) {
                            return true;
                        }
                    }
                }
                return false;
            },

            /**
             * Determines the list of allowed target containers (this.dropTargets)
             * for the given component (the drag source) and marks this containers.
             * @param reference the reference of the element which should be inserted in an(other) container
             */
            markTargets: function (reference) {
                var c = elements.const.dnd.css;
                var candidates = [];
                elements.pageBody.containers.forEach(function (candidate) {
                    candidates.push({
                        path: candidate.reference.path,
                        type: candidate.reference.type
                    });
                });
                var path = reference.path;
                core.ajaxPost(elements.const.edit.url.targets + path, {
                    type: reference.type,
                    targetList: JSON.stringify(candidates)
                }, {}, _.bind(function (result) {
                    this.dropTargets = [];
                    result.forEach(function (target) {
                        var $target = elements.pageBody.$('.' + elements.const.class.container
                            + '[data-' + elements.const.data.path + '="' + target.path + '"]');
                        if ($target.length === 1) {
                            var view = $target[0].view;
                            if (view) {
                                this.dropTargets.push(view);
                                view.$el.addClass(c.base + c._target);
                            }
                        }
                    }, this);
                }, this));
            },

            clearTargets: function () {
                var c = elements.const.dnd.css;
                this.setDragTarget();
                elements.pageBody.containers.forEach(function (container) {
                    container.$el.removeClass(c.base + c._targetOver);
                    container.$el.removeClass(c.base + c._target);
                }, this);
                this.dropTargets = undefined;
            },

            // move / insert handling

            getDragTarget: function (event) {
                return elements.pageBody.getPointerComponent(event,
                    '.' + elements.const.class.container, _.bind(this.isTarget, this));
            },

            setDragTarget: function (container, event) {
                var lgr = elements.log.dnd;
                var c = elements.const.dnd.css;
                if (this.dragTarget && this.dragTarget !== container) {
                    if (lgr.getLevel() <= log.levels.DEBUG) {
                        lgr.debug('elements.dnd.dragTarget.clear! (' + this.dragTarget.reference.path + ')');
                    }
                    this.dragTarget.$el.removeClass(c.base + c._targetOver);
                    this.$insert.removeClass(c.base + c._visible);
                    this.$insert.removeClass(c.base + c.insert._vertical);
                    this.$insert.removeClass(c.base + c.insert._horizontal);
                    this.insert = undefined;
                    this.dragTarget = undefined;
                }
                if (container) {
                    if (this.dragTarget === container) {
                        this.moveInsertMarker(event);
                    } else {
                        if (lgr.getLevel() <= log.levels.DEBUG) {
                            var pointer = elements.pageBody.getPointer(event);
                            lgr.debug('elements.dnd.dragTarget.set: ' + container.reference.path + ' ' + JSON.stringify(pointer));
                        }
                        this.dragTarget = container;
                        container.$el.addClass(c.base + c._targetOver);
                        this.initInsertMarker(container);
                        this.moveInsertMarker(event);
                        this.$insert.addClass(c.base + c._visible);
                    }
                }
            },

            initInsertMarker: function (container) {
                var c = elements.const.dnd.css;
                this.insert = {
                    handlePos: elements.pageBody.$handles.offset(),
                    containerRect: elements.pageBody.getViewRect(container.$el),
                    vertical: true,
                    before: undefined
                };
                if (container.elements.length > 0) {
                    var eRect = elements.pageBody.getViewRect(container.elements[0].$el);
                    if (container.elements.length > 1) {
                        var eRect2 = elements.pageBody.getViewRect(container.elements[1].$el);
                        this.insert.vertical = eRect.y2 <= eRect2.y1 || eRect2.y2 <= eRect.y1;
                    } else {
                        this.insert.vertical = Math.abs(this.insert.containerRect.x1 - eRect.x1)
                            <= Math.abs(this.insert.containerRect.y1 - eRect.y1);
                    }
                    if (this.insert.vertical) {
                        this.insert.x = this.insert.containerRect.x1 - this.insert.handlePos.left;
                        this.insert.w = this.insert.containerRect.w;
                        this.$insert.css('left', this.insert.x);
                        this.$insert.css('width', this.insert.w);
                        this.$insert.css('height', 0);
                        this.$insert.addClass(c.base + c.insert._vertical);
                    } else {
                        this.insert.y = this.insert.containerRect.y1 - this.insert.handlePos.top;
                        this.insert.h = this.insert.containerRect.h;
                        this.$insert.css('top', this.insert.y);
                        this.$insert.css('height', this.insert.h);
                        this.$insert.css('width', 0);
                        this.$insert.addClass(c.base + c.insert._horizontal);
                    }
                } else {
                    this.insert.x = this.insert.containerRect.x1 - this.insert.handlePos.left;
                    this.insert.y = this.insert.containerRect.y1 + this.insert.containerRect.h / 2 - this.insert.handlePos.top;
                    this.insert.w = this.insert.containerRect.w;
                    this.$insert.css('top', this.insert.y);
                    this.$insert.css('left', this.insert.x);
                    this.$insert.css('width', this.insert.w);
                    this.$insert.css('height', 0);
                    this.$insert.addClass(c.base + c.insert._vertical);
                }
            },

            moveInsertMarker: function (event) {
                var pointer = elements.pageBody.getPointer(event);
                for (var i = 0; i < this.dragTarget.elements.length; i++) {
                    var element = this.dragTarget.elements[i];
                    var eRect = elements.pageBody.getViewRect(element.$el);
                    if (this.insert.vertical) {
                        if (pointer.y >= eRect.y1 && pointer.y <= eRect.y2) {
                            if (pointer.y > eRect.y1 + eRect.h / 2) {
                                this.insert.y = eRect.y2;
                                this.insert.before = i + 1 < this.dragTarget.elements.length
                                    ? this.dragTarget.elements[i + 1] : undefined;
                            } else {
                                this.insert.y = eRect.y1;
                                this.insert.before = element;
                            }
                            this.insert.y -= this.insert.handlePos.top + 3;
                            this.$insert.css('top', this.insert.y);
                            break;
                        }
                    } else {
                        if (pointer.x >= eRect.x1 && pointer.x <= eRect.x2) {
                            if (pointer.x > eRect.x1 + eRect.w / 2) {
                                this.insert.x = eRect.x2;
                                this.insert.before = i + 1 < this.dragTarget.elements.length
                                    ? this.dragTarget.elements[i + 1] : undefined;
                            } else {
                                this.insert.x = eRect.x1;
                                this.insert.before = element;
                            }
                            this.insert.x -= this.insert.handlePos.left + 3;
                            this.$insert.css('left', this.insert.x);
                            break;
                        }
                    }
                }
            }
        });

        //
        // page edit view
        //

        /**
         * The 'Page View' to control the editing of a page embedded in the Pages edit frame.
         */
        elements.PageBody = Backbone.View.extend({

            initialize: function (options) {
                var c = elements.const.handle.css;
                var d = elements.const.dnd.css;
                var e = elements.const.event;
                this.params = core.url.getParameters(window.location.search);
                // determine the editing UI components of the page
                this.$handles = this.$('.' + c.root);
                this.pointer = core.getWidget(this.el, '.' + c.base + c._pointer + ' .' + c.base, elements.Pointer);
                this.selection = core.getWidget(this.el, '.' + c.base + c._selection + ' .' + c.base, elements.Selection);
                this.dnd = core.getWidget(this.el, '.' + d.base + d._handle, elements.DndHandle);
                // register the handlers for component selection in interaction with the edit frame
                $(document).on(e.element.selected, _.bind(this.onElementSelected, this));
                $(document).on(e.element.select, _.bind(this.selectElement, this));
                window.addEventListener("message", _.bind(this.onMessage, this), false);
                var id = elements.const.dnd.id;
                this.$el
                //.on('mouseover' + id, _.bind(this.onMouseOver, this))
                //.on('mouseout' + id, _.bind(this.onMouseOver, this))
                    .on('dragenter' + id, _.bind(this.onDragEnter, this))
                    .on('dragover' + id, _.bind(this.onDragOver, this))
                    .on('drop' + id, _.bind(this.onDrop, this));
            },

            initComponents: function () {
                // determine the set of containers and elements of the current page as Component view instances
                this.components = [];
                this.containers = [];
                this.containerRefs = [];
                this.elements = [];
                this.dropZones = [];
                var self = this;
                this.$('.' + elements.const.class.container).each(function () {
                    var view = core.getView(this, elements.Container);
                    self.components.push(view);
                    self.containers.push(view);
                    self.containerRefs.push(view.reference);
                });
                this.$('.' + elements.const.class.element).each(function () {
                    var view = core.getView(this, elements.Element);
                    self.components.push(view);
                    self.elements.push(view);
                });
                // build the component view hierarchy
                this.components.forEach(function (component) {
                    if (component.$parent.length === 1) {
                        component.parent = component.$parent[0].view;
                    }
                    if (component.parent && component.parent.$el.hasClass(elements.const.class.container)) {
                        component.container = component.parent;
                        component.container.elements.push(component);
                    }
                }, this);
                this.$('.' + elements.const.class.dropzone).each(function () {
                    var view = core.getView(this, elements.DropZone);
                    self.dropZones.push(view);
                });
                // send container references to the edit frame
                parent.postMessage(elements.const.event.page.containerRefs
                    + JSON.stringify(this.containerRefs), '*');
            },

            getElementIndex: function (path) {
                var result = [];
                var i;
                for (i = 0; i < this.components.length; i++) {
                    if (this.components[i].reference.path === path) {
                        result.push({component: i});
                    }
                }
                for (i = 0; i < this.containers.length; i++) {
                    if (this.containers[i].reference.path === path) {
                        result.push({container: i});
                    }
                }
                for (i = 0; i < this.elements.length; i++) {
                    if (this.elements[i].reference.path === path) {
                        result.push({element: i});
                    }
                }
                return result;
            },

            // change event handling

            elementInserted: function (event, reference) {
                this.redraw(new pages.Reference(undefined, reference.name === '*'
                    ? reference.path : core.getParentPath(reference.path)));
            },

            elementChanged: function (event, reference) {
                this.redraw(reference);
            },

            elementDeleted: function (event, reference) {
                this.clear(reference);
            },

            /**
             * render a piece of the page content to refresh a (changed) part of the page
             * @param reference the element to refresh as a reference to the repository
             */
            redraw: function (reference) {
                this.dnd.reset();
                var selection = elements.pageBody.selection.component;
                if (selection) {
                    selection = selection.reference;
                }
                var index = this.getElementIndex(reference.path);
                var toRefresh = []; // collect the views of the element on the current page...
                var i;
                for (i = 0; i < index.length; i++) {
                    if (index[i].component >= 0) {
                        toRefresh.push(this.components[index[i].component]);
                    }
                }
                for (i = 0; i < toRefresh.length; i++) {
                    this.redrawComponent(toRefresh[i], i + 1 < toRefresh.length ? undefined : _.bind(function () {
                        // reinitialize view after last refresh (hoping that the reload calls are serialized)
                        window.setTimeout(_.bind(function () {
                            this.initComponents();
                            if (selection) {
                                this.selectPath(selection.path, true);
                            }
                        }, this), 200);
                    }, this));
                }
            },

            /**
             * refresh the view of one component (element or container)
             * @param component the component view; this views content is replaced with new HTML code
             * @param callback some things to do after reload of the HTML code (optional)
             */
            redrawComponent: function (component, callback) {
                core.ajaxGet(component.reference.path + '.html', {
                    data: _.extend(this.params, {
                        type: component.reference.type
                    })
                }, _.bind(function (content) {
                    component.$el.replaceWith(content);
                    if (_.isFunction(callback)) {
                        callback(this);
                    }
                }, this), _.bind(function (content) {
                    // reload page if an error has been occurred
                    window.location.reload();
                }, this));
            },


            /**
             * remove a piece of the page content to drop a (deleted) part of the page
             * @param reference the element to drop as a reference to the repository
             */
            clear: function (reference) {
                this.dnd.reset();
                var selection = elements.pageBody.selection.component;
                if (selection) {
                    selection = selection.reference;
                }
                var index = this.getElementIndex(reference.path);
                for (var i = 0; i < index.length; i++) {
                    if (index[i].component >= 0) {
                        this.components[index[i].component].$el.remove();
                    }
                }
                this.initComponents();
                if (selection) {
                    this.selectPath(selection.path, true);
                }
            },

            // DnD

            /**
             * forward drag start to the DnD handle on start dragging from inside or outside
             * @param event the jQuery DnD event
             * @param object the dragged object (component from inside, DbD object from outside)
             */
            onDragStart: function (event, object) {
                this.dnd.onDragStart(event, object);
            },

            /**
             * start drag handling on drag from outside of the page
             * @param event the jQuery DnD event
             */
            onDragEnter: function (event) {
                var lgr = elements.log.dnd;
                event.preventDefault();
                if (lgr.getLevel() <= log.levels.TRACE) {
                    lgr.trace('elements.dndEnter(' + '' + ')');
                }
                if (pages.current.dnd.object) {
                    this.onDragStart(event, pages.current.dnd.object);
                }
                return false;
            },

            /**
             * determine the target for the current drag position
             * @param event the jQuery DnD event
             */
            onDragOver: function (event) {
                var lgr = elements.log.dnd;
                event.preventDefault();
                var dnd = core.dnd.getDndData(event);
                var object = pages.current.dnd.object;
                if (object) {
                    if (object.type === 'component' || object.type === 'element') {
                        var ref = dnd.el.view.reference;
                        var target = this.dnd.getDragTarget(event);
                        if (lgr.getLevel() <= log.levels.TRACE) {
                            lgr.trace('elements.dndOver(' + (ref ? ref.path : 'body') + '): '
                                + JSON.stringify(dnd.pos) + " - " + (target ? target.reference.path : '?'));
                        }
                        this.dnd.setDragTarget(target, event);
                    } else {
                        var zone = this.dnd.getDragZone(event);
                        if (lgr.getLevel() <= log.levels.TRACE) {
                            lgr.trace('elements.dndOver('
                                + JSON.stringify(dnd.pos) + " - " + (zone ? zone.$el.attr('id') : '?'));
                        }
                        this.dnd.setDragZone(zone, event);
                    }
                }
                return false;
            },

            /**
             * forward drag end to the DnD handle on end dragging from inside
             * @param event the jQuery DnD event
             */
            onDragEnd: function (event) {
                this.dnd.onDragEnd(event);
            },

            /**
             * performs the drop operation if DnD status enables a change by delegating the drop to the edit frame
             * @param event the jQuery DnD event
             */
            onDrop: function (event) {
                var lgr = elements.log.dnd;
                event.preventDefault();
                var object, target;
                if (this.dnd.dragTarget && this.dnd.insert) {
                    object = JSON.parse(event.originalEvent.dataTransfer.getData('application/json'));
                    target = this.dnd.dragTarget.reference;
                    var before = this.dnd.insert.before ? this.dnd.insert.before.reference : undefined;
                    if (lgr.getLevel() <= log.levels.DEBUG) {
                        lgr.debug('elements.dnd.onDrop(' + this.dnd.dragTarget.reference.path + '): '
                            + JSON.stringify(object) + ' > '
                            + JSON.stringify(target) + ' < '
                            + JSON.stringify(before)
                        );
                    }
                    parent.postMessage(elements.const.event.dnd.drop
                        + JSON.stringify({
                            target: {
                                container: {
                                    reference: {
                                        path: target.path,
                                        type: target.type
                                    }
                                },
                                before: before ? {
                                    reference: {
                                        path: before.path
                                    }
                                } : undefined
                            },
                            object: object
                        }), '*');
                } else if (this.dnd.dragZone) {
                    object = JSON.parse(event.originalEvent.dataTransfer.getData('application/json'));
                    target = this.dnd.dragZone.data;
                    if (lgr.getLevel() <= log.levels.DEBUG) {
                        lgr.debug('elements.dnd.onDrop(' + this.dnd.dragZone.$el.attr('id') + '): '
                            + JSON.stringify(object) + ' > '
                            + JSON.stringify(target)
                        );
                    }
                    parent.postMessage(elements.const.event.dnd.drop
                        + JSON.stringify({
                            zone: target,
                            object: object
                        }), '*');
                }
                return false;
            },

            // component selection and edit frame message handling

            onMouseOver: function (event) {
                var lgr = elements.log.ptr;
                if (event) {
                    event.preventDefault();
                }
                var component = this.getPointerComponent(event, '.' + elements.const.class.component);
                this.pointer.setComponent(component);
                if (this.selection.component) {
                    var pointer = this.getPointer(event);
                    if (lgr.getLevel() <= log.levels.TRACE) {
                        lgr.trace('elements.onMouseOver( ' + JSON.stringify(this.selection.toolsBounds) + ',' + JSON.stringify(pointer) + ')');
                    }
                    this.selection.setHeadVisibility(this.selection.component === component
                        || this.isInside(this.selection.toolsBounds, pointer));
                }
                return false;
            },

            selectPath: function (path, force) {
                var found = false;
                if (path) {
                    var $target;
                    do { // traverse upwards if 'force' and a path has no editable element on the page
                        $target = $('.' + elements.const.class.component
                            + '[data-' + elements.const.data.path + '="' + path + '"]');
                    } while ((!$target || $target.length < 1) && force && (path = core.getParentPath(path)) && path !== "/");
                    if ($target && $target.length > 0) {
                        var component = $target[0].view;
                        if (component) {
                            elements.log.std.debug('pages.elements.selectElement(' + path + ')');
                            this.setSelection(component, force);
                            found = true;
                        }
                    }
                }
                if (!found) {
                    this.clearSelection();
                }
            },

            setSelection: function (component, force) {
                var lgr = elements.log.std;
                if (lgr.getLevel() <= log.levels.DEBUG) {
                    lgr.debug('pages.elements.setSelection(' + component + ',' + force + ')');
                }
                if (elements.pageBody.selection.component !== component || force) {
                    if (component) {
                        var e = elements.const.event;
                        this.dnd.reset();
                        elements.pageBody.selection.setComponent(component, force);
                        elements.pageBody.selection.setHeadVisibility(true);
                        elements.trigger('elements.selection.set', e.element.selected, component.reference);
                    } else {
                        this.clearSelection();
                    }
                } else {
                    this.clearSelection();
                }
            },

            clearSelection: function () {
                var lgr = elements.log.std;
                this.dnd.reset();
                if (elements.pageBody.selection.component) {
                    var e = elements.const.event;
                    if (lgr.getLevel() <= log.levels.DEBUG) {
                        lgr.debug('pages.elements.clearSelection(' + elements.pageBody.selection.component + ')');
                    }
                    elements.pageBody.selection.setComponent(undefined);
                    elements.trigger('elements.selection.clear', e.element.selected, []);
                }
            },

            selectElement: function (event, refOrPath) {
                this.selectPath(refOrPath ? refOrPath.path : refOrPath, true);
            },

            onElementSelected: function (event, reference) {
                var lgr = elements.log.std;
                var e = elements.const.event;
                if (reference && reference.path) {
                    if (lgr.getLevel() <= log.levels.DEBUG) {
                        lgr.debug('pages.elements.' + e.element.selected + '(' + reference.path + ')');
                    }
                    parent.postMessage(e.element.selected + JSON.stringify({reference: reference}), '*');
                } else {
                    if (lgr.getLevel() <= log.levels.DEBUG) {
                        lgr.debug('pages.elements.selectionCleared()');
                    }
                    parent.postMessage(e.element.selected + JSON.stringify({}), '*');
                }
            },

            /**
             * the message handler for all messages sent from the edit frame to the edited page (this document)
             * @param event "<operation><argument-object|array-JSON>"
             */
            onMessage: function (event) {
                var lgr = elements.log.std;
                var e = elements.const.event;
                var message = e.messagePattern.exec(event.data);
                if (lgr.getLevel() <= log.levels.TRACE) {
                    lgr.trace('elements.message.on: "' + event.data + '"...');
                }
                if (message) {
                    var args = JSON.parse(message[2]); // argument object|array
                    switch (message[1]) { // operation
                        case e.element.select:
                            if (lgr.getLevel() <= log.levels.DEBUG) {
                                lgr.debug('elements.message.on.' + e.element.select + JSON.stringify(args));
                            }
                            if (args.reference && args.reference.path) {
                                elements.trigger('elements.msg.select', e.element.select, args.reference);
                            } else {
                                elements.trigger('elements.msg.select', e.element.select, []);
                            }
                            break;
                        case e.element.inserted:
                            if (lgr.getLevel() <= log.levels.DEBUG) {
                                lgr.debug('elements.message.on.' + e.element.inserted + JSON.stringify(args));
                            }
                            this.elementInserted(event, args.reference);
                            break;
                        case e.element.changed:
                            if (lgr.getLevel() <= log.levels.DEBUG) {
                                lgr.debug('elements.message.on.' + e.element.changed + JSON.stringify(args));
                            }
                            this.elementChanged(event, args.reference);
                            break;
                        case e.element.deleted:
                            if (lgr.getLevel() <= log.levels.DEBUG) {
                                lgr.debug('elements.message.on.' + e.element.deleted + JSON.stringify(args));
                            }
                            this.elementDeleted(event, args.reference);
                            break;
                        case e.dnd.object:
                            if (lgr.getLevel() <= log.levels.DEBUG) {
                                lgr.debug('elements.message.on.' + e.dnd.object + JSON.stringify(args));
                            }
                            pages.current.dnd.object = args;
                            break;
                        case e.dnd.finished:
                            if (lgr.getLevel() <= log.levels.DEBUG) {
                                lgr.debug('elements.message.on.' + e.dnd.finished);
                            }
                            pages.current.dnd.object = undefined;
                            elements.pageBody.dnd.reset();
                            break;
                    }
                }
            },

            // general UI functions

            /**
             * returns the mouse position {x,y} of the event
             */
            getPointer: function (event) {
                return {
                    x: event.pageX,
                    y: event.pageY
                };
            },

            isInside: function (viewRect, pointer) {
                var lgr = elements.log.ptr;
                var result = viewRect && pointer &&
                    pointer.x >= viewRect.x1 && pointer.x < viewRect.x2 &&
                    pointer.y >= viewRect.y1 && pointer.y < viewRect.y2;
                if (lgr.getLevel() <= log.levels.DEBUG) {
                    lgr.debug('elements.isInside( ' + JSON.stringify(viewRect) + ',' + JSON.stringify(pointer) + '): ' + result);
                }
                return result;
            },

            /**
             * returns the rectangle {x1,y1,x2,y2,w,h} of a view
             *             x1,y1 -- w ---- +
             *               |             |
             *               h             h
             *               |             |
             *               + ---- w -- x2,y2
             * @param view    the view instance
             * @param offset  an optional offset (move) {dx,dy} for the rectangle
             */
            getViewRect: function ($el, offset) {
                var viewPos = $el.offset();
                var rect = {
                    x1: viewPos.left,
                    y1: viewPos.top,
                    w: $el.outerWidth(),
                    h: $el.outerHeight()
                };
                if (offset) {
                    rect.x1 += offset.dx;
                    rect.y1 += offset.dy;
                }
                rect.x2 = rect.x1 + rect.w;
                rect.y2 = rect.y1 + rect.h;
                return rect;
            },

            /**
             * returns the component (view) on the events mouse position
             * which matches to the selector and the condition (if present; optional)
             * @param event      the current event object
             * @param selector   the jQuery selector rule for the view
             * @param condition  an optional condition (boolean function(view))
             * @returns {undefined}
             */
            getPointerComponent: function (event, selector, condition) {
                var component = undefined;
                var pointer = this.getPointer(event);
                var domEl = document.elementFromPoint(pointer.x - window.pageXOffset, pointer.y - window.pageYOffset);
                if (domEl) {
                    var handleEl;
                    if ((handleEl = (elements.pageBody.pointer.getComponentEl(domEl))
                        || elements.pageBody.selection.getComponentEl(domEl))) {
                        domEl = handleEl;
                    }
                    var $target = $(domEl).closest(selector);
                    if ($target.length > 0 && $target[0].view) {
                        component = this.getPointerView($target[0].view, pointer, selector, condition);
                    }
                }
                return component;
            },

            /**
             * Determine the best matching component (view) at a mouse position (deepest in the DOM).
             * @param view       the starting view
             * @param pointer    the (mouse) position
             * @param selector   the jQuery selector rule for the view
             * @param condition  an optional condition (boolean function(view))
             * @returns the view found or 'undefined' if no view is matching selector and condition
             */
            getPointerView: function (view, pointer, selector, condition) {
                if (view) {
                    var lgr = elements.log.std;
                    var self = this;
                    var viewRect = this.getViewRect(view.$el);
                    if (lgr.getLevel() <= log.levels.TRACE) {
                        lgr.trace('elements.getPointerView('
                            + (view.reference ? view.reference.path : view.data.path) + ', '
                            + JSON.stringify(pointer) + ' / ' + JSON.stringify(viewRect) + ', "'
                            + selector + '"' + (condition ? ' ++' : '') + ' ...');
                    }
                    var useNested = undefined; // search for a better matching nested child...
                    view.$el.find(selector).each(function () {
                        // search visual nested components
                        if (!useNested) {
                            var nested = this.view; // use the elements view
                            if (nested) {
                                var nestedRect = self.getViewRect(nested.$el);
                                if (lgr.getLevel() <= log.levels.TRACE) {
                                    lgr.trace('elements.getPointerView.try: '
                                        + (nested.reference ? nested.reference.path : nested.data.path) + ' '
                                        + JSON.stringify(nestedRect));
                                }
                                if (nestedRect.x1 <= pointer.x && nestedRect.y1 <= pointer.y &&
                                    nestedRect.x2 >= pointer.x && nestedRect.y2 >= pointer.y) {
                                    // uses nested only if it not overlaps the whole parent
                                    if (nestedRect.x1 - viewRect.x1 > 20 ||
                                        nestedRect.y1 - viewRect.y1 > 20 ||
                                        viewRect.x2 - nestedRect.x2 > 20 ||
                                        viewRect.y2 - nestedRect.y2 > 20) {
                                        // nested is matching but possibly is there another, better matching child (recursive)
                                        useNested = self.getPointerView(nested, pointer, selector, condition);
                                    }
                                }
                            }
                        }
                    });
                    if (_.isFunction(condition)) { // check optional condition ?
                        if (useNested) { // check nested if found
                            if (condition(useNested)) {
                                view = useNested;
                            } else { // if nested not matches check the view
                                if (!condition(view)) {
                                    view = undefined;
                                }
                            }
                        } else { // check view if no nested child found
                            if (!condition(view)) {
                                view = undefined;
                            }
                        }
                    } else {
                        if (useNested) { // use better matching nested child than the view
                            view = useNested;
                        }
                    }
                }
                if (lgr.getLevel() <= log.levels.TRACE) {
                    lgr.trace('elements.getPointerView: '
                        + (view ? (view.reference ? view.reference.path : view.data.path) : 'undefined'));
                }
                return view;
            }
        });

        elements.pageBody = core.getView('body.' + elements.const.class.editBody, elements.PageBody);
        elements.pageBody.initComponents();

    })(window.composum.pages.elements, window.composum.pages, window.core);
})(window);
