package com.kukharev.core;

import com.kukharev.model.HandProbabilities;

import java.util.*;

public class HeroProbabilityCalculator {
    // --- Calls ---
    public static HandProbabilities calculate(List<String> hole, List<String> board, int activeOpponents) {
        HandProbabilities handProbabilitiesOnPreflop = null;
        HandProbabilities handProbabilitiesOnFlop = null;
        HandProbabilities handProbabilitiesOnTurn = null;
        HandProbabilities handProbabilitiesOnRiver = null;

        // Check, what stage of the game it is:
        if (board.isEmpty()) {
            // Preflop
            handProbabilitiesOnPreflop = calculatePreflop(hole, board, activeOpponents);
            return handProbabilitiesOnPreflop;
        } else if (board.size() == 3) {
            // Flop
            handProbabilitiesOnFlop =  calculateFlop(hole, board, activeOpponents);
            return handProbabilitiesOnFlop;
        } else if (board.size() == 4) {
            // Turn
            handProbabilitiesOnTurn =  calculateTurn(hole, board, activeOpponents);
            return handProbabilitiesOnTurn;
        } else if (board.size() == 5) {
            // River
            handProbabilitiesOnRiver =  calculateRiver(hole, board, activeOpponents);
            return handProbabilitiesOnRiver;
        }
        return new HandProbabilities(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    // --- Preflop calculations --- done/tested
    public static HandProbabilities calculatePreflop(List<String> hole, List<String> board, int activeOpponents) {
        // Check all combinations of hole cards and calculate probabilities
        // Some of the probabilities were precalculated and stored in the database
        double highCard = 0;
        double onePair = 0;
        double twoPair = 0;
        double set = 0; // Three of a kind
        double straight = 0;
        double flush = 0;
        double fullHouse = 0;
        double quads = 0.72; // Four of a kind
        double straightFlush = 0;
        double royalFlush = 0;

        // Check if we have a pocket pair
        if (hole.get(0).charAt(0) == hole.get(1).charAt(0)) {
            onePair = 100;
            // Stored probability for two pair if player got pocket pair
            // p_total = comb(2, 2) * comb(48, 3) / comb(50, 5) + comb(12, 1) * comb(4, 2) * comb(46, 3) / comb(50, 5) <— incorrect formula
            // p_twoPair = comb(12, 1) * comb(4, 2) * comb(46, 3) / comb(50, 5)
            twoPair = 36.87;
            // Stored probability for set if player got pocket pair
            // p_set = 1 - (comb(48, 5) / comb(50, 5)) OR p_set = comb(2, 1) * comb(48, 4) / comb(50, 5) + quads
            set = 19.18;
            fullHouse = 0.73;
            // quads = comb(2, 2) * (comb(48, 3) / comb(50, 5))
            quads = 0.82;
            // comb(11, 4) * comb(39, 1) / comb (50, 5)
            flush = 1.21;
            straight = computeStraightProbability(hole, board);
            straightFlush = straight * 0.04;

            // For royal flush we need to have A, K, Q, J, T of the same suit. We have already checked that we have pocket pair.
            // So we just need to make sure than pair is in the range of T, J, Q, K, A.
            for (String card : hole) {
                char rank = card.charAt(0);
                if (rank == 'T' || rank == 'J' || rank == 'Q' || rank == 'K' || rank == 'A') {
                    royalFlush = 0.015; // Pair is in the range
                } else {
                    royalFlush = 0.0; // No cards in the range
                }
            }

        // Check if our 2 card are same suit
        } else if (hole.get(0).charAt(1) == hole.get(1).charAt(1)) {
            // No pocket pair, but suited cards, high card calculation is in another place
            // 2 * comb(3, 1) * comb(47, 4) / comb(50, 5)
            onePair = 43.86;
            // comb(3, 1)^2 * comb(46, 3) / comb(50, 5)
            twoPair = 23.50;
            // 2 * comb(3, 2) * comb(47, 3) / comb(50, 5)
            set = 4.83;
            // comb(3, 2)^2 * comb(48, 1) / comb(50, 5)
            fullHouse = 0.53;
            // 2 * comb(3, 3) * comb(47, 2) / comb(50, 5)
            quads = 0.09;
            // comb(11, 3) * comb(39, 2) / comb(50, 5) + comb(11, 2) * comb(39, 1) / comb(50, 5) + comb(11, 5) / comb(50, 5)
            flush = 6.4;

            /*Straight calculation:
            There are 10 possible straights, each can be made with 5 combinations of cards and here are all the straights:
            [A 2 3 4 5]
            [2 3 4 5 6]
            [3 4 5 6 7]
            [4 5 6 7 8]
            [5 6 7 8 9]
            [6 7 8 9 T]
            [7 8 9 T J]
            [8 9 T J Q]
            [9 T J Q K]
            [T J Q K A]
            for each straight-diapason: if both ranks from hand are in range
            then we can make a straight with 3 cards from board, if one of the ranks is in range then we can make a straight with 4 cards from board,
            but we are not calculating for ranges without cards from hand.
            Let:
            needed = {r1, r2, r3, r4, r5} and have = {R1, R2}, so missing = needed - have and k = |missing| is how many more ranks do you need to get from 5 random ones.
            if both cards from hand are in range: let k = number of missing ranks (5 - number of uniques from hand in this range):
            */
            straight = computeStraightProbability(hole, board);
            straightFlush = straight * 0.04;

            // For royal flush we need to have A, K, Q, J, T of the same suit. We have already checked that we have suited cards.
            // To reduce calculations we just need to check how many cards from the range T, J, Q, K, A we have in hand.
            // If we have one card from the range and another out of it, then is 0.015% for royal. If both, then 0.092%.
            int count = 0;
            for (String card : hole) {
                char rank = card.charAt(0);
                if (rank == 'T' || rank == 'J' || rank == 'Q' || rank == 'K' || rank == 'A') {
                    count++;
                }
            }
            if (count == 2) {
                royalFlush = 0.092; // Both cards are in the range
            } else if (count == 1) {
                royalFlush = 0.015; // One card is in the range
            } else {
                royalFlush = 0.0; // No cards in the range
            }


        } else {
            // No pocket pair and no suited cards
            // Same calculations as above, but without suited cards
            onePair = 43.86;
            twoPair = 23.50;
            set = 4.83;
            fullHouse = 0.86;
            quads = 0.09;
            // Flush as if we have a pocket pair
            flush = 1.21;
            straight = computeStraightProbability(hole, board);
            straightFlush = straight * 0.04;

            for (String card : hole) {
                char rank = card.charAt(0);
                if (rank == 'T' || rank == 'J' || rank == 'Q' || rank == 'K' || rank == 'A') {
                    royalFlush = 0.015;
                } else {
                    royalFlush = 0.0;
                }
            }

        }
        highCard = computeHighCardProbability(hole, board, activeOpponents);


        return new HandProbabilities(highCard, onePair, twoPair, set, straight, flush, fullHouse, quads, straightFlush, royalFlush);
    }

    // --- Flop calculations --- done/tested
    public static HandProbabilities calculateFlop(List<String> hole, List<String> board, int activeOpponents) {
        double highCard = computeHighCardProbability(hole, board, activeOpponents);
        double onePair = 0;
        double twoPair = 0;
        double set = 0; // Three of a kind
        double straight = computeStraightProbability(hole, board);
        double flush = computeFlushProbability(hole, board);
        double fullHouse = 0;
        double quads = computeQuadsProbability(hole, board);    // Four of a kind
        double straightFlush = computeStraightFlushProbability(hole, board);
        double royalFlush = computeRoyalFlushProbability(hole, board);

        /*
        // To save resources, we are starting from the top combination and going down. In case we have a higher combination, we can skip the rest and fill them with 100.
        // Starting from royal.
        // Check if royal flush is possible (player has A, K, Q, J or T in their hand).
        int count = 0;
        for (String card : hole) {
            char rank = card.charAt(0);
            if (rank == 'T' || rank == 'J' || rank == 'Q' || rank == 'K' || rank == 'A') {
                count++;
            }
            // Check if count == 2, are both cards of the same suit.
            if (count == 2 && card.charAt(1) != hole.get(0).charAt(1)) {
                // Here we check cards from the board, if they are in the range T, J, Q, K, A and of the same suit as bout cards in hand we increase the count once again.
                if (board.size() > 0) {
                    for (String boardCard : board) {
                        char boardRank = boardCard.charAt(0);
                        char boardSuit = boardCard.charAt(1);
                        if ((boardRank == 'T' || boardRank == 'J' || boardRank == 'Q' || boardRank == 'K' || boardRank == 'A') &&
                                (boardSuit == hole.get(0).charAt(1) || boardSuit == hole.get(1).charAt(1))) {
                            count++;
                        }
                    }
                }
                // If count is 5, then we have royal flush, return and fill the rest with 100.
                if (count == 5) {
                    return new HandProbabilities(100, 100, 100, 100, 100, 100, 100, 100, 100, 100);
                } else if (count == 4) {
                    // If we have 4, then 1 card is missing to royal.
                    royalFlush = 4.255;
                } else if (count == 3) {
                    // If count is 3, then we have straight flush with two cards are missing.
                    straightFlush = 0.092;
                } else if (count == 2) {
                    // If count is 2, then we have straight flush with two cards missing.
                    straightFlush = 0.0;
                }
            } else if (count == 1) {
                // If we have one card in hand the range, then we have to check, if there are any cards on the board in the range.
                // If there are no cards in the range, then we have no royal flush.
                if (board.size() > 0) {
                    for (String boardCard : board) {
                        char boardRank = boardCard.charAt(0);
                        char boardSuit = boardCard.charAt(1);
                        if ((boardRank == 'T' || boardRank == 'J' || boardRank == 'Q' || boardRank == 'K' || boardRank == 'A') &&
                                (boardSuit == hole.get(0).charAt(1) || boardSuit == hole.get(1).charAt(1))) {
                            count++;
                        }
                    }
                }
                if (count == 4) {
                    royalFlush = 4.255; // One card is missing to royal flush.
                } else if (count == 3) {
                    royalFlush = 0.092; // Two cards are missing to royal flush.
                } else if (count == 2) {
                    royalFlush = 0.0; // Three cards are missing to royal flush.
                }
            } else {
                // If we have no cards in the range, then we have no royal flush.
                royalFlush = 0.0;
            }
        }
        // Check quads, full house, flush, set, two pair and one pair probabilities.
        // Check if we have a pocket pair
        if (hole.get(0).charAt(0) == hole.get(1).charAt(0)) {
            // We have a pocket pair, so we can calculate probabilities based on that.
            onePair = 100;
            // Two pair probability is based on the fact that we have a pocket pair, first check the board for pairs.
            for (String card : board) {
                // If there are 2 equal cards on the board, then we have two pairs.
                // if there are 3 equal cards on the board, then we have a full house.
                // If we have a card, equal to the pocket pair, then we have a set.
                // If we have 4 equal cards on the board, then we have quads.
                if (card.charAt(0) == hole.get(0).charAt(0)) {
                    set = 100; // We have a set.
                    for (String otherCard : board) {
                        if (card.charAt(0) == otherCard.charAt(0) && card != otherCard) {
                            fullHouse = 100; // We have a full house.
                            break;
                        } else {
                            // We have no full house yet.
                            // Check probability of full house based on the fact that we have a pocket pair.
                            fullHouse = 0.73; // Precalculated probability of full house with pocket pair.
                        }
                    }
                    for (String otherCard : board) {
                        if (card.charAt(0) == otherCard.charAt(0) && card != otherCard) {
                            quads = 100; // We have quads.
                            break;
                        } else {
                            // We have no quads yet.
                            // Check probability of quads based on the fact that we have a pocket pair.
                            quads = 0.72; // Precalculated probability of quads with pocket pair.
                        }
                    }
                } else {
                    // If we have a pair on the board, then we have two pairs.
                    for (String otherCard : board) {
                        if (card.charAt(0) == otherCard.charAt(0) && card != otherCard) {
                            twoPair = 100; // We have two pairs.
                            break;
                        } else {
                            // We have no two pairs yet.
                            // Check probability of two pairs based on the fact that we have a pocket pair.
                            twoPair = 36.87; // Precalculated probability of two pairs with pocket pair.
                        }
                    }
                }
            }
        }

        // Check flush probability
        // if we have 2 cards of the same suit in hand, then we can check the board for cards of the same suit.
        if (hole.get(0).charAt(1) == hole.get(1).charAt(1)) {
            // We have suited cards, so we can calculate flush probability based on that.
            // Check if we have 3 or more cards of the same suit on the board.
            int suitCount = 2; // We have 2 suited cards in hand.
            char suit = hole.get(0).charAt(1); // Suit of the first card in hand.
            for (String card : board) {
                if (card.charAt(1) == suit) {
                    suitCount++; // We have a card of the same suit on the board.
                }
            }
            if (suitCount >= 5) {
                flush = 100; // We have a flush.
            } else if (suitCount == 4) {
                flush = 6.4; // We have a flush with one card missing.
            } else if (suitCount == 3) {
                flush = 1.21; // We have a flush with two cards missing.
            } else {
                flush = 0.0; // We have no flush.
            }
        }
        */

        Map<Character, Integer> rankCounts = new HashMap<>();
        List<String> all = new ArrayList<>();
        all.addAll(hole);
        all.addAll(board);

        for (String card : all) {
            char rank = card.charAt(0);
            rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
        }

        int countPairs = 0;
        boolean hasThree = false;
        boolean hasFour = false;

        for (int count : rankCounts.values()) {
            if (count == 4) {
                hasFour = true;
            } else if (count == 3) {
                hasThree = true;
            } else if (count == 2) {
                countPairs++;
            }
        }

        // Check for full house first
        if (hasThree && countPairs >= 1) {
            fullHouse = 100;
        } else if (hasThree) {
            set = 100;
        } else if (countPairs >= 2) {
            twoPair = 100;
        } else if (countPairs == 1) {
            onePair = 100;
        }


        return new HandProbabilities(highCard, onePair, twoPair, set, straight, flush, fullHouse, quads, straightFlush, royalFlush);
    }

    // --- Turn calculations ---
    public static HandProbabilities calculateTurn(List<String> hole, List<String> board, int activeOpponents) {
        double highCard = computeHighCardProbability(hole, board, activeOpponents);
        double straight = computeStraightProbability(hole, board);
        double flush = computeFlushProbability(hole, board);
        double quads = computeQuadsProbability(hole, board);
        double straightFlush = computeStraightFlushProbability(hole, board);
        double royalFlush = computeRoyalFlushProbability(hole, board);

        double onePair = 0;
        double twoPair = 0;
        double set = 0;
        double fullHouse = 0;

        /*
        if (royalFlush == 100) {
            return new HandProbabilities(100, 100, 100, 100, 100, 100, 100, 100, 100, 100);
        } else if (straightFlush == 100) {
            // Check if royal is possible
            int count = 0;
            for (String card : hole) {
                char rank = card.charAt(0);
                if (rank == 'T' || rank == 'J' || rank == 'Q' || rank == 'K' || rank == 'A') {
                    count++;
                }
            }
            for (String card : board) {
                char rank = card.charAt(0);
                if (rank == 'T' || rank == 'J' || rank == 'Q' || rank == 'K' || rank == 'A') {
                    count++;
                }
            }
            if (count == 4) {
                royalFlush = 0.02;
            }
            return new HandProbabilities(100, 100, 100, 100, 100, 100, 100, 100, 100, royalFlush);
        } else if (quads == 100){
            royalFlush = computeRoyalFlushProbability(hole, board);
            straightFlush = computeStraightFlushProbability(hole, board);
            return new HandProbabilities(100, 100, 100, 100, 100, 100, 100, 100, straightFlush, royalFlush);
        } else if (fullHouse == 100) {
            royalFlush = computeRoyalFlushProbability(hole, board);
            straightFlush = computeStraightFlushProbability(hole, board);
            quads = computeQuadsProbability(hole, board);
            return new HandProbabilities(100, 100, 100, 100, 100, 100, 100, quads, straightFlush, royalFlush);
        } else {
            royalFlush = computeRoyalFlushProbability(hole, board);
            straightFlush = computeStraightFlushProbability(hole, board);
            quads = computeQuadsProbability(hole, board);
            flush = computeFlushProbability(hole, board);
            straight = computeStraightProbability(hole, board);

            // Calculate full house probability based on pocket pair, set, or two pair combinations.
            boolean hasPocketPair = hole.get(0).charAt(0) == hole.get(1).charAt(0);
            if (hasPocketPair) onePair = 100;

            // Count all card ranks
            Map<Character, Integer> rankCounts = new HashMap<>();
            for (String card : hole) {
                char rank = card.charAt(0);
                rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
            }
            for (String card : board) {
                char rank = card.charAt(0);
                rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
            }

            boolean hasSet = false;
            boolean hasTwoPair = false;
            int pairsFound = 0;

            // Identify sets and pairs
            for (int count : rankCounts.values()) {
                if (count >= 3) hasSet = true;
                if (count >= 2) pairsFound++;
            }
            if (pairsFound >= 2) hasTwoPair = true;
            if (hasSet) {
                set = 100;
                twoPair = 100;
                onePair = 100;
                highCard = 100;
            } else if (hasTwoPair) {
                set = 8.69;
                twoPair = 100;
                onePair = 100;
                highCard = 100;
            } else if (hasPocketPair) {
                set = 4.35;
                twoPair = 26.09;
                onePair = 100;
                highCard = 100;
            } else {
                highCard = computeHighCardProbability(hole, board, activeOpponents);
            }

            if (hasSet) {
                // Full house needs one pair more
                for (Map.Entry<Character, Integer> entry : rankCounts.entrySet()) {
                    if (entry.getValue() >= 2) {
                        fullHouse = 100;
                        break;
                    }
                }
                if (fullHouse != 100) {
                    // Probability to hit a pair from remaining cards
                    // Approximate: one draw, 3 cards of that rank left, ~3/remainingCards
                    int remaining = 52 - hole.size() - board.size();
                    fullHouse = Math.min(100, (3.0 / remaining) * 100);
                }
            } else if (hasTwoPair) {
                // Need to improve one of the pairs to trips
                int remaining = 52 - hole.size() - board.size();
                fullHouse = Math.min(100, (2 * 2.0 / remaining) * 100); // two ranks, each has 2 cards unseen
            } else if (hasPocketPair) {
                // Check if there is one matching rank on board → set
                char rank = hole.get(0).charAt(0);
                int match = 0;
                for (String card : board) {
                    if (card.charAt(0) == rank) match++;
                }
                if (match == 2) {
                    // Already have a set
                    fullHouse = 100;
                } else if (match == 1) {
                    // Have a set, need a pair of any other rank
                    int remaining = 52 - hole.size() - board.size();
                    fullHouse = Math.min(100, (3.0 / remaining) * 100);
                } else {
                    // No set yet — can't reach full house directly from pocket pair
                    fullHouse = 0;
                }
            } else {
                // No viable base for full house
                fullHouse = 0;
            }
            return new HandProbabilities(highCard, onePair, twoPair, set, straight, flush, fullHouse, quads, straightFlush, royalFlush);
        }
        */

        Map<Character, Integer> rankCounts = new HashMap<>();
        List<String> all = new ArrayList<>();
        all.addAll(hole);
        all.addAll(board);

        for (String card : all) {
            char rank = card.charAt(0);          // 'A', 'K', 'Q', 'T', '9' и т. д.
            rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
        }

        char[] ranksArr = {'2','3','4','5','6','7','8','9','T','J','Q','K','A'};
        char[] suitsArr = {'H','D','C','S'};

        Set<String> used = new HashSet<>(all);

        int pairHits = 0;
        int twoPairHits = 0;
        int setHits = 0;
        int fullHouseHits = 0;
        int unseenCards = 52 - used.size();

        for (char r : ranksArr) {
            for (char s : suitsArr) {
                String c = "" + r + s;
                if (used.contains(c)) continue;

                Map<Character, Integer> tmp = new HashMap<>(rankCounts);
                tmp.put(r, tmp.getOrDefault(r, 0) + 1);

                int pairs = 0;
                boolean triple = false;
                boolean quad = false;

                for (int cnt : tmp.values()) {
                    if (cnt >= 2) pairs++;
                    if (cnt == 3) triple = true;
                    if (cnt == 4) quad = true;
                }

                if (pairs >= 1) pairHits++;
                if (pairs >= 2) twoPairHits++;
                if (triple && !quad) setHits++;
                if (triple && pairs >= 2 && !quad) fullHouseHits++;
            }
        }

        onePair   = 100.0 * pairHits      / unseenCards;
        twoPair   = 100.0 * twoPairHits   / unseenCards;
        set       = 100.0 * setHits       / unseenCards;
        fullHouse = 100.0 * fullHouseHits / unseenCards;

        return new HandProbabilities(highCard, onePair, twoPair, set, straight, flush, fullHouse, quads, straightFlush, royalFlush);
    }

    // --- River calculations --- done/tested
    public static HandProbabilities calculateRiver(List<String> hole, List<String> board, int activeOpponents) {
        double highCard = 0;
        double onePair = 0;
        double twoPair = 0;
        double set = 0;
        double straight = 0;
        double flush = 0;
        double fullHouse = 0;
        double quads = 0;
        double straightFlush = 0;
        double royalFlush = 0;

        List<String> allCards = new ArrayList<>();
        allCards.addAll(hole);
        allCards.addAll(board);

        highCard = computeHighCardProbability(hole, board, activeOpponents);

        // Count ranks
        Map<Character, Integer> rankCounts = new HashMap<>();
        for (String card : allCards) {
            char rank = card.charAt(0);
            rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
        }

        int pairs = 0;
        boolean hasThree = false;
        boolean hasFour = false;

        for (int count : rankCounts.values()) {
            if (count == 2) pairs++;
            else if (count == 3) hasThree = true;
            else if (count == 4) hasFour = true;
        }

        if (pairs >= 1) onePair = 100;
        if (pairs >= 2) twoPair = 100;
        if (hasThree) set = 100;
        if (hasFour) quads = 100;
        if (hasThree && pairs >= 1) fullHouse = 100;

        // Count suits for flush
        Map<Character, List<Character>> suitToRanks = new HashMap<>();
        for (String card : allCards) {
            char rank = card.charAt(0);
            char suit = card.charAt(1);
            suitToRanks.computeIfAbsent(suit, k -> new ArrayList<>()).add(rank);
        }

        for (Map.Entry<Character, List<Character>> entry : suitToRanks.entrySet()) {
            if (entry.getValue().size() >= 5) {
                flush = 100;

                // Check for straight flush
                List<Integer> values = new ArrayList<>();
                for (char r : entry.getValue()) {
                    int v = "23456789TJQKA".indexOf(r) + 2;
                    if (!values.contains(v)) values.add(v);
                }
                // Add wheel check
                if (values.contains(14)) values.add(1);
                Collections.sort(values);

                int consecutive = 1;
                for (int i = 1; i < values.size(); i++) {
                    if (values.get(i) == values.get(i - 1) + 1) {
                        consecutive++;
                        if (consecutive >= 5) {
                            straightFlush = 100;
                            straight = 100;
                            // Check if it's royal
                            if (values.containsAll(List.of(10, 11, 12, 13, 14))) {
                                royalFlush = 100;
                            }
                            break;
                        }
                    } else if (values.get(i) != values.get(i - 1)) {
                        consecutive = 1;
                    }
                }
            }
        }

        // Check regular straight if no straight flush
        if (straightFlush != 100) {
            Set<Integer> uniqueRanks = new HashSet<>();
            for (String card : allCards) {
                int v = "23456789TJQKA".indexOf(card.charAt(0)) + 2;
                uniqueRanks.add(v);
            }
            List<Integer> sorted = new ArrayList<>(uniqueRanks);
            if (sorted.contains(14)) sorted.add(1); // A-2-3-4-5 wheel
            Collections.sort(sorted);

            int consecutive = 1;
            for (int i = 1; i < sorted.size(); i++) {
                if (sorted.get(i) == sorted.get(i - 1) + 1) {
                    consecutive++;
                    if (consecutive >= 5) {
                        straight = 100;
                        break;
                    }
                } else if (sorted.get(i) != sorted.get(i - 1)) {
                    consecutive = 1;
                }
            }
        }

        return new HandProbabilities(
                highCard, onePair, twoPair, set, straight, flush, fullHouse, quads, straightFlush, royalFlush
        );
    }

    // --- Combination calculations ---
    public static double computeHighCardProbability(List<String> hole, List<String> board, int activeOpponents) {
        Map<Character, Integer> rankValues = Map.ofEntries(
                Map.entry('2', 2), Map.entry('3', 3), Map.entry('4', 4),
                Map.entry('5', 5), Map.entry('6', 6), Map.entry('7', 7),
                Map.entry('8', 8), Map.entry('9', 9), Map.entry('T', 10),
                Map.entry('J', 11), Map.entry('Q', 12), Map.entry('K', 13),
                Map.entry('A', 14)
        );

        Set<String> knownCards = new HashSet<>();
        knownCards.addAll(hole);
        knownCards.addAll(board);

        int maxRank = hole.stream()
                .mapToInt(c -> rankValues.get(c.charAt(0)))
                .max().orElse(0);

        // Count remaining cards of higher ranks
        int higherCards = 0;
        Map<Integer, Integer> knownRankCount = new HashMap<>();
        for (String card : knownCards) {
            int r = rankValues.get(card.charAt(0));
            knownRankCount.put(r, knownRankCount.getOrDefault(r, 0) + 1);
        }

        for (int r = maxRank + 1; r <= 14; r++) {
            higherCards += Math.max(0, 4 - knownRankCount.getOrDefault(r, 0));
        }

        int remainingCards = 52 - knownCards.size();
        int totalOpponentCards = activeOpponents * 2;

        // Probability all opponent cards are not higher than ours
        double probNoHigher = 1.0;
        for (int i = 0; i < totalOpponentCards; i++) {
            probNoHigher *= ((double) (remainingCards - higherCards - i)) / (remainingCards - i);
        }

        double pNoHigher = Math.max(0.0, Math.min(1.0, probNoHigher));
        if (maxRank == 14 ) {return 100.0;}
        return 1 - pNoHigher * -100.0;
    }

    public static double computeStraightProbability(List<String> hole, List<String> board) {
        Set<String> deck = new HashSet<>();
        String ranks = "23456789TJQKA";
        String suits = "CDHS";
        for (char r : ranks.toCharArray()) {
            for (char s : suits.toCharArray()) {
                deck.add("" + r + s);
            }
        }

        // Удаляем известные карты
        deck.removeAll(hole);
        deck.removeAll(board);

        List<String> remaining = new ArrayList<>(deck);
        int needed = 5 - board.size();
        if (needed < 0 || needed > 5) return 0.0;

        long total = 0;
        long success = 0;

        List<List<String>> additions = combinations(remaining, needed);

        for (List<String> extra : additions) {
            List<String> fullBoard = new ArrayList<>(board);
            fullBoard.addAll(extra);

            List<String> all = new ArrayList<>(hole);
            all.addAll(fullBoard);

            Set<Integer> handRanks = new HashSet<>();
            Set<Integer> allRanks = new HashSet<>();

            Map<Character, Integer> rankMap = Map.ofEntries(
                    Map.entry('2', 2), Map.entry('3', 3), Map.entry('4', 4),
                    Map.entry('5', 5), Map.entry('6', 6), Map.entry('7', 7),
                    Map.entry('8', 8), Map.entry('9', 9), Map.entry('T', 10),
                    Map.entry('J', 11), Map.entry('Q', 12), Map.entry('K', 13),
                    Map.entry('A', 14)
            );

            for (String c : all) allRanks.add(rankMap.get(c.charAt(0)));
            for (String c : hole) handRanks.add(rankMap.get(c.charAt(0)));

            // Проверка на стрит
            List<Integer> list = new ArrayList<>(allRanks);
            if (list.contains(14)) list.add(1); // wheel

            Collections.sort(list);
            int consec = 1;
            boolean found = false;

            for (int i = 1; i < list.size(); i++) {
                if (list.get(i) == list.get(i - 1) + 1) {
                    consec++;
                    if (consec >= 5) {
                        // Проверим, участвует ли карта из руки
                        for (int j = i - 4; j <= i; j++) {
                            if (handRanks.contains(list.get(j))) {
                                found = true;
                                break;
                            }
                        }
                        if (found) break;
                    }
                } else if (list.get(i) != list.get(i - 1)) {
                    consec = 1;
                }
            }

            total++;
            if (found) success++;
        }
        return (double) success / total * 100.0;
    }

    public static double computeStraightFlushProbability(List<String> hole, List<String> board) {
        // Collect all suited cards from hand and board
        Map<Character, List<Integer>> suitedRanks = new HashMap<>();
        Map<Character, Integer> rankMap = Map.ofEntries(
                Map.entry('2', 2), Map.entry('3', 3), Map.entry('4', 4),
                Map.entry('5', 5), Map.entry('6', 6), Map.entry('7', 7),
                Map.entry('8', 8), Map.entry('9', 9), Map.entry('T', 10),
                Map.entry('J', 11), Map.entry('Q', 12), Map.entry('K', 13),
                Map.entry('A', 14)
        );

        // Add hole cards
        for (String card : hole) {
            char suit = card.charAt(1);
            int rank = rankMap.get(card.charAt(0));
            suitedRanks.computeIfAbsent(suit, k -> new ArrayList<>()).add(rank);
        }

        // Add board cards
        for (String card : board) {
            char suit = card.charAt(1);
            int rank = rankMap.get(card.charAt(0));
            suitedRanks.computeIfAbsent(suit, k -> new ArrayList<>()).add(rank);
        }

        // Now check each suit: if there are 3+ cards of same suit, check if ranks could form a straight
        double straightFlush = 0.0;
        for (List<Integer> suited : suitedRanks.values()) {
            if (suited.size() >= 3) {
                Set<Integer> ranks = new HashSet<>(suited);
                // Generate all possible straights
                for (int low = 2; low <= 10; low++) {
                    boolean possible = true;
                    int missing = 0;
                    for (int i = 0; i < 5; i++) {
                        if (!ranks.contains(low + i)) missing++;
                    }
                    if (missing <= 2) {
                        // We assume approximate probabilities for 1 or 2 missing cards
                        if (missing == 0) {
                            // We already have a straight flush (but not royal)
                            straightFlush = 100.0;
                            break;
                        } else if (missing == 1) {
                            straightFlush = Math.max(straightFlush, 4.255);
                        } else if (missing == 2) {
                            straightFlush = Math.max(straightFlush, 0.092);
                        }
                    }
                }

                // Special case for wheel straight: A-2-3-4-5
                if (ranks.contains(14) && ranks.contains(2) && ranks.contains(3) && ranks.contains(4)) {
                    int missing = ranks.contains(5) ? 0 : 1;
                    if (missing == 0) {
                        straightFlush = 100.0;
                        break;
                    } else {
                        straightFlush = Math.max(straightFlush, 4.255);
                    }
                }
            }
        }
        return straightFlush;
    }

    public static double computeRoyalFlushProbability(List<String> hole, List<String> board) {
        // Set of ranks needed for a royal flush
        Set<Character> royalRanks = Set.of('T', 'J', 'Q', 'K', 'A');

        // Map from suit to ranks of that suit in royal range seen in hand + board
        Map<Character, Set<Character>> suitToRoyalRanks = new HashMap<>();

        // Count known cards
        Set<String> knownCards = new HashSet<>(hole);
        knownCards.addAll(board);

        // Track all seen royal cards per suit
        for (String card : knownCards) {
            char rank = card.charAt(0);
            char suit = card.charAt(1);
            if (royalRanks.contains(rank)) {
                suitToRoyalRanks.computeIfAbsent(suit, k -> new HashSet<>()).add(rank);
            }
        }

        int totalKnown = knownCards.size();
        int cardsLeft = 52 - totalKnown;
        int stage = board.size(); // 0 = preflop, 3 = flop, 4 = turn

        // Try each suit and calculate the probability of completing royal flush in that suit
        double bestProb = 0.0;
        for (Map.Entry<Character, Set<Character>> entry : suitToRoyalRanks.entrySet()) {
            Set<Character> seenRanks = entry.getValue();
            int have = seenRanks.size();
            int missing = 5 - have;

            if (missing == 0) return 100.0; // already have royal flush

            if (missing > (5 - stage)) continue; // not enough cards remaining to complete

            // Count how many of the needed royal cards in this suit are still unseen
            int unseenRoyalCards = 0;
            for (char rank : royalRanks) {
                String card = "" + rank + entry.getKey();
                if (!knownCards.contains(card)) unseenRoyalCards++;
            }

            if (unseenRoyalCards < missing) continue; // not enough remaining cards to complete it

            // Calculate probability: P = C(unseenRoyalCards, missing) / C(cardsLeft, missing)
            double numerator = combin(unseenRoyalCards, missing);
            double denominator = combin(cardsLeft, missing);
            if (denominator > 0) {
                double prob = (numerator / denominator) * 100.0;
                bestProb = Math.max(bestProb, prob);
            }
        }

        return bestProb;
    }

    public static double computeFlushProbability(List<String> hole, List<String> board) {
        Map<Character, Integer> suitCount = new HashMap<>();

        // Count suits in hole + board
        for (String card : hole) {
            char suit = card.charAt(1);
            suitCount.put(suit, suitCount.getOrDefault(suit, 0) + 1);
        }
        for (String card : board) {
            char suit = card.charAt(1);
            suitCount.put(suit, suitCount.getOrDefault(suit, 0) + 1);
        }

        int knownCards = hole.size() + board.size();
        int remainingCards = 52 - knownCards;
        int stage = board.size(); // 0 = preflop, 3 = flop, 4 = turn
        int unseenCards = 5 - stage;

        double bestProb = 0.0;
        for (int count : suitCount.values()) {
            int missing = 5 - count;
            if (missing <= 0) return 100.0; // already have a flush
            if (missing > unseenCards) continue; // not enough draws left

            // Estimate probability to hit missing cards from remaining deck
            double p = 1.0;
            for (int i = 0; i < missing; i++) {
                p *= ((13 - count - i) * 1.0) / (remainingCards - i);
            }
            bestProb = Math.max(bestProb, p * 100);
        }

        return bestProb;
    }

    public static double computeQuadsProbability(List<String> hole, List<String> board) {
        Map<Character, Integer> rankCount = new HashMap<>();
        for (String card : hole) {
            char rank = card.charAt(0);
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1);
        }
        for (String card : board) {
            char rank = card.charAt(0);
            rankCount.put(rank, rankCount.getOrDefault(rank, 0) + 1);
        }

        int knownCards = hole.size() + board.size();
        int remainingCards = 52 - knownCards;
        int stage = board.size(); // 0=preflop, 3=flop, 4=turn, 5=river
        int drawsLeft = 5 - stage;

        double bestProb = 0.0;

        for (Map.Entry<Character, Integer> entry : rankCount.entrySet()) {
            int seen = entry.getValue();
            if (seen >= 4) return 100.0; // already have quads
            int missing = 4 - seen;
            if (missing > drawsLeft) continue;

            double prob = 1.0;
            for (int i = 0; i < missing; i++) {
                prob *= (4 - seen - i) / (double)(remainingCards - i);
            }
            bestProb = Math.max(bestProb, prob * 100);
        }

        return bestProb;
    }

    //  --- Helpers ---

    public static <T> List<List<T>> combinations(List<T> list, int k) {
        List<List<T>> result = new ArrayList<>();
        combineHelper(list, k, 0, new ArrayList<>(), result);
        return result;
    }

    private static <T> void combineHelper(List<T> list, int k, int start, List<T> path, List<List<T>> result) {
        if (path.size() == k) {
            result.add(new ArrayList<>(path));
            return;
        }
        for (int i = start; i < list.size(); i++) {
            path.add(list.get(i));
            combineHelper(list, k, i + 1, path, result);
            path.remove(path.size() - 1);
        }
    }

    private static long combin(int n, int k) {
        if (k < 0 || k > n) return 0;
        if (k == 0 || k == n) return 1;
        long res = 1;
        for (int i = 1; i <= k; i++) {
            res = res * (n - i + 1) / i;
        }
        return res;
    }

    private static double hypergeometricProbability(int n, int K, int N, int k) {
        if (k > n || k > K || n > N) return 0.0;

        double numerator = combinDouble(K, k) * combinDouble(N - K, n - k);
        double denominator = combinDouble(N, n);

        return numerator / denominator;
    }

    private static double combinDouble(int n, int k) {
        if (k > n) return 0.0;
        if (k == 0 || k == n) return 1.0;

        double result = 1.0;
        for (int i = 1; i <= k; i++) {
            result *= (double) (n - (k - i)) / i;
        }
        return result;
    }
}
