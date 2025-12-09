package org.ludum.dominio;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.core.options.Constants.FEATURES_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("org.ludum.dominio")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.ludum.dominio.financeiro," +
                                                          "org.ludum.dominio.comunidade," +
                                                          "org.ludum.dominio.identidade," +
                                                          "org.ludum.dominio.catalogo," +
                                                          "org.ludum.dominio.crowdfunding," +
                                                          "org.ludum.dominio.oficina")
@ConfigurationParameter(key = FEATURES_PROPERTY_NAME, value = "src/test/resources/org/ludum/financeiro," +
                                                              "src/test/resources/org/ludum/comunidade," +
                                                              "src/test/resources/org/ludum/identidade," +
                                                              "src/test/resources/org/ludum/catalogo," +
                                                              "src/test/resources/org/ludum/crowdfunding," +
                                                              "src/test/resources/org/ludum/oficina")
public class RunCucumberTest {
}