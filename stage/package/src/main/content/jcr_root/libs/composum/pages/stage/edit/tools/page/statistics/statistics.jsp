<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cpp:defineFrameObjects/>
<cpp:element var="statistics" type="com.composum.pages.stage.model.edit.FramePage" mode="none"
             cssClasses="composum-pages-tools">
    <div class="composum-pages-tools_actions btn-toolbar">
        <div class="composum-pages-tools_left-actions">
            <div class="${statisticsCssBase}_range composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-chevron-left time-range-select_prev composum-pages-tools_button btn btn-default"
                        title="Earlier"><span
                        class="composum-pages-tools_button-label">Earlier</span></button>
                <div class="time-range-select_type btn-group" role="group">
                    <button type="button"
                            class="composum-pages-tools_button btn btn-default dropdown-toggle"
                            data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    </button>
                    <ul class="dropdown-menu" role="menu">
                        <li><a class="days" href="#"></a></li>
                        <li><a class="weeks" href="#"></a></li>
                        <li><a class="months" href="#"></a></li>
                        <li><a class="years" href="#"></a></li>
                    </ul>
                </div>
                <button type="button"
                        class="fa fa-bullseye time-range-select_current composum-pages-tools_button btn btn-default"
                        title="Current"><span
                        class="composum-pages-tools_button-label">Current</span></button>
                <button type="button"
                        class="fa fa-chevron-right time-range-select_next composum-pages-tools_button btn btn-default"
                        title="Later"><span
                        class="composum-pages-tools_button-label">Later</span></button>
            </div>
        </div>
        <div class="composum-pages-tools_right-actions">
            <div class="composum-pages-tools_button-group btn-group btn-group-sm" role="group">
                <button type="button"
                        class="fa fa-refresh ${statisticsCssBase}_reload composum-pages-tools_button btn btn-default"
                        title="Reaload"><span
                        class="composum-pages-tools_button-label">Reload</span></button>
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
