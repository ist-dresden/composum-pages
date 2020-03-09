/**
 * Actions which can be triggered from an edited page and from the Pages edit frame
 * for execution in the context of the edit frame.
 */
(function () {
    'use strict';
    CPM.namespace('pages.hybrid');

    (function (hybrid, pages, core) {

        hybrid.openEditDialog = function (url, path) {
            if (path) {
                if (pages.elements) { // context is a page
                    pages.elements.openEditDialog({
                        path: path
                    }, {
                        url: url
                    });
                } else { // context is the stage edit frame
                    pages.dialogs.openEditDialog(undefined/*name*/, path, undefined/*type*/,
                        undefined/*context*/, url);
                }
            }
        };

        hybrid.openCustomDialog = function (url, type, config, init, trigger) {
            if (pages.elements) { // context is a page
                pages.elements.openCustomDialog(url, type, config, init, trigger);
            } else { // context is the stage edit frame
                pages.dialogHandler.openLoadedDialog(url, eval(type), config, init ? eval(init) : undefined,
                    trigger ? function () {
                        pages.trigger(trigger.context, trigger.event, trigger.args);
                    } : undefined);
            }
        };

    })(CPM.pages.hybrid, CPM.pages, CPM.core);
})();
