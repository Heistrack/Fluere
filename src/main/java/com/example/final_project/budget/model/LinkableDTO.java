package com.example.final_project.budget.model;

import org.springframework.hateoas.Link;

import java.util.UUID;

public interface LinkableDTO {

    void addLink(Link link);

    UUID getId();
}
