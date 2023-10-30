# fast-crud-assistant

基于模型(实体)快速构建增删改查的助手

生成原理 java annotation process tools(编译时注解处理技术).

好处,将精力更多的投入在对主业务的建模和处理上,将单一表的数据的基本操做交给它即可.

现有对SpringDataJpa的支持.

如何实现?

按所需要的功能 根据按需在Entity类上添加一下注解和注解类所需要生成的pkg(包路径), 或者源码路径sourcePath.

@GenMapper(数据转换工具)
@GenRepository(jap操做对象)
@TypeConverter(Field,字段类型转换工具)
@GenVo(数据展示对象)
@GenQuery(条件查询对象)
@GenCreator(创建中间对象)
@GenUpdater(更新中间对象)
@GenController @GenService @GenServiceImpl (controller 和 service及其实现)

添加data-pre的坐标和apt-last-time的坐标,
针对实体Entity类需要继承data-pre提供的 BaseJpaAggregate 类,
此父类现在已提供
id					(默认 String 类型可自行变更) 
version      	版号 
createdAt 	 创建时间
updatedAt	更新时间 
共四个字段.

并且 BaseJpaAggregate 类继承了 springDataCommons 所提供的 AbstractAggregateRoot 可用于对数据操做时候事件解耦的支持.

使用操做如下:

在启动类同级目录下:

加入 package-info.java

```java
@QueryEntities(value = {BaseJpaAggregate.class})
package com.uiys;

import com.querydsl.core.annotations.QueryEntities;
import com.uiys.jpa.support.BaseJpaAggregate;
```



按需求 在实体类上添加 如上注解.
因需要借助利用apt生成相关的类用到 maven 的 compile(编译)所以需要在项目 pom 文件中的 dependencies 的依赖中添加如下坐标

```xml
<dependency>
  <groupId>com.uiys</groupId>
  <artifactId>data-pre</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>

<dependency>
  <groupId>com.uiys</groupId>
  <artifactId>apt-last-time</artifactId>
  <version>1.0-SNAPSHOT</version>
  <exclusions>
    <exclusion>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
    </exclusion>
    <exclusion>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-openfeign</artifactId>
    </exclusion>
    <exclusion>
      <groupId>org.springframework.data</groupId>
      <artifactId>spring-data-commons</artifactId>
    </exclusion>
  </exclusions>
</dependency>
```

 

在plugins中添加编译时候需要对编译器进行处理的maven插件 (针对注解上的生成)
         

```xml
     <!--这部分的plugin内容式提供上面对添加注解的Entity类生成相应类-->
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.1</version>
        <configuration>
          <source>1.8</source>
          <target>1.8</target>
          <annotationProcessorPaths>

            <dependency>
              <groupId>com.uiys</groupId>
              <artifactId>apt-last-time</artifactId>
              <version>1.0-SNAPSHOT</version>
            </dependency>

            <path>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok</artifactId>
              <version>1.18.20</version>
            </path>
            <path>
              <groupId>org.mapstruct</groupId>
              <artifactId>mapstruct-processor</artifactId>
              <version>1.4.2.Final</version>
            </path>
            <path>
              <groupId>org.projectlombok</groupId>
              <artifactId>lombok-mapstruct-binding</artifactId>
              <version>0.2.0</version>
            </path>
          </annotationProcessorPaths>
        </configuration>
      </plugin>


    <!--这部分的plugin提供Entity类的QueryDsl能力,可使用利用@Repository注解生成的repository接口中父接口QuerydslPredicateExecutor中的查询方便查询的时候使用 BooleanBuilder 表达式-->
      <plugin>
        <groupId>com.mysema.maven</groupId>
        <artifactId>apt-maven-plugin</artifactId>
        <version>1.1.3</version>
        <executions>
          <execution>
            <goals>
              <goal>process</goal>
            </goals>
            <configuration>
              <outputDirectory>target/generated-sources/java</outputDirectory>
              <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
            </configuration>
          </execution>
        </executions>
      </plugin>
```





G1Eneity实体类
![](https://s2.loli.net/2023/10/19/wUJpehKY1xALb8r.jpg)





模型型对象指定创建在 target 编译文件中, 源码中不存在 方便 开发时候对 实体类做变更后, 实时编译生成.
![](https://s2.loli.net/2023/10/19/LBHySrNCUEsAIXi.jpg)



操做型对象和G1实体类
![](https://s2.loli.net/2023/10/19/6x13Pwdva97KqjS.jpg)







#   PostMan 操做

G1 创建
![](https://s2.loli.net/2023/10/19/IQVRCBTAqr15NHd.jpg)



G1更新
![](https://s2.loli.net/2023/10/19/1qULAQlr5giTsER.jpg)



G1数据禁用
![](https://s2.loli.net/2023/10/19/yTNIWlUn4fxgAiE.jpg)



G1数据激活
![](https://s2.loli.net/2023/10/19/S8LZC57Yj4lxtzF.jpg)





G1按Id查找
![](https://s2.loli.net/2023/10/19/5gISKqOpTzX93CQ.jpg)



G1分页查询
![](https://s2.loli.net/2023/10/19/g2jM1B8TtyUILOZ.jpg)







#  END…