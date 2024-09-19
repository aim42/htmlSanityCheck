package org.aim42.htmlsanitycheck.check;

import org.aim42.htmlsanitycheck.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory to create Checker instances
 */
public class CheckerCreator {
    private static final Logger logger = LoggerFactory.getLogger(CheckerCreator.class);

    private CheckerCreator() {
    }

    public static List<Checker> createCheckerClassesFrom(final List<Class<? extends Checker>> checkerClasses,
                                                         final Configuration configuration) {

        return checkerClasses.stream()
                .map(checkerClass -> CheckerCreator.createSingleChecker(checkerClass, configuration))
                .collect(Collectors.toList());
    }

    private static boolean isCase(Class<? extends Checker> caseValue, Class<? extends Checker> switchValue) {
        if (switchValue != null) {
            return caseValue.isAssignableFrom(switchValue);
        }
        return false;
    }

    public static Checker createSingleChecker(final Class<? extends Checker> checkerClass,
                                              final Configuration configuration) {
        if (null == checkerClass) {
            throw new UnknownCheckerException("Checker Class must not be 'null'");
        }

        Checker checker;
        // switch over all possible Checker classes
        // in case of new Checkers, this has to be adapted,
        // as Checker constructors will differ in minor details!

        // clearly violates the open-close principle

        if (isCase(BrokenCrossReferencesChecker.class, checkerClass)) {
            checker = new BrokenCrossReferencesChecker(configuration);
        } else if (isCase(BrokenHttpLinksChecker.class, checkerClass)) {
            checker = new BrokenHttpLinksChecker(configuration);
        } else if (isCase(DuplicateIdChecker.class, checkerClass)) {
            checker = new DuplicateIdChecker(configuration);
        } else if (isCase(ImageMapChecker.class, checkerClass)) {
            checker = new ImageMapChecker(configuration);
        } else if (isCase(MissingAltInImageTagsChecker.class, checkerClass)) {
            checker = new MissingAltInImageTagsChecker(configuration);
        } else if (isCase(MissingImageFilesChecker.class, checkerClass)) {
            checker = new MissingImageFilesChecker(configuration);
        } else if (isCase(MissingLocalResourcesChecker.class, checkerClass)) {
            checker = new MissingLocalResourcesChecker(configuration);
        } else {
            logger.error("unknown Checker {}", checkerClass);
            throw new UnknownCheckerException(checkerClass.toString());
        }

        return checker;

    }
}
