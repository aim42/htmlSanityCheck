package org.aim42.htmlsanitycheck

// see end-of-file for license information


class MisconfigurationException extends Exception {


    public MisconfigurationException( String message, File srcDir) {
        super( message + ": " + srcDir.canonicalPath )
    }

    public MisconfigurationException( String message ) {
        super(message)
    }
}
