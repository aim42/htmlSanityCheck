package org.aim42.htmlSanityCheck.it;

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.MavenGoal;

import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension
public class MavenIT {


    @MavenTest
    @MavenGoal("compile")
    void sunshine(MavenExecutionResult result) {
        assertThat(result).isSuccessful();
    }


    @MavenTest
    @MavenGoal("compile")
    void rain(MavenExecutionResult result) {
        assertThat(result).isSuccessful();
        assertThat(result).out().info().contains("2 missing image files found.");
        assertThat(result).out().info().contains("image \"./images/missing-image-urjk8ybepw8.jpg\" missing");
        assertThat(result).out().info().contains("data-URI image missing");
        assertThat(result).out().info().contains("id \"some-anchor\" has 2 definitions.");
        assertThat(result).out().info().contains("link target \"24mia\" missing");
        assertThat(result).out().info().contains("link target \"Cross-References\" missing");
    }

}
