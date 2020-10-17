package com.github.lmm1990.jtemplate.ast.ifNode;

import com.github.lmm1990.jtemplate.ast.INode;

import java.util.List;

public class ElseNode extends IfAstNode {

    IfNodeToken nodeToken = IfNodeToken.ELSE_TYPE;

    public ElseNode(List<INode> childrenList){
        setChildrenList(childrenList);
    }

    @Override
    public IfNodeToken getNodeToken() {
        return nodeToken;
    }
}
