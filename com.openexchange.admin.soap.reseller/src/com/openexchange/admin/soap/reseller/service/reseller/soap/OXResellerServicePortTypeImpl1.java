
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package com.openexchange.admin.soap.reseller.service.reseller.soap;

import java.util.logging.Logger;

/**
 * This class was generated by Apache CXF 2.6.0
 * 2012-06-06T11:54:37.349+02:00
 * Generated source version: 2.6.0
 *
 */

@javax.jws.WebService(
                      serviceName = "OXResellerService",
                      portName = "OXResellerServiceHttpsSoap11Endpoint",
                      targetNamespace = "http://soap.reseller.admin.openexchange.com",

                      endpointInterface = "com.openexchange.admin.soap.reseller.service.reseller.soap.OXResellerServicePortType")

public class OXResellerServicePortTypeImpl1 implements OXResellerServicePortType {

    private static final Logger LOG = Logger.getLogger(OXResellerServicePortTypeImpl.class.getName());

    /* (non-Javadoc)
     * @see com.openexchange.admin.soap.reseller.service.reseller.soap.OXResellerServicePortType#updateDatabaseRestrictions(com.openexchange.admin.soap.reseller.service.rmi.dataobjects.Credentials  creds )*
     */
    @Override
    public void updateDatabaseRestrictions(com.openexchange.admin.soap.reseller.service.rmi.dataobjects.Credentials creds) throws InvalidCredentialsException_Exception , StorageException_Exception , RemoteException_Exception , OXResellerException_Exception    {
        LOG.info("Executing operation updateDatabaseRestrictions");
        System.out.println(creds);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new InvalidCredentialsException_Exception("InvalidCredentialsException...");
        //throw new StorageException_Exception("StorageException...");
        //throw new RemoteException_Exception("RemoteException...");
        //throw new OXResellerException_Exception("OXResellerException...");
    }

    /* (non-Javadoc)
     * @see com.openexchange.admin.soap.reseller.service.reseller.soap.OXResellerServicePortType#change(com.openexchange.admin.soap.reseller.service.reseller.soap.Change  parameters )*
     */
    @Override
    public void change(Change parameters) throws InvalidCredentialsException_Exception , StorageException_Exception , RemoteException_Exception , OXResellerException_Exception , InvalidDataException_Exception    {
        LOG.info("Executing operation change");
        System.out.println(parameters);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new InvalidCredentialsException_Exception("InvalidCredentialsException...");
        //throw new StorageException_Exception("StorageException...");
        //throw new RemoteException_Exception("RemoteException...");
        //throw new OXResellerException_Exception("OXResellerException...");
        //throw new InvalidDataException_Exception("InvalidDataException...");
    }

    /* (non-Javadoc)
     * @see com.openexchange.admin.soap.reseller.service.reseller.soap.OXResellerServicePortType#getAvailableRestrictions(com.openexchange.admin.soap.reseller.service.rmi.dataobjects.Credentials  creds )*
     */
    @Override
    public java.util.List<com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.Restriction> getAvailableRestrictions(com.openexchange.admin.soap.reseller.service.rmi.dataobjects.Credentials creds) throws InvalidCredentialsException_Exception , StorageException_Exception , RemoteException_Exception , OXResellerException_Exception    {
        LOG.info("Executing operation getAvailableRestrictions");
        System.out.println(creds);
        try {
            java.util.List<com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.Restriction> _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new InvalidCredentialsException_Exception("InvalidCredentialsException...");
        //throw new StorageException_Exception("StorageException...");
        //throw new RemoteException_Exception("RemoteException...");
        //throw new OXResellerException_Exception("OXResellerException...");
    }

    /* (non-Javadoc)
     * @see com.openexchange.admin.soap.reseller.service.reseller.soap.OXResellerServicePortType#removeDatabaseRestrictions(com.openexchange.admin.soap.reseller.service.reseller.soap.RemoveDatabaseRestrictions  parameters )*
     */
    @Override
    public void removeDatabaseRestrictions(RemoveDatabaseRestrictions parameters) throws InvalidCredentialsException_Exception , StorageException_Exception , RemoteException_Exception , OXResellerException_Exception    {
        LOG.info("Executing operation removeDatabaseRestrictions");
        System.out.println(parameters);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new InvalidCredentialsException_Exception("InvalidCredentialsException...");
        //throw new StorageException_Exception("StorageException...");
        //throw new RemoteException_Exception("RemoteException...");
        //throw new OXResellerException_Exception("OXResellerException...");
    }

    /* (non-Javadoc)
     * @see com.openexchange.admin.soap.reseller.service.reseller.soap.OXResellerServicePortType#delete(com.openexchange.admin.soap.reseller.service.reseller.soap.Delete  parameters )*
     */
    @Override
    public void delete(Delete parameters) throws InvalidCredentialsException_Exception , StorageException_Exception , RemoteException_Exception , OXResellerException_Exception    {
        LOG.info("Executing operation delete");
        System.out.println(parameters);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new InvalidCredentialsException_Exception("InvalidCredentialsException...");
        //throw new StorageException_Exception("StorageException...");
        //throw new RemoteException_Exception("RemoteException...");
        //throw new OXResellerException_Exception("OXResellerException...");
    }

    /* (non-Javadoc)
     * @see com.openexchange.admin.soap.reseller.service.reseller.soap.OXResellerServicePortType#list(java.lang.String  searchPattern ,)com.openexchange.admin.soap.reseller.service.rmi.dataobjects.Credentials  creds )*
     */
    @Override
    public java.util.List<com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.ResellerAdmin> list(java.lang.String searchPattern,com.openexchange.admin.soap.reseller.service.rmi.dataobjects.Credentials creds) throws InvalidCredentialsException_Exception , StorageException_Exception , RemoteException_Exception , InvalidDataException_Exception    {
        LOG.info("Executing operation list");
        System.out.println(searchPattern);
        System.out.println(creds);
        try {
            java.util.List<com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.ResellerAdmin> _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new InvalidCredentialsException_Exception("InvalidCredentialsException...");
        //throw new StorageException_Exception("StorageException...");
        //throw new RemoteException_Exception("RemoteException...");
        //throw new InvalidDataException_Exception("InvalidDataException...");
    }

    /* (non-Javadoc)
     * @see com.openexchange.admin.soap.reseller.service.reseller.soap.OXResellerServicePortType#getRestrictionsFromContext(com.openexchange.admin.soap.reseller.service.reseller.soap.dataobjects.ResellerContext  ctx ,)com.openexchange.admin.soap.reseller.service.rmi.dataobjects.Credentials  creds )*
     */
    @Override
    public java.util.List<com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.Restriction> getRestrictionsFromContext(com.openexchange.admin.soap.reseller.service.reseller.soap.dataobjects.ResellerContext ctx,com.openexchange.admin.soap.reseller.service.rmi.dataobjects.Credentials creds) throws InvalidCredentialsException_Exception , DuplicateExtensionException_Exception , StorageException_Exception , RemoteException_Exception , OXResellerException_Exception , InvalidDataException_Exception    {
        LOG.info("Executing operation getRestrictionsFromContext");
        System.out.println(ctx);
        System.out.println(creds);
        try {
            java.util.List<com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.Restriction> _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new InvalidCredentialsException_Exception("InvalidCredentialsException...");
        //throw new DuplicateExtensionException_Exception("DuplicateExtensionException...");
        //throw new StorageException_Exception("StorageException...");
        //throw new RemoteException_Exception("RemoteException...");
        //throw new OXResellerException_Exception("OXResellerException...");
        //throw new InvalidDataException_Exception("InvalidDataException...");
    }

    /* (non-Javadoc)
     * @see com.openexchange.admin.soap.reseller.service.reseller.soap.OXResellerServicePortType#create(com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.ResellerAdmin  adm ,)com.openexchange.admin.soap.reseller.service.rmi.dataobjects.Credentials  creds )*
     */
    @Override
    public com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.ResellerAdmin create(com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.ResellerAdmin adm,com.openexchange.admin.soap.reseller.service.rmi.dataobjects.Credentials creds) throws InvalidCredentialsException_Exception , StorageException_Exception , RemoteException_Exception , OXResellerException_Exception , InvalidDataException_Exception    {
        LOG.info("Executing operation create");
        System.out.println(adm);
        System.out.println(creds);
        try {
            com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.ResellerAdmin _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new InvalidCredentialsException_Exception("InvalidCredentialsException...");
        //throw new StorageException_Exception("StorageException...");
        //throw new RemoteException_Exception("RemoteException...");
        //throw new OXResellerException_Exception("OXResellerException...");
        //throw new InvalidDataException_Exception("InvalidDataException...");
    }

    /* (non-Javadoc)
     * @see com.openexchange.admin.soap.reseller.service.reseller.soap.OXResellerServicePortType#getMultipleData(java.util.List<com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.ResellerAdmin>  admins ,)com.openexchange.admin.soap.reseller.service.rmi.dataobjects.Credentials  creds )*
     */
    @Override
    public java.util.List<com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.ResellerAdmin> getMultipleData(java.util.List<com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.ResellerAdmin> admins,com.openexchange.admin.soap.reseller.service.rmi.dataobjects.Credentials creds) throws InvalidCredentialsException_Exception , StorageException_Exception , RemoteException_Exception , OXResellerException_Exception , InvalidDataException_Exception    {
        LOG.info("Executing operation getMultipleData");
        System.out.println(admins);
        System.out.println(creds);
        try {
            java.util.List<com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.ResellerAdmin> _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new InvalidCredentialsException_Exception("InvalidCredentialsException...");
        //throw new StorageException_Exception("StorageException...");
        //throw new RemoteException_Exception("RemoteException...");
        //throw new OXResellerException_Exception("OXResellerException...");
        //throw new InvalidDataException_Exception("InvalidDataException...");
    }

    /* (non-Javadoc)
     * @see com.openexchange.admin.soap.reseller.service.reseller.soap.OXResellerServicePortType#getData(com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.ResellerAdmin  adm ,)com.openexchange.admin.soap.reseller.service.rmi.dataobjects.Credentials  creds )*
     */
    @Override
    public com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.ResellerAdmin getData(com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.ResellerAdmin adm,com.openexchange.admin.soap.reseller.service.rmi.dataobjects.Credentials creds) throws InvalidCredentialsException_Exception , StorageException_Exception , RemoteException_Exception , OXResellerException_Exception , InvalidDataException_Exception    {
        LOG.info("Executing operation getData");
        System.out.println(adm);
        System.out.println(creds);
        try {
            com.openexchange.admin.soap.reseller.service.reseller.rmi.dataobjects.ResellerAdmin _return = null;
            return _return;
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new InvalidCredentialsException_Exception("InvalidCredentialsException...");
        //throw new StorageException_Exception("StorageException...");
        //throw new RemoteException_Exception("RemoteException...");
        //throw new OXResellerException_Exception("OXResellerException...");
        //throw new InvalidDataException_Exception("InvalidDataException...");
    }

    /* (non-Javadoc)
     * @see com.openexchange.admin.soap.reseller.service.reseller.soap.OXResellerServicePortType#updateDatabaseModuleAccessRestrictions(com.openexchange.admin.soap.reseller.service.reseller.soap.UpdateDatabaseModuleAccessRestrictions  parameters )*
     */
    @Override
    public void updateDatabaseModuleAccessRestrictions(UpdateDatabaseModuleAccessRestrictions parameters) throws InvalidCredentialsException_Exception , StorageException_Exception , RemoteException_Exception , OXResellerException_Exception    {
        LOG.info("Executing operation updateDatabaseModuleAccessRestrictions");
        System.out.println(parameters);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new InvalidCredentialsException_Exception("InvalidCredentialsException...");
        //throw new StorageException_Exception("StorageException...");
        //throw new RemoteException_Exception("RemoteException...");
        //throw new OXResellerException_Exception("OXResellerException...");
    }

    /* (non-Javadoc)
     * @see com.openexchange.admin.soap.reseller.service.reseller.soap.OXResellerServicePortType#initDatabaseRestrictions(com.openexchange.admin.soap.reseller.service.reseller.soap.InitDatabaseRestrictions  parameters )*
     */
    @Override
    public void initDatabaseRestrictions(InitDatabaseRestrictions parameters) throws InvalidCredentialsException_Exception , StorageException_Exception , RemoteException_Exception , OXResellerException_Exception    {
        LOG.info("Executing operation initDatabaseRestrictions");
        System.out.println(parameters);
        try {
        } catch (java.lang.Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        //throw new InvalidCredentialsException_Exception("InvalidCredentialsException...");
        //throw new StorageException_Exception("StorageException...");
        //throw new RemoteException_Exception("RemoteException...");
        //throw new OXResellerException_Exception("OXResellerException...");
    }

}
