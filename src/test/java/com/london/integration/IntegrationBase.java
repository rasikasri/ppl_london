package com.london.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.london.LondonPeopleApplication;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.request;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Slf4j
@SpringBootTest(classes = {LondonPeopleApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {
    "local",
    "ci"
})
public abstract class IntegrationBase {
    private static String RESPONSE_PATH = "/response/";
    private static String LOCAL_SERVER = "http://localhost:";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    WireMockServer mockServer;

    @BeforeEach
    void setup () {
        mockServer = new WireMockServer(8081);
        mockServer.start();
    }

    @AfterEach
    void teardown () {
        mockServer.stop();
    }

    void mockResponse(String path, String body, int status, RequestMethod method) {
        mockServer.stubFor(
            request(method.toString(), urlPathEqualTo(path))
                .willReturn(aResponse()
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                    .withStatus(status)
                    .withBody(body)));
    }

    String getResponseResources(String filename) {
        String resource = null;
        try {
            resource = IOUtils.toString(IntegrationBase.class.getResourceAsStream(RESPONSE_PATH + filename));
        } catch (IOException e) {
            log.error("Error in reading response file");
        }
        return resource;
    }

    ResponseEntity<String> sendRequestToServer(String path) {
        return restTemplate.getForEntity( LOCAL_SERVER + port + path, String.class);
    }
}
