package com.github.lmm1990.jtemplate.ast;

import java.util.List;

public class IfNode extends ChildrenNode {

    private NodeType nodeType = NodeType.IF_TAG;

    /**
     * 条件
     * */
    private String condition;

    public IfNode(List<INode> childrenList,String condition){
        setChildrenList(childrenList);
        this.condition = condition;
    }

    @Override
    public NodeType getNodeType() {
        return nodeType;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
