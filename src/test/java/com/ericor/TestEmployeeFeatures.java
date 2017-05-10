package com.ericor;

import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.CoreMatchers.containsString;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by erickacorrales on 22/3/17.
 */
public class TestEmployeeFeatures {

    private final WireMockServer wireMockServer = new WireMockServer();
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    private static final String APPLICATION_JSON = "application/json";
    private final InputStream jsonInputStream = this.getClass().getClassLoader().getResourceAsStream("employees.json");
    private final String jsonString = new Scanner(jsonInputStream, "UTF-8").useDelimiter("\\Z").next();
    private HttpResponse httpResponse = null;

    @When("^the client calls /employees$")
    public void the_client_calls_employees() throws Throwable {

        wireMockServer.start();

        configureFor("localhost", 8080);
        stubFor(get(urlEqualTo("/employees"))
                    .withHeader("accept", equalTo(APPLICATION_JSON))
                    .willReturn(aResponse()
                                    .withBody(jsonString)
                                    .withStatus(200)));

        HttpGet request = new HttpGet("http://localhost:8080/employees");
        request.addHeader("accept", APPLICATION_JSON);
        httpResponse = httpClient.execute(request);

        verify(getRequestedFor(urlEqualTo("/employees"))
                    .withHeader("accept", equalTo(APPLICATION_JSON))
        );

        wireMockServer.stop();
    }

    @Then("^the client receives status code of (\\d+)$")
    public void the_client_receives_status_code_of(int statusCode) throws Throwable {
        int currentStatusCode  = httpResponse.getStatusLine().getStatusCode();
        assertThat("status code is incorrect : ",currentStatusCode, is(statusCode));
    }

    @Then("^the response should contain data:$")
    public void the_response_should_contain_data(String data) throws Throwable {
        String responseString = convertResponseToString(httpResponse);
        assertThat(responseString.trim(), containsString(data));
    }


    @When("^the client request POST \"([^\"]*)\" with json data$")
    public void the_client_request_POST_with_json_data(String resourceUri, String dataPost) throws Throwable {

        wireMockServer.start();

        configureFor("localhost", 8080);
        stubFor(post(urlEqualTo(resourceUri))
                .withHeader("content-type", equalTo(APPLICATION_JSON))
                .willReturn(aResponse().withStatus(201)));

        HttpPost request = new HttpPost("http://localhost:8080" + resourceUri);
        StringEntity entity = new StringEntity(dataPost);
        request.addHeader("content-type", APPLICATION_JSON);
        request.setEntity(entity);
        httpResponse = httpClient.execute(request);

        verify(postRequestedFor(urlEqualTo(resourceUri))
                .withHeader("content-type", equalTo(APPLICATION_JSON)));

        wireMockServer.stop();

    }

    private String convertResponseToString(HttpResponse response) throws IOException {
        InputStream responseStream = response.getEntity().getContent();
        Scanner scanner = new Scanner(responseStream, "UTF-8");
        String responseString = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return responseString;
    }
}
