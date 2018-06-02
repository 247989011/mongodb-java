package com.mini.mongodb;

import com.google.gson.Gson;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * The type Mini mogodb.
 */
public class MiniMogodb {
    private static String _uuid = "_uuid";


    /**
     * 创建数据库
     * Create data base.
     *
     * @param path the path
     */
    public void createDataBase(String path) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);

            Document doc = new Document();
            Element root = new Element("database");
            Element test = new Element("table");
            test.setAttribute("name", "test");
            Element index = new Element("table");
            index.setAttribute("name", "system.indexs");
            Element users = new Element("table");
            users.setAttribute("name", "system.users");

            root.addContent(test);
            root.addContent(index);
            root.addContent(users);

            doc.setRootElement(root);

            XMLOutputter out = new XMLOutputter();
            out.output(doc,fos);

            System.out.println("create database success!");

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    /**
     * 表插入数据
     * Add data.
     *
     * @param path   the path   数据库名
     * @param table  the table  表名
     * @param object the object 文档
     */
    public boolean addData(String path, String table, Object object) {
        //查出数据库，表
        FileInputStream fis = null;
        FileOutputStream fos = null;
        Gson gson = new Gson();
        try {
           fis = new FileInputStream(path);

            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(fis);
            Element root = document.getRootElement();

            List<Element> elements = root.getChildren();
            boolean flag = false;
            for (Element tab : elements) {
                if (tab.getAttributeValue("name").equals(table.trim())) {
                    Map base = new HashMap();
                    base.put(_uuid, UUID.randomUUID().toString());
                    Element data = new Element("data");
                    //转换对象为json
                    String json = gson.toJson(object);
                    Map mp = gson.fromJson(json, Map.class);
                    base.putAll(mp);
                    //插入数据
                    data.addContent(gson.toJson(base));
                    tab.addContent(data);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                Element test = new Element("table");
                test.setAttribute("name", table);
                Map base = new HashMap();
                base.put(_uuid, UUID.randomUUID().toString());
                Element data = new Element("data");
                //转换对象为json
                String json = gson.toJson(object);
                Map mp = gson.fromJson(json, Map.class);
                base.putAll(mp);
                //插入数据
                data.addContent(gson.toJson(base));
                test.addContent(data);
                root.addContent(test);
            }
            //写出
            fos = new FileOutputStream(path);
            XMLOutputter out = new XMLOutputter();
            out.output(document,fos);
            System.out.println("插入一条记录");
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("数据库不存在");
            return false;
        } catch (Exception e) {
            System.out.println("插入失败");
            return false;
        }finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public boolean updateData(String path, String table, Object object) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        Gson gson = new Gson();
        try {
            fis = new FileInputStream(path);
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(fis);

            Element database = document.getRootElement();

            List<Element> tables = database.getChildren();
            boolean flag = false;
            for (Element t : tables) {
                if (t.getAttributeValue("name").equals(table)) {
                    List<Element> records = t.getChildren();
                    for (Element record : records) {
                        //判断id
                        Map data = gson.fromJson(record.getText(), Map.class);
                        Map newData = gson.fromJson(gson.toJson(object), Map.class);
                        if (data.get(_uuid).equals(newData.get(_uuid))) {
                            data.putAll(newData);
                            Element da = new Element("data");
                            da.setText(gson.toJson(data));
                            t.removeContent(record);
                            t.addContent(da);
                            flag = true;
                            break;
                        }
                    }
                }
            }
            //写出
            fos = new FileOutputStream(path);
            XMLOutputter out = new XMLOutputter();
            out.output(document,fos);
            if (flag) {
                System.out.println("更新成功");
            }{
                System.out.println("更新失败");
            }
            return flag;
        } catch (Exception e) {
            System.out.println("更新失败");
            e.printStackTrace();
            return false;
        }finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }



    }


    /**
     * 删除记录
     * Delete data int.
     *
     * @param path   the path
     * @param table  the table
     * @param object the object
     *
     * @return the int
     */
    public int deleteData(String path, String table, Object object) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        Gson gson = new Gson();
        int i = 0;
        try {
            fis = new FileInputStream(path);
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(fis);
            Element root = document.getRootElement();

            List<Element> tables = root.getChildren();

            for (Element t : tables) {
                if (t.getAttributeValue("name").equals(table)) {
                    List<Element> records = t.getChildren();
                    for (Element r : records) {
                        Map d = gson.fromJson(r.getText(), Map.class);
                        Map nd = gson.fromJson(gson.toJson(object), Map.class);
                        if (d.get(_uuid).equals(nd.get(_uuid))) {
                            t.removeContent(r);
                            i ++;
                            break;
                        }
                    }
                }
            }
            fos = new FileOutputStream(path);
            XMLOutputter out = new XMLOutputter();
            out.output(document,fos);
            System.out.println("删除成功");
            return i;
        } catch (Exception e) {
            System.out.println("删除失败");
            return 0;
        }finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取表全部数据
     * Load table datas.
     *
     * @param path  the path
     * @param table the table
     */
    public List<Map> loadTableDatas(String path, String table) {
        List<Map> ret = new ArrayList<Map>();
        Gson gson = new Gson();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(fis);
            Element root = document.getRootElement();
            List<Element> tables = root.getChildren();
            for (Element t : tables) {
                if(t.getAttributeValue("name").equals(table)) {
                    List<Element> records = t.getChildren();
                    for (Element r : records) {
                        Map map = gson.fromJson(r.getText(), Map.class);
                        ret.add(map);
                        System.out.println(map.toString());
                    }
                }
            }

            return  ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
