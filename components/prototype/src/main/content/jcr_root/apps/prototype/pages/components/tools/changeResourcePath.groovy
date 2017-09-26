/**
 * a simple example to change all references to resources with a given path to a new path
 * this is implicit used if a resource is moved by the 'move' operation of the EditService
 * this script can be executed by the Groovy executor in the source view of the Composum Console Browser
 */
package apps.prototype.pages.components.tools

import com.composum.pages.commons.service.EditService
import com.composum.sling.core.filter.ResourceFilter
import com.composum.sling.core.filter.StringFilter

def service = getService(EditService)
def resourceFilter = ResourceFilter.ALL
def propertyFilter = StringFilter.ALL
def root = getResource('/content/sites/ist/composum')

service.changeReferences(resourceFilter, propertyFilter, root,
        '/content/sites/ist/composum/assets/background','/content/shared/composum/pages/assets/background'
)
service.changeReferences(resourceFilter, propertyFilter, root,
        '/content/sites/ist/composum/assets/general/Composum-color.svg','/content/shared/composum/pages/assets/logo/Composum-color.svg'
)

commit()
