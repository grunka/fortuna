package com.grunka.random.fortuna;

import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.ISAACRandom;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FortunaTest {
    @Test
    public void shouldCreateInstanceAndWaitForInitialization() throws Exception {
        Fortuna fortuna = Fortuna.createInstance();
        try {
            fortuna.nextInt(42);
        } catch (IllegalStateException ignored) {
            fail("Did not wait for initialization");
        }
    }

    @Test
    public void shouldProduceEvenDistribution() {
        int numbers = 1000;
        SummaryStatistics fortunaNumbers = new SummaryStatistics();
        SummaryStatistics isaacNumbers = new SummaryStatistics();
        SummaryStatistics mersenneNumbers = new SummaryStatistics();
        Fortuna fortuna = Fortuna.createInstance();
        ISAACRandom isaacRandom = new ISAACRandom();
        MersenneTwister mersenneTwister = new MersenneTwister();
        for (int i = 0; i < 10000000; i++) {
            fortunaNumbers.addValue(fortuna.nextInt(numbers));
            isaacNumbers.addValue(isaacRandom.nextInt(numbers));
            mersenneNumbers.addValue(mersenneTwister.nextInt(numbers));
        }
        double varFortuna = fortunaNumbers.getVariance();
        double varIsaac = isaacNumbers.getVariance();
        double varMersenne = mersenneNumbers.getVariance();
        double varUni = new UniformRealDistribution(0, numbers).getNumericalVariance();
        double percentDifferenceFortuna = (varFortuna - varUni) / varUni;
        double percentDifferenceIsaac = (varIsaac - varUni) / varUni;
        double percentDifferenceMersenne = (varMersenne - varUni) / varUni;
        System.out.println("Variances: Fortuna "+varFortuna+", ISAAC "+varIsaac+", Mersenne "+varMersenne+", Uniform "+varUni);
        System.out.println("UniformRealDistribution vs Fortuna variance difference percent: "+percentDifferenceFortuna*100+" %");
        System.out.println("UniformRealDistribution vs ISAAC variance difference percent: "+percentDifferenceIsaac*100+" %");
        System.out.println("UniformRealDistribution vs Mersenne variance difference percent: "+percentDifferenceMersenne*100+" %");
        assertEquals("UniformRealDistribution vs Fortuna variance", 0.0, percentDifferenceFortuna, 0.01);
    }

}
