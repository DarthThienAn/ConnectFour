package com.connectfour;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Start extends Activity implements OnGestureListener {

	Connect connect = new Connect();

	ConnectFourView myConnectFour;
	
//	private int testCount = 0;
	
	TextView text;
	
	private Button serverButton;
	private Button clientButton;
	private Button connectButton;
	private boolean serverSide = false;
	private boolean clientSide = false;
	private EditText serverIp;
	private boolean connected = false;

	private boolean gameStart = false;
	private boolean ready = false;
	private boolean ready2 = false;

	private boolean gameover = false;
	
	private Button checkButton;
	

	private GestureDetector gestureScanner;
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return gestureScanner.onTouchEvent(me);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		return;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		return;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		
		if(myConnectFour != null)
		{
			if (myConnectFour.getTurn())
			{
				myConnectFour.update();
				if(myConnectFour.touch(e.getX(), e.getY()))
				{
					myConnectFour.setText("Press check to see if opponent has gone yet.");
					
					if (serverSide)
					{
						connect.sendMsgFromServer("" + myConnectFour.getLastX());
						connect.sendMsgFromServer("" + myConnectFour.getLastY());
					}
					if (clientSide)
					{
						connect.sendMsgFromClient("" + myConnectFour.getLastX());
						connect.sendMsgFromClient("" + myConnectFour.getLastY());
					}
					
					if(gameover = myConnectFour.getGameOver())
					{
						if (myConnectFour.getWinner() == myConnectFour.RED)
							myConnectFour.setText("Game Over: You win");
						else if (myConnectFour.getWinner() == myConnectFour.YELLOW)
							myConnectFour.setText("Game Over: You lose");
					}					
				}				
			}
		}
		
		return true;
	}
	
	public void test(String input) {
		if (gameStart)
		{
			if (input != null)
			{
				if (serverSide)
				{
					int x = Integer.parseInt(input);
					int y = Integer.parseInt(connect.getMsgToServer());
					
					if (!myConnectFour.checkTile(x, y))
					{
						myConnectFour.setTileMarked(3, x, y);
						myConnectFour.setText("Your move.");
					}
				}
				else if (clientSide)
				{
					int x = Integer.parseInt(input);
					int y = Integer.parseInt(connect.getMsgToClient());
					
					if (!myConnectFour.checkTile(x, y))
					{
						myConnectFour.setTileMarked(3, x, y);
						myConnectFour.setText("Your move.");
					}
				}
			}
		}
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gestureScanner = new GestureDetector(this);
		
		// remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.start);
		text = (TextView) findViewById(R.id.start_text);

		serverButton = (Button) findViewById(R.id.server);
		serverButton.setOnClickListener(serverClick);
		clientButton = (Button) findViewById(R.id.client);
		clientButton.setOnClickListener(clientClick);
	}
	
	private OnClickListener serverClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			setContentView(R.layout.server);

			text = (TextView) findViewById(R.id.server_status);
			serverButton = (Button) findViewById(R.id.server_start);
			serverButton.setOnClickListener(check);
			
			connect.initServer();
			text.setText("Server initialized, IP: " + connect.getServerIP());
			
			serverSide = true;
		}
	};

	private OnClickListener clientClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
//			clientSide = true;
			setContentView(R.layout.client);

			text = (TextView) findViewById(R.id.client_status);
			serverIp = (EditText) findViewById(R.id.server_ip);
			connectButton = (Button) findViewById(R.id.connect_phones);
			connectButton.setOnClickListener(connectClick);

			text.setText("Enter an IP Address to connect to");
		}
	};	
	

	private OnClickListener connectClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!connected) {
				text.setText("Attempting to connect...");

				String serverIpAddress = serverIp.getText().toString();
				if (!serverIpAddress.equals(""))
				{
					if(connect.initClient(serverIpAddress))
					{
						connected = true;
						setContentView(R.layout.client2);

						clientSide = true;
						
						clientButton = (Button) findViewById(R.id.client_start);
						clientButton.setOnClickListener(check);
					}
				}
				else
					text.setText("Client Initialization failed");
			}
		}
	};
	
	private OnClickListener check = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!ready)
			{
				ready = true;
				
				if (serverSide)
					connect.sendMsgFromServer("ready");
				else if (clientSide)
					connect.sendMsgFromClient("ready");
			}
			
			if (!ready2)
			{
				String input = "";
				
				if (serverSide)
					input = connect.getMsgToServer();
				else if (clientSide)
					input = connect.getMsgToClient();
				
				if (input == null)
					Log.d("Error", "Null");
				else if (input.equals("ready"))
					ready2 = true;
			}
			
			if (ready && ready2 && !gameStart)
			{
				gameStart = true;

				setContentView(R.layout.game);

				myConnectFour = (ConnectFourView) (findViewById(R.id.game));
				myConnectFour.setTextView((TextView) findViewById(R.id.status));
				checkButton = (Button) findViewById(R.id.check_button);
				checkButton.setOnClickListener(click);
				
				if(serverSide)
				{
					myConnectFour.setTurn(true);
					myConnectFour.setText("Game Start. Your move.");
				}
				else
				{
					myConnectFour.setTurn(false);
					myConnectFour.setText("Game Start. Press check to see if opponent has gone.");
				}
			}
		}
	};

	private OnClickListener click = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			checker();
		}
	};
	
	private void checker()
	{
		if (gameStart && !myConnectFour.getTurn() && !gameover) {
			text.setText("Waiting for move...");
			
			String input = "";

				if (serverSide && connect != null)
				{
					if((input = connect.getMsgToServer()) != null)
					{
						test(input);
						myConnectFour.setTurn(true);
						myConnectFour.setText("Your move.");
						if(gameover = myConnectFour.getGameOver())
						{
							if (myConnectFour.getWinner() == myConnectFour.RED)
								myConnectFour.setText("Game Over: You win");
							else if (myConnectFour.getWinner() == myConnectFour.YELLOW)
								myConnectFour.setText("Game Over: You lose");
						}
					}
				}
				else if (clientSide && connect != null)
				{
					if((input = connect.getMsgToClient()) != null)
					{
						test(input);
						myConnectFour.setTurn(true);
						myConnectFour.setText("Your move.");
						if(gameover = myConnectFour.getGameOver())
						{
							if (myConnectFour.getWinner() == myConnectFour.RED)
								myConnectFour.setText("Game Over: You win");
							else if (myConnectFour.getWinner() == myConnectFour.YELLOW)
								myConnectFour.setText("Game Over: You lose");
						}
					}
				}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if (connect != null)
			connect.close();
		System.exit(0);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (connect != null)
			connect.close();
		System.exit(0);
	}

}
