package com.nixsolutions.alextuleninov.modulthird.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "Colors")
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToOne(mappedBy = "color")
    private Light light;

    @OneToOne(mappedBy = "oldColor")
    private ColorHistoryRecord colorHistoryRecordOld;

    @OneToOne(mappedBy = "newColor")
    private ColorHistoryRecord colorHistoryRecordNew;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Light getLight() {
        return light;
    }

    public void setLight(Light light) {
        this.light = light;
    }

    public ColorHistoryRecord getColorHistoryRecordOld() {
        return colorHistoryRecordOld;
    }

    public void setColorHistoryRecordOld(ColorHistoryRecord colorHistoryRecord) {
        this.colorHistoryRecordOld = colorHistoryRecord;
    }

    public ColorHistoryRecord getColorHistoryRecordNew() {
        return colorHistoryRecordNew;
    }

    public void setColorHistoryRecordNew(ColorHistoryRecord colorHistoryRecordNew) {
        this.colorHistoryRecordNew = colorHistoryRecordNew;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return Objects.equals(name, color.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
