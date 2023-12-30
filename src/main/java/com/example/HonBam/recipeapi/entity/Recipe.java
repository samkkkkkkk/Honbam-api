package com.example.HonBam.recipeapi.entity;

import lombok.*;

import javax.persistence.*;

@Getter @Setter
@ToString
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "data_id")
    private int dataId;

    @Column(name = "cocktail_name")
    private String cocktailName;

    @Column(name = "cocktail_img")
    private String cocktailImg;


    private String recipe;

    @Column(name = "recipe_detail")
    private String recipeDetail;

}
