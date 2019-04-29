package com.lct;

import com.mongodb.*;

/**
 * Created by Administrator on 2018/9/15 0015.
 * java 代码 操作  mongodb 数据库
 */
public class MongoDBDaoImpl {

    /**
     * 连接没有进行安全认证的 MongoDB 服务器
     *
     * @return
     */
    public static MongoClient getMongoClientNoCheck() {
        /**MongoClient 是线程安全的，可以在多个线程中共享同一个实例*/
        MongoClient mongoClient = null;
        try {
            /** new MongoClient 创建客户端的时候，可以传入 MongoClientOptions 客户端配置选项
             * 所以可以将设置全部事先设置好
             */
            MongoClientOptions.Builder build = new MongoClientOptions.Builder();
            /**与目标数据库能够建立的最大连接数为50*/
            build.connectionsPerHost(50);

            /**如果当前所有的连接都在使用中，则每个连接上可以有50个线程排队等待*/
            build.threadsAllowedToBlockForConnectionMultiplier(50);

            /**一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为，此处为 2分钟
             * 如果超过 maxWaitTime 都没有获取到连接的话，该线程就会抛出 Exception
             * */
            build.maxWaitTime(1000 * 60 * 2);

            /**设置与数据库建立连接时最长时间为1分钟*/
            build.connectTimeout(1000 * 60 * 1);
            MongoClientOptions mongoClientOptions = build.build();

            /** 将 MongoDB 服务器的 ip 与端口先封装好
             * 连接 MongoDB 服务端地址，实际项目中应该放到配置文件进行配置
             * */
            ServerAddress serverAddress = new ServerAddress("localhost", 27017);

            /**
             * 通过 ServerAddress 与 MongoClientOptions 创建连接到 MongoDB 的数据库实例
             * MongoClient(String host, int port)：
             *      1）host：MongoDB 服务端 IP
             *      2）port：MongoDB 服务端 端口，默认为 27017
             *      3）即使 MongoDB 服务端关闭，此时也不会抛出异常，只有到真正调用方法是才会
             *      4）连接 MongoDB 服务端地址，实际项目中应该放到配置文件进行配置
             * MongoClient(final ServerAddress addr, final MongoClientOptions options)
             * 重载了很多构造方法，这只是其中两个
             *      */
            mongoClient = new MongoClient(serverAddress, mongoClientOptions);
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return mongoClient;
    }

    /**
     * 连接进行安全认证的 MongoDB 服务器，此时需要验证账号密码
     * 1）注意：如果 MongoDB 服务器为开启安全认证，则即使连接的时候使用了 账号密码，则也不受影响，同样成功
     *
     * @return
     */
    public static MongoClient getMongoClientCheck() {
        MongoClient mongoClient = null;
        try {

            /** new MongoClient 创建客户端的时候，可以传入 MongoClientOptions 客户端配置选项
             * 所以可以将设置全部事先设置好
             */
            MongoClientOptions.Builder build = new MongoClientOptions.Builder();
            /**与目标数据库能够建立的最大连接数为50*/
            build.connectionsPerHost(50);

            /**如果当前所有的连接都在使用中，则每个连接上可以有50个线程排队等待*/
            build.threadsAllowedToBlockForConnectionMultiplier(50);

            /**一个线程访问数据库的时候，在成功获取到一个可用数据库连接之前的最长等待时间为，此处为 2分钟
             * 如果超过 maxWaitTime 都没有获取到连接的话，该线程就会抛出 Exception
             * */
            build.maxWaitTime(1000 * 60 * 2);

            /**设置与数据库建立连接时最长时间为1分钟*/
            build.connectTimeout(1000 * 60 * 1);
            MongoClientOptions mongoClientOptions = build.build();

            /** 将 MongoDB 服务器的 ip 与端口先封装好
             * 连接 MongoDB 服务端地址，实际项目中应该放到配置文件进行配置
             * */
            ServerAddress serverAddress = new ServerAddress("localhost", 27017);

            /** MongoCredential：表示 MongoDB 凭据、证书
             * createScramSha1Credential(final String userName, final String source, final char[] password)
             *      1）userName：登录的用户名
             *      2）source：用户需要验证的数据库名称，注意账号当时在哪个数据库下创建，则此时就去哪个库下面进行验证，否则即使账号密码正确也无济于事
             *      3）password：用户的密码
             *      4）实际开发中也应该放到配置文件中进行配置
             * 同理还有：
             * createCredential(final String userName, final String database, final char[] password)
             * createScramSha256Credential(final String userName, final String source, final char[] password)
             * createMongoCRCredential(final String userName, final String database, final char[] password)
             * createMongoX509Credential(final String userName)
             * createMongoX509Credential()
             * createPlainCredential(final String userName, final String source, final char[] password)
             * createGSSAPICredential(final String userName)
             * A、如果 MongoDB 服务端未开启安全认证，这里设置的账号密码连接时也不受影响，同样连接成功
             * B、如果 MongoDB 服务端开启了安全认证，但是账号密码是错误的，则此时不会里面抛异常，等到正在 CRUD 时就会抛异常：Exception authenticating
             * C、如下所示，这是事项在 admin 数据库中创建好的 管理员账号 root
             */
            MongoCredential credential = MongoCredential.createCredential(
                    "root", "admin", "root".toCharArray());
            /** MongoClient(final ServerAddress addr, final MongoCredential credential, final MongoClientOptions options)
             * 1）addr：MongoDB 服务器地址
             * 2）credential：MongoDB 安全认证证书
             * 3）options：MongoDB 客户端配置选项
             */
            mongoClient = new MongoClient(serverAddress, credential, mongoClientOptions);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return mongoClient;
    }

    /**
     * 获取 MongoDB 的一些信息
     */
    public static void showMongoDBInfo() {
        /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
         * 实际开发中应该将 MongoDB 服务器地址配置在配置文件中*/
        MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);
        ServerAddress serverAddress = mongoClient.getAddress();
        System.out.println("serverAddress>>>" + serverAddress);//输出：127.0.0.1:27017

        String connectPoint = mongoClient.getConnectPoint();
        System.out.println("connectPoint>>>" + connectPoint);

        int bsonObjectSize = mongoClient.getMaxBsonObjectSize();
        System.out.println("bsonObjectSize>>>" + bsonObjectSize);

        mongoClient.close();
    }

    public static void main(String[] args) {

    }
}
