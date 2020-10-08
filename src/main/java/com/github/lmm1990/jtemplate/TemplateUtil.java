package com.github.lmm1990.jtemplate;

import com.github.lmm1990.jtemplate.ast.*;

import java.util.*;
import java.util.regex.Pattern;

public class TemplateUtil {

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

    private List<INode> nodeList = new ArrayList<>();

    /**
     * 解析语法
     */
    private List<INode> parse(String sourceCode) {
        List<ParserContext> contextList = createParserContext(sourceCode);
        List<INode> tempNodeList;
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
                currentNode = envNodeindexList.isEmpty() ? null : nodeList.get(nodeList.size() - 1);
                //当前节点有子节点
                if (currentNode instanceof ChildrenNode) {
                    tempNodeList = ((ChildrenNode) currentNode).getChildrenList();
                } else {
                    tempNodeList = null;
                }
                context = parseTemplate(context, tempNodeList == null ? nodeList : tempNodeList);
            }
        }
        return transform(nodeList);
    }

    /**
     * 合并连续TextNode
     */
    private List<INode> transform(List<INode> nodeList) {
        INode baseCurrentNode;
        TextNode prevNode, currentNode = null;
        for (int i = 1, length = nodeList.size(); i < length; i++) {
            baseCurrentNode = nodeList.get(i);
            if (baseCurrentNode instanceof ChildrenNode) {
                transform(((ChildrenNode) baseCurrentNode).getChildrenList());
                continue;
            }
            if (NodeType.TEXT == baseCurrentNode.getNodeType()) {
                currentNode = (TextNode) nodeList.get(i);
                if (currentNode.getContent().equals("\n")) {
                    nodeList.remove(i);
                    i--;
                    length--;
                    continue;
                }
            }
            if (nodeList.get(i - 1).getNodeType() == NodeType.TEXT &&
                    NodeType.TEXT == baseCurrentNode.getNodeType()) {
                prevNode = (TextNode) nodeList.get(i - 1);
                prevNode.setContent(String.format("%s%s", prevNode.getContent(), currentNode.getContent()));
                nodeList.remove(i);
                i--;
                length--;
            }
        }
        return nodeList;
    }

    /**
     * 解析模板
     */
    private ParserContext parseTemplate(ParserContext parserContext, List<INode> nodeList) {
        String content = parserContext.getSource().substring(parserContext.getOffset());
        int column = content.indexOf(TemplateToken.TAG_START.getValue());
        //解析文本节点
        if (column == -1) {
            ParserContext tempContext = new ParserContext(parserContext);
            tempContext.setColumn(parserContext.getOffset());
            tempContext.setOffset(parserContext.getSource().length());
            return parseText(tempContext, nodeList);
        }
        column += parserContext.getOffset();
        if (column != parserContext.getOffset()) {
            ParserContext tempContext = new ParserContext(parserContext);
            tempContext.setColumn(parserContext.getOffset());
            tempContext.setOffset(column);
            return parseText(tempContext, nodeList);
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
        String[] lexicalList = sourceCode.split(" ");
        if (lexicalList[0].equals(NodeType.IF_TAG.getName()) || lexicalList[0].equals(NodeType.END_IF_TAG.getName())) {
            //解析if
            return parseIf(lexicalList, parserContext, nodeList);
        }
        if (lexicalList[0].equals(NodeType.EACH_TAG.getName()) || lexicalList[0].equals(NodeType.END_EACH_TAG.getName())) {
            //解析each
            return parseEach(lexicalList, parserContext, nodeList);
        }
        if (lexicalList.length == 1) {
            ParserContext tempContext = new ParserContext(parserContext);
            tempContext.setColumn(column);
            tempContext.setOffset(offset);
            return parseVariable(lexicalList[0], tempContext, nodeList);
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
        INode envNode = null;
        for (int i = 0,length = envNodeindexList.size(); i < length; i++) {
            if(envNode == null){
                envNode = nodeList.get(envNodeindexList.get(i));
                continue;
            }
            envNode = ((ChildrenNode)envNode).getChildrenList().get(envNodeindexList.get(i));
        }
        return envNode;
    }

    /**
     * 解析if
     *
     * @param lexicalList 词汇列表
     */
    private ParserContext parseEach(String[] lexicalList, ParserContext parserContext, List<INode> nodeList) {
        String sourceCode = parserContext.getSource().substring(parserContext.getOffset());
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
            nodeList.add(new TextNode("\n"));
            envNodeindexList.remove(envNodeindexList.size() - 1);
            return context;
        }
        if (lexicalList.length != 4 || !lexicalList[2].equals("in")) {
            throw new TemplateSyntaxException(String.format("语法错误，行：%d，列：%d",
                    parserContext.getLine(), parserContext.getOffset()));
        }
        nodeList.add(new EachNode(new ArrayList<>(), lexicalList[1], lexicalList[3]));
        envNodeindexList.add(nodeList.size() - 1);
        return context;
    }

    /**
     * 解析if
     *
     * @param lexicalList 词汇列表
     */
    private ParserContext parseIf(String[] lexicalList, ParserContext parserContext, List<INode> tempNodeList) {
        String sourceCode = parserContext.getSource().substring(parserContext.getOffset());
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
        if (NodeType.END_IF_TAG.getName().equals(lexicalList[0])) {
            tempNodeList.add(new TextNode("\n"));
            envNodeindexList.remove(envNodeindexList.size() - 1);
            return context;
        }
        tempNodeList.add(new IfNode(new ArrayList<>(), lexicalList[1]));
        envNodeindexList.add(tempNodeList.size() - 1);
        return context;
    }

    /**
     * 解析变量
     */
    private ParserContext parseVariable(String variable, ParserContext parserContext, List<INode> nodeList) {
        if (variable.isEmpty()) {
            throw new TemplateSyntaxException(String.format("语法错误，行：%d，列：%d", parserContext.getLine(), parserContext.getOffset()));
        }
        INode node = new VariableNode(variable, parserContext);
        addNode(node, nodeList);
        ParserContext context = new ParserContext(parserContext);
        context.setOffset(parserContext.getOffset() + TemplateToken.TAG_END.getValue().length());
        return context;
    }

    /**
     * 解析文本
     */
    private ParserContext parseText(ParserContext parserContext, List<INode> nodeList) {
        TextNode node = new TextNode(parserContext.getSource().substring(parserContext.getColumn(), parserContext.getOffset()));
        addNode(node, nodeList);
        return parserContext;
    }

    /**
     * 添加节点
     */
    private void addNode(INode node, List<INode> nodeList) {
        INode envNode = getEnvNode();
        if (envNode == null) {
            nodeList.add(node);
            return;
        }
        if (envNode instanceof ChildrenNode) {
            ((ChildrenNode) envNode).getChildrenList().add(node);
        }
    }

    /**
     * 编译模板
     */
    public String build(String sourceCode, Map<String, Object> data) {
        List<INode> nodeList = parse(sourceCode);
        return build(nodeList, data);
    }

    /**
     * 编译模板
     */
    public String build(List<INode> nodeList, Map<String, Object> data) {
        StringBuilder result = new StringBuilder();
        for (INode node : nodeList) {
            switch (node.getNodeType()) {
                case TEXT:
                    result.append(buildText((TextNode) node));
                    break;
                case IF_TAG:
                    result.append(buildIf(data, (IfNode) node));
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
    private String buildIf(Map<String, Object> data, IfNode node) {
        Object flag = data.get(node.getCondition());
        if (flag instanceof Boolean) {
            if((boolean)flag) {
                return build(node.getChildrenList(), data);
            }
            return "";
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
