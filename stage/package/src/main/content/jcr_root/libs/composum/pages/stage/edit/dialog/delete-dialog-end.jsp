<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<!-- end of dialog content -->
                    </div>
                </div>
                <div class="modal-footer ${dialogCssBase}_footer">
                    <div class="${dialogCssBase}_hints">
                        <span class="${dialogCssBase}_path-hint" title="${dialog.resource.path}">${dialog.pathHint}</span>
                        <span class="${dialogCssBase}_name-hint" title="${dialog.resource.path}">${dialog.nameHint}</span>
                        <span class="${dialogCssBase}_type-hint" title="${dialog.resourceType}">${dialog.typeHint}</span>
                    </div>
                    <button type="button" class="${dialogCssBase}_button-cancel ${dialogCssBase}_button btn btn-default"
                            data-dismiss="modal">${cpn:i18n(slingRequest,'Cancel')}</button>
                    <button type="submit" class="${dialogCssBase}_button-submit-delete ${dialogCssBase}_button btn btn-danger">${cpn:i18n(slingRequest,'Delete')}</button>
                </div>
            </form>
        </div>
    </div>
</div>