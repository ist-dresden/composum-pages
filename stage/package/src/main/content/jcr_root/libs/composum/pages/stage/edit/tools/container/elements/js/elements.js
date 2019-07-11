(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.tools = window.composum.pages.tools || {};

    (function (tools, pages, core) {
        'use strict';

        tools.const = _.extend(tools.const || {}, {
            elements: {
                event: {
                    id: '.tools.Elements'
                },
                css: {
                    base: 'composum-pages-stage-edit-tools-container-elements',
                    tools: 'composum-pages-tools',
                    _actions: '_actions',
                    _panel: '_panel',
                    _move_up: '_move-up',
                    _move_down: '_move-down',
                    _go_up: '_go-up',
                    _select: '_select',
                    _content: '_content',
                    _list: '_list',
                    _element: '_element'
                },
                log: {
                    prefix: 'context.elements.'
                },
                uri: {
                    base: '/libs/composum/pages/stage/edit/tools/container/elements',
                    _content: '.content.html',
                    edit: {
                        _: '/bin/cpm/pages/edit',
                        _allowed: '.isAllowedElement.json',
                        context: {
                            _: '/bin/cpm/pages/edit.context',
                            _actions: '.actions.html',
                            _container: '.container.html'
                        }
                    },
                    actions: '/bin/cpm/pages/edit.contextActions.html',
                    allowed: '/bin/cpm/pages/edit.isAllowedElement.json'
                }
            }
        });

        tools.ElementsItem = Backbone.View.extend({

            initialize: function (options) {
                this.log = {
                    tools: pages.contextTools.log,
                    dnd: log.getLogger('dnd')
                };
                this.reference = new pages.Reference(this);
                this.$el
                    .on('dragstart', _.bind(this.onDragStart, this))
                    .on('dragover', _.bind(this.onDragOver, this))
                    .on('dragend', _.bind(this.onDragEnd, this))
                    .on('drop', _.bind(this.onDrop, this));
            },

            onDragStart: function (event) {
                var e = pages.const.event;
                var $element = $(event.currentTarget);
                var object = {
                    type: 'element',
                    reference: this.reference
                };
                pages.trigger('elements.dnd.start', e.dnd.object, [object]);
                var jsonData = JSON.stringify(object);
                var dndEvent = event.originalEvent;
                dndEvent.dataTransfer.setData('application/json', jsonData);
                dndEvent.dataTransfer.effectAllowed = 'move';
                if (this.log.dnd.getLevel() <= log.levels.DEBUG) {
                    this.log.dnd.debug(tools.const.elements.log.prefix + 'dndStart(' + jsonData + ')');
                }
                $element.addClass('dragging');
            },

            onDragOver: function (event) {
                event.preventDefault();
                var dnd = core.dnd.getDndData(event);
                var target = this.getDndTarget(dnd);
                if (this.log.dnd.getLevel() <= log.levels.TRACE) {
                    this.log.dnd.trace(tools.const.elements.log.prefix + 'dndOver(' + dnd.el.view.reference.path + '): '
                        + JSON.stringify(dnd.pos) + " - " + (target ? target.container.reference.path : '?'));
                }
                this.elements.setDndTarget(dnd, target);
                // 'return' DnD operation if allowed (dndTarget defined) and an element to drop is available
                dnd.ev.dataTransfer.dropEffect = this.elements.dndTarget && pages.current.dnd.object
                    ? (pages.current.dnd.object.type === 'component' ? 'copy' : 'move') : 'none';
                return false;
            },

            onDrop: function (event) {
                event.preventDefault();
                this.elements.onDrop(event);
                return false;
            },

            onDragEnd: function (event) {
                event.preventDefault();
                var e = pages.const.event;
                pages.trigger('elements.dnd.end', e.dnd.finished, [event], ['...']);
                return false;
            },

            /**
             * determines the target object for the dnd item on dragging content; returns nothing if the target
             * is the same item in the designated container as dragged currently ('undefined' if nothing to change)
             * @param dnd the extended dnd event
             * @returns target description: {container:{data,view},before:{data,item}} od nothing (drop not useful)
             */
            getDndTarget: function (dnd) {
                if (dnd.pos.rx > 0.05 && dnd.pos.rx < 0.95) { // no target if outside of the element
                    var object = pages.current.dnd.object;
                    if (!object instanceof tools.ElementsItem || object.reference.path !== this.reference.path) {
                        // if this element is not the dragged object itself...
                        if (dnd.pos.ry < 0.25) {
                            var prev = this.elements.getPrevSibling(this);
                            if (!object instanceof tools.ElementsItem || !prev || prev.reference.path !== object.reference.path) {
                                // drop before this element if dragged object is not always the previous sibling...
                                return {
                                    container: {
                                        reference: this.elements.reference, view: this.elements
                                    },
                                    before: {
                                        reference: this.reference, item: this
                                    }
                                }
                            }
                        } else if (dnd.pos.ry > 0.75) {
                            var next = this.elements.getNextSibling(this);
                            if (!object instanceof tools.ElementsItem || !next || next.reference.path !== object.reference.path) {
                                // drop behind this element if dragged object is not always the next sibling...
                                return {
                                    container: {
                                        reference: this.elements.reference, view: this.elements
                                    },
                                    before: next
                                        ? {reference: next.reference, item: next}
                                        : {reference: new pages.Reference(), item: undefined}
                                }
                            }
                        } else {
                            // drop target is the element itself (useful if it is a container)
                            return {
                                container: {
                                    reference: this.reference, view: this
                                },
                                before: {reference: new pages.Reference(), item: undefined}
                            }
                        }
                    }
                }
                return undefined;
            }
        });

        tools.Elements = Backbone.View.extend({

            initialize: function (options) {
                var e = pages.const.event;
                var c = tools.const.elements.css;
                var id = tools.const.elements.event.id;
                this.log = {
                    tools: pages.contextTools.log,
                    dnd: log.getLogger('dnd')
                };
                this.reference = new pages.Reference(this);
                this.$actions = this.$('.' + c.base + c._actions);
                this.$dndPanel = this.$('.' + c.tools + c._panel);
                this.$moveUp = this.$('.' + c.base + c._move_up);
                this.$moveDown = this.$('.' + c.base + c._move_down);
                this.$goUp = this.$('.' + c.base + c._go_up);
                this.$select = this.$('.' + c.base + c._select);
                this.$content = this.$('.' + c.base + c._content);
                this.$moveUp.click(_.bind(this.moveUp, this));
                this.$moveDown.click(_.bind(this.moveDown, this));
                this.$goUp.click(_.bind(this.selectContainer, this));
                this.$select.click(_.bind(this.selectElement, this));
                $(document)
                    .on(e.element.selected + id, _.bind(this.onElementSelected, this))
                    .on(e.element.inserted + id, _.bind(this.onElementInserted, this))
                    .on(e.element.changed + id, _.bind(this.onElementChanged, this))
                    .on(e.element.deleted + id, _.bind(this.onElementDeleted, this))
                    .on(e.dnd.finished + id, _.bind(this.onDragFinished, this));
                this.$el
                    .on('dragenter' + id, _.bind(this.onDragEnter, this))
                    .on('dragover' + id, _.bind(this.onDragOver, this))
                    .on('drop' + id, _.bind(this.onDrop, this));
            },

            beforeClose: function () {
                var e = pages.const.event;
                var id = tools.const.elements.event.id;
                $(document)
                    .off(e.element.selected + id)
                    .off(e.element.inserted + id)
                    .off(e.element.changed + id)
                    .off(e.element.deleted + id)
                    .off(e.dnd.finished + id);
            },

            initContent: function (options) {
                var c = tools.const.elements.css;
                this.selection = undefined;
                this.$list = this.$content.find('.' + c.base + c._list);
                this.$elements = this.$content.find('.' + c.base + c._element);
                var elements = this.elements = [];
                var that = this;
                this.$elements.each(function () {
                    var item = core.getView(this, tools.ElementsItem);
                    item.elements = that;
                    elements.push(item);
                    item.$el.click(_.bind(that.selectLocal, that))
                });
                if (this.log.tools.getLevel() <= log.levels.DEBUG) {
                    this.log.tools.debug('context.elements.init.content[' + this.elements.length + ']');
                }
                this.selectPath(pages.current.element);
            },

            getElement: function (reference) {
                if (this.elements && reference) {
                    for (var i = 0; i < this.elements.length; i++) {
                        if (this.elements[i].reference.path === reference.path) {
                            return this.elements[i];
                        }
                    }
                }
                return undefined;
            },

            getPrevSibling: function (item) {
                for (var i = 0; i < this.elements.length; i++) {
                    if (this.elements[i] === item) {
                        return i > 0 ? this.elements[i - 1] : undefined;
                    }
                }
                return undefined;
            },

            getNextSibling: function (item) {
                for (var i = 0; i < this.elements.length; i++) {
                    if (this.elements[i] === item) {
                        return i < this.elements.length - 1 ? this.elements[i + 1] : undefined;
                    }
                }
                return undefined;
            },

            getNextElement: function (pos) {
                for (var i = 0; i < this.elements.length; i++) {
                    var rect = this.elements[i].el.getBoundingClientRect();
                    if (rect.top > pos.y) {
                        return this.elements[i];
                    }
                }
                return undefined;
            },

            onTabSelected: function () {
                if (this.log.tools.getLevel() <= log.levels.DEBUG) {
                    this.log.tools.debug(tools.const.elements.log.prefix + 'on.tab.selected');
                }
                this.reload();
            },

            onElementSelected: function (event, reference) {
                if (this.log.tools.getLevel() <= log.levels.DEBUG) {
                    this.log.tools.debug(tools.const.elements.log.prefix + 'on.element.selected(' + JSON.stringify(reference) + ')');
                }
                this.selectPath(reference);
            },

            onElementInserted: function (event, reference) {
                if (this.log.tools.getLevel() <= log.levels.DEBUG) {
                    this.log.tools.debug(tools.const.elements.log.prefix + 'on.element.inserted(' + JSON.stringify(reference) + ')');
                }
                this.reload();
            },

            onElementChanged: function (event, reference) {
                if (this.log.tools.getLevel() <= log.levels.DEBUG) {
                    this.log.tools.debug(tools.const.elements.log.prefix + 'on.element.changed(' + JSON.stringify(reference) + ')');
                }
                this.reload();
            },

            onElementDeleted: function (event, reference) {
                if (this.log.tools.getLevel() <= log.levels.DEBUG) {
                    this.log.tools.debug(tools.const.elements.log.prefix + 'on.element.deleted(' + JSON.stringify(reference) + ')');
                }
                this.reload();
            },

            reload: function () {
                if (this.log.tools.getLevel() <= log.levels.DEBUG) {
                    this.log.tools.debug(tools.const.elements.log.prefix + 'reload(' + JSON.stringify(this.reference) + ')');
                }
                var u = tools.const.elements.uri.edit.context;
                core.ajaxGet(u._ + u._container + this.reference.path, {
                        data: {
                            type: this.reference.type // the type to support synthetic resources
                        }
                    },
                    undefined, undefined, _.bind(function (data) {
                        if (data.status === 200) {
                            this.$content.html(data.responseText);
                        } else {
                            this.$content.html("");
                        }
                        this.initContent();
                    }, this));
            },

            selectLocal: function (event) {
                if (this.log.tools.getLevel() <= log.levels.DEBUG) {
                    this.log.tools.debug(tools.const.elements.log.prefix + 'select.local('
                        + JSON.stringify(event.currentTarget.view.reference) + ')');
                }
                event.preventDefault();
                this.selectPath(event.currentTarget.view.reference);
                return false;
            },

            selectContainer: function (event) {
                if (this.log.tools.getLevel() <= log.levels.DEBUG) {
                    this.log.tools.debug(tools.const.elements.log.prefix + 'select.container('
                        + (this.selection ? JSON.stringify(this.selection) : '[]') + ')');
                }
                event.preventDefault();
                if (this.selection) {
                    var e = pages.const.event;
                    var parentPath = core.getParentPath(this.selection);
                    pages.trigger(tools.const.elements.log.prefix + 'select.container', e.element.select,
                        [new pages.Reference(undefined, parentPath)]);
                }
                return false;
            },

            selectElement: function (event) {
                if (this.log.tools.getLevel() <= log.levels.DEBUG) {
                    this.log.tools.debug(tools.const.elements.log.prefix + 'select.element('
                        + (this.selection ? JSON.stringify(this.selection) : '[]') + ')');
                }
                event.preventDefault();
                if (this.selection) {
                    var e = pages.const.event;
                    pages.trigger(tools.const.elements.log.prefix + 'select.element', e.element.select,
                        [new pages.Reference(undefined, this.selection)]);
                }
                return false;
            },

            selectPath: function (reference) {
                if (this.log.tools.getLevel() <= log.levels.DEBUG) {
                    this.log.tools.debug(tools.const.elements.log.prefix + 'select.path(' + JSON.stringify(reference) + ')');
                }
                var c = tools.const.elements.css;
                this.$content.find('.' + c.base + c._element).removeClass('selected').removeClass('current');
                if (!reference || this.selection === reference.path) {
                    reference = this.reference;
                } else {
                    var selected = this.getElement(reference);
                    if (selected) {
                        selected.$el.addClass('selected');
                        if (pages.current.element && reference.path === pages.current.element.path) {
                            selected.$el.addClass('current');
                        }
                    }
                }
                this.selection = reference.path;
                this.setElementActions(reference);
            },

            setElementActions: function (reference) {
                if (this.log.tools.getLevel() <= log.levels.TRACE) {
                    this.log.tools.trace(tools.const.elements.log.prefix + 'element.actions(' + JSON.stringify(reference) + ')');
                }
                if (this.actions) {
                    this.actions.dispose();
                    this.actions = undefined;
                }
                this.$actions.html('');
                if (this.$actions.length > 0 && reference && reference.path) {
                    // load edit actions for the selected element...
                    var u = tools.const.elements.uri.edit.context;
                    core.ajaxGet(u._ + u._actions + reference.path, {
                            data: {
                                type: reference.type // the type to support synthetic resources
                            }
                        },
                        _.bind(function (data) {
                            this.$actions.html(data);
                            this.actions = core.getWidget(this.$actions[0],
                                '.' + pages.toolbars.const.editToolbarClass, pages.toolbars.EditToolbar);
                            this.actions.reference = reference;
                        }, this));
                }
            },

            moveUp: function (event) {
                event.preventDefault();
                if (this.selection && this.selection !== this.reference) {
                    var element = this.getElement({path: this.selection});
                    if (this.log.tools.getLevel() <= log.levels.DEBUG) {
                        this.log.tools.debug(tools.const.elements.log.prefix + 'move.up('
                            + (element ? JSON.stringify(element.reference) : '[]') + ')');
                    }
                    if (element) {
                        var before = this.getPrevSibling(element);
                        if (before) {
                            pages.actions.dnd.doDropMove({
                                container: {
                                    reference: this.reference
                                },
                                before: {
                                    reference: before.reference
                                }
                            }, {
                                type: 'element',
                                reference: element.reference
                            });
                        }
                    }
                }
                return false;
            },

            moveDown: function (event) {
                event.preventDefault();
                if (this.selection && this.selection !== this.reference) {
                    var element = this.getElement({path: this.selection});
                    if (this.log.tools.getLevel() <= log.levels.DEBUG) {
                        this.log.tools.debug(tools.const.elements.log.prefix + 'move.down('
                            + (element ? JSON.stringify(element.reference) : '[]') + ')');
                    }
                    if (element) {
                        var next = this.getNextSibling(element);
                        if (next) {
                            var before = this.getNextSibling(next);
                            pages.actions.dnd.doDropMove({
                                container: {
                                    reference: this.reference
                                },
                                before: before ? {
                                    reference: before.reference
                                } : undefined
                            }, {
                                type: 'element',
                                reference: element.reference
                            });
                        }
                    }
                }
                return false;
            },

            // DnD...

            onDragEnter: function (event) {
                event.preventDefault();
                var dnd = core.dnd.getDndData(event);
                var target = this.getDndTarget(dnd);
                if (this.log.dnd.getLevel() <= log.levels.TRACE) {
                    this.log.dnd.trace(tools.const.elements.log.prefix + 'dnd.enter(' + dnd.el.view.reference.path + '): '
                        + JSON.stringify(dnd.pos) + " - " + (target ? target.container.reference.path : '?'));
                }
                this.setDndTarget(dnd, target);
                return false;
            },

            /**
             * necessary to receive the 'drop' event (!) and useful to return the current DnD status (dropEffect)
             */
            onDragOver: function (event) {
                event.preventDefault();
                // 'return' DnD operation if allowed (dndTarget defined) and an element to drop is available
                event.originalEvent.dataTransfer.dropEffect = this.dndTarget && pages.current.dnd.object
                    ? (pages.current.dnd.object.type === 'component' ? 'copy' : 'move') : 'none';
                return false;
            },

            onDrop: function (event) {
                event.preventDefault();
                if (this.dndTarget && pages.current.dnd.object) {
                    if (this.log.dnd.getLevel() <= log.levels.INFO) {
                        this.log.dnd.info(tools.const.elements.log.prefix + 'dnd.drop('
                            + this.dndTarget.container.reference.path + ':' + this.dndTarget.before.reference.path + ', '
                            + pages.current.dnd.object.type + ':' + JSON.stringify(pages.current.dnd.object.reference) + ')');
                    }
                    pages.actions.dnd.doDrop(this.dndTarget, pages.current.dnd.object);
                }
                return false;
            },

            /**
             * determines the target object for the dnd item on dragging content; returns nothing if the target
             * is the same item in the designated container as dragged currently ('undefined' if nothing to change)
             * @param dnd the extended dnd event
             * @returns target description: {container:{path,view},before:{path,item}} od nothing (drop not useful)
             */
            getDndTarget: function (dnd) {
                var object = pages.current.dnd.object;
                var before = this.getNextElement(dnd.pos);
                if (before) {
                    if (!object instanceof tools.ElementsItem || object.reference.path !== before.reference.path) {
                        var prev = this.getPrevSibling(before);
                        if (!object instanceof tools.ElementsItem || !prev || object.reference.path !== prev.reference.path) {
                            return {
                                container: {reference: this.reference, view: this},
                                before: {reference: before.reference, view: before}
                            }
                        }
                    }
                } else {
                    var last = this.elements.length > 0 ? this.elements[this.elements.length - 1] : undefined;
                    if (!object instanceof tools.ElementsItem || !last || object.reference.path !== last.reference.path) {
                        return {
                            container: {reference: this.reference, view: this},
                            before: {reference: new pages.Reference(), item: undefined}
                        }
                    }
                }
                return undefined;
            },

            /**
             * stores the target found in the DnD status if this status is not the same as before
             * @param dnd the extended event
             * @param target the designated target (undefined: no useful target container at the current position)
             */
            setDndTarget: function (dnd, target) {
                if (!this.dndBusy) {
                    this.dndBusy = true; // wait for the answer of the hierarchy check
                    // if the status is not the same as known already...
                    if (!this.dndTarget || !target
                        || this.dndTarget.container.reference.path !== target.container.reference.path
                        || this.dndTarget.before.reference.path !== target.before.reference.path) {
                        if (target) {
                            var object = pages.current.dnd.object;
                            if (object) {
                                var u = tools.const.elements.uri;
                                // check the hierarchy rules to make the DnD object an element of the target
                                core.ajaxGet(u.allowed + target.container.reference.path, {
                                        dataType: 'json',
                                        data: {
                                            path: object.reference.path,
                                            type: object.reference.type
                                        }
                                    },
                                    _.bind(function (data) { // success
                                        // drop is useful if the DnD object can be an element of the target
                                        this.changeDndTarget(dnd, target, data.isAllowed ? object : undefined);
                                    }, this),
                                    _.bind(function () { // fail
                                        this.changeDndTarget(dnd, target); // drop not useful
                                    }, this));
                            } else { // no DnD object
                                this.changeDndTarget(dnd, target); // drop not useful
                            }
                        } else { // no target found
                            this.changeDndTarget(dnd, target); // drop not useful
                        }
                    } else {
                        this.dndBusy = false;
                    }
                }
            },

            /**
             * changes the status of the current DnD operation
             * @param dnd the extended event
             * @param target the designated target (undefined: no useful target container at the current position)
             * @param object the dragged element information (undefined: drop not allowed/useful)
             */
            changeDndTarget: function (dnd, target, object) {
                this.$list.removeClass('drop-into');
                this.$elements.removeClass('drop-before').removeClass('drop-into');
                if (target && object) {
                    this.dndTarget = target;
                    if (target.before.item) {
                        target.before.item.$el.addClass('drop-before');
                    } else {
                        if (target.container.view.$list) {
                            target.container.view.$list.addClass('drop-into');
                        } else {
                            target.container.view.$el.addClass('drop-into');
                        }
                    }
                    if (this.log.dnd.getLevel() <= log.levels.DEBUG) {
                        this.log.dnd.debug(tools.const.elements.log.prefix
                            + 'dndTarget: ' + target.container.reference.path);
                    }
                } else {
                    this.dndTarget = undefined;
                    if (this.log.dnd.getLevel() <= log.levels.TRACE) {
                        this.log.dnd.trace(tools.const.elements.log.prefix + 'dndTarget: NONE');
                    }
                }
                this.dndBusy = false;
            },

            onDragFinished: function (event) {
                this.changeDndTarget();
                this.$elements.removeClass('dragging');
            }
        });

        /**
         * register this tool as a pages context tool for initialization after load of the context tools set
         */
        pages.contextTools.addTool(function (contextTabs) {
            var tool = core.getWidget(contextTabs.el, '.' + tools.const.elements.css.base, tools.Elements);
            if (tool) {
                tool.contextTabs = contextTabs;
            }
            return tool;
        });

    })(window.composum.pages.tools, window.composum.pages, window.core);
})(window);
