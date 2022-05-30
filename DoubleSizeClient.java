import java.io.*;  
import java.net.*;

/* PSEDUO-CODE for Algorithm
1. See if there is available server to hold job (smallest??)
2. Otherwise boot up server 2* core requirement (or largest)



*/


public class DoubleSizeClient {
    public static void main(String[] args) {  
    try{   

		//Setup Code   
		Socket s=new Socket("localhost",50000);  
		DataOutputStream dout=new DataOutputStream(s.getOutputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		String command = "";  
		dout.write(("HELO\n").getBytes());  
		command = br.readLine();
		System.out.println(command);

		String username = System.getProperty("user.name");
		dout.write(("AUTH " + username +  "\n").getBytes());
		command = br.readLine();
		System.out.println(command);


		dout.write(("REDY\n").getBytes());
		command = br.readLine();
		String j = command.toString();
		String[] job = j.split(" ");
		System.out.println(command);
		//

		int largestServerCores = 0;

		if(job[0].equals("JOBN")) {
			int coresRequired = getCoresRequired(j);
			dout.write(("GETS Capable " + coresRequired + " " + getRamRequired(j) + " " + getMemoryRequired(j) + "\n").getBytes());
			command = br.readLine();
			String serverInfo = command.toString();
			String[] serverInfoArray = serverInfo.split(" ");		
			int numServers = Integer.valueOf(serverInfoArray[1]);
			dout.write(("OK\n").getBytes());
			//Gets the first capable server
			String server = br.readLine();
			boolean doubled = false;
			for(int i = 1; i < numServers; i++) {
				command = br.readLine();
				if(!doubled && getServerSize(command) >= coresRequired * 2) {
					doubled = true;
					server = command;
				}
				if(i + 1 == numServers) {
					largestServerCores = getServerSize(command);
					System.out.println(largestServerCores);
				}
			}
			dout.write(("OK\n").getBytes());
			
			
			String toSend  = "SCHD " + getJobID(j) + " " + getServerType(server) + " " + getServerID(server) + "\n";
			dout.write(toSend.getBytes());
			while(!(command = br.readLine()).equals("OK")) {}
		}
		dout.write(("REDY\n").getBytes());

		while(!(command = br.readLine()).equals("NONE")) {
			j = command.toString();
			job = j.split(" ");
			if(job[0].equals("JOBN")) {
				int coresRequired = getCoresRequired(j);
				dout.write(("GETS Capable " + coresRequired + " " + getRamRequired(j) + " " + getMemoryRequired(j) + "\n").getBytes());
				command = br.readLine();
				String serverInfo = command.toString();
				String[] serverInfoArray = serverInfo.split(" ");		
				int numServers = Integer.valueOf(serverInfoArray[1]);
				dout.write(("OK\n").getBytes());
				//Gets the first capable server
				String server = br.readLine();
				server = getFirstCapableServer(server, numServers, coresRequired, br, dout);
				//dout.write(("OK\n").getBytes());
				
				String toSend  = "SCHD " + getJobID(j) + " " + getServerType(server) + " " + getServerID(server) + "\n";
				dout.write(toSend.getBytes());
				while(!(command = br.readLine()).equals("OK")) {}
			}
			dout.write(("REDY\n").getBytes());
		}
		
		dout.write(("QUIT\n").getBytes());
		dout.flush();
		dout.close();
		br.close();  
		s.close();  
    	}catch(Exception e){System.out.println(e);}
    }
    
    
    static String findLargestType(String[] s) {
	int largestServerSize = 0;
    	String serverType = "";
    	for(String server : s) {
    		if (getServerSize(server) > largestServerSize) {
    			serverType = getServerType(server);
				largestServerSize = getServerSize(server);

    		}
    	}
	return serverType;
    }
    
    static int largestServerCount(String[] servers, String serverType) {
    	int count = 0;
    	for(String server : servers) {
    		if(serverType.equals(getServerType(server))) {
    			count++;
    		}
    	}
    	return count;
    }
    
    static int getServerSize(String server) {
    	String[] serverInfo = server.split(" ");
    	return Integer.valueOf(serverInfo[4]);
    }
    static String getServerType(String server) {
    	String[] serverInfo = server.split(" ");
    	return serverInfo[0];
    }

    static String getServerID(String server) {
    	String[] serverInfo = server.split(" ");
    	return serverInfo[1];
    }

    static String getServerStatus(String server) {
    	String[] serverInfo = server.split(" ");
    	return serverInfo[2];
    }

	static int getWaitingJobCount(String server) {
    	String[] serverInfo = server.split(" ");
    	return Integer.valueOf(serverInfo[7]);
	}



    static int getCoresRequired(String job) {
	String[] jobInfo = job.split(" ");
	return Integer.valueOf(jobInfo[4]);
    }

    static int getRamRequired(String job) {
	String[] jobInfo = job.split(" ");
	return Integer.valueOf(jobInfo[5]);
    }


    static int getMemoryRequired(String job) {
	String[] jobInfo = job.split(" ");
	return Integer.valueOf(jobInfo[6]);
    }
    
    static int getJobID(String job) {
	String[] jobInfo = job.split(" ");
	return Integer.valueOf(jobInfo[2]);
    }


	//Returns the first capable server that is able to run the job immediately.
	//Otherwise will attempt to find and start and server twice the necessary size.
	//If the are no available servers, will queue the job on the first server with the lowest wait time
	static String getFirstCapableServer(String server, int numServers, int coresRequired, BufferedReader br, DataOutputStream dout) {
		try{
			int lowestWaitingTime = 9999999;
			int lowestWaitingCount = 999;
			String serverPriority1 = " ";
			String serverPriority2 = "";
			String[] serverList = new String[numServers];

			serverList[0] = server;
			//Checks if the first server is not capable of immediately running the job
			if(getServerStatus(server).equals("inactive") || getServerSize(server) < coresRequired) {
				int largestServerSize = getServerSize(server);
				//Iterates through every server
				for(int i = 1; i < numServers; i++) {
					String command = br.readLine();
					serverList[i] = command;
					if(getServerSize(command) >= largestServerSize) {
						largestServerSize = getServerSize(command);
					}
					else {
						continue;
					}
					server = command;

					//Stops the loop if it finds a server that is immediately able to run the job 
					if (!getServerStatus(command).equals("inactive") && getServerSize(server) >= coresRequired) {
						dout.write(("OK\n").getBytes());
						return server;
					}
					//Stops the loop if if finds a server with twice the necessary number of cores available
					if(getServerSize(command) == coresRequired * 2) {
						dout.write(("OK\n").getBytes());
						return server;
					}
				}

				if(serverPriority1 == "") {
					dout.write(("OK\n").getBytes());
					br.readLine();
					for(int i = 0; i < numServers; i++) {
						//Gets the estimated wait time of the current server
						int waitingJobs = getWaitingJobCount(serverList[i]);
						//Checks if the current server has a lower waiting time than the current lowest
						//If so, replaces reference to that server with the current
						if(waitingJobs < lowestWaitingCount) {
							lowestWaitingCount = waitingJobs;
							dout.write(("EJWT " + getServerType(serverList[i]) + " " + (getServerID(serverList[i])) + "\n").getBytes());
							String command = br.readLine();
							lowestWaitingTime = Integer.valueOf(command);
							serverPriority2 = serverList[i];
						}
						else if(waitingJobs == lowestWaitingCount) {
							dout.write(("EJWT " + getServerType(serverList[i]) + " " + (getServerID(serverList[i])) + "\n").getBytes());
							String command = br.readLine();
							//Checks if the current server has a lower waiting time than the current lowest
							//If so, replaces reference to that server with the current
							if(Integer.valueOf(command) < lowestWaitingTime) {
								lowestWaitingTime = Integer.valueOf(command);
								serverPriority2 = serverList[i];
							}
						}
					}
					if(serverPriority2 != "") {
						server = serverPriority2;
					}
					return server;
				}
			}
			dout.write(("OK\n").getBytes());
		}
		catch(Exception e){System.out.println(e);}
		return server;
	}

}  
