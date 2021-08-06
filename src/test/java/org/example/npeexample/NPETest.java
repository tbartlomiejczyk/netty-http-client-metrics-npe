package org.example.npeexample;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import wiremock.org.apache.commons.lang3.RandomStringUtils;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {Application.class})
public class NPETest {

    public static final int WIREMOCK_STATIC_PORT = 8901;

    @LocalServerPort
    private int port;

    @Autowired
    private ApplicationContext context;

    protected WebTestClient webClient;

    @Rule
    public WireMockRule wm =
            new WireMockRule(WireMockConfiguration.options().port(WIREMOCK_STATIC_PORT));

    @Before
    public void init() {

        final String testServerUrl = "http://localhost:" + port;
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(testServerUrl);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        this.webClient = WebTestClient
                .bindToApplicationContext(context)
                .configureClient()
                .uriBuilderFactory(factory)
                .baseUrl(testServerUrl)
                .responseTimeout(Duration.ofMinutes(1))
                .build();

    }

    @After
    public void removeStubs() {
        removeAllMappings();
    }

    @Test
    public void test() {
        stubFor(post(urlEqualTo("/test/path"))
                .willReturn(aResponse()
                        .withStatus(431)
                ));

        final String bigHeader = RandomStringUtils.randomAlphanumeric(32000);

        webClient.post().uri("/test/path")
                .header("bigHeader", bigHeader)
                .exchange()
                .expectStatus().isEqualTo(431)
                .expectBody();
    }

}
