package com.ecommerce.infrastructure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.HashMap;
import java.util.Map;

public class CustomHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, String> headers = new HashMap<>();

    public CustomHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        String header = super.getHeader(name);
        if (header != null) {
            return header;
        }
        return headers.get(name);
    }

    public void addHeader(String headerName, String value) {
        headers.put(headerName, value);
    }
}