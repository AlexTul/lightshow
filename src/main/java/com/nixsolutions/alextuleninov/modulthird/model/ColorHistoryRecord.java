package com.nixsolutions.alextuleninov.modulthird.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "Color_history")
public class ColorHistoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(name = "ligth_id")
    @Access(AccessType.PROPERTY)
    private Light light;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(name = "old_color_id")
    @Access(AccessType.PROPERTY)
    private Color oldColor;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(name = "new_color_id")
    @Access(AccessType.PROPERTY)
    private Color newColor;

    @Column(name = "changed_at", nullable = false)
    private Timestamp changedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Light getLight() {
        return light;
    }

    public void setLight(Light light) {
        this.light = light;
    }

    public Color getOldColor() {
        return oldColor;
    }

    public void setOldColor(Color old_color) {
        this.oldColor = old_color;
    }

    public Color getNewColor() {
        return newColor;
    }

    public void setNewColor(Color new_color) {
        this.newColor = new_color;
    }

    public Timestamp getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Timestamp changedAt) {
        this.changedAt = changedAt;
    }
}
