<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><cpp:defineFrameObjects/>
<!-- end of dialog content -->
                    </div>
                </div>
                <div class="modal-footer ${dialogCssBase}_footer">
                    <button type="button" class="${dialogCssBase}_button-cancel ${dialogCssBase}_button btn btn-default" data-dismiss="modal">${cpn:i18n(slingRequest,'Cancel')}</button>
                    <button type="button" class="${dialogCssBase}_button-prev ${dialogCssBase}_button btn btn-default">&lt;&lt;</button>
                    <button type="button" class="${dialogCssBase}_button-next ${dialogCssBase}_button btn btn-primary">&gt;&gt;</button>
                    <button type="submit" class="${dialogCssBase}_button-submit ${dialogCssBase}_button btn btn-primary">${cpn:i18n(slingRequest,dialog.submitLabel)}</button>
                </div>
            </form>
        </div>
    </div>
</div>