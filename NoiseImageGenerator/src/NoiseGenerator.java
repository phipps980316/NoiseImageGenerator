import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class NoiseGenerator {
    private float[][] noise;
    private final Random random;
    private final int seed;
    private final int size;

    public NoiseGenerator(int size, int runs, int maxPatchSize){
        this.noise = new float[size][size];
        this.random = new Random();
        this.seed = random.nextInt(1000000000);
        this.size = size;

        for(float[] array : noise){
            Arrays.fill(array, 1.0f);
        }

        this.random.setSeed(this.seed);

        for(int i = 0; i < runs; i++){
            int patchSize = this.random.nextInt(maxPatchSize);
            int centerX = this.random.nextInt(size);
            int centerY = this.random.nextInt(size);

            for(int x = centerX - patchSize; x < centerX + patchSize; x++){
                for(int y = centerY - patchSize; y < centerY + patchSize; y++){
                    if(x > 0 && x < size && y > 0 && y < size) this.noise[x][y] = 0.0f;
                }
            }
        }
        blur((int) (Math.sqrt(size) + 1), maxPatchSize);
        toImage();
    }

    private void blur(int kernelSize, int sigma){
        int center = kernelSize/2;
        float[][] blurKernel = generateBlurKernel(kernelSize, sigma, center);
        float[][] processedImage = new float[size][size];

        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                for(int posX = 0; posX < kernelSize; posX++){
                    for(int posY = 0; posY < kernelSize; posY++){
                        int offsetX = posX - (kernelSize/2);
                        int offsetY = posY - (kernelSize/2);
                        if(((x + offsetX) > 0)&&((x + offsetX) < size)&&((y + offsetY) > 0)&&((y + offsetY) < size)){
                            processedImage[x][y] += noise[x+offsetX][y+offsetY] * blurKernel[posX][posY];
                        }
                    }
                }
            }
        }
        noise = processedImage;
    }

    private void toImage(){
        String path = "output/image.png";
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                float value = this.noise[x][y];
                if(value > 1) value = 1f;
                try{
                    Color color = new Color(value, value, value);
                    image.setRGB(x,y,color.getRGB());
                } catch (IllegalArgumentException e){
                    System.out.println(value);
                    e.printStackTrace();
                }
            }
        }

        File file = new File(path);
        try{
            ImageIO.write(image, "png", file);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private float[][] generateBlurKernel(int kernelSize, float sigma, int center){
        float[][] coefficients = new float[kernelSize][kernelSize];
        float coefficientSum = 0.0f;

        if(kernelSize % 2 == 1 && sigma > 0){
            for(int x = 0; x < kernelSize; x++){
                for(int y = 0; y < kernelSize; y++){
                    int distFromCenterX = Math.abs(x - center);
                    int distFromCenterY = Math.abs(y - center);
                    float value = (float) ((1/(2*Math.PI*Math.pow(sigma, 2)))*Math.exp(-((Math.pow(distFromCenterX, 2) + Math.pow(distFromCenterY, 2))/(2*Math.pow(sigma, 2)))));
                    coefficients[x][y] = value;
                    coefficientSum += value;
                }
            }
        }

        for(int x = 0; x < kernelSize; x++){
            for(int y = 0; y < kernelSize; y++){
                coefficients[x][y] = coefficients[x][y] * (1/coefficientSum);
            }
        }

        return coefficients;
    }
}
