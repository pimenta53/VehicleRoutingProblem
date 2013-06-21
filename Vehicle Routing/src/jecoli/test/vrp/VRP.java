
package jecoli.test.vrp;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;



import jecoli.test.MatUtils;
import jecoli.test.tsp.libtsp.EucTsp;
import jecoli.test.tsp.libtsp.Tsp;
//import utils.MatUtils;

public class VRP 
{                          
 int nc; // number of clients
 int nv; // number of vehicles

 Tsp tspp; // tsp instance with cost matrix
          // dimension of matrix = nc + 1
          // the deposit is node 0, clients are nodes 1 to nc

 double capacity; // capacity of the vehicles
 double[] demands; // demands of the nc clients    
 int [] sol;

 // move types
 public static final int STR_CROSS = 1;
 public static final int STR_EXCHANGE = 2;
 public static final int STR_RELOC = 3;
 // strategies for move
 public static final int ALWAYS_PERFORM = 0;
 public static final int PERFORM_ON_IMPROVE = 1;
 // local opt search strategies
 public static final int BEST = 0;
 public static final int FIRST = 1;

/** default constructor
*/
public VRP()
{

	
}

	public VRP(VRP vrp){
	
		this.nc = vrp.getNumberClients();
		 this.nv = vrp.getNumberClients();
		 this.tspp = vrp.getTsp(); 
		 this.capacity = vrp.getCapacity();
		 this.demands = vrp.getDemands();
		 this.sol=vrp.getSol();
		
	}

public VRP(int c, int v, Tsp t, double cap, double[] dem)
{
 this.nc = c;
 this.nv = v;
 this.tspp = t; 
 this.capacity = cap;
 this.demands = dem;
}

public VRP(String vrpfile, String tspfile) throws Exception
{
 FileReader f = new FileReader(vrpfile);
 BufferedReader B = new BufferedReader(f);   
 String str;
 str = B.readLine();
 int temp = Integer.parseInt(str);  
 setNumberVehicles(temp); 
 
 str = B.readLine();
 double temp1= Double.valueOf(str).doubleValue();
 setCapacity(temp1); 

 str = B.readLine();
 temp = Integer.parseInt(str);  
 setNumberClients(temp); 

 demands = new double[nc];
 for(int k=0; k<nc; k++)
 {
 	str = B.readLine();
 	temp1= Double.valueOf(str).doubleValue();
	demands[k] = temp1;
 }

 tspp = new EucTsp(tspfile);
 sol=MatUtils.give_rand_perm (nc+nv-1); 

}




int[] getSol() {
	return sol;
}

void setSol(int[] sol) {
	this.sol = sol;
}

/** Get the problem's number of vehicles
*/
int getNumberVehicles()
{
 return nv;
}


/** Sets the problem's number of vehicles
*/
void setNumberVehicles(int nv)
{
 this.nv = nv;
}

/** Get the problem's number of clients 
*/
int getNumberClients()
{
 return nc;
}

/** Sets the problem's number of clients 
*/
void setNumberClients(int nc)
{
  this.nc = nc;
}

/** Gets the cost matrix
*/
Tsp getTsp()
{
  return tspp;
}

/** Sets the cost matrix
*/
void setTsp(Tsp t)
{
 tspp = t; 
}

/** Get the problem's vehicle capacity
*/ 
double getCapacity()
{
 return capacity;
}

/** Sets the problem's vehicle capacity
*/ 
void setCapacity(double cap)
{
  capacity = cap;
}

/** Get the problem's demands
*/ 
double[] getDemands()
{
 return demands;
}

/** Sets the problem's demands
*/ 
void setDemands(double[] dem)
{
 demands = dem;
}

/** Get the demand of a given client
*/ 
double getDemand(int c)
{
 return demands[c];
}

/** Sets the demand of a given client
*/ 
void setDemand(int c, double d)
{
 demands[c] = d;
}



// Ways to represent solutions 
// 1. matrix representation - int [][]
//    each row specifies an individual route. The deposit is always included as first stop.
// 2. array representation -  int[] 
//    array of size equal to number clients + number vehicles- 1
//    the numbers between 0 and nc-1 mean clients and specify their positions in the route
//    the numbers between nc and nc+nv-2 are markers that specify the
//    beginning/end of the routes (can be read as the vehicle numbers)

/** Function that converts a solution between array representation to matrix representation 
*/
int[][] get_routes (int[] sol)
{
 int[][] res = new int[nv][];
 int[] temp = new int[nc];

 for(int v=0, k=0; k< nc+nv-1; v++) // for each vehicle
 {
   int l = 0;
   // first check if empty route
   if(sol[k] < nc) // route is not empty
   {
	 l = 2;
	 temp[0] = 0; // deposit
	 temp[1] = sol[k]+1;
     k++;
   }
   while(k< nc+nv-1 && sol[k] < nc)
   {
	 temp[l] = sol[k]+1;
	 l++;
     k++;
   }                
   if (l>0)
   {
   	res[v] = new int[l];
	for(int i=0; i<l; i++)
		res[v][i] = temp[i];
   }
   else // empty
   {
   	res[v] = new int[1];
	res[v][0] = 0;
   }
   k++;
   if(k >= nc+nv-1 && v<nv-1)
   {
   	res[v+1] = new int[1];
	res[v+1][0] = 0;
   }
 }
  return res;
}


/** Function that converts a solution between matrix representation and array representation 
*/
int[] routes_to_array (int[][] routes)
{
	int [] res = new int [nc+nv-1];

	for(int v=0, g=0; v < nv; v++)
	{
		//if(routes[v].length > 1) 
		for(int i=1; i<routes[v].length; i++, g++)
			res[g] = routes[v][i]-1;
		if(v!=nv-1)
		{
			res[g] = nc + v;
			g++;
		}
	}
	return res;
}

/** Extracts routes from a given order of stops 
Stops given from 0 to nc -1*/

int[][] routes_from_order (int [] ord)
{
	return routes_from_order(ord, false);
}

int[][] routes_from_order (int [] ord, boolean optim)
// optim - specifies if solution in optimized by min cost insertion in the routes
{
 int [] temp = new int[nc];
 int [][] tres = new int[nc][];
 int r, i, j;
 double dem = 0.0;

 temp[0] = 0;
 for(i=0, j=1, r=0; i< ord.length; i++)
 {
	if( dem+getDemand(ord[i]) > getCapacity() )
	{
		tres[r] = new int[j];
		for(int k=0; k< j; k++) tres[r][k] = temp[k];
		r++;
		dem = 0.0;
		j=1;
	}
	if( optim) insert_min_cost(temp, ord[i]+1, j);
	else temp[j] = ord[i]+1;	
	j++;
	dem += getDemand(ord[i]);
 }
 
 int [][] res = new int[r+1][];
 for(i=0; i<r; i++)
 	res[i] = tres[i];
 res[r] = new int[j];
 for(int k=0; k< j; k++) res[r][k] = temp[k];

 return res;
}

void insert_min_cost(int [] route, int node, int l)
// l - number of already inserted nodes
{
	if(l==1) route[1] = node;
	else
	{
		double inscost = 0.0;
		int pos = 0;
		inscost += tspp.get_distance(route[0], node);
		inscost += tspp.get_distance(node, route[1]);
		inscost -= tspp.get_distance(route[0], route[1]);

		for(int i=1; i< l; i++)
		{
			double newinscost = 0.0;
			newinscost += tspp.get_distance(route[i], node);
			newinscost += tspp.get_distance(node, route[(i+1)%l]);
			newinscost -= tspp.get_distance(route[i], route[(i+1)%l]);
			if (newinscost < inscost)
			{
				inscost = newinscost;
				pos = i;
			}
		}
		for(int j = l; j > pos+1; j--)
			route[j] = route[j-1];
		route[pos+1] = node;
	}
}

int[] routes_to_order (int[][] routes)
{
	int [] res = new int[nc];	

	for(int i=0, r=0; r < routes.length; r++)
	 for(int k=1; k < routes[r].length; k++, i++)
	 	res [i] = routes[r][k]-1;	

	return res;
}

/** Function that calculates the cost of a given solution in matrix representation */
double total_cost(int[][] routes)
{
 // it doesn't check if it is a valid solution in terms of capacities
 double cost = 0;
 int size = routes.length;
 //System.out.println("tamnho--->"+size);
 
 for(int v=0; v<size; v++) // for each vehicle
 {
   double costv = 0;
   //if(routes[v][0] != -1)
   cost += tspp.cost(routes[v]);	
 }
 return cost;
}


/** Function that calculates the cost of a given solution to a VRP instance.
@param sol the solution represented in array representation 
@returns the total cost
*/  
double total_cost(int[] sol)
{
	int[][] r = get_routes(sol);
	return total_cost(r);
}

/** Function that calculates the sum of the excess of load in
* the vehicles (given by the total demand in its route minus
* the vehcile capacity)
@param sol the solution represented in matrix representation 
@returns the sum of the excesses or 0 is the solution is feasible
*/ 
double excess_demand(int[][] routes)
{
 double exc = 0;

 for(int v=0; v<routes.length; v++) // for each vehicle
  {
	exc += excess_demand_route(routes[v]);
   }

 return exc;
}

// excess demand for a given route

double excess_demand_route(int[] route)
{
   double loadv = 0;
   	for(int i=1; i< route.length; i++)
		loadv += getDemand(route[i]-1);
	if (loadv > capacity) return (loadv-capacity);
	else return 0.0;
}

/** Function that calculates the sum of the excess of load in
* the vehicles (given by the total demand in its route minus
* the vehcile capacity)
@param sol the solution represented in array representation 
*/
double excess_demand(int[] sol)
{
	int[][] r = get_routes(sol);
	return excess_demand(r);
}


// Improvement heuristics
// within routes

int[] improve_routes_2opt(int[] sol)
{
	int[] res;
	int[][] routes = get_routes(sol);
	for(int i=0; i< routes.length; i++)
		if(routes[i].length > 2) tspp.one_pass_2opt(routes[i]);
	res = routes_to_array(routes);
	return res;
}

void improve_routes_2opt(int[][] routes)
{
	for(int i=0; i< routes.length; i++)
	{
		tspp.one_pass_2opt(routes[i]);
	}
}








// I/O

void print_routes (int[] sol)
{
 int[][] r = get_routes(sol);
 print_routes(r);
}

void print_routes(int[][] routes)
{
	for(int i=0; i < routes.length; i++)
	{
		System.out.print("Veiculo:" + i);
		for(int j=0; j < routes[i].length; j++)
			System.out.print(" "+ routes[i][j]);
		System.out.println(".");
	}
	System.out.println("CUSTO: " + total_cost(routes) );
	System.out.println("PENAL: " + excess_demand(routes));
}




public static void main (String [] args)
{

	int mni=0;
	int mnui=0;
	
	try{

  VRP v = new VRP("1pp.vrp","1pp.cit");


	for(int i = 0; i < 100000; i++)
	{

  	int [] rsol1 = MatUtils.give_rand_perm (v.getNumberClients()+v.getNumberVehicles()-1);    
  	
  	int [][] routes = v.get_routes(rsol1);
  	//v.print_routes (routes);
  	
  	if(v.excess_demand(routes)==0) mni++;
  	else mnui++;
  
  	
  	//v.rand_string_exchange(routes, 2,2);
  	//v.print_routes (routes);
	}
  System.out.println("boas alternativas ->"+mni+" más->"+mnui);
   //v.iterated_lo(STR_EXCHANGE, FIRST);
  	} catch (Exception e)
	{ e.printStackTrace(); }


}


}
