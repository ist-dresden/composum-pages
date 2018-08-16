package com.composum.pages.options.microsite.service.impl;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * the import provider for the ZIP file based site import into one page
 * - implemented for the repository and as mock object for unit testing
 */
public interface MicrositeImportProvider {

    /**
     * retrieves a property value from a (probably not commited) resource
     *
     * @param resource     the resource object
     * @param name         the property name (elative path)
     * @param defaultValue the value if the property is not set)
     * @param <T>          the type of the property value
     * @return the current value or the default value if no such property exists
     */
    <T> T getProperty(Resource resource, String name, T defaultValue);

    /**
     * stores property values in a resource
     *
     * @param resource the resource to modify
     * @param name     the property name (elative path)
     * @param value    the value - property will be removed if this value is <code>null</code>
     */
    void setProperty(Resource resource, String name, Object value);

    /**
     * clear the ZIP parent resource and remove all content imported before
     *
     * @param contentRoot the content resource of the landing page
     * @throws PersistenceException if an error occurs
     */
    void clearContent(Resource contentRoot) throws PersistenceException;

    /**
     * creates a 'folder' if not always available
     *
     * @param contentRoot the content resource of the landing page
     * @param path        the folder path relative to the content root
     * @throws PersistenceException if an error occurs
     */
    void createFolder(Resource contentRoot, String path)
            throws PersistenceException;

    /**
     * save an imported source file as child of the given parent resource
     *
     * @param parentResource the parent resource of the file
     * @param name           the filename
     * @param sourceContent  the content of the file (String, encoding=UTF-8)
     * @throws IOException if an error occurs
     */
    void storeSourceFile(Resource parentResource, String name, String sourceContent)
            throws IOException;

    /**
     * save an imported binary file as child of the given parent resource
     *
     * @param parentResource the parent resource of the file
     * @param name           the filename
     * @param content        the content of the file
     * @throws IOException if an error occurs
     */
    void storeBinaryFile(Resource parentResource, String name, InputStream content)
            throws IOException;

    /**
     * retrieves the current user
     *
     * @param contentRoot the target resource
     */
    String getCurrentUser(Resource contentRoot);
}
