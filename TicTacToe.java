/**
 * TicTacToe.java is for creating a TicTacToe board
 * @author Jared Rosenberger
 * @version 12/11/23
 * TicTacToe.java
 */
import java.net.Socket;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

public class TicTacToe implements Runnable{
	
    private Character[] board;
    private Boolean gameWon;
    private Character winner;
    
    private Socket connection;
    private OutputStream out;
    private DataOutputStream dataOut;
    private InputStream in;
    private DataInputStream dataIn;
    
    private final Character PLAYER_X = 'X';
    private final Character PLAYER_O = 'O';
    private int currentPlayer;
    
	public TicTacToe(Socket sock) {
        board = new Character[9];
        for(int i=0; i<board.length; i++) {
        	board[i] = '-';
        }
        gameWon = false;
        winner = 'q';
        currentPlayer = 0;
        connection = sock;
        try {
        	in = connection.getInputStream();
        	dataIn = new DataInputStream(in);
        	out = connection.getOutputStream();
        	dataOut = new DataOutputStream(out);
        }catch(IOException e) {}
	}//end of constructor
	
	/**
	 * checkWin checks if either player has 3 pieces in a row
	 * @return true for a winning move/tie or false for no winning move/tie
	 */
	private synchronized Boolean checkWin() {
		//Horizontal Wins
		if(board[0] != '-' && board[0] == board[1] && board[0] == board[2]) {
			winner = board[0];
			gameWon = true;
		}
		else if(board[3] != '-' && board[3] == board[4] && board[3] == board[5]) {
			winner = board[3];
			gameWon = true;
		}
		else if(board[6] != '-' && board[6] == board[7] && board[6] == board[8]) {
			winner = board[6];
			gameWon = true;
		}
		//Vertical Wins
		else if(board[0] != '-' && board[0] == board[3] && board[0] == board[6]) {
			winner = board[0];
			gameWon = true;
		}
		else if(board[1] != '-' && board[1] == board[4] && board[1] == board[7]) {
			winner = board[1];
			gameWon = true;
		}
		else if(board[2] != '-' && board[2] == board[5] && board[2] == board[8]) {
			winner = board[2];
			gameWon = true;
		}
		//Diagonal Wins
		else if(board[0] != '-' && board [0] == board[4] && board[0] == board[8]) {
			winner = board[0];
			gameWon = true;
		}
		else if(board[2] != '-' && board[2] == board[4] && board[2] == board[6]) {
			winner = board[2];
			gameWon = true;
		}
		else if(boardFull()) {
			winner = 't';
			gameWon = true;
		}
		return gameWon;
	}//end checkWin
	
	/**
	 * boardFull is used to see if a tie condition is present
	 * @return true if every space is full, false otherwise
	 */
	private synchronized Boolean boardFull() {
		Boolean flag = true;
		for(Character i : board) {
			if(i == '-')
				flag = false;
		}
		return flag;
	}//end boardFull
	
	/**
	 * sendBoard encodes the spaces of the board to a string and sends it to the player
	 */
	private synchronized void sendBoard() {
		String messageOut="";
		try {
			dataOut.flush();
			messageOut = messageOut + board[0] + board[1] + board[2] 
					+ board[3] + board[4] + board[5]
					+ board[6] + board[7] + board[8];
			dataOut.writeUTF(messageOut);
			dataOut.flush();
		}catch(IOException e) {}
	}//end sendBoard
	
	/**
	 * move attempts to put a new piece on the board
	 * @param index is the val 1-9 that the player inputs
	 * @param move is the current player's piece
	 * @return true if the move is legal, false otherwise
	 */
	public synchronized Boolean move(int index, Character move) {
		int i = index-1;
		if(i>-1 && i<9 && board[i] == '-') {
			board[i] = move;
			return true;
		}
		else
			return false;
	}//end move
	
	/**
	 * run handles all the input and output between the server and the player
	 */
	public void run() {
		Character player = null;
		String input = "";
		int index = -1;
		Boolean playerDone = false;
		//While loop keeps the game running
		while(!gameWon) {
			//Controls whose turn it is
			if(currentPlayer == 0) {
				player = PLAYER_X;
			}
			else {
				player = PLAYER_O;
			}
			//while loop runs each players turn
			while(!playerDone) {
				try {
					dataOut.writeUTF("Player " + player + "'s turn!");
					dataOut.writeUTF("Please input a number 1-9");
					input = dataIn.readUTF();
					try {
						index = Integer.parseInt(input);
					}catch(Exception e) {index = -1;}
					dataOut.flush();
					if(move(index, player)) {
						dataOut.writeUTF("Successful move");
						dataOut.flush();
						checkWin();
						playerDone = true;
					}
					else {
						dataOut.writeUTF("Invalvid Move, try again...");
						dataOut.flush();
					}
					sendBoard();
					dataOut.writeChar(winner);
					dataOut.flush();
				}catch(IOException e) {}
			}
			playerDone = false;
			currentPlayer = (currentPlayer+1)%2;
			/*
			System.out.println("Board:");
			for(int i=0; i<board.length; i++) {
				System.out.print(board[i] +"\t");
				if(i==2 || i==5 || i==8)
					System.out.print("\n");
			}
			System.out.println();
			*/
		}
		try {
			dataOut.flush();
			if(winner == 't') {
				dataOut.writeUTF("It's a tie! better luck next time!");
				//System.out.println("It's a tie! better luck next time!");
			}
			else
				dataOut.writeUTF("Player " + winner + " wins.");
				//System.out.println("Player " + winner + " wins.");
		}catch(IOException e) {}
	}//end run
}//end TicTacToe