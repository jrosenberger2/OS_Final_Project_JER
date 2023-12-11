/**
 * TicTacToe server is the server side of a networked tictactoe game
 * @author Jared Rosenberger
 * @version 12/11/23
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TicTacToeServer {
	
	public static void main(String[] args) {
		ServerSocket server;
		try {
			server = new ServerSocket(8080);
			System.out.println("Listing for connection on port 8080");
			while(!server.isClosed()) {
				try(Socket connection = server.accept()){
					System.out.println("New Client connected.");
					TicTacToe game = new TicTacToe(connection);
					game.run();
		        }catch(IOException e) {}
			}//end while loop
			System.out.println("Server is closed");
		} catch (IOException e) {}
	}//end main
}//end TicTacToeServer