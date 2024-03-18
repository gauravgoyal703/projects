import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.BitSet;

public class BloomFilterSaver {

    public static void saveBloomFilter(BloomFilter bloomFilter, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             DataOutputStream dos = new DataOutputStream(fos)) {

            // Write the identifier CCBF
            dos.writeByte('C');
            dos.writeByte('C');
            dos.writeByte('B');
            dos.writeByte('F');

            // Write the version number (e.g., 1)
            dos.writeShort(1); // Adjust this as per your versioning

            // Write the number of hash functions
            dos.writeShort(bloomFilter.getNumHashFunctions());

            // Write the number of bits
            dos.writeInt(bloomFilter.getBitArraySize());

            // Write the bit array
            byte[] bitArray = bloomFilter.getBitArray();
            dos.write(bitArray);
        }
    }
}
