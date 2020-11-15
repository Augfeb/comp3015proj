import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient {
	Socket clientSocket;
	
	public EchoClient() throws IOException {
		Scanner scanner = new Scanner(System.in);
		
		System.out.print("Please input the IP address of Echo Server: ");
		String server = scanner.nextLine();
		
		System.out.print("Please input the port number of Echo Server: ");
		int port = Integer.parseInt(scanner.nextLine());

		clientSocket = new Socket(server, port);
		
		DataInputStream in = new DataInputStream(clientSocket.getInputStream());
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		
		byte[] buffer = new byte[1024];
		
		System.out.print("Please input username and password: ");
		String str = scanner.nextLine();
		
		out.writeInt(str.length());
		out.write(str.getBytes(), 0, str.length());
		
		int len = in.readInt();
		
		in.read(buffer, 0, len);
		
		System.out.println(new String(buffer, 0, len));
		
		 String reMsg = (new String(buffer, 0, len));
		 
		 if(reMsg.equals("Wrong password...") || reMsg.equals("User not found...")) {
			 System.exit(-1);
		 }
		 
		 System.out.println("hello");
	
		
		clientSocket.close();
		scanner.close();
	}

	public static void main(String[] args) throws IOException {
		new EchoClient();
	}

}