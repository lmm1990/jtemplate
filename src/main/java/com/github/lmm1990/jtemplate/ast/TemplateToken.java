package com.github.lmm1990.jtemplate.ast;

public class TemplateToken {

    /**
     * 模板开始语法
     * */
    private String tagStart = "{{";

    /**
     * 模板结束语法
     * */
    private String tagEnd = "}}";

    /**
     * 模板开始语法长度
     * */
    private int tagStartLength = tagStart.length();

    /**
     * 模板结束语法长度
     * */
    private int tagEndLength = tagEnd.length();

    public String getTagStart() {
        return tagStart;
    }

    public void setTagStart(String tagStart) {
        this.tagStart = tagStart;
        this.tagStartLength = tagStart.length();
    }

    public String getTagEnd() {
        return tagEnd;
    }

    public void setTagEnd(String tagEnd) {
        this.tagEnd = tagEnd;
        this.tagEndLength = tagEnd.length();
    }

    public int getTagStartLength() {
        return tagStartLength;
    }

    public int getTagEndLength() {
        return tagEndLength;
    }
}
