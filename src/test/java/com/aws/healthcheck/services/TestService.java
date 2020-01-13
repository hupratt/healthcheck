package com.aws.healthcheck.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.aws.healthcheck.dto.Resource;
import com.aws.healthcheck.dto.Response;
import com.aws.healthcheck.util.Sleep;
import com.aws.healthcheck.util.StringManipulation;
import com.aws.healthcheck.web.AppExceptionController;
import com.aws.healthcheck.web.MainController;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TestService {

        @Autowired
        private HealthService healthService;

        @Autowired
        private FileHandlingService fileHandlingService;

        @Autowired
        private MainController mainController;

        @Autowired
        private AppExceptionController exceptionController;

        @LocalServerPort
        private int port;

        @Autowired
        private RestTemplate restTemplate;

        @Autowired
        private RestService fakeRestService;

        @Value("${comm.api.url}")
        private String myurl;

        @Value("${comm.fileHandling.path}")
        private String csvOutputPath;

        MockRestServiceServer mockServer;
        ClientHttpRequestFactory originalRequestFactory;

        // Smoke test to check that spring loads the controller
        @Test
        public void contextLoads() throws Exception {
                assertThat(mainController).isNotNull();
                assertThat(exceptionController).isNotNull();
        }

        @Test
        public void testgetRestCall200() throws IOException {
                final Response<Resource> staticTestData = FileHandlingService.readJsonWithObjectMapper();
                // rest template is bound to our new mock server and will reply to us in the
                // following way
                // getRestCall will trigger a REST get call
                // and receive a json that we have frozen into a static file called "first.json"
                try {
                        originalRequestFactory = restTemplate.getRequestFactory();
                        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
                        mockServer.reset();
                        mockServer.expect(ExpectedCount.between(1, 10),
                                        requestTo(myurl + HealthService.URL_SUFFIX))
                                        .andExpect(method(HttpMethod.GET))
                                        .andRespond(MockRestResponseCreators.withSuccess(
                                                        StringManipulation.responseToString(staticTestData),
                                                        MediaType.APPLICATION_JSON));
                        final Resource mock = healthService.getRestCall().getEmbedded()
                                        .getItem().stream().collect(Collectors.toList()).get(0);

                        final String Response = "{" + "\"_embedded\" :{\"item\": [{" + " \"Id\": \"AACANCELFEE\","
                                        + " \"Description\": \"AA Contract Cancellation Fee\","
                                        + " \"ShortDescr\" : \"AA Cancel Fee\"," + " \"CategoryAccount\" : \"52211\","
                                        + " \"TxnCodeCr\" : \"813\"," + " \"TxnCodeDr\" : \"812\"" + "}" + "]" + "}"
                                        + "}";

                        final ObjectMapper ResponseMapper = new ObjectMapper();
                        final Response<Resource> Response_inst = ResponseMapper.reader()
                                        .forType(Response.class).readValue(Response);
                        final List<Resource> Response_list = Response_inst.getEmbedded().getItem();

                        // Jackson deserialized into a Hashmap that we then convert into a list of
                        // response types
                        final ObjectMapper mapper = new ObjectMapper();
                        final List<Resource> Response_testdata = mapper.convertValue(Response_list,
                                        new TypeReference<List<Resource>>() {
                                        });
                        final Resource Response_ = Response_testdata.get(0);
                        assertEquals(Response_, mock);
                        mockServer.verify();
                } finally {
                        restTemplate.setRequestFactory(originalRequestFactory);
                }
        }

        @Test
        public void testgetRestCall400handling() throws IOException {
                try {
                        originalRequestFactory = restTemplate.getRequestFactory();
                        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
                        mockServer.reset();
                        final MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate).build();

                        mockServer.expect(ExpectedCount.between(1, 10),
                                        requestTo(myurl + HealthService.URL_SUFFIX))
                                        .andExpect(method(HttpMethod.GET))
                                        .andRespond(MockRestResponseCreators.withStatus(HttpStatus.BAD_REQUEST));
                        final String httpResponse = fakeRestService.getMessage();

                        final String Response = "{\"status\":400,\"message\": \"Bad Request\"}";
                        log.debug(httpResponse);
                        log.debug(Response);
                        assertEquals(Response, httpResponse);
                        mockServer.verify();
                } finally {
                        restTemplate.setRequestFactory(originalRequestFactory);
                }

        }

        @Test
        public void testgetRestCall401handling() throws IOException {
                try {
                        originalRequestFactory = restTemplate.getRequestFactory();
                        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
                        mockServer.reset();
                        final MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate).build();

                        mockServer.expect(ExpectedCount.between(1, 10),
                                        requestTo(myurl + HealthService.URL_SUFFIX))
                                        .andExpect(method(HttpMethod.GET))
                                        .andRespond(MockRestResponseCreators.withStatus(HttpStatus.UNAUTHORIZED));
                        final String httpResponse = fakeRestService.getMessage();

                        final String Response = "{\"status\":401,\"message\": \"Unauthorized\"}";
                        log.debug(httpResponse);
                        log.debug(Response);
                        assertEquals(Response, httpResponse);
                        mockServer.verify();
                } finally {
                        restTemplate.setRequestFactory(originalRequestFactory);
                }

        }

        @Test
        public void testgetRestCall403handling() throws IOException {
                try {
                        originalRequestFactory = restTemplate.getRequestFactory();
                        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
                        mockServer.reset();
                        final MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate).build();

                        mockServer.expect(ExpectedCount.between(1, 10),
                                        requestTo(myurl + HealthService.URL_SUFFIX))
                                        .andExpect(method(HttpMethod.GET))
                                        .andRespond(MockRestResponseCreators.withStatus(HttpStatus.FORBIDDEN));
                        final String httpResponse = fakeRestService.getMessage();

                        final String Response = "{\"status\":403,\"message\": \"Forbidden\"}";
                        log.debug(httpResponse);
                        log.debug(Response);
                        assertEquals(Response, httpResponse);
                        mockServer.verify();
                } finally {
                        restTemplate.setRequestFactory(originalRequestFactory);
                }

        }

        @Test
        public void testgetRestCall404handling() throws IOException {
                try {
                        originalRequestFactory = restTemplate.getRequestFactory();
                        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
                        mockServer.reset();
                        final MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate).build();

                        mockServer.expect(ExpectedCount.between(1, 10),
                                        requestTo(myurl + HealthService.URL_SUFFIX))
                                        .andExpect(method(HttpMethod.GET))
                                        .andRespond(MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND));
                        final String httpResponse = fakeRestService.getMessage();

                        final String Response = "{\"status\":404,\"message\": \"Not Found\"}";
                        log.debug(httpResponse);
                        assertEquals(Response, httpResponse);
                        mockServer.verify();
                } finally {
                        restTemplate.setRequestFactory(originalRequestFactory);
                }

        }

        @Test
        public void testgetRestCall500handling() throws IOException {
                try {
                        originalRequestFactory = restTemplate.getRequestFactory();
                        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
                        mockServer.reset();
                        final MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate).build();

                        mockServer.expect(ExpectedCount.between(1, 10),
                                        requestTo(myurl + HealthService.URL_SUFFIX))
                                        .andExpect(method(HttpMethod.GET))
                                        .andRespond(MockRestResponseCreators.withServerError());
                        final String httpResponse = fakeRestService.getMessage();

                        final String Response = "{\"status\":500,\"message\": \"Internal Server Error\"}";
                        log.debug(httpResponse);
                        assertEquals(Response, httpResponse);
                        mockServer.verify();
                } finally {
                        restTemplate.setRequestFactory(originalRequestFactory);
                }

        }

        @Test
        public void testDataWasWrittenInTargetFileAndHasData() throws InterruptedException, IOException {

                Long fileLastModifiedBefore = new Long(0);
                Long fileLastModifiedAfter = new Long(0);
                // grab from yaml instead
                String csvOutputPath = "s.csv";
                List<Integer> csvExportOutput = new ArrayList<>();
                final int numberOfLines = 3;

                // delete if file exists
                FileHandlingService.deleteFile(csvOutputPath);
                // trigger the controller @ /posts
                restTemplate.getForEntity(String.format("http://localhost:%d/posts", port), String.class);
                // wait for the file to be written on the filesystem
                Sleep.main();

                // test that it generated a csv with at the very list three lines in it and
                // whose last modified date is different from null
                try {
                        csvExportOutput = fileHandlingService.fetchFirstThreeLines(csvExportOutput, numberOfLines,
                                        csvOutputPath);

                        fileLastModifiedAfter = fileHandlingService.fileLastModified(fileLastModifiedBefore,
                                        csvOutputPath);

                } catch (final IOException e) {
                        log.error("File did not get downloaded or has < 3 lines of data inside {}", e);
                        Assert.fail("Does not meet one of two requirements of the test: file was downloaded or file >=3 lines of data inside");
                }
                assertTrue(csvExportOutput.get(2) != -1);
                assertTrue(fileLastModifiedBefore != fileLastModifiedAfter);
        }
}
