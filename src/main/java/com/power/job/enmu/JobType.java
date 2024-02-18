package com.power.job.enmu;

public enum JobType {
    FEIGN("feign"), HTTP("http");
    public final String value;

    JobType(String value) {
        this.value = value;
    }

}
