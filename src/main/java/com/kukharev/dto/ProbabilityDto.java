package com.kukharev.dto;

/* goes to the front */
public record ProbabilityDto(double[] hero, double[] opp) {
    /*
      hero[0..9] : highCard, pair, twoPair, set, straight,
                   flush, fullHouse, quads, straightFlush, royal
      opp[0..8]  : pair, twoPair, set, straight,
                   flush, fullHouse, quads, straightFlush, royal
    */
}
