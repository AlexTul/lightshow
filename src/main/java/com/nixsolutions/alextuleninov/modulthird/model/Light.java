package com.nixsolutions.alextuleninov.modulthird.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Lights")
public class Light {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String label;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "color_id")
    @Access(AccessType.PROPERTY)
    private Color color;

    @Column(nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "light")
    List<ColorHistoryRecord> colorHistoryRecordList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<ColorHistoryRecord> getColorHistoryRecordList() {
        return colorHistoryRecordList;
    }

    public void setColorHistoryRecordList(List<ColorHistoryRecord> colorHistoryRecordList) {
        this.colorHistoryRecordList = colorHistoryRecordList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Light light = (Light) o;
        return Objects.equals(label, light.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

}
