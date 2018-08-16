(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.elements = window.composum.pages.elements || {};

    (function (elements, core) { // strong dependency to: 'invoke.js'
        'use strict';

        elements.const = _.extend(elements.const || {}, {
            handle: { // selection handle CSS classes
                handles: 'composum-pages-stage-edit-handles',
                pointer: 'composum-pages-component-handle_pointer',
                selection: 'composum-pages-component-handle_selection',
                class: {
                    base: 'composum-pages-component-handle',
                    visible: '_visible'
                }
            },
            dnd: { // DnD handle CSS classes
                class: {
                    base: 'composum-pages-stage-edit-dnd',
                    handle: '_handle',
                    visible: '_visible',
                    target: '_target',
                    targetOver: '_target-over',
                    insert: {
                        vertical: '_vertical',
                        horizontal: '_horizontal'
                    }
                }
            },
            edit: { // editing interface urls and keys
                url: {
                    targets: '/bin/cpm/pages/edit.targetContainers.json'
                }
            },
            log: { // logging switches (for debugging only)
                operation: true,
                dnd: {
                    event: false,
                    target: false
                },
                mouse: {
                    position: false
                }
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
                this.data = {
                    name: this.$el.data(elements.const.data.name),
                    path: this.$el.data(elements.const.data.path),
                    type: this.$el.data(elements.const.data.type)
                };
                // set up the parent component DOM element
                this.$parent = this.$el.parent().closest('.' + elements.const.class.component);
                // determine the draggable settings an set up the DnD event handling
                this.draggable = !!this.$el.attr('draggable');
                if (this.draggable) {
                    this.el.addEventListener('dragstart', _.bind(this.onDragStart, this), false);
                }
                // set up the component selection handling
                this.$el.mouseover(_.bind(this.onMouseOver, this));
                this.$el.mouseout(_.bind(this.onMouseOver, this));
                this.$el.click(_.bind(this.onClick, this));
            },

            /**
             * returns the resource name for visualization
             */
            getName: function () {
                return this.data.name;
            },

            /**
             * returns the shortened path for visualization
             */
            getPathHint: function () {
                var path = this.data.path;
                path = path.replace(/^\/content\/.*\/jcr:content\//, './');
                path = path.replace(/\/[^\/]*$/, '/');
                return path;
            },

            /**
             * returns the shortened resource type for visualization
             */
            getTypeHint: function () {
                var type = this.data.type;
                type = type.replace(/^(.*\/)?composum\/(.*\/)?pages\//, '$2');
                type = type.replace(/\/components?\//, '/');
                type = type.replace(/\/containers?\//, '/');
                type = type.replace(/\/elements?\//, '/');
                return type;
            },

            /**
             * returns the current dimensions for visualization
             */
            getSizeHint: function () {
                var size = Math.round(this.$el.width()) + 'x' + Math.round(this.$el.height()) + 'px';
                return size;
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

            onMouseOver: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var component = elements.pageBody.getPointerComponent(event, '.' + elements.const.class.component);
                elements.pageBody.pointer.setComponent(component);
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
                this.$head = this.$('.' + elements.const.handle.class.base + '_head');
                this.$left = this.$('.' + elements.const.handle.class.base + '_left');
                this.$right = this.$('.' + elements.const.handle.class.base + '_right');
                this.$bottom = this.$('.' + elements.const.handle.class.base + '_bottom');
                this.$path = this.$('.' + elements.const.handle.class.base + '_path');
                this.$name = this.$('.' + elements.const.handle.class.base + '_name');
                this.$type = this.$('.' + elements.const.handle.class.base + '_type');
                this.$size = this.$('.' + elements.const.handle.class.base + '_size');
                this.setupEvents([this.$head, this.$left, this.$right, this.$bottom]);
                $(window).resize(_.bind(this.onResize, this));
            },

            setupEvents: function (handles) {
                for (var i = 0; i < handles.length; i++) {
                    handles[i].click(_.bind(this.onClick, this));
                    handles[i].mouseover(_.bind(this.onMouseOver, this));
                    handles[i].mouseout(_.bind(this.onMouseOver, this));
                    handles[i][0].addEventListener('dragstart', _.bind(this.onDragStart, this), false);
                }
            },

            /**
             * binds the handle to a component
             */
            setComponent: function (component) {
                if (component) {
                    if (this.component !== component) {
                        this.component = component;
                        this.setBounds(component);
                        this.$name.text(component.getName());
                        this.$path.text(component.getPathHint());
                        this.$type.text(component.getTypeHint());
                        this.$size.text(component.getSizeHint());
                        this.$el.addClass(elements.const.handle.class.base + elements.const.handle.class.visible);
                        var isDraggable = !!component.getDraggable();
                        this.$head.attr('draggable', isDraggable);
                        this.$left.attr('draggable', isDraggable);
                        this.$right.attr('draggable', isDraggable);
                        this.$bottom.attr('draggable', isDraggable);
                    }
                } else {
                    this.hide();
                }
            },

            /**
             * adapts the bounds of the handle to the bounds of the component
             */
            setBounds: function (component) {
                var handlePos = elements.pageBody.$handles.offset();
                var bounds = elements.pageBody.getViewRect(component, {
                    dx: -handlePos.left,
                    dy: -handlePos.top
                });
                this.$head.css('top', bounds.y1);
                this.$head.css('left', bounds.x1 + 6);
                this.$head.css('width', bounds.w - 12);
                this.$left.css('top', bounds.y1);
                this.$left.css('left', bounds.x1);
                this.$left.css('height', bounds.h);
                this.$right.css('top', bounds.y1);
                this.$right.css('left', bounds.x1 + bounds.w - 6);
                this.$right.css('height', bounds.h);
                this.$bottom.css('left', bounds.x1 + 6);
                this.$bottom.css('top', bounds.y1 + bounds.h - 6);
                this.$bottom.css('width', bounds.w - 12);
            },

            hide: function () {
                if (this.component) {
                    this.$el.removeClass(elements.const.handle.class.base + elements.const.handle.class.visible);
                    this.component = undefined;
                }
            },

            /**
             * returns the DOM element of the handles component if 'domEl' is one of the handles elements;
             * returns 'undefined' if the 'domEl' does not match to the handle
             */
            getComponentEl: function (domEl) {
                if (domEl) {
                    if (domEl === this.el || domEl === this.$head[0] || domEl === this.$left[0] ||
                        domEl === this.$right[0] || domEl === this.$bottom[0] || domEl === this.$path[0] ||
                        domEl === this.$name[0] || domEl === this.$type[0] || domEl === this.$size[0]) {
                        return this.component.el;
                    }
                }
                return undefined;
            },

            // event handling

            onClick: function (event) {
                event.preventDefault();
                if (this.component) {
                    elements.pageBody.setSelection(this.component);
                }
            },

            onMouseOver: function (event) {
                event.preventDefault();
                if (this.component) {
                    this.component.onMouseOver(event);
                }
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
                this.$insert = this.$('.' + elements.const.dnd.class.base + '_insert');
                this.$image = this.$('.' + elements.const.dnd.class.base + '_image');
                this.$content = this.$image.find('.' + elements.const.dnd.class.base + '_content');
                this.$overlay = this.$image.find('.' + elements.const.dnd.class.base + '_overlay');
                this.$path = this.$overlay.find('.' + elements.const.dnd.class.base + '_path');
                this.$name = this.$overlay.find('.' + elements.const.dnd.class.base + '_name');
                this.$type = this.$overlay.find('.' + elements.const.dnd.class.base + '_type');
            },

            reset: function () {
                this.clearTargets();
                this.$el.removeClass(elements.const.dnd.class.base + elements.const.dnd.class.visible);
                this.currentComponent = undefined;
            },

            // event handlers

            onDragStart: function (event, component) {
                var self = elements.pageBody.dnd;
                if (!self.currentComponent) {
                    self.currentComponent = component;
                    if (elements.const.log.dnd.event) {
                        elements.log.debug('elements.dnd.onDragStart(' + component.data.path + ')');
                    }
                    self.reset();
                    if (_.isFunction(event.dataTransfer.setDragImage)) {
                        var pos = component.$el.offset();
                        self.$image.css({
                            width: Math.max(100, component.$el.width()) + 'px'
                        });
                        self.$content.html('').append(component.$el.clone());
                        self.$image.addClass(elements.const.dnd.class.base + elements.const.dnd.class.visible);
                        event.dataTransfer.setDragImage(self.$image[0], Math.max(0, event.pageX - pos.left), 50);
                        setTimeout(_.bind(function () {
                            self.$content.html('');
                            self.$image.removeClass(elements.const.dnd.class.base + elements.const.dnd.class.visible);
                        }, self), 100);
                    }
                    event.dataTransfer.setData('application/component', JSON.stringify(component.data));
                    event.dataTransfer.effectAllowed = 'move';
                    setTimeout(_.bind(function () {
                        self.markTargets(component);
                        event.target.addEventListener('drag', self.onDrag, false);
                        event.target.addEventListener('dragend', self.onDragEnd, false);
                    }, self), 150);
                }
            },

            onDragEnd: function (event) {
                var self = elements.pageBody.dnd;
                if (elements.const.log.dnd.event) {
                    elements.log.debug('elements.dnd.onDragEnd()');
                }
                event.target.removeEventListener('dragend', self.onDragEnd);
                event.target.removeEventListener('drag', self.onDrag);
                self.reset();
            },

            onDrag: function (event) {
                var self = elements.pageBody.dnd;
                var target = self.getDragTarget(event);
                self.setDragTarget(target, event);
            },

            onDragOver: function (event) {
                var self = elements.pageBody.dnd;
                if (self.dragTarget && self.insert) {
                    event.preventDefault();
                }
            },

            onDrop: function (event) {
                var self = elements.pageBody.dnd;
                if (self.dragTarget && self.insert) {
                    var source = JSON.parse(event.dataTransfer.getData('application/component'));
                    var target = self.dragTarget.data;
                    var before = self.insert.before ? self.insert.before.data : undefined;
                    if (elements.const.log.dnd.event) {
                        elements.log.debug('elements.dnd.onDrop(' + self.dragTarget.data.path + '): '
                            + JSON.stringify(source) + ' > '
                            + JSON.stringify(target) + ' < '
                            + JSON.stringify(before)
                        );
                    }
                    elements.pageBody.move(source, target, before);
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
                        if (this.dropTargets[i].data.path === container.data.path) {
                            return true;
                        }
                    }
                }
                return false;
            },

            /**
             * Determines the list of allowed target containers (this.dropTargets)
             * for the given component (the drag source) and marks this containers.
             * @param component  the component which should be inserted in an(other) container
             */
            markTargets: function (component) {
                var candidates = [];
                elements.pageBody.containers.forEach(function (candidate) {
                    candidates.push({
                        path: candidate.data.path,
                        type: candidate.data.type
                    });
                });
                var path = component.data.path;
                core.ajaxPost(elements.const.edit.url.targets + path, {
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
                                view.el.addEventListener('drop', this.onDrop, false);
                                view.el.addEventListener('dragover', this.onDragOver, false);
                                view.$el.addClass(elements.const.dnd.class.base + elements.const.dnd.class.target);
                            }
                        }
                    }, this);
                }, this));
            },

            clearTargets: function () {
                this.setDragTarget();
                elements.pageBody.containers.forEach(function (container) {
                    container.el.removeEventListener('drop', this.onDrop);
                    container.el.removeEventListener('dragover', this.onDragOver);
                    container.$el.removeClass(elements.const.dnd.class.base + elements.const.dnd.class.targetOver);
                    container.$el.removeClass(elements.const.dnd.class.base + elements.const.dnd.class.target);
                }, this);
                this.dropTargets = undefined;
            },

            clearOver: function () {
                if (this.dropTargets) {
                    this.dropTargets.forEach(function (target) {
                        target.$el.removeClass(elements.const.dnd.class.base + elements.const.dnd.class.targetOver);
                    }, this);
                }
            },

            // move / insert handling

            getDragTarget: function (event) {
                var container = elements.pageBody.getPointerComponent(event,
                    '.' + elements.const.class.container, _.bind(this.isTarget, this));
                return container;
            },

            setDragTarget: function (container, event) {
                if (this.dragTarget && this.dragTarget !== container) {
                    if (elements.const.log.dnd.target) {
                        elements.log.debug('elements.dnd.dragTarget.clear! (' + this.dragTarget.data.path + ')');
                    }
                    this.dragTarget.$el.removeClass(elements.const.dnd.class.base + elements.const.dnd.class.targetOver);
                    this.$insert.removeClass(elements.const.dnd.class.base + elements.const.dnd.class.visible);
                    this.$insert.removeClass(elements.const.dnd.class.base + elements.const.dnd.class.insert.vertical);
                    this.$insert.removeClass(elements.const.dnd.class.base + elements.const.dnd.class.insert.horizontal);
                    this.insert = undefined;
                    this.dragTarget = undefined;
                }
                if (container) {
                    if (this.dragTarget === container) {
                        this.moveInsertMarker(event);
                    } else {
                        if (elements.const.log.dnd.target) {
                            var pointer = elements.pageBody.getPointer(event);
                            elements.log.debug('elements.dnd.dragTarget.set: ' + container.data.path + ' ' + JSON.stringify(pointer));
                        }
                        this.dragTarget = container;
                        container.$el.addClass(elements.const.dnd.class.base + elements.const.dnd.class.targetOver);
                        this.initInsertMarker(container);
                        this.moveInsertMarker(event);
                        this.$insert.addClass(elements.const.dnd.class.base + elements.const.dnd.class.visible);
                    }
                }
            },

            initInsertMarker: function (container) {
                this.insert = {
                    handlePos: elements.pageBody.$handles.offset(),
                    containerRect: elements.pageBody.getViewRect(container),
                    vertical: true,
                    before: undefined
                };
                if (container.elements.length > 0) {
                    var eRect = elements.pageBody.getViewRect(container.elements[0]);
                    if (container.elements.length > 1) {
                        var eRect2 = elements.pageBody.getViewRect(container.elements[1]);
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
                        this.$insert.addClass(elements.const.dnd.class.base + elements.const.dnd.class.insert.vertical);
                    } else {
                        this.insert.y = this.insert.containerRect.y1 - this.insert.handlePos.top;
                        this.insert.h = this.insert.containerRect.h;
                        this.$insert.css('top', this.insert.y);
                        this.$insert.css('height', this.insert.h);
                        this.$insert.css('width', 0);
                        this.$insert.addClass(elements.const.dnd.class.base + elements.const.dnd.class.insert.horizontal);
                    }
                } else {
                    this.insert.x = this.insert.containerRect.x1 - this.insert.handlePos.left;
                    this.insert.y = this.insert.containerRect.y1 + this.insert.containerRect.h / 2 - this.insert.handlePos.top;
                    this.insert.w = this.insert.containerRect.w;
                    this.$insert.css('top', this.insert.y);
                    this.$insert.css('left', this.insert.x);
                    this.$insert.css('width', this.insert.w);
                    this.$insert.css('height', 0);
                    this.$insert.addClass(elements.const.dnd.class.base + elements.const.dnd.class.insert.vertical);
                }
            },

            moveInsertMarker: function (event) {
                var pointer = elements.pageBody.getPointer(event);
                for (var i = 0; i < this.dragTarget.elements.length; i++) {
                    var element = this.dragTarget.elements[i];
                    var eRect = elements.pageBody.getViewRect(element);
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
                // determine the editing UI components of the page
                this.$handles = this.$('.' + elements.const.handle.handles);
                this.pointer = core.getWidget(this.el, '.' + elements.const.handle.pointer
                    + ' .' + elements.const.handle.class.base, elements.Pointer);
                this.selection = core.getWidget(this.el, '.' + elements.const.handle.selection
                    + ' .' + elements.const.handle.class.base, elements.Selection);
                this.dnd = core.getWidget(this.el, '.' + elements.const.dnd.class.base
                    + elements.const.dnd.class.handle, elements.DndHandle);
                // init the component sets
                this.initComponents();
                // register the handlers for component selection in interaction with the edit frame
                $(document).on(elements.const.event.element.selected, _.bind(this.onComponentSelected, this));
                $(document).on(elements.const.event.element.select, _.bind(this.selectComponent, this));
                window.addEventListener("message", _.bind(this.onMessage, this), false);
            },

            initComponents: function () {
                // determine the set of containers and elements of the current page as Component view instances
                this.components = [];
                this.containers = [];
                this.containerRefs = [];
                this.elements = [];
                var self = this;
                this.$('.' + elements.const.class.container).each(function () {
                    var view = core.getView(this, elements.Container);
                    self.components.push(view);
                    self.containers.push(view);
                    self.containerRefs.push(view.data);
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
                // send container references to the edit frame
                parent.postMessage(elements.const.event.page.containerRefs
                    + JSON.stringify(this.containerRefs), '*');
            },

            // operations

            /**
             * Insert a new component by triggering the Pages edit frame with the insert parameters.
             * @param type    the resource type of the new component
             * @param target  the target container path and type (element reference)
             * @param before  the path of the following sibling (optional)
             */
            insert: function (type, target, before) {
                if (elements.const.log.operation) {
                    elements.log.debug('elements.insert(' + type + ' > '
                        + target.path + (before ? (' < ' + before.path) : '') + ')');
                }
                parent.postMessage(elements.const.event.element.insert
                    + JSON.stringify({
                        type: type,
                        target: {path: target.path, type: target.type},
                        before: before ? before.path : null
                    }), '*');
            },

            /**
             * Move an existing component by triggering the Pages edit frame with the move parameters.
             * @param source  the resource to move to the new container and/or index
             * @param target  the target container path and type (element reference)
             * @param before  the path of the following sibling (optional)
             */
            move: function (source, target, before) {
                if (elements.const.log.operation) {
                    elements.log.debug('elements.move(' + source.path + ' > '
                        + target.path + (before ? (' < ' + before.path) : '') + ')');
                }
                parent.postMessage(elements.const.event.moveComponent
                    + JSON.stringify({
                        source: source.path,
                        target: {path: target.path, type: target.type},
                        before: before ? before.path : null
                    }), '*');
            },

            // DnD forwarding

            onDragStart: function (event, component) {
                this.dnd.onDragStart(event, component);
            },

            // component selection and edit frame message handling

            setSelection: function (component, force) {
                elements.log.debug('pages.elements.setSelection(' + component + ',' + force + ')');
                if (elements.pageBody.selection.component !== component || force) {
                    if (component) {
                        this.dnd.reset();
                        elements.pageBody.selection.setComponent(component);
                        var eventData = [
                            component.data.name,
                            component.data.path,
                            component.data.type
                        ];
                        elements.log.debug('elements.trigger.' + elements.const.event.element.selected + '(' + component.data.path + ')');
                        $(document).trigger(elements.const.event.element.selected, eventData);
                    } else {
                        this.clearSelection();
                    }
                } else {
                    this.clearSelection();
                }
            },

            clearSelection: function () {
                this.dnd.reset();
                if (elements.pageBody.selection.component) {
                    elements.log.debug('pages.elements.clearSelection(' + elements.pageBody.selection.component + ')');
                    elements.pageBody.selection.setComponent(undefined);
                    elements.log.debug('elements.trigger.' + elements.const.event.element.selected + '([])');
                    $(document).trigger(elements.const.event.element.selected, []);
                }
            },

            selectComponent: function (event, name, path, type) {
                if (path) {
                    var $target = $('.' + elements.const.class.component
                        + '[data-' + elements.const.data.path + '="' + path + '"]');
                    var found = false;
                    if ($target && $target.length > 0) {
                        var component = $target[0].view;
                        if (component) {
                            elements.log.debug('pages.elements.selectComponent(' + path + ')');
                            this.setSelection(component, true);
                            found = true;
                        }
                    }
                    if (!found) {
                        elements.log.debug('elements.trigger.' + elements.const.event.element.selected + '([])');
                        $(document).trigger(elements.const.event.element.selected, []);
                    }
                } else {
                    this.clearSelection();
                }
            },

            onComponentSelected: function (event, name, path, type) {
                if (path) {
                    elements.log.debug('pages.elements.element.selected(' + path + ')');
                    parent.postMessage(elements.const.event.element.selected
                        + JSON.stringify({name: name, path: path, type: type}), '*');
                } else {
                    elements.log.debug('pages.elements.selectionCleared()');
                    parent.postMessage(elements.const.event.element.selected
                        + JSON.stringify({}), '*');
                }
            },

            onMessage: function (event) {
                var message = elements.const.event.messagePattern.exec(event.data);
                if (message) {
                    var args = JSON.parse(message[2]);
                    switch (message[1]) {
                        case elements.const.event.element.select:
                            if (args.path) {
                                var eventData = [
                                    args.name,
                                    args.path,
                                    args.type
                                ];
                                elements.log.debug('elements.trigger.' + elements.const.event.element.select + '(' + args.path + ')');
                                $(document).trigger(elements.const.event.element.select, eventData);
                            } else {
                                elements.log.debug('elements.trigger.' + elements.const.event.element.select + '([])');
                                $(document).trigger(elements.const.event.element.select, []);
                            }
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
                    x: event.pageX - window.pageXOffset,
                    y: event.pageY - window.pageYOffset
                };
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
            getViewRect: function (view, offset) {
                var viewPos = view.$el.offset();
                // FIXME: check this and find the reason... i don't know why but in the bootstrap
                // context the views rectangle must be relative to the scroll position !?
                viewPos.top -= $(window).scrollTop();
                viewPos.left -= $(window).scrollLeft();
                var rect = {
                    x1: viewPos.left,
                    y1: viewPos.top,
                    w: view.$el.outerWidth(),
                    h: view.$el.outerHeight()
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
                var domEl = document.elementFromPoint(pointer.x, pointer.y);
                if (domEl) {
                    var handleEl;
                    if ((handleEl = elements.pageBody.pointer.getComponentEl(domEl)) ||
                        (handleEl = elements.pageBody.selection.getComponentEl(domEl))) {
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
                    var self = this;
                    var viewRect = this.getViewRect(view);
                    if (elements.const.log.mouse.position) {
                        elements.log.debug('elements.getPointerView(' + view.data.path + ', '
                            + JSON.stringify(pointer) + ' / ' + JSON.stringify(viewRect) + ', "'
                            + selector + '"' + (condition ? ' ++' : '') + ' ...');
                    }
                    var useNested = undefined; // search for a better matching nested child...
                    view.$el.find(selector).each(function () {
                        // search visual nested components
                        if (!useNested) {
                            var nested = this.view; // use the elements view
                            if (nested) {
                                var nestedRect = self.getViewRect(nested);
                                if (elements.const.log.mouse.position) {
                                    elements.log.debug('elements.getPointerView.try: ' + nested.data.path + ' ' + JSON.stringify(nestedRect));
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
                if (elements.const.log.mouse.position) {
                    elements.log.debug('elements.getPointerView: ' + (view ? view.data.path : 'undefined'));
                }
                return view;
            }
        });

        elements.pageBody = core.getView('body.' + elements.const.class.editBody, elements.PageBody);

    })(window.composum.pages.elements, window.core);
})(window);
