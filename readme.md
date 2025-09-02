## RPC框架学习笔记

1.定义编码器和解码器

```markdown
1. 使用netty进行网络传输的过程中，需要将jackson数据序列化为二进制流，在接收端需要将二进制流反序列化为 jackson数据
2. 使用netty进行网络传输过程中，由于是纯二进制流进行传输，没有显示的边界表示，可能导致发送的数据会被合并成一个包，包与包之间的界限就要通过编码解码器进行：
	编码器：jackson数据  --->  魔数+数据长度+数据本身  --->   序列化为二进制流 
	解码器：二进制流  --->  解析魔数，长度 -> 获取数据本身  --->  反序列化为原始jackson数据
```

2.定义的一些实体类

```markdown
定义实体：
1. RpcInvocation : RPC调用描述对象 （类名、方法名、参数、返回结果、请求唯一id）
2. RpcProtocol : RPC协议 编码器和解码器的协议 （魔数、数据长度、数据）
3. ServiceWrapper : 服务的ip和端口 （ip + port)
4. ServiceListWrapper : 提供某个服务的一连串ip和端口 (List<[ip]+[端口]>)
5. Cachepool : 定义了resultCache用于缓存服务返回结果 客户端请求唯一ID:返回结果
```



3.客户端流程：

```markdown
1. 创建Netty引导类Bootstrap
2. bootstrap配置： 
		NIO时间循环线程组：用于处理IO事件
		设置TCP保活机制：一段时间没有数据交互连接仍有效
		选择通道实现类型：NIO非阻塞Socket通道
		配置通道初始化逻辑：添加编码、解码器、添加客户端收到数据逻辑（可以添加监听器监听方法返回结果）
3. 使用bootstrap.connect连接服务端开放的ip和端口发送请求
4. 客户端调用的请求拦截，生成代理对象注入，从注册中心查询服务对应的 ip:port ，这边使用了随机、轮询、哈希一致性算法选择适合的服务器端口调取服务，实现负载均衡。
4. 使用Netty的CompletableFuture来接收服务端返回结果，方式为**异步**接收，获取的结果缓存在resultCache中              
```

4.使用注解串联springboot客户端发送服务请求：

```markdown
1. 客户端启动类使用 @EnableRpcClient 注解：将客户端动态代理类处理器引入spring容器中，扫面每个Bean字段，寻找标注了 @RpcReference 的字段，找到了就将该字段生成代理对象，拦截该字段的方法调用，将其发送给远程服务器调用，将生成的代理对象注入。
2. @RpcReference 注解：用于标注需要进行Rpc调用的字段
```

5.服务端流程：

```markdown
1. 创建服务端引导类ServerBootStrap
2. serverBootStrap配置： 
		NIO时间循环线程组：用于处理IO事件
		选择通道实现类型：NIO非阻塞Socket通道
		配置通道初始化逻辑：添加编码、解码器、添加服务端收到数据逻辑（收到客户端的请求，将请求转化为Jackson格式再进行服务调用修改请求中的result字段，再将该请求返回给客户端）
3. 使用serverBootStrap.bind开放服务端连接入口，设置监听器异步监听是否有请求接收。
4. 开启服务，设置自身服务的ip和端口，向redis注册中心进行注册。
```

6.spi机制

```markdown
1.	输入：传入一个接口类 clazz。
2.	查找：在 META-INF/rpc/<接口全限定名> 目录下查找所有 SPI 配置文件。
3.	解析：
      •	按行读取配置文件，解析实现类名与接口类名的映射。
      •	通过反射加载接口类。
4.	缓存：将解析出的实现类与接口类映射信息缓存到 EXTENSION_LOADER_CLASS_CACHE。
5.	输出：缓存中存储了 SPI 加载的所有实现类信息。
```

7.客户端spring动态代理流程

```markdown
1. EnableRpcClient注解中import了ClientInjectHandler配置类，Spring应用上下文启动时扫描所有的配置类，在该配置类中扫描到import了Client类，会对该类进行初始化配置（静态代码块执行），创建netty客户端以及通道等配置，在方法init中应用spi机制注入客户端的相关配置（注册中心、序列化方式、负载均衡算法）。
2. 在ClientInjectHandler配置类中，spring扫描所有字段，找到标有RpcReference注解的字段，将配置好的Client类创建代理对象，使用ClientProxy创建代理对象并注入Reference标注的字段。
3. 客户端在调用该字段的方法时候就会出发注入的代理对象的invoke方法，向服务端发送请求
```

8.服务端spring动态代理的流程

```markdown
1. EnableRpcServer注解中import了ServerInjectHandle配置类，服务端spring应用上下文启动时扫描到该配置，在该配置类中扫描到Server类，并对该类进行初始化（静态代码块执行），创建netty服务端，并在init方法中应用spi机制注入服务端的相关配置（注册中心、序列化方式）。
2. 在ServerInjectHandle配置类中，扫描spring应用中RpcService注解的字段，获取该字段的实现类，并将实现类在ServerCache中进行缓存。
3. 在spring容器启动后调用start方法开启服务端监听。
```