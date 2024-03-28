package org.aim42.net;

// created by https://www.geekality.net/2013/09/27/java-ignore-ssl-certificate-errors/

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public final class TrustAllCertificates implements X509TrustManager, HostnameVerifier
{
    public X509Certificate[] getAcceptedIssuers() {return new X509Certificate[0];}
    public void checkClientTrusted(X509Certificate[] certs, String authType) { //NOSONAR(S4830)
        /* As we do only read external content, we do not care much about SSL certs */
    }
    public void checkServerTrusted(X509Certificate[] certs, String authType) { //NOSONAR(S4830)
        /* As we do only read external content, we do not care much about SSL certs */
    }
    public boolean verify(String hostname, SSLSession session) {
        /* As we do only read external content, we do not care much about hostname equality */
        return !hostname.isEmpty();
    }

    /**
     * Installs a new {@link TrustAllCertificates} as trust manager and hostname verifier.
     */
    public static void install() {
        try {
            TrustAllCertificates trustAll = new TrustAllCertificates();

            // Install the all-trusting trust manager
            // Intentiaonally using "SSL" to make it work with a low security level
            SSLContext sc = SSLContext.getInstance("SSL"); //NOSONAR(S4424)
            sc.init(null,
                    new TrustManager[]{trustAll},
                    new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(trustAll);
        }
        catch (NoSuchAlgorithmException | KeyManagementException e)
        {
            throw new RuntimeException("Failed setting up all thrusting certificate manager.", e); //NOSONAR(S112)
        }

    }
}

/*
 * Copyright Gernot Starke and aim42 contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
