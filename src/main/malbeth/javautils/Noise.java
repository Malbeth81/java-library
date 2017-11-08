package malbeth.javautils;

import java.util.Random;

public class Noise {

    public static double[][] blend(double[][] noise1, double[][] noise2, double alpha) {
        if (noise1 != null && noise2 != null && noise1.length > 0 && noise1.length == noise2.length && noise1[0].length == noise2[0].length) {
            double[][] result = new double[noise1.length][noise1[0].length];
            for (int i = 0; i < noise1.length; i++) {
                if (noise1[i].length != noise1[0].length || noise2[i].length != noise1[i].length) {
                    return null;
                }
                for (int j = 0; j < noise1[i].length; j++)
                    result[i][j] = interpolate(noise1[i][j], noise2[i][j], alpha);
            }
            return result;
        }

        return null;
    }

    private  static double interpolate(double a, double b, double alpha) {
        return a * (1 - alpha) + b * alpha;
    }

    public static double[][] perlinNoise(int width, int height, int octaveCount) {
        return perlinNoise(whiteNoise(width, height), octaveCount);
    }

    public static double[][] perlinNoise(double[][] noise, int octaveCount) {
        if (noise == null || octaveCount <= 0)
            return noise;

        int width = noise.length;
        int height = noise[0].length;

        double[][] result = new double[width][height];
        double amplitude = 1.0f;
        double persistance = 0.5f;
        double totalAmplitude = 0.0f;

        double[][][] smoothNoise = new double[octaveCount][][];
        for (int i = 0; i < octaveCount; i++)
            smoothNoise[i] = smoothNoise(noise, i);

        // Blend noise together
        for (int octave = octaveCount - 1; octave >= 0; octave--) {
            amplitude *= persistance;
            totalAmplitude += amplitude;

            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++)
                    result[i][j] += smoothNoise[octave][i][j] * amplitude;
        }

        // Normalisation
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                result[i][j] /= totalAmplitude;

        return result;
    }

    public static double[][] smoothNoise(int width, int height, int octave) {
        return smoothNoise(whiteNoise(width, height), octave);
    }

    public static double[][] smoothNoise(double[][] noise, int octave) {
        if (noise == null || octave <= 0)
            return noise;

        int width = noise.length;
        int height = noise[0].length;

        double[][] result = new double[width][height];

        int samplePeriod = 1 << octave; // calculates 2 ^ k
        double sampleFrequency = 1.0f / samplePeriod;

        for (int i = 0; i < width; i++) {
            // Calculate the horizontal sampling indices
            int sample_i0 = (i / samplePeriod) * samplePeriod;
            int sample_i1 = (sample_i0 + samplePeriod) % width; //wrap around
            double horizontal_blend = (i - sample_i0) * sampleFrequency;

            for (int j = 0; j < height; j++) {
                // Calculate the vertical sampling indices
                int sample_j0 = (j / samplePeriod) * samplePeriod;
                int sample_j1 = (sample_j0 + samplePeriod) % height; //wrap around
                double vertical_blend = (j - sample_j0) * sampleFrequency;

                // Blend the top two corners
                double top = interpolate(noise[sample_i0][sample_j0], noise[sample_i1][sample_j0], horizontal_blend);

                // Blend the bottom two corners
                double bottom = interpolate(noise[sample_i0][sample_j1], noise[sample_i1][sample_j1], horizontal_blend);

                // Final blend
                result[i][j] = interpolate(top, bottom, vertical_blend);
            }
        }

        return result;
    }

    public static double[][] whiteNoise(int width, int height) {
        double[][] result = new double[width][height];

        Random random = new Random();
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                result[i][j] = random.nextFloat();

        return result;
    }

}