<%@page session="false" pageEncoding="utf-8" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineFrameObjects/>
<cpp:widget label="Level" property="shape/level" type="select" default="default"
            options="default,primary,success,info,warning,danger"/>
<cpp:widget label="Icon" property="shape/icon" type="iconcombobox" default="number"
            options="number,plus,circle:dot,bullseye,dot-circle-o:dot circle,comment,wrench,none"
            typeahead="/bin/cpm/core/system.typeahead.json/libs/fonts/awesome/4.7.0/font-awesome-keys.txt"
            hint="the symbol - icon or number"/>
<cpp:widget label="Type" property="shape/type" type="select" hint="the figure of the symbol"
            options="circle,roundrect,rectangle" default="circle"/>
<cpp:widget label="Position" property="shape/position" type="position"
            hint="the position in percent"/>
<cpp:widget label="Placement" property="shape/placement" type="select" options=":default,top,left,right,bottom"
            hint="placement relative to the symbol"/>
