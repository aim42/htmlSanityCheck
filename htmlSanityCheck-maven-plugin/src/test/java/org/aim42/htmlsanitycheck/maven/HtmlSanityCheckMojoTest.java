package org.aim42.htmlsanitycheck.maven;

import org.aim42.htmlsanitycheck.Configuration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HtmlSanityCheckMojoTest {
    @Test
    void setupConfiguration() {
        HtmlSanityCheckMojo mojo = new HtmlSanityCheckMojo();
        Configuration config = mojo.setupConfiguration();
        Assertions.assertThat(config).isNotNull();
    }
}