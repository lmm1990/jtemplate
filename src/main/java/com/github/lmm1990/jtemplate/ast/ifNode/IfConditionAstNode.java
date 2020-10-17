package com.github.lmm1990.jtemplate.ast.ifNode;

public abstract class IfConditionAstNode extends IfAstNode {

    /**
     * 条件
     */
    private String condition;

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
