package com.kynn.reevo_backend.common.ratelimit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, RequestInfo> requestCounts = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);
        if (rateLimit == null) {
            return true;
        }

        String clientIp = getClientIp(request);
        String key = clientIp + ":" + handlerMethod.getMethod().getName();
        long currentTime = System.currentTimeMillis();

        RequestInfo info = requestCounts.compute(key, (k, v) -> {
            if (v == null || currentTime - v.startTime > TimeUnit.SECONDS.toMillis(rateLimit.duration())) {
                return new RequestInfo(currentTime, 1);
            }
            return new RequestInfo(v.startTime, v.count + 1);
        });

        if (info.count > rateLimit.limit()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests. Please try again later.");
            return false;
        }

        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private record RequestInfo(long startTime, int count) {}
}
