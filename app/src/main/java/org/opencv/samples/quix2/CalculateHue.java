package org.opencv.samples.quix2;

import android.util.Log;

/**
 * Created by ygyg331 on 2018-10-24.
 */

public class CalculateHue {


    //R = r*100 / r+g+b , G = g*100 / r+g+b, B = b*100 / r+g+b
    private double[] getLargeRGB(int[] value){
        double H=0.0;
        // index 0 1 2  : R G B
        double[] LargeRGB =new double[3];
        double sum=value[0]+value[1]+value[2];
        LargeRGB[0]=value[0]*100/sum;
        LargeRGB[1]=value[1]*100/sum;
        LargeRGB[2]=value[2]*100/sum;

        return LargeRGB;
    };

    public double getS(int[] value){

        double[] tmpRGB=getLargeRGB(value);
        double S=0.0;
        double tmp=Math.min(tmpRGB[0],tmpRGB[1]);
        double minValue= Math.min(tmpRGB[2],tmp);
        S= 1-(tmpRGB[0]+tmpRGB[1]+tmpRGB[2])/3 * minValue;

        return S;
    };

    public double getI(int[] value){
        double[] tmpRGB=getLargeRGB(value);

        double I=(tmpRGB[0]+tmpRGB[1]+tmpRGB[2])/3;

        return I;
    };

    //H = arccos( 0.5 * {(R-G) + (R-B)} /  root({R-G}^2 + (R-B)(G-B)}) )
    public double getH(int[] value){
        double[] tmpRGB=getLargeRGB(value);

        double H=0.0;
        double tmp=1,num,dec;
        double RG=tmpRGB[0]-tmpRGB[1];
        double RB=tmpRGB[0]-tmpRGB[2];
        double GB=tmpRGB[1]-tmpRGB[2];
        double RG_sq=Math.pow(RG,2);
        num = 0.5 * ((RG) +(RB));

        dec = Math.sqrt(RG_sq  + (RB)*(GB));

        tmp=Math.abs(num/dec);

        H=Math.acos(tmp);
 
        Log.i("HUE T",Double.toString(num));
        Log.i("HUE B",Double.toString(dec));
        Log.i("HUE",Double.toString(tmp));
        return H;
    }
}
