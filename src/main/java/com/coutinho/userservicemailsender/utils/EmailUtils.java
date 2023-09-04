package com.coutinho.userservicemailsender.utils;

public class EmailUtils {

    public static String getEmailMessage(String name, String host, String token){
        return "Hello" +name+ "\n\nYour account has been created. please click the link below to verify your account \n\n"
                + getVerificationUrl(host, token) + "\n\n The support Team";
    }
    public static String getVerificationUrl(String host, String token){
        return host + "/api/users?token=" + token;
    }
}
