package com.ecommerce.product.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cdn")
@Data
public class CdnProperties {
    private Boolean enabled;
    private String host;
    private String region;
    private String apiKey;
    private String imgUrl;

    public String getImgUrl(HttpServletRequest request) {
        return enabled ? imgUrl : "http://" + request.getServerName() + ":" + request.getLocalPort() + "/mock/";
    }
}