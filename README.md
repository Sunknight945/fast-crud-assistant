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



# 2.增加filterPipeline

汽车充电, 有两种充发, 一个快充, 一个慢充. 
充电的整个逻辑是且这个逻辑比较长且对于快充来说第四步将被省略.
1. 写入充电日志
2. 找到用户
3. 找到车
4. 给慢充的发快充的体验券 (对于使用快充的这一步需要省略...)
5. 给用户添加充电积分
6. 写入充电时间.
那么如何开发这个功能更合理?
写大段代码可以实现 (第四步的时候加分支判断)
写6个service也可以, (第四步的时候进行判断)
写责任链也可以, 更优雅.(但是正链需要实现跳脸已实现第四步的跳链, 然后最好支持 动态跳链 以方便 以后可能不给 慢充的用户快充体验券. )

```yaml
com:  
  uiys:  
    charging:  
      KI:  
        - ChargeLogFilter  
        - FindAccountFilter  
        - FindCarFilter  
        - AddIntegralFilter  
        - WriteChargeTimeFilter  
      MI:  
        - ChargeLogFilter  
        - FindAccountFilter  
        - FindCarFilter  
        - SendKICouponFilter  
        - AddIntegralFilter  
        - WriteChargeTimeFilter

```
以上.   

## 根据业务代码的差异,自由组合业务执行链条, 动态实现跳链 和终止执行链, 执行链依赖上下文.

里面的角色有
###1. Filter  (单个执行单元) 
###2. FilterChain (执行单元的链条)
###3. FilterPipeline (控制执行单元的管道,获取FilterChain并让filterChain执行起来)
###4. PipelineConfiguration(在启动的时候初始化的filterChain)

###5. FilterContext(filter执行时候的上下文)
###6. FilterSelector(执行单元的选择器 [根据Filter来查看这个默认的执行链上有没有当前的执行单元] )
###7. LocalBaseFilterSelector (执行单元选择器的默认实现需要根据BizCode手动装填存放在Yaml文件中的Filter名称)
###8. ChargingWayConfiguration(读取xx前缀的Filter 列表)




##    然后整体的逻辑流程是 

SpringBoot 在启动的时候 会在 PipelineConfiguration 里面根据写入的 FilterBean 在创建FilterPipeline的时候将 FilterBean 以node的形式写入到 pipeLine 的FilterChain中 这个FilterChain理应存放了所有的Filter.
然后 根据请求 里面的 BizCode ,把需要的Filter的名字写入到 FilterSelector 的实现类 LocalBaseFilterSelector 的FilterNames中. 用与跳链.
Context 里面会将请求放进去, 并支持 是否执行的Boolean vlaue. 然后 把 FilterSelector 放进去, 以便在链中判断当前filter需不需要执行.

###1. **pipeline 找到 filterChain #hand(context),**
###2. **filterChain 找到 filter #doFilter(context,filterChain).**
###3. **在 filer 里面context获取filterName 做当前filter名字的对比. 有的话就执行, 没有的话 看 context继续不 , 如果继续 就 由filterChain #fireNext(context)**

###4. **filterChain 的 next 如果不为null 就 next.hand(context); 把当前剩余的 (next)chain 走完.**



4在启动的时候初始化的filterChain

```java
@Configuration  
public class PipelineConfiguration {  
    @Bean  
    public FilterPipeline filterPipeline() {  
       FilterPipeline filterPipeline = new FilterPipeline();  
       filterPipeline.addFilter("日志记录Filter", chargeLogReportFilter());  
       filterPipeline.addFilter("找人Filter", findAccountFilter());  
       filterPipeline.addFilter("找车Filter", findCarFilter());  
       filterPipeline.addFilter("慢充用户发券Filter", sendKICouponFilter());  
       filterPipeline.addFilter("赠予积分Filter", addIntegralFilter());  
       filterPipeline.addFilter("写入充电时间Filter", writeChargeTimeFilter());  
       return filterPipeline;  
      }  
    
    @Bean  
    public ChargeLogFilter chargeLogReportFilter() {  
       return new ChargeLogFilter();  
    }  
  
    @Bean  
    public FindCarFilter findCarFilter() {  
       return new FindCarFilter();  
    }  
    
    @Bean  
    public FindAccountFilter findAccountFilter() {  
       return new FindAccountFilter();  
    }  
    
    @Bean  
    public SendKICouponFilter sendKICouponFilter() {  
       return new SendKICouponFilter();  
    }  
    
    @Bean  
    public AddIntegralFilter addIntegralFilter() {  
       return new AddIntegralFilter();  
    }  
    
    @Bean  
    public WriteChargeTimeFilter writeChargeTimeFilter() {  
       return new WriteChargeTimeFilter();  
    }  
  }
```


3. FilterPipeline (控制执行单元的管道,获取FilterChain并让filterChain执行起来)
```java
@Getter  
@Slf4j  
public class FilterPipeline {  
  
    private DefaultFilterChain chain;  
  
    public <E extends ChargeContext> void addFilter(String desc, ChargeFilter<E> filter) {  
       log.info("加入{},filter处理器!", desc);  
       this.chain = new DefaultFilterChain<E>(chain, filter);  
    }  

}

```

FilterChain (执行单元的链条)
```java
public interface FilterChain<E> {  
  
    void hand(E context);  
  
    void fireNext(E context);  
  
}
```
FilterChain (执行单元的链条的实现)
```java
public class DefaultFilterChain<E extends ChargeContext> implements FilterChain<E> {  
    private final DefaultFilterChain<E> next;  
    private final ChargeFilter<E> filter;  
  
  
    public DefaultFilterChain(DefaultFilterChain<E> next, ChargeFilter<E> filter) {  
       this.next = next;  
       this.filter = filter;  
    }  
  
    @Override  
    public void hand(E context) {  
       filter.doFilter(context, this);  
    }  
  
    @Override  
    public void fireNext(E context) {  
       if (Objects.nonNull(next)) {  
          next.hand(context);  
       }  
    }  
}
```
FilterSelector(执行单元的选择器)
```java
public interface FilterSelector  {  
  
    boolean matchFilter(String filterName);  
  
    List<String> filterNames();  
  
}
```
FilterSelector(执行单元的选择器实现)
```java
public class LocalBasedFilterSelector implements FilterSelector {  
  
    private List<String> filterNames = new ArrayList<>();  
  
  
    public LocalBasedFilterSelector() {  
    }  
  
    public void addFilter(String filterName){  
       filterNames.add(filterName);  
    }  
  
    @Override  
    public boolean matchFilter(String filterName) {  
       return filterNames.contains(filterName);  
    }  
  
    @Override  
    public List<String> filterNames() {  
       return this.filterNames;  
    }  
}
```


 Filter  (单个执行单元) 
```java
public interface ChargeFilter<E> {  
  
    void doFilter(E context, FilterChain<E> chain);  
   
}
```
 Filter  (单个执行单元抽象类) 
```java
public abstract class AbstractChargeFilter<E extends ChargeContext> implements ChargeFilter<E> {  
  
    @Override  
    public void doFilter(E context, FilterChain<E> chain) {  
       if (context.getSelector()  
         .filterNames()  
         .contains(this.getClass()  
          .getSimpleName())) {  
          hand(context);  
       }  
       if (context.continued()) {  
          chain.fireNext(context);  
       }  
  
  
    }  
  
    public abstract void hand(E context);  
}
```
 Filter  (单个执行单元其中的一个实现类)  还有其他![[Pasted image 20231025151358.png]]
```java
public class AddIntegralFilter extends AbstractChargeFilter<CurrentChargeContext> {  
  
    @Autowired  
    private IntegralRecordRepository integralRecordRepository;  
  
    @Override  
    public void hand(CurrentChargeContext context) {  
       System.out.println("\"AddIntegralFilter\" = " + "AddIntegralFilter");  
       ChargeRequest request = context.getRequest();  
       System.out.println("context.getRequest() = " + request);  
  
       IntegralRecordCreator integralCreator = new IntegralRecordCreator();  
       integralCreator.setIntegral(new Random().nextInt(100));  
       integralCreator.setWxOpenId(request.getUserId());  
       integralCreator.setInOrOut(IntegralInOrOut.IN);  
  
       EntityOperations.doCreate(integralRecordRepository)  
         .create(() -> IntegralRecordMapper.INSTANCE.u2E(integralCreator))  
         .update(IntegralRecord::init)  
         .execute();  
  
    }  
}
```


ChargingWayConfiguration
```java
@Getter  
@Configuration  
@ConfigurationProperties(prefix = "com.uiys")  
public class ChargingWayConfiguration {  
  
    @Setter  
    private Map<String, List<String>> charging;  
  
  
    public Optional<List<String>> getWaysByBizCode(BizCode code) {  
       if (Objects.isNull(charging)) {  
          throw new RuntimeException("filter配置有问题!");  
       }  
  
  
       if (!charging.containsKey(code.toString())) {  
          return Optional.empty();  
       }  
       return Optional.of(charging.get(code.toString()));  
    }  
  
}

```

# 3.增加内存数据处理器

整体思路: 利用SpeL表达式结合函数式编程. 构建出处理这种数据结构的executor.
一个这种数据可能有3个注解标志,SpeL的表达式放在注解的字段上面,执行的时候根据类找到
所有的field,探查field有无次注解如果有则创建字段Executor后再创建类Executor.
最后由类Executor执行.执行过程与手写的过程差不多, 但是要用到SpeL表达式和函数式编程.SpeL表达式用于构建字段Executor时的Function 和BiConsumer函数对象之后就交由 apply 和 accept 自动执行.

有这一种数据处理情况
```json
{
    "预先查询结构1": {
        "自身信息1": "Welcome to JSON Viewer Pro",
        "自身信息2": "Welcome to JSON Viewer Pro",
        "其他数据_1_key": "数据1的key值",
        "其他数据_2_key": "数据2的key值",
        "其他数据_3_key": "数据3的key值"
    },
    "其他数据_1": {},
    "其他数据_2": {},
    "其他数据_3": []
}
```
其中预先查询的数据已经出现, 需要对下面其他数据 1,2,3 做进一步的查询和拼装操做,其中 数据1 和数据 2 是单条对象, 而数据3是多条对象, 那么 对于此种数据结构, 可能会有6个步骤 1. 批量查询,2.数据转换, 3.数据做分组, 4.数据装配. 

类似于: 下面两个最简形式.数据量越大操做越多,重复结构的代码也越多.
```java
public class OrderDetailVo {  
    private OrderVO orderVO;// 原始查询到的数据  
    private AddressVO address;  
    private ShopVO shop;  
}
List<OrderDetailVo> sourceList = 查询处理的数据;

List<String> shopIds = sourceList.stream()  
  .map(item -> item.getOrderVO()  
    .getShopId())  
  .collect(Collectors.toList());  
  
Map<String, ShopVO> showIdMap = shopRepository.findAllById(shopIds)  
  .stream()  
  .map(ShopMapper.INSTANCE::u2Vo)  
  .collect(Collectors.toMap(ShopVO::getId, Function.identity()));  
  
List<String> addressIds = sourceList.stream()  
  .map(item -> item.getOrderVO()  
    .getAddressId())  
  .collect(Collectors.toList());  
Map<String, AddressVO> addressIdMap = addressRepository.findAllById(addressIds)  
  .stream()  
  .map(AddressMapper.INSTANCE::u2Vo)  
  .collect(Collectors.toMap(AddressVO::getId, Function.identity()));  
  
sourceList.forEach(item -> {  
    item.setAddress(addressIdMap.getOrDefault(item.getOrderVO()  
      .getAddressId(), null));  
    item.setShop(showIdMap.getOrDefault(item.getOrderVO()  
      .getShopId(), null));  
});
```
但是这个是可以避免的.

避免成这样在数据结构需要的字段上面加入这个注解就行了:
```java
@Data  
public class OrderDetailVo {  
    private OrderVO orderVO;  
    @MemoryDataHandler(sourceKey = "#{orderVO.addressId}",   
                       joinDataLoader = "#{@addressRepository.findAllById(#root)}",   
                       dataKey = "#{id}",   
                       joinDataConverter = "#{T(com.uiys.order.mapper.AddressMapper).INSTANCE.u2Vo(#root)}")  
    private AddressVO address;  
    @MemoryDataHandler(sourceKey = "#{orderVO.shopId}",   
                       joinDataLoader = "#{@shopRepository.findAllById(#root)}",  
                       dataKey = "#{id}",   
                       joinDataConverter = "#{T(com.uiys.order.mapper.ShopMapper).INSTANCE.u2Vo(#root)}")  
    private ShopVO shop;  
  
}
```


甚至这样:
```java
@Data  
@MemoryDataHandlerTypeConfig(runWay = MemoryRunWays.Parallel)
public class OrderDetailVo {  
    private OrderVO orderVO;  
    @MemoryDataHandler(sourceKey = "#{orderVO.addressId}",   
                       joinDataLoader = "#{@addressRepository.findAllById(#root)}",   
                       dataKey = "#{id}",   
                       joinDataConverter = "#{T(com.uiys.order.mapper.AddressMapper).INSTANCE.u2Vo(#root)}")  
    private AddressVO address;  
    @MemoryDataHandler(sourceKey = "#{orderVO.shopId}",   
                       joinDataLoader = "#{@shopRepository.findAllById(#root)}",  
                       dataKey = "#{id}",   
                       joinDataConverter = "#{T(com.uiys.order.mapper.ShopMapper).INSTANCE.u2Vo(#root)}")  
    private ShopVO shop;  
  
}
```

然后一行代码搞定:
```java
memoryDataExecutor.load(sourceList);
```



#  未完待续…