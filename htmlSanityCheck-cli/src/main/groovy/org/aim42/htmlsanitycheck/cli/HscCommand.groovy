package org.aim42.htmlsanitycheck.cli

import org.aim42.htmlsanitycheck.AllChecksRunner
import org.aim42.htmlsanitycheck.Configuration
import org.aim42.htmlsanitycheck.ProductInformation
import org.aim42.htmlsanitycheck.check.AllCheckers
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import picocli.CommandLine.Parameters

import java.nio.file.Files
import java.nio.file.Paths
import java.util.logging.Formatter
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.LogRecord
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

// see end-of-file for license information

@Command(name = "hsc", mixinStandardHelpOptions = true,
        description = "Check HTML files for Sanity",
        showDefaultValues = true
)
class HscCommand implements Runnable {
    private static final Logger rootLogger
    private static ConsoleHandler consoleHandler

    private static final Logger logger

    private static final String SHORT_FORMAT = "%1\$s%n"
    private static final String LEVEL_FORMAT = "%2\$-7s - %1\$s%n"
    private static final String LEVEL_CLASS_FORMAT = "%2\$-7s - %3\$s - %1\$s%n"

    private static void setLogFormatter(final String format) {
        Formatter formatter = new SimpleFormatter() {

            @Override
            synchronized String format(LogRecord lr) {
                return String.format(format,
                        lr.getMessage(),
                        lr.getLevel().getLocalizedName(),
                        String.format("%-30s", lr.getSourceClassName())
                )
            }
        }
        consoleHandler.setFormatter(formatter)
    }

    static {
        LogManager logManager = LogManager.getLogManager()
        logManager.reset()

        rootLogger = Logger.getLogger("org.aim42.htmlsanitycheck")
        logger = Logger.getLogger(HscCommand.class.name)

        rootLogger.setLevel(Level.WARNING)
        consoleHandler = new ConsoleHandler()

        setLogFormatter(SHORT_FORMAT)

        rootLogger.addHandler(consoleHandler)
    }

    HscRunner runner

    protected HscCommand(HscRunner runner) {
        this.runner = runner
    }

    @Option(names = ["-v", "--verbose"],
            description = "Increase verbosity. Repeatable, cumulative, i.e., -v, -vv, -vvv, or -vvvv",
            fallbackValue = "true", arity = "0..3")
    private boolean[] verbosity

    @Option(names = ["-V", "--version"], description = "Display version information")
    boolean versionRequested

    @Option(names = ["-r", "--resultsDir"], description = "Results Directory")
    String resultsDirectoryName = "/tmp/results"

    @Option(names = ["-s", "--suffix"], description = "File name suffixes to investigate", split = ",")
    String[] suffixes = ["html", "htm"]

    @Parameters(index = "0", arity = "0..1", description = "base directory (default: current directory)")
    File srcDir = new File(".").getAbsoluteFile()

    @Parameters(index = "1..*",
            arity = "0..*",
            description = "files to investigate (default: all files beyond <srcDir> with <suffixes>)"
    )
    File[] srcDocs

    static void main(String[] args) {
        HscRunner hscRunner = new HscRunner()
        HscCommand hscCommand = new HscCommand(hscRunner)
        CommandLine cmd = new CommandLine(hscCommand)
        hscRunner.setHscCommand(hscCommand)
        hscRunner.setCmd(cmd)
        cmd.execute(args)
    }

    private List<File> findFiles() throws IOException {
        Files.walk(Paths.get(srcDir.getPath()))
                .filter(Files::isRegularFile)
                .filter({ path ->
                    suffixes.any { suffix -> path.toString().endsWith(".${suffix}") }
                })
                .collect { it.toFile() }
    }

    static class HscRunner {

        HscCommand hscCommand
        CommandLine cmd

        private static void setLogLevel(final Level level) {
            rootLogger.setLevel(level)
            consoleHandler.setLevel(level)
        }

        private void configureLogging() {
            if (hscCommand.verbosity != null) {
                int verbosityLevel = hscCommand.verbosity.length
                switch (verbosityLevel) {
                    case 0:
                        // Default logging level
                        break
                    case 1:
                        setLogLevel(Level.INFO)
                        break
                    case 2:
                        setLogLevel(Level.FINE)
                        setLogFormatter(LEVEL_FORMAT)
                        break
                    case 3:
                        setLogLevel(Level.FINER)
                        setLogFormatter(LEVEL_CLASS_FORMAT)
                        break
                    case 4:
                        setLogLevel(Level.FINEST)
                        setLogFormatter(LEVEL_CLASS_FORMAT)
                        break
                }
            }
        }

        void run() {
            if (hscCommand.versionRequested) {
                System.out.println("Version: ${ProductInformation.VERSION}")
                return
            }

            configureLogging()
            def srcDocuments = hscCommand.srcDocs ?: hscCommand.findFiles()
            if (!srcDocuments) {
                System.err.println("Please specify at least one src document (either explicitly or implicitly)")
                cmd.usage(System.out)
                System.exit(1)
            }

            var resultsDirectory = new File(hscCommand.resultsDirectoryName)
            var configuration = Configuration.builder()
                    .sourceDir(hscCommand.srcDir)
                    .sourceDocuments(srcDocuments as Set)
                    .checkingResultsDir(resultsDirectory)
                    .checksToExecute(AllCheckers.CHECKER_CLASSES)
                    .build()

            // if we have no valid configuration, abort with exception
            configuration.validate()
            logger.fine("Starting HTML sanity check")
            // create output directory for checking results
            resultsDirectory.mkdirs()

            // create an AllChecksRunner...
            var allChecksRunner = new AllChecksRunner(configuration)

            // ... and perform the actual checks
            var allChecks = allChecksRunner.performAllChecks(false)

            // check for findings and fail build if requested
            var nrOfFindingsOnAllPages = allChecks.nrOfFindingsOnAllPages()
            logger.fine("Found ${nrOfFindingsOnAllPages} error(s) on all checked pages")
            logger.finer("Finishing HTML sanity check")
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
