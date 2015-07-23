package server;

import io.netty.handler.ssl.SslHandler;
import network.LoadBalancingSSLHandler;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * Created by d.budka on 2015-07-23.
 */
public class SSLFactory {
    public static SSLEngine getSSLEngine() throws Exception{

        KeyStore ks = KeyStore.getInstance("JKS");

        String pass = "password1";
        ks.load(new FileInputStream("log735"), pass.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, pass.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);
        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(false);

        sslEngine.setEnabledProtocols(sslEngine.getSupportedProtocols());
        sslEngine.setEnabledCipherSuites(sslEngine.getSupportedCipherSuites());
        sslEngine.setEnableSessionCreation(true);

        return sslEngine;
    }
}
