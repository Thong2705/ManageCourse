package com.mockproject.group3.enums;

public enum Template {
    PAYOUT_TEMPLATE("PayoutTemplate");

    private String templateName;

    Template(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }
}
