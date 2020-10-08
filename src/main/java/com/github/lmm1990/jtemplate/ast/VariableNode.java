package com.github.lmm1990.jtemplate.ast;

public class VariableNode extends INode {

    private NodeType nodeType = NodeType.VARIABLE_TAG;
    /**
     * 内容
     * */
    private String variable;

    private ParserContext parserContext;

    public VariableNode(String variable,ParserContext parserContext){
        this.variable = variable;
        this.parserContext = parserContext;
    }

    @Override
    public NodeType getNodeType() {
        return nodeType;
    }

    public String getVariable() {
        return variable;
    }

    public ParserContext getParserContext() {
        return parserContext;
    }

    public void setParserContext(ParserContext parserContext) {
        this.parserContext = parserContext;
    }
}
