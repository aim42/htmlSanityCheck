package org.aim42.htmlsanitycheck.check;

import lombok.extern.slf4j.Slf4j;
import org.aim42.htmlsanitycheck.Configuration;
import org.aim42.htmlsanitycheck.collect.SingleCheckResults;
import org.aim42.htmlsanitycheck.html.HtmlElement;
import org.aim42.htmlsanitycheck.html.HtmlPage;
import org.aim42.htmlsanitycheck.tools.Web;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class MissingImageFilesChecker extends Checker {

    private static final Logger logger = LoggerFactory.getLogger(MissingImageFilesChecker.class);
    private List<HtmlElement> images;
    final File baseDir;
    private File currentDir;

    public MissingImageFilesChecker(Configuration pConfig) {
        super(pConfig);
        baseDir = getMyConfig().getSourceDir();
    }

    @Override
    protected void initCheckingResultsDescription() {
        getCheckingResults().setWhatIsChecked("Missing Local Images Check");
        getCheckingResults().setSourceItemName("img src attributes");
        getCheckingResults().setTargetItemName("missing image files");
    }

    @Override
    protected SingleCheckResults check(final HtmlPage pageToCheck) {
        log.trace("Checking '{}'", pageToCheck.getFile());
        final File file1 = pageToCheck.getFile();
        final File file = (file1 == null ? null : file1.getParentFile());
        currentDir = file != null ? file : baseDir;

        //get list of all image-tags "<img..." in html file
        images = pageToCheck.getAllImageTags();

        logger.debug("images to check: " + images);

        checkAllImages();

        return getCheckingResults();

    }

    private void checkAllImages() {

        images.forEach(this::checkSingleLocalImage);

    }

    private void checkSingleLocalImage(HtmlElement image) {
        String imageSrcAttribute = image.getImageSrcAttribute();

        // check only "local" image references
        // (that is, NO remote URL)
        boolean isRemoteURL = Web.isRemoteURL(imageSrcAttribute);
        boolean isDataURI = Web.isDataURI(imageSrcAttribute);
        if (!isRemoteURL) {
            if (isDataURI) {
                // bookkeeping:
                getCheckingResults().incNrOfChecks();

                doesDataURIContainData(imageSrcAttribute);

            } else {
                //we have a simple local image

                // bookkeeping:
                getCheckingResults().incNrOfChecks();

                doesImageFileExist(imageSrcAttribute);
            }
        }
    }

    /**
     * check if a single image file exists
     *
     * @param relativePathToImageFile == XYZ in <img src="XYZ">
     **/
    private void doesImageFileExist(String relativePathToImageFile) {
        File parentDir = relativePathToImageFile.startsWith("/") ? baseDir : currentDir;

        String decodedRelativePathtoImageFile;
        try {
            decodedRelativePathtoImageFile = URLDecoder.decode(relativePathToImageFile,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e); //NOSONAR(S112)
        }

        File imageFile = new File(parentDir, decodedRelativePathtoImageFile);

        if (!imageFile.exists() || imageFile.isDirectory()) {
            String findingText = "image \"" + relativePathToImageFile + "\" missing";
            getCheckingResults().newFinding(findingText);
        }


    }

    /**
     * check if the given data-URI contains actual data
     * <p>
     * Good: "data:image/png;base64,iVBORw0KGgoAAAANSU..."
     * <p>
     * Bad: "data:image/jpg;base64,"
     *
     * @param dataURI == XYZ in <img src="XYZ">
     **/
    private void doesDataURIContainData(String dataURI) {
        // let's do a simple regexp

        if (Pattern.matches("^data:image/[a-z]+;base64,", dataURI)) {
            String findingText = "data-URI image missing";
            getCheckingResults().newFinding(findingText);
        }

    }
}
