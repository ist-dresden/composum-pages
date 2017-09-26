/**
 * a simple example to change the resource type of all resources in the subtree of a root resource
 * the resource type and the new type is specified as a regex pattern and the corresponding replacement
 * this script can be executed by the Groovy executor in the source view of the Composum Console Browser
 */
package apps.prototype.pages.components.tools

import com.composum.pages.commons.service.EditService
import com.composum.sling.core.filter.ResourceFilter

def service = getService(EditService)
def filter = ResourceFilter.ALL
def root = getResource('/content/sites/ist/composum')

service.changeResourceType(filter, root,
        '^composum/prototype/pages/site/(.+)$','composum/pages/components/simple/$1'
)

commit()
