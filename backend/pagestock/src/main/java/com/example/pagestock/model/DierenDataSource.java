package com.example.pagestock.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DierenDataSource {

    private ArrayList<Dieren> dieren;

    public DierenDataSource() {
        dieren = new ArrayList<>();
        dieren.add(new Dieren("Igor", 8));
        dieren.add(new Dieren("Winky", 2));
    }

    public  ArrayList<Dieren> getDieren() {
        return dieren;
    }
}
