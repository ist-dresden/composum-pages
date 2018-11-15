/** a collection of candidates for a transfer to the 'core' framework */
(function (core) {
    'use strict';

    core.parseBool = function (string) {
        return (/^(0*1|on|true)$/i).test(string);
    };

    core.url = core.url || {};

    (function (url) {

        url.getParameters = function (query) {
            var parameters = {};
            var regex = /[?&;](.+?)=([^&;]+)/g;
            var match;
            if (query) {
                while (match = regex.exec(query)) {
                    parameters[match[1]] = decodeURIComponent(match[2]);
                }
            }
            return parameters;
        };

    })(core.url);

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
                    ry: pos.y / size.h,
                    cx: event.clientX,
                    cy: event.clientY,
                    px: event.pageX,
                    py: event.pageY
                }
            }
        };

    })(core.dnd);

})(window.core);
