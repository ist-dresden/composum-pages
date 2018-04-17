<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="element" type="com.composum.pages.commons.model.Element">
    <sling:call script="single.jsp"/>
    <div class="col-xs-12">
        <cpp:include path="dialog" resourceType="composum/pages/components/element/codeblock"/>
        <cpp:include path="rendered" resourceType="composum/pages/components/element/codeblock"/>
    </div>
</cpp:element>
