### 1. Connection handler 在哪设置的值?
HttpClientConnection 设置了失败的情况, 也把他作为返回值
在 MysqlConnection 里, 这个值是在 OK 里设置的


### 2. ConnectionPool
Connection 已经完成, 要开始搞 ConnectionPool 了, MySQLConnectionPool 里只有一个 Connection, 并发的请求会放到队列里
每个 Connection 对应于一个 ConnectionID, 有一个总控部件, 维护 (ConnectionId, Promise) 的序列, 当 Promise ready 时,
返回给 client

问题

1. connection 的空闲或者繁忙状态怎么能让外面的 wrapper 知道呢?
2. 请求队列应该放在哪?