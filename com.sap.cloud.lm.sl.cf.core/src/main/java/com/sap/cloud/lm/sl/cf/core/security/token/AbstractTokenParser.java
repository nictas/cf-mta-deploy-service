package com.sap.cloud.lm.sl.cf.core.security.token;

import org.springframework.security.oauth2.common.OAuth2AccessToken;

public abstract class AbstractTokenParser implements TokenParser {

    private TokenParser nextParser;

    @Override
    public ParsedToken parse(String tokenString) {
        OAuth2AccessToken token = parseInternal(tokenString);
        if (token != null) {
            return ParsedToken.from(token, getTokenFormat());
        }
        if (nextParser != null) {
            return nextParser.parse(tokenString);
        }
        return null;
    }

    protected abstract OAuth2AccessToken parseInternal(String tokenString);

    protected abstract String getTokenFormat();

}
