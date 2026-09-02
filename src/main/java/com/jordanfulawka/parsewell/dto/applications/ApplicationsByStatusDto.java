package com.jordanfulawka.parsewell.dto.applications;

public record ApplicationsByStatusDto(int numApplied, int numHeardBack, int numRejected, int numGhosted, int numOther) {
}
