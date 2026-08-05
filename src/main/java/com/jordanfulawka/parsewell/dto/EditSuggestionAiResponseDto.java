package com.jordanfulawka.parsewell.dto;

public class EditSuggestionAiResponseDto {

    private String section;
    private String beforeText;
    private String afterText;
    private String reason;
    private String editType;
    private int orderIndex;

    public EditSuggestionAiResponseDto() {}

    public EditSuggestionAiResponseDto(String section, String beforeText, String afterText, String reason, String editType, int orderIndex) {
        this.section = section;
        this.beforeText = beforeText;
        this.afterText = afterText;
        this.reason = reason;
        this.editType = editType;
        this.orderIndex = orderIndex;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getBeforeText() {
        return beforeText;
    }

    public void setBeforeText(String beforeText) {
        this.beforeText = beforeText;
    }

    public String getAfterText() {
        return afterText;
    }

    public void setAfterText(String afterText) {
        this.afterText = afterText;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getEditType() {
        return editType;
    }

    public void setEditType(String editType) {
        this.editType = editType;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Override
    public String toString() {
        return "EditSuggestionAiResponseDto{" +
                "section='" + section + '\'' +
                ", beforeText='" + beforeText + '\'' +
                ", afterText='" + afterText + '\'' +
                ", reason='" + reason + '\'' +
                ", editType='" + editType + '\'' +
                ", orderIndex=" + orderIndex +
                '}';
    }
}
