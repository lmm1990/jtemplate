package com.github.lmm1990.jtemplate.ast.ifNode;

import com.github.lmm1990.jtemplate.ast.INode;
import com.github.lmm1990.jtemplate.ast.NodeType;

import java.util.List;

public class ElseIfNode extends IfConditionAstNode {

    private IfNodeToken nodeToken = IfNodeToken.ELSE_IF_TYPE;

    /**
     * 条件
     * */
    private String condition;

    public ElseIfNode(List<INode> childrenList, String condition){
        setChildrenList(childrenList);
        this.condition = condition;
    }

    @Override
    public NodeType getNodeType() {
        return nodeType;
    }

    @Override
    public String getCondition() {
        return condition;
    }

    @Override
    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public IfNodeToken getNodeToken() {
        return nodeToken;
    }
}
