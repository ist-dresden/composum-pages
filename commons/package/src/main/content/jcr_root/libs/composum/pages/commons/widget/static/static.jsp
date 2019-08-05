<%@page session="false" pageEncoding="UTF-8" %>
<%--
  - level: danger (red), warning (yellow), success (green), info (blue), remark (hint: small text, no border)
--%>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<cpn:div test="${not empty widget.model.text}" class="${widgetCSS}_${widget.widgetType} form-group">
    <cpn:div test="${not empty widget.model.level}" class="hint hint-${widget.model.level}" body="true">
        <cpn:text class="${widgetCSS}_text" value="${widget.model.text}" i18n="${widget.i18n}" type="rich"/>
    </cpn:div>
</cpn:div>
