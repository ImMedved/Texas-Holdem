package com.kukharev.core;
import com.kukharev.model.HandProbabilities;
import java.util.List;

public class ProbabilityCalculator {
    public static HandProbabilities computeHero(List<String> hole, List<String> board, int activeOpponents) {
        return HeroProbabilityCalculator.calculate(hole, board, activeOpponents);
    }

    public static HandProbabilities computeOpponents(List<String> hole, List<String> board, int activeOpponents) {
        return OpponentProbabilityCalculator.calculate(hole, board, activeOpponents);
    }
}
