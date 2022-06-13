package com.devivan.economicsupervision.Objects.Designer;

import com.devivan.economicsupervision.Objects.Designer.Works.Work;

import java.util.ArrayList;

public class Designer {

    private String name;
    private String url;
    private ArrayList<Work> works;

    public Designer() {

    }

    public Designer(String name, String url, ArrayList<Work> works) {
        this.name = name;
        this.url = url;
        this.works = works;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<Work> getWorks() {
        return works;
    }

    public void setWorks(ArrayList<Work> works) {
        this.works = works;
    }
}
