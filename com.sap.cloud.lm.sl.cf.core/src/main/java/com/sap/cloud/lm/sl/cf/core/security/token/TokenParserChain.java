package com.sap.cloud.lm.sl.cf.core.security.token;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TokenParserChain {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenParserChain.class);

    private final List<TokenParser> tokenParsers;

    @Inject
    public TokenParserChain(List<TokenParser> tokenParsers) {
        LOGGER.info("Parser chain: " + tokenParsers.toString());
        this.tokenParsers = tokenParsers;
    }

    public ParsedToken parse(String tokenString) {
        for (TokenParser tokenParser : tokenParsers) {
            ParsedToken parsedToken = tokenParser.parse(tokenString);
            if (parsedToken != null) {
                return parsedToken;
            }
        }
        return null;
    }

}
