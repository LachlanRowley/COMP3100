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

		dout.write(("GETS All\n").getBytes());
		command = br.readLine();
		System.out.println(command);
		
		
		String serverInfo = command.toString();
		String[] serverInfoArray = serverInfo.split(" ");
		
		int numServers = Integer.valueOf(serverInfoArray[1]);
		
		String[] servers = new String[numServers];
		for(int i = 0; i < numServers; i++) {
			dout.write(("OK\n").getBytes());
			command = br.readLine();
			servers[i] = command.toString();
			System.out.println(command);
		}

		System.out.println(findLargestType(servers)[0]);
		

		dout.write(("QUIT\n").getBytes());
		dout.flush();
		dout.close();
		br.close();  
		s.close();  
    	}catch(Exception e){System.out.println(e);}
    }
    
    
    static String[] findLargestType(String[] s) {
    	int serverTypeCount = 0;
		int largestServerSize = 0;
    	String serverType = "";
    	for(String string : s) {
    		if (getServerSize(string) > largestServerSize) {
    			largestServerSize = getServerSize(string);
    			if(serverType == getServerType(string)) {
    				serverTypeCount += 1;
    			}
    			else {
    				serverTypeCount = 1;
    			}
    		}
    	}
    	String[] serverArray = new String[serverTypeCount];
    	for(String server : s) {
    		int count = 0;
    		String tempServerType = getServerType(server);
    		if(tempServerType == serverType) {
    			serverArray[count] = server;
    			count++;
	    		System.out.println(tempServerType);
    		}
    	}
		return serverArray;
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
