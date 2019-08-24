<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:model var="model" type="com.composum.pages.components.model.asset.Video"
           test="@{model.withMetaInfo}">
    <cpn:div class="${modelCSS}_meta">
        <cpn:link test="${not empty model.copyrightUrl}" body="true" class="${modelCSS}_url"
                  href="${model.copyrightUrl}" rel="copyright">
            <cpn:text class="${modelCSS}_copyright" value="${model.copyright}" type="rich"/>
        </cpn:link>
        <cpn:link test="${not empty model.licenseUrl}" body="true" class="${modelCSS}_url"
                  href="${model.licenseUrl}" rel="license">
            <cpn:text class="${modelCSS}_license" value="${model.license}"/>
        </cpn:link>
    </cpn:div>
</cpp:model>
