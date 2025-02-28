package com.marciliojr.comprazfx.service;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class ImagePreProcessor {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static File preprocessImage(File inputImage) throws Exception {
        Mat src = Imgcodecs.imread(inputImage.getAbsolutePath(), Imgcodecs.IMREAD_GRAYSCALE);

        Imgproc.threshold(src, src, 120, 255, Imgproc.THRESH_BINARY);

        File outputImage = new File("processed_receipt.png");
        Imgcodecs.imwrite(outputImage.getAbsolutePath(), src);

        return outputImage;
    }
}
