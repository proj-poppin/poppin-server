package com.poppin.poppinserver.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
@Table(name = "taste_popup")
public class TastePopup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fasion_beauty")
    private Boolean fasionBeauty;

    @Column(name = "character")
    private Boolean character;

    @Column(name = "food_beverage")
    private Boolean foodBeverage;

    @Column(name = "webtoon_ani")
    private Boolean webtoonAni;

    @Column(name = "interior_things")
    private Boolean interiorThings;

    @Column(name = "movie")
    private Boolean movie;

    @Column(name = "musical")
    private Boolean musical;

    @Column(name = "sports")
    private Boolean sports;

    @Column(name = "game")
    private Boolean game;

    @Column(name = "it_tech")
    private Boolean itTech;

    @Column(name = "kpop")
    private Boolean kpop;

    @Column(name = "alchol")
    private Boolean alchol;

    @Column(name = "animal_plant")
    private Boolean animalPlant;
}
