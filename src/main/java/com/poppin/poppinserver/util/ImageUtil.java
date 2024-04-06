package com.poppin.poppinserver.util;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.util.Iterator;

@Component
public class ImageUtil {
    public static InputStream cropImageToSquare(InputStream originalImageStream) throws IOException {
        // InputStream의 내용을 메모리에 저장합니다.
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        originalImageStream.transferTo(buffer);
        byte[] originalBytes = buffer.toByteArray();

        // 메모리에 저장된 내용으로부터 새 InputStream을 생성합니다.
        InputStream imageStreamForFormatName = new ByteArrayInputStream(originalBytes);
        String formatName = getFormatName(imageStreamForFormatName);

        // 다시 새 InputStream을 생성하여 이미지 처리에 사용합니다.
        InputStream imageStreamForProcessing = new ByteArrayInputStream(originalBytes);
        BufferedImage originalImage = ImageIO.read(imageStreamForProcessing);

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int newSideLength = Math.min(width, height);
        int x = (width - newSideLength) / 2;
        int y = (height - newSideLength) / 2;

        BufferedImage croppedImage = originalImage.getSubimage(x, y, newSideLength, newSideLength);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(croppedImage, formatName, os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    // InputStream으로부터 이미지 형식을 추출합니다.
    private static String getFormatName(InputStream input) throws IOException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(input)) {
            Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
            if (!imageReaders.hasNext()) {
                throw new IOException("No suitable ImageReader found for source data.");
            }
            ImageReader reader = imageReaders.next();
            return reader.getFormatName();
        }
    }
}
