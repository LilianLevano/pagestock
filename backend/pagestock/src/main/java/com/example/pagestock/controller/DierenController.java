package com.example.pagestock.controller;

import com.example.pagestock.model.Dieren;
import com.example.pagestock.model.DierenDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;


@Controller
public class DierenController {

    private DierenDataSource dierenDataSource;

    @Autowired
    public DierenController(DierenDataSource dierenDataSource) {
        this.dierenDataSource = dierenDataSource;
    }

    @GetMapping("/api/dieren")
    @ResponseBody
    public ArrayList<Dieren> getDieren(){
        return dierenDataSource.getDieren();
    }

}
