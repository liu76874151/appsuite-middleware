
package com.openexchange.admin.soap.usercopy.soap;

import javax.xml.ws.WebFault;


/**
 * This class was generated by Apache CXF 2.6.0
 * 2012-06-06T08:52:52.842+02:00
 * Generated source version: 2.6.0
 */

@WebFault(name = "StorageException", targetNamespace = "http://soap.copy.user.admin.openexchange.com")
public class StorageException_Exception extends java.lang.Exception {

    private com.openexchange.admin.soap.usercopy.soap.StorageException storageException;

    public StorageException_Exception() {
        super();
    }

    public StorageException_Exception(String message) {
        super(message);
    }

    public StorageException_Exception(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageException_Exception(String message, com.openexchange.admin.soap.usercopy.soap.StorageException storageException) {
        super(message);
        this.storageException = storageException;
    }

    public StorageException_Exception(String message, com.openexchange.admin.soap.usercopy.soap.StorageException storageException, Throwable cause) {
        super(message, cause);
        this.storageException = storageException;
    }

    public com.openexchange.admin.soap.usercopy.soap.StorageException getFaultInfo() {
        return this.storageException;
    }
}
