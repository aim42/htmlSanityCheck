package org.aim42.htmlsanitycheck.test.dns;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class CustomHostNameResolver {

    private static Object originalResolver;

    private static final List<String> hostnames = Arrays.asList(
            "www.amazon.com",
            "google.com",
            "junit.org",
            "plumelib.org",
            "people.csail.mit.edu",
            "arc42.org",
            "mock.codes"
    );

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
        if (hostnames.contains(hostname)) {
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