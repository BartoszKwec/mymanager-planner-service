package pl.kwec.mymanagerplannerservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserIdAuthFilter - doFilterInternal")
class UserIdAuthFilterTest {

    private UserIdAuthFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new UserIdAuthFilter();
    }

    @Test
    @DisplayName("should extract valid userId from X-User-Id header and set as attribute")
    void shouldExtractValidUserIdFromHeader() throws ServletException, IOException {
        when(request.getHeader("X-User-Id")).thenReturn("123");

        filter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute("userId", 123L);
        verify(filterChain).doFilter(request, response);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "100", "999999", "9223372036854775807"})
    @DisplayName("should accept all valid positive user IDs")
    void shouldAcceptValidPositiveUserIds(String userId) throws ServletException, IOException {
        when(request.getHeader("X-User-Id")).thenReturn(userId);

        filter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute(eq("userId"), eq(Long.parseLong(userId)));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("should continue filter chain when X-User-Id header is not present")
    void shouldContinueFilterChainWhenHeaderNotPresent() throws ServletException, IOException {
        when(request.getHeader("X-User-Id")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("should return 400 BAD_REQUEST when X-User-Id header contains non-numeric value")
    void shouldReturn400WhenHeaderIsNotNumeric() throws ServletException, IOException {
        when(request.getHeader("X-User-Id")).thenReturn("not-a-number");

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "12.34", "1L", "", "12 ", " 12"})
    @DisplayName("should return 400 BAD_REQUEST for various invalid user ID formats")
    void shouldReturn400ForInvalidFormats(String invalidUserId) throws ServletException, IOException {
        when(request.getHeader("X-User-Id")).thenReturn(invalidUserId);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("should not continue filter chain when X-User-Id header is invalid")
    void shouldNotContinueFilterChainWhenHeaderIsInvalid() throws ServletException, IOException {
        when(request.getHeader("X-User-Id")).thenReturn("invalid");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
    }
}
