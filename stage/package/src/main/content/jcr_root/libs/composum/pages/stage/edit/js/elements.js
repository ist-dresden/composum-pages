/**
 * edit user interface functions embedded in a content page to support edit interaction
 */
(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.elements = window.composum.pages.elements || {};

    (function (elements, pages, core) { // strong dependency to: 'invoke.js', 'commons.js'
        'use strict';

        elements.const = _.extend(elements.const || {}, {
            handle: { // selection handle CSS classes
                handles: 'composum-pages-stage-edit-handles',
                pointer: 'composum-pages-component-handle_pointer',
                selection: 'composum-pages-component-handle_selection',
                active: 'composum-pages-active-handle',
                action: 'composum-pages-stage-edit-toolbar_button',
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
                },
                id: '.pagesElements'
            },
            edit: { // editing interface urls and keys
                url: {
                    targets: '/bin/cpm/pages/edit.targetContainers.json',
                    toolbar: '/bin/cpm/pages/edit.editToolbar.html'
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
                this.$el.mouseover(_.bind(this.onMouseOver, this))
                    .mouseout(_.bind(this.onMouseOver, this))
                    .click(_.bind(this.onClick, this));
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

            onMouseOver: function (event) {
                if (event) {
                    event.preventDefault();
                }
                var component = elements.pageBody.getPointerComponent(event, '.' + elements.const.class.component);
                if (elements.pageBody.pointer.setComponent(component)) {
                    if (elements.pageBody.selection) {
                        elements.pageBody.selection.setHeadVisibility(elements.pageBody.selection.component === component);
                    }
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
                this.$top = this.$('.' + elements.const.handle.class.base + '_top');
                this.$left = this.$('.' + elements.const.handle.class.base + '_left');
                this.$right = this.$('.' + elements.const.handle.class.base + '_right');
                this.$bottom = this.$('.' + elements.const.handle.class.base + '_bottom');
                this.$head = this.$('.' + elements.const.handle.class.base + '_head');
                this.$toolbar = this.$('.' + elements.const.handle.class.base + '_toolbar');
                this.$path = this.$('.' + elements.const.handle.class.base + '_path');
                this.$name = this.$('.' + elements.const.handle.class.base + '_name');
                this.$type = this.$('.' + elements.const.handle.class.base + '_type');
                this.$size = this.$('.' + elements.const.handle.class.base + '_size');
                $(window).resize(_.bind(this.onResize, this));
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
                        var isDraggable = core.parseBool(component.getDraggable());
                        this.$top.attr('draggable', isDraggable);
                        this.$head.attr('draggable', isDraggable);
                        this.$left.attr('draggable', isDraggable);
                        this.$right.attr('draggable', isDraggable);
                        this.$bottom.attr('draggable', isDraggable);
                        component.getToolbar(_.bind(function (component, html) {
                            this.$toolbar.html(html);
                            this.$toolbar.find('.' + elements.const.handle.action)
                                .mouseover(_.bind(this.onMouseOver, this))
                                .mouseout(_.bind(this.onMouseOver, this))
                                .click(_.bind(this.onActionClick, this));
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
                var handlePos = elements.pageBody.$handles.offset();
                var bounds = elements.pageBody.getViewRect(component.$el, {
                    dx: -handlePos.left,
                    dy: -handlePos.top
                });
                this.$top.css('top', bounds.y1);
                this.$top.css('left', bounds.x1 + 4);
                this.$top.css('width', bounds.w - 8);
                this.$left.css('top', bounds.y1);
                this.$left.css('left', bounds.x1);
                this.$left.css('height', bounds.h);
                this.$right.css('top', bounds.y1);
                this.$right.css('left', bounds.x1 + bounds.w - 4);
                this.$right.css('height', bounds.h);
                this.$bottom.css('left', bounds.x1 + 4);
                this.$bottom.css('top', bounds.y1 + bounds.h - 4);
                this.$bottom.css('width', bounds.w - 8);
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
                    elements.pageBody.$handles.addClass(elements.const.handle.active);
                } else {
                    elements.pageBody.$handles.removeClass(elements.const.handle.active);
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
                this.currentReference = undefined;
            },

            // event handlers

            onDragStart: function (event, object) {
                var self = elements.pageBody.dnd;
                var dnd = core.dnd.getDndData(event);
                var reference = object.reference;
                if (!self.currentReference) {
                    self.currentReference = reference;
                    if (object instanceof elements.Component) {
                        if (elements.log.getLevel() <= log.levels.DEBUG) {
                            elements.log.debug('elements.dnd.onDragStart(' + object.reference.path + ')');
                        }
                        self.reset();
                        dnd.ev.dataTransfer.setData('application/json', JSON.stringify({
                            type: 'element',
                            reference: reference
                        }));
                        dnd.ev.dataTransfer.effectAllowed = 'move';
                        parent.postMessage(elements.const.event.dnd.object + JSON.stringify({
                            type: 'element',
                            reference: reference
                        }), '*');
                        if (_.isFunction(dnd.ev.dataTransfer.setDragImage)) {
                            var pos = object.$el.offset();
                            self.$image.css({
                                width: Math.max(100, object.$el.width()) + 'px'
                            });
                            self.$content.html('').append(object.$el.clone());
                            self.$image.addClass(elements.const.dnd.class.base + elements.const.dnd.class.visible);
                            dnd.ev.dataTransfer.setDragImage(self.$image[0], Math.max(0, dnd.ev.pageX - pos.left), 50);
                            setTimeout(_.bind(function () {
                                self.$content.html('');
                                self.$image.removeClass(elements.const.dnd.class.base + elements.const.dnd.class.visible);
                            }, self), 100);
                        }
                    }
                    setTimeout(_.bind(function () {
                        self.markTargets(reference);
                    }, self), 150);
                }
            },

            onDragEnd: function (event) {
                var self = elements.pageBody.dnd;
                if (elements.log.getLevel() <= log.levels.DEBUG) {
                    elements.log.debug('elements.dnd.onDragEnd()');
                }
                self.reset();
                parent.postMessage(elements.const.event.dnd.finished + '{}', '*');
            },

            onDrop: function (event) {
                elements.pageBody.onDrop(event);
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
                                view.$el.addClass(elements.const.dnd.class.base + elements.const.dnd.class.target);
                            }
                        }
                    }, this);
                }, this));
            },

            clearTargets: function () {
                this.setDragTarget();
                elements.pageBody.containers.forEach(function (container) {
                    container.$el.removeClass(elements.const.dnd.class.base + elements.const.dnd.class.targetOver);
                    container.$el.removeClass(elements.const.dnd.class.base + elements.const.dnd.class.target);
                }, this);
                this.dropTargets = undefined;
            },

            // move / insert handling

            getDragTarget: function (event) {
                return elements.pageBody.getPointerComponent(event,
                    '.' + elements.const.class.container, _.bind(this.isTarget, this));
            },

            setDragTarget: function (container, event) {
                if (this.dragTarget && this.dragTarget !== container) {
                    if (elements.log.getLevel() <= log.levels.DEBUG) {
                        elements.log.debug('elements.dnd.dragTarget.clear! (' + this.dragTarget.reference.path + ')');
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
                        if (elements.log.getLevel() <= log.levels.DEBUG) {
                            var pointer = elements.pageBody.getPointer(event);
                            elements.log.debug('elements.dnd.dragTarget.set: ' + container.reference.path + ' ' + JSON.stringify(pointer));
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
                var e = elements.const.event;
                this.params = core.url.getParameters(window.location.search);
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
                $(document).on(e.element.selected, _.bind(this.onElementSelected, this));
                $(document).on(e.element.select, _.bind(this.selectElement, this));
                window.addEventListener("message", _.bind(this.onMessage, this), false);
                var id = elements.const.dnd.id;
                this.$el
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

            redraw: function (reference) {
                var selection = elements.pageBody.selection.component;
                if (selection) {
                    selection = selection.reference;
                }
                var index = this.getElementIndex(reference.path);
                for (var i = 0; i < index.length; i++) {
                    if (index[i].component >= 0) {
                        this.redrawComponent(this.components[index[i].component], _.bind(function () {
                            this.initComponents();
                            if (selection) {
                                this.selectPath(selection.path, true);
                            }
                        }, this));
                    }
                }
            },

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

            clear: function (reference) {
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

            elementInserted: function (event, reference) {
                var pathAndName = core.getParentAndName(reference.path);
                this.redraw(new pages.Reference(undefined, pathAndName.path));
            },

            elementChanged: function (event, reference) {
                this.redraw(reference);
            },

            elementDeleted: function (event, reference) {
                this.clear(reference);
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
                event.preventDefault();
                if (elements.log.getLevel() <= log.levels.DEBUG) {
                    elements.log.debug('elements.dndEnter(' + '' + ')');
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
                event.preventDefault();
                var dnd = core.dnd.getDndData(event);
                var ref = dnd.el.view.reference;
                var target = this.dnd.getDragTarget(event);
                if (elements.log.getLevel() <= log.levels.DEBUG) {
                    elements.log.debug('elements.dndOver(' + (ref ? ref.path : 'body') + '): '
                        + JSON.stringify(dnd.pos) + " - " + (target ? target.reference.path : '?'));
                }
                this.dnd.setDragTarget(target, event);
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
                event.preventDefault();
                if (this.dnd.dragTarget && this.dnd.insert) {
                    var object = JSON.parse(event.originalEvent.dataTransfer.getData('application/json'));
                    var target = this.dnd.dragTarget.reference;
                    var before = this.dnd.insert.before ? this.dnd.insert.before.reference : undefined;
                    if (elements.log.getLevel() <= log.levels.DEBUG) {
                        elements.log.debug('elements.dnd.onDrop(' + this.dnd.dragTarget.reference.path + '): '
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
                }
                return false;
            },

            // component selection and edit frame message handling

            selectPath: function (path, force) {
                var found = false;
                if (path) {
                    var $target = $('.' + elements.const.class.component
                        + '[data-' + elements.const.data.path + '="' + path + '"]');
                    if ($target && $target.length > 0) {
                        var component = $target[0].view;
                        if (component) {
                            elements.log.debug('pages.elements.selectElement(' + path + ')');
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
                elements.log.debug('pages.elements.setSelection(' + component + ',' + force + ')');
                if (elements.pageBody.selection.component !== component || force) {
                    if (component) {
                        var e = elements.const.event;
                        this.dnd.reset();
                        elements.pageBody.selection.setComponent(component);
                        elements.pageBody.selection.setHeadVisibility(true);
                        elements.log.debug('elements.trigger.' + e.element.selected + '(' + component.reference.path + ')');
                        $(document).trigger(e.element.selected, component.reference);
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
                    var e = elements.const.event;
                    elements.log.debug('pages.elements.clearSelection(' + elements.pageBody.selection.component + ')');
                    elements.pageBody.selection.setComponent(undefined);
                    elements.log.debug('elements.trigger.' + e.element.selected + '([])');
                    $(document).trigger(e.element.selected, []);
                }
            },

            selectElement: function (event, reference) {
                this.selectPath(reference ? reference.path : reference, true);
            },

            onElementSelected: function (event, reference) {
                var e = elements.const.event;
                if (reference && reference.path) {
                    elements.log.debug('pages.elements.' + e.element.selected + '(' + reference.path + ')');
                    parent.postMessage(e.element.selected + JSON.stringify({reference: reference}), '*');
                } else {
                    elements.log.debug('pages.elements.selectionCleared()');
                    parent.postMessage(e.element.selected + JSON.stringify({}), '*');
                }
            },

            onMessage: function (event) {
                var e = elements.const.event;
                var message = e.messagePattern.exec(event.data);
                if (elements.log.getLevel() <= log.levels.TRACE) {
                    elements.log.trace('elements.message.on: "' + event.data + '"...');
                }
                if (message) {
                    var args = JSON.parse(message[2]);
                    switch (message[1]) {
                        case e.element.select:
                            if (elements.log.getLevel() <= log.levels.DEBUG) {
                                elements.log.debug('elements.message.on.' + e.element.select + JSON.stringify(args));
                            }
                            if (args.reference && args.reference.path) {
                                if (elements.log.getLevel() <= log.levels.DEBUG) {
                                    elements.log.debug('elements.trigger.' + e.element.select + '(' + args.reference.path + ')');
                                }
                                $(document).trigger(e.element.select, args.reference);
                            } else {
                                if (elements.log.getLevel() <= log.levels.DEBUG) {
                                    elements.log.debug('elements.trigger.' + e.element.select + '([])');
                                }
                                $(document).trigger(e.element.select, []);
                            }
                            break;
                        case e.element.inserted:
                            if (elements.log.getLevel() <= log.levels.DEBUG) {
                                elements.log.debug('elements.message.on.' + e.element.inserted + JSON.stringify(args));
                            }
                            this.elementInserted(event, args.reference);
                            break;
                        case e.element.changed:
                            if (elements.log.getLevel() <= log.levels.DEBUG) {
                                elements.log.debug('elements.message.on.' + e.element.changed + JSON.stringify(args));
                            }
                            this.elementChanged(event, args.reference);
                            break;
                        case e.element.deleted:
                            if (elements.log.getLevel() <= log.levels.DEBUG) {
                                elements.log.debug('elements.message.on.' + e.element.deleted + JSON.stringify(args));
                            }
                            this.elementDeleted(event, args.reference);
                            break;
                        case e.dnd.object:
                            if (elements.log.getLevel() <= log.levels.DEBUG) {
                                elements.log.debug('elements.message.on.' + e.dnd.object + JSON.stringify(args));
                            }
                            pages.current.dnd.object = args;
                            break;
                        case e.dnd.finished:
                            if (elements.log.getLevel() <= log.levels.DEBUG) {
                                elements.log.debug('elements.message.on.' + e.dnd.finished);
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
                    x: event.originalEvent.pageX - window.pageXOffset,
                    y: event.originalEvent.pageY - window.pageYOffset
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
            getViewRect: function ($el, offset) {
                var viewPos = $el.offset();
                // FIXME: check this and find the reason... i don't know why but in the bootstrap
                // context the views rectangle must be relative to the scroll position !?
                viewPos.top -= $(window).scrollTop();
                viewPos.left -= $(window).scrollLeft();
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
                    var viewRect = this.getViewRect(view.$el);
                    if (elements.log.getLevel() <= log.levels.TRACE) {
                        elements.log.trace('elements.getPointerView(' + view.reference.path + ', '
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
                                if (elements.log.getLevel() <= log.levels.TRACE) {
                                    elements.log.trace('elements.getPointerView.try: ' + nested.reference.path + ' ' + JSON.stringify(nestedRect));
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
                if (elements.log.getLevel() <= log.levels.TRACE) {
                    elements.log.trace('elements.getPointerView: ' + (view ? view.reference.path : 'undefined'));
                }
                return view;
            }
        });

        elements.pageBody = core.getView('body.' + elements.const.class.editBody, elements.PageBody);

    })(window.composum.pages.elements, window.composum.pages, window.core);
})(window);
