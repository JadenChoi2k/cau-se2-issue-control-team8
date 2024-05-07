package org.cause2.team8.domain.project;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IssuePriority {
    BLOCKER("당장해야하는", 5),
    CRITICAL("치명적인", 4),
    MAJOR("주요한", 3),
    MINOR("마이너", 2),
    TRIVIAL("사소한", 1);

    private final String description;
    private final int level;
}
