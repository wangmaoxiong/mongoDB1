package com.lct;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Administrator on 2018/9/16.
 */
public class MongoDocumentTest {

    /**
     * 为指定数据库下的指定集合插入单个文档
     * 实际开发中 文档应该尽量封装成 POJO 进行传输，本文纯粹为了演示简单，所以直接写了方法里面
     *
     * @param databaseName   ：数据库名称，如果不存在，则会隐式创建
     * @param collectionName ：集合名称，如果不存在，则会隐式创建
     */
    public static void insertSingleDocument(String databaseName, String collectionName) {
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**获取数据库中的集合
             * 如果集合不存在，则会隐式创建，此时在内存中，MongoDB 客户端 show tables 看不到
             * 如果继续往集合插入值，则会真实写入磁盘中，show tables 会有值*/
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);

            /**
             * org.bson.Document implements Map<String, Object>, Serializable, Bson
             * Document 是 MongoDB 集合中的文档 对象，构造方法如下：
             * 1、Document()
             * 2、Document(String key, Object value) ——构造方法直接赋值
             * 3、Document(Map<String, Object> map)
             * 4、文档中可以再内嵌文档
             */
            Document document = new Document("name", "KOKO");
            /** Object put(String key, Object value) :为文档添加值
             * Document append(String key, Object value)：为文档追加值，返回的仍是源文档
             */
            document.put("age", 28);
            document.append("desc", "中国").append("price", 5588.00);

            /** insertOne(TDocument var1)：为集合添加单个文档
             * insertMany(List<? extends TDocument> var1) :同时添加多个文档
             * 还以其它一些重载的方法
             * insertXxx 方法执行完，MongoDB 数据库就已经有数据了
             */
            mongoCollection.insertOne(document);

            mongoClient.close();
        }
    }

    /**
     * 为指定数据下的指定集合插入多个文档
     * 1、为了演示方便，直接将 文档数据写死在了方法中，实际应用中应该尽量封装成 POJO 进行传递
     *
     * @param databaseName   ：数据库名称，如果不存在，则会隐式创建，如 java
     * @param collectionName ：集合名称，如果不存在，则会隐式创建，如 c4
     */
    public static void insertManyDocument(String databaseName, String collectionName) {
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
             * 实际应用中 MongoDB 服务器地址应该在配置文件中进行配置，不能写死*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**获取数据库中的集合
             * 如果集合不存在，则会隐式创建，此时在内存中，MongoDB 客户端 show tables 看不到
             * 如果继续往集合插入值，则会真实写入磁盘中，show tables 会有值*/
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);

            /**
             * org.bson.Document implements Map<String, Object>, Serializable, Bson
             * Document 是 MongoDB 集合中的文档 对象，构造方法如下：
             * 1、Document()
             * 2、Document(String key, Object value)
             * 3、Document(Map<String, Object> map)
             */
            List<Document> documents = new ArrayList<Document>();
            for (int i = 0; i < 5; i++) {
                Document document = new Document("name", "COCO");
                document.put("age", i);
                documents.add(document);
            }

            /** insertOne(TDocument var1)：为集合添加单个文档
             * insertMany(List<? extends TDocument> var1) :同时添加多个文档
             * 还以其它一些重载的方法
             * insertXxx 方法执行完，MongoDB 数据库就已经有数据了，使用 命令行可以查看：
             * > db.c4.find()
             { "_id" : ObjectId("5b9db3fe27df231b5cdc4784"), "name" : "KOKO", "age" : 28 }
             { "_id" : ObjectId("5b9db40f27df23032cc898f5"), "name" : "COCO", "age" : 0 }
             { "_id" : ObjectId("5b9db40f27df23032cc898f6"), "name" : "COCO", "age" : 1 }
             { "_id" : ObjectId("5b9db40f27df23032cc898f7"), "name" : "COCO", "age" : 2 }
             { "_id" : ObjectId("5b9db40f27df23032cc898f8"), "name" : "COCO", "age" : 3 }
             { "_id" : ObjectId("5b9db40f27df23032cc898f9"), "name" : "COCO", "age" : 4 }
             >
             */
            mongoCollection.insertMany(documents);

            /**关闭 MongoDB 客户端连接，释放资源*/
            mongoClient.close();
        }
    }

    /**
     * 更新指定数据库下指定集合中的特定文档
     * 为了演示简单，更新的文档直接写死在方法中了
     *
     * @param databaseName   ：数据库名
     * @param collectionName ：数据库下的集合名
     */
    public static void updateDocuments(String databaseName, String collectionName) {
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
             * 实际应用中 MongoDB 地址应该配置在配置文件中*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**获取数据库中的集合
             * 如果集合不存在，则会隐式创建，此时在内存中，MongoDB 客户端 show tables 看不到
             * 如果继续往集合插入值，则会真实写入磁盘中，show tables 会有值*/
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);

            /**Bson bson = Filters.eq("age", 4)：查询 age 等于 4 的文档，然后进行更新
             * org.bson.conversions.Bson 是一个接口
             * com.mongodb.client.model.Filters 专门用于条件查询，提供了各种检索条件*/
            Bson bson = Filters.eq("age", 5);

            /** org.bson.Document 实现了 org.bson.conversions.Bson
             * 构建修改的新文档，如下所示 如果 desc 字段源文档已经存在，则修改字段值 desc 的值为 USA，如果不存在 desc 字段，则直接添加 desc 字段，其余字段不发生变化
             * 注意更新操作，必须如下所示，设置文档的 key 为 $set，value 为 更新文档
             */
            Document document = new Document();
            document.put("$set", new Document("desc", "USA"));

            /**updateMany(Bson var1, Bson var2)：更新查询结果中的所有文档，如果数据库文件不存在，则修改无效
             * var1：被更新的文档，条件查询
             * var2：更新的新文档
             * UpdateResult updateOne(Bson var1, Bson var2)：更新查询结果中的第一个文档
             */
            UpdateResult updateResult = mongoCollection.updateMany(bson, document);

            /**
             * 修改后例如：
             * loopDocument>>>{ "_id" : { "$oid" : "5b9db3fe27df231b5cdc4784" }, "name" : "KOKO", "age" : 28 }
             loopDocument>>>{ "_id" : { "$oid" : "5b9db40f27df23032cc898f5" }, "name" : "COCO", "age" : 0, "desc" : "USA" }
             loopDocument>>>{ "_id" : { "$oid" : "5b9db40f27df23032cc898f6" }, "name" : "COCO", "age" : 1, "desc" : "USA" }
             loopDocument>>>{ "_id" : { "$oid" : "5b9db40f27df23032cc898f7" }, "name" : "COCO", "age" : 2, "desc" : "USA" }
             loopDocument>>>{ "_id" : { "$oid" : "5b9db40f27df23032cc898f8" }, "name" : "COCO", "age" : 3, "desc" : "USA" }
             loopDocument>>>{ "_id" : { "$oid" : "5b9db40f27df23032cc898f9" }, "name" : "COCO", "age" : 4, "desc" : "USA" }
             */

            mongoClient.close();
        }
    }

    /**
     * 删除指定数据库下指定集合中的文档
     *
     * @param databaseName   ：数据库名
     * @param collectionName ：数据库下的集合名
     * @param isAllDel       ：是否全部将匹配的全部删除，默认为 false，只删除第一个
     */
    public static void delDocument(String databaseName, String collectionName, Boolean isAllDel) {
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
             * 实际应用中 MongoDB 地址应该配置在配置文件中*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**获取数据库中的集合
             * 如果集合不存在，则会隐式创建，此时在内存中，MongoDB 客户端 show tables 看不到
             * 如果继续往集合插入值，则会真实写入磁盘中，show tables 会有值*/
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);

            /**Bson bson = Filters.eq("age", 4) ：age 等于 4 的文档
             * Filters.eq("name", "Spring") : name 等于 Spring 的文档
             */
            Bson bson = Filters.eq("name", "Spring");

            /**deleteOne(Bson var1)：删除结果中的第一个文档
             * 即如果 bson 对应多个文档，则只删除第一个
             * deleteMany(Bson var1)：将匹配的文档全部删除
             */
            if (isAllDel == null || !isAllDel) {
                mongoCollection.deleteOne(bson);
            } else {
                mongoCollection.deleteMany(bson);
            }
            mongoClient.close();
        }
    }

    /**
     * 获取指定数据库下指定集合中的所有文档
     *
     * @param databaseName   ：数据库名
     * @param collectionName ：数据库下的集合名
     * @return
     */
    public static List<Document> findAllDocuments(String databaseName, String collectionName) {
        List<Document> documentList = new ArrayList<Document>();
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
             * 实际应用中 MongoDB 地址应该配置在配置文件中*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**获取数据库中的集合
             * 如果集合不存在，则会隐式创建，此时在内存中，MongoDB 客户端 show tables 看不到
             * 如果继续往集合插入值，则会真实写入磁盘中，show tables 会有值*/
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);

            Long documentSize = mongoCollection.countDocuments();
            System.out.println("集合中文档总数为：" + documentSize);

            /** find()：获取集合中的所有文档*/
            FindIterable<Document> documentFindIterable = mongoCollection.find();

            /**从 FindIterable<Document> 获取第一个文档，不存在時，返回null*/
            Document firstDocument = documentFindIterable.first();
            if (firstDocument != null) {
                System.out.println("firstDocument>>>" + firstDocument.toJson());
            }
            /** 获取迭代器游标，遍历每个文档，集合为空时，则大小为0，不为null*/
            MongoCursor<Document> documentMongoCursor = documentFindIterable.iterator();
            while (documentMongoCursor.hasNext()) {
                Document loopDocument = documentMongoCursor.next();
                System.out.println("loopDocument>>>" + loopDocument.toJson());
                documentList.add(loopDocument);
            }
            /** 控制台输出如下：
             firstDocument>>>{ "_id" : { "$oid" : "5b9db40f27df23032cc898f5" }, "name" : "COCO", "age" : 0 }
             loopDocument>>>{ "_id" : { "$oid" : "5b9db40f27df23032cc898f6" }, "name" : "COCO", "age" : 1 }
             loopDocument>>>{ "_id" : { "$oid" : "5b9db40f27df23032cc898f7" }, "name" : "COCO", "age" : 2 }
             loopDocument>>>{ "_id" : { "$oid" : "5b9db40f27df23032cc898f8" }, "name" : "COCO", "age" : 3 }
             loopDocument>>>{ "_id" : { "$oid" : "5b9db40f27df23032cc898f9" }, "name" : "COCO", "age" : 4 }
             */
            mongoClient.close();
        }
        return documentList;
    }

    /**
     * 条件查询————使用 com.mongodb.client.model.Filters
     * find(Bson bson),这个 Bson 参数会有下面的 Filters 类返回
     * Filters.eq("age", 25)：查询 age 等于 25 的文档
     * Filters.lt("age",25)：查询 age 小于 25 的文档
     * Filters.lte("age",25)：查询 age 小于且等于 25 的文档
     * Filters.gt("age", 25)：查询 age 大于 25 的文档
     * Filters.gte("age", 25)：查询 age 大于且等于 25 的文档
     * Bson bson = Filters.exists("desc")：查询含有 desc 字段的所有文档
     * Bson bson = Filters.exists("desc", false)：查询不含有 desc 字段的所有文档
     * Bson bson = Filters.in("age", 25,28) :查询 age 等于 25 或者 age 等于28的所有文档
     * Bson bson = Filters.nin("age", 25,28)：查询age 不等于 25 且不等于 28的文档
     * Filters.and(Bson... filters)：组合查询，逻辑与
     * Filters.or(Bson... filters)：组合查询，逻辑或
     * Filters.not(Bson... filters)：组合查询，逻辑非
     *
     * @param databaseName   ：数据库名
     * @param collectionName ：数据库下的集合名
     * @return
     */
    public static List<Document> searchDocuments(String databaseName, String collectionName) {
        List<Document> documentList = new ArrayList<Document>();
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
             * 实际应用中 MongoDB 地址应该配置在配置文件中*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**获取数据库中的集合
             * 如果集合不存在，则会隐式创建，此时在内存中，MongoDB 客户端 show tables 看不到
             * 如果继续往集合插入值，则会真实写入磁盘中，show tables 会有值*/
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);

            Long documentSize = mongoCollection.countDocuments();
            System.out.println("集合中文档总数为：" + documentSize);

            /** find()：获取集合中的所有文档
             * FindIterable<TDocument> find(Bson var1)：条件查询
             * Filters.eq("age", 25)：查询 age 等于 25 的文档
             * Filters.lt("age",25)：查询 age 小于 25 的文档
             * Filters.lte("age",25)：查询 age 小于且等于 25 的文档
             * Filters.gt("age", 25)：查询 age 大于 25 的文档
             * Filters.gte("age", 25)：查询 age 大于且等于 25 的文档
             * */
            /*Bson bson = Filters.eq("age", 25);*/

            /** Bson bson = Filters.exists("desc")：查询含有 desc 字段的所有文档
             * Bson bson = Filters.exists("desc", false)：查询不含有 desc 字段的所有文档
             */
            /*Bson bson = Filters.exists("desc", false)*/

            /** Bson bson = Filters.in("age", 25,28) :查询 age 等于 25 或者 age 等于28的所有文档
             * Bson bson2 = Filters.eq("name","KOKO")：查询 name 等于 KOKO 的所有文档
             */
            Bson bson1 = Filters.in("age", 25, 28);
            Bson bson2 = Filters.eq("name", "KOKO");
            /** and(Bson... filters)：组合查询，逻辑与*/
            Bson bson3 = Filters.and(bson1, bson2);

            /** or(Bson... filters)：组合查询，逻辑或
             */
            Bson bson4 = Filters.eq("address", "湖南");
            /*Bson bson = Filters.or(bson3, bson4);*/

            /**Bson bson = Filters.nin("age", 25,28)：查询age 不等于 25 且不等于 28的文档*/
            /*Bson bson = Filters.nin("age", 25,28);*/

            Bson bson = Filters.not(bson1);

            FindIterable<Document> documentFindIterable = mongoCollection.find(bson);

            /**从 FindIterable<Document> 获取第一个文档，不存在時，返回null*/
            Document firstDocument = documentFindIterable.first();
            if (firstDocument != null) {
                System.out.println("firstDocument>>>" + firstDocument.toJson());
            }
            /** 获取迭代器游标，遍历每个文档，集合为空时，则大小为0，不为null*/
            MongoCursor<Document> documentMongoCursor = documentFindIterable.iterator();
            while (documentMongoCursor.hasNext()) {
                Document loopDocument = documentMongoCursor.next();
                System.out.println("loopDocument>>>" + loopDocument.toJson());
                documentList.add(loopDocument);
            }
            /** 控制台输出示例如下：
             * firstDocument>>>{ "_id" : { "$oid" : "5b9e115f27df230dd893c64e" }, "name" : "KOKO", "age" : 28, "desc" : "中国" }
             * loopDocument>>>{ "_id" : { "$oid" : "5b9e115f27df230dd893c64e" }, "name" : "KOKO", "age" : 28, "desc" : "中国" }
             loopDocument>>>{ "_id" : { "$oid" : "5b9e119c27df232174b3c9f6" }, "name" : "KOKO", "age" : 28, "desc" : "中国", "price" : 5588.0 }
             */
            mongoClient.close();
        }
        return documentList;
    }

    /**
     * 对查询的结果进行排序——————使用 com.mongodb.client.model.Sorts
     * Bson bson1 = Sorts.ascending("age")：根据 age 由小到大排序输出
     * Bson bson2 = Sorts.descending("name")：根据 age 由大到小排序输出
     * Bson orderBy(Bson... sorts)，如 orderBy(bson1,bson2):
     * 组合排序，先根据 bson1 排序，当bson1排序字段有相同时，再根据 bson2 排序，依次类推
     *
     * @param databaseName   ：数据库名
     * @param collectionName ：数据库下的集合名
     * @return
     */
    public static List<Document> getDocuments(String databaseName, String collectionName) {
        List<Document> documentList = new ArrayList<Document>();
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
             * 实际应用中 MongoDB 地址应该配置在配置文件中*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**获取数据库中的集合
             * 如果集合不存在，则会隐式创建，此时在内存中，MongoDB 客户端 show tables 看不到
             * 如果继续往集合插入值，则会真实写入磁盘中，show tables 会有值*/
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);

            Long documentSize = mongoCollection.countDocuments();
            System.out.println("集合中文档总数为：" + documentSize);

            /** find()：获取集合中的所有文档
             * */
            FindIterable<Document> documentFindIterable = mongoCollection.find();

            /**descending(String... fieldNames)
             *      Bson bson1 = Sorts.ascending("age")：根据 age 由小到大排序输出
             *      Bson bson2 = Sorts.descending("name")：根据 age 由大到小排序输出
             */
            Bson bson1 = Sorts.ascending("age");
            Bson bson2 = Sorts.descending("name");

            /** Bson orderBy(Bson... sorts)
             * 组合排序，先根据 bson1 排序，当bson1排序字段有相同时，再根据 bson2 排序，依次类推
             */
            Bson bson = Sorts.orderBy(bson1, bson2);

            documentFindIterable.sort(bson);

            /**从 FindIterable<Document> 获取第一个文档，不存在時，返回null*/
            Document firstDocument = documentFindIterable.first();
            if (firstDocument != null) {
                System.out.println("firstDocument>>>" + firstDocument.toJson());
            }
            /** 获取迭代器游标，遍历每个文档，集合为空时，则大小为0，不为null*/
            MongoCursor<Document> documentMongoCursor = documentFindIterable.iterator();
            while (documentMongoCursor.hasNext()) {
                Document loopDocument = documentMongoCursor.next();
                System.out.println("loopDocument>>>" + loopDocument.toJson());
                documentList.add(loopDocument);
            }
            /** 控制台输出示例如下：
             * firstDocument>>>{ "_id" : { "$oid" : "5b9e115f27df230dd893c64e" }, "name" : "KOKO", "age" : 28, "desc" : "中国" }
             * loopDocument>>>{ "_id" : { "$oid" : "5b9e115f27df230dd893c64e" }, "name" : "KOKO", "age" : 28, "desc" : "中国" }
             loopDocument>>>{ "_id" : { "$oid" : "5b9e119c27df232174b3c9f6" }, "name" : "KOKO", "age" : 28, "desc" : "中国", "price" : 5588.0 }
             */
            mongoClient.close();
        }
        return documentList;
    }

    /**
     * 对查询的结果进行字段过滤，即设置哪些字段返回——————使用 com.mongodb.client.model.Projections
     * Bson bson = Projections.excludeId()：结果中文档将不会再有 _id 主键字段
     * Bson exclude(String... fieldNames) ：排除哪些字段不再显示
     * 如 Projections.exclude("name", "age")：表示结果中文档将不会再有 name、age 字段
     * Bson include(String... fieldNames)：包含哪些字段进行显示
     * 如 Projections.include("desc","name")：表示结果中文档只包含 desc、name 以及主键 _id 字段
     * Bson fields(Bson... projections)：组合过滤
     *
     * @param databaseName   ：数据库名
     * @param collectionName ：数据库下的集合名
     * @return
     */
    public static List<Document> loadDocuments(String databaseName, String collectionName) {
        List<Document> documentList = new ArrayList<Document>();
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
             * 实际应用中 MongoDB 地址应该配置在配置文件中*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**获取数据库中的集合
             * 如果集合不存在，则会隐式创建，此时在内存中，MongoDB 客户端 show tables 看不到
             * 如果继续往集合插入值，则会真实写入磁盘中，show tables 会有值*/
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);

            Long documentSize = mongoCollection.countDocuments();
            System.out.println("集合中文档总数为：" + documentSize);

            /** find()：获取集合中的所有文档
             * */
            FindIterable<Document> documentFindIterable = mongoCollection.find();

            /** Bson bson = Projections.excludeId()：结果中文档将不会再有 _id 主键字段
             * Bson exclude(String... fieldNames) ：排除哪些字段不再显示
             *      如 Projections.exclude("name", "age")：表示结果中文档将不会再有 name、age 字段
             * Bson include(String... fieldNames)：包含哪些字段进行显示
             *      如 Projections.include("desc","name")：表示结果中文档只包含 desc、name 以及主键 _id 字段
             */
            /*Bson bson = Projections.excludeId();*/
            /*Bson bson = Projections.exclude("name", "age");*/
            /*Bson bson = Projections.include("desc","name");*/

            /**Bson fields(Bson... projections)：组合过滤
             *      Projections.excludeId()：排除主键 _id 字段
             *      Projections.include("name","age")：包含 name、age字段，默认单纯这一句会包含 主键 _id 字段的
             * 组合起来就是：结果文档中只会有 name 与 age 字段
             */
            Bson bson1 = Projections.excludeId();
            Bson bson2 = Projections.include("name", "age");
            Bson bson = Projections.fields(bson1, bson2);

            documentFindIterable.projection(bson);

            /**从 FindIterable<Document> 获取第一个文档，不存在時，返回null*/
            Document firstDocument = documentFindIterable.first();
            if (firstDocument != null) {
                System.out.println("firstDocument>>>" + firstDocument.toJson());
            }
            /** 获取迭代器游标，遍历每个文档，集合为空时，则大小为0，不为null*/
            MongoCursor<Document> documentMongoCursor = documentFindIterable.iterator();
            while (documentMongoCursor.hasNext()) {
                Document loopDocument = documentMongoCursor.next();
                System.out.println("loopDocument>>>" + loopDocument.toJson());
                documentList.add(loopDocument);
            }
            /** 控制台输出示例如下：
             * firstDocument>>>{ "_id" : { "$oid" : "5b9e115f27df230dd893c64e" }, "name" : "KOKO", "age" : 28, "desc" : "中国" }
             * loopDocument>>>{ "_id" : { "$oid" : "5b9e115f27df230dd893c64e" }, "name" : "KOKO", "age" : 28, "desc" : "中国" }
             loopDocument>>>{ "_id" : { "$oid" : "5b9e119c27df232174b3c9f6" }, "name" : "KOKO", "age" : 28, "desc" : "中国", "price" : 5588.0 }
             */
            mongoClient.close();
        }
        return documentList;
    }

    /**
     * 查询并删除----------即取值之后，原来的值就会被删除，值只能取一次，可用于做消息队列
     * 为了演示简单，查询条件直接写死在方法中了
     *
     * @param databaseName   ：数据库名称
     * @param collectionName ：集合名称
     */
    public static Document findAndDelDocument(String databaseName, String collectionName) {
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
             * 实际应用中 MongoDB 地址应该配置在配置文件中*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**获取数据库中的集合
             * 如果集合不存在，则会隐式创建，此时在内存中，MongoDB 客户端 show tables 看不到
             * 如果继续往集合插入值，则会真实写入磁盘中，show tables 会有值*/
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);

            Long documentSize = mongoCollection.countDocuments();
            System.out.println("集合中文档总数为：" + documentSize);

            /** findOneAndDelete(Bson var1)：条件查询并删除，查询符合条件的第一条数据，返回之后，删除源数据
             *  即 值只取一次，取完一次就没有了，适合做消息队列，没有查询到值时，返回 null
             *  如下所示：查询 age 等于 28的文档中的第一条，查询之后集合中便没有它了
             */
            Bson bson = Filters.eq("age", 28);
            Document document = mongoCollection.findOneAndDelete(bson);
            if (document != null) {
                System.out.println("document>>>" + document.toJson());
            }
            mongoClient.close();
            return document;
        }
        return null;
    }

    /**
     * 查询并修改----------即取值之后，用一个新文档来替换旧文档
     * 为了演示简单，查询条件 与 替换文档 直接写死在方法中了
     *
     * @param databaseName   ：数据库名称
     * @param collectionName ：集合名称
     */
    public static Document findAndReplaceDocument(String databaseName, String collectionName) {
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
             * 实际应用中 MongoDB 地址应该配置在配置文件中*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**获取数据库中的集合
             * 如果集合不存在，则会隐式创建，此时在内存中，MongoDB 客户端 show tables 看不到
             * 如果继续往集合插入值，则会真实写入磁盘中，show tables 会有值*/
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);

            Long documentSize = mongoCollection.countDocuments();
            System.out.println("集合中文档总数为：" + documentSize);

            /** findOneAndReplace(Bson var1, TDocument var2)：条件查询并更新
             *      查询符合条件的第一条数据，返回之后，用新文档替换旧文档，没有查询到值时，返回 null
             *  如下所示查询 age 等于 6的第一条文档，然后用一个新文档替换源文档，主键 _id 不会变化
             */
            Bson bson = Filters.eq("age", 6);
            Document tDocument = new Document("type", "success");
            tDocument.append("time", new Date().getTime());
            Document document = mongoCollection.findOneAndReplace(bson, tDocument);

            if (document != null) {
                System.out.println("document>>>" + document.toJson());
            }
            /**
             * 控制台输出示例：
             * document>>>{ "_id" : { "$oid" : "5b9e20191eb14dd1f57e3932" }, "name" : "LiSi6", "age" : 6.0 }
             * MongoDB 客户端 查看示例：
             * > db.c1.find()
             { "_id" : ObjectId("5b9e1b251eb14dd1f57e3930"), "name" : "zhangSan", "address" : "湖南" }
             { "_id" : ObjectId("5b9e20191eb14dd1f57e3931"), "type" : "success", "time" : NumberLong("1537095949021") }
             */
            mongoClient.close();
            return document;
        }
        return null;
    }

    /**
     * 遍历文档迭代器，两个重载的 forEach 方法
     * 1）forEach(Block<? super TResult> var1)
     * 2）forEach(Consumer<? super T> action)
     * <p/>
     * 为了演示简单，查询条件写死在方法中了
     *
     * @param databaseName   ：数据库名称
     * @param collectionName ：集合名称
     */
    public static Document forEachDocument(String databaseName, String collectionName) {
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
             * 实际应用中 MongoDB 地址应该配置在配置文件中*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**获取数据库中的集合
             * 如果集合不存在，则会隐式创建，此时在内存中，MongoDB 客户端 show tables 看不到
             * 如果继续往集合插入值，则会真实写入磁盘中，show tables 会有值*/
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);

            Long documentSize = mongoCollection.countDocuments();
            System.out.println("集合中文档总数为：" + documentSize);

            MongoIterable<Document> mongoIterable = mongoCollection.find();

            /** 除了 MongoCursor<Document> mongoCursor = mongoIterable.iterator() 使用游标进行遍历之外
             *  因为 MongoDB 可以使用 js 引擎进行解析，所以还可以使用 forEach(Block<? super TResult> var1) 方法
             *  很类似 JQuery 的 forEach 遍历，控制台输出示例：
             *  block document>>>Document{{_id=5b9e1b251eb14dd1f57e3930, name=zhangSan, address=湖南}}
             block document>>>Document{{_id=5b9e20191eb14dd1f57e3931, type=success, time=1537095949021}}
             block document>>>Document{{_id=5b9e20191eb14dd1f57e3932, type=success, time=1537096589427}}
             block document>>>Document{{_id=5b9e20191eb14dd1f57e3933, name=LiSi7, age=7.0}}
             */
            mongoIterable.forEach(new Block<Document>() {
                @Override
                public void apply(Document document) {
                    System.out.println("block document>>>" + document);
                }
            });

            /** forEach 还有一个重载的方法 forEach(Consumer<? super T> action)
             * 效果完全一致控制台输出示例：
             * consumer document>>>{ "_id" : { "$oid" : "5b9e1b251eb14dd1f57e3930" }, "name" : "zhangSan", "address" : "湖南" }
             consumer document>>>{ "_id" : { "$oid" : "5b9e20191eb14dd1f57e3931" }, "type" : "success", "time" : { "$numberLong" : "1537095949021" } }
             consumer document>>>{ "_id" : { "$oid" : "5b9e20191eb14dd1f57e3932" }, "type" : "success", "time" : { "$numberLong" : "1537096589427" } }
             consumer document>>>{ "_id" : { "$oid" : "5b9e20191eb14dd1f57e3933" }, "name" : "LiSi7", "age" : 7.0 }
             */
            System.out.println("=================================");
            mongoIterable.forEach(new Consumer<Document>() {
                @Override
                public void accept(Document document) {
                    System.out.println("consumer document>>>" + document.toJson());
                }
            });
            mongoClient.close();
        }
        return null;
    }

    /**
     * 有序操作 与 无序操作——————————未总结完成
     *
     * @param databaseName
     * @param collectionName
     */
    public static void orderOption(String databaseName, String collectionName) {
        if (databaseName != null && !"".equals(databaseName) && collectionName != null && !"".equals(collectionName)) {
            /** MongoClient(String host, int port)：直接指定 MongoDB IP 与端口进行连接
             * 实际应用中 MongoDB 地址应该配置在配置文件中*/
            MongoClient mongoClient = new MongoClient("127.0.0.1", 27017);

            /**getDatabase(String databaseName)：获取指定的数据库
             * 如果此数据库不存在，则会自动创建，此时存在内存中，服务器不会存在真实的数据库文件，show dbs 命令 看不到
             * 如果再往其中添加数据，服务器则会生成数据库文件，磁盘中会真实存在，show dbs 命令 可以看到
             * */
            MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

            /**获取数据库中的集合
             * 如果集合不存在，则会隐式创建，此时在内存中，MongoDB 客户端 show tables 看不到
             * 如果继续往集合插入值，则会真实写入磁盘中，show tables 会有值*/
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);

            Long documentSize = mongoCollection.countDocuments();
            System.out.println("集合中文档总数为：" + documentSize);

            /**InsertOneModel(T document)*/
            Document insertDocument = new Document("name", "insertModel");
            insertDocument.append("time", new Date().getTime());
            InsertOneModel<Document> insertOneModel = new InsertOneModel<Document>(insertDocument);

            /**UpdateOneModel(Bson filter, Bson update)*/
            Document updateDocument = new Document();
            updateDocument.append("$set", new Document("name", "updateOneModel").append("time", new Date().getTime()));
            Bson updateBson = Filters.eq("age", 28);
            UpdateOneModel<Document> updateOneModel = new UpdateOneModel<Document>(updateBson, updateDocument);

            /**DeleteOneModel(Bson filter)
             * 同理还有 DeleteManyModel<T>
             */
            Bson delBson = Filters.eq("age", 26);
            DeleteOneModel<Document> deleteOneModel = new DeleteOneModel<Document>(delBson);

            /**ReplaceOneModel(Bson filter, T replacement)
             * 同理还有 UpdateManyModel<T> extends WriteModel<T> */
            Bson repBson = Filters.eq("age", 27);
            Document replaceDocument = new Document("name", "replaceOneModel");
            replaceDocument.put("time", new Date().getTime());
            ReplaceOneModel<Document> replaceOneModel = new ReplaceOneModel<Document>(repBson, replaceDocument);

            List<WriteModel<Document>> writeModelList = new ArrayList<WriteModel<Document>>();
            writeModelList.add(insertOneModel);
            writeModelList.add(updateOneModel);
            writeModelList.add(deleteOneModel);
            writeModelList.add(replaceOneModel);

            /**bulkWrite(List<? extends WriteModel<? extends TDocument>> var1)：有序操作
             * 有一个重载的方法：bulkWrite(List<? extends WriteModel<? extends TDocument>> var1, BulkWriteOptions var2)
             * 用于设置是有序操作还是无序操作：
             * mongoCollection.bulkWrite(writeModelList, new BulkWriteOptions().ordered(false));
             * false 表示无序操作，true 表示有序操作
             * */
            mongoCollection.bulkWrite(writeModelList);
            mongoClient.close();
        }
    }

    public static void main(String[] args) {
//        insertSingleDocument("java", "c1");
//        findAllDocuments("java", "c2");

        orderOption("java", "c2");

//        updateDocuments("java", "c1");

        System.out.println("--------------1------------");
//        searchDocuments("java", "c1");
//        getDocuments("java", "c1");
//        loadDocuments("java", "c1");
//        findAndDelDocument("java", "c1");
//        findAndReplaceDocument("java", "c1");

//        forEachDocument("java", "c1");

        System.out.println("----------------2----------------");
//        findAllDocuments("java", "c1");
    }
}
