# Tryout of Spring Cloud

and identifying potential replacement for Netflix OSS components

## Discovery service

### Spring Cloud Consul
	Requires consul server
	Distributed as zip, precompiled binary, not for Z linux, written in GO
	• Service Discovery: Clients of Consul can register a service, such as api or mysql, and other clients can use Consul to discover providers of a given service. Using either DNS or HTTP, applications can easily find the services they depend upon.
	• Health Checking: Consul clients can provide any number of health checks, either associated with a given service ("is the webserver returning 200 OK"), or with the local node ("is memory utilization below 90%"). This information can be used by an operator to monitor cluster health, and it is used by the service discovery components to route traffic away from unhealthy hosts.
	• KV Store: Applications can make use of Consul's hierarchical key/value store for any number of purposes, including dynamic configuration, feature flagging, coordination, leader election, and more. The simple HTTP API makes it easy to use.
	• Secure Service Communication: Consul can generate and distribute TLS certificates for services to establish mutual TLS connections. Intentions can be used to define which services are allowed to communicate. Service segmentation can be easily managed with intentions that can be changed in real time instead of using complex network topologies and static firewall rules.
	• Multi Datacenter: Consul supports multiple datacenters out of the box. This means users of Consul do not have to worry about building additional layers of abstraction to grow to multiple regions.
	
	From <https://www.consul.io/docs/intro> 
	
	
	
### Spring Cloud Zookeeper
	Requires standalone install of zookeeper
	Requires some manual management tasks during operation, maybe is automatable now
	
	Java server and client, 
	
	odd number of machines
	Minimum of three servers - can tolerate one server going down
	Recommended 5 servers

	Recommended not sharing data directory with others
	Uses disk for storing log
	
	Should be able to be embedded but not sure
	Curator wraps zookeeper api
	Curator has test classes for embedded server and cluster

## Gateway

### Spring cloud Gateway

- Spring Cloud network name resolution

have to start service with
`-Dspring.cloud.inetutils.preferredNetworks=127.0.0.1`

Reactor netty debug:
`-Dreactor.netty.http.server.accessLogEnabled=true`

- routes on GW: /actuator/gateway/routes

Routes are created by enabling built-in route locator `spring.cloud.gateway.discovery.locator.enabled` or custom RouteLocator implementation. We will probably have to roll a custom implementation.

```json
[
  {
    "predicate": "Paths: [/DISCOVERY/**], match trailing slash: true",
    "metadata": {
      "management.port": "8761"
    },
    "route_id": "ReactiveCompositeDiscoveryClient_DISCOVERY",
    "filters": [
      "[[RewritePath /DISCOVERY/?(?<remaining>.*) = '/${remaining}'], order = 1]"
    ],
    "uri": "lb://DISCOVERY",
    "order": 0
  },
  {
    "predicate": "Paths: [/GATEWAY/**], match trailing slash: true",
    "metadata": {
      "management.port": "9090"
    },
    "route_id": "ReactiveCompositeDiscoveryClient_GATEWAY",
    "filters": [
      "[[RewritePath /GATEWAY/?(?<remaining>.*) = '/${remaining}'], order = 1]"
    ],
    "uri": "lb://GATEWAY",
    "order": 0
  },
  {
    "predicate": "Paths: [/SIMPLEAPISERVICE/**], match trailing slash: true",
    "metadata": {
      "management.port": "8080"
    },
    "route_id": "ReactiveCompositeDiscoveryClient_SIMPLEAPISERVICE",
    "filters": [
      "[[RewritePath /SIMPLEAPISERVICE/?(?<remaining>.*) = '/${remaining}'], order = 1]"
    ],
    "uri": "lb://SIMPLEAPISERVICE",
    "order": 0
  }
]
```

https://spring.io/blog/2020/03/25/spring-tips-spring-cloud-loadbalancer

Routing of request in debug
```log
2021-05-06 12:30:31.192 DEBUG 9524 --- [ctor-http-nio-5] o.s.c.g.h.RoutePredicateHandlerMapping   : Route matched: ReactiveCompositeDiscoveryClient_SIMPLEAPISERVICE
2021-05-06 12:30:31.193 DEBUG 9524 --- [ctor-http-nio-5] o.s.c.g.h.RoutePredicateHandlerMapping   : Mapping [Exchange: GET http://localhost:9090/SIMPLEAPISERVICE] to Route{id='ReactiveCompositeDiscoveryClient_SIMPLEAPISERVICE', uri=lb://SIMPLEAPISERVICE, order=0, predicate=Paths: [/SIMPLEAPISERVICE/**], match trailing slash: true, gatewayFilters=[[[RewritePath /SIMPLEAPISERVICE/?(?<remaining>.*) = '/${remaining}'], order = 1]], metadata={management.port=8080}}
2021-05-06 12:30:31.193 DEBUG 9524 --- [ctor-http-nio-5] o.s.c.g.h.RoutePredicateHandlerMapping   : [97cf0f04-3] Mapped to org.springframework.cloud.gateway.handler.FilteringWebHandler@1e684f05
2021-05-06 12:30:31.193 DEBUG 9524 --- [ctor-http-nio-5] o.s.c.g.handler.FilteringWebHandler      : Sorted gatewayFilterFactories: [[GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.RemoveCachedBodyFilter@594131f2}, order = -2147483648], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.AdaptCachedBodyGlobalFilter@64aeaf29}, order = -2147482648], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.NettyWriteResponseFilter@747835f5}, order = -1], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.ForwardPathFilter@4e0cc334}, order = 0], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.GatewayMetricsFilter@4877919f}, order = 0], [[RewritePath /SIMPLEAPISERVICE/?(?<remaining>.*) = '/${remaining}'], order = 1], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter@51d0ec6f}, order = 10000], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter@1c628f6a}, order = 10150], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.WebsocketRoutingFilter@1e12a5a6}, order = 2147483646], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.NettyRoutingFilter@9b47400}, order = 2147483647], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.ForwardRoutingFilter@7bee8621}, order = 2147483647]]
2021-05-06 12:30:31.231 DEBUG 9524 --- [ctor-http-nio-5] o.s.c.g.r.RouteDefinitionRouteLocator    : RouteDefinition ReactiveCompositeDiscoveryClient_GATEWAY applying {pattern=/GATEWAY/**} to Path
2021-05-06 12:30:31.231 DEBUG 9524 --- [ctor-http-nio-5] o.s.c.g.r.RouteDefinitionRouteLocator    : RouteDefinition ReactiveCompositeDiscoveryClient_GATEWAY applying filter {regexp=/GATEWAY/?(?<remaining>.*), replacement=/${remaining}} to RewritePath
2021-05-06 12:30:31.231 DEBUG 9524 --- [ctor-http-nio-5] o.s.c.g.r.RouteDefinitionRouteLocator    : RouteDefinition matched: ReactiveCompositeDiscoveryClient_GATEWAY
2021-05-06 12:30:31.231 DEBUG 9524 --- [ctor-http-nio-5] o.s.c.g.r.RouteDefinitionRouteLocator    : RouteDefinition ReactiveCompositeDiscoveryClient_DISCOVERY applying {pattern=/DISCOVERY/**} to Path
2021-05-06 12:30:31.232 DEBUG 9524 --- [ctor-http-nio-5] o.s.c.g.r.RouteDefinitionRouteLocator    : RouteDefinition ReactiveCompositeDiscoveryClient_DISCOVERY applying filter {regexp=/DISCOVERY/?(?<remaining>.*), replacement=/${remaining}} to RewritePath
2021-05-06 12:30:31.232 DEBUG 9524 --- [ctor-http-nio-5] o.s.c.g.r.RouteDefinitionRouteLocator    : RouteDefinition matched: ReactiveCompositeDiscoveryClient_DISCOVERY
2021-05-06 12:30:31.232 DEBUG 9524 --- [ctor-http-nio-5] o.s.c.g.r.RouteDefinitionRouteLocator    : RouteDefinition ReactiveCompositeDiscoveryClient_SIMPLEAPISERVICE applying {pattern=/SIMPLEAPISERVICE/**} to Path
2021-05-06 12:30:31.232 DEBUG 9524 --- [ctor-http-nio-5] o.s.c.g.r.RouteDefinitionRouteLocator    : RouteDefinition ReactiveCompositeDiscoveryClient_SIMPLEAPISERVICE applying filter {regexp=/SIMPLEAPISERVICE/?(?<remaining>.*), replacement=/${remaining}} to RewritePath
2021-05-06 12:30:31.232 DEBUG 9524 --- [ctor-http-nio-5] o.s.c.g.r.RouteDefinitionRouteLocator    : RouteDefinition matched: ReactiveCompositeDiscoveryClient_SIMPLEAPISERVICE
2021-05-06 12:30:32.389 ERROR 9524 --- [ctor-http-nio-7] a.w.r.e.AbstractErrorWebExceptionHandler : [97cf0f04-3]  500 Server Error for HTTP GET "/SIMPLEAPISERVICE"

java.net.UnknownHostException: failed to resolve 'FNPL0Z2' after 4 queries 
	at io.netty.resolver.dns.DnsResolveContext.finishResolve(DnsResolveContext.java:1013) ~[netty-resolver-dns-4.1.63.Final.jar:4.1.63.Final]
	Suppressed: reactor.core.publisher.FluxOnAssembly$OnAssemblyException: 
Error has been observed at the following site(s):
	|_ checkpoint ⇢ org.springframework.cloud.gateway.filter.WeightCalculatorWebFilter [DefaultWebFilterChain]
	|_ checkpoint ⇢ org.springframework.boot.actuate.metrics.web.reactive.server.MetricsWebFilter [DefaultWebFilterChain]
	|_ checkpoint ⇢ HTTP GET "/SIMPLEAPISERVICE" [ExceptionHandlingWebHandler]
Stack trace:
		at io.netty.resolver.dns.DnsResolveContext.finishResolve(DnsResolveContext.java:1013) ~[netty-resolver-dns-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.resolver.dns.DnsResolveContext.tryToFinishResolve(DnsResolveContext.java:966) ~[netty-resolver-dns-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.resolver.dns.DnsResolveContext.query(DnsResolveContext.java:414) ~[netty-resolver-dns-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.resolver.dns.DnsResolveContext.onResponse(DnsResolveContext.java:625) ~[netty-resolver-dns-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.resolver.dns.DnsResolveContext.access$400(DnsResolveContext.java:63) ~[netty-resolver-dns-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.resolver.dns.DnsResolveContext$2.operationComplete(DnsResolveContext.java:458) ~[netty-resolver-dns-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.util.concurrent.DefaultPromise.notifyListener0(DefaultPromise.java:578) ~[netty-common-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.util.concurrent.DefaultPromise.notifyListeners0(DefaultPromise.java:571) ~[netty-common-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.util.concurrent.DefaultPromise.notifyListenersNow(DefaultPromise.java:550) ~[netty-common-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.util.concurrent.DefaultPromise.notifyListeners(DefaultPromise.java:491) ~[netty-common-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.util.concurrent.DefaultPromise.setValue0(DefaultPromise.java:616) ~[netty-common-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.util.concurrent.DefaultPromise.setSuccess0(DefaultPromise.java:605) ~[netty-common-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.util.concurrent.DefaultPromise.trySuccess(DefaultPromise.java:104) ~[netty-common-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.resolver.dns.DnsQueryContext.trySuccess(DnsQueryContext.java:201) ~[netty-resolver-dns-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.resolver.dns.DnsQueryContext.finish(DnsQueryContext.java:193) ~[netty-resolver-dns-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.resolver.dns.DnsNameResolver$DnsResponseHandler.channelRead(DnsNameResolver.java:1264) ~[netty-resolver-dns-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.handler.codec.MessageToMessageDecoder.channelRead(MessageToMessageDecoder.java:103) ~[netty-codec-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:357) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1410) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:379) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:365) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:919) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.nio.AbstractNioMessageChannel$NioMessageUnsafe.read(AbstractNioMessageChannel.java:97) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:719) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:655) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:581) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:493) ~[netty-transport-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:989) ~[netty-common-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) ~[netty-common-4.1.63.Final.jar:4.1.63.Final]
		at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) ~[netty-common-4.1.63.Final.jar:4.1.63.Final]
		at java.base/java.lang.Thread.run(Thread.java:834) ~[na:na]


```

This has suffered from DNS resolution problem of local hostnames
Healthy route:
```log
2021-05-06 12:54:47.714 DEBUG 23616 --- [ctor-http-nio-4] o.s.c.g.h.RoutePredicateHandlerMapping   : Route matched: ReactiveCompositeDiscoveryClient_SIMPLEAPISERVICE
2021-05-06 12:54:47.714 DEBUG 23616 --- [ctor-http-nio-4] o.s.c.g.h.RoutePredicateHandlerMapping   : Mapping [Exchange: GET http://localhost:9090/SIMPLEAPISERVICE] to Route{id='ReactiveCompositeDiscoveryClient_SIMPLEAPISERVICE', uri=lb://SIMPLEAPISERVICE, order=0, predicate=Paths: [/SIMPLEAPISERVICE/**], match trailing slash: true, gatewayFilters=[[[RewritePath /SIMPLEAPISERVICE/?(?<remaining>.*) = '/${remaining}'], order = 1]], metadata={management.port=8080}}
2021-05-06 12:54:47.714 DEBUG 23616 --- [ctor-http-nio-4] o.s.c.g.h.RoutePredicateHandlerMapping   : [d6ba0f26-4] Mapped to org.springframework.cloud.gateway.handler.FilteringWebHandler@119f1f4
2021-05-06 12:54:47.714 DEBUG 23616 --- [ctor-http-nio-4] o.s.c.g.handler.FilteringWebHandler      : Sorted gatewayFilterFactories: [[GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.RemoveCachedBodyFilter@50085d9c}, order = -2147483648], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.AdaptCachedBodyGlobalFilter@b5c6a30}, order = -2147482648], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.NettyWriteResponseFilter@196624bf}, order = -1], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.ForwardPathFilter@4b425577}, order = 0], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.GatewayMetricsFilter@3bb9ca38}, order = 0], [[RewritePath /SIMPLEAPISERVICE/?(?<remaining>.*) = '/${remaining}'], order = 1], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter@5df7e31b}, order = 10000], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter@2b44d6d0}, order = 10150], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.WebsocketRoutingFilter@5934153e}, order = 2147483646], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.NettyRoutingFilter@3e900e1a}, order = 2147483647], [GatewayFilterAdapter{delegate=org.springframework.cloud.gateway.filter.ForwardRoutingFilter@63a9c661}, order = 2147483647]]
2021-05-06 12:54:47.716 DEBUG 23616 --- [ctor-http-nio-5] r.netty.http.client.HttpClientConnect    : [id:f85a99ac-2, L:/10.57.8.91:58604 - R:FNPL0Z2/10.57.8.91:8080] Handler is being applied: {uri=http://FNPL0Z2:8080/, method=GET}
2021-05-06 12:54:47.720 DEBUG 23616 --- [ctor-http-nio-5] r.n.http.client.HttpClientOperations     : [id:f85a99ac-2, L:/10.57.8.91:58604 - R:FNPL0Z2/10.57.8.91:8080] Received response (auto-read:false) : [Content-Type=application/json, Transfer-Encoding=chunked, Date=Thu, 06 May 2021 10:54:47 GMT]
2021-05-06 12:54:47.721 DEBUG 23616 --- [ctor-http-nio-5] r.n.http.client.HttpClientOperations     : [id:f85a99ac-2, L:/10.57.8.91:58604 - R:FNPL0Z2/10.57.8.91:8080] Received last HTTP packet
```

The whole Gateway uses reactive patterns, so it would be good studying them upfront.

it's built on https://projectreactor.io/

Writing filters:
https://www.baeldung.com/spring-cloud-custom-gateway-filters

This is how a request through looks on API:
```json
{
  "user-agent": "insomnia/2021.3.0",
  "accept": "*/*",
  "customheader": "customvalue",
  "forwarded": "proto=http;host=\"localhost:9090\";for=\"0:0:0:0:0:0:0:1:54246\"",
  "x-forwarded-for": "0:0:0:0:0:0:0:1",
  "x-forwarded-proto": "http",
  "x-forwarded-prefix": "/SIMPLEAPISERVICE",
  "x-forwarded-port": "9090",
  "x-forwarded-host": "localhost:9090",
  "host": "FNPL0Z2:8081",
  "content-length": "0"
}
```


Circuit breaker
https://piotrminkowski.com/2019/12/11/circuit-breaking-in-spring-cloud-gateway-with-resilience4j/

https://piotrminkowski.com/2020/02/23/timeouts-and-retries-in-spring-cloud-gateway/

Routes with CircuitBreaker Filter
```json
[
  {
    "predicate": "Paths: [/GATEWAY/**], match trailing slash: true",
    "metadata": {
      "management.port": "9090"
    },
    "route_id": "ReactiveCompositeDiscoveryClient_GATEWAY",
    "filters": [
      "[[SpringCloudCircuitBreakerResilience4JFilterFactory name = 'GATEWAY', fallback = [null]], order = 1]",
      "[[RewritePath /GATEWAY/?(?<remaining>.*) = '/${remaining}'], order = 2]"
    ],
    "uri": "lb://GATEWAY",
    "order": 0
  },
  {
    "predicate": "Paths: [/DISCOVERY/**], match trailing slash: true",
    "metadata": {
      "management.port": "8761"
    },
    "route_id": "ReactiveCompositeDiscoveryClient_DISCOVERY",
    "filters": [
      "[[SpringCloudCircuitBreakerResilience4JFilterFactory name = 'DISCOVERY', fallback = [null]], order = 1]",
      "[[RewritePath /DISCOVERY/?(?<remaining>.*) = '/${remaining}'], order = 2]"
    ],
    "uri": "lb://DISCOVERY",
    "order": 0
  }
]
```


Rate Limiting:
is realized with Filter, requires Redis
The Redis implementation is based off of work done at Stripe. It requires the use of the spring-boot-starter-data-redis-reactive Spring Boot starter.

```yaml
spring:
  cloud:
    gateway:
      routes:
      - id: requestratelimiter_route
        uri: https://example.org
        filters:
        - name: RequestRateLimiter
          args:
            redis-rate-limiter.replenishRate: 10
            redis-rate-limiter.burstCapacity: 20
            redis-rate-limiter.requestedTokens: 1
```


Spring cloud load balancer

Basic service list for LB: ServiceInstanceListSupplier, retrieves instances every time.

Recomended to use cache backed implementation, but with Eureka discovery client I believe that is void. The call will be made in-memory against the discovery client's cache.

Load balancer can do healthchecks, which might be a great thing for static services
Static services: at the moment in Discovery, move to Gateway? Pros Cons?
Otherwise not necessary with propper DiscoveryClient


Sticky sessions:

Request-based Sticky Session for LoadBalancer

has to have `RequestBasedStickySessionServiceInstanceListSupplier` configured
A session cookie based implementation of ServiceInstanceListSupplier that gives preference to the instance with an id specified in a request cookie.
private String instanceIdCookieName = "sc-lb-instance-id";
Works on cookie
List of service instances `get(Request req)` needs request
The supplier is a wrapper on top of delegate, which provides all instances.


Hint-Based Load-Balancing

hints are `String` property, global default and per service values possible
HintBasedServiceInstanceListSupplier checks for a hint request header (the default header-name is `X-SC-LB-Hint`)
It can also come from the `hint` key in service `metadataMap`, where we can put it with Filter.


After load balancing, the reuqest can be further altered, but the implementation has to be client-specific. 
But it is easily pluggable.
