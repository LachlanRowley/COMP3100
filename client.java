import java.io.*;  
import java.net.*;  
public class MyClient {  
    public static void main(String[] args) {  
    try{      
	    Socket s=new Socket("localhost",50000);  
	    DataOutputStream dout=new DataOutputStream(s.getOutputStream());
	    //DataInputSteam din = new DataInputSteam(s.getInputStream());
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
   	    dout.write(("QUIT\n").getBytes());
	    dout.flush();
	    dout.close();
	    br.close();  
	    s.close();  
    	}catch(Exception e){System.out.println(e);}
    }
}  
