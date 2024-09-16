package org.aim42.htmlsanitycheck.check;

import lombok.extern.slf4j.Slf4j;
import org.aim42.htmlsanitycheck.Configuration;
import org.aim42.htmlsanitycheck.collect.SingleCheckResults;
import org.aim42.htmlsanitycheck.html.HtmlElement;
import org.aim42.htmlsanitycheck.html.HtmlPage;

/**
 * checks for missing or empty alt-attributes in image tags.
 */
@Slf4j
public class MissingAltInImageTagsChecker extends Checker {
    public MissingAltInImageTagsChecker(Configuration pConfig) {
        super(pConfig);
    }

    @Override
    protected void initCheckingResultsDescription() {
        getCheckingResults().setWhatIsChecked("Missing alt-attribute declaration in image tags");
        getCheckingResults().setSourceItemName("image tags");
        getCheckingResults().setTargetItemName("missing alt attributes");
    }


    @Override
    protected SingleCheckResults check(final HtmlPage pageToCheck) {
        log.trace("Checking '{}'", pageToCheck.getFile());
        // the number of checks is calculated by counting
        // ALL image tags:
        getCheckingResults().setNrOfChecks(pageToCheck.getAllImageTags().size());

        // see HtmlPageSpec for behavior: missing or empty alt-attributes are included...
        pageToCheck.getAllImageTagsWithMissingAltAttribute().stream()
                .forEach(element -> reportSingleImageTagWithMissingAlt(element));


        return getCheckingResults();
    }

    private void reportSingleImageTagWithMissingAlt(HtmlElement element) {

        String imageName = element.getImageSrcAttribute();

        String findingText = "image \"" + imageName + "\" is missing alt-attribute";

        getCheckingResults().newFinding(findingText);

    }

}
