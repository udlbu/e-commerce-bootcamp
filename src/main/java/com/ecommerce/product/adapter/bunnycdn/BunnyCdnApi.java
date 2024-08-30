package com.ecommerce.product.adapter.bunnycdn;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BunnyCdnApi {

    @PUT("{imageName}")
    @Headers({
            "Accept: application/json",
            "Content-Type: application/octet-stream"
    })
    Call<Void> uploadImage(@Header("AccessKey") String apiKey,
                           @Path("imageName") String imageName,
                           @Body RequestBody body);

}
