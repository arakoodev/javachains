package com.edgechain.lib.retrofit;

import com.edgechain.lib.embeddings.WordEmbeddings;
import com.edgechain.lib.endpoint.Endpoint;
import com.edgechain.lib.index.request.feign.RedisRequest;
import com.edgechain.lib.response.StringResponse;
import io.reactivex.rxjava3.core.Single;
import org.springframework.http.MediaType;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.HashMap;
import java.util.List;

public interface RedisService  {

  @POST(value = "index/redis/upsert")
  Single<StringResponse> upsert(@Body RedisRequest request);

  @POST(value = "index/redis/query")
  Single<List<WordEmbeddings>> query(@Body RedisRequest request);

  @HTTP(method = "DELETE", path = "index/redis/delete", hasBody = true)
  void deleteByPattern(@Query("pattern") String pattern, @Body Endpoint endpoint);

}