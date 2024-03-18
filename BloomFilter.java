import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.MurmurHash3;

import java.util.BitSet;

public class BloomFilter {
    private final BitSet bitSet;
    private final int bitArraySize;
    private final int numHashFunctions;


    public BloomFilter(int bitArraySize, int numHashFunctions) {
        this.bitArraySize = bitArraySize;
        this.bitSet = new BitSet(bitArraySize);
        this.numHashFunctions = numHashFunctions;
    }

    public void add(String element) {
        // For each hash function, hash the element and set the bit at the resulting position.
        int[] hashes = createHashes(element);
        // System.out.printf("Word: '%s' added at %d.%n", element, hash);
        for (int hash : hashes) {
            bitSet.set(Math.abs(hash) % bitArraySize);
        }
    }

    public boolean mightContain(String element) {
        // For each hash function, hash the element and check the bit at the resulting position.
        // If any bit is not set, return false. Otherwise, return true.
        int[] hashes = createHashes(element);
        for (int hash : hashes) {
            if (!bitSet.get(Math.abs(hash) % bitArraySize)) {
                return false; // If any of the bits is not set, the element is definitely not in the set
            }
        }
        return true; // If all bits are set, the element might be in the set
    }

    private int[] createHashes(String element) {
        byte[] bytes = StringUtils.getBytesUtf8(element);
        int[] hashes = new int[numHashFunctions];
        for (int i = 0; i < numHashFunctions; i++) {
            // Using different seeds for each hash function simulation
            hashes[i] = MurmurHash3.hash32x86(bytes, 0, bytes.length, 104729 + i);
        }
        return hashes;
    }
}
