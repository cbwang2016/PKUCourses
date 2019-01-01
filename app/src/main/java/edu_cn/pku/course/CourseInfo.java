package edu_cn.pku.course;

import org.w3c.dom.Element;

/**
 * 为了方便管理课程列表，将每个课程的各种信息组成一个类。
 */
public class CourseInfo implements Comparable<CourseInfo> {
    private Element nNode;
    private int isPinned;

    public CourseInfo(Element nNode) {
        this.nNode = nNode;
        isPinned = 0;
    }

    public void setPinned(int i) {
        isPinned = i;
    }

    public int isPinned() {
        return isPinned;
    }

    public String getCourseId() {
        return nNode.getAttribute("bbid");
    }

    public String getRawCourseName() {
        return nNode.getAttribute("name");
    }

    public String getCourseName() {
        if (getRawCourseName().split("\\([0-9]").length != 2)
            return getRawCourseName();
        return getRawCourseName().split("\\([0-9]")[0];
    }

    public String getSemesterString() {
        if (getRawCourseName().split("\\([0-9]").length != 2)
            return "";
        return Utils.lastBetweenStrings(getRawCourseName(), "(", ")");
    }

    private int getSemesterYear() {
        if (getRawCourseName().split("\\([0-9]").length != 2)
            return -1;
        try {
            return Integer.parseInt(getSemesterString().split("-")[0]);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    private int getSemesterNumber() {
        if (getRawCourseName().split("\\([0-9]").length != 2)
            return -1;
        try {
            return Integer.parseInt(Utils.betweenStrings(getRawCourseName(), "学年第", "学期"));
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    @Override
    public int compareTo(CourseInfo comp) {
        if (this.isPinned != comp.isPinned)
            return comp.isPinned - this.isPinned;
        if (this.getSemesterYear() != comp.getSemesterYear())
            return comp.getSemesterYear() - this.getSemesterYear();
        if (this.getSemesterNumber() != comp.getSemesterNumber())
            return comp.getSemesterNumber() - this.getSemesterNumber();
        return this.getCourseName().compareTo(comp.getCourseName());
    }
}