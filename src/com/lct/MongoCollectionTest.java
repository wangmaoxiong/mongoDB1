package com.lct;

import com.mongodb.MongoClient;
import com.mongodb.client.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/16.
 */
public class MongoCollectionTest {

    /**
     * 显示的为指定数据库创建集合
     *
     * @param databaseName   数据库名称，如 java
     * @param collectionName 集合名词，如 c1
     */
    public static void createCollectionByShow(String databaseName, String collectionName) {
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**createCollection(String var1)：显示创建集合，此时 java 数据库下会立即创建 c1 集合
             * 注意如果 数据库中已经存在此 集合，则会抛出异常： already exists'
             *
             * 执行完成后，cmd 客户端可以用命令查看：
             * > show dbs
             admin   0.000GB
             config  0.000GB
             java    0.000GB
             local   0.000GB
             > use java
             switched to db java
             > show tables
             c1
             * */
            mongoDatabase.createCollection(collectionName);

            /**关闭 MongoDB 客户端连接，释放资源*/
            mongoClient.close();
        }
    }


    /**
     * 获取指定数据库下的指定集合
     *
     * @param databaseName   数据库名称
     * @param collectionName 获取的集合名称
     */
    public static MongoCollection<Document> getCollection(String databaseName, String collectionName) {
        MongoCollection<Document> mongoCollection = null;
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**获取数据库中的集合
             * 如果集合不存在，则返回的 MongoCollection<Document> 文档个数为0，不会为 null*/
            mongoCollection = mongoDatabase.getCollection(collectionName);

            Long documentSize = mongoCollection.countDocuments();
            System.out.println("集合中文档总数：documentSize >>> " + documentSize);
        }
        return mongoCollection;
    }


    /**
     * 获取指定数据库下的所有集合名称
     *
     * @param databaseName 数据库名称
     * @return 返回集合名称的列表
     */
    public static List<String> getAllCollectionNames(String databaseName) {
        List<String> collectionNameList = new ArrayList<String>();
        if (databaseName != null && !"".equals(databaseName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /** listCollectionNames()：获取整个数据库下集合名称列表*/
            MongoIterable<String> mongoIterable = mongoDatabase.listCollectionNames();

            /** first()：获取数据库下第一个集合名称
             * 不存在时返回 null */
            String first = mongoIterable.first();
            System.out.println("first collectionName>>>" + first);

            /**获取它的游标进行迭代集合名称
             * 数据库下没有集合时，则大小为空，不会为 null
             * 如控制台输出：
             * first collectionName>>>c2
             collectionName >>>c2
             collectionName >>>c3
             collectionName >>>c4
             collectionName >>>c1
             * */
            MongoCursor<String> mongoCursor = mongoIterable.iterator();
            while (mongoCursor.hasNext()) {
                String collectionName = mongoCursor.next();
                System.out.println("collectionName >>>" + collectionName);
                collectionNameList.add(collectionName);
            }
            /**关闭游标*/
            mongoCursor.close();

            /**关闭 MongoDB 客户端连接*/
            mongoClient.close();
        }
        return collectionNameList;
    }


    /**
     * 获取指定数据库下的所有集合
     *
     * @param databaseName 数据库名称
     * @return 返回集合的 Document 对象，此对象包含集合的完整信息
     */
    public static List<Document> getAllCollection(String databaseName) {
        List<Document> collectionList = new ArrayList<Document>();
        if (databaseName != null && !"".equals(databaseName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /** mongoDatabase.listCollections()：获取当前数据库下所有的集合列表
             * ListCollectionsIterable<Document> 与 MongoCollection<Document> 不同
             */
            ListCollectionsIterable<Document> collectionsIterable = mongoDatabase.listCollections();

            /**
             * collectionsIterable.first()：返回第一个集合的 Document 对象，包含整个集合信息，如下所示：
             * document >>>{ "name" : "c2", "type" : "collection", "options" : { },
             * "info" : { "readOnly" : false, "uuid" : { "$binary" : "mkVMeWvV9eGfA5ZOkkYhqw==", "$type" : "03" } },
             * "idIndex" : { "v" : 2, "key" : { "_id" : 1 }, "name" : "_id_", "ns" : "java.c2" } }
             */
            Document firstDocument = collectionsIterable.first();
            System.out.println("first collection >>>" + firstDocument.toJson());

            /**
             * collectionsIterable.iterator() 获取 MongoCursor<Document> 游标
             * 同样其中的 Document 包含集合的详细信息，如下所示：
             * first collection >>>{ "name" : "c2", "type" : "collection", "options" : { }, "info" : { "readOnly" : false, "uuid" : { "$binary" : "mkVMeWvV9eGfA5ZOkkYhqw==", "$type" : "03" } }, "idIndex" : { "v" : 2, "key" : { "_id" : 1 }, "name" : "_id_", "ns" : "java.c2" } }
             loop collection >>>{ "name" : "c2", "type" : "collection", "options" : { }, "info" : { "readOnly" : false, "uuid" : { "$binary" : "mkVMeWvV9eGfA5ZOkkYhqw==", "$type" : "03" } }, "idIndex" : { "v" : 2, "key" : { "_id" : 1 }, "name" : "_id_", "ns" : "java.c2" } }
             loop collection >>>{ "name" : "c3", "type" : "collection", "options" : { }, "info" : { "readOnly" : false, "uuid" : { "$binary" : "dkX79PJ6xdFSEKvqtVrnuA==", "$type" : "03" } }, "idIndex" : { "v" : 2, "key" : { "_id" : 1 }, "name" : "_id_", "ns" : "java.c3" } }
             loop collection >>>{ "name" : "c4", "type" : "collection", "options" : { }, "info" : { "readOnly" : false, "uuid" : { "$binary" : "pEOe5CziXX6fnbp/juiHgA==", "$type" : "03" } }, "idIndex" : { "v" : 2, "key" : { "_id" : 1 }, "name" : "_id_", "ns" : "java.c4" } }
             loop collection >>>{ "name" : "c1", "type" : "collection", "options" : { }, "info" : { "readOnly" : false, "uuid" : { "$binary" : "REMR6xJJls5e92eKGlR0pg==", "$type" : "03" } }, "idIndex" : { "v" : 2, "key" : { "_id" : 1 }, "name" : "_id_", "ns" : "java.c1" } }
             */
            MongoCursor<Document> mongoCursor = collectionsIterable.iterator();
            while (mongoCursor.hasNext()) {
                Document loopDocument = mongoCursor.next();
                System.out.println("loop collection >>>" + loopDocument.toJson());
                collectionList.add(loopDocument);
            }
            /**关闭游标*/
            mongoCursor.close();
            /**关闭 MongoDB 客户端连接*/
            mongoClient.close();
        }
        return collectionList;
    }


    /**
     * 删除指定数据库下的指定集合，如果数据库中不存在此集合，则不会做任何处理
     *
     * @param databaseName   数据库名称
     * @param collectionName 获取的集合名称
     */
    public static void delCollection(String databaseName, String collectionName) {
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**获取数据库中的集合*/
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);

            /**删除当前集合，如果集合不存在，则不做任何处理，不会抛异常*/
            mongoCollection.drop();
            mongoClient.close();
        }
    }

}
