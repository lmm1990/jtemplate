package com.github.lmm1990.jtemplate.ast;

/**
 * if、else if、else、each结尾节点
 * */
public class EndNode extends INode {

    private NodeType nodeType = NodeType.END_TAG;

    @Override
    public NodeType getNodeType() {
        return nodeType;
    }
}
