package com.github.lmm1990.jtemplate.ast;

import java.util.List;

public class EachNode extends ChildrenNode {

    private NodeType nodeType = NodeType.EACH_TAG;

    /**
     * 循环项
     * */
    private String itemKey;

    /**
     * 循环列表key
     * */
    private String listKey;

    public EachNode(List<INode> childrenList, String itemKey,String listKey){
        setChildrenList(childrenList);
        this.itemKey = itemKey;
        this.listKey = listKey;
    }

    @Override
    public NodeType getNodeType() {
        return nodeType;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getListKey() {
        return listKey;
    }

    public void setListKey(String listKey) {
        this.listKey = listKey;
    }
}
