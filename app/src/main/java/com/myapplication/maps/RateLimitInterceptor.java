package com.myapplication.maps;


import com.revinate.guava.util.concurrent.RateLimiter;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;


public class RateLimitInterceptor implements Interceptor {
    private RateLimiter limiter = RateLimiter.create(3);

    @Override
    public Response intercept(Chain chain) throws IOException {
        limiter.acquire(1);
        return chain.proceed(chain.request());
    }
}