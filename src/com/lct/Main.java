package com.lct;

import com.mongodb.*;

import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {
	// write your code here
    }

    public void testAdd() throws UnknownHostException {
        MongoClient mongoClient = new MongoClient("localhost", 27017);

        DB db = mongoClient.getDB("test");
        DBCollection dbCollection = db.getCollection("person");
        BasicDBObject dbObject = new BasicDBObject();
        dbObject.put("name", "李东浩");
        dbObject.put("desc", "天生丽质小清新");

        dbCollection.insert(dbObject);
        mongoClient.close();
    }

    public static void testFind() throws UnknownHostException {
        /**
         * 连接到 MongoDB 服务
         * MongoClient(String host, int port)：
         * 1）host：MongoDB 服务端 IP
         * 2）port：MongoDB 服务端 端口，默认为 27017
         * 即使 MongoDB 服务端关闭，此时也不会抛出异常*/
        MongoClient mongoClient = new MongoClient("localhost", 27017);

        /**
         * java.net.ConnectException: Connection refused: connect
         */
        DB db = mongoClient.getDB("mydb1");

        DBCollection dbCollection = db.getCollection("c2");
        DBCursor cursor = dbCollection.find();
        while (cursor.hasNext()) {
            DBObject dbObject = cursor.next();
            System.out.println(dbObject.toString());
            System.out.println(dbObject.get("name"));
        }
    }


}
