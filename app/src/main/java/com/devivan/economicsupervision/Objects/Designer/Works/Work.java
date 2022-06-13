package com.devivan.economicsupervision.Objects.Designer.Works;

public class Work {

    private int resource;
    private String animation;
    private String url;

    public Work() {

    }

    public Work(int resource, String url) {
        this.resource = resource;
        this.url = url;
    }

    public Work(String animation, String url) {
        this.animation = animation;
        this.url = url;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public String getAnimation() {
        return animation;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
