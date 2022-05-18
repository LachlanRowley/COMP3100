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
				System.out.println(server);
				if(getServerStatus(server).equals("inactive") || getServerSize(server) < coresRequired) {
					int largestServerSize = getServerSize(server);
					for(int i = 1; i < numServers; i++) {
						command = br.readLine();
						if(getServerSize(command) >= largestServerSize) {
							largestServerSize = getServerSize(command);
						}
						else {
							continue;
						}
						server = command;
						if (!getServerStatus(command).equals("inactive") && getServerSize(server) >= coresRequired) {
							break;
						}
						
						if(getServerSize(command) == coresRequired * 2) {
							break;
						}
					}
				}
				dout.write(("OK\n").getBytes());
				
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
}  
