<%@page session="false" pageEncoding="UTF-8" %>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.2" %>
<%@taglib prefix="cpn" uri="http://sling.composum.com/cpnl/1.0" %>
<%@taglib prefix="cpp" uri="http://sling.composum.com/cppl/1.0" %>
<cpp:defineObjects/>
<style type="text/css">
    .composum-pages-debug-frame {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 9999;
        box-sizing: border-box;
        border: 1px solid red;
        pointer-events: none;
    }

    .composum-pages-debug-frame_canvas {
        position: absolute;
        top: 1px;
        right: 1px;
        width: 300px;
        background: rgba(255, 255, 255, 0.85);
        color: black;
        font-family: sans-serif;
        font-size: 13px;
        line-height: 13px;
    }

    .composum-pages-debug-frame_page-view,
    .composum-pages-debug-frame_dnd-view,
    .composum-pages-debug-frame_ptr-view {
        width: 100%;
    }

    .composum-pages-debug-frame_page-view .value,
    .composum-pages-debug-frame_dnd-view .value,
    .composum-pages-debug-frame_ptr-view .value {
        display: inline-block;
        box-sizing: border-box;
        width: 22%;
        padding: 3px 5px;
        text-align: right;
    }

    .composum-pages-debug-frame_dnd-view .path,
    .composum-pages-debug-frame_ptr-view .path {
        display: block;
        box-sizing: border-box;
        width: 100%;
    }

    .composum-pages-debug-frame_dnd-view .pointer {
        display: block;
        position: fixed;
        width: 5px;
        height: 5px;
        background: red;
    }

    .composum-pages-debug-frame_canvas .hidden {
        display: none;
    }
</style>
<div class="composum-pages-debug-frame">
    <div class="composum-pages-debug-frame_canvas">
        <div class="composum-pages-debug-frame_page-view">
            <span class="value value-top"></span>
            <span class="value value-left"></span>
            <span class="value value-width"></span>
            <span class="value value-height"></span>
        </div>
        <div class="composum-pages-debug-frame_ptr-view">
            <span class="value value-x"></span>
            <span class="value value-y"></span>
            <span class="path"></span>
            <span class="value value-x1"></span>
            <span class="value value-y1"></span>
            <span class="value value-x2"></span>
            <span class="value value-y2"></span>
        </div>
        <div class="composum-pages-debug-frame_dnd-view">
            <span class="pointer"></span>
            <span class="value value-x"></span>
            <span class="value value-y"></span>
            <span class="value value-count"></span>
            <span class="value value-ipos"></span>
            <span class="path"></span>
        </div>
    </div>
</div>