package org.aim42.htmlsanitycheck.check;

import org.aim42.htmlsanitycheck.Configuration;
import org.aim42.htmlsanitycheck.collect.Finding;
import org.aim42.htmlsanitycheck.collect.SingleCheckResults;
import org.aim42.htmlsanitycheck.html.HtmlElement;
import org.aim42.htmlsanitycheck.html.HtmlPage;
import org.aim42.htmlsanitycheck.tools.Web;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * principal checks on imageMap usage:
 * <p>
 * 1.) for every usemap-reference there is one map
 * 2.) every map is referenced by at least one image
 * 3.) every every map name is unique
 * 4.) every area-tag has one non-empty href attribute
 * 5.) every href points to valid target (broken-links check)
 * <p>
 * see also: http://www.w3schools.com/tags/tag_map.asp
 **/
public class ImageMapChecker extends Checker {
    private List<HtmlElement> maps;
    private List<String> mapNames;
    private List<HtmlElement> imagesWithUsemapRefs;
    private List<String> usemapRefs;
    private List<String> listOfIds;
    private String findingText;
    private HtmlPage pageToCheck;


    public ImageMapChecker(Configuration pConfig) {
        super(pConfig);
    }

    @Override
    protected void initCheckingResultsDescription() {
        getCheckingResults().setWhatIsChecked("Consistency of ImageMaps");
        getCheckingResults().setSourceItemName("imageMap");
        getCheckingResults().setTargetItemName("map/area and usemap-references");
    }

    @Override
    protected SingleCheckResults check(final HtmlPage pageToCheck) {

        this.pageToCheck = pageToCheck;

        readImageMapAttributesFromHtml();

        checkBrokenImageMapReferences();

        checkDuplicateMapNames();

        checkDanglingMaps();

        checkEmptyMaps();

        checkForBrokenHrefLinks();// the major check

        return getCheckingResults();
    }

    private void checkDanglingMaps() {
        mapNames.stream()
                .filter(n -> !usemapRefs.contains(n))
                .map(mapName -> "ImageMap \"" + mapName + "\" not referenced by any image.")
                .forEach(findingText -> getCheckingResults().addFinding(new Finding(findingText)));
    }

    private void checkEmptyMaps() {
        mapNames.stream().map(mapName ->
                        pageToCheck.getAllAreasForMapName(mapName))
                .filter(areas -> !areas.isEmpty())
                .peek(a -> getCheckingResults().incNrOfChecks())
                .forEach(area -> getCheckingResults().addFinding(new Finding(findingText)));
    }

    /*
check for duplicate map names
 */
    private void checkDuplicateMapNames() {
        int mapNameCount;

        Set<String> mapNameSet = new HashSet<>(mapNames);

        mapNameSet.stream()
                .peek(a -> getCheckingResults().incNrOfChecks())
                .filter(name -> mapNames.stream().filter(name2 -> name2.equals(name)).count() > 1)
                .forEach(mapName ->
                        getCheckingResults().addFinding(
                                new Finding(mapNames.stream().filter(name2 -> name2.equals(mapName)).count() + " imagemaps with identical name \"" + mapName + "\" exist.")));


    }

    /*
     * <img src="x" usemap="y">...
     * a.) if there is no map named "y" -> problem
     * b.) if there are more maps named "y" -> problem
     */
    private void checkBrokenImageMapReferences() {
        imagesWithUsemapRefs.stream()
                .forEach(imageTag -> checkBrokenImageMapReference(imageTag.getUsemapRef(), imageTag));
    }

    private void checkBrokenImageMapReference(String imgMap, HtmlElement imageTag) {
        getCheckingResults().incNrOfChecks();


        long mapCount = mapNames.stream().filter(it -> it == imgMap).count();

        if (mapCount == 0L) {
            // no map found, despite img-tag usemap-reference
            findingText = "ImageMap \"" + imageTag.getUsemapRef() + "\" (referenced by image \"" + imageTag.getImageSrcAttribute() + "\") missing.";
            getCheckingResults().addFinding(new Finding(findingText));
        }
    }

    private void checkForBrokenHrefLinks() {

        mapNames.forEach(n -> checkAreaHrefsForMapName(n));
    }

    /*
    for a specific mapName, check all its contained  areaHrefs
     */
    private void checkAreaHrefsForMapName(String mapName) {
        List<String> areaHrefs = pageToCheck.getAllHrefsForMapName(mapName);

        // if this List is empty -> the map is empty
        // TODO replace checkEmptyMaps with additional check here

        areaHrefs.stream()
                .peek(a -> getCheckingResults().incNrOfChecks())
                .filter(href -> Web.isCrossReference(href))
                .forEach(href -> checkLocalHref(href, mapName, areaHrefs));

    }

    /*
check if href has valid local target
TODO: currently restricted to LOCAL references
TODO: remove duplication to BrokenCrossReferencesChecker
*/
    private void checkLocalHref(String href, String mapName, List<String> areaHrefs) {
        // strip href of its leading "#"
        String linkTarget = (href.startsWith("#")) ? href.substring(1) : href;


        if (!listOfIds.contains(linkTarget)) {

            // we found a broken link!
            findingText = "ImageMap \"" + mapName + "\" refers to missing link \"" + linkTarget + "\"";

            // now count occurrences - how often is it referenced
            int nrOfReferences = (int) areaHrefs.stream().filter(it -> it == href).count();
            if (nrOfReferences > 1) {
                findingText += ", reference count: " + nrOfReferences + ".";
            } else findingText += ".";

            getCheckingResults().newFinding(findingText, nrOfReferences);
        }

    }

    private void readImageMapAttributesFromHtml() {
        // get all <img src="x" usemap="y">
        imagesWithUsemapRefs = pageToCheck.getImagesWithUsemapDeclaration();

        // get all <map name="z">...
        maps = pageToCheck.getAllImageMaps();

        // get the names of all maps
        mapNames = pageToCheck.getAllMapNames();

        // get all referenced maps from image tags with usemap-attribute
        usemapRefs = pageToCheck.getAllUsemapRefs();

        // list of all id="XYZ"
        listOfIds = pageToCheck.getAllIdStrings();

    }
}
