package com.vanuston.medeil.client;

import java.rmi.RemoteException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import com.vanuston.medeil.util.StubType;
import com.vanuston.medeil.implementation.*;
import com.vanuston.medeil.util.Logger;
import com.vanuston.medeil.util.RegistryConstants;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final class RegistryFactory {
    public static int errCnt = 0;
    static Logger log = Logger.getLogger(RegistryFactory.class, "RegistryFactory") ;
    public static String serverHostIP;
    public static Integer serverPort = 20202;
    public static String currentUser="";
    private RegistryFactory() {
        super();
    }
    private static LabelPrint labelPrin;

    public static Object getClientStub(String moduleTypeString) throws RemoteException, NotBoundException {
        if (serverHostIP == null || serverPort == null || serverHostIP.equals("")) {
            try {
                serverHostIP = (InetAddress.getLocalHost()).getHostAddress();
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            }
            serverPort = 20202;
        }

        Registry clientRegistry = LocateRegistry.getRegistry(serverHostIP, serverPort);
        StubType stubType = StubType.valueOf(moduleTypeString);

        try {
            switch (stubType) {
                case LabelPrinStub:
                    labelPrin = (com.vanuston.medeil.implementation.LabelPrint) clientRegistry.lookup(RegistryConstants.LabelPrin);
                    return labelPrin;
                default:
                    // Add logger
                    log.debug("Stub type not found");
                    return null;
            }
        } catch (ConnectException e) {
           try {
                 serverHostIP = (InetAddress.getLocalHost()).getHostAddress();

            } catch (UnknownHostException ex) {
                log.debug("RegistryFactory getClientStub : "+ex.getMessage());
                ex.printStackTrace();
            }
        }
        return null;
    }
}
