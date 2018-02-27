package com.sap.cloud.lm.sl.cf.core.security.token;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import org.cloudfoundry.client.lib.util.JsonUtil;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import com.sap.cloud.lm.sl.cf.client.util.TokenFactory;

@Component
@Order(0)
public class JwtTokenParser extends AbstractTokenParser {

    private final TokenFactory tokenFactory;

    @Inject
    public JwtTokenParser(TokenFactory tokenFactory) {
        this.tokenFactory = tokenFactory;
    }

    @Override
    protected OAuth2AccessToken parseInternal(String tokenString) {
        Map<String, Object> tokenInfo = parseTokenInfo(tokenString);
        return tokenFactory.createToken(tokenString, tokenInfo);
    }

    private Map<String, Object> parseTokenInfo(String tokenString) {
        String[] tokenParts = tokenString.split("\\.", 3);
        if (tokenParts.length != 3) {
            // The token should have three parts (header, body and signature) separated by a dot. It doesn't, so we consider it invalid.
            return Collections.emptyMap();
        }
        String body = decode(tokenParts[1]);
        return JsonUtil.convertJsonToMap(body);
    }

    private String decode(String string) {
        Decoder decoder = Base64.getDecoder();
        return new String(decoder.decode(string), StandardCharsets.UTF_8);
    }

    @Override
    protected String getTokenFormat() {
        return "JWT";
    }

}
