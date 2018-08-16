(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    window.composum.pages.user = window.composum.pages.user || {};

    (function (user, pages, core) {
        'use strict';

        user.const = _.extend(user.const || {}, {
            userViewCssBase: 'composum-pages-stage-edit-tools-main-user',
            developModeKey: '_develop-mode'
        });

        user.View = Backbone.View.extend({

            initialize: function (options) {
                this.$developMode = this.$('.' + user.const.userViewCssBase + user.const.developModeKey);
                this.showDevelopMode();
                this.$developMode.click(_.bind(this.toogleDevelopMode, this));
            },

            showDevelopMode: function () {
                this.isDevelopMode = (pages.current.mode == pages.const.modes.develop);
                if (this.isDevelopMode) {
                    this.$developMode.addClass('active');
                } else {
                    this.$developMode.removeClass('active');
                }
            },

            toogleDevelopMode: function () {
                window.location.href = window.location.pathname
                    + "?pages.mode.switch=" + (this.isDevelopMode ? 'edit' : 'develop');
            }
        });

        user.view = core.getView('.' + user.const.userViewCssBase, user.View);

    })(window.composum.pages.user, window.composum.pages, window.core);
})(window);
