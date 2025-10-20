package org.ludum.oficina.mod;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/org/ludum/oficina/mod",
    glue = "org.ludum.oficina.mod",
    plugin = {"pretty", "html:target/cucumber-reports.html"}
)
public class RunCubumberTest {}
