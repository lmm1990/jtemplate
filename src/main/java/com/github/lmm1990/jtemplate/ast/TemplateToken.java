package com.github.lmm1990.jtemplate.ast;

public enum TemplateToken {
    //模板语法开始
    TAG_START("{{"),
    //模板语法结束
    TAG_END("}}");

    private String value;

    TemplateToken(String name) {
        this.value = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
