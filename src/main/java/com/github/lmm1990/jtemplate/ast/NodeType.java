package com.github.lmm1990.jtemplate.ast;

public enum NodeType {

    //文本节点
    TEXT(""),

    //if节点
    IF_TAG("if"),

    //each循环节点
    EACH_TAG("each"),
    //each循环节点
    END_EACH_TAG("/each"),

    VARIABLE_TAG("variable"),

    //if、else if、else、each结尾节点
    END_TAG("end");

    private String name;

    NodeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
