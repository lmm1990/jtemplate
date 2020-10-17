package com.github.lmm1990.jtemplate.ast.ifNode;

public enum IfNodeToken {
    IF_TYPE("if"),
    ELSE_IF_TYPE("else if"),
    ELSE_TYPE("else"),
    //if结束节点
    END_IF_TYPE("/if");

    private String name;

    IfNodeToken(String name) {
        this.name = name;
    }

    public static IfNodeToken of(String name) {
        for(IfNodeToken s : values()) {
            if(name.equals(s.getName())) {
                return s;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
