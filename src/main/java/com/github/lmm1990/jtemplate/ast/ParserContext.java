package com.github.lmm1990.jtemplate.ast;

public class ParserContext {

    /**
     * 源码
     * */
    private String source;

    /**
     * 行
     * */
    private int line;

    /**
     * 列
     * */
    private int column;

    /**
     * 位置
     * */
    private int offset;

    public ParserContext(ParserContext context){
        this.source = context.getSource();
        this.line = context.getLine();
        this.column = context.getColumn();
        this.offset = context.getOffset();
    }

    public ParserContext(String source,int line,int column,int offset){
        this.source = source;
        this.line = line;
        this.column = column;
        this.offset = offset;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
