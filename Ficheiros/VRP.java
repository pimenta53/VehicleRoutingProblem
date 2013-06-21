
package gavrp;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.GregorianCalendar;

import utils.MatUtils;
import libtsp.*;

public class VRP 
{                          
 int nc; // number of clients
 int nv; // number of vehicles

 Tsp tspp; // tsp instance with cost matrix
          // dimension of matrix = nc + 1
          // the deposit is node 0, clients are nodes 1 to nc

 double capacity; // capacity of the vehicles
 double[] demands; // demands of the nc clients    


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

 for(int v=0; v<nv; v++) // for each vehicle
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

 for(int v=0; v<nv; v++) // for each vehicle
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

// among different routes

int[][] iterated_lo(int move, int strat)
{
  	int [] sol1 = MatUtils.give_rand_perm (getNumberClients()+getNumberVehicles()-1);    
  	int [][] routes = get_routes(sol1);

	iterated_lo(routes, move, strat);
	return routes; 
  
}

void iterated_lo(int [][] routes, int move, int strat)
{
 double imp = 0.0;

 //print_routes(routes);
 System.out.println("Custo: " + total_cost(routes) + " Pen.: " + excess_demand(routes));
 do 
 	{
		switch (move)
		{
		case STR_CROSS:
			if (strat == FIRST) imp = first_imp_string_cross (routes);
			else if (strat == BEST) imp = best_imp_string_cross (routes);
			break;
		case STR_EXCHANGE:
			if (strat == FIRST) imp = first_imp_string_exchange (routes, 2);
			else if (strat == BEST) imp = best_imp_string_exchange (routes, 2);
			break;
		case STR_RELOC:
			if (strat == FIRST) imp = first_imp_string_relocation (routes, 2);
			else if (strat == BEST) imp = best_imp_string_relocation (routes, 2);
			break;
		}
		improve_routes_2opt(routes);
		System.out.println("Custo: " + total_cost(routes) + " Pen.: " + excess_demand(routes));
	}
 while(imp>0.0);

}

// STRING CROSS MOVES - source Van Breedam

// string cross with random routes and cut points
// mode = ALWAYS_PERFORM ou PERFORM_ON_IMPROVE
void rand_string_cross(int[][] routes, int mode)
{
 // select two routes
 int r1, r2, c1, c2;
 int c = 0; // used to avoid possibility of infinite loopS

 do 
 {
 	r1 = MatUtils.irandom(routes.length-1);
	c++;
 }	
 while (c < 2*routes.length && routes[r1].length <3 );
 c = 0;
 do{
 	r2 = MatUtils.irandom(routes.length-1);
	c++;
 } while (c < 2*routes.length && (routes[r2].length<3 || r1==r2) );

 if (c != 2*routes.length) 
 {
 // select cut points
 c1 = MatUtils.irandom(routes[r1].length-3)+2;
 c2 = MatUtils.irandom(routes[r2].length-3)+2;

 boolean feasible = (excess_demand(routes) == 0.0);
 string_cross(routes, r1, r2, c1, c2, mode, feasible, true);
 }

}

// first improvement
double first_imp_string_cross (int [][] routes)
{
 boolean improved = false;
 double res = 0.0;
 boolean feasible = (excess_demand(routes) == 0.0);

 for(int r1 = 0; r1 < routes.length && !improved; r1++)
 		for(int r2 = r1+1; r2 < routes.length && !improved; r2++)
				for(int c1 = 2; c1 < routes[r1].length && !improved; c1++)
					for(int c2 = 2; c2 < routes[r2].length && !improved; c2++)
 					if(routes[r1].length >= 3 && routes[r2].length >= 3)
					{
 						res = imp_string_cross(routes, r1, r2, c1, c2, feasible);
						if(res > 0.0) improved = true;;
					}
 return res;
}


// best improvement
double best_imp_string_cross(int [][] routes)
{
 double best = 0.0;
 int br1=0, br2=0, bc1=0, bc2=0;
 boolean feasible = (excess_demand(routes) == 0.0);

 for(int r1 = 0; r1 < routes.length; r1++)
 		for(int r2 = r1+1; r2 < routes.length; r2++)
				for(int c1 = 2; c1 < routes[r1].length; c1++)
					for(int c2 = 2; c2 < routes[r2].length; c2++)
 					if(routes[r1].length >= 3 && routes[r2].length >= 3)
					{
 						double imp = imp_string_cross(routes, r1, r2, c1, c2, feasible, false);
						if (imp > best ) { best = imp; br1 = r1; br2 = r2; bc1 = c1; bc2 = c2; }
					}
 if (best> 0.0) imp_string_cross(routes, br1, br2, bc1, bc2, feasible, true);
 return best;
}

/* String cross move - always perform the move*/
double string_cross(int [][] routes,int r1, int r2, int c1, int c2) 
{
	boolean feasible = (excess_demand(routes) == 0.0);
	return string_cross(routes, r1, r2, c1, c2, ALWAYS_PERFORM, true, true);
}

double imp_string_cross(int [][] routes,int r1, int r2, int c1, int c2, boolean feasible) 
{
	return imp_string_cross(routes, r1, r2, c1, c2, feasible, true);
}

double imp_string_cross(int [][] routes, int r1, int r2, int c1, int c2, 
	boolean feasible, boolean sw) 
{
	return string_cross(routes, r1, r2, c1, c2, PERFORM_ON_IMPROVE, feasible, sw);
}
// String cross move - only perform if there is improvement
// returns improvement 
// if sw is true perform the switch when solution is better else only return value
// mode = ALWAYS_PERFORM ou PERFORM_ON_IMPROVE
double string_cross(int [][] routes, int r1, int r2, int c1, int c2, 
	int mode, boolean feasible, boolean sw) 
{
 double res;
 int [] newr1;
 int [] newr2;
 int i, j;

 newr1 = new int[c1+ (routes[r2].length-c2)];
 newr2 = new int[c2+ (routes[r1].length-c1)];

 for(i=0; i<c1; i++)
 	newr1[i] = routes[r1][i];
 for(j=c2; j < routes[r2].length; j++, i++)
 	newr1[i] = routes[r2][j]; 

 for(i=0; i<c2; i++)
 	newr2[i] = routes[r2][i];
 for(j=c1; j < routes[r1].length; j++, i++)
 	newr2[i] = routes[r1][j]; 

 if (!feasible)
 {
 	double oldpen = excess_demand_route(routes[r1]) + excess_demand_route(routes[r2]);
 	double newpen = excess_demand_route(newr1) + excess_demand_route(newr2);
 	if(mode == ALWAYS_PERFORM || newpen < oldpen) 
	{
 		if (sw) {
			routes[r1] = newr1;
 			routes[r2] = newr2;
			}
		res = oldpen - newpen;
	}
	else res = 0.0;
 }
 else
 {
 	double newpen = excess_demand_route(newr1) + excess_demand_route(newr2);
	if(newpen > 0.0 && mode != ALWAYS_PERFORM) res = 0.0;
	else
	{
		double oldcost = tspp.cost(routes[r1])+tspp.cost(routes[r2]);
		tspp.one_pass_2opt(newr1);
		tspp.one_pass_2opt(newr2);
 		double newcost = tspp.cost(newr1)+tspp.cost(newr2);

 		if(mode == ALWAYS_PERFORM || oldcost > newcost)
 		{
 			if (sw) {
				routes[r1] = newr1;
 				routes[r2] = newr2;
				}
			res = oldcost - newcost;
 		}
 	else res = 0.0;
	}
  }

 return res;
}

// STRING EXCHANGE

// String exchange move - random routes and cut points 
void rand_string_exchange(int[][] routes, int mode, int K)
{
 // select two routes
 int r1, r2, c1, c2, x1, x2;
 int c = 0; // avoid infinite loops
 
 // select cut lengths
 //x1 = MatUtils.irandom(K-1)+1; //number of stops from r1 to r2
 //x2 = MatUtils.irandom(K-1)+1; //number of stops from r2 to r1
 x1 = MatUtils.irandom(K); //number of stops from r1 to r2
 x2 = MatUtils.irandom(K); //number of stops from r2 to r1

 do {
 r1 = MatUtils.irandom(routes.length-1);
 c++;
 }
 while (c < 2* routes.length && routes[r1].length <= x1);
 do{
 	r2 = MatUtils.irandom(routes.length-1);
	c++;
 } while (c < 2* routes.length && ( r1==r2 || routes[r2].length <= x2) );


 if (c != 2*routes.length) 
 {
 // select cut points
 c1 = MatUtils.irandom(routes[r1].length-x1-1)+1;
 c2 = MatUtils.irandom(routes[r2].length-x2-1)+1;

 boolean feasible = (excess_demand(routes) == 0.0);
 string_exchange(routes, r1, r2, x1, x2, c1, c2, mode, feasible, true);
 }
}

double first_imp_string_exchange(int [][] routes, int K)
{
 boolean improved = false; 
 double res = 0.0; 
 boolean feasible = (excess_demand(routes) == 0.0);

 for(int x1 = 0; x1 <= K && !improved; x1++)
 for(int x2 = 0; x2 <= K && !improved; x2++)
 for(int r1 = 0; r1 < routes.length && !improved; r1++)
 		for(int r2 = r1+1; r2 < routes.length && !improved; r2++)
				for(int c1 = 1; c1 <= (routes[r1].length-x1) && !improved; c1++)
					for(int c2 = 1; c2 <= (routes[r2].length-x2) && !improved; c2++)
						if(r1 != r2 && routes[r1].length > x1 && routes[r2].length > x2) 
						{
 							res = imp_string_exchange(routes, r1, r2, x1, x2, c1, c2, feasible);
							if (res > 0.0) improved = true;
						}
  return res;
}

double best_imp_string_exchange(int [][] routes, int K)
{
 double best = 0.0;
 int bx1=0, bx2=0, br1=0, br2=0, bc1=0, bc2=0;
 boolean feasible = (excess_demand(routes) == 0.0);

 for(int x1 = 0; x1 <= K; x1++)
 for(int x2 = 0; x2 <= K; x2++)
 for(int r1 = 0; r1 < routes.length; r1++)
 		for(int r2 = r1+1; r2 < routes.length; r2++)
				for(int c1 = 1; c1 <= (routes[r1].length-x1); c1++)
					for(int c2 = 1; c2 <= (routes[r2].length-x2); c2++)
						if(r1 != r2 && routes[r1].length > x1 && routes[r2].length > x2) 
						{
 							double imp =  imp_string_exchange(routes, r1, r2, x1, x2, c1, c2, feasible, false); 
							if (imp > best) { best = imp; bx1 = x1; bx2 = x2; br1 = r1; br2 = r2; 
							                  bc1 = c1; bc2 = c2; }
						}

 if(best> 0.0) imp_string_exchange(routes, br1, br2, bx1, bx2, bc1, bc2, feasible, true); 
 return best;
}

// String exchange move - always perform
void string_exchange(int [][] routes, int r1, int r2, int x1, int x2, int c1, int c2)
{
	boolean feasible = (excess_demand(routes) == 0.0);
	string_exchange(routes, r1, r2, x1, x2, c1, c2, ALWAYS_PERFORM, feasible, true);
}

double imp_string_exchange(int [][] routes, int r1, int r2, int x1, int x2, int c1, int c2,
	boolean feasible)
{
 return imp_string_exchange(routes, r1, r2, x1, x2, c1, c2, feasible, true);
}
double imp_string_exchange(int [][] routes, int r1, int r2, int x1, int x2, int c1, int c2, 
	boolean feasible, boolean sw)
{
 return string_exchange(routes, r1, r2, x1, x2, c1, c2, PERFORM_ON_IMPROVE, feasible, sw);
}

// String exchange move - only when improves 
// returns cost of new solution
// mode = ALWAYS_PERFORM ou PERFORM_ON_IMPROVE
double string_exchange(int [][] routes, int r1, int r2, int x1, int x2, int c1, int c2, 
	int mode, boolean feasible, boolean sw)
{
 int [] newr1;
 int [] newr2;
 int i, j, k;
 double res;

 newr1 = new int[routes[r1].length-x1+x2];
 newr2 = new int[routes[r2].length-x2+x1];

 for(i=0, k=0; i<c1; i++, k++)
 	newr1[k] = routes[r1][i];
 for(j=c2; j< c2+x2; j++, k++)
 	newr1[k] = routes[r2][j]; 
 for(; i<c1+x1; i++);
 for(; i< routes[r1].length; i++, k++)
 	newr1[k] = routes[r1][i];

 for(i=0, k=0; i<c2; i++, k++)
 	newr2[k] = routes[r2][i];
 for(j=c1; j< c1+x1; j++, k++)
 	newr2[k] = routes[r1][j]; 
 for(; i<c2+x2; i++);
 for(; i< routes[r2].length; i++, k++)
 	newr2[k] = routes[r2][i];

 if (!feasible)
 {
 	double oldpen = excess_demand_route(routes[r1]) + excess_demand_route(routes[r2]);
 	double newpen = excess_demand_route(newr1) + excess_demand_route(newr2);
 	if(mode == ALWAYS_PERFORM || newpen < oldpen) 
	{
 		if (sw) {
			routes[r1] = newr1;
 			routes[r2] = newr2;
			}
		res = oldpen - newpen;
	}
	else res = 0.0;
 }
 else
 {
 	double newpen = excess_demand_route(newr1) + excess_demand_route(newr2);
	if(newpen > 0.0 && mode != ALWAYS_PERFORM) res = 0.0;
	else
	{
		double oldcost = tspp.cost(routes[r1])+tspp.cost(routes[r2]);
		tspp.one_pass_2opt(newr1);
		tspp.one_pass_2opt(newr2);
 		double newcost = tspp.cost(newr1)+tspp.cost(newr2);

 		if(mode == ALWAYS_PERFORM || oldcost > newcost)
 		{
 			if (sw) {
				routes[r1] = newr1;
 				routes[r2] = newr2;
				}
			res = oldcost - newcost;
 		}
 	else res = 0.0;
	}
  }
 return res;
}

// STRING RELOCATION

// random routes and cut points
void rand_string_relocation(int[][] routes, int mode, int K)
{
 // select two routes
 int r1, r2, c1, c2, x1, x2;
 int [] newr1;
 int [] newr2;
 int c = 0;

 // select cut lengths
 x1 = MatUtils.irandom(K-1)+1; //number of stops from r1 to r2

 do{
 r1 = MatUtils.irandom(routes.length-1); // source
 c++;
 }
 while (c < 2* routes.length && routes[r1].length <= x1);
 do{
 	r2 = MatUtils.irandom(routes.length-1); // destination
	c++;
 } while (c < 2* routes.length && r1==r2);


 if (c != 2*routes.length) 
 {
 // select cut points
 c1 = MatUtils.irandom(routes[r1].length-x1-1)+1;
 c2 = MatUtils.irandom(routes[r2].length-1)+1;

 boolean feasible = (excess_demand(routes) == 0.0);
 string_relocation (routes, r1, r2, x1, c1, c2, mode, feasible, true); 
 }
}

double first_imp_string_relocation(int [][] routes, int K)
{
 boolean improve = false;
 double res = 0.0;
 boolean feasible = (excess_demand(routes) == 0.0);

 for(int x1 = 1; x1 <= K && !improve; x1++)
 for(int r1 = 0; r1 < routes.length && !improve; r1++)
 		for(int r2 = 0; r2 < routes.length && !improve; r2++)
				for(int c1 = 1; c1 <= routes[r1].length-x1 && !improve; c1++)
					for(int c2 = 1; c2 <= routes[r2].length && !improve; c2++)
 						if(r1 != r2 && routes[r1].length > x1)
						{
 						  res = imp_string_relocation(routes, r1, r2, x1, c1, c2, feasible);
						  if (res > 0) improve = true;
						 }
 return res;
}

double best_imp_string_relocation(int [][] routes, int K)
{
 double best = 0.0;
 int bx1=0, br1=0, br2=0, bc1=0, bc2=0;
 boolean feasible = (excess_demand(routes) == 0.0);

 // find best move
 for(int x1 = 1; x1 <= K; x1++)
 for(int r1 = 0; r1 < routes.length; r1++)
 		for(int r2 = 0; r2 < routes.length; r2++)
				for(int c1 = 1; c1 <= routes[r1].length-x1; c1++)
					for(int c2 = 1; c2 <= routes[r2].length; c2++)
 						if(r1 != r2 && routes[r1].length > x1)
						{
 						 double imp = imp_string_relocation(routes, r1, r2, x1, c1, c2, feasible, false);
						 if(imp>best) { best = imp; bx1 = x1; br1 = r1; br2 = r2; bc1 = c1; bc2 = c2; }
						 }
 // execute best move
 if(best > 0.0) imp_string_relocation(routes, br1, br2, bx1, bc1, bc2, feasible, true);
 return best;
}

// String exchange move - always perform

double imp_string_relocation(int [][] routes, int r1, int r2, int x1, int c1, int c2, boolean feasible)
{
 return imp_string_exchange(routes, r1, r2, x1, 0, c1, c2, feasible);
}

double imp_string_relocation(int [][] routes, int r1, int r2, int x1, int c1, int c2, 
	boolean feasible, boolean sw)
{

 return imp_string_exchange(routes, r1, r2, x1, 0, c1, c2, feasible, sw);
}

double string_relocation(int [][] routes, int r1, int r2, int x1, int c1, int c2, 
	int mode, boolean feasible, boolean sw)
{
 return string_exchange(routes, r1, r2, x1, 0, c1, c2, mode, feasible, sw);
}


int[] inversion_improve(int [] sol)
{
 int i, j, l;
 int s = sol.length;
 int [] newsol = new int[s];
 int pos = MatUtils.irandom(s-1);
 int k = MatUtils.irandom(s-2);
 int n = (int)(k/2);

 for(i=0; i < s; i++) newsol[i] = sol[i];
 for(i=0, j=pos, l=(pos+k-1)%s; i<n;i++,j=(j+1)%s) {
		newsol[j] = sol[l];
		newsol[l] = sol[j];
    	if(l > 0) l--;
    	else l=s-1;
  	}
 improve_routes_2opt(newsol);

 if (total_cost(newsol) < total_cost(sol))
 	return newsol;
 else return sol;
}

int[] order_inversion_improve(int [] sol)
{
 int i, j, l;
 int [] newsol = new int[nc];
 int pos = MatUtils.irandom(nc-1);
 int k = MatUtils.irandom(nc-2);
 int n = (int)(k/2);

 for(i=0; i < nc; i++) newsol[i] = sol[i];
 for(i=0, j=pos, l=(pos+k-1)%nc; i<n;i++,j=(j+1)%nc) {
		newsol[j] = sol[l];
		newsol[l] = sol[j];
    	if(l > 0) l--;
    	else l=nc-1;
  	}

 int [][] r = routes_from_order(sol);
 int [][] r1 = routes_from_order (newsol);
 //improve_routes_2opt(newsol);

 if (total_cost(r1) < total_cost(r))
 	return newsol;
 else return sol;
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
	try{

  VRP v = new VRP(args[0]+".vrp", args[0]+".cit");

/*
	for(int i = 0; i < 1000; i++)
	{

  	int [] rsol1 = MatUtils.give_rand_perm (v.getNumberClients()+v.getNumberVehicles()-1);    
  	int [][] routes = v.get_routes(rsol1);
  	v.print_routes (routes);
  	v.rand_string_exchange(routes, 2);
  	v.print_routes (routes);
	}
  */
   v.iterated_lo(STR_EXCHANGE, FIRST);
  	} catch (Exception e)
	{ e.printStackTrace(); }

}

}
