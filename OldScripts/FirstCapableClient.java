import java.io.*;  
import java.net.*;  
public class FirstCapableClient {
    public static void main(String[] args) {  
    try{      
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

		
		if(job[0].equals("JOBN")) {
			System.out.println("GETS Capable " + getCoresRequired(j) + " " + getRamRequired(j) + " " + getMemoryRequired(j) + "\n");
			dout.write(("GETS Capable " + getCoresRequired(j) + " " + getRamRequired(j) + " " + getMemoryRequired(j) + "\n").getBytes());
			command = br.readLine();
			String serverInfo = command.toString();
			String[] serverInfoArray = serverInfo.split(" ");		
			int numServers = Integer.valueOf(serverInfoArray[1]);
			dout.write(("OK\n").getBytes());
			String server = br.readLine();
			for(int i = 1; i < numServers; i++) {
				command = br.readLine();
			}
			dout.write(("OK\n").getBytes());
			
					
			String toSend  = "SCHD " + getJobID(j) + " " + getServerType(server) + " " + 0 + "\n";
			dout.write(toSend.getBytes());
			while(!(command = br.readLine()).equals("OK")) {}
		}
		dout.write(("REDY\n").getBytes());

		while(!(command = br.readLine()).equals("NONE")) {
			System.out.println(command);
			j = command.toString();
			job = j.split(" ");
			if(job[0].equals("JOBN")) {
				dout.write(("GETS Capable " + getCoresRequired(j) + " " + getRamRequired(j) + " " + getMemoryRequired(j) + "\n").getBytes());
				command = br.readLine();
				String serverInfo = command.toString();
				String[] serverInfoArray = serverInfo.split(" ");		
				int numServers = Integer.valueOf(serverInfoArray[1]);
				dout.write(("OK\n").getBytes());
				String server = br.readLine();
				for(int i = 1; i < numServers; i++) {
					command = br.readLine();
				}
				dout.write(("OK\n").getBytes());
				
						
				String toSend  = "SCHD " + getJobID(j) + " " + getServerType(server) + " " + 0 + "\n";
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
