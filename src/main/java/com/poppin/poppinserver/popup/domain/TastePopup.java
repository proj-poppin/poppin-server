package com.poppin.poppinserver.popup.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(name = "fashion_beauty", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean fashionBeauty;

    @Column(name = "characters", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean characters;

    @Column(name = "food_beverage", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean foodBeverage;

    @Column(name = "webtoon_ani", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean webtoonAni;

    @Column(name = "interior_things", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean interiorThings;

    @Column(name = "movie", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean movie;

    @Column(name = "musical", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean musical;

    @Column(name = "sports", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean sports;

    @Column(name = "game", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean game;

    @Column(name = "it_tech", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean itTech;

    @Column(name = "kpop", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean kpop;

    @Column(name = "alcohol", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean alcohol;

    @Column(name = "animal_plant", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean animalPlant;

    @Column(name = "etc", columnDefinition = "TINYINT(1)")
    private Boolean etc;

    @Builder
    public TastePopup(Boolean fasionBeauty, Boolean characters, Boolean foodBeverage,
                      Boolean webtoonAni, Boolean interiorThings, Boolean movie,
                      Boolean musical, Boolean sports, Boolean game,
                      Boolean itTech, Boolean kpop, Boolean alcohol,
                      Boolean animalPlant, Boolean etc) {
        this.fashionBeauty = fasionBeauty;
        this.characters = characters;
        this.foodBeverage = foodBeverage;
        this.webtoonAni = webtoonAni;
        this.interiorThings = interiorThings;
        this.movie = movie;
        this.musical = musical;
        this.sports = sports;
        this.game = game;
        this.itTech = itTech;
        this.kpop = kpop;
        this.alcohol = alcohol;
        this.animalPlant = animalPlant;
        this.etc = etc;
    }

    public void update(Boolean fasionBeauty, Boolean characters, Boolean foodBeverage,
                       Boolean webtoonAni, Boolean interiorThings, Boolean movie,
                       Boolean musical, Boolean sports, Boolean game,
                       Boolean itTech, Boolean kpop, Boolean alcohol,
                       Boolean animalPlant, Boolean etc) {
        this.fashionBeauty = fasionBeauty;
        this.characters = characters;
        this.foodBeverage = foodBeverage;
        this.webtoonAni = webtoonAni;
        this.interiorThings = interiorThings;
        this.movie = movie;
        this.musical = musical;
        this.sports = sports;
        this.game = game;
        this.itTech = itTech;
        this.kpop = kpop;
        this.alcohol = alcohol;
        this.animalPlant = animalPlant;
        this.etc = etc;
    }
}
