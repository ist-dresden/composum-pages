/**
 * general functions used in the injected edit code of a content page and in the Pages edit frame
 */
(function (window) {
    window.composum = window.composum || {};
    window.composum.pages = window.composum.pages || {};
    (function (pages, core) {
        'use strict';

        pages.const = _.extend(pages.const || {}, {
            commons: {
                data: { // the data attribute names of a component
                    encoded: 'pages-edit-encoded',
                    reference: 'pages-edit-reference',
                    name: 'pages-edit-name',
                    path: 'pages-edit-path',
                    type: 'pages-edit-type',
                    prim: 'pages-edit-prim',
                    synthetic: 'pages-edit-synthetic'
                },
                url: { // the URLs to load data from the authoring system
                    edit: '/bin/cpm/pages/edit',
                    _resourceInfo: '.resourceInfo.json'
                }
            }
        });

        /**
         * a resource reference class to handel containers, elements and component types event if they are synthetic
         * @param nameOrData  name value or a data object: View, $() or a template data object to clone
         * @param path        the resource path; should be undefined (not present) if a data object is used
         * @param type        the resource type is important if a synthetic resource is referenced
         * @param prim        the primary type if the reference is used to create a (synthetic) resource
         * @param synthetic   the 'synthetic' state indicator
         */
        pages.Reference = function (nameOrData, path, type, prim, synthetic) {
            if (nameOrData instanceof Backbone.View) {
                nameOrData = nameOrData.$el;
            }
            if (nameOrData instanceof jQuery) {
                var d = pages.const.commons.data;
                var reference = undefined;
                var encoded = nameOrData.data(d.encoded);
                if (encoded) {
                    reference = JSON.parse(atob(encoded));
                } else {
                    reference = nameOrData.data(d.reference);

                }
                if (reference) {
                    _.extend(this, reference);
                } else {
                    this.name = nameOrData.data(d.name);
                    this.path = path || nameOrData.data(d.path);
                    this.type = type || nameOrData.data(d.type);
                    this.prim = prim || nameOrData.data(d.prim);
                    this.synthetic = synthetic !== undefined ? synthetic
                        : core.parseBool(nameOrData.data(d.synthetic));
                }
            } else if (nameOrData && nameOrData.path && !path) {
                _.extend(this, nameOrData); // clone an appropriate data object
            } else {
                this.name = nameOrData; // resource name
                this.path = path;       // resource path
                this.type = type;       // resource type
                this.prim = prim;       // primary / component type
                this.synthetic = synthetic !== undefined && synthetic;
            }
        };
        _.extend(pages.Reference.prototype, {

            /**
             * check reference data on completeness
             */
            isComplete: function () {
                return this.name !== undefined && this.path !== undefined &&
                    this.type !== undefined && this.prim !== undefined;
            },

            /**
             * load reference information if necessary and call callback after data load if a callback is present
             */
            complete: function (callback) {
                if (!this.isComplete()) {
                    var u = pages.const.commons.url;
                    var options = {};
                    if (this.type) {
                        options.data = {
                            type: this.type
                        };
                    }
                    core.ajaxGet(u.edit + u._resourceInfo + this.path, options, _.bind(function (data) {
                        // '' as fallback to prevent from infinite recursion..
                        if (!this.name) {
                            this.name = data.name ? data.name : '';
                        }
                        if (!this.type) {
                            this.type = data.type ? data.type : '';
                        }
                        if (!this.prim) {
                            this.prim = data.prim ? data.prim : '';
                        }
                        this.synthetic = data.synthetic;
                        if (_.isFunction(callback)) {
                            callback(this);
                        }
                    }, this));
                } else {
                    if (_.isFunction(callback)) {
                        callback(this);
                    }
                }
            }
        });

    })(window.composum.pages, window.core);
})(window);
