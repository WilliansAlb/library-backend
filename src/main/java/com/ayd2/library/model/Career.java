package com.ayd2.library.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "career")
@Getter
@Setter
public class Career {
    @Id
    @Column(name = "career_id")
    private Long careerId;

    @Column(name = "name")
    private String name;

    public Career(Long careerId, String name){
        this.careerId = careerId;
        this.name = name;
    }

    public Career(){

    }
}
