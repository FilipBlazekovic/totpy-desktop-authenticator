package com.filipblazekovic.totpy.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import lombok.SneakyThrows;

public class QRCode {

  private static final int QR_CODE_WIDTH = 250;
  private static final int QR_CODE_HEIGHT = 250;

  private QRCode() {
  }

  @SneakyThrows
  public static BufferedImage generate(String data) {
    return MatrixToImageWriter.toBufferedImage(
        new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT)
    );
  }

  @SneakyThrows
  public static String parse(BufferedImage image) {
    return new MultiFormatReader()
        .decode(
            new BinaryBitmap(
                new HybridBinarizer(
                    new BufferedImageLuminanceSource(image)
                )
            )
        )
        .getText();
  }

}
