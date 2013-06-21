package cvrp;

import java.io.BufferedReader;
import java.io.FileReader;

import jecoli.test.tsp.libtsp.EucTsp;
import jecoli.test.tsp.libtsp.Tsp;

public class Cvrp {

  int nc; // number of clients
  int nv; // number of vehicles

  Tsp tspp; // tsp instance with cost matrix
            // dimension of matrix = nc + 1
            // the deposit is node 0, clients are nodes 1 to nc

  double capacity; // capacity of the vehicles
  double[] demands; // demands of the nc clients

  /**
   * default constructor
   */
  public Cvrp()
  {

  }

  public Cvrp(int c, int v, Tsp t, double cap, double[] dem)
  {
    this.nc = c;
    this.nv = v;
    this.tspp = t;
    this.capacity = cap;
    this.demands = dem;
  }

  public Cvrp(String vrpfile, String tspfile) throws Exception
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

  /**
   * Get the problem's number of vehicles
   */
  int getNumberVehicles()
  {
    return nv;
  }


  /**
   * Sets the problem's number of vehicles
   */
  void setNumberVehicles(int nv)
  {
    this.nv = nv;
  }

  /**
   * Get the problem's number of clients
   */
  int getNumberClients()
  {
    return nc;
  }

  /**
   * Sets the problem's number of clients
   */
  void setNumberClients(int nc)
  {
    this.nc = nc;
  }

  /**
   * Gets the cost matrix
   */
  Tsp getTsp()
  {
    return tspp;
  }

  /**
   * Sets the cost matrix
   */
  void setTsp(Tsp t)
  {
    tspp = t;
  }

  /**
   * Get the problem's vehicle capacity
   */
  double getCapacity()
  {
    return capacity;
  }

  /**
   * Sets the problem's vehicle capacity
   */
  void setCapacity(double cap)
  {
    capacity = cap;
  }

  /**
   * Get the problem's demands
   */
  double[] getDemands()
  {
    return demands;
  }

  /**
   * Sets the problem's demands
   */
  void setDemands(double[] dem)
  {
    demands = dem;
  }

  /**
   * Get the demand of a given client
   */
  double getDemand(int c)
  {
   return demands[c];
  }

  /**
   * Sets the demand of a given client
   */
  void setDemand(int c, double d)
  {
   demands[c] = d;
  }

  /**
   * Function that converts a solution between array representation to matrix representation
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

  /**
   * Function that converts a solution between matrix representation and array representation
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

  /** Function that calculates the cost of a given solution to a VRP instance.
   * @param sol the solution represented in array representation
   * @returns the total cost
   */
  double total_cost(int[] sol)
  {
    int[][] r = get_routes(sol);
    return total_cost(r);
  }

  /**
   * Function that calculates the cost of a given solution in matrix representation
   */
  double total_cost(int[][] routes)
  {
    // it doesn't check if it is a valid solution in terms of capacities
    // if it needs to check capacity, then if it surpasses capacity, return -1 (not a valid solution)
    double cost = 0;

    for(int v=0; v<nv; v++) // for each vehicle
    {
      //if(routes[v][0] != -1)
      cost += tspp.cost(routes[v]);
    }
    return cost;
  }
}
