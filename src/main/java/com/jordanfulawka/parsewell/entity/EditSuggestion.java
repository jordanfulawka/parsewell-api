package com.jordanfulawka.parsewell.entity;

import com.jordanfulawka.parsewell.entity.enums.EditType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name="edit_suggestions")
public class EditSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="application_id")
    private Application application;

    @Column(name="section", columnDefinition = "TEXT")
    private String section;

    @Column(name="before_text", columnDefinition = "TEXT")
    private String beforeText;

    @Column(name="after_text", columnDefinition = "TEXT")
    private String afterText;

    @Column(name="reason", columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name="edit_type")
    private EditType editType;

    @Column(name="order_index")
    private int orderIndex;

    public EditSuggestion() {}

    public EditSuggestion(Application application, String section, String beforeText, String afterText, String reason, EditType editType, int orderIndex) {
        this.application = application;
        this.section = section;
        this.beforeText = beforeText;
        this.afterText = afterText;
        this.reason = reason;
        this.editType = editType;
        this.orderIndex = orderIndex;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
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

    public EditType getEditType() {
        return editType;
    }

    public void setEditType(EditType editType) {
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
        return "EditSuggestion{" +
                "id=" + id +
                ", application=" + application +
                ", section='" + section + '\'' +
                ", beforeText='" + beforeText + '\'' +
                ", afterText='" + afterText + '\'' +
                ", reason='" + reason + '\'' +
                ", editType=" + editType +
                ", orderIndex=" + orderIndex +
                '}';
    }
}
