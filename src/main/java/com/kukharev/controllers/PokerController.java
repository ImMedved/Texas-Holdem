package com.kukharev.controllers;

import com.kukharev.core.ProbabilityCalculator;
import com.kukharev.dto.ProbabilityDto;
import com.kukharev.dto.RequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PokerController {
    private static final Logger logger = LoggerFactory.getLogger(PokerController.class);

    @PostMapping("/probabilities")
    public ProbabilityDto calc(@RequestBody RequestDto req) {
        logger.info("Received request: hole={}, board={}, activeOpp={}", req.hole(), req.board(), req.activeOpp());

        var hero = ProbabilityCalculator.computeHero(
                req.hole(), req.board(), req.activeOpp());

        var opp = ProbabilityCalculator.computeOpponents(
                req.hole(), req.board(), req.activeOpp());

        double[] heroArr = {
                hero.highCardValue(), hero.pairValue(), hero.twoPairValue(), hero.setValue(),
                hero.straightValue(), hero.flushValue(), hero.fullHouseValue(),
                hero.quadsValue(), hero.straightFlushValue(), hero.royalValue()
        };

        double[] oppArr = {
                opp.pairValue(), opp.twoPairValue(), opp.setValue(), opp.straightValue(),
                opp.flushValue(), opp.fullHouseValue(), opp.quadsValue(),
                opp.straightFlushValue(), opp.royalValue()
        };
        return new ProbabilityDto(heroArr, oppArr);
    }
}
