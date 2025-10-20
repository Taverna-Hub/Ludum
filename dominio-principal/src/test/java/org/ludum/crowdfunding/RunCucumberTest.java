package org.ludum.crowdfunding;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/org/ludum/crowdfunding", // Pasta onde está seu .feature
    glue = "org.ludum.crowdfunding",                       // Pacote onde estarão seus Step Definitions
    plugin = {"pretty", "html:target/cucumber-reports.html"} // Gera um relatório de teste em HTML
)
public class RunCucumberTest {}
