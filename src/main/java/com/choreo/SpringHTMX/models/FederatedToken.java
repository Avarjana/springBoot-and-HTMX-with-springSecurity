package com.choreo.SpringHTMX.models;

public class FederatedToken {

    private String idp;
    private String scope;
    private String tokenValidityPeriod;
    private String accessToken;

    public FederatedToken() {
    }

    public String getIdp() {
        return idp;
    }

    public void setIdp(String idp) {
        this.idp = idp;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTokenValidityPeriod() {
        return tokenValidityPeriod;
    }

    public void setTokenValidityPeriod(String tokenValidityPeriod) {
        this.tokenValidityPeriod = tokenValidityPeriod;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "{" +
                "idp='" + idp + '\'' +
                ", scope='" + scope + '\'' +
                ", tokenValidityPeriod='" + tokenValidityPeriod + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}