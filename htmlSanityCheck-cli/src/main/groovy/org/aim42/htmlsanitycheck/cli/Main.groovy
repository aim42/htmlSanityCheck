package org.aim42.htmlsanitycheck.cli

import org.aim42.htmlsanitycheck.AllChecksRunner
import org.aim42.htmlsanitycheck.Configuration
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.Parameters
import picocli.CommandLine.Command
import picocli.CommandLine.Option

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

// see end-of-file for license information

@Command(name = "hsc", mixinStandardHelpOptions = true, version = "hsc 2.0.0",
        description = "Check HTML files for Sanity")
class Main implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Main.class)

    MainRunner runner

    Main() {
        Main(new MainRunner())
    }

    Main(MainRunner runner) {
        this.runner = runner
    }

    @Option(names = ["-r", "--resultsDir"], description = "Results Directory")
    String resultsDirectoryName = "/tmp/results"

    @Parameters(arity = "1", description = "base directory", index = "0")
    File srcDir

    @Parameters(arity = "0..*", description = "at least one File", index = "1..*")
    File[] files

    static void main(String[] args) {
        MainRunner runner = new MainRunner()
        Main app = new Main(runner)
        CommandLine cmd = new CommandLine(app)
        runner.setMain(app)
        runner.setCmd(cmd)
        cmd.execute(args)
    }

    private static List<File> findFiles(File directory) throws IOException {
        Files.walk(Paths.get(directory.getPath()))
                .filter(Files::isRegularFile)
                .filter({ path -> path.toString().endsWith(".html") || path.toString().endsWith(".htm")})
                .collect { it.toFile() }
    }

    void run() {
        var configuration = new Configuration()

        var resultsDirectory = new File(resultsDirectoryName)

        configuration.addConfigurationItem(Configuration.ITEM_NAME_sourceDir, srcDir)
        configuration.addConfigurationItem(Configuration.ITEM_NAME_sourceDocuments,
                files ?: findFiles(srcDir)
        )
        configuration.addConfigurationItem((Configuration.ITEM_NAME_checkingResultsDir), resultsDirectory)

            if (configuration.isValid()) {
                // create output directory for checking results
                resultsDirectory.mkdirs()

                // create an AllChecksRunner...
                var allChecksRunner = new AllChecksRunner(configuration)

                // ... and perform the actual checks
                var allChecks = allChecksRunner.performAllChecks()

                // check for findings and fail build if requested
                var nrOfFindingsOnAllPages = allChecks.nrOfFindingsOnAllPages()
                logger.debug("Found ${nrOfFindingsOnAllPages} error(s) on all checked pages")
            }
        }
    }

    void run() {
        runner.run()
    }
}

/*========================================================================
 Copyright Gerd Aschemann and aim42 contributors

 Licensed under the Apache License, Version 2.0 (the "License")
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an
 "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ========================================================================*/
