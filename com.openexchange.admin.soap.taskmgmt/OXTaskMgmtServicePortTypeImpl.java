
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package com.openexchange.admin.soap.taskmgmt;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import com.openexchange.admin.rmi.OXTaskMgmtInterface;
import com.openexchange.admin.rmi.dataobjects.xsd.Credentials;
import com.openexchange.admin.rmi.exceptions.InvalidCredentialsException;
import com.openexchange.admin.rmi.exceptions.InvalidDataException;
import com.openexchange.admin.rmi.exceptions.StorageException;
import com.openexchange.admin.rmi.exceptions.TaskManagerException;
import com.openexchange.admin.soap.dataobjects.xsd.Context;
import com.openexchange.admin.soap.dataobjects.xsd.Database;
import com.openexchange.admin.soap.dataobjects.xsd.Entry;
import com.openexchange.admin.soap.dataobjects.xsd.SOAPMapEntry;
import com.openexchange.admin.soap.dataobjects.xsd.SOAPStringMap;
import com.openexchange.admin.soap.dataobjects.xsd.SOAPStringMapMap;

/**
 * This class was generated by Apache CXF 2.6.0
 * 2012-05-31T16:29:10.012+02:00
 * Generated source version: 2.6.0
 * 
 */

@javax.jws.WebService(
                      serviceName = "OXTaskMgmtService",
                      portName = "OXTaskMgmtServiceHttpsEndpoint",
                      targetNamespace = "http://soap.admin.openexchange.com",
                      // wsdlLocation = "null",
                      endpointInterface = "com.openexchange.admin.soap.taskmgmt.OXTaskMgmtServicePortType")
                      
public class OXTaskMgmtServicePortTypeImpl implements OXTaskMgmtServicePortType {

    public static final AtomicReference<OXTaskMgmtInterface> RMI_REFERENCE = new AtomicReference<OXTaskMgmtInterface>();

    private static OXTaskMgmtInterface getTaskMgmtInterface() throws RemoteException_Exception {
        final OXTaskMgmtInterface taskMgmtInterface = RMI_REFERENCE.get();
        if (null == taskMgmtInterface) {
            throw new RemoteException_Exception("Missing "+OXTaskMgmtInterface.class.getName() + " instance.");
        }
        return taskMgmtInterface;
    }

    @Override
    public java.lang.Object getTaskResults(final com.openexchange.admin.soap.dataobjects.xsd.Context ctx,final com.openexchange.admin.rmi.dataobjects.xsd.Credentials cred,final java.lang.Integer id) throws StorageException_Exception , InvalidCredentialsException_Exception , InvalidDataException_Exception , RemoteException_Exception , ExecutionException_Exception , InterruptedException_Exception    { 
        final OXTaskMgmtInterface taskMgmtInterface = getTaskMgmtInterface();
        try {
            return taskMgmtInterface.getTaskResults(soap2Context(ctx), soap2Credentials(cred), id.intValue());
        } catch (final RemoteException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final InvalidCredentialsException e) {
            throw new InvalidCredentialsException_Exception(e.getMessage(), e);
        } catch (final StorageException e) {
            throw new StorageException_Exception(e.getMessage(), e);
        } catch (final InvalidDataException e) {
            throw new InvalidDataException_Exception(e.getMessage(), e);
        } catch (final java.lang.InterruptedException e) {
            throw new InterruptedException_Exception(e.getMessage(), e);
        } catch (final ExecutionException e) {
            throw new ExecutionException_Exception(e.getMessage(), e);
        }
    }

    @Override
    public void flush(final Flush parameters) throws StorageException_Exception , InvalidCredentialsException_Exception , InvalidDataException_Exception , TaskManagerException_Exception , RemoteException_Exception    { 
        final OXTaskMgmtInterface taskMgmtInterface = getTaskMgmtInterface();
        try {
            taskMgmtInterface.flush(soap2Context(parameters.ctx), soap2Credentials(parameters.auth));
        } catch (final RemoteException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final InvalidCredentialsException e) {
            throw new InvalidCredentialsException_Exception(e.getMessage(), e);
        } catch (final StorageException e) {
            throw new StorageException_Exception(e.getMessage(), e);
        } catch (final InvalidDataException e) {
            throw new InvalidDataException_Exception(e.getMessage(), e);
        } catch (final TaskManagerException e) {
            throw new TaskManagerException_Exception(e.getMessage(), e);
        }
    }

    @Override
    public java.lang.String getJobList(final com.openexchange.admin.soap.dataobjects.xsd.Context ctx,final com.openexchange.admin.rmi.dataobjects.xsd.Credentials cred) throws StorageException_Exception , InvalidCredentialsException_Exception , InvalidDataException_Exception , RemoteException_Exception    { 
        final OXTaskMgmtInterface taskMgmtInterface = getTaskMgmtInterface();
        try {
            return taskMgmtInterface.getJobList(soap2Context(ctx), soap2Credentials(cred));
        } catch (final RemoteException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final InvalidCredentialsException e) {
            throw new InvalidCredentialsException_Exception(e.getMessage(), e);
        } catch (final StorageException e) {
            throw new StorageException_Exception(e.getMessage(), e);
        } catch (final InvalidDataException e) {
            throw new InvalidDataException_Exception(e.getMessage(), e);
        }
    }

    @Override
    public void deleteJob(final DeleteJob parameters) throws StorageException_Exception , InvalidCredentialsException_Exception , InvalidDataException_Exception , TaskManagerException_Exception , RemoteException_Exception    { 
        final OXTaskMgmtInterface taskMgmtInterface = getTaskMgmtInterface();
        try {
            taskMgmtInterface.deleteJob(soap2Context(parameters.ctx), soap2Credentials(parameters.auth), parameters.i.intValue());
        } catch (final RemoteException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final InvalidCredentialsException e) {
            throw new InvalidCredentialsException_Exception(e.getMessage(), e);
        } catch (final StorageException e) {
            throw new StorageException_Exception(e.getMessage(), e);
        } catch (final InvalidDataException e) {
            throw new InvalidDataException_Exception(e.getMessage(), e);
        } catch (final TaskManagerException e) {
            throw new TaskManagerException_Exception(e.getMessage(), e);
        }
    }

    private static com.openexchange.admin.rmi.dataobjects.Credentials soap2Credentials(final Credentials soapCredentials) {
        if (null == soapCredentials) {
            return null;
        }
        final com.openexchange.admin.rmi.dataobjects.Credentials credentials = new com.openexchange.admin.rmi.dataobjects.Credentials();
        credentials.setLogin(soapCredentials.getLogin());
        credentials.setPassword(soapCredentials.getPassword());
        return credentials;
    }

    private static com.openexchange.admin.rmi.dataobjects.Context soap2Context(final Context soapContext) {
        if (null == soapContext) {
            return null;
        }
        final com.openexchange.admin.rmi.dataobjects.Context ret = new com.openexchange.admin.rmi.dataobjects.Context();
        ret.setAverage_size(soapContext.getAverageSize());
        ret.setEnabled(soapContext.isEnabled());
        ret.setFilestore_name(soapContext.getFilestoreName());
        ret.setFilestoreId(soapContext.getFilestoreId());
        ret.setId(soapContext.getId());
        if (null != soapContext.getLoginMappings()) {
            for (String loginMapping : soapContext.getLoginMappings()) {
                if (null != loginMapping) {
                    ret.addLoginMapping(loginMapping);
                }
            }
        }
        ret.setMaxQuota(soapContext.getMaxQuota());
        ret.setName(soapContext.getName());
        ret.setUsedQuota(soapContext.getUsedQuota());
        ret.setReadDatabase(soap2Database(soapContext.getReadDatabase()));
        ret.setWriteDatabase(soap2Database(soapContext.getWriteDatabase()));
        ret.setUserAttributes(soap2MapMap(soapContext.getUserAttributes()));
        return ret;
    }

    private static com.openexchange.admin.rmi.dataobjects.Database soap2Database(final Database soapDatabase) {
        if (null == soapDatabase) {
            return null;
        }
        final com.openexchange.admin.rmi.dataobjects.Database ret = new com.openexchange.admin.rmi.dataobjects.Database();

        Integer itg = soapDatabase.getClusterWeight();
        if (itg != null) {
            ret.setClusterWeight(itg);
        }

        itg = soapDatabase.getCurrentUnits();
        if (itg != null) {
            ret.setCurrentUnits(itg);
        }

        String tmp = soapDatabase.getDriver();
        if (tmp != null) {
            ret.setDriver(tmp);
        }

        itg = soapDatabase.getId();
        if (itg != null) {
            ret.setId(itg);
        }

        tmp = soapDatabase.getLogin();
        if (tmp != null) {
            ret.setLogin(tmp);
        }

        Boolean bool = soapDatabase.isMaster();
        if (tmp != null) {
            ret.setMaster(bool);
        }

        itg = soapDatabase.getMasterId();
        if (itg != null) {
            ret.setMasterId(itg);
        }

        itg = soapDatabase.getMaxUnits();
        if (itg != null) {
            ret.setMaxUnits(itg);
        }

        tmp = soapDatabase.getName();
        if (tmp != null) {
            ret.setName(tmp);
        }

        tmp = soapDatabase.getPassword();
        if (tmp != null) {
            ret.setPassword(tmp);
        }

        itg = soapDatabase.getPoolHardLimit();
        if (itg != null) {
            ret.setPoolHardLimit(itg);
        }

        itg = soapDatabase.getPoolInitial();
        if (itg != null) {
            ret.setPoolInitial(itg);
        }

        itg = soapDatabase.getPoolMax();
        if (itg != null) {
            ret.setPoolMax(itg);
        }

        itg = soapDatabase.getReadId();
        if (itg != null) {
            ret.setRead_id(itg);
        }

        tmp = soapDatabase.getScheme();
        if (tmp != null) {
            ret.setScheme(tmp);
        }

        tmp = soapDatabase.getUrl();
        if (tmp != null) {
            ret.setUrl(tmp);
        }
        return ret;
    }

    private static Map<String, Map<String, String>> soap2MapMap(final SOAPStringMapMap soapStringMapMap) {
        if (null == soapStringMapMap) {
            return null;
        }
        final java.util.List<SOAPMapEntry> entries = soapStringMapMap.getEntries();
        final Map<String, Map<String, String>> map = new HashMap<String, Map<String,String>>(entries.size());
        for (final SOAPMapEntry soapMapEntry : entries) {
            if (null != soapMapEntry)
                map.put(soapMapEntry.getKey(), soap2Map(soapMapEntry.getValue()));
        }
        return map;
    }

    private static Map<String, String> soap2Map(final SOAPStringMap soapStringMap) {
        if (null == soapStringMap) {
            return null;
        }
        final java.util.List<Entry> entries = soapStringMap.getEntries();
        final Map<String, String> map = new HashMap<String, String>(entries.size());
        for (final Entry entry : entries) {
            if (null != entry)
                map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

}
