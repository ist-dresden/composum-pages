<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<!-- end of dialog content -->
                    </div>
                </div>
                <div class="modal-footer ${dialogCSS}_footer">
                    <button type="button" class="${dialogCSS}_button-cancel ${dialogCSS}_button btn btn-default" data-dismiss="modal">${cpn:i18n(slingRequest,'Cancel')}</button>
                    <div class="${dialogCSS}_hints">
                    </div>
                    <button type="button" class="${dialogCSS}_button-prev ${dialogCSS}_button btn btn-default fa fa-chevron-left"></button>
                    <button type="button" class="${dialogCSS}_button-next ${dialogCSS}_button btn btn-primary fa fa-chevron-right"></button>
                    <button type="submit" class="${dialogCSS}_button-submit ${dialogCSS}_button btn btn-default">${dialog.submitLabel}</button>
                </div>
            </form>
        </div>
    </div>
</div>