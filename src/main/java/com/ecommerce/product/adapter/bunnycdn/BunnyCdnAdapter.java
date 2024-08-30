package com.ecommerce.product.adapter.bunnycdn;

import com.ecommerce.product.config.CdnProperties;
import com.ecommerce.product.domain.model.Image;
import com.ecommerce.product.domain.model.ImageName;
import com.ecommerce.product.domain.port.CdnPort;
import io.micrometer.core.instrument.MeterRegistry;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Base64;
import java.util.Objects;

public class BunnyCdnAdapter implements CdnPort {
    private final static Logger LOG = LoggerFactory.getLogger(BunnyCdnAdapter.class);

    private final CdnProperties properties;

    private final MeterRegistry meterRegistry;

    public BunnyCdnAdapter(CdnProperties properties, MeterRegistry meterRegistry) {
        this.properties = properties;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void uploadImage(Image image, ImageName imageName) {
        byte[] binary = Base64.getDecoder().decode(image.val().substring(23));
        RequestBody body = RequestBody.create(binary, MediaType.parse("application/octet-stream"));
        try {
            Response<Void> response = buildApi().uploadImage(properties.getApiKey(), imageName.val(), body).execute();
            if (!response.isSuccessful()) {
                throw new ImageCdnUploadException("CDN responded with error <" + response.code() + ">");
            }
        } catch (Exception ex) {
            LOG.warn("Cannot upload image to CDN <" + imageName.val() + ">", ex);
            meterRegistry.counter("cdnUploadAnomaly").increment();
        }
    }

    @NotNull
    private BunnyCdnApi buildApi() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Objects.requireNonNull(HttpUrl.parse(properties.getHost() + "/" + properties.getRegion() + "/")))
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        return retrofit.create(BunnyCdnApi.class);
    }

    private static class ImageCdnUploadException extends RuntimeException {

        public ImageCdnUploadException(String message) {
            super(message);
        }
    }
}