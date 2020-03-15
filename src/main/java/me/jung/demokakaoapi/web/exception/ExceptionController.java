package me.jung.demokakaoapi.web.exception;

import lombok.RequiredArgsConstructor;
import me.jung.demokakaoapi.advice.exception.CAuthenticationEntryPointException;
import me.jung.demokakaoapi.domain.CommonResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/exception")
public class ExceptionController {

    @GetMapping(value = "/entrypoint")
    public CommonResult entrypointException() {
        throw new CAuthenticationEntryPointException();
    }

    @GetMapping(value = "/accessdenied")
    public CommonResult accessDeniedException(HttpServletRequest request, AccessDeniedHandler e) {
        throw new AccessDeniedException("");
    }

}
