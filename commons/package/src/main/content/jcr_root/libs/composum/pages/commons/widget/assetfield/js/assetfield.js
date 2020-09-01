(function () {
    'use strict';
    CPM.namespace('pages.widgets');

    (function (widgets, pages, core) {
        'use strict';

        widgets.const = _.extend(widgets.const || {}, {
            assetfield: {
                css: {
                    base: 'assetfield-widget'
                },
                profile: 'assetfield'
            }
        });

        widgets.AssetFieldWidget = widgets.PathFieldWidget.extend({

            profileAspect: function () {
                return widgets.const.assetfield.profile;
            }
        });

        CPM.widgets.register('.widget.' + widgets.const.assetfield.css.base, widgets.AssetFieldWidget);

    })(CPM.pages.widgets, CPM.pages, CPM.core);
})();
