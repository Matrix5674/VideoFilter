package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import java.util.ArrayList;

public class Basic implements PixelFilter {
    //Color channels
    short[][] red;
    short[][] green;
    short[][] blue;

    //Kernals
    private double[][] boxBlurKernel = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
    private double[][] PrewittEdgeKernel = {{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}};
    private double[][] emboss = {{-4,0,0}, {0,0,0}, {0,0,4}};

    //Kernal in use
    private double[][] kernal = emboss;
    double kernalWeight = calculateKernalWeight(kernal); //Total Kernal Weight


    @Override
    public DImage processImage(DImage img) {

        red = img.getRedChannel();
        green = img.getGreenChannel();
        blue = img.getBlueChannel();

        short[][] redConvoluted = new short[red.length][red[1].length];
        short[][] greenConvoluted = new short[green.length][green[1].length];
        short[][] blueConvoluted = new short[blue.length][blue[1].length];

        convolute(redConvoluted, greenConvoluted, blueConvoluted);

        img.setColorChannels(redConvoluted, greenConvoluted, blueConvoluted);
        return img;
    }

    public void convolute(short[][] redConvoluted, short[][] greenConvoluted, short[][] blueConvoluted){
        for (int r = 0; r < red.length-2; r++) {
            for (int c = 0; c < red[r].length-2; c++) {
                redConvoluted[r][c] = changePixel(red, kernal, kernalWeight, r, c);
                greenConvoluted[r][c] = changePixel(green, kernal, kernalWeight, r, c);
                blueConvoluted[r][c] = changePixel(blue, kernal, kernalWeight, r, c);
            }
        }
    }

    public short changePixel(short[][] grid, double[][] kernal, double kernalWeight, int startR, int startC){
        double result = 0;
        //Loops through all the factors in the Kernal
        for (int r = startR; r < startR+3; r++) {
            for (int c = startC; c < startC+3; c++) {
                double val1 = grid[r][c];
                double val2 = kernal[r-startR][c-startC];
                result += val1*val2;
            }
        }

        return (short)(result/kernalWeight);

    }

    public double calculateKernalWeight(double[][] kernal){
        double result = 0;
        for (int r = 0; r < kernal.length; r++) {
            for (int c = 0; c < kernal[r].length; c++) {
                result += kernal[r][c];
            }
        }
        return result;
    }
}

