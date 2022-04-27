package com.ams.flinksql;

public class Human {
    
    private String name;

    private String age;

    public Human() {
    }

    public Human(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Human [age=" + age + ", name=" + name + "]";
    }

    

}
