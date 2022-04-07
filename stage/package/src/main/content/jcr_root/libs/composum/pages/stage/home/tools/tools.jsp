<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<sling:defineObjects/>
<cpn:component id="status" type="com.composum.sling.nodes.console.ConsolesModel">
    <div class="composum-pages-stage-home-tools">
        <c:forEach items="${status.consoles}" var="console">
            <c:if test="${!console.menu}">
                <a class="btn btn-sm btn-default composum-pages-stage-home-tools_console_${console.name} composum-pages-stage-home-tools_console-link"
                   href="${console.staticUrl}"${console.linkAttributes} target="_top"><span
                        class="composum-pages-stage-home-tools_console-label">${console.label}</span></a>
            </c:if>
        </c:forEach>
    </div>
</cpn:component>
