package com.lct;

import com.mongodb.MongoClient;
import com.mongodb.client.ListDatabasesIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/16.
 */
public class MongoDBTest {

    /**
     * 删除指定数据库
     *
     * @param databaseName 数据库名称
     */
    public static void dropDatabase(String databaseName) {
        if (databaseName != null && !"".equals(databaseName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             *
             * 注意 MongoDatabase 相当于一个 MongoDB 连接，连接可以有多个
             * MongoClient 相当于一个客户端，客户端可以只有一个，也可有多个
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**删除当前所在的数据库
             * 1）即使数据库中有集合，集合中有文档，整个数据库都会删除，show dbs 不会再有
             * 2）如果待删除的数据库实际没有存在，即 show dbs 看不到，也不影响，不抛异常
             *
             * 也可以使用 MongoClient 的 dropDatabase(String dbName) 方法进行删除
             */
            mongoDatabase.drop();

            /**关闭 MongoDB 客户端连接，释放资源*/
            mongoClient.close();
        }
    }

    /**
     * 删除指定数据库
     *
     * @param databaseName 数据库名称
     */
    public static void delDatabase(String databaseName) {
        if (databaseName != null && !"".equals(databaseName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /** dropDatabase(String dbName) :删除 MongoDB 下的指定数据库
             * 数据库中内容全部会被删除，show dbs 也不会再有
             *
             * 也可以使用 MongoDatabase 的 drop() 方法，删除当前数据库
             * */
            mongoClient.dropDatabase(databaseName);

            /**关闭 MongoDB 客户端连接，释放资源*/
            mongoClient.close();
        }
    }

    /**
     * 获取 MongoDB 服务端所有数据库名字
     *
     * @return
     */
    public static List<String> findAllDBNames() {
        List<String> dbNameList = new ArrayList<String>();
        /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
         * 实际开发中应该将 MongoDB 服务器地址配置在配置文件中*/
        MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

        /**getDatabase(String databaseName)：获取指定的数据库
         * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
         * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
         * listDatabaseNames()：获取 MongoDB 服务端所有数据库
         * 先返回 迭代器 MongoIterable，在根据迭代器获取 游标 MongoCursor
         * 最后便利游标进行取值
         * */
        MongoIterable<String> mongoIterable = mongoClient.listDatabaseNames();
        MongoCursor<String> mongoCursor = mongoIterable.iterator();
        while (mongoCursor.hasNext()) {
            String dbName = mongoCursor.next();
            System.out.println("dbName>>>" + dbName);
            dbNameList.add(dbName);
        }
        /**
         * 控制台输出示例：
         * dbName>>>admin
         dbName>>>config
         dbName>>>java
         dbName>>>local
         */
        mongoClient.close();
        return dbNameList;
    }

    /**
     * 获取 MongoDB 服务端所有数据库 文档对象，如：
     * { "name" : "admin", "sizeOnDisk" : 32768.0, "empty" : false }
     * { "name" : "config", "sizeOnDisk" : 73728.0, "empty" : false }
     * { "name" : "java", "sizeOnDisk" : 204800.0, "empty" : false }
     * { "name" : "local", "sizeOnDisk" : 32768.0, "empty" : false }
     *
     * @return ：返回的 Document 包含了数据库的详细信息
     */
    public static List<Document> findAllDBs() {
        List<Document> dbList = new ArrayList<Document>();
        /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
         * 实际开发中应该将 MongoDB 服务器地址配置在配置文件中*/
        MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

        /**getDatabase(String databaseName)：获取指定的数据库
         * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
         * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
         * listDatabaseNames()：获取 MongoDB 服务端所有数据库
         * 先返回 迭代器 MongoIterable，在根据迭代器获取 游标 MongoCursor
         * 最后便利游标进行取值
         * mongoClient.listDatabases()：原理同上，只是返回的最终结果不是 String,而是 Document
         * */

        ListDatabasesIterable<Document> databasesIterable = mongoClient.listDatabases();
        MongoCursor<Document> mongoCursor = databasesIterable.iterator();
        while (mongoCursor.hasNext()) {
            Document db = mongoCursor.next();
            System.out.println(db.toJson());
            dbList.add(db);
        }
        /**
         * 输出内容示例：
         * { "name" : "admin", "sizeOnDisk" : 32768.0, "empty" : false }
         { "name" : "config", "sizeOnDisk" : 73728.0, "empty" : false }
         { "name" : "java", "sizeOnDisk" : 204800.0, "empty" : false }
         { "name" : "local", "sizeOnDisk" : 32768.0, "empty" : false }
         */
        mongoClient.close();
        return dbList;
    }


    public static void main(String[] args) {
//        findAllDBs();
//        delDatabase("food");

//        findAllDBNames();
    }
}
