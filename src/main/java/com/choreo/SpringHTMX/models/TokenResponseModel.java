package com.choreo.SpringHTMX.models;

import java.util.List;

public class TokenResponseModel {

    private String access_token;
    private String refresh_token;
    private String scope;
    private String id_token;
    private List<FederatedToken> federated_tokens;

    public TokenResponseModel() {
    }

    public List<FederatedToken> getFederated_tokens() {
        return federated_tokens;
    }

    public void setFederated_tokens(List<FederatedToken> federated_tokens) {
        this.federated_tokens = federated_tokens;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }
}
