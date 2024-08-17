package org.aim42.htmlsanitycheck.cli

import java.security.Permission

class NoExitSecurityMock extends SecurityManager {
    private final SecurityManager originalSecurityManager

    int exitCalled = 0

    NoExitSecurityMock(SecurityManager originalSecurityManager) {
        this.originalSecurityManager = originalSecurityManager
    }

    @Override
    void checkPermission(Permission perm) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkPermission(perm)
        }
    }

    @Override
    void checkPermission(Permission perm, Object context) {
        if (originalSecurityManager != null) {
            originalSecurityManager.checkPermission(perm, context)
        }
    }

    @Override
    void checkExit(int status) {
        exitCalled++
        throw new SecurityException("System.exit(" + status + ") called")
    }
}