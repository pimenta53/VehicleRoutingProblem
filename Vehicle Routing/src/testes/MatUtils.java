/*
Copyright 2008,
CCTC - Computer Science and Technology Center
IBB-DEB - Institute for Biotechnology and  Bioengineering - Department of Biological Engineering
University of Minho

This is free software: you can redistribute it and/or modify
it under the terms of the GNU Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This code is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Public License for more details.

You should have received a copy of the GNU Public License
along with this code.  If not, see <http://www.gnu.org/licenses/>.

Created inside the SysBio Research Group (http://sysbio.di.uminho.pt)
University of Minho
*/

package testes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class MatUtils
{
  static int iset=0;
  static double gset;

  // trignometric functions
  public static double sinh(double t)
  { 
   return (Math.exp(t) - Math.exp(-t))/2;
  }

  public static double cosh(double t) 
  {
   return (Math.exp(t) + Math.exp(-t))/2;
  }

  public static double tanh(double t) 
  {
   Double aux=new Double(sinh(t)/cosh(t));
   if(aux.isNaN())
    { if(t>0.0) return 1.0;
      else return -1.0;
    }
   return aux.doubleValue();
  }

  public static double sec(double t) 
  {
   return 1/Math.cos(t);
  }

  public static double cosec(double t)
  {
   return 1/Math.sin(t);
  }

  public static double cot(double t)
  {
   return 1/Math.tan(t);
  }

  public static double sign(double a)
	{ double res;
	  if(a>0) res=1.0;
	  else if(a==0.0)res=0.0;
	  else res=-1.0;
	  return(res);
    }

  // random number generators

  public static double frandom(double a, double b) 
	// Random real within range [a,b[
        {return (b-a)*Math.random()+a;}

  public static int irandom(int a, int b) 
	// Random integer within range {a,...,b}    
	 { 
	 return (int)((b+1-a)*Math.random())+a;}

  public static int irandom(int b) 
	// Random integer within range {0,...,b}    
	 { return (int)((b+1)*Math.random());}

  public static double normal(double u, double s) 
	// Random normal distribution with mean u and standard deviation s
     {
       double fac,rsq,v1,v2;
       
       if(iset==0) {
                    do{
                       v1=2.0*Math.random()-1.0;
                       v2=2.0*Math.random()-1.0;
                       rsq=v1*v1+v2*v2;
                      }
                    while(rsq>=1.0 || rsq==0.0);
                    fac=Math.sqrt(-2.0*Math.log(rsq)/rsq);
                    gset=v1*fac;
                    iset=1;
                    return (u+s*(v2*fac)); 
                   }
       else {
              iset=0;
              return (u+s*gset);
            }
     }
	
	public static double normal2(double u, double s) 
	// Random normal distribution with mean u and standard deviation s
        // less efficient, less precision
     {
       double res=0.0;
	   //Random R=new();
       for(int i=0;i<12;i++)
          res+=Math.random();
	
       return (res-6)*s+u;
     }
	public static double cauchy(double u, double s)
	{
		double res = Math.random();
		double x = Math.tan( Math.PI*(res-0.5) );
		return u+s*x;
	}

    public static double zn(double alfa) 
	// Constant Zn - normal function
     {
      if(alfa==0.5) return(0.67);
      	else if(alfa==0.68) return(1.0);
		else if(alfa==0.80) return(1.28);
		else if(alfa==0.90) return(1.64);
		else if(alfa==0.95) return(1.96);
		else if(alfa==0.99) return(2.58);
	  return(0.0); // beware, this should never be true
	 }

    public static double confval(int N, double alfa,double std)
		{
 		 return zn(alfa)*(std/Math.sqrt( (double)N ));
		}

	// array functions

	public static void shufflearray(int [ ] a, int size)
	{
		int R;
	 	for(int i=0; i<size; i++)
	  	{
		 R=MatUtils.irandom(i,size-1);
		 int aux = a[i];
		 a[i] = a[R];
		 a[R] = aux;
		}

	}

    public static double product(double [] a, double [] b)
	 {
	   double res=0.0;
	   int size=a.length;
	   for(int i=0;i<size;i++)
	     res+=a[i]*b[i];

       return res;
	 }

	// statistics

    public static double sad(double [] a, double []b)
     {
           double res=0.0;
	   int size=a.length;
	   for(int i=0;i<size;i++)
	     res+=Math.abs(a[i]-b[i]);
           return res;
     }

    public static double sse(double [] a, double []b)
     {
           double res=0.0;
	   int size=a.length;
	   for(int i=0;i<size;i++)
	     res+=Math.pow(a[i]-b[i],2);
           return res;
	 }

    public static double nclasses(double [] a, double []b)
         {
           double res=0.0;
	       int size=a.length;
		   if (size == 1)
		   {
			if(a[0] == b[0]) res = 1.0;
		   }
		   else
		   {
	       	for(int i=0;i<size && res< 1.0;i++)
		   	  if(a[i]==1.0 && b[i]==1.0) res=1.0; 
		   }
           return res;
	     }


	// only works for binary or m-class representations of classes
    public static void choosehigher(double [] a)
         {
          
	       int size=a.length;
		   if (size==1) 
			{
				if(a[0] < 0.5) a[0] = 0;
				else a[0] = 1.0;
			}
		   else
		   {
		   	double max = a[0];
		   	int maxint = 0;
	       	for(int i=1;i<size;i++)
		   		if(a[i] > max) { max  = a[i]; maxint = i; }
	       	for(int i=0;i<size;i++)
				if ( i == maxint) a[i] = 1.0;
				else a[i] = 0.0;
			}
	     }
  
	// arrays - utilities

	public static double avgarr (int [] a)
	{
		double res = 0.0;
		for(int i=0; i<a.length; i++)
			res += a[i];

		return (res/a.length);
	}

	public static double stdarr (int [] a, double avg ) // std given the average
	{
		double res = 0.0;
		
		for(int i=0; i<a.length; i++)
			res += (a[i] - avg)*(a[i] - avg);

		return ( Math.sqrt(res / (a.length-1)));

	}

	public static int maxindex(int [] a)
	{
		int ind = 0;
		int max = a[0];

		for(int i=1; i<a.length; i++)
			if(a[i]>max) 
			{	
				max = a[i];
				ind = i;	
			}

		return ind;

	}
   
		public static double[] absarr(double [] a)
	{
		double[] absarr = new  double[a.length];
		
		for(int i=0; i<a.length; i++)
			absarr[i] = Math.abs(a[i]);

		return absarr;
	}

	public static double maxarr(double [] a)
	{
		double max = a[0];
		for(int i=1; i<a.length; i++)
			if(a[i]>max) max = a[i];

		return max;
	}

	public static double minarr(double [] a)
	{
		double min = a[0];
		for(int i=1; i<a.length; i++)
			if(a[i]<min) min = a[i];

		return min;
	}

	public static double avgarr (double [] a)
	{
		double res = 0.0;
		for(int i=0; i<a.length; i++)
			res += a[i];

		return (res/a.length);
	}

	public static double stdarr (double [] a, double avg ) // std given the average
	{
		double res = 0.0, r = 0.0;
		
		for(int i=0; i<a.length; i++)
			res += (a[i] - avg)*(a[i] - avg);

		if (res > 0.0) r = Math.sqrt(res / (a.length-1));
		
		return r;

	}
		
	public static double[] arrdiv(double[] vals, double divisor){
		
		double[] ret = new double[vals.length];
		
		for(int i=0; i< vals.length; i++){
			ret[i] = vals[i]/divisor;
		}
		
		return ret;
	}
	
	public static double p_norm(double[] vals, int p){
		
		double pnorm=0.0;
		
		for(double d: vals){			
			pnorm += Math.pow(Math.abs(d),p);			
		}
		
		return Math.pow(pnorm,(1.0d/(double)p));
	}
	
	// sums the absolute values of a double[]
	public static double sumDoubleArray(double[] double_array){
		double sum = 0.0;
		
		for(double d: double_array)
			sum+=Math.abs(d);
		
		return sum;
	}


	public static int countBooleanValues(boolean[] array, boolean value)
	{
		int count = 0;
		for(int i=0; i < array.length; i++)
			if (array[i] == value) count++;
		return count;
	}
	
	public static boolean[] logicalOr(boolean[] arr1, boolean[] arr2)
	{
		boolean[] res = new boolean[arr1.length];
		for(int i=0; i < arr1.length; i++)
				res[i] = (arr1[i] || arr2[i]);
		return res;
	}
	
	public static boolean is_in (int [] array, int elem)
	{
 		int i=0;
		boolean found=false;

		if(array!=null) 
		{
			while (i<array.length && !found )
 			{
   				if(array[i]== elem) found = true;
   				else i++;
 			} 
 		}
		return found;
	}

	public static boolean is_in (int [] array, int elem, int endpos)
	{
 		int i=0;
		boolean found=false;

		if(array!=null) 
		{
			while (i<endpos && !found )
 			{
   				if(array[i]== elem) found = true;
   				else i++;
 			} 
 		}
		return found;
	}

	public static boolean is_in (int [] array, int elem, int stpos, int endpos)
	{
 		int i=stpos;
		boolean found=false;

		if(array!=null) 
		{
			while (i<endpos && !found )
 			{
   				if(array[i]== elem) found = true;
   				else i++;
 			} 
 		}
		return found;
	}

	public static int pos_in_arr (int[] array, int elem)
	{
 		int i=0, res=-1;

 		while (i<array.length && res <0)
 		{
   			if(array[i]== elem) res = i;
   			else i++;
 		} 
		return res;
	}

	public static int pos_in_arr (String[] array, String elem)
	{
 		int i=0, res=-1;

 		while (i<array.length && res <0)
 		{
   			if(array[i].equals(elem)) res = i;
   			else i++;
 		} 
		return res;
	}

	
	public static int pos_in_arr (int[] array, int elem, int st, int end)
	{
 		int i=st, res=-1;

 		while (i<end && res <0)
 		{
   			if(array[i]== elem) res = i;
   			else i++;
 		} 
		return res;
	}

	public static void copy_arr(int [] a1, int [] a2)
	{
		for(int i=0; i< a1.length; i++)
			a2[i] = a1[i];
	}


	public static void save_array(double [] array, String fname) throws Exception
	{
		FileWriter f=new FileWriter(fname);
		BufferedWriter W=new BufferedWriter(f); 

		for(int i=0; i< array.length; i++)
			W.write( array[i] + "\n");
		W.flush();
		W.close();
		f.close();
	}

	public static void save_array(int [] array, String fname) throws Exception
	{
		FileWriter f=new FileWriter(fname);
		BufferedWriter W=new BufferedWriter(f); 

		for(int i=0; i< array.length; i++)
			W.write( array[i] + "\n");
		W.flush();
		W.close();
		f.close();
	}

	public static void saveArrayNames (int[] array, String[] names, String fname) throws Exception
	{
		FileWriter f=new FileWriter(fname);
		BufferedWriter W=new BufferedWriter(f); 

		for(int i=0; i< array.length; i++)
			W.write( names[i] + "\t" + array[i] + "\n");
		W.flush();
		W.close();
		f.close();
	}
	
	public static double[] read_array (String filename) throws Exception
 	{
		FileReader f = new FileReader(filename);
		BufferedReader inp = new BufferedReader(f);

		String str = inp.readLine();
		int nl = 0;
		while(str!=null) {
			nl++;
			str = inp.readLine();
		}

		inp.close();
		f.close();

		double[] res = new double[nl];
		f = new FileReader(filename);
		inp = new BufferedReader(f);
		for(int i=0; i < nl; i++) {
			str = inp.readLine();
			res[i] = Double.valueOf(str).doubleValue();
		}

		inp.close();
		f.close();

		return res;
 	}

	public static int next_elem (int [] array, int elem)
	{
  		int j =0;
		boolean found = false;

  		while (!found && (j<array.length) )
  		{
    		if(array[j] == elem) found = true;
    		j++;
  		}
  		if(found) return array[j%array.length];
  		else return -1;
	}

	
	public static int prev_elem (int [] array, int elem)
	{
  		int j =0;
		boolean found = false;

  		while (!found && (j<array.length) )
  		{
    		if(array[j] == elem) found = true;
    		else j++;
 		}
  		if(found) return array[(j==0)?(array.length-1):(j-1)];
  		else return -1;
	}


	public static void next_and_prev(int [] array, int elem, int [] np)
	// np[0] - next; np[1] - prev.
	{
  		int j =0;
		boolean found = false;

  		while (!found && (j<array.length) )
  		{
    		if(array[j] == elem) found = true;
    		else j++;
 		}
  		if(found) 
		{
			np[1]= array[(j==0)?(array.length-1):(j-1)];
			np[0]= array[(j+1)%array.length];
		}
  		else 
		{
			np[1]= -1;
			np[0]= -1;
		}
	}

	public static int [] give_k_perm (int [] elems, int k)
	{
  		int i, j, l;
  		int [] res = new int[k];
  		int [] aux = new int[k]; 

		for(i=0;i<k;i++) aux[i]=elems[i];

		for(j=k-1,i=0;j>=0;j--,i++)
  		{
    		int r=irandom(j); /* dar um numero entre 0 e j */ 
    		res[i]=aux[r];
   			for(l=r;l<j;l++)
        		 aux[l]=aux[l+1];
  		}

 		return(res);
	}

	public static int [] give_rand_perm (int k)
	{
  		int i, j, l;
  		int[] res = new int[k];
  		int[] aux = new int[k];

  		for(i=0;i<k;i++)
      		aux[i]=i;
  		for(j=k-1,i=0;j>=0;j--,i++)
  		{
    		int r=irandom(j); /* dar um numero entre 0 e j */ 
    		res[i]=aux[r];
    		for(l=r;l<j;l++)
         		aux[l]=aux[l+1];
  		}
 		return(res);
	}


	public static double normalized(double v, double min, double max, double ll, double ul)
	// min, max - new limits (normalized) ul, ll - limits of the data v - value to normalize
	{
		return( min + (v-ll)*(max-min)/(ul-ll) );
	}

    public static void debug(String a)
       {
          System.out.println(a);
          System.out.flush();
       }

	// string handling

	public static boolean isNumber (String s)
	{
		boolean pointcanappear = true;
		boolean signcanappear = true;
		boolean res = true;
		int i=0;

		if(s.length()==0) res = false;
		while ( i<s.length() && res)
		{
			char c = s.charAt(i);	
			boolean ok = false;
			if(Character.isWhitespace(c))
				ok = true;
			else if(Character.isDigit(c))
			{
					if(signcanappear) signcanappear = false;
					ok = true;
			}
			else if(c=='-' && signcanappear)
			{
					signcanappear = false;
					ok = true;
			}
			else if( (c=='.' || c==',') && pointcanappear)
			{
					pointcanappear = false;
					ok = true;
			}
			if(ok) i++;
			else res = false;
		}
		return res;
	}

	public static boolean isPositiveInteger(String s)
	{
		boolean res = true;
		int i=0;

		if(s.length()==0) res = false;
		while ( i<s.length() && res)
		{
			char c = s.charAt(i);	
			boolean ok = false;
			if(Character.isWhitespace(c))
				ok = true;
			else if(Character.isDigit(c))
					ok = true;
			if(ok) i++;
			else res = false;
		}
		return res;
	}

	public static String doubleToString (double d, int df)
	{
		String res = "";
		NumberFormat form;
		form = NumberFormat.getInstance(Locale.US); 
		form.setMaximumFractionDigits(df);
		form.setGroupingUsed(false);
        try {
			res = form.format(d);
        } catch (IllegalArgumentException e) { res = "";}
		return res;
	}

    public static void printa(double [] arr)
	{
	 for(int i=0;i<arr.length;i++)
	    System.out.print(arr[i]+" ");
	}
	
    public static void printa(int [] arr)
	{
	 for(int i=0;i<arr.length;i++)
	    System.out.print(arr[i]+" ");
	}

	public static boolean perfect (int n)
	{
		int sum = 0;

		for(int k=1; k <= n/2; k++)
			if (n%k == 0) sum +=k;

		return (n==sum);
	}

	// returns the next combination of n elements in groups of r (r = a.length)
	public static boolean lastCombination (int[] a, int n)
	{
		boolean res = true; 
		int k = 0;
		while (res && k < a.length) 
			if (a[k] != n - a.length + k)
				res = false;
			else k++;
		return res;
	}
	
	public static int [] nextCombination (int[] a, int n)
	{
		if (a == null) return null;
		int [] next = new int[a.length];
		int r = a.length;
		int i = r-1; 
		
		if (lastCombination(a,n)) return null;
		while (a[i] == n - r + i) {
		      i--;
		}
		next[i] = a[i] + 1;
		for (int j = i + 1; j < r; j++) {
		      next[j] = next[i] + j - i;
		    }
		for (int j=0; j < i; j++) next[j] = a[j];
		return next;
	}
	

	public static boolean[] nextCombination(boolean[] s)
	{
		boolean [] nextS = new boolean[s.length];
		
		int pos = s.length - 1;
		
		while(pos >=0 && s[pos]== true )
			pos--;
		if (pos < 0) nextS = null;
		else
		{
			for(int i=0; i < pos; i++) nextS[i] = s[i];
			nextS[pos] = true;
			for(int i = pos+1; i < s.length; i++) nextS[i] = false;
		}
		return nextS;
	}
	

	public static double roundToMil (double val)
	{
		 double r = val * 1000.0;
		 int l = (int)r;
		 if (r-l >=0.5) l++;
		
		 r = (double)l / 1000.0;
		 return r;
	}
	
	public static boolean [] generateRandomBooleanArray(int size)
	{
		boolean [] res = new boolean[size];
		Random r = new Random();
		for(int i=0;i<size;i++) res[i] = r.nextBoolean();		
		return res;
	}
	
	public static double euclideanDistance (double[] a, double[] b)
	{
		// assumes a and b are of the same size
		double sq = 0.0;
		for(int i=0; i < a.length; i++)
			sq += (Math.pow(a[i]-b[i],2.0));
		return Math.sqrt(sq);
	}
	
	public static double round(double value, int nDecimals){
		BigDecimal r = new BigDecimal(value);
		return r.setScale(nDecimals, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * Greatest Common Divisor according to Euclid's algorithm
	 * 
	 * @param array
	 * @return
	 */
	public static long gcd(long [] array) {

		// local vars
		int listSize=array.length;
		long a, b, gcd1;
		long ONE = (long) 1;

		long [] arrayTmp = array.clone(); 

		Arrays.sort(arrayTmp);
		if(listSize == 0 || arrayTmp[listSize-1]==0) {
			return(ONE);
		}

		a = arrayTmp[0];
		gcd1 = a;
		for(int i = 1;i<listSize;i++) {
			if(gcd1 == 1) {
				break;
			}
			gcd1 = a;
			a = arrayTmp[i];
			b = gcd1;
			while( b != 0) {
				gcd1 = b;
				b = a % gcd1;
				a = gcd1;
			}
			// System.out.println(gcd1);
		}
		return(gcd1);
	}
	
	  /**
	   * Summatory.
	   * 
	   * @param value the value
	   * 
	   * @return the int
	   */
	  public static int summatory(int value){
		  int summatoryValue = 0;
		  
		for(int i = 0;i <= value;i++)
			summatoryValue += i;
		  
		return summatoryValue;
	  }
}
