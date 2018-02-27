package com.sap.cloud.lm.sl.cf.core.security.token;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

public class ParsedToken {

    private OAuth2AccessToken token;
    private String tokenFormat;

    public ParsedToken(OAuth2AccessToken token, String tokenFormat) {
        this.token = token;
        this.tokenFormat = tokenFormat;
    }

    public OAuth2AccessToken getToken() {
        return token;
    }

    public String getTokenFormat() {
        return tokenFormat;
    }

}
