package com.kukharev.dto;

import java.util.List;

/* comes from the frontend */
public record RequestDto(
        List<String> hole,   // two pocket
        List<String> board,  // 0-5 table cards
        int activeOpp        // number of active opponents
) {}
