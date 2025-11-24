package pl.kwec.mymanagerplannerservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserIdAuthFilter extends OncePerRequestFilter {

    public static final String USER_ID_ATTRIBUTE = "userId";

    @Override
    protected void doFilterInternal(final HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null) {
            try {
                Long userId = Long.parseLong(userIdHeader);
                request.setAttribute(USER_ID_ATTRIBUTE, userId);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
