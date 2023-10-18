# fast-crud-assistant

基于模型(实体)快速构建增删改查的助手

生成原理 java annotation process tools(编译时注解处理技术).

好处,将精力更多的投入在主业务建模和处理上,将单一表数据的基本操做交给它即可.

现有对SpringDataJpa的支持.

如何实现?

现有功能可在 此框架的加持下 按所需要的功能 根据按需在Entity类上添加
@GenMapper(数据转换工具)
@GenRepository(jap操做对象)
@TypeConverter(Field,字段类型转换工具)
@GenVo(数据展示对象)
@GenQuery(条件查询对象)
@GenCreator(创建中间对象)
@GenUpdater(更新中间对象)
@GenController @GenService @GenServiceImpl(controller 和 service及其实现)

添加data-pre的坐标和apt-last-time的坐标即可.

 


















