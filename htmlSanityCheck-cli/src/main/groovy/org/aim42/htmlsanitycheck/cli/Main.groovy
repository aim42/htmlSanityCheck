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

//    @Option(names = ["-h", "--help"], usageHelp = true, description = "Display help message")
//    void help () {
//        logger.info("""SYNOPSIS: htmlSanityCheck
//                "-s sourceDir
//                "[ -f sourceFile ]+
//                "-r resultsDir""")
//    }

    @Option(names = ["-r", "--resultsDir"], description = "Results Directory")
    String resultsDirectoryName = "/tmp/results"

    @Parameters(arity = "1..*", description = "at least one File")
    File[] files

    static void main(String[] args) {
        Main app = new Main()
        CommandLine cmd = new CommandLine(app)
        cmd.execute(args)
    }

    private List<File> findFiles(File directory) throws IOException {
        List<File> files = new ArrayList<>()
        Files.walk(Paths.get(directory.getPath()))
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().endsWith(".html"))
                .forEach(files::add)
        return files
    }

    void run() {
        var configuration = new Configuration()

        var resultsDirectory = new File(resultsDirectoryName)

        configuration.addConfigurationItem(Configuration.ITEM_NAME_sourceDocuments,
                files.collect { file ->
                    if (file.isDirectory()) {
                        return findFiles(file)
                    } else {
                        return new File(file)
                    }
                }
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