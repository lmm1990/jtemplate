package com.github.lmm1990.jtemplate.ast;

public class TextNode extends INode {

    private NodeType nodeType = NodeType.TEXT;
    /**
     * 内容
     * */
    private String content;

    public TextNode(String content){
        this.content = content;
    }

    @Override
    public NodeType getNodeType() {
        return nodeType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
