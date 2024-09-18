package org.aim42.htmlsanitycheck.check;

import lombok.extern.slf4j.Slf4j;
import org.aim42.htmlsanitycheck.Configuration;
import org.aim42.htmlsanitycheck.collect.SingleCheckResults;
import org.aim42.htmlsanitycheck.html.HtmlPage;
import org.aim42.htmlsanitycheck.tools.InvalidUriSyntaxException;
import org.aim42.htmlsanitycheck.tools.Web;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class MissingLocalResourcesChecker extends Checker {
    public static final String MLRC_MESSAGE_PREFIX = "local resource";
    public static final String MLRC_MESSAGE_MISSING = "missing";
    public static final String MLRC_REFCOUNT = ", reference count: ";
    private static final Logger logger = LoggerFactory.getLogger(MissingLocalResourcesChecker.class);
    // NOTE that we need, both the full list as well as the unique set of resources
    // List of the local resources referenced in anchor tags
    private List<String> localResourcesList;
    /**
     * The base directory to resolve absolute paths.
     */
    private final File baseDir;
    /**
     * The current directory, obtained from the HtmlPage, to resolve
     * relative paths.
     */
    private File currentDir;

    public MissingLocalResourcesChecker(Configuration pConfig) {
        super(pConfig);
        baseDir = pConfig.getSourceDir();
    }

    @Override
    protected void initCheckingResultsDescription() {
        getCheckingResults().setWhatIsChecked("Missing Local Resources Check");
        getCheckingResults().setSourceItemName("anchor tag href attribute");
        getCheckingResults().setTargetItemName("missing local resources");
    }

    @Override
    protected SingleCheckResults check(final HtmlPage pageToCheck) {
        log.trace("Checking '{}'", pageToCheck.getFile());
        //get list of all anchor-tags containing href="xyz" in html file
        List<String> allHrefs = pageToCheck.getAllHrefStrings();

        // now filter out all local resources
        localResourcesList = allHrefs.stream().filter(Web::isLocalResource).collect(Collectors.toList());

        // now filter out all local resources
        // unique local references - each one is unique
        // created from the List of all by toSet() method
        Set<String> localResourcesSet = new HashSet<>(localResourcesList);

        logger.debug("local resources set: {}", localResourcesSet);

        final File file1 = pageToCheck.getFile();
        final File file = (file1 == null ? null : file1.getParentFile());
        currentDir = file != null ? file : baseDir;

        // perform the actual checks
        checkAllLocalResources(localResourcesSet);

        return getCheckingResults();

    }

    private void checkAllLocalResources(Set<String> localResources) {

        localResources.forEach(this::checkSingleLocalResource);
    }

    private void checkSingleLocalResource(String localResource) {
        // the localResource is either path+filename  or filename or directory

        logger.debug("single resource to be checked: {}", localResource);

        // bookkeeping:
        getCheckingResults().incNrOfChecks();

        // we need to strip the localResource of #anchor-parts
        String localResourcePath;
        try {
            localResourcePath = new URI(localResource).getPath();
        } catch (URISyntaxException e) {
            throw new InvalidUriSyntaxException(e);
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
        int nrOfOccurrences = (int) localResourcesList.stream().filter(et -> et.equals(nonExistingLocalResource)).count();

        if (nrOfOccurrences > 1) {
            findingText += MLRC_REFCOUNT + nrOfOccurrences;
        }

        // add Finding to our current checking results, increment nrOfFindings by nrOfOccurrences
        getCheckingResults().newFinding(findingText, nrOfOccurrences);
    }
}
