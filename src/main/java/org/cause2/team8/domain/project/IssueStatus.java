package org.cause2.team8.domain.project;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IssueStatus {
    NEW(0), ASSIGNED(1), FIXED(2), RESOLVED(3), CLOSED(4), REOPENED(1);

    private final int stage;

    public IssueStatus getNext() {
        for (IssueStatus status : IssueStatus.values()) {
            if (status.stage == this.stage + 1 && status != REOPENED) {
                return status;
            }
        }
        return null;
    }

    public IssueStatus getPrev() {
        for (IssueStatus status : IssueStatus.values()) {
            if (status.stage == this.stage - 1 && status != REOPENED) {
                return status;
            }
        }
        return null;
    }
}
