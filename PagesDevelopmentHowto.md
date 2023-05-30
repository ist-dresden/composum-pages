# Some hints for developers of pages

This is a wild collection of hints how to do things when extending Pages itself, developing components etc., in no
particular order.
This is not about how to use Composum Pages.

## Client libraries

Consult http://localhost:9090/system/console/clientlibs to get an overview what is there, find categories etc., and
find examples where libraries are defined and declared and of js / css files.

## Pages Dialogs

Entry point for many dialogs:
pages/stage/package/src/main/content/jcr_root/libs/composum/pages/stage/edit/js/dialogs.js

## Extensions to pages

Compare ToolsCollection and classes using that: provide a collection with
resource type <code>composum/pages/tools/collection</code>) with children that declare that extension. Compare class
comment in ToolsCollection.
