package com.jordanfulawka.parsewell.entity;

import com.jordanfulawka.parsewell.entity.enums.EditType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="edit_suggestion")
public class EditSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="application_id")
    private Application application;

    @Column(name="section")
    private String section;

    @Column(name="before_text")
    private String beforeText;

    @Column(name="after_text")
    private String afterText;

    @Column(name="reason")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name="edit_type")
    private EditType editType;

    @Column(name="order_index")
    private int orderIndex;

}
