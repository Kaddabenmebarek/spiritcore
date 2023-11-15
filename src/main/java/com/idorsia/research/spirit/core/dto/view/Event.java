package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.actelion.research.util.Pair;

public class Event implements Serializable {

	private static final long serialVersionUID = 7517789916754824881L;
	private List<Pair<?,?>> actions = new ArrayList<>();
    private LocalDateTime dateStart;
    private Duration duration;
    private String description;
    private String fullDescription;
    private String color;

    public LocalDateTime getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDateTime dateStart) {
        this.dateStart = dateStart;
    }

    public List<Pair<?,?>> getActions() {
        return actions;
    }


    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void addAction(Pair<?,?> pair) {
        actions.add(pair);
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }
}
