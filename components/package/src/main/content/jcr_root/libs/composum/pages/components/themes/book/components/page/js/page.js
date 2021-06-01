$(document).ready(function () { // sidebar menu toggling...
    $('.composum-pages-components-page_content_nav_toggle').click(function (event) {
        $(event.currentTarget).parent().toggleClass('sidebar-visible');
    });
});
