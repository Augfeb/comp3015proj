import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
	ServerSocket srvSocket;
	
	public EchoServer(int port) throws IOException {
		srvSocket = new ServerSocket(port);
		
		byte[] buffer = new byte[1024];
		
		while(true) {
			System.out.printf("Listening at port %d...\n", port);
			
			Socket clientSocket = srvSocket.accept();
			
			System.out.printf("Established a connection to host %s:%d\n\n", clientSocket.getInetAddress(), clientSocket.getPort());
			
			
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			
			int len = in.readInt();
			in.read(buffer, 0, len);
			
			String str = "ECHO: " + new String(buffer, 0, len);
			
			out.writeInt(str.length());
			out.write(str.getBytes(), 0, str.length());
			
			clientSocket.close();
		}
	}
	
	public static void main(String[] args) throws IOException {
		new EchoServer(9999);
	}

}