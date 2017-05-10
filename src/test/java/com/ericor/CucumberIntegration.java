package com.ericor;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

/**
 * Created by erickacorrales on 15/3/17.
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources",
                    plugin = {"pretty"})
public class CucumberIntegration {
}
