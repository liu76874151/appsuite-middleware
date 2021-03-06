
/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package com.openexchange.custom.parallels.soap;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import com.openexchange.admin.rmi.OXLoginInterface;
import com.openexchange.admin.rmi.exceptions.InvalidCredentialsException;
import com.openexchange.admin.rmi.exceptions.InvalidDataException;
import com.openexchange.admin.rmi.exceptions.StorageException;
import com.openexchange.custom.parallels.soap.rmi.Credentials;

/**
 * This class was generated by Apache CXF 2.6.1
 * 2012-07-11T15:32:34.688+02:00
 * Generated source version: 2.6.1
 *
 */

@javax.jws.WebService(
                      serviceName = "OXServerService",
                      portName = "OXServerServiceHttpsEndpoint",
                      targetNamespace = "http://soap.parallels.custom.openexchange.com",
                      // wsdlLocation = "null",
                      endpointInterface = "com.openexchange.custom.parallels.soap.OXServerServicePortType")

public class OXServerServicePortTypeImpl implements OXServerServicePortType {

    public static final AtomicReference<OXLoginInterface> RMI_REFERENCE = new AtomicReference<OXLoginInterface>();

    private static OXLoginInterface getLoginInterface() throws RemoteException_Exception {
        final OXLoginInterface loginInterface = RMI_REFERENCE.get();
        if (null == loginInterface) {
            throw new RemoteException_Exception("Missing "+OXLoginInterface.class.getName() + " instance.");
        }
        return loginInterface;
    }

    @Override
    public List<Bundle> getServerBundleList(final Credentials auth) throws InvalidCredentialsException_Exception , InvalidDataException_Exception , RemoteException_Exception , StorageException_Exception    {
        try {
            final OXLoginInterface loginInterface = getLoginInterface();
            loginInterface.login(soap2Credentials(auth));
        } catch (final RemoteException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final StorageException e) {
            throw new StorageException_Exception(e.getMessage(), e);
        } catch (final InvalidCredentialsException e) {
            throw new InvalidCredentialsException_Exception(e.getMessage(), e);
        } catch (final InvalidDataException e) {
            throw new InvalidDataException_Exception(e.getMessage(), e);
        }
        try {
            final JMXHelper jmxHelper = new JMXHelper();
            final Bundle[] bundles = jmxHelper.listBundles();
            if (null == bundles || 0 == bundles.length) {
                return Collections.emptyList();
            }
            return Arrays.asList(bundles);
        } catch (final MalformedObjectNameException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final InstanceNotFoundException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final NullPointerException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final MBeanException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final ReflectionException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final IOException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        }
    }

    @Override
    public java.lang.String getServerVersion(final com.openexchange.custom.parallels.soap.rmi.Credentials auth) throws InvalidCredentialsException_Exception , InvalidDataException_Exception , RemoteException_Exception , StorageException_Exception    {
        try {
            final OXLoginInterface loginInterface = getLoginInterface();
            loginInterface.login(soap2Credentials(auth));
        } catch (final RemoteException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final StorageException e) {
            throw new StorageException_Exception(e.getMessage(), e);
        } catch (final InvalidCredentialsException e) {
            throw new InvalidCredentialsException_Exception(e.getMessage(), e);
        } catch (final InvalidDataException e) {
            throw new InvalidDataException_Exception(e.getMessage(), e);
        }
        try {
            final JMXHelper jmxHelper = new JMXHelper();
            return jmxHelper.getServerVersion();
        } catch (final MalformedObjectNameException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final InstanceNotFoundException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final NullPointerException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final MBeanException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final ReflectionException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
        } catch (final IOException e) {
            throw new RemoteException_Exception(e.getMessage(), e);
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

}
