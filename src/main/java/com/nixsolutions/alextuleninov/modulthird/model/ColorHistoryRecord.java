package com.nixsolutions.alextuleninov.modulthird.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "Color_history")
public class ColorHistoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "light_id")
    @Access(AccessType.PROPERTY)
    private Light light;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "old_color_id")
    @Access(AccessType.PROPERTY)
    private Color oldColor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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

    public void setOldColor(Color oldColor) {
        this.oldColor = oldColor;
    }

    public Color getNewColor() {
        return newColor;
    }

    public void setNewColor(Color newColor) {
        this.newColor = newColor;
    }

    public Timestamp getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Timestamp changedAt) {
        this.changedAt = changedAt;
    }

}
