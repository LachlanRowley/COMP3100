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
		String j = command.toString();
		String[] job = j.split(" ");
		System.out.println(command);

		dout.write(("GETS All\n").getBytes());
		command = br.readLine();
		System.out.println(command);
		
		
		String serverInfo = command.toString();
		String[] serverInfoArray = serverInfo.split(" ");		
		int numServers = Integer.valueOf(serverInfoArray[1]);
		
		String[] servers = new String[numServers];
		for(int i = 0; i < numServers; i++) {
			command = br.readLine();
			servers[i] = command.toString();
			System.out.println(command);
		}
		String largestServerType = findLargestType(servers);
		int largestServerTypeCount = largestServerCount(servers, largestServerType);
	
		
		String toSend  = "SCHD " + job[2] + " " + largestServerType + " " + 0%largestServerTypeCount + "\n";
		dout.write(toSend.getBytes());

		
		dout.write(("REDY\n").getBytes());	
		command = br.readLine();
		System.out.println(command);
		
		dout.write(("REDY\n").getBytes());	
		command = br.readLine();
		System.out.println(command);
		
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
}  
