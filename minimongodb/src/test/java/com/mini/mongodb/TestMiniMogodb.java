package com.mini.mongodb;

import java.util.Random;
import java.util.UUID;

public class TestMiniMogodb {
    private static final String _uuid = UUID.randomUUID().toString();
    private static final String database_name ="mini-mongodb.xml";
    private  MiniMogodb mogodb = new MiniMogodb();
    static String table = "persons";

    public static void main(String[] args){
        Person p  = new Person();
        p.set_uuid(_uuid);
        p.setAge(32);
        p.setMoney(10000);
        p.setName("tom");
        p.setSex("male");

        TestMiniMogodb test = new TestMiniMogodb();
        //test.createDatabase(); //创建数据库

        p.setSex("female");
        p.setMoney(new Random().nextDouble());
        p.setAge(new Random().nextInt(100));
        test.insertData("persons",p); //插入数据
       // p.set_uuid("3ff6f1a9-2a1f-4298-b9c7-a7f0d3399c37");

        //test.updateData("persons",p);//更新数据

        //test.deleteData("persons", p); //删除数据
        test.listAll(table);//列出表所有数据
    }

    private void listAll(String table) {
        mogodb.loadTableDatas(database_name,table );
    }

    private void deleteData(String persons, Object p) {
        mogodb.deleteData(database_name, persons, p);
    }

    private void updateData(String table, Object p) {
        mogodb.updateData(database_name, table, p);
    }

    private void insertData(String table, Object p) {

        mogodb.addData(database_name, table, p);

    }

    private void createDatabase() {

        mogodb.createDataBase(database_name);
    }

}
