package com.ecommerce.shared.config;

import com.github.tomakehurst.wiremock.extension.ResponseTransformerV2;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;

public class WireMockConnectionCloseExtension implements ResponseTransformerV2 {
    @Override
    public Response transform(Response response, ServeEvent serveEvent) {
        return Response.Builder
                .like(response)
                .headers(HttpHeaders.copyOf(response.getHeaders()).plus(new HttpHeader("Connection", "Close")))
                .build();
    }

    @Override
    public String getName() {
        return "ConnectionCloseExtension";
    }
}
