<%@page session="false" pageEncoding="UTF-8" %><%--
--%><%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %><%--
--%><%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %><%--
--%><%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><cpp:defineObjects/>
<cpp:element var="textImage" type="com.composum.pages.components.model.textimage.TextImage"
               cssAdd="@{textImageCssBase}_@{textImage.floatingText?'floating':'block'} @{textImageCssBase}_@{textImage.imagePosition}">
   <c:if test="${!textImage.imageBottom}">
       <div class="${textImageCssBase}_image">
           <cpp:include path="image" resourceType="composum/pages/components/element/image"/>
       </div>
   </c:if>
   <c:choose>
       <c:when test="${textImage.textValid}">
           <div class="${textImageCssBase}_text-block">
               <cpn:text tagName="h${textImage.titleLevel}" tagClass="${textImageCssBase}_title" value="${textImage.title}"/>
               <cpn:text tagClass="${textImageCssBase}_text" value="${textImage.text}" type="rich"/>
           </div>
       </c:when>
       <c:otherwise>
           <cpp:include replaceSelectors="placeholder"/>
       </c:otherwise>
   </c:choose>
   <c:if test="${textImage.imageBottom}">
       <div class="${textImageCssBase}_image">
           <cpp:include path="image" resourceType="composum/pages/components/element/image"/>
       </div>
   </c:if>
</cpp:element>
