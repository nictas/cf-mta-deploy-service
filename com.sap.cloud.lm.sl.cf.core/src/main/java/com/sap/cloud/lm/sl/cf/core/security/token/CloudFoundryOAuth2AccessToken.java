package com.sap.cloud.lm.sl.cf.core.security.token;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

public class CloudFoundryOAuth2AccessToken implements OAuth2AccessToken {

    private OAuth2AccessToken token;
    private String tokenFormat;

    protected CloudFoundryOAuth2AccessToken(OAuth2AccessToken token, String tokenFormat) {
        this.token = token;
        this.tokenFormat = tokenFormat;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return token.getAdditionalInformation();
    }

    @Override
    public Set<String> getScope() {
        return token.getScope();
    }

    @Override
    public OAuth2RefreshToken getRefreshToken() {
        return token.getRefreshToken();
    }

    @Override
    public String getTokenType() {
        return token.getTokenType();
    }

    @Override
    public boolean isExpired() {
        return token.isExpired();
    }

    @Override
    public Date getExpiration() {
        return token.getExpiration();
    }

    @Override
    public int getExpiresIn() {
        return token.getExpiresIn();
    }

    @Override
    public String getValue() {
        return token.getValue();
    }

    public String getTokenFormat() {
        return tokenFormat;
    }

    public static CloudFoundryOAuth2AccessToken from(OAuth2AccessToken token, String tokenFormat) {
        return new CloudFoundryOAuth2AccessToken(token, tokenFormat);
    }

}
