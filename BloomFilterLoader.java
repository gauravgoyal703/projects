import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class BloomFilterLoader {

    public static BloomFilter loadBloomFilter(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             DataInputStream dis = new DataInputStream(fis)) {

            // Read and validate the identifier
            byte[] identifier = new byte[4];
            dis.readFully(identifier);
            String identifierString = new String(identifier);
            if (!"CCBF".equals(identifierString)) {
                throw new IllegalArgumentException("Invalid file type");
            }

            // Read and validate the version number
            short version = dis.readShort();
            if (version != 1) { // Replace 1 with your current version number as needed
                throw new IllegalArgumentException("Unsupported version number: " + version);
            }

            // Read the number of hash functions
            short numHashFunctions = dis.readShort();

            // Read the number of bits
            int bitArraySize = dis.readInt();

            // Initialize the Bloom filter with the read parameters
            BloomFilter bloomFilter = new BloomFilter(bitArraySize, numHashFunctions);

            // Read the bit array
            byte[] bitArray = new byte[(bitArraySize + 7) / 8];
            dis.readFully(bitArray);

            // Load the bit array into the Bloom filter
            bloomFilter.loadBitArray(bitArray);

            return bloomFilter;
        }
    }
}
