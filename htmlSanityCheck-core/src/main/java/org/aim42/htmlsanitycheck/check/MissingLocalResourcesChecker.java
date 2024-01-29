package org.aim42.htmlsanitycheck.check;

import org.aim42.htmlsanitycheck.Configuration;
import org.aim42.htmlsanitycheck.collect.SingleCheckResults;
import org.aim42.htmlsanitycheck.html.HtmlPage;
import org.aim42.htmlsanitycheck.tools.Web;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MissingLocalResourcesChecker extends Checker {
    public static final String MLRC_MESSAGE_PREFIX = "local resource";
    public static final String MLRC_MESSAGE_MISSING = "missing";
    public static final String MLRC_REFCOUNT = ", reference count: ";
    private static final Logger logger = LoggerFactory.getLogger(MissingLocalResourcesChecker.class);
    private Set<String> localResourcesSet;
    /**
     * The base directory to resolve absolute paths.
     */
    private final File baseDir;
    /**
     * The current directory, obtained from the HtmlPage, to resolve
     * relative paths.
     */
    private File currentDir;
    /**
     * True to require files to be referenced and not directories. Useful if the web server doesn't
     * support a default directory, such as Amazon S3.
     */
    private final boolean requireFiles = false;
    public MissingLocalResourcesChecker(Configuration pConfig) {
        super(pConfig);
        baseDir = ((File) (pConfig.getConfigItemByName(Configuration.getITEM_NAME_sourceDir())));
    }

    @Override
    protected void initCheckingResultsDescription() {
        getCheckingResults().setWhatIsChecked("Missing Local Resources Check");
        getCheckingResults().setSourceItemName("anchor tag href attribute");
        getCheckingResults().setTargetItemName("missing local resources");
    }

    @Override
    protected SingleCheckResults check(final HtmlPage pageToCheck) {
        //get list of all anchor-tags containing href="xyz" in html file
        List<String> allHrefs = pageToCheck.getAllHrefStrings();

        // now filter out all local resources

        // now filter out all local resources
        localResourcesSet = allHrefs.stream().filter(Web::isLocalResource).collect(Collectors.toSet());

        logger.debug("local resources set: " + localResourcesSet);

        final File file1 = pageToCheck.getFile();
        final File file = (file1 == null ? null : file1.getParentFile());
        currentDir = file != null ? file : baseDir;

        // perform the actual checks
        checkAllLocalResources(localResourcesSet);

        return getCheckingResults();

    }

    private void checkAllLocalResources(Set<String> localResources) {

        localResources.forEach(localResource -> checkSingleLocalResource(localResource));
    }

    private void checkSingleLocalResource(String localResource) {
        // the localResource is either path+filename  or filename or directory

        logger.debug("single resource to be checked: + " + localResource);

        // bookkeeping:
        getCheckingResults().incNrOfChecks();

        // we need to strip the localResource of #anchor-parts
        String localResourcePath = null;
        try {
            localResourcePath = new URI(localResource).getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        if (localResourcePath == null) {
            // For example, javascript:;
            return;

        }


        File parentDir = localResourcePath.startsWith("/") ? baseDir : currentDir;

        // we need the baseDir for robust checking of local resources...
        File localFile = new File(parentDir, localResourcePath);

        // action required if resource does not exist
        if (!localFile.exists() || !localFile.isFile()) {
            handleNonexistingLocalResource(localResource);
        }

    }

    private void handleNonexistingLocalResource(final String nonExistingLocalResource) {
        String findingText = MLRC_MESSAGE_PREFIX + " \"" + nonExistingLocalResource + "\" " + MLRC_MESSAGE_MISSING;

        // how often is localResource referenced?
        int nrOfOccurrences = (int) localResourcesSet.stream().filter(et -> et.equals(nonExistingLocalResource)).count();

        if (nrOfOccurrences > 1) findingText += MLRC_REFCOUNT + nrOfOccurrences;

        // add Finding to our current checking results, increment nrOfFindings by nrOfOccurrences
        getCheckingResults().newFinding(findingText, nrOfOccurrences);
    }
}
