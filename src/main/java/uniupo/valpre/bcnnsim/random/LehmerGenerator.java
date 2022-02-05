package uniupo.valpre.bcnnsim.random;

import java.nio.ByteBuffer;
import java.util.Date;

public class LehmerGenerator implements RandomGenerator {

    private final long a;
    private final long m;
    private final long q;
    private final long r;

    private long state = 1;

    long DEFAULT      = 123456789L; /* initial seed, use 0 < DEFAULT < MODULUS   */

    long seed         = DEFAULT;    /* seed is the state of the generator        */

    public LehmerGenerator(long a, long m, long seed) {
        this.a = a;
        this.m = m;
        q = m/a;
        r = m % a;
        this.seed = seed % m;
    }

    public LehmerGenerator(long a, long m) {
       this(a,m,new Date().getTime());
    }

    public LehmerGenerator(long seed){
        this(48271, (long) (Math.pow(2,31) - 1), seed);
    }

    public LehmerGenerator(){
        this(new Date().getTime());
    }

    public double random() {
        long  t = a * (state % q) - r * (state / q);
        if(t > 0){
            state = t;
        }else {
            state = t + m;
        }
        return ((double) state) / m;
    }
}
