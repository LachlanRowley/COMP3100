import java.io.*;  
import java.net.*;  
public class MyClient {
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
		System.out.println(command);
		int jobCount = 0;
		String j = command.toString();
		String[] job = j.split(" ");



		dout.write(("GETS All\n").getBytes());
		command = br.readLine();
		System.out.println(command);
		
		
		String serverInfo = command.toString();
		String[] serverInfoArray = serverInfo.split(" ");		
		int numServers = Integer.valueOf(serverInfoArray[1]);
		dout.write(("OK\n").getBytes());
		String[] servers = new String[numServers];
		System.out.println(numServers);
		for(int i = 0; i < numServers; i++) {
			command = br.readLine();
			System.out.println(command);
			servers[i] = command.toString();
		}

		dout.write(("OK\n").getBytes());
		command = br.readLine();
		System.out.println(command);

		String largestServerType = findLargestType(servers);
//		System.out.println(largestServerType);
		int largestServerTypeCount = largestServerCount(servers, largestServerType);
//		System.out.println(largestServerTypeCount);

		if(job[0].equals("JOBN")) {
			String toSend  = "SCHD " + job[2] + " " + largestServerType + " " + jobCount%largestServerTypeCount +"\n";
			dout.write(toSend.getBytes());
			jobCount++;
			while(!(command = br.readLine()).equals("OK")) {}
		}
		dout.write(("REDY\n").getBytes());

		while(!(command = br.readLine()).equals("NONE")) {
			System.out.println(command);
			j = command.toString();
			job = j.split(" ");
			if(job[0].equals("JOBN")) {
//				System.out.println(command);
				String toSend  = "SCHD " + job[2] + " " + largestServerType + " " + jobCount%largestServerTypeCount +"\n";
				dout.write(toSend.getBytes());
				jobCount++;
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
}  
