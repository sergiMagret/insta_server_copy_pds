package org.udg.pds.springtodo.entity;

import javax.validation.constraints.NotNull;

public class Token {
    public Token(){}
    public Token(String t){
        this.tokenId = t;
    }
    @NotNull
    public String tokenId;
}
