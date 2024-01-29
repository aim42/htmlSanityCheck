package org.aim42.htmlsanitycheck.check;

import org.aim42.htmlsanitycheck.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * abstract factory to create Checker instances
 */
public class CheckerCreator {
    private static final Logger logger = LoggerFactory.getLogger(CheckerCreator.class);

    public static Set<Checker> createCheckerClassesFrom(final Collection<Class<? extends Checker>> checkerClasses, final Configuration pConfig) {

        return checkerClasses.stream()
                .map(checkerClass -> CheckerCreator.createSingleChecker(checkerClass, pConfig))
                .collect(Collectors.toSet());
    }

    private static boolean isCase(Class<? extends Checker> caseValue, Class<? extends Checker> switchValue) {
        if (switchValue != null) {
            return caseValue.isAssignableFrom(switchValue);
        }
        return false;
    }

    public static Checker createSingleChecker(final Class<? extends Checker> checkerClass, final Configuration pConfig) {
        Checker checker;

        // switch over all possible Checker classes
        // in case of new Checkers, this has to be adapted,
        // as Checker constructors will differ in minor details!

        // clearly violates the open-close principle

        if (isCase(BrokenCrossReferencesChecker.class, checkerClass)) {
            checker = new BrokenCrossReferencesChecker(pConfig);
        } else if (isCase(BrokenHttpLinksChecker.class, checkerClass)) {
            checker = new BrokenHttpLinksChecker(pConfig);
        } else if (isCase(DuplicateIdChecker.class, checkerClass)) {
            checker = new DuplicateIdChecker(pConfig);
        } else if (isCase(ImageMapChecker.class, checkerClass)) {
            checker = new ImageMapChecker(pConfig);
        } else if (isCase(MissingAltInImageTagsChecker.class, checkerClass)) {
            checker = new MissingAltInImageTagsChecker(pConfig);
        } else if (isCase(MissingImageFilesChecker.class, checkerClass)) {
            checker = new MissingImageFilesChecker(pConfig);
        } else if (isCase(MissingLocalResourcesChecker.class, checkerClass)) {
            checker = new MissingLocalResourcesChecker(pConfig);
        } else {
            logger.warn("unknown Checker " + checkerClass.toString());
            throw new UnknownCheckerException(checkerClass.toString());
        }

        return checker;

    }
}
