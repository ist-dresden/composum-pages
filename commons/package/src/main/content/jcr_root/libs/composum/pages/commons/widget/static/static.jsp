<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<cpn:div test="${not empty widget.model.text}" class="${widgetCssBase}_${widget.widgetType} form-group">
    <cpn:div test="${not empty widget.model.level}" class="hint hint-${widget.model.level}" body="true">
        <cpn:text class="${widgetCssBase}_text" value="${cpn:text(widget.model.text)}" i18n="${widget.i18n}"/>
    </cpn:div>
</cpn:div>
