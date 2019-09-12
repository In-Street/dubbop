###dubbo 远程调用过程：
   
   *服务暴露：
   
   ![dubbo-provider](https://raw.githubusercontent.com/yufeicheng/images/master/dubbo/dubbo_provider.png)
     
   1. 通过ServiceConfig 拿到对外提供服务的类，通过ProxyFactory 的getInvoke() 生成AbstractProxyInvoker, 完成服务到invoker的转化；
   
   2. 通过DubboProtocol 的 export 方法，完成invoker向exporter的转化【创建 Netty Server 侦听服务，接受客户端请求，然后完成服务注册中心服务】
   
   * 服务消费：
   
   ![dubbo-consumer](https://raw.githubusercontent.com/yufeicheng/images/master/dubbo/dubbo-consumer.png)
   
   1.  ReferenceConfig 的init 方法调用 DubboProtocol的 refer 方法 生成invoker实例，【创建netty client 链接服务提供者】
   2.  invoker 实例转化为调用的接口服务。
---

### Dubbo 容错模式： <设置cluster 属性>
    
   1. Failover cluster:
   
        1. 失败后自动切换到其他其他服务提供者重试
        2. retries = 2,对接口内的所有方法都有效，或指定固定方法
        ```
            @Reference(retries = 2)
            或
            <dubbo:reference>
                  <dubbo:method name="findFoo" retries="2" />
            </dubbo:reference>
        ```
        3. 实用读操作或幂等写操作。[接口不幂等，会有重复写的问题]
        4. 设置超时时间不合理，一次任务还没执行完就会重试再次执行的问题。
        
   2. Failfast cluster:
   
        1. 快速失败，调用服务失败后立即报错，也只调用一次。
        2. 适用于非幂等写操作。
        
   3. Failsafe cluster:
   
        1. 失败安全，调用服务出现异常，直接忽略异常。
        2. 适用日志写入操作。
        
   4. Failback cluster:
    
        1. 调用服务失败后，后台记录请求，然后按一定策略进行重试。
        2. 适用消息通知操作
        
   5. Forking cluster:
   
        1. client 并行调用多个服务提供者，只要一个成功即返回。
        2. 适用实时性要求高的写操作。
        3. 浪费服务资源。
        4. forks 设置最大并行数。  
        
   6. Broadcast cluster:
    
        1. dubbo client 逐个调用所有服务提供者，任一一次失败则本次标记失败，
        2. 适用通知所有提供者更新缓存或日志。
        
   7. 自定义： 扩展 Cluster 接口
   
### Dubbo 负载均衡策略： <设置loadbalance属性>

   1. random loadbalance:
   
        1. 默认设置
        2. 随机策略 
        
   2. RoundRobin loadbalance:
        
        1. 轮询策略
        2. 会存在执行比较慢的服务提供者请求堆积，时间长，消费者调用这个服务的请求处理会阻塞。
        
   3. LeastActive loadbalance：
   
        1. 最少活跃调用数策略。
        2. 每个服务提供者维护着一个活跃数计数器，用来记录当前同时处理请求的个数，值越小说明当前服务提供者处理速度越快，负载越小，路由时应该选择该机器。
        3. 请求处理堆积越多，该值越大。
        
   4. ConsistentHash loadbalance：
   
        1. 一致性Hash策略。    
        2. 引入虚拟节点，保证均衡性
         ![一致性Hash](https://raw.githubusercontent.com/yufeicheng/images/master/dubbo/%E4%B8%80%E8%87%B4%E6%80%A7HASH.png)
        
        3. Dubbo提供的一致性Hash策略是不均匀的。
        
### 线程：

   1. Dubbo 默认的底层网络通讯使用的是 Netty ，服务提供方 NettyServer 使用两级线程池，其中 EventLoopGroup(boss) 主要用来接受客户端的链接请求，并把接受的请求分发给 EventLoopGroup（worker） 来处理，boss 和 worker 线程组我们称之为 IO 线程。
    
   2.如果服务提供方的逻辑能迅速完成，并且不会发起新的 IO 请求，那么直接在 IO 线程上处理会更快，因为这减少了线程池调度。
    
   3. 但如果处理逻辑较慢，或者需要发起新的 IO 请求，比如需要查询数据库，则 IO 线程必须派发请求到新的线程池进行处理，否则 IO 线程会被阻塞，将导致不能接收其它请求。        
        
### Provider 设置线程池【application.properties】

   1. 调度策略： dubbo.protocol.dispatcher=xxx
   
        1. all : 所有消息都派发到线程池，包括请求，响应，连接事件，断开事件，心跳等。
        
        2. direct: 所有消息都不派发到线程池，全部在 IO 线程上直接执行。
        3. message 只有请求响应消息派发到线程池，其它连接断开事件，心跳等消息，直接在 IO 线程上执行。
        4. execution 只请求消息派发到线程池，不含响应，响应和其它连接断开事件，心跳等消息，直接在 IO 线程上执行。
        5. connection 在 IO 线程上，将连接断开事件放入队列，有序逐个执行，其它消息派发到线程池。
   
   2. 线程池： dubbo.protocol.threadpool=xxx
        
        1. fixed : 固定线程池，不关闭             
        
        2. cached 缓存线程池，空闲一分钟自动删除，需要时重建
        
        3. limited: 可伸缩线程池，但池中的线程数只会增长不会收缩。只增长不收缩的目的是为了避免收缩时突然来了大流量引起的性能问题。
        
   3. 线程数： dubbo.protocol.threads=xx     
   
   4.  @Service 设 async = true ，consumer无需等待provider执行完成，consmer 需要 RpcContext.getContext().getFuture() 来获取provider异步结果
   
### Consumer 设为异步：
    
   1. 泛化调用异步: referenceConfig.setAsync(true)
   
   2. @Reference(async = true) 【所有方法皆为异步】 并且利用 RpcContext 获取结果：
        
        1. Future<Object> future = RpcContext.getContext().getFuture(); 
        
        2. 
            ```
             Future<Object> future = RpcContext.getContext().asyncCall(() -> {
                        //请求线程
                        log.info("getUser 执行：{}", Thread.currentThread().getName());
                        return userService.getUser();
                    });
    
           ```  
   3. 自定义ReferenceConfig ,设置provider接口的某个方法为异步，此时无需 @Reference注解配置
        
         ```
          @Configuration
                         public class ReferenceConfiguration {
                         
                             private static ReferenceConfig<UserService> referenceConfig ;
                         
                             static {
                                 referenceConfig = new ReferenceConfig<UserService>();
                                 referenceConfig.setInterface(UserService.class);
                                 referenceConfig.setVersion("1.0.0");
                                 referenceConfig.setGroup("UserModule");
                                 referenceConfig.setApplication(new ApplicationConfig("api"));
                                 referenceConfig.setRegistry(new RegistryConfig("zookeeper://39.106.118.71:2181"));
                             }
                         
                             private static final List<MethodConfig> asyncMethods = new ArrayList<>();
                         
                             @Bean
                             public ReferenceConfig asyncMethod() {
                         
                                 MethodConfig methodConfig = new MethodConfig();
                                 methodConfig.setName("getUserAsync");
                                 methodConfig.setAsync(true);
                                 asyncMethods.add(methodConfig);
                                 referenceConfig.setMethods(asyncMethods);
                                 return referenceConfig;
                             }
                         }
                        
                        ///////////////////////////////////////////////////////////// 
                         Controller 中：
                              @Autowired
                              private ReferenceConfig referenceConfig;  
                              
                              获取接口：referenceConfig.get()
         ```      
   
                    
            
### 架构

   * ServiceConfig API 代表发布服务配置对象； ReferenceConfig API 代表消费服务配置对象； 可以初始化配置类，或者通过配置文件
   
   * proxy 服务代理层:  扩展接口 ProxyFactory ，Dubbo 提供 JavassistProxyFactory(默认) 、 JdkProxyFactory 两种实现类, 为Provider、Consumer 提供服务。   
   
   * registry 注册中心层： 封装服务地址注册与发现。 接口 Registry ，实现类 ZookeeperRegistry、RedisRegistry、MulticastRegistry、DubboRegistry ;
                         扩展接口 RegistryFactory , 实现类： DubboRegistryFactory、MulticastRegistryRegistryFactory、RedisRegistryFactory、ZookeeperRegistryFactory
                         
   * cluster 路由层： 容错：FailoverCluster 、FailfastCluster 、 FailsafeCluster 、FailbackCluster、ForkingCluster                         
                     均衡: RandomLoadBalance 、RoundRobinLoadBalance、 LeastActiveLoadBalance 、ConsistentHashLoadBalance
                     
   * monitor 监控层：RPC 调用次数和调用时间监控，扩展接口为 MonitorFactory，对应的实现类为 DubboMonitorFactory。
   
   * protocol 远程调用层：封装 RPC 调用，扩展接口为 Protocol， 对应实现有 RegistryProtocol、DubboProtocol、InjvmProtocol 等。
   
   * serialize 数据序列化层：扩展接口为 Serialization，对应扩展实现有 DubboSerialization、FastJsonSerialization、Hessian2Serialization、JavaSerialization，
                           扩展接口ThreadPool对应扩展实现有 FixedThreadPool、CachedThreadPool、LimitedThreadPool                       