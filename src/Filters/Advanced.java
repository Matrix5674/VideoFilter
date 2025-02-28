package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import java.util.ArrayList;

public class Advanced implements PixelFilter {
    //Color channels
    short[][] red;
    short[][] green;
    short[][] blue;

    //Kernals

    //Sobel Edge Detection
    private double[][] Gx = {{-1, 0, 1},{-2, 0, 2},{-1, 0, 1}};
    private double[][] Gy = {{1, 2, 1},{0, 0, 0},{-1, -2, -1}};

    //Kernal in use
    private double[][] horizontalKernal = Gx;
    private double[][] verticalKernal = Gy;
    double horizontalKernalWeight = calculateKernalWeight(horizontalKernal); //Horizontal Kernal Weight
    double verticalKernalWeight = calculateKernalWeight(verticalKernal); //Vertical Kernal Weight


    @Override
    public DImage processImage(DImage img) {

        red = img.getRedChannel();
        green = img.getGreenChannel();
        blue = img.getBlueChannel();

        short[][] redXConvoluted = new short[red.length][red[1].length];
        short[][] greenXConvoluted = new short[green.length][green[1].length];
        short[][] blueXConvoluted = new short[blue.length][blue[1].length];

        short[][] redYConvoluted = new short[red.length][red[1].length];
        short[][] greenYConvoluted = new short[green.length][green[1].length];
        short[][] blueYConvoluted = new short[blue.length][blue[1].length];

        //Get horizontal and vertical convolutions
        convolute(redXConvoluted, greenXConvoluted, blueXConvoluted, horizontalKernal, horizontalKernalWeight);
        convolute(redYConvoluted, greenYConvoluted, blueYConvoluted, verticalKernal, verticalKernalWeight);

        //Combine convolutions into a single pixel grid
        applyFinalConvolutions(redXConvoluted, redYConvoluted, greenXConvoluted, greenYConvoluted, blueXConvoluted, blueYConvoluted);

        img.setColorChannels(red, green, blue);
        return img;
    }

    public void convolute(short[][] redConvoluted, short[][] greenConvoluted, short[][] blueConvoluted, double[][] kernal, double kernalWeight){
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

    public void applyFinalConvolutions(short[][] rX, short[][] rY, short[][] gX, short[][] gY, short[][] bX, short[][] bY){
        changePixelFinal(red, rX, rY);
        changePixelFinal(green, gX, gY);
        changePixelFinal(blue, bX, bY);
    }

    public void changePixelFinal(short[][] grid, short[][] gridX, short[][] gridY){
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                grid[r][c] = calculateFinalPixelValue(gridX[r][c], gridY[r][c]);
            }
        }
    }

    public double calculateKernalWeight(double[][] kernal){
        double result = 0;
        for (int r = 0; r < kernal.length; r++) {
            for (int c = 0; c < kernal[r].length; c++) {
                result += kernal[r][c];
            }
        }

        if (result == 0) result = 1;
        return result;
    }


    public short calculateFinalPixelValue (short x, short y){
        return (short)(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
    }
}

