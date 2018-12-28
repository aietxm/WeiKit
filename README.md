# WeiKit

## Overview
这是一个公共组件库，汲取guava，apache commons，vjtool等库的精华，提供一套相对完整和高性能的java公用工具包，避免常用代码的重复开发。

## 目录规范
1. 每个库在根目录下单独的子目录中, 一个设计良好的库, 目录应该结构如下: 
  
  ```
  README.md
  pom.xml
  src/main
      |-resources
      |-java
         |--org/cirno9/commons/
  ```
  
  说明:
  
    * README.md 功能介绍, 测试用例, 更新记录; 
    * `resources/`文件夹是存放配置文件的, 配置文件可以是`.properties`或者`.xml`, 但作为一个库, 推荐少配置;
    * `java/`文件夹下是源代码, 注意：路径前缀`org/cirno9/commons/` 是必须的, 每个库都应该遵循这个包路径;
  
2. 日志使用slf4j+log4j, 注意不要单独使用log4j! 应该使用日志门面比如slf4j, 详见sample的代码;
3. pom文件: 如果没有必要,建议少依赖第三方的库

## 模块划分
|模块名|功能|稳定版本|备注|
|---|---|---|---|
|common-base|基础包|||
|common-redis|Redis常用功能|||

持续更新中


