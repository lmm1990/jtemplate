# jTemplate

新一代 java 模板引擎
[![CircleCI](https://circleci.com/gh/lmm1990/jtemplate.svg?style=svg&circle-token=8fbd92b700202d548fc38d581637d156f563f843)](https://circleci.com/gh/lmm1990/jtemplate)
![maven](https://img.shields.io/maven-central/v/com.github.lmm1990/jtemplate)
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
![java version](https://img.shields.io/badge/JAVA-11+-green.svg)

##      	目录

*	[特性](#特性)
*	[快速上手](#快速上手)
*	[模板语法](#模板语法)
*	[更新日志](#更新日志)
*	[授权协议](#授权协议)

##	特性

1.	无需第三方依赖

## 快速上手

### Maven 资源

```xml
<dependency>
    <groupId>com.github.lmm1990</groupId>
    <artifactId>jtemplate</artifactId>
    <version>1.2</version>
</dependency>
```

## Gradle 依赖

```gradle
compile group: 'com.github.lmm1990', name: 'jtemplate', version: '1.2'
```

##	模板语法

### 输出

```
{{value}}
{{data.key}}
```

### 条件

```
{{if value}} 
... 
{{/if}}

{{if v1}}
... 
{{else if v2}}
...
{{/if}}
```

### 循环

```
{{each item in list}}
    {{item.name}}:{{item.price}}
{{/each}}
```

## 更新日志

###	v1.2

1. 支持\转义

## 授权协议

Apache License 2.0