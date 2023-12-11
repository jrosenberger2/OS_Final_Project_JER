/**
 * Player manages the players of the networked tictactoe game
 * @author Jared Rosenberger
 * @version 12/11/23
 */
import java.net.Socket;
import java.util.Scanner;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.DataInputStream;
class Player {
	private static Character[] board = {'-','-','-','-','-','-','-','-','-'};
	public static void main(String[] args) {
		char winner = 'q';
		Scanner scan = new Scanner(System.in);
		String messageIn = "";
		String messageOut = "";
		try {
			Socket connection = new Socket("localhost", 8080);
			DataOutputStream dataOut = new DataOutputStream(connection.getOutputStream());
			DataInputStream dataIn = new DataInputStream(connection.getInputStream());		
			//While Loop controls when the game ends
			while(winner == 'q') {
				//Player _'s move
				messageIn = dataIn.readUTF();
				System.out.println(messageIn);
				//Please enter a num 1-9
				messageIn = "";
				messageIn = dataIn.readUTF();
				System.out.println(messageIn);
				printBoard();
				//Send client message to server
				messageOut = scan.nextLine();
				dataOut.writeUTF(messageOut);
				dataOut.flush();
				//Good/Bad Move
				messageIn = "";
				messageIn = dataIn.readUTF();
				System.out.println(messageIn);
				//Board
				messageIn = "";
				messageIn = dataIn.readUTF();
				if(messageIn.length() == 9) {
					setBoard(messageIn);
				}
				printBoard();
				System.out.print("\n");
				messageIn = "";
				//testing for game ending condition
				char c = dataIn.readChar();
				if(c == 't' || c== 'X' || c == 'O')
					winner = c;
				messageIn = "";
			}//end while loop
			System.out.println("Game Over!");
			messageIn = dataIn.readUTF();
			System.out.println(messageIn);
			scan.close();	
			connection.close();
		}catch(IOException e) {System.out.println("Connection error");}
	}//end main
	/*
	 * printBoard prints the current game board to the user's console
	 */
	private static void printBoard() {
		System.out.println("Board:");
		for(int i=0; i<board.length; i++) {
			System.out.print(board[i] +"\t");
			if(i==2 || i==5 || i==8)
				System.out.print("\n");
		}
	}//end printBoard
	
	/**
	 * setBoard is used to show new moves on the user's board
	 * @param board is the order of the board passed in from the server
	 */
	private static void setBoard(String newMoves) {
		for(int i=0; i<board.length; i++) {
			board[i] = newMoves.charAt(i);
		}
	}//end setBoard
}//end Player