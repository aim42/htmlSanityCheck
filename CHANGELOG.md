# ChangeLog

## Publication (Generic)

- [Documentation](https://hsc.aim42.org)
- [Maven Central](https://central.sonatype.com/namespace/org.aim42.htmlSanityCheck)
- [Gradle Plugin Portal](https://plugins.gradle.org/search?term=org.aim42.htmlSanityCheck)

## 2.0.0-rc4

#### Improvements (2.0.0-rc4)

- [#276](https://github.com/aim42/htmlSanityCheck/issues/276) - Exclude Hosts/URLs by RegExp
- [#367](https://github.com/aim42/htmlSanityCheck/issues/367) - Add SDKMAN distribution
- [#390](https://github.com/aim42/htmlSanityCheck/issues/390) - Check directory links for index file(s)
- [#391](https://github.com/aim42/htmlSanityCheck/issues/391) - Check Remote URLs only once

#### Version Bumps (2.0.0-rc4)

- AssertJ
- Develocity
- Gradle
- Gradle ToolChain Resolver
- Gradle Versions Plugin
- JReleaser
- JUnit / Jupiter
- JaCoCo Reporter GH Action
- Lombok
- Mockito
- Slf4j
- TestContainers

#### Misc. Changes (2.0.0-rc4)

- [#343](https://github.com/aim42/htmlSanityCheck/issues/343)
    - Increase test coverage
    - Add Maven sticker
    - Check for new GH Actions by DependaBot
    - Fix landing page
    - Fix typos + grammar
    - Clean up variable names

### New Contributors (2.0.0-rc4)

- [Eleonor Bengtsson](https://github.com/elebeida) (EB)

## 2.0.0-rc3

#### Improvements (2.0.0-rc3)

- [#314](https://github.com/aim42/htmlSanityCheck/issues/314) - Implement Maven plugin
- [#318](https://github.com/aim42/htmlSanityCheck/issues/318) - Implement Command Line Interface (CLI)

#### Version Bumps (2.0.0-rc3)

- TestContainers
- JSoup
- Lombok

## 2.0.0-rc2

### Changes (2.0.0-rc2)

#### Improvements (2.0.0-rc2)

- [#330](https://github.com/aim42/htmlSanityCheck/issues/330) - Replace incorporated `net.ricecode` components
- [#331](https://github.com/aim42/htmlSanityCheck/issues/331) - Use mocks for remote/integration testing

#### Version Bumps (2.0.0-rc2)

- Gradle Wrapper
- Junit5
- WireMock and TestContainers
- JReleaser

#### Misc. Changes (2.0.0-rc2)

- Drop ignored (flaky) tests (thanks again to RC)
- Refer to Apache license directly from ASF site (due to flaky HSC checks of former location)
- Fix typos, simplify the language, and align with technical writer conventions of the ChangeLog and other documentation
- Fix generating the ChangeLog.md file (header) for the documentation

### New Contributors (2.0.0-rc2)

- [Rehan Chalana](https://github.com/RehanChalana) (RC)
- [Prankur Tiwari](https://github.com/Prankurtiwari) (PT)

## 2.0.0-rc1

### Publication (2.0.0-rc1)

- [Documentation](https://hsc.aim42.org)
- [Maven Central](https://central.sonatype.com/namespace/org.aim42.htmlSanityCheck)
- [Gradle Plugin Portal](https://plugins.gradle.org/search?term=org.aim42.htmlSanityCheck)

### Changes (2.0.0-rc1)

#### Features (2.0.0-rc1)

- Add [Develocity](https://scans.gradle.com/) build scans
- Add [JReleaser](https://jreleaser.org/)
- Improve Signing (for Maven Central)
- [#343](https://github.com/aim42/htmlSanityCheck/issues/343) - Clean up and improve code and docs
- Misc. dependency/plugin version bumps
- Execute the full integration test with misc. Gradle version only on CI (to improve local build/test speed)
- Reduce empty lines from console reporting
- Move Gradle classes to Gradle subdirectory and improve type safety in plugin implementation
- Ensure that self-check always uses the latest build
- Enable upload to Gradle Plugin Portal
- [#258](https://github.com/aim42/htmlSanityCheck/issues/258) - Change to new (replaced) hsc.aim42.org documentation page
- Add Mastodon announcement (incl. credentials) via JReleaser
- Consequently, rename 'HTML Sanity Checker' → 'HTML Sanity Check'
- Set GitHub specific admonition icons in README files
- [#332](https://github.com/aim42/htmlSanityCheck/issues/332) - Update and fix documentation
- Derive the Groovy version from implicit (Gradle) dependency
- Add GPG agent configuration
- Release to Maven Central via JReleaser
- Unify utility classes (and tests) for Web and URLs

#### BugFixes (2.0.0-rc1)

- Avoid NPE in config initialization
- Replace an outdated/missing link from [#185](https://github.com/aim42/htmlSanityCheck/issues/185)
- Fix/Update badges and contained links
- Search MavenCentral first for dependencies to avoid strange Gradle errors with Maven local repository
- [#153](https://github.com/aim42/htmlSanityCheck/issues/153) - Rollback HTTP redirect codes to bbc210fb and ignore 'javascript:' URLs
- Fix Gradle URLs
- Fix image dir for GitHub subdirectory README
- Fix GitHub representation of AsciiDoc in README files
- Avoid JPMS warnings for AsciiDoctor task

### New Contributors (2.0.0-rc1)

- [Sandra Parsick](https://github.com/sparsick) (SP)

## 2.0.0-rc0

### Publication (2.0.0-rc0)

- [Maven Central](https://central.sonatype.com/namespace/org.aim42.htmlSanityCheck)

### Changes (2.0.0-rc0)

#### Features (2.0.0-rc0)

- [#322](https://github.com/aim42/htmlSanityCheck/issues/322) - Add Git properties
- [#321](https://github.com/aim42/htmlSanityCheck/issues/321) - Enable JitPack builds
- [#320](https://github.com/aim42/htmlSanityCheck/issues/320) - Add SonarQube (SonarCloud) analysis
- [#317](https://github.com/aim42/htmlSanityCheck/issues/317) - Add test coverage reports (aka. JaCoCo)
- [#315](https://github.com/aim42/htmlSanityCheck/issues/315) - Introduce dependabot to become aware of dependency updates
- [#312](https://github.com/aim42/htmlSanityCheck/issues/312) - Port HSC from Groovy to Java (to prepare [#314](https://github.com/aim42/htmlSanityCheck/issues/314), the proposed Maven plugin)
- [#309](https://github.com/aim42/htmlSanityCheck/issues/309) - Modularize htmlSanityChecker → gradle plugin + core (GA/BK/JT)

#### BugFixes (2.0.0-rc0)

- [#323](https://github.com/aim42/htmlSanityCheck/issues/323) - Bump jquery due to (moderate) security warnings


### New Contributors (2.0.0-rc0)
- [Gerd Aschemann](https://github.com/ascheman) (GA)
- [Björn Kasteleiner](https://github.com/bjkastel) (BK)
- [Thomas Ruhroth](https://github.com/truhroth) (TR)
- [Johannes Thorn](https://github.com/johthor) (JT)

And some others who participated in our
[CyberLand](https://cyberland.ijug.eu/) open source camps during the last months.

## 1.1.6
April 2021: GS, compiled with Java 8 - as I accidentally compiled V 1.1.4 with jdk 14... no other changes or fixes

## 1.1.4
April 2021: GS, merge [#283](https://github.com/aim42/htmlSanityCheck/issues/283), remove codenarc,
remove docToolChain-submodule

## 1.1.3.snapshot
Jan 2021: try to fix [#282](https://github.com/aim42/htmlSanityCheck/issues/282)

## 1.0.0-RC-2
Oct 2018: enhanced configurability, fixed several bugs, released version on Gradle plugin portal


## 0.9.8
July 2018: added CodeNarc V1.2, upgraded to Gradle 4.9, fixed travis build, improved footer of the HTML report


## 0.9.7
Dec 6th 2017: simplified gradle build, removed SonarQube, CodeNarc etc., added ArchUnit tests

May 3rd 2017: Fix broken logos in reports: [#149](https://github.com/aim42/htmlSanityCheck/issues/149)
May 3rd 2017: Clean up three logging statements.

## 0.9.6
Dec 13th 2016: Fix absolute local image check: [#130](https://github.com/aim42/htmlSanityCheck/issues/130)
Dec 12th 2016: Gradle plugin sends output to info log.
April 8th 2016: Add JUnit XML reporting to support automated tools.

## 0.9.3
June 14th 2015: published on Gradle Plugin Repository
May 14th 2015: reverted subproject structure, as it led to many build problems.

## 0.8.0
It could not be published on Bintray due to these issues.


## 0.8.0-SNAPSHOT
Restructured code into several Gradle subprojects.

- [#29](https://github.com/aim42/htmlSanityCheck/issues/29) - detect some more variants of URLs, e.g., IP-address prefixed etc.
- [#42](https://github.com/aim42/htmlSanityCheck/issues/42) - reference counter for "missing local ref checker"
- [#60](https://github.com/aim42/htmlSanityCheck/issues/60) - upgraded to jsoup parser v. 1.8.1 (from 1.7.3)
- [#62](https://github.com/aim42/htmlSanityCheck/issues/62) - upgraded to AsciiDoctor-Gradle-Plugin-1.5.2 (from 1.5.0)
- [#65](https://github.com/aim42/htmlSanityCheck/issues/65) - nicer message for reference-counter
- [#66](https://github.com/aim42/htmlSanityCheck/issues/66) - log messages now have appropriate levels (most are .debug now)
- [#68](https://github.com/aim42/htmlSanityCheck/issues/68) - corrected reference counter for broken cross-references (n occurrences are now counted as n issues)


## 0.5.3 (first public version)
Functional as Gradle-plugin
