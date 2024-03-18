import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.MurmurHash3;

public class BloomFilter {
    private byte[] bitArray;
    private final int bitArraySize;
    private final int numHashFunctions;

    public BloomFilter(int bitArraySize, int numHashFunctions) {
        this.bitArraySize = bitArraySize;
        // Ensure the bit array accommodates the specified number of bits, rounded up to the nearest byte
        this.bitArray = new byte[(bitArraySize + 7) / 8];
        this.numHashFunctions = numHashFunctions;
    }

    public void loadBitArray(byte[] bitArray) {
        // Method to directly set the bit array from loaded data
        // If using a BitSet, convert byte[] back to BitSet
        this.bitArray = bitArray; // Or convert to BitSet
    }


    public void add(String element) {
        int[] hashes = createHashes(element);
        for (int hash : hashes) {
            int index = Math.abs(hash) % bitArraySize;
            int byteIndex = index / 8;
            int bitIndex = index % 8;
            bitArray[byteIndex] |= (1 << bitIndex);
        }
    }

    public boolean mightContain(String element) {
        int[] hashes = createHashes(element);
        for (int hash : hashes) {
            int index = Math.abs(hash) % bitArraySize;
            int byteIndex = index / 8;
            int bitIndex = index % 8;
            if ((bitArray[byteIndex] & (1 << bitIndex)) == 0) {
                return false; // If any bit is not set, the element is definitely not in the set
            }
        }
        return true; // If all bits are set, the element might be in the set
    }

    private int[] createHashes(String element) {
        byte[] bytes = StringUtils.getBytesUtf8(element);
        int[] hashes = new int[numHashFunctions];
        for (int i = 0; i < numHashFunctions; i++) {
            // Generate hash values using MurmurHash3 with varying seeds to simulate multiple hash functions
            hashes[i] = MurmurHash3.hash32x86(bytes, 0, bytes.length, 104729 + i);
        }
        return hashes;
    }

    public byte[] getBitArray() {
        return bitArray;
    }

    public int getBitArraySize() {
        return bitArraySize;
    }

    public int getNumHashFunctions() {
        return numHashFunctions;
    }
}
