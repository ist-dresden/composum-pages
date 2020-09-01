(function () {
    'use strict';
    CPM.namespace('pages.widgets');

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            linkfield: {
                cssBase: 'linkfield-widget',
                profile: 'linkfield'
            }
        });

        widgets.LinkFieldWidget = widgets.PathFieldWidget.extend({

            profileAspect: function () {
                return widgets.const.linkfield.profile;
            },

            isExternal: function (path) {
                return /^([a-zA-Z]+:)?\/\/.*$/.exec(path) !== null;
            },

            /**
             * @override suppress root path handling for external links
             */
            applyRootPath: function (path, shorten) {
                if (!this.isExternal(path)) {
                    path = widgets.PathFieldWidget.prototype.applyRootPath.apply(this, [path, shorten]);
                }
                return path;
            }
        });

        CPM.widgets.register('.widget.' + widgets.const.linkfield.cssBase, widgets.LinkFieldWidget);

    })(CPM.pages.widgets, CPM.pages, CPM.core);
})();
