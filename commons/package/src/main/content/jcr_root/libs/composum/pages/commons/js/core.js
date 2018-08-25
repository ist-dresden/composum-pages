/** a collection of candidates for a transfer to the 'core' framework */
(function (core) {
    'use strict';

    core.dnd = core.dnd || {};

    (function (dnd) {

        /**
         * returns a dnd data object extracted form the jQuery event
         * with mouse position relative to the target element
         */
        dnd.getDndData = function (event) {
            var el = event.currentTarget;
            var clientRect = el.getBoundingClientRect();
            var size = {
                w: clientRect.right - clientRect.left,
                h: clientRect.bottom - clientRect.top
            };
            var pos = {
                x: event.clientX - clientRect.left,
                y: event.clientY - clientRect.top
            };
            return {
                ev: event.originalEvent,
                el: el,
                $el: $(el),
                box: clientRect,
                size: size,
                pos: {
                    x: pos.x, y: pos.y,
                    rx: pos.x / size.w,
                    ry: pos.y / size.h
                }
            }
        };

    })(core.dnd);

})(window.core);
