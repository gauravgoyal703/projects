import java.io.*;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {

        TokenBucketRateLimiter rateLimiter = TokenBucketRateLimiter.Builder.newInstance()
                .setCapacity(5)
                .setFillRate(1)
                .build();

        rateLimiter.addToken();
        System.out.println(rateLimiter.canAccess());
        System.out.println(rateLimiter.canAccess());
        System.out.println(rateLimiter.canAccess());
        System.out.println(rateLimiter.canAccess());
        System.out.println(rateLimiter.canAccess());
        System.out.println(rateLimiter.canAccess());
        System.out.println(rateLimiter.canAccess());
        System.out.println(rateLimiter.canAccess());
        //Thread.sleep(1000);
        rateLimiter.addToken();
        rateLimiter.addToken();
        rateLimiter.addToken();
        System.out.println(rateLimiter.canAccess());
        System.out.println(rateLimiter.canAccess());
        System.out.println(rateLimiter.canAccess());


        BloomFilter bf = new BloomFilter(3000, 5);
        List<String> words = readFile(3000);
        System.out.printf("Words read from the file: %s.%n", words.size());

        for (String word : words.subList(0, 2000)) {
            bf.add(word);
        }

        BloomFilterSaver.saveBloomFilter(bf, "/Users/gauravgoyal/Learn/resources/bloom_data.bf");
        bf = BloomFilterLoader.loadBloomFilter("/Users/gauravgoyal/Learn/resources/bloom_data.bf");

        List<String> wordsToCheck = new ArrayList<>(words.subList(0, 3000));
        for (String word : wordsToCheck) {
            if (!bf.mightContain(word)) {
                System.out.printf("Word: '%s' is present in bloom filter: %s.%n", word, false);
            }
        }

    }

    public static List<String> readFile(final int size) {
        String resourcePath = "/Users/gauravgoyal/Learn/resources/dict.txt";
        List<String> words = new ArrayList<>();
        int counter = size;

        try (BufferedReader reader = new BufferedReader(new FileReader(resourcePath))) {
            String word;
            while ((word = reader.readLine()) != null) {
                if(counter <= 0) {
                    break;
                }
                words.add(word.trim());
                counter--;
            }
        } catch (IOException e) {
            System.err.printf("Error: %s.%n", e.getMessage());
        } catch (NullPointerException e) {
            System.out.println("Resource file " + resourcePath + " was not found.");
        }
        return words;
    }

}
