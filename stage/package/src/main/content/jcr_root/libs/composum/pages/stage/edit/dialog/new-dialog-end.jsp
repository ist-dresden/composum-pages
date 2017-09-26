<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<!-- end of dialog content -->
                    </div>
                </div>
                <div class="modal-footer ${dialogCssBase}_footer">
                    <div class="${dialogCssBase}_hints">
                        <span class="${dialogCssBase}_path-hint" title="${dialog.resource.path}">${dialog.pathHint}</span>
                        <span class="${dialogCssBase}_name-hint" title="${dialog.resource.path}">${dialog.nameHint}</span>
                        <span class="${dialogCssBase}_type-hint" title="${dialog.resourceType}">${dialog.typeHint}</span>
                    </div>
                    <button type="button" class="${dialogCssBase}_button-cancel ${dialogCssBase}_button btn btn-default" data-dismiss="modal">Cancel</button>
                    <button type="submit" class="${dialogCssBase}_button-create ${dialogCssBase}_button btn btn-primary">Create...</button>
                </div>
            </form>
        </div>
    </div>
</div>