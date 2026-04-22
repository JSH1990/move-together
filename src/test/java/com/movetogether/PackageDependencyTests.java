package com.movetogether;


import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

@AnalyzeClasses(packagesOf = App.class)
public class PackageDependencyTests {

    private static final String ACCOUNT = "..modules.account..";
    private static final String MAIN = "..modules.main..";

    @ArchTest
    ArchRule modulesPackageRule = classes().that().resideInAPackage("com.movetogether.modules..")
            .should().onlyBeAccessed().byClassesThat()
            .resideInAnyPackage("com.movetogether.modules..");


    @ArchTest
    ArchRule cycleCheck = slices().matching("com.movetogether.modules.(*)..")
            .should().beFreeOfCycles();
}
