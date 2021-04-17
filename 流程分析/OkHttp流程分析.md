# OkHttp流程分析

在分析之前，先写一段没有任何封装的请求代码

````
String url = "http://wwww.baidu.com";
OkHttpClient okHttpClient = new OkHttpClient();
final Request request = new Request.Builder()
        .url(url)
        .get()//默认就是GET请求，可以不写
        .build();
Call call = okHttpClient.newCall(request);
call.enqueue(new Callback() {
    @Override
    public void onFailure(Call call, IOException e) {
        Log.d(TAG, "onFailure: ");
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Log.d(TAG, "onResponse: " + response.body().string());
    }
});

````

在上述代码中可以看到几个关键类和作用

- OkHttpClient : 用于创建Call对象，源码注释是Factory for {@linkplain Call calls}
- Request ：构建我们的请求。源码注释是An HTTP request。简单直接
- Call ：发起我们的http请求

开始分析

## OkHttpClient和Request

先看看我们的 OkHttpClient Request

````
OkHttpClient.java

public OkHttpClient() {
   this(new Builder());
}
````

我们跟进代码可以看到OkHttpClient是一个构建者模式。Request不跟进也可以看到是一个构造者模式。

接下来，我们继续看Call

## Call

````
//OkHttpClient.java
Call newCall(Request request) {
    //此处传入我们的OkHttpClient
    return RealCall.newRealCall(this, request, false);
}

//RealCall.java
RealCall newRealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket) {
    RealCall call = new RealCall(client, originalRequest, forWebSocket);
    //Transmitter意为发射器，功能挺杂。注释说是OkHttp的应用程序和网络层之间的桥梁
    call.transmitter = new Transmitter(client, call);
    return call;
}

````
在这段代码中，我们可以看到我们得到的Call的实例是RealCall。所以我们enqueue（）方法的调用实际是RealCall的方法。

````
  @Override public void enqueue(Callback responseCallback) {
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }
    transmitter.callStart();
    //从我们之前传入OkHttpClient中获取其中的Dispatcher
    client.dispatcher().enqueue(new AsyncCall(responseCallback));
  }
````

注意这里有一个Dispatcher。这个类负责我们网络请求的调度策略。在这段代码中我们用AsyncCall封装了我们的Callback。AsyncCall 是一个NamedRunnable。在这个NamedRunnable中封装了我们的execute()

````
public abstract class NamedRunnable implements Runnable {
  protected final String name;

  public NamedRunnable(String format, Object... args) {
    this.name = Util.format(format, args);
  }

  @Override public final void run() {
    String oldName = Thread.currentThread().getName();
    Thread.currentThread().setName(name);
    try {
      execute();
    } finally {
      Thread.currentThread().setName(oldName);
    }
  }

  protected abstract void execute();
}

````

execute()的具体实现如下

````
    @Override protected void execute() {
      boolean signalledCallback = false;
      transmitter.timeoutEnter();
      try {
        //此处得到我们的需要的网络数据
        Response response = getResponseWithInterceptorChain();
        signalledCallback = true;
        responseCallback.onResponse(RealCall.this, response);
      } catch (IOException e) {
        if (signalledCallback) {
          // Do not signal the callback twice!
          Platform.get().log(INFO, "Callback failure for " + toLoggableString(), e);
        } else {
          // 此处是请求失败回调
          responseCallback.onFailure(RealCall.this, e);
        }
      } catch (Throwable t) {
        cancel();
        if (!signalledCallback) {
          IOException canceledException = new IOException("canceled due to " + t);
          canceledException.addSuppressed(t);
          responseCallback.onFailure(RealCall.this, canceledException);
        }
        throw t;
      } finally {
        client.dispatcher().finished(this);
      }
    }
````
在上面这个方法中我们可以看到请求的结果是如何获得，那么这个runnable任务是怎么调度执行的呢？我们就得回过头看看Dispatcher类的enqueue()方法做了啥

````
  void enqueue(AsyncCall call) {
    synchronized (this) {
      readyAsyncCalls.add(call);

      if (!call.get().forWebSocket) {
        //如果有请求同一host的AsyncCall就进行复用
        AsyncCall existingCall = findExistingCallWithHost(call.host());
        if (existingCall != null) call.reuseCallsPerHostFrom(existingCall);
      }
    }
    推动执行方法
    promoteAndExecute();
  }

  private boolean promoteAndExecute() {
    assert (!Thread.holdsLock(this));

    List<AsyncCall> executableCalls = new ArrayList<>();
    boolean isRunning;
    synchronized (this) {
      //循环收集可以执行的AsyncCall
      for (Iterator<AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
        AsyncCall asyncCall = i.next();

        if (runningAsyncCalls.size() >= maxRequests) break; // 最大执行的call大于64 跳出
        if (asyncCall.callsPerHost().get() >= maxRequestsPerHost) continue; // 请求同一host的call > 5 跳过 

        i.remove();
        asyncCall.callsPerHost().incrementAndGet();
        executableCalls.add(asyncCall);
        runningAsyncCalls.add(asyncCall);
      }
      isRunning = runningCallsCount() > 0;
    }

    //把可以执行的AsyncCall，统统执行
    for (int i = 0, size = executableCalls.size(); i < size; i++) {
      AsyncCall asyncCall = executableCalls.get(i);
      //executorService()返回一个线程池
      asyncCall.executeOn(executorService());
    }

    return isRunning;
  }

void executeOn(ExecutorService executorService) {
    boolean success = false;
    try {
        //线程池运行Runnable，执行run，调用前面提到的AsyncCall.execute
        executorService.execute(this);
        success = true;
    } catch (RejectedExecutionException e) {
        InterruptedIOException ioException = new InterruptedIOException("executor rejected");
        ioException.initCause(e);
        transmitter.noMoreExchanges(ioException);
        //失败回调
        responseCallback.onFailure(RealCall.this, ioException);
    } finally {
        if (!success) {
            //结束
            client.dispatcher().finished(this);
        }
    }
}


````
到了这里我们的AsyncCall就顺利执行了。整个Okhttp代码的主干就在这里。接下来我们继续探讨okhttp这个框架的一些细节设计

## 拦截器链

前边得到Response的地方，我们调用了getResponseWithInterceptorChain()。但没细讲，这个部分就是大名鼎鼎的okhttp的拦截器链。

````
//RealCall.java
Response getResponseWithInterceptorChain() throws IOException {
    List<Interceptor> interceptors = new ArrayList<>();
    //添加自定义拦截器
    interceptors.addAll(client.interceptors());
    //添加默认拦截器
    interceptors.add(new RetryAndFollowUpInterceptor(client));
    interceptors.add(new BridgeInterceptor(client.cookieJar()));
    interceptors.add(new CacheInterceptor(client.internalCache()));
    interceptors.add(new ConnectInterceptor(client));
    if (!forWebSocket) {
        //添加自定义网络拦截器（在ConnectInterceptor后面，此时网络连接已准备好）
        interceptors.addAll(client.networkInterceptors());
    }
    //添加默认拦截器，共4+1=5个
    interceptors.add(new CallServerInterceptor(forWebSocket));
    //创建拦截器链
    Interceptor.Chain chain =
        new RealInterceptorChain(interceptors, transmitter, null, 0,
                                 originalRequest, this, client.connectTimeoutMillis(),
                                 client.readTimeoutMillis(), client.writeTimeoutMillis());
    //放行
    Response response = chain.proceed(originalRequest);
    return response;
}

````

我们跟进proceed方法 

````

Response proceed(Request request, Transmitter transmitter,Exchange exchange)
    throws IOException {
    //传入index + 1，可以访问下一个拦截器
    RealInterceptorChain next = 
        new RealInterceptorChain(interceptors, transmitter, exchange,
                                 index + 1, request, call, connectTimeout, 
                                 readTimeout, writeTimeout);
    Interceptor interceptor = interceptors.get(index);
    //执行第一个拦截器，同时传入next
    Response response = interceptor.intercept(next);
    //等所有拦截器处理完，就能返回Response了
    return response;
}


````
然后简单介绍一下各个拦截器的作用

- RetryAndFollowUpInterceptor ：负责重定向和重试
- BridgeInterceptor ：负责把应用请求变成网络请求，把网络响应变成应用响应
- CacheInterceptor ：负责okio读写缓存
- ConnectInterceptor ： 负责创建连接
- CallServerInterceptor ：负责写请求和读响应

## 缓存

缓存的实现是基于请求和响应的header来做的。CacheStrategy即缓存策略，CacheInterceptor拦截器会根据他拿到网络请求networkRequest、缓存响应cacheResponse，从而决定是使用网络还是缓存。由于是根据请求和响应的header来做。所以要想使用okhttp来做缓存的话。需要公司的后台同学配合。

## 连接池

我们先看到Transmitter这个类，Transmitter意为发射器，功能挺杂。注释说是OkHttp的应用程序和网络层之间的桥梁。newRealCall方法中有用到。我们走近这个类。Transmitter中我们可以看到RealConnectionPool 这个类。这个类就是我们的连接池了。

````
//RealConnectionPool.java
//线程池，用于清理过期的连接。一个连接池最多运行一个线程
Executor executor =
    new ThreadPoolExecutor(0,Integer.MAX_VALUE,60L,TimeUnit.SECONDS,
                           new SynchronousQueue<>(), 
                           Util.threadFactory("OkHttp ConnectionPool", true));
//每个ip地址的最大空闲连接数，为5个
int maxIdleConnections;
//空闲连接存活时间，为5分钟
long keepAliveDurationNs;
//连接队列
Deque<RealConnection> connections = new ArrayDeque<>();

//获取连接
boolean transmitterAcquirePooledConnection(Address address, Transmitter transmitter,
                                           List<Route> routes, boolean requireMultiplexed) {
    for (RealConnection connection : connections) {
        //要求多路复用，跳过不支持多路复用的连接
        if (requireMultiplexed && !connection.isMultiplexed()) continue;
        //不合条件，跳过
        if (!connection.isEligible(address, routes)) continue;
        //分配一个连接
        transmitter.acquireConnectionNoEvents(connection);
        return true;
    }
    return false;
}

//移除连接，executor运行cleanupRunnable，调用了该方法
long cleanup(long now) {
    //查找移除的连接，或下一次移除的时间
    synchronized (this) {
        for (Iterator<RealConnection> i = connections.iterator(); i.hasNext(); ) {
            //...
            if (idleDurationNs > longestIdleDurationNs) {
                longestIdleDurationNs = idleDurationNs;
                longestIdleConnection = connection;
            }
        }
        if (longestIdleDurationNs >= this.keepAliveDurationNs
            || idleConnectionCount > this.maxIdleConnections) {
            //移除连接
            connections.remove(longestIdleConnection);
        }
    }
    //关闭Socket
    closeQuietly(longestIdleConnection.socket());
}

````

