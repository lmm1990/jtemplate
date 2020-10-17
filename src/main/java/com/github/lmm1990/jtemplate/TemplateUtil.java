package com.github.lmm1990.jtemplate;

import com.github.lmm1990.jtemplate.ast.*;
import com.github.lmm1990.jtemplate.ast.ifNode.*;

import java.util.*;
import java.util.regex.Pattern;

public class TemplateUtil {

    private List<INode> baseAstNodeList = new ArrayList<>();
    private List<INode> astNodeList;

    /**
     * 编译模板
     */
    public TemplateUtil(String sourceCode) {
        astNodeList = parse(sourceCode);
    }

    private static final Pattern emptySpacePattern = Pattern.compile("\\s+");

    /**
     * 创建ParserContext
     */
    private List<ParserContext> createParserContext(String sourceCode) {
        String[] rowList = sourceCode.replaceAll("\r\n", "\n").split("\n");
        List<ParserContext> parserContextList = new ArrayList<>(rowList.length);
        for (int i = 0, length = rowList.length; i < length; i++) {
            rowList[i] = rowList[i].trim();
            parserContextList.add(new ParserContext(String.format("%s\n", rowList[i]), i + 1, 0, 0));
        }
        return parserContextList;
    }

    /**
     * 节点环境下标列表
     */
    private final List<Integer> envNodeindexList = new ArrayList<>();

    /**
     * 解析语法
     */
    private List<INode> parse(String sourceCode) {
        List<ParserContext> contextList = createParserContext(sourceCode);
        List<INode> tempNodeList = null;
        ParserContext context;
        String content;
        INode currentNode;
        for (int line = 1, length = contextList.size(); line <= length; line++) {
            context = contextList.get(line - 1);
            while (true) {
                content = context.getSource().substring(context.getOffset());
                if (content.isEmpty()) {
                    break;
                }
                currentNode = envNodeindexList.isEmpty() ? null : baseAstNodeList.get(baseAstNodeList.size() - 1);
                //当前节点有子节点
                if (currentNode instanceof ChildrenNode) {
                    INode tempNode = getEnvNode();
                    if (tempNode instanceof ChildrenNode) {
                        tempNodeList = ((ChildrenNode) tempNode).getChildrenList();
                    }
                } else {
                    tempNodeList = null;
                }
                context = parseTemplate(context, tempNodeList == null ? baseAstNodeList : tempNodeList);
            }
        }
        return transform(baseAstNodeList);
    }

    /**
     * 合并连续TextNode
     */
    private List<INode> transform(List<INode> astNodeList) {
        INode baseCurrentNode;
        TextNode prevNode, currentNode = null;
        for (int i = 1, length = astNodeList.size(); i < length; i++) {
            baseCurrentNode = astNodeList.get(i);
            if (baseCurrentNode instanceof ChildrenNode) {
                transform(((ChildrenNode) baseCurrentNode).getChildrenList());
                continue;
            }
            if (NodeType.END_TAG == baseCurrentNode.getNodeType()) {
                astNodeList.remove(i);
                i--;
                length--;
                continue;
            }
            if (NodeType.TEXT == baseCurrentNode.getNodeType()) {
                currentNode = (TextNode) astNodeList.get(i);
                if (currentNode.getContent().equals("\n")) {
                    astNodeList.remove(i);
                    i--;
                    length--;
                    continue;
                }
            }
            if (astNodeList.get(i - 1).getNodeType() == NodeType.TEXT &&
                    NodeType.TEXT == baseCurrentNode.getNodeType()) {
                prevNode = (TextNode) astNodeList.get(i - 1);
                prevNode.setContent(String.format("%s%s", prevNode.getContent(), currentNode.getContent()));
                astNodeList.remove(i);
                i--;
                length--;
            }
        }
        return astNodeList;
    }

    /**
     * 解析模板
     */
    private ParserContext parseTemplate(ParserContext parserContext, List<INode> astNodeList) {
        String content = parserContext.getSource().substring(parserContext.getOffset());
        int column = content.indexOf(TemplateToken.TAG_START.getValue());
        //解析文本节点
        if (column == -1) {
            ParserContext tempContext = new ParserContext(parserContext);
            tempContext.setColumn(parserContext.getOffset());
            tempContext.setOffset(parserContext.getSource().length());
            return parseText(tempContext, astNodeList);
        }
        column += parserContext.getOffset();
        if (column != parserContext.getOffset()) {
            ParserContext tempContext = new ParserContext(parserContext);
            tempContext.setColumn(parserContext.getOffset());
            tempContext.setOffset(column);
            return parseText(tempContext, astNodeList);
        }
        column += TemplateToken.TAG_START.getValue().length();
        content = parserContext.getSource().substring(column);
        int offset = content.indexOf(TemplateToken.TAG_END.getValue());
        if (offset == -1) {
            throw new TemplateSyntaxException(String.format("语法错误，缺少：%s，行：%d，列：%d",
                    TemplateToken.TAG_END.getValue(), parserContext.getLine(), offset));
        }
        offset += column;
        content = parserContext.getSource().substring(column, offset);

        String sourceCode = emptySpacePattern.matcher(content).replaceAll(" ");

        //else if 特殊处理
        int index = sourceCode.indexOf(IfNodeToken.ELSE_IF_TYPE.getName());
        if(index > -1){
            sourceCode = sourceCode.substring(index+IfNodeToken.ELSE_IF_TYPE.getName().length()).trim();
        }
        String[] baseLexicalList = sourceCode.split(" ");
        String[] lexicalList;
        if(index> -1){
            lexicalList = new String[baseLexicalList.length+1];
            lexicalList[0] = IfNodeToken.ELSE_IF_TYPE.getName();
            for (int i = 0,length = baseLexicalList.length; i < length; i++) {
                lexicalList[i+1] = baseLexicalList[i];
            }
        }
        else {
            lexicalList = baseLexicalList;
        }
        if (lexicalList[0].equals(NodeType.IF_TAG.getName()) ||
                lexicalList[0].equals(IfNodeToken.ELSE_IF_TYPE.getName()) ||
                lexicalList[0].equals(IfNodeToken.ELSE_TYPE.getName()) ||
                lexicalList[0].equals(IfNodeToken.END_IF_TYPE.getName())) {
            //解析if
            return parseIf(lexicalList, parserContext, astNodeList);
        }
        if (lexicalList[0].equals(NodeType.EACH_TAG.getName()) || lexicalList[0].equals(NodeType.END_EACH_TAG.getName())) {
            //解析each
            return parseEach(lexicalList, parserContext, astNodeList);
        }
        if (lexicalList.length == 1) {
            ParserContext tempContext = new ParserContext(parserContext);
            tempContext.setColumn(column);
            tempContext.setOffset(offset);
            return parseVariable(lexicalList[0], tempContext, astNodeList);
        } else {
            throw new TemplateSyntaxException(String.format("语法错误，行：%d，列：%d", parserContext.getLine(), offset));
        }
    }

    /**
     * 获得节点环境下标
     */
    private INode getEnvNode() {
        if (envNodeindexList.isEmpty()) {
            return null;
        }
        if (envNodeindexList.isEmpty()) {
            return null;
        }
        ChildrenNode envNode = null;
        for (int i = 0, length = envNodeindexList.size(); i < length; i++) {
            if (envNode == null) {
                envNode = (ChildrenNode) baseAstNodeList.get(envNodeindexList.get(i));
                continue;
            }
            envNode = (ChildrenNode) envNode.getChildrenList().get(envNodeindexList.get(i));
        }
        return envNode;
    }

    /**
     * 获得待解析的源码
     */
    private String getSourceCode(ParserContext parserContext) {
        return parserContext.getSource().substring(parserContext.getOffset());
    }

    /**
     * 解析each
     *
     * @param lexicalList 词汇列表
     */
    private ParserContext parseEach(String[] lexicalList, ParserContext parserContext, List<INode> astNodeList) {
        String sourceCode = getSourceCode(parserContext);
        int offset = parserContext.getOffset();
        ParserContext context = new ParserContext(parserContext);
        //检查each开头关闭标签
        int index = sourceCode.indexOf(TemplateToken.TAG_END.getValue());
        if (index == -1) {
            throw new TemplateSyntaxException(String.format("语法错误，缺少：%s，行：%d，列：%d",
                    TemplateToken.TAG_END.getValue(), parserContext.getLine(), parserContext.getOffset()));
        }
        offset += index + TemplateToken.TAG_END.getValue().length();
        context.setOffset(offset);
        if (NodeType.END_EACH_TAG.getName().equals(lexicalList[0])) {
            astNodeList.add(new EndNode());
            envNodeindexList.remove(envNodeindexList.size() - 1);
            return context;
        }
        if (lexicalList.length != 4 || !lexicalList[2].equals("in")) {
            throw new TemplateSyntaxException(String.format("语法错误，行：%d，列：%d",
                    parserContext.getLine(), parserContext.getOffset()));
        }
        astNodeList.add(new EachNode(new ArrayList<>(), lexicalList[1], lexicalList[3]));
        envNodeindexList.add(astNodeList.size() - 1);
        return context;
    }

    /**
     * 解析if
     *
     * @param lexicalList 词汇列表
     */
    private ParserContext parseIf(String[] lexicalList, ParserContext parserContext, List<INode> tempNodeList) {
        String sourceCode = getSourceCode(parserContext);
        int offset = parserContext.getOffset();
        ParserContext context = new ParserContext(parserContext);
        //检查if开头关闭标签
        int index = sourceCode.indexOf(TemplateToken.TAG_END.getValue());
        if (index == -1) {
            throw new TemplateSyntaxException(String.format("语法错误，缺少：%s，行：%d，列：%d",
                    TemplateToken.TAG_END.getValue(), parserContext.getLine(), parserContext.getOffset()));
        }
        offset += index + TemplateToken.TAG_END.getValue().length();
        context.setOffset(offset);
        switch (IfNodeToken.of(lexicalList[0])) {
            case IF_TYPE:
                tempNodeList.add(new IfCombination(new ArrayList<>() {{
                    add(new IfNode(new ArrayList<>(), lexicalList[1]));
                }}));
                envNodeindexList.add(tempNodeList.size() - 1);
                envNodeindexList.add(0);
                break;
            case ELSE_IF_TYPE:
                envNodeindexList.remove(envNodeindexList.size() - 1);
                INode tempNode = getEnvNode();
                //IfCombination
                if (tempNode instanceof ChildrenNode) {
                    tempNodeList = ((ChildrenNode) tempNode).getChildrenList();
                }
                tempNodeList.add(new EndNode());
                tempNodeList.add(new ElseIfNode(new ArrayList<>(), lexicalList[1]));
                envNodeindexList.add(tempNodeList.size() - 1);
                break;
            case ELSE_TYPE:
                envNodeindexList.remove(envNodeindexList.size() - 1);
                tempNode = getEnvNode();
                //IfCombination
                if (tempNode instanceof ChildrenNode) {
                    tempNodeList = ((ChildrenNode) tempNode).getChildrenList();
                }
                tempNodeList.add(new EndNode());
                tempNodeList.add(new ElseNode(new ArrayList<>()));
                envNodeindexList.add(tempNodeList.size() - 1);
                break;
            case END_IF_TYPE:
                tempNodeList.add(new EndNode());
                envNodeindexList.remove(envNodeindexList.size() - 1);
                envNodeindexList.remove(envNodeindexList.size() - 1);
                break;
        }
        return context;
    }

    /**
     * 解析变量
     */
    private ParserContext parseVariable(String variable, ParserContext parserContext, List<INode> astNodeList) {
        if (variable.isEmpty()) {
            throw new TemplateSyntaxException(String.format("语法错误，行：%d，列：%d", parserContext.getLine(), parserContext.getOffset()));
        }
        INode node = new VariableNode(variable, parserContext);
        addNode(node, astNodeList);
        ParserContext context = new ParserContext(parserContext);
        context.setOffset(parserContext.getOffset() + TemplateToken.TAG_END.getValue().length());
        return context;
    }

    /**
     * 解析文本
     */
    private ParserContext parseText(ParserContext parserContext, List<INode> astNodeList) {
        TextNode node = new TextNode(parserContext.getSource().substring(parserContext.getColumn(), parserContext.getOffset()));
        addNode(node, astNodeList);
        return parserContext;
    }

    /**
     * 添加节点
     */
    private void addNode(INode node, List<INode> astNodeList) {
        INode envNode = getEnvNode();
        if (envNode == null) {
            astNodeList.add(node);
            return;
        }
        if (envNode instanceof ChildrenNode) {
            ((ChildrenNode) envNode).getChildrenList().add(node);
        }
    }

    /**
     * 编译模板
     */
    public String build(Map<String, Object> data) {
        return build(astNodeList, data);
    }

    /**
     * 编译模板
     */
    private String build(List<INode> astNodeList, Map<String, Object> data) {
        StringBuilder result = new StringBuilder();
        for (INode node : astNodeList) {
            switch (node.getNodeType()) {
                case TEXT:
                    result.append(buildText((TextNode) node));
                    break;
                case IF_TAG:
                    result.append(buildIf(data, (IfCombination) node));
                    break;
                case VARIABLE_TAG:
                    result.append(buildVariable(data, (VariableNode) node));
                    break;
                case EACH_TAG:
                    result.append(buildEach(data, (EachNode) node));
                    break;
            }
        }
        return result.toString();
    }

    /**
     * 编译Text
     */
    private String buildText(TextNode node) {
        return node.getContent();
    }

    /**
     * 编译each
     */
    private String buildEach(Map<String, Object> data, EachNode node) {
        StringBuilder result = new StringBuilder();
        Object baseEachData = data.get(node.getListKey());
        Map<String, Object> rowData = new HashMap<>();
        if (baseEachData instanceof Collection) {
            Collection<Map<String, Object>> eachData = (Collection<Map<String, Object>>) baseEachData;
            for (Map<String, Object> baseRow : eachData) {
                rowData.clear();
                baseRow.forEach((k, v) -> {
                    rowData.put(String.format("%s.%s", node.getItemKey(), k), v);
                });
                result.append(build(node.getChildrenList(), rowData));
            }
        }
        return result.toString();
    }

    /**
     * 编译if
     */
    private String buildIf(Map<String, Object> data, IfCombination node) {
        IfAstNode item;
        for (INode baseItem : node.getChildrenList()) {
            item = (IfAstNode) baseItem;
            switch (item.getNodeToken()) {
                case IF_TYPE:
                case ELSE_IF_TYPE:
                    if (buildIfCondition(data, (IfConditionAstNode) item)) {
                        return build(item.getChildrenList(), data);
                    }
                    continue;
                case ELSE_TYPE:
                    return build(item.getChildrenList(), data);
            }
        }
        return "";
    }

    /**
     * 编译if条件
     */
    private boolean buildIfCondition(Map<String, Object> data, IfConditionAstNode node) {
        String[] lexicalList = node.getCondition().split("==");
        if(lexicalList.length==2){
            Object flag = data.get(lexicalList[0]);
            switch (flag.getClass().getTypeName()){
                case "java.lang.Integer":
                    return Integer.parseInt(flag.toString()) == Integer.parseInt(lexicalList[1]);
            }
        }
        Object flag = data.get(node.getCondition());
        switch (flag.getClass().getTypeName()){
            case "java.lang.Boolean":
                return Boolean.parseBoolean(flag.toString());
        }
        //TODO if 非boolean条件暂时未实现
        throw new TemplateSyntaxException("if 非boolean条件暂时未实现");
    }

    /**
     * 编译变量
     */
    private String buildVariable(Map<String, Object> data, VariableNode node) {
        Object result = data.getOrDefault(node.getVariable(), "");
        if (result == null) {
            return "";
        }
        return result.toString();
    }
}
