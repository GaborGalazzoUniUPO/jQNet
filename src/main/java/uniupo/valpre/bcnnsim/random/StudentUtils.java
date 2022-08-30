package uniupo.valpre.bcnnsim.random;
/* -------------------------------------------------------------------------
 * This is a Java library that can be used to evaluate the probability
 * density functions (pdf's), cumulative distribution functions (cdf's), and
 * inverse distribution functions (idf's) for a variety of discrete and
 * continuous random variables.
 *
 * The following notational conventions are used
 *                 x : possible value of the random variable
 *                 u : real variable (probability) between 0.0 and 1.0
 *  a, b, n, p, m, s : distribution-specific parameters
 *
 * There are pdf's, cdf's and idf's for 6 discrete random variables
 *
 *      Random Variable    Range (x)  Mean         Variance
 *
 *      bernoulli(p)       0..1       p            p*(1-p)
 *      binomial(n, p)     0..n       n*p          n*p*(1-p)
 *      equilikely(a, b)   a..b       (a+b)/2      ((b-a+1)*(b-a+1)-1)/12
 *      geometric(p)       0...       p/(1-p)      p/((1-p)*(1-p))
 *      pascal(n, p)       0...       n*p/(1-p)    n*p/((1-p)*(1-p))
 *      poisson(m)         0...       m            m
 *
 * and for 7 continuous random variables
 *
 *      uniform(a, b)      a < x < b  (a+b)/2      (b-a)*(b-a)/12
 *      exponential(m)     x > 0      m            m*m
 *      erlang(n, b)       x > 0      n*b          n*b*b
 *      normal(m, s)       all x      m            s*s
 *      logNormal(a, b)    x > 0         see below
 *      chiSquare(n)       x > 0      n            2*n
 *      student(n)         all x      0  (n > 1)   n/(n-2)   (n > 2)
 *
 * For the Lognormal(a, b), the mean and variance are
 *
 *                        mean = Exp(a + 0.5*b*b)
 *                    variance = (Exp(b*b) - 1)*Exp(2*a + b*b)
 *
 * Name            : Rvms.java (Random Variable ModelS)
 * Authors         : Steve Park & Dave Geyer
 * Translated by   : Richard Dutton & Jun Wang
 * Language        : Java
 * Latest Revision : 7-1-04
 * -------------------------------------------------------------------------
 */

public class StudentUtils {
	final double TINY = 1.0e-10;
	final double SQRT2PI = 2.506628274631;    /* sqrt(2 * pi) */
	static StudentUtils instance;

	public StudentUtils() {
	}

	public static StudentUtils getInstance() {
		if (instance == null)
			instance = new StudentUtils();
		return instance;
	}


	public double pdfStudent(long n, double x)
		/* ===================================
		 * NOTE: use n >= 1 and x > 0.0
		 * ===================================
		 */ {
		double s, t;

		s = -0.5 * (n + 1) * Math.log(1.0 + ((x * x) / (double) n));
		t = -logBeta(0.5, n / 2.0);
		return (Math.exp(s + t) / Math.sqrt((double) n));
	}

	public double cdfStudent(long n, double x)
		/* ===================================
		 * NOTE: use n >= 1 and x > 0.0
		 * ===================================
		 */ {
		double s, t;

		t = (x * x) / (n + x * x);
		s = inBeta(0.5, n / 2.0, t);
		if (x >= 0.0)
			return (0.5 * (1.0 + s));
		else
			return (0.5 * (1.0 - s));
	}

	public double idfStudent(long n, double u)
		/* ===================================
		 * NOTE: use n >= 1 and 0.0 < u < 1.0
		 * ===================================
		 */ {
		double t, x = 0.0;                       /* initialize to the mean, then */

		do {                                     /* use Newton-Raphson iteration */
			t = x;
			x = t + (u - cdfStudent(n, t)) / pdfStudent(n, t);
		} while (Math.abs(x - t) >= TINY);
		return (x);
	}

	/* ===================================================================
	 * The six functions that follow are a 'special function' mini-library
	 * used to support the evaluation of pdf, cdf and idf functions.
	 * ===================================================================
	 */

	/* ========================================================================
	 * LogGamma returns the natural log of the gamma function.
	 * NOTE: use a > 0.0
	 *
	 * The algorithm used to evaluate the natural log of the gamma function is
	 * based on an approximation by C. Lanczos, SIAM J. Numerical Analysis, B,
	 * vol 1, 1964.  The constants have been selected to yield a relative error
	 * which is less than 2.0e-10 for all positive values of the parameter a.
	 * ========================================================================
	 */
	public double logGamma(double a){
		double s[] = new double[6];
		double sum, temp;
		int i;

		s[0] = 76.180091729406 / a;
		s[1] = -86.505320327112 / (a + 1.0);
		s[2] = 24.014098222230 / (a + 2.0);
		s[3] = -1.231739516140 / (a + 3.0);
		s[4] = 0.001208580030 / (a + 4.0);
		s[5] = -0.000005363820 / (a + 5.0);
		sum = 1.000000000178;
		for (i = 0; i < 6; i++)
			sum += s[i];
		temp = (a - 0.5) * Math.log(a + 4.5) - (a + 4.5) + Math.log(SQRT2PI * sum);
		return (temp);
	}


	/* ======================================================================
	 * LogBeta returns the natural log of the beta function.
	 * NOTE: use a > 0.0 and b > 0.0
	 *
	 * The algorithm used to evaluate the natural log of the beta function is
	 * based on a simple equation which relates the gamma and beta functions.
	 *
	 */
	public double logBeta(double a, double b) {
		return (logGamma(a) + logGamma(b) - logGamma(a + b));
	}


	/* =======================================================================
	 * Evaluates the incomplete beta function.
	 * NOTE: use a > 0.0, b > 0.0 and 0.0 <= x <= 1.0
	 *
	 * The algorithm used to evaluate the incomplete beta function is based on
	 * equation 26.5.8 in the Handbook of Mathematical Functions, Abramowitz
	 * and Stegum (editors).  The absolute error is less than 1e-10 for all x
	 * between 0 and 1.
	 * =======================================================================
	 */
	public double inBeta(double a, double b, double x) {
		double t, factor, f, g, c;
		double p[] = new double[3];
		double q[] = new double[3];
		boolean swap;
		long n;

		if (x > (a + 1.0) / (a + b + 1.0)) { /* to accelerate convergence   */
			swap = true;                          /* complement x and swap a & b */
			x = 1.0 - x;
			t = a;
			a = b;
			b = t;
		} else                                 /* do nothing */
			swap = false;
		if (x > 0)
			factor = Math.exp(a * Math.log(x) + b * Math.log(1.0 - x) - logBeta(a, b)) / a;
		else
			factor = 0.0;
		p[0] = 0.0;
		q[0] = 1.0;
		p[1] = 1.0;
		q[1] = 1.0;
		f = p[1] / q[1];
		n = 0;
		do {                               /* recursively generate the continued */
			g = f;                           /* fraction 'f' until two consecutive */
			n++;                             /* values are small                   */
			if ((n % 2) > 0) {
				t = (double) (n - 1) / 2;
				c = -(a + t) * (a + b + t) * x / ((a + n - 1.0) * (a + n));
			} else {
				t = (double) n / 2;
				c = t * (b - t) * x / ((a + n - 1.0) * (a + n));
			}
			p[2] = p[1] + c * p[0];
			q[2] = q[1] + c * q[0];
			if (q[2] != 0.0) {                 /* rescale to avoid overflow */
				p[0] = p[1] / q[2];
				q[0] = q[1] / q[2];
				p[1] = p[2] / q[2];
				q[1] = 1.0;
				f = p[1];
			}
		} while ((Math.abs(f - g) >= TINY) || (q[1] != 1.0));
		if (swap)
			return (1.0 - factor * f);
		else
			return (factor * f);
	}
}
