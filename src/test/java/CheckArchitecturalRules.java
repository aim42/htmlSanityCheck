
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.Before;
import org.junit.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class CheckArchitecturalRules {


    private final ClassFileImporter importer = new ClassFileImporter();

    private JavaClasses classes;

    @Before
    public void importClasses() {
        classes = importer.importPackages("org.aim42");
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

    //@Test
    public void interfaces_should_not_have_names_ending_with_the_word_interface() {

        noClasses().that().areInterfaces()
                .should().haveNameMatching(".*Interface")
                .check(classes);
    }


    //@Test
    public void checkers_should_extend_checker() {
        classes().that().haveNameMatching(".*Checker").should().implement("Checker").check(classes);
    }

    @Test
    public void only_suggester_should_call_string_similiarity_library() {
        noClasses().that().dontHaveSimpleName("Suggester")
                .and().haveNameNotMatching("..Test")
                .and().resideInAPackage( "net.ricecode")
                .should().accessClassesThat()
                .resideInAPackage("..similarity..")
                .check(classes);
        //.haveSimpleNameContaining("StringSimilarityService").check(classes);
    }
}


/*=============================================================
 Copyright Gernot Starke and aim42 contributors

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
