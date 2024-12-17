package com.decoder.aiquizzer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long submissionId;

    @Column(nullable = false)
    private String userId;

    @ManyToOne
    private Quiz quiz;

    @Column(nullable = false)
    private Date submissionDate;

    @Column(nullable = false)
    private int score;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Response> responses;
}
