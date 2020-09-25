public class Main {
    public static void main(String[] args){
        int size = 1024;

        int runs = 1000;

        int patchSize = 30;

        NoiseGenerator noiseGenerator = new NoiseGenerator(size, runs, patchSize);

    }
}
