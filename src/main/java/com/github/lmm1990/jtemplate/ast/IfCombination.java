package com.github.lmm1990.jtemplate.ast;

import java.util.List;

/**
 * if组合，包含else if、else
 * */
public class IfCombination extends ChildrenNode {

    NodeType nodeType = NodeType.IF_TAG;

    @Override
    public NodeType getNodeType() {
        return nodeType;
    }

    public IfCombination(List<INode> childrenList){
        setChildrenList(childrenList);
    }
}
