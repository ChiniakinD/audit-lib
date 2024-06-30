package org.ChiniakinD;

import org.ChiniakinD.exception.GlobalExceptionHandler;
import org.ChiniakinD.utils.LogOutputUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.WebRequest;

import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {

    private LogOutputUtil logOutputUtil;
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        logOutputUtil = Mockito.mock(LogOutputUtil.class);
        globalExceptionHandler = new GlobalExceptionHandler(logOutputUtil);
    }

    @Test
    public void testHandleAllExceptions() {
        Exception exception = new Exception("Exception");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("Test");
        globalExceptionHandler.handleAllExceptions(exception, request);
        verify(logOutputUtil, times(1)).saveLog(anyString());
    }
}