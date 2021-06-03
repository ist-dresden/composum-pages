<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<sling:defineObjects/>
<cpn:component id="status" type="com.composum.sling.nodes.console.Consoles">
    <div class="composum-pages-stage-home-tools">
        <c:forEach items="${status.consoles}" var="console">
            <a class="btn btn-sm btn-default composum-pages-stage-home-tools_console_${console.name} composum-pages-stage-home-tools_console-link"
               href="${console.url}"${console.linkAttributes}><span
                    class="composum-pages-stage-home-tools_console-label">${console.label}</span></a>
        </c:forEach>
    </div>
</cpn:component>
