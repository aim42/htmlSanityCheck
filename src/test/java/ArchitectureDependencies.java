
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitectureDependencies {


    private final ClassFileImporter importer = new ClassFileImporter();

    private JavaClasses classes;

    @Before
    public void importClasses() {
        classes = importer.importClasspath(); // imports all classes from the classpath that are not from JARs
    }

    @Test
    public void collect_should_not_access_suggest() {
        ArchRule rule = noClasses().that().resideInAPackage("..collect..")
                .should().accessClassesThat().resideInAPackage("..suggest.."); // The '..' represents a wildcard for any number of packages

        rule.check(classes);
    }

    @Test
    public void check_can_access_report() {
        ArchRule rule = classes().that().resideInAnyPackage( "check")
                .should().accessClassesThat().resideInAnyPackage( "report");
    }

    @Test
    public void interfaces_should_not_have_names_ending_with_the_word_interface() {

        noClasses().that().areInterfaces().should().haveNameMatching(".*Interface").check(classes);
    }

}


/*=============================================================
 Copyright 2014 Gernot Starke and aim42 contributors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an
 "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 =============================================================*/
