package com.labaway.backend.configuration.security;
/*
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Profile("test")
//@RequiredArgsConstructor
public class TestRollbackFilter extends OncePerRequestFilter {

    private final PlatformTransactionManager transactionManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {

        TransactionTemplate template = new TransactionTemplate(transactionManager);

        template.execute(status -> {
            try {
                filterChain.doFilter(request, response);
                status.setRollbackOnly();  // Always roll back at the end of request
            } catch (IOException | ServletException | RuntimeException e) {
                status.setRollbackOnly();
                throw new RuntimeException(e);
            } catch (Exception e) {
                status.setRollbackOnly();
                throw new RuntimeException(e);
            }
            return null;
        });
    }
}

 */