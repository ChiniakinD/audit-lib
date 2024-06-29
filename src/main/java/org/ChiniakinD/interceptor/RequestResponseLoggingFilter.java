package org.ChiniakinD.interceptor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.ChiniakinD.model.HttpRequestAspect;
import org.ChiniakinD.model.HttpResponseAspect;
import org.ChiniakinD.utils.LogOutputUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Класс который фильтрует запросы и ответы.
 *
 * @author ChiniakinD
 */
@Component
@RequiredArgsConstructor
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private final LogOutputUtil logOutputUtil;

    /**
     * Метод который фильтрует запросы и ответы и логирует их.
     *
     * @param request     запрос.
     * @param response    ответ.
     * @param filterChain цепочка фильтров
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        String requestBody = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
        logOutputUtil.setHttpRequestAspect(new HttpRequestAspect(wrappedRequest.getMethod(), wrappedRequest.getRequestURI(), requestBody));

        String responseBody = new String(wrappedResponse.getContentAsByteArray(), StandardCharsets.UTF_8);
        logOutputUtil.setHttpResponseAspect(new HttpResponseAspect(wrappedResponse.getStatus(), responseBody));

        String logMessage = logOutputUtil.createHttpRequestLogMessage();
        logOutputUtil.saveLog(logMessage);
    }

}
