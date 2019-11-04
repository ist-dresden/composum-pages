<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<sling:defineObjects/>
<cpn:component var="current" type="com.composum.pages.stage.model.tools.PropertiesComparatorRoot" scope="request">
    <div class="composum-pages-tools-comparator_node ${current.equalPath?'same-path':''} ${current.equalLanguage?'same-language':''} ${current.equalVersion?'same-version':''}">
        <div class="composum-pages-tools-comparator_node-head">
            <div class="composum-pages-tools-comparator_node-head_indent"></div>
            <div class="composum-pages-tools-comparator_node-head_left">
                <div class="composum-pages-tools-comparator_node-name"
                     title="${cpn:text(current.left.path)}">${cpn:text(current.left.name)}</div>
                <div class="composum-pages-tools-comparator_node-type">${cpn:text(current.left.typeHint)}</div>
                <div class="composum-pages-tools-comparator_node-meta">
                    <div class="composum-pages-tools-comparator_node-path">${cpn:text(current.left.path)}</div>
                    <cpn:text class="composum-pages-tools-comparator_node-language" value="${current.left.language}"
                              test="${not empty current.left.language}" format="language: {}"/>
                    <cpn:text class="composum-pages-tools-comparator_node-version" value="${current.left.version}"
                              test="${not empty current.left.version}" format="version: {}"/>
                </div>
            </div>
            <div class="composum-pages-tools-comparator_node-head_right">
                <div class="composum-pages-tools-comparator_node-name"
                     title="${cpn:text(current.right.path)}">${cpn:text(current.right.name)}</div>
                <div class="composum-pages-tools-comparator_node-type">${cpn:text(current.right.typeHint)}</div>
                <div class="composum-pages-tools-comparator_node-meta">
                    <div class="composum-pages-tools-comparator_node-path">${cpn:text(current.right.path)}</div>
                    <cpn:text class="composum-pages-tools-comparator_node-language" value="${current.right.language}"
                              test="${not empty current.right.language}" format="language: {}"/>
                    <cpn:text class="composum-pages-tools-comparator_node-version" value="${current.right.version}"
                              test="${not empty current.right.version}" format="version: {}"/>
                </div>
            </div>
        </div>
        <sling:include replaceSelectors="content"/>
    </div>
</cpn:component>
