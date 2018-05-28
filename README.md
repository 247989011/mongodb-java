# 模仿monogdb写一个数据库
* 简介：

    MongoDB  是一个基于分布式文件存储的数据库。由C++语言编写。旨在为WEB应用提供可扩展的高性能数据存储解决方案。
    MongoDB 是一个介于关系数据库和非关系数据库之间的产品，是非关系数据库当中功能最丰富，最像关系数据库的。他支持的数据结构非常松散，是类似json的bson格式，因此可以存储比较复杂的数据类型。Mongo最大的特点是他支持的查询语言非常强大，其语法有点类似于面向对象的查询语言，几乎可以实现类似关系数据库单表查询的绝大部分功能，而且还支持对数据建立索引。
*  特点：高性能、易部署、易使用，存储数据非常方便
    
    主要功能特性有：
       
        *面向集合存储，易存储对象类型的数据。
        *模式自由。
        *支持动态查询。
        *支持完全索引，包含内部对象。
        *支持查询。
        *支持复制和故障恢复。
        *使用高效的二进制数据存储，包括大型对象（如视频等）。
        *自动处理碎片，以支持云计算层次的扩展性。
        *支持RUBY，PYTHON，JAVA，C++，PHP，C#等多种语言。
        *文件存储格式为BSON（二进制）（一种JSON的扩展，比JSON数据类型更加丰富）。
        *可通过网络访问。
        *支持ObjectId、日期类型、正则表达式、JS代码、二进制数据等
* MongoDB 注意事项： 
    
        * MongoDB 不支持事务
        * MongoDB 不支持多表连接查询
        * MongoDB 中的键值对是有序的，相同的键值对，不同的顺序，属于不同的文档
        * new Date(); 返回日期对象，属于日期类型，Date()函数返回日期字符串，在Shell中操作日期要使用日期类型， 日期类型是包含时区的
        * _id的值可以是任意类型，默认是ObjectId类型，可以在分片环境中生成唯一的标识符（时间戳（单位：秒）+主机的唯一标示（主机名的hash值）+进程标识符PID+自动增加的计数器）， 通过时间戳可以大概知道ObjectId大概是按时间先后排序的，主机的唯一标示可以用于保证在多台服务器器上不重复，进程ID为了保证同台服务器器上多个不同的进程之间生成不重复 值，最后一部分是一个自增的计数器， 前面三部分是为了保证同一秒在同一台机器上的同一个进程上生成一个唯一的值，最后一部分用于保证在同一秒、同一台机器、同一个进程 在同一秒内（注意是同一秒内）生成唯一值
        * mongodb中有一些特殊的键，它们被称为修改器、操作符等       

```mongo
 以 $ 开头，如 
        $set（更新字段）、
        $unset(删除字段)、 
        $inc(自增或自减)、
        $and、$or、$in、$nin、$nor、$exists（用于判断文档中是否包含某字段）、
        $push(向数组中尾部添加一个元素)、
        $pushAll(将数组中的所有值push)、
        $addToSet（向set集合中添加元素）、
        $pop(删除数组中的头部或尾部元素)， 
        $pull(删除数组中指定的值)、
        $size（根据数组的长度进行筛选）、
        $slice(返回数组中部分元素，如前几个、后几个、中间连续几个元素)、
        $elemMatch(用于匹配数组中的多个条件)、
        $where(自定义筛选条件，效率比较低，需要将bson转为js对象，不能使用索引，可以先使用普通查询过滤掉部分不满足条件的文档，然后再使用where，尽量减少where操作文档的数量过大)
```        



## MongoDB 与MySQL对比

对比项|MySQL|Mongodb
-----|-------|-------
开源|是|是
类型|关系型数据库|非关系型数据库(文档型数据库)NoSQL
||
表|二维表table|集合list
表字段|字段field|键key
主外键|PK，FK|无
灵活度扩展性|差|极高
记录record|关系数据的表的record必须保证拥有每一个field|mongodb的每一个documen的key可以不一样
查询|使用SQL|使用内置的find函数，基于BSON的特性查询工具
||
服务器守护进程|mysqld|mongod
客户端工具|mysql|mongo
逻辑备份工具|mysqldump|mongodump
逻辑还原工具|mysql|mongorestore
数据导出工具|mysqldump|mongoexport
数据导入工具|source|mongoimport
||
新建用户并授权|grant all on \*.\* to username@'localhost'identified by 'passwd';|db.addUser("user","psw") <br>db.auth("user","psw")
显示库列表|show databases;|show dbs
进去库|use dbname;|use dbname
显示表列表|show tables;|show collections
查询主从状态|show slave status;|rs.status
创建库|create database name;|无需单独创建，直接use进去
删除库|drop database dbname;|首先进去该库，db.dropDatabase()
创建表|create table tname(id int);|无需单独创建，直接插入数据
删除表|drop table tname;|db.tname.drop()
插入记录|insert into tname(id) value(2);|db.tname.insert({id:2})
删除记录|delete from tname where id=2;|db.tname.remove({id:2})
修改/更新记录|update tname set id=3 where id=2;|db.tname.update({id:2},{$set:{id:3}},false,true)
查询所有记录|select * from tname;|db.tname.find()
查询所有列|select id from tname;|db.tname.find({},{id:1})
条件查询|select * from tname where id=2;|db.tname.find({id:2})
条件查询|select * from tname where id < 2;|db.tname.find({id:{$lt:2}})
条件查询|select * from tname where id >=2;|db.tname.find({id:{$gte:2}})
条件查询|select * from tname where id=2 and name='steve';|db.tname.find({id:2,name:'steve'})
条件查询|select * from tname where id=2 or name='steve';|db.tname.find($or:[{id:2}, {name:'steve'}])
条件查询|select * from tname limit 1;|db.tname.findOne()
模糊查询|select * from tname where name like "%ste%";|db.tname.find({name:/ste/})
模糊查询|select * from tname where name like "ste%";|db.tname.find({name:/^ste/})
获取表记录数|select count(id) from tname;|db.tname.count()
获取有条件的记录数|select count(id) from tname where id=2;|db.tname.find({id:2}).count()
查询时去掉重复值|select distinct(last_name) from tname;|db.tname.distinct('last_name')
正排序查询|select *from tname order by id;|db.tname.find().sort({id:1})
逆排序查询|select *from tname order by id desc;|db.tname.find().sort({id:-1})
取存储路径|explain select * from tname where id=3;|db.tname.find({id=3}).explain()
||




    


## windows下载安装
    
* 下载地址：https://www.mongodb.com/download-center
* 设置window的环境变量 ：把mongodb/bin目录路径配置到Path下
* 启动数据库服务
    * 方式一： 使用默认的路径，MongoDB安装所在的盘符:/data/db

            MongoDB的数据时存储在磁盘上的，而不是存储在内存中的，所以在启动MongoDB服务时需要指定数据存储的位置， 如果不指定会默认存储在/data/db目录下, 注意在使用默认位置时，需要以管理员身份预先创建好目录

            ```cmd
            C:\Windows\system32>D:
            D:\>mkdir data
            D:\>cd data
            D:\data>mkdir db
            D:\data>cd D:\Java\MongoDB\Server\bin
            D:\Java\MongoDB\Server\bin>mongod
            
            MongoDB starting : pid=9712 port=27017 dbpath=D:\data\db\ 64-bit host=zm-PC
            ```
    
    * 方式二： 显式指定数据库路径      

            此种方式也要预先创建好目录，这里的位置放置MongoDB里面，如D:\Java\MongoDB\DB，放在MongoDB里面不需要管理员身份创建 
            根据自己需要修改mongodb.bat文件中的配置

            ```cmd
            mongod --dbpath D:\soft\MongoDB\data\db
            ```    
    * 方式三： 安装服务
 
        ```cmd
        D:\soft\MongoDB\bin>mongod  --dbpath=D:\data\db --logappend --logpath=D:\data\log.txt --install
        ```
        以后启动服务只需要net start MongoDB, 比mongod --dbpath=xxx 稍微方便些, 以后电脑开机后就自动启动了，省的每次都要启动        

 * mongo 用于客户端连接服务器  
     
     语法： mongo [IP:PORT][/DATABASE_NAME]
     
     IP默认的是127.0.0.1 
     Port默认的是27017 
     database默认的是test，mongodb中默认有两个数据库admin、local    

    ```cmd
    // 连接时不指定要连接数据库，需要时从连接中获取需要的数据库
    D:\soft\MongoDB\bin>mongo --nodb
    MongoDB shell version v3.4.6
    > db
    2017-07-27T20:27:25.181+0800 E QUERY    [thread1] ReferenceError: db is not defined :
    @(shell):1:1
    > conn = new Mongo("localhost:27017")
    connection to localhost:27017
    > db = conn.getDB("test")
    test
    >
    ```
## shell 基本操作
### 数据库操作
* 创建一个数据库
    
    `use [databaseName]`
    
    注：此时数据库并没有被真正的创建
    ![create-db](doc/create-db.png '')
* 查看所有数据库
    
    `show dbs`
* 查看当前数据库 

    `db`    
* 删除当前数据库

    `db.dropDatabase()`   

### 集合操作
* 创建集合(文档)
  * 方式一：隐式创建集合
            
            当向集合中的插入文档时，如果集合不存在，系统会自动创建，所以向一个不存在的集合中插入数据也就是创建了集合
        
    ```cmd
    > db
    test
    > show tables
    > db.users.insert({"usernmae": "mengdee", "age": 26})
    WriteResult({ "nInserted" : 1 })
    > show tables
    users
    >
    ```
  * 方式二：显示创建集合 
            
            db.createCollection(“集合名字”， 可选配置) 
            
            显示创建集合可以通过一些配置创建一些特殊的集合，如固定集合
  ```cmd
  > show tables
  users
  > db.createCollection("address")
  { "ok" : 1 }
  
  // 固定集合只能通过调用方法显式创建，固定集合可以指定集合存储数据的大小和最多允许存储的条数
  // 当固定集合文档条数达到上限时，再插入新的文档会将最老的文档删除掉，然后插入到该位置
  > db.createCollection("address", {capped: true, size: 10000, max:1000})
  { "ok" : 1 }
  > show tables
  address
  users
  >
  ```
* 删除集合

    db.集合名字.drop()
    ```cmd
    > db.address.drop()
    true
    ```
* 查看集合

    `show tables`和`show collections`都可以查看当前数据下的集合
     
* 添加数据(记录)
    * 方式一： insert： _id 会自动创建唯一索引，当id重复的时候会报错
           
            db.[documentName].insert({})        // 插入一条，返回值中不包含insertedIds
            db.[documentName].insert([{}, {}])  // 批量插入，返回值中不包含insertedIds
            db.[documentName].insertOne(Bson)   // 插入一条，返回值返回插入的insertedId
            db.[documentName].insertMany(Bson)  // 批量插入，返回值中包含insertedIds
            db.[documentName].findAndModify({查询条件}, "update": {需要添加或更新的字段},  "upsert": true });

        * 写入安全：
             * 应答模式：插入时会返回成功或者失败
             * 非应答模式：插入时没有反馈，即插入有没有成功不知道
             
        ```cmd
        > var user = {"name": "mengdee", "age": 20, "address": "上海市浦东新区张江镇", "create_time": new Da
        te()}
        > db.users.insert(user)
        WriteResult({ "nInserted" : 1 })
        > db.users.find()
        { "_id" : ObjectId("5976ad21670af2aa52ea90df"), "username" : "mengdee", "age" : 26 }
        { "_id" : ObjectId("5976b395670af2aa52ea90e0"), "name" : "mengdee", "age" : 20, "address" : "上海市
        浦东新区张江镇", "create_time" : ISODate("2017-07-25T02:57:04.545Z") }
        
        > var userDoc = db.users.findOne()
        > var insertDate = userDoc["_id"]
        > insertDate.getTimestamp()
        ISODate("2017-07-25T02:29:53Z")
        > insertDate.str
        5976ad21670af2aa52ea90df
        
        > db.users.insertOne({"username": "mengday3"})
        {
                "acknowledged" : true,
                "insertedId" : ObjectId("5976b632670af2aa52ea90e1")
        }
        > db.users.insertMany([{"username": "mengday4"}, {"username": "mengday5"}])
        {
                "acknowledged" : true,
                "insertedIds" : [
                        ObjectId("5976b666670af2aa52ea90e2"),
                        ObjectId("5976b666670af2aa52ea90e3")
                ]
        }
        > db.users.insert([{"username": "mengday6"}, {"username": "mengday7"}])
        BulkWriteResult({
                "writeErrors" : [ ],
                "writeConcernErrors" : [ ],
                "nInserted" : 2,
                "nUpserted" : 0,
                "nMatched" : 0,
                "nModified" : 0,
                "nRemoved" : 0,
                "upserted" : [ ]
        })
        
        // 使用insertOne插入重复的_id 会报错
        > db.users.insertOne({"_id": 1, "username": "mengday8"})
        { "acknowledged" : true, "insertedId" : 1 }
        > db.users.insertOne({"_id": 1, "username": "mengday8"})
        2017-07-25T11:15:47.822+0800 E QUERY    [thread1] WriteError: E11000 duplicate key error collection:
         test.users index: _id_ dup key: { : 1.0 } :
        WriteError({
                "index" : 0,
                "code" : 11000,
                "errmsg" : "E11000 duplicate key error collection: test.users index: _id_ dup key: { : 1.0 }
        ",
                "op" : {
                        "_id" : 1,
                        "username" : "mengday8"
                }
        })
        
        // findAndModify 也可以用于插入文档，但是前提是一定不存在，如果存在了就变成更新了，单纯的插入还是不要用种方式了，findAndModify一般用于更新或删除操作
        > db.users.findAndModify({ "query": {"username": "mengday11"}, "update": {"username": "mengday11", "age": 26},  "upsert": true })
        null
        > db.users.find()
        { "_id" : ObjectId("597c584448c373e228a9259e"), "username" : "xxx", "age" : 26 }
        ```     
        
        * MongoDB Shell是一种JS，所以可以通过var来定义变量，可以通过find（）方法来查找集合中的所有数据，相当于select * from users 
        * 插入数据时，如果没有指定_id这个字段，系统会自动生成一个值，从该值中能够解析出插入文档的日期或者获取日期中的字符串值
    * 方式二：save() 
            
            db.集合名字.save(Bson) : 如果要插入的文档没有指定_id,则插入文档，如果指定_id,如果集合中不包含_id，则插入文档，如果集合中已经存在相同的id值，则更会整体替换
        ```cmd
        > db.user.find()
        > db.user.insert({"_id":1,"username":"mery"})
        WriteResult({ "nInserted" : 1 })
        > db.user.insert({"_id":2,"username":"mery2"})
        WriteResult({ "nInserted" : 1 })
        > db.user.find()
        { "_id" : 1, "username" : "mery" }
        { "_id" : 2, "username" : "mery2" }
        > db.user.save({"_id":3,"age":20})
        WriteResult({ "nMatched" : 0, "nUpserted" : 1, "nModified" : 0, "_id" : 3 })
        > db.user.find()
        { "_id" : 1, "username" : "mery" }
        { "_id" : 2, "username" : "mery2" }
        { "_id" : 3, "age" : 20 }
        > db.user.save({"_id":2,"sex":"nan"})
        WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
        > db.user.find()
        { "_id" : 1, "username" : "mery" }
        { "_id" : 2, "sex" : "nan" }
        { "_id" : 3, "age" : 20 }
        ```
    * update() 
        
        `update({查询条件}, {更新的文档}， 是否开启addOrUpdate)` : addOrUpdate为true，当集合中不存在的时候就插入，存在就更新
        ```cmd
        > db.users.find()
        { "_id" : 5, "username" : "mengday10" }
        > db.users.update({"username" : "mengday11"}, { "_id" : 6, "age" : 20, "gender" : 1 }, true)
        WriteResult({ "nMatched" : 0, "nUpserted" : 1, "nModified" : 0, "_id" : 6 })
        > db.users.find()
        { "_id" : 5, "username" : "mengday10" }
        { "_id" : 6, "age" : 20, "gender" : 1 }
        >
        ```
* 删除 remove
    
    `remove({删除条件})`： 删除满足条件的所有数据 <br>
    `remove({删除条件}， true)`： 删除满足条件的第一条数据  <br>
    `remove({})`: 清空集合中的所有文档
    
    ```cmd
     db.users.find()
    { "_id" : 1, "username" : "mengday", "password" : "123456" }
    { "_id" : 2, "username" : "mengday2", "password" : "123456" }
    { "_id" : 3, "username" : "mengday3", "password" : "123456", "age" : 18 }
    { "_id" : 4, "username" : "mengday4", "password" : "123456", "age" : 28 }
    { "_id" : 5, "username" : "mengday5", "password" : "123456", "age" : 38 }
    > db.users.remove({age: {$lt: 38}})
    WriteResult({ "nRemoved" : 2 })
    > db.users.find()
    { "_id" : 1, "username" : "mengday", "password" : "123456" }
    { "_id" : 2, "username" : "mengday2", "password" : "123456" }
    { "_id" : 5, "username" : "mengday5", "password" : "123456", "age" : 38 }
    > db.users.remove({"password": "123456"}, true)
    WriteResult({ "nRemoved" : 1 })
    > db.users.find()
    { "_id" : 2, "username" : "mengday2", "password" : "123456" }
    { "_id" : 5, "username" : "mengday5", "password" : "123456", "age" : 38 }
    > db.users.remove({})
    WriteResult({ "nRemoved" : 2 })
    
    // findAndModify可以用来插入或更新upsert、也可以用来删除
    > db.users.findAndModify({
       "query": {"username": "mengday"},
       "remove": true
    })
    {
            "_id" : ObjectId("597c3c1587d089dfa7ce1be3"),
            "username" : "mengday",
            "addresses" : [
                    {
                            "city" : "shanghai",
                            "area" : "zhangjiang"
                    },
                    {
                            "city" : "beijing",
                            "area" : "CHAOYANG"
                    }
            ],
            "create_time" : ISODate("2017-07-29T09:09:14.031Z")
    }
    ```   

* 更新 update，findAndModify
        
        更新指定字段的值 
        替换整个文档 
        更新满足条件的第一条文档 
        更新满足条件的所有文档
    * 使用$set修改器修改指定字段, 当字段不存在时会创建并赋值
        ```cmd
        > db.users.find()
        { "_id" : 1, "username" : "mengday5", "password" : "123456", "age" : 38 }
        // 使用$set修改器修改指定字段, 当字段不存在时会创建并赋值
        > db.users.update({"username": "mengday5"}, {$set: {"age": 18}})
        WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
        > db.users.find()
        { "_id" : 1, "username" : "mengday5", "password" : "123456", "age" : 18 }

        ```
    * $unset 用于删除字段
        ```cmd
        > db.user.find()
        { "_id" : 1, "username" : "mery" }
        { "_id" : 2, "sex" : "nan" }
        > db.user.update({"_id":1},{$set:{"age":"20"}})
        WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
        > db.user.find()
        { "_id" : 1, "username" : "mery", "age" : "20" }
        { "_id" : 2, "sex" : "nan" }
        > db.user.update({"_id":1},{$unset:{"age":"0"}})
        WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
        > db.user.find()
        { "_id" : 1, "username" : "mery" }
        { "_id" : 2, "sex" : "nan" }
        >
        ```
    * $push: 向数组的尾部添加一个元素，如果字段不存在则创建
        ```cmd
        > db.users.update({"username": "mengday5"}, {"$push": {"hobby": "mm"}})
        WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
        > db.users.find()
        { "_id" : 1, "username" : "mengday5", "password" : "123456", "hobby" : [ "mm" ] }
        > db.users.update({"username": "mengday5"}, {"$push": {"hobby": "money"}})
        WriteResult({ "nMatched" : 1, "nUpserted" : 0, "nModified" : 1 })
        > db.users.find()
        { "_id" : 1, "username" : "mengday5", "password" : "123456", "hobby" : [ "mm", "money" ] }
        >
        ```
    
        
         
* 查询指定文档的数据
    
    `db.[documentName].find()`  查询所有
    `db.[documentName].findOne()`  查询第一条数据
* 更新文档数据
    
    
```cmd
// mong shell 是一种javascript shell, 可以定义变量、函数，调用函数
> x = 200
200
> x / 5
40
> Math.sin(Math.PI / 2)
1
// new Date():是创建一个日期对象，而Date()函数是返回日期字符串，注意日期类型数据和字符串数据不是一种数据，MongoDB Shell在操作日期字段的时候是操作的日期类型而不是字符串类型
> new Date()
ISODate("2017-07-27T00:18:34.370Z")
> Date()
Thu Jul 27 2017 08:18:37 GMT+0800
>
> "Hello, World!".replace("World", "MongoDB")
Hello, MongoDB!
//定义函数
> function factorial(n) {
... if(n <= 1) return 1;
... return n * factorial(n - 1);
... }
> factorial(5)
120
>

// db 是一个全局变量，存储当前的数据名称，当切换数据库时会自动更改db的值
> db
test
> show dbs
admin  0.000GB
local  0.000GB
> use local
switched to db local
> show tables
startup_log
> use test
switched to db test
> db.dropDatabase()
{ "ok" : 1 }
> db.xxx.insert

// 查看一个bson的大小，每个文档最大不超过16M
> var user = {"username": "mengday"}
> Object.bsonsize(user)
27
```
    
## 其他基础命令
* help命令：
    
    如果想知道某个对象下都有哪些函数可以使用help命令，直接使用help会列举出mongodb支持操作，使用db.help()会列举所有db对象所支持的所有操作，使用db.mycoll.help()可以列举所有集合对象对应的操作
```cmd
> help
        db.help()                    help on db methods
        db.mycoll.help()             help on collection methods
        sh.help()                    sharding helpers
        rs.help()                    replica set helpers
        help admin                   administrative help
        help connect                 connecting to a db help
        help keys                    key shortcuts
        help misc                    misc things to know
        help mr                      mapreduce

        show dbs                     show database names
        show collections             show collections in current database
        show users                   show users in current database
        show profile                 show most recent system.profile entries with time >= 1ms
        show logs                    show the accessible logger names
        show log [name]              prints out the last segment of log in memory, 'global' is default
        use <db_name>                set current database
        db.foo.find()                list objects in collection foo
        db.foo.find( { a : 1 } )     list objects in foo where a == 1
        it                           result of the last line evaluated; use to further iterate
        DBQuery.shellBatchSize = x   set default number of items to display on shell
        exit                         quit the mongo shell
// 查看所有数据级别的操作
> db.help()
// 查看集合级别的操作
> db.mycoll.help()
// 列举数据库命令
> db.listCommands()
```    
* 查看函数方法的实现或者查看方法的定义（比如忘记函数的参数了）可以通过输入函数名，不带小括号

```cmd
> db.foo.update
function (query, obj, upsert, multi) {
    var parsed = this._parseUpdate(query, obj, upsert, multi);
    ...
}

// 打印语句
> print("hello, mongodb")
hello, mongodb
>

// 执行js脚本
D:\Java\MongoDB\Server\bin>mongo script1.js script2.js
loading file: script1.js
I am script1.js
loading file: script2.js
I am script2.js

// 使用load（）函数加载脚本来执行
> load("script1.js")
I am script1.js
true

// script3.js
print(db.getMongo().getDBs());    // show dbs
db.getSisterDB("foo");    // use foo
db.users.insert({"username": "mengday", "age": 26})
print(db.getCollectionNames());  // show collections

> load("script3.js")
[object BSON]
users
true
>

// 使用脚本可以定义一些辅助的工具函数
tools.js
var connectTo = function (port, dbname) {
    if(!port) port = 27017;
    if(!dbname) dbname = "test";

    db = connect("localhost:" + port + "/" + dbname);

    return db;
}

> load("tools.js")
true
> typeof connectTo
function
> connectTo("27017", "admin")
connecting to: mongodb://localhost:27017/admin
MongoDB server version: 3.4.6
admin
>


//客户端启动时自动执行js脚本
//在用户的主目录下（如C:\Users\mengday）下创建一个.mongorc.js文件，该脚本可以做一些操作，如重写shell命令，禁掉一部分功能，如删除数据库，表等危险操作
// .mongorc.js
print("--------------MongoDB is started--------------");

var no = function(){
    print("not permission");
}

db.dropDatabase = DB.prototype.dropDatabase = no;
DBCollection.prototype = no;
DBCollection.prototype.dropIndex = no;

// 启动shell时会自动执行
D:\Java\MongoDB\Server\bin>mongo
MongoDB shell version v3.4.6
connecting to: mongodb://127.0.0.1:27017
MongoDB server version: 3.4.6
----------------MongoDB is started-----------------
> db
test
> db.dropDatabase()
not permission
>


// mongo 启动后给EDITOR变量赋值一个文本编辑器的位置，然后就可以使用edit命令来打开编辑某个变量了，编辑后保存，然后直接关掉编辑器即可，这对于一条命令或者变量比较长编辑比较方便，注意文本编辑器的位置不能包含空格，路径要使用/,不能使用\
EDITOR="D:/SublimeText/sublime_text.exe"
var user = {"username": "mengday", "nickname": "xxx"};
edit user

// 方式二：可以将该配置放在.mongorc.js中，以后就不用每次设置了
EDITOR="D:/SublimeText/sublime_text.exe";

```

    
## bson 扩充的数据类型

