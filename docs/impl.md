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


### 3. 关于 ConnectionPool 是否要直接继承 AsyncObject 的问题
mysql-async 的方式是没有直接继承, 这样的好处是 Postgresql 和 Mysql 可以分别有自己的实现
ConnectionPool 并没有两种不同的实现, 所以这个理由不足以反对ConnectionPool 直接继承 AsyncObject
但是有一点, MysqlConnectionPool 比 ObjectPool 多了几个方法, 这些方法是 SendQuery
也就是说, ConnectionPool 完全可以按照 Connection 的用法来用, 所以 ConnectionPool 既是 Connection 又是
ObjectPool, 从这个角度来讲, 应该把 ConnectionPool 和 ObjectPool 分开