package com.github.lmm1990.jtemplate.ast;

import java.util.List;

public class ChildrenNode extends INode {

    /**
     * 子级节点列表
     * */
    private List<INode> childrenList;

    public List<INode> getChildrenList() {
        return childrenList;
    }

    public void setChildrenList(List<INode> childrenList) {
        this.childrenList = childrenList;
    }
}
