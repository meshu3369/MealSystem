package com.example.meshu.mealsystem;

import java.io.Serializable;

/**
 * Created by Engr Meshu on 8/17/2015.
 */
public class MealPerson implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name,email,password,house,deposit,meal,description,due;
    private int admin,due_pay,id;

    @Override
    public String toString() {
        return "MealPerson{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", house='" + house + '\'' +
                ", deposit='" + deposit + '\'' +
                ", meal='" + meal + '\'' +
                ", description='" + description + '\'' +
                ", due='" + due + '\'' +
                ", admin=" + admin +
                ", due_pay=" + due_pay +
                ", id=" + id +
                '}';
    }

    public MealPerson(int id,String name, String email, String password, String house, String deposit, String meal, String description, String due, int admin, int due_pay) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.house = house;

        this.deposit = deposit;
        this.meal = meal;
        this.description = description;
        this.due = due;
        this.admin = admin;
        this.due_pay = due_pay;
        this.id = id;
    }
    public MealPerson(int id,String name, String house, String deposit, String meal, String description, String due, int due_pay) {
        this.name = name;
        this.house = house;

        this.deposit = deposit;
        this.meal = meal;
        this.description = description;
        this.due = due;
        this.due_pay = due_pay;
        this.id = id;
    }

    public MealPerson(String name, String email, String password, String house) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.house = house;

    }

    public MealPerson(String name, String email, String password, String house, int admin) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.house = house;
        this.admin = admin;
    }

    public MealPerson(String name, String email, String password, String house, String deposit, String meal, String description, String due, int admin, int due_pay) {

        this.name = name;
        this.email = email;
        this.password = password;
        this.house = house;
        this.deposit = deposit;
        this.meal = meal;
        this.description = description;
        this.due = due;
        this.admin = admin;
        this.due_pay = due_pay;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }


    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDue() {
        return due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public int getDue_pay() {
        return due_pay;
    }

    public void setDue_pay(int due_pay) {
        this.due_pay = due_pay;
    }
}
