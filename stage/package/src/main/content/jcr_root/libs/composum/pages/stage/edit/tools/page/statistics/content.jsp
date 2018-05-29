<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:model var="model" type="com.composum.pages.commons.model.Page">
    <div class="${modelCssBase}_chart">
        <cpn:text tagName="h6" tagClass="${modelCssBase}_chart-title">Chart</cpn:text>
        <div class="${modelCssBase}_chartist">
        </div>
    </div>
    <div class="${modelCssBase}_referrers">
        <table class="table">
            <thead>
            <tr>
                <th class="url">Referrers</th>
                <th class="unique" title="unique">u</th>
                <th class="total" title="total">t</th>
            </tr>
            </thead>
            <tbody></tbody>
        </table>
    </div>
</cpp:model>
