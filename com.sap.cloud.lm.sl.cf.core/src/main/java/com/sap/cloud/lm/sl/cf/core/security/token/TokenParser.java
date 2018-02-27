package com.sap.cloud.lm.sl.cf.core.security.token;

public interface TokenParser {

    ParsedToken parse(String tokenString);

}
