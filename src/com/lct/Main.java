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
        dbObject.put("name", "���");
        dbObject.put("desc", "��������С����");

        dbCollection.insert(dbObject);
        mongoClient.close();
    }

    public static void testFind() throws UnknownHostException {
        /**
         * ���ӵ� MongoDB ����
         * MongoClient(String host, int port)��
         * 1��host��MongoDB ����� IP
         * 2��port��MongoDB ����� �˿ڣ�Ĭ��Ϊ 27017
         * ��ʹ MongoDB ����˹رգ���ʱҲ�����׳��쳣*/
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
