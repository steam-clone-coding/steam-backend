package com.clonecoding.steam.aop;


import com.clonecoding.steam.dto.user.UserDTO;
import com.clonecoding.steam.service.user.impl.UserServiceImpl;
import com.clonecoding.steam.utils.auth.SecurityUserContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class UserInfoReadAspect {

    private final UserServiceImpl userService;

    @Around("@annotation(com.clonecoding.steam.aop.annotation.UserInfoRead)")
    public Object processCustomAnnotation(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        addUserInRequest(proceedingJoinPoint);
        return proceedingJoinPoint.proceed();
    }


    private void addUserInRequest(ProceedingJoinPoint pjp) {
        for (Object obj : pjp.getArgs()) {
            if (!(obj instanceof HttpServletRequest)) {
                continue;
            }
            HttpServletRequest request = (HttpServletRequest) obj;
            String username = SecurityUserContextUtils.getUsernameFromSecurityContext();

            UserDTO.Preview userDTO = userService.getUserByUsername(username);

            request.setAttribute("user", userDTO);

            break;

        }
    }


}
