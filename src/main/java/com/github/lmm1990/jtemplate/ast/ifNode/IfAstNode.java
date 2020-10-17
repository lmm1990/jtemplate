package com.github.lmm1990.jtemplate.ast.ifNode;

import com.github.lmm1990.jtemplate.ast.ChildrenNode;
import com.github.lmm1990.jtemplate.ast.NodeType;

public abstract class IfAstNode extends ChildrenNode {

    NodeType nodeType = NodeType.IF_TAG;

    /**
     * if节点类型
     * */
    IfNodeToken nodeToken;

    public IfNodeToken getNodeToken() {
        return nodeToken;
    }
}
