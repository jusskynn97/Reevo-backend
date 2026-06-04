package com.kynn.reevo_backend.user.internal.config;

import java.util.UUID;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.kynn.reevo_backend.user.internal.domain.Account;
import com.kynn.reevo_backend.common.api.CurrentUserId;

@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    // Kiểm tra xem tham số có đánh dấu @CurrentUserId và có kiểu UUID không
    return parameter.hasParameterAnnotation(CurrentUserId.class)
            && parameter.getParameterType().equals(UUID.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null && authentication.getPrincipal() instanceof Account account) {
      return account.getId();
    }

    return null; // Hoặc throw exception nếu bắt buộc phải có user
  }
}