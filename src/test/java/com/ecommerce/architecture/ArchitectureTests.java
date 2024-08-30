package com.ecommerce.architecture;

import com.ecommerce.AppRunner;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaParameter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@AnalyzeClasses(packagesOf = AppRunner.class, importOptions = {ImportOption.DoNotIncludeArchives.class, ImportOption.DoNotIncludeTests.class})
public class ArchitectureTests {

    // endpoint rules
    @ArchTest
    public void endpointClassesShouldEndWithSuffixEndpoint(JavaClasses cls) {
        classes().that()
                .areAnnotatedWith(RestController.class)
                .should()
                .haveSimpleNameEndingWith("Endpoint")
                .check(cls);
    }

    @ArchTest
    public void endpointClassesShouldResideInApiPackage(JavaClasses cls) {
        classes().that()
                .areAnnotatedWith(RestController.class)
                .should()
                .resideInAPackage("..api..")
                .check(cls);
    }

    @ArchTest
    public void methodParametersAnnotatedWithRequestBodyShouldContainRequestSuffixInClassName(JavaClasses cls) {
        methods().that()
                .areAnnotatedWith(PutMapping.class)
                .or().areAnnotatedWith(PostMapping.class)
                .should(new ArchCondition<>("") {
                    @Override
                    public void check(JavaMethod javaMethod, ConditionEvents conditionEvents) {
                        if (hasRequestBodyAndHasNotRequestSuffix(javaMethod)) {
                            conditionEvents.add(SimpleConditionEvent.violated(javaMethod,
                                    javaMethod.getFullName() + " contains @RequestBody params without 'Request' suffix"));
                        }
                    }

                    private boolean hasRequestBodyAndHasNotRequestSuffix(JavaMethod javaMethod) {
                        for (JavaParameter parameter : javaMethod.getParameters()) {
                            if (parameter.isAnnotatedWith(RequestBody.class)
                                    && !parameter.getRawType().getName().endsWith("Request")) {
                                return true;
                            }
                        }
                        return false;
                    }
                })
                .allowEmptyShould(true)
                .check(cls);
    }

    // packaging rules
    @ArchTest
    public void interfacesWhichArePortsShouldResideInPackagePort(JavaClasses cls) {
        ArchRuleDefinition.classes()
                .that()
                .areInterfaces()
                .and()
                .haveSimpleNameEndingWith("Port")
                .should()
                .resideInAPackage("..port..")
                .check(cls);
    }

    @ArchTest
    public void domainClassesShouldNotBeDependentOnFramework(JavaClasses cls) {
        ArchRuleDefinition.noClasses()
                .that()
                .resideInAPackage("..domain..")
                .should()
                .beAnnotatedWith(Service.class)
                .orShould()
                .beAnnotatedWith(Component.class)
                .orShould()
                .beAnnotatedWith(Repository.class)
                .orShould()
                .beAnnotatedWith(Bean.class)
                .check(cls);
    }
}
