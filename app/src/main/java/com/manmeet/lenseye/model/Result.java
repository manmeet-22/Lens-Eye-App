package com.manmeet.lenseye.model;

import java.io.Serializable;

public class Result {

    private String resultName;
    private Float resultAccuracy;

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public Float getResultAccuracy() {
        return resultAccuracy;
    }

    public void setResultAccuracy(Float resultAccuracy) {
        this.resultAccuracy = resultAccuracy;
    }

    public Result(String resultName, Float resultAccuracy) {
        this.resultName = resultName;
        this.resultAccuracy = resultAccuracy;
    }

    @Override
    public String toString() {
        return "Result{" +
                "resultName='" + resultName + '\'' +
                ", resultAccuracy=" + resultAccuracy +
                '}';
    }
}
