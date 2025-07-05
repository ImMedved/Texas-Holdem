package com.kukharev.controllers;

import com.kukharev.core.CombinedProbabilityCalculator;
import com.kukharev.dto.ProbabilityDto;
import com.kukharev.dto.RequestDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PokerController {

    @PostMapping("/probabilities")
    public ProbabilityDto calc(@RequestBody RequestDto req) {

        var hero = CombinedProbabilityCalculator.computeAll(
                req.hole(), req.board(), req.activeOpp());

        var opp = CombinedProbabilityCalculator.computeOpponents(
                req.hole(), req.board(), req.activeOpp());

        double[] heroArr = {
                hero.highCard(), hero.pair(), hero.twoPair(), hero.set(),
                hero.straight(), hero.flush(), hero.fullHouse(),
                hero.quads(), hero.straightFlush(), hero.royal()
        };

        double[] oppArr = {
                opp.pair(), opp.twoPair(), opp.set(), opp.straight(),
                opp.flush(), opp.fullHouse(), opp.quads(),
                opp.straightFlush(), opp.royal()
        };

        return new ProbabilityDto(heroArr, oppArr);
    }
}
