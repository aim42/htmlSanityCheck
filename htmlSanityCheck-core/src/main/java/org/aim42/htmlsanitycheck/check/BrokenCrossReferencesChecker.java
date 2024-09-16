package org.aim42.htmlsanitycheck.check;

import lombok.extern.slf4j.Slf4j;
import org.aim42.htmlsanitycheck.Configuration;
import org.aim42.htmlsanitycheck.collect.SingleCheckResults;
import org.aim42.htmlsanitycheck.html.HtmlPage;
import org.aim42.htmlsanitycheck.tools.Web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class BrokenCrossReferencesChecker extends SuggestingChecker {
    private List<String> listOfIds;
    private List<String> hrefList;
    private Set<String> hrefSet;

    public BrokenCrossReferencesChecker(Configuration pConfig) {
        super(pConfig);
    }

    @Override
    protected void initCheckingResultsDescription() {
        getCheckingResults().setWhatIsChecked("Broken Internal Links Check");
        getCheckingResults().setSourceItemName("href");
        getCheckingResults().setTargetItemName("missing id");
    }

    @Override
    protected void setValidPossibilities() {
        setValidPossibilities(listOfIds);
    }

    @Override
    protected SingleCheckResults check(final HtmlPage pageToCheck) {
        log.trace("Checking '{}'", pageToCheck.getFile());
        //get list of all a-tags "<a href=..." in html file
        hrefList = pageToCheck.getAllHrefStrings();
        hrefSet = new HashSet<>(hrefList);

        // get list of all id="XYZ"
        listOfIds = pageToCheck.getAllIdStrings();

        checkAllInternalLinks();

        return getCheckingResults();
    }

    /**
     * check all internal links against the existing id's
     */
    private void checkAllInternalLinks() {
        // for all hrefSet check if the corresponding id exists

        hrefSet.forEach(this::checkSingleInternalLink);

    }

    /**
     * check a single internal link (href) against the existing id's within
     * the html document
     */
    private void checkSingleInternalLink(String href) {
        getCheckingResults().incNrOfChecks();
        if (Web.containsInvalidChars(href)) {
            // we found link with illegal characters!
            String findingText = "link \"" + href + "\" contains illegal characters";
            // now count occurrences - how often is it referenced
            int nrOfReferences = countNrOfReferences(href);
            if (nrOfReferences > 1) {
                findingText += ", reference count: " + nrOfReferences;
            }

            getCheckingResults().newFinding(findingText, nrOfReferences);
        } else if (Web.isCrossReference(href)) {

            // bookkeeping:
            getCheckingResults().incNrOfChecks();

            doesLinkTargetExist(href);
        }

    }

    /**
     * check if the id for the href parameter exists
     *
     * @param href = "#XYZ" in id="XYZ"
     */
    private void doesLinkTargetExist(String href) {
        if (href.equals("#")) {
            return;
        }

        // strip href of its leading "#"
        String linkTarget = (href.startsWith("#")) ? href.substring(1) : href;
        // fragment can be URL-encoded
        try {
            linkTarget = URLDecoder.decode(linkTarget, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e); //NOSONAR(S112)
        }

        if (!listOfIds.contains(linkTarget)) {
            // we found a broken link!
            addBrokenLinkToResults(linkTarget, href);
        }

    }

    /**
     * bookkeeping the broken links that we found
     */
    private void addBrokenLinkToResults(String linkTarget, String href) {
        String findingText = "link target \"" + linkTarget + "\" missing";

        // now count occurrences - how often is it referenced
        int nrOfReferences = countNrOfReferences(href);
        if (nrOfReferences > 1) {
            findingText += ", reference count: " + nrOfReferences;
        }


        // determine suggestions "what could have been meant?"

        getCheckingResults().newFinding(findingText, nrOfReferences);
    }

    private int countNrOfReferences(String href) {
        return (int) hrefList.stream().filter(href::equals).count();
    }
}
