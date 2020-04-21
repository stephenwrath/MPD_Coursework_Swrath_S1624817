//Stephen Wrath S1624817
package com.example.mpd_coursework;
public class Roadwork {
    private String title;
    private String desc;
    private String point;


    public Roadwork()
    {
        title = "";
        desc = "";
        point = "";
    }

    public Roadwork(String atitle, String adesc, String apoint)
    {
        title = atitle;
        desc = adesc;
        point = apoint;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String atitle)
    {
        title = atitle;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String adesc)
    {
        desc = adesc;
    }

    public String getPoint()
    {
        return point;
    }

    public void setPoint(String apoint)
    {
        point = apoint;
    }


    public String toString()
    {
        String temp;
        temp = title + " " + desc + "" + point;
        return temp;
    }
}
