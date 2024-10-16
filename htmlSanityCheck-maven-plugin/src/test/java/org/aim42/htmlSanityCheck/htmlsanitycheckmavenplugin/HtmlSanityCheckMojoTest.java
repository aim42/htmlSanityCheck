package org.aim42.htmlSanityCheck.htmlsanitycheckmavenplugin;

import org.junit.jupiter.api.Test;
import org.aim42.htmlsanitycheck.Configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class HtmlSanityCheckMojoTest {


    @Test
    void setupConfiguration() {
        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();
        Configuration config = mojo.setupConfiguration();
        assertTrue(config != null);

    }
}