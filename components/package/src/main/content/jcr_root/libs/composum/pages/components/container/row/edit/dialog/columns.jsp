<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:widget label="columns" property="columns" type="select" required="true"
            options="-12-:100%,-6--6-:50% / 50%,-4--8-:33% / 66%,-8--4-:66% / 33%,-4--4--4-:33% / 33% / 33%,-3--6--3-:25% / 50% / 25%,-9--3-:75% / 25%,-3--9-:25% / 75%"/>
