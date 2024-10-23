package com.example.fluere.budget.model;

import org.springframework.hateoas.Link;

public interface LinkableDTO {

    void addLink(Link link);

    String PathMessage();
}
