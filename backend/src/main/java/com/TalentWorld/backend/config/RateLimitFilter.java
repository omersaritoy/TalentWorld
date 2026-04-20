package com.TalentWorld.backend.config;


import com.TalentWorld.backend.service.impl.RateLimitService2;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService2 rateLimitService2;

    public RateLimitFilter(RateLimitService2 rateLimitService2) {
        this.rateLimitService2 = rateLimitService2;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String clientIp=getClientIp(request);

        Bucket tokenBucket=rateLimitService2.resolveBucket(clientIp);

        var probe=tokenBucket.tryConsumeAndReturnRemaining(1);

        if(probe.isConsumed()){
            response.addHeader("X-Rate-Limit-Remaining",String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request,response);
        }
        else {
            var waitForRefill= probe.getNanosToWaitForRefill()/1_000_000_000;

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

            response.addHeader("X-Rate-Limit-Retry-After-Seconds",String.valueOf(waitForRefill));
            response.setContentType("application/json");

            String jsonResponse= """
                    {
                        "status":"%s",
                        "error":"TOO_MANY_REQUESTS",
                        "message":"You Have exhausted your API Request Quota",
                        "retryAfterSeconds":"%s"
                    
                    }""".formatted(HttpStatus.TOO_MANY_REQUESTS.value(),waitForRefill);
            response.getWriter().write(jsonResponse);

        }

    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty()) {
            return request.getRemoteAddr();
        }
        return ip.split(",")[0].trim();
    }
}
