<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<cpp:element var="element" type="com.composum.pages.commons.model.Element">
    <div id="tab-single_head" class="panel-heading" role="tab">
        <h4 class="panel-title"><a role="button" data-toggle="collapse" href="#tab-single_body"
                                   aria-expanded="true" aria-controls="tab-single_body">Single Properties</a></h4>
    </div>
    <div id="tab-single_body" class="panel-collapse collapse in"
         role="tabpanel" aria-labelledby="tab-single_head">
        <div class="panel-body">
            <table class="table">
                <tbody>
                <tr>
                    <td>checkbox (1; boolean; removable)</td>
                    <td>${element.properties.single_checkbox_removable}</td>
                </tr>
                <tr>
                    <td>checkbox (2; boolean; true / false)</td>
                    <td>${element.properties.single_checkbox}</td>
                </tr>
                <tr>
                    <td>checkselect (1; removable)</td>
                        <%-- danger! the text tag is not rendered if text is empty; no 'td' if no value!; don't use:
                            <cpn:text tagName="td">${element.properties.single_checkselect_removable}</cpn:text> --%>
                    <td>${cpn:text(element.properties.single_checkselect_removable)}</td>
                </tr>
                <tr>
                    <td>checkselect (2; toggle value)</td>
                    <cpn:text tagName="td">${element.properties.single_checkselect}</cpn:text>
                </tr>
                <tr>
                    <td>link (URL)</td>
                    <td>
                        <a href="${cpn:url(slingRequest,element.properties.single_link_url)}">${cpn:text(element.properties.single_link_url)}</a>
                    </td>
                </tr>
                <tr>
                    <td>link (path)</td>
                    <td>
                        <a href="${cpn:url(slingRequest,element.properties.single_link_path)}">${cpn:text(element.properties.single_link_path)}</a>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>
</cpp:element>
