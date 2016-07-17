package com.wuzy.netty.codec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.wuzy.netty.util.KryoUtil;

/**
 * Created by wuzhengyun on 16/7/16.
 */
public class KryoDemo {

    public static void main(String[] args) {
       /* Kryo kryo = new Kryo();
        //kryo.register(Student.class);
        //序列化
        Output output = new Output(1024);
        Student student = new Student(1,"sky");
       // kryo.writeObject(output,student);
        kryo.writeClassAndObject(output,student);
        output.flush();
        byte[] bytes = output.toBytes();
        System.out.println("bytes: "+bytes);

        //反序列化
        Input input = new Input(bytes);
        Student student1 = (Student)kryo.readClassAndObject(input);
       // Student student1 = kryo.readObject(input,Student.class);
        System.out.println(student1.getId()+","+student1.getName());

        output.close();
        input.close();
        */


      Student student = new Student(1,"sky");
       byte[] bytes =  KryoUtil.serial(student);

        Object obj = KryoUtil.deserial(bytes);
        Student student1 = (Student)obj;
        System.out.println(student1.getId()+","+student1.getName());
    }
}

class Student{
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Student() {
    }
}
