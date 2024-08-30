package com.ecommerce;

import com.ecommerce.product.config.CdnProperties;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder.responseDefinition;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathTemplate;

public class CdnAbility {

    private final CdnProperties properties;
    private final WireMockClassRule wireMockServer;

    public CdnAbility(CdnProperties properties, WireMockClassRule wireMockServer) {
        this.properties = properties;
        this.wireMockServer = wireMockServer;
    }

    public void stubUploadImageReturnSuccess() {
        wireMockServer.
                stubFor(put(urlPathTemplate("/" + properties.getRegion() + "/{imageName}"))
                .willReturn(responseDefinition().withStatus(HttpStatus.OK.value())));
    }

    public void verifyUploadImageWasCalledOnce() {
        wireMockServer.
                verify(1, putRequestedFor(urlPathTemplate("/" + properties.getRegion() + "/{imageName}")));
    }

    public void verifyUploadImageWasNotCalled() {
        wireMockServer.
                verify(0, putRequestedFor(urlPathTemplate("/" + properties.getRegion() + "/{imageName}")));
    }

    public void reset() {
        wireMockServer.resetAll();
    }
}
