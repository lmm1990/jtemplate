package com.github.lmm1990.jtemplate.ast;

public enum NodeType {
    //模板语法开始
    TAG_START("{{"),
    //模板语法结束
    TAG_END("}}"),

    //文本节点
    TEXT(""),

    //if节点
    IF_TAG("if"),
    //if结束节点
    END_IF_TAG("/if"),

    //each循环节点
    EACH_TAG("each"),
    //each循环节点
    END_EACH_TAG("/each"),

    VARIABLE_TAG("variable");

    private String name;

    private NodeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
