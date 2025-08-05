package com.kukharev.model;

public class HandProbabilities {

    private final double highCardValue;
    private final double pairValue;
    private final double twoPairValue;
    private final double setValue;
    private final double straightValue;
    private final double flushValue;
    private final double fullHouseValue;
    private final double quadsValue;
    private final double straightFlushValue;
    private final double royalValue;

    public HandProbabilities(double highCardValue,
                             double pairValue,
                             double twoPairValue,
                             double setValue,
                             double straightValue,
                             double flushValue,
                             double fullHouseValue,
                             double quadsValue,
                             double straightFlushValue,
                             double royalValue) {
        this.highCardValue = highCardValue;
        this.pairValue = pairValue;
        this.twoPairValue = twoPairValue;
        this.setValue = setValue;
        this.straightValue = straightValue;
        this.flushValue = flushValue;
        this.fullHouseValue = fullHouseValue;
        this.quadsValue = quadsValue;
        this.straightFlushValue = straightFlushValue;
        this.royalValue = royalValue;
    }

    public double highCardValue() { return highCardValue; }
    public double pairValue() { return pairValue; }
    public double twoPairValue() { return twoPairValue; }
    public double setValue() { return setValue; }
    public double straightValue() { return straightValue; }
    public double flushValue() { return flushValue; }
    public double fullHouseValue() { return fullHouseValue; }
    public double quadsValue() { return quadsValue; }
    public double straightFlushValue() { return straightFlushValue; }
    public double royalValue() { return royalValue; }

    public double getHighCard() {
        return highCardValue;
    }
    public double getOnePair() {
        return pairValue;
    }
    public double getTwoPair() {
        return twoPairValue;
    }
    public double getSet() {
        return setValue;
    }
    public double getStraight() {
        return straightValue;
    }
    public double getFlush() {
        return flushValue;
    }
    public double getFullHouse() {
        return fullHouseValue;
    }
    public double getQuads() {
        return quadsValue;
    }
    public double getStraightFlush() {
        return straightFlushValue;
    }
    public double getRoyalFlush() {
        return royalValue;
    }
}
