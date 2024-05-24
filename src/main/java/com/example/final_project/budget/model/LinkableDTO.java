package com.example.final_project.budget.model;

import org.springframework.hateoas.Link;

public interface LinkableDTO {

    void addLink(Link link);

    String PathMessage();
}
