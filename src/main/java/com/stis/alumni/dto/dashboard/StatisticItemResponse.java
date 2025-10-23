package com.stis.alumni.dto.dashboard;

public class StatisticItemResponse {

    private String label;
    private long count;
    private double percentage;

    public StatisticItemResponse() {
    }

    public StatisticItemResponse(String label, long count, double percentage) {
        this.label = label;
        this.count = count;
        this.percentage = percentage;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
