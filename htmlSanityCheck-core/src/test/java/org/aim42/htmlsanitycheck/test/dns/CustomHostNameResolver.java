package org.aim42.htmlsanitycheck.test.dns;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class CustomHostNameResolver {

    public static final String WIREMOCK_HOST = "my.custom.mocked.host";
    private static Object originalResolver;

    static {
        try {
            Field implField = InetAddress.class.getDeclaredField("impl");
            implField.setAccessible(true);
            originalResolver = implField.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup fallback DNS resolver", e);
        }
    }

    public InetAddress[] resolve(String hostname) throws UnknownHostException {
        // Custom DNS resolution logic
        if (WIREMOCK_HOST.equals(hostname)) {
            return new InetAddress[]{InetAddress.getByAddress("localhost", new byte[]{127, 0, 0, 1})};
        }
        // Fallback to original resolver using reflection
        try {
            return (InetAddress[]) originalResolver.getClass()
                    .getMethod("lookupAllHostAddr", String.class)
                    .invoke(originalResolver, hostname);
        } catch (Exception e) {
            throw new UnknownHostException("Failed to resolve hostname: " + hostname);
        }
    }
}