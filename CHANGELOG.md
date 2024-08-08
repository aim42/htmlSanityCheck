# HtmlSanityCheck - ChangeLog

## v2.0.0-rc0

### Publication

- [Maven Central](https://central.sonatype.com/namespace/org.aim42.htmlSanityCheck)

### Changes

#### Features

- [#322](https://github.com/aim42/htmlSanityCheck/issues/322) - Add Git properties
- [#321](https://github.com/aim42/htmlSanityCheck/issues/321) - Enable JitPack builds
- [#320](https://github.com/aim42/htmlSanityCheck/issues/320) - Add SonarQube (SonarCloud) analysis
- [#317](https://github.com/aim42/htmlSanityCheck/issues/317) - Add test coverage reports (aka. JaCoCo)
- [#315](https://github.com/aim42/htmlSanityCheck/issues/315) - Introduce dependabot to become aware of dependency updates
- [#312](https://github.com/aim42/htmlSanityCheck/issues/312) - Port HSC from Groovy to Java (in order to prepare [#314](https://github.com/aim42/htmlSanityCheck/issues/314), the proposed Maven plugin)
- [#309](https://github.com/aim42/htmlSanityCheck/issues/309) - Modularize htmlSanityChecker → gradle plugin + core (GA/BK/JT)

#### BugFixes

- [#323](https://github.com/aim42/htmlSanityCheck/issues/323) - Bump jquery due to (moderate) security warnings


### New Contributors
- [Gerd Aschemann](https://github.com/ascheman) (GA)
- [Björn Kasteleiner](https://github.com/bjkastel) (BK)
- [Thomas Ruhroth](https://github.com/truhroth) (TR)
- [Johannes Thorn](https://github.com/johthor) (JT)

And some others who participated in our
[CyberLand](https://cyberland.ijug.eu/) open source camps during the last months.

## v1.1.6
April 2021: GS, compiled with Java 8 - as I accidentally compiled V 1.1.4 with jdk 14... no other changes or fixes

## v1.1.4
April 2021: GS, merge [#283](https://github.com/aim42/htmlSanityCheck/issues/283), remove codenarc,
remove docToolChain-submodule

## v1.1.3.snapshot
Jan 2021: try to fix [#282](https://github.com/aim42/htmlSanityCheck/issues/282)

## v1.0.0-RC-2
Oct 2018: enhanced configurability, fixed several bugs, released version on Gradle plugin portal


## v0.9.8
July 2018: added CodeNarc V1.2, upgraded to Gradle 4.9, fixed travis build, improved footer of the HTML report


## v0.9.7
Dec 6th 2017: simplified gradle build, removed SonarQube, CodeNarc etc., added ArchUnit tests

May 3rd 2017: Fix broken logos in reports: [#149](https://github.com/aim42/htmlSanityCheck/issues/149)
May 3rd 2017: Clean up three logging statements.

## v0.9.6
Dec 13th 2016: Fix absolute local image check: [#130](https://github.com/aim42/htmlSanityCheck/issues/130)
Dec 12th 2016: Gradle plugin sends output to info log.
April 8th 2016: Add JUnit XML reporting to support automated tools.

## v0.9.3
June 14th 2015: published on Gradle Plugin Repository
May 14th 2015: reverted subproject structure, as it led to numerous build problems.

## v0.8.0
It could not be published on Bintray due to these issues.


## v0.8.0-SNAPSHOT
Restructured code into several Gradle subprojects.

- [#29](https://github.com/aim42/htmlSanityCheck/issues/29) - detect some more variants of URLs, e.g., IP-address prefixed etc.
- [#42](https://github.com/aim42/htmlSanityCheck/issues/42) - reference counter for "missing local ref checker"
- [#60](https://github.com/aim42/htmlSanityCheck/issues/60) - upgraded to jsoup parser v. 1.8.1 (from 1.7.3)
- [#62](https://github.com/aim42/htmlSanityCheck/issues/62) - upgraded to AsciiDoctor-Gradle-Plugin-1.5.2 (from 1.5.0)
- [#65](https://github.com/aim42/htmlSanityCheck/issues/65) - nicer message for reference-counter
- [#66](https://github.com/aim42/htmlSanityCheck/issues/66) - log messages now have appropriate levels (most are .debug now)
- [#68](https://github.com/aim42/htmlSanityCheck/issues/68) - corrected reference counter for broken cross-references (n occurrences are now counted as n issues)


# 0.5.3 (first public version)
Functional as Gradle-plugin
