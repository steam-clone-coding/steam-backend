package com.clonecoding.steam.utils.auth;

import com.clonecoding.steam.exceptions.ExceptionMessages;
import com.clonecoding.steam.exceptions.UnAuthorizedException;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUserContextUtils {

    public static String getUsernameFromSecurityContext(){

        try{
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if(username == null || username.equals("anonymousUser")){
                throw new Exception();
            }

            return username;
        }catch (Exception e){
            throw new UnAuthorizedException(ExceptionMessages.USER_NOT_FOUND.getMessage());
        }

    }
}

