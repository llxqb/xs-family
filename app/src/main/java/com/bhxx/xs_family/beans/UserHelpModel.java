package com.bhxx.xs_family.beans;

import java.io.Serializable;

public class UserHelpModel implements Serializable {
    private int uhId;
    private String uhQuestion;
    private String uhAnswer;
    private String uhCreateTime;

    public int getUhId() {
        return uhId;
    }

    public void setUhId(int uhId) {
        this.uhId = uhId;
    }

    public String getUhQuestion() {
        return uhQuestion;
    }

    public void setUhQuestion(String uhQuestion) {
        this.uhQuestion = uhQuestion;
    }

    public String getUhAnswer() {
        return uhAnswer;
    }

    public void setUhAnswer(String uhAnswer) {
        this.uhAnswer = uhAnswer;
    }

    public String getUhCreateTime() {
        return uhCreateTime;
    }

    public void setUhCreateTime(String uhCreateTime) {
        this.uhCreateTime = uhCreateTime;
    }
}
