<%@page session="false" pageEncoding="UTF-8" %>
<%--
    the markup of the element-type-select-widget content (loaded via AJAX)

    /libs/composum/pages/stage/widget/element
--%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:model var="components" type="com.composum.pages.stage.model.edit.page.Components"
           cssBase="composum-pages-edit-widget">
    <c:if test="${empty components.categories}">
        <div class="${componentsCssBase}_empty">
            <cpn:text class="${componentsCssBase}_paragraph alert alert-warning"
                      value="no element type found for insertion" i18n="true"/>
        </div>
    </c:if>
    <div class="${componentsCssBase}_categories">
        <c:forEach items="${components.categories}" var="category">
            <div class="${componentsCssBase}_category">
                <a class="${componentsCssBase}_category_title" href="#${componentsCssBase}_category_${category}"
                   data-toggle="collapse"><i
                        class="fa fa-angle-down"></i><span>${cpn:i18n(slingRequest,category)}</span></a>
                <ul class="${componentsCssBase}_list collapse in" id="${componentsCssBase}_category_${category}">
                    <c:catch var="theException">
                        <c:forEach items="${components.components[category]}" var="componentType">
                            <li class="${componentsCssBase}_element-type">
                                <input type="radio" name="${components.widgetName}" value="${componentType.path}"
                                       class="${componentsCssBase}_radio"/>
                                <cpp:include path="${componentType.path}" resourceType="${componentType.path}"
                                             subtype="edit/tile" replaceSelectors="select"/>
                            </li>
                        </c:forEach>
                    </c:catch>
                    <c:if test="${exception != null}">
                        <%
                            if (log != null) {
                                log.error(String.valueOf(request.getAttribute("componentType")),
                                        (Throwable) request.getAttribute("theException"));
                            }
                        %>
                    </c:if>
                </ul>
            </div>
        </c:forEach>
    </div>
</cpp:model>
