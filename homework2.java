import java.util.Scanner;
import java.io.File;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;
import java.util.Arrays;

class homework2 {
	/*matrix counter*/
	private static float[][] matrix = new float[26][26];
	/*current key */
	private static int[] key = new int[26];
	/*random number generator*/

	private static Random rn = new Random();
	private static final float E = 2.7182818284590452353602874713526624977572f;

	public homework2(){
	}

	/* function that simplifies System.out */
	public static void print(String a) {
		System.out.print(a);
	}

	/* incrament map if a and b are alphabets */
	static void addToMatrix(char a, char b) {
		if(97 <= (int)a && (int)a <= 122 && 97 <= (int)b && (int)b <= 122) {
			matrix[(int)a-97][(int)b-97]++;
		}
	}

	/* compute p for a decripted message m */
	static float computePlausibility(String m) {
		float p = 0.0f;
		float mij;
		for(int i = 0; i < m.length()-1; ++i) {
			mij = retriveFromMatrix(m.charAt(i), m.charAt(i+1));
			if(mij != -1) {
				p += mij;
			}
		}
		return (float)java.lang.Math.log((double)p);
	}

	/* retrive the count from matrix if a and b are alphabets */
	static float retriveFromMatrix(char a, char b) {
		if(97 <= (int)a && (int)a <= 122 && 97 <= (int)b && (int)b <= 122) {
			return matrix[(int)a-97][(int)b-97];
		}
		return -1;
	}

	/* swap 2 keys in Key and return a copy of the new Key */
	static int[] swapKeys() {
		int index1 = rn.nextInt(26);
		int index2 = rn.nextInt(26);

		int[] newKey = Arrays.copyOf(key, 26);

		newKey[index1] = key[index2];
		newKey[index2] = key[index1];
		return newKey;
	}

	/* decrypt c using curkey */
	static String decode(String c, int[] curkey) {
		String m = "";
		for(int i = 0; i < c.length(); ++i) {
			char cur = c.charAt(i);
			if (cur != ' ' && cur != ',' && cur != '.') {
				cur = (char)(curkey[(int)c.charAt(i)-97] + 97);
			}
			m += cur;
		}
		return m;
	}

	/* add 1 to all elements in the counter matrix */
	public static void incrementMatrix() {
		for(int i = 0; i < 26; ++i) {
			for(int j = 0; j < 26; ++j) {
				matrix[i][j]++;
			}
		}
	}

	/* substract 1 from all elements in counter matrix */
	public static void decrementMatrix() {
		for(int i = 0; i < 26; ++i) {
			for(int j = 0; j < 26; ++j) {
				matrix[i][j]--;
			}
		}
	}


	/*flip a biased coin with probability p */
	public static boolean biasedCoin(float p) {
		print("p: " + p + " ");

		float flip = (rn.nextFloat() * (1-0) + 0);

		print("flip : " + flip + "\n");
		return flip < p;
	}

	/* Implementing Fisher–Yates shuffle */
	static void shuffleArray(int[] ar){
		Random rnd = ThreadLocalRandom.current();
		for (int i = ar.length - 1; i > 0; i--)
		{
		  int index = rnd.nextInt(i + 1);
		  // Simple swap
		  int a = ar[index];
		  ar[index] = ar[i];
		  ar[i] = a;
		}
	}

	public static void main(String args[]) {

		String content = "";
		try{
			content = new Scanner(new File("war-and-peace.txt")).useDelimiter("\\Z").next();
			System.out.println(content);
		} catch(Exception e) {
			print("error parsing book");
		}

		/* initialize key as 0~25 */
		for(int i = 0; i < 25; ++i) {
			key[i] = i;
		}

		shuffleArray(key);

		String cipher = " qkne l knixw tkn onixenw iytxrerjnx, qkne tkn uxrray, tkn almbxny, qnxn xiemnw le crobjey hnarxn jn," +  
			   " qkne l qiy ykrqe tkn ckixty iew wlimxijy, tr iww, wlvlwn, iew jniybxn tknj, " +
			   " qkne l ylttlem knixw tkn iytxrerjnx qknxn kn onctbxnw qltk jbck iuuoibyn le tkn " +
			   " onctbxn xrrj, " +
			   " krq yrre beiccrbetihon l hncijn tlxnw iew ylcd, " +
			   " tloo xlylem iew molwlem rbt l qiewnxnw raa hz jzynoa, le tkn jzytlcio jrlyt elmkt ilx, iew axrj tljn tr tljn, orrdnw bu le unxanct ylonecn it tkn ytixy.";
		

		/*loop through the book and update counter matrix */
		char[] charArray = content.toCharArray();
		
		for(int i = 0; i < charArray.length - 1; ++i) {
			addToMatrix(charArray[i], charArray[i+1]);
		}

		/* normalize counter matrix */
		incrementMatrix();

		int total = 0;
		for(int i = 0; i < 26; ++i) {
			for(int j = 0; j < 26; ++j) {
				total += matrix[i][j];
			}
		}
		for(int i = 0; i < 26; ++i) {
			for(int j = 0; j < 26; ++j) {
				matrix[i][j] /= total;
			}
		}

		int count = 40000;
		float prePlaus = computePlausibility(decode(cipher, key));
		float curPlaus;
		int[] newKey;
		while(count > 0) {
			newKey = swapKeys();
			/*compute p(f)**/
			curPlaus = computePlausibility(decode(cipher, newKey));

			/* if p(f*) is greater than p(f) */
			if(curPlaus > prePlaus) {
				/* accept new key */

				key = Arrays.copyOf(newKey, 26);
				prePlaus = curPlaus;
			} else {
				/*
				if(prePlaus < curPlaus) {
					print("wrong!!");
				}
				if(Math.abs(curPlaus/prePlaus) > 1) {
					print("cant happen" + (curPlaus/prePlaus) + "\n");
				}*/

				/*flip a p(f*)/p(f) coin */
				if(biasedCoin((float)Math.abs(curPlaus/prePlaus))) {
					key = Arrays.copyOf(newKey, 26);
					prePlaus = curPlaus;
				}
			}
			
			count--;	
			print(prePlaus + "");
		}

	    print(decode(cipher, key));
	}
}