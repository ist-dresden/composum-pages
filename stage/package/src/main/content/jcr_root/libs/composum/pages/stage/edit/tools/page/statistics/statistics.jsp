<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="statistics" type="com.composum.pages.stage.model.edit.FramePage" mode="none"
             cssClasses="composum-pages-tools">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
            </div>
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
            </div>
        </div>
    </div>
    <div class="composum-pages-tools_panel">
        <div class="${statisticsCssBase}_statistics-view">
            <div class="${statisticsCssBase}_content">
                    <%-- <sling:call script="content.jsp"/> - load after init via Ajax --%>
            </div>
        </div>
    </div>
</cpp:element>
