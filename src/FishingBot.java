import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;

public class FishingBot implements HotkeyListener, IntellitypeListener {

	public static ArrayList<Event> events = new ArrayList<Event>();	
	public static int FISHING_KEY = KeyEvent.VK_F12;
	public static int BUFF_KEY = KeyEvent.VK_F11;//3
	public static int ROD_KEY = KeyEvent.VK_F9;//4
	public static int SPEAR_KEY = KeyEvent.VK_F10;//5
	public static int COLOR_KEY = KeyEvent.VK_F7;
	public static int STARTER_KEY = KeyEvent.VK_F6;
	public static int WOBBLE_BUFFER = 2;
	public final static int TIMEOUT = 21000;
	public static int X = 700, Y = 250;
	public static int W = 500, H = 270;
	public static int R = 70, G = 30, B = 30;

	BufferedImage b = null;
	static JFrame frame = null;
	Container pane = null;
	boolean fishing = false;
	int step = 0;
	int bobbleX, bobbleY;
	Robot robot = new Robot();
	long timeout, resetTimer = -1, buffTimer = -1;
	JIntellitype jint = null;
	boolean settingHotKeys;
	JTextField txtX = null, txtY = null, txtW = null, txtH = null;
	JTextField txtR = null, txtG = null, txtB = null;
	JTextField txtWobble = null;
	JLabel errorLabel = null;
	JLabel noticeLabel = null;
	JLabel hotkeyLabel = null, hotkeyLabel2 = null;
	JTextArea output = null;

	public class Event {
		public ArrayList<Integer> keys;
		public int interval, sleep;
		public long timer;
		public Event(int k, int i, int d){
			keys = new ArrayList<Integer>();
			keys.add(k);
			interval = i;
			timer = 0;
			sleep = d;
		}
		public Event(ArrayList<Integer> k, int i, int d){
			keys = k;
			interval = i;
			timer = 0;
			sleep = d;
		}
	}

	public static void main(String[] args) {
		try{
			new FishingBot();
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(frame, e);
			System.exit(1);
		}
	}

	public FishingBot() throws Exception {
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add(SPEAR_KEY);
		a.add(SPEAR_KEY);
		a.add(ROD_KEY);
		events.add(new Event(a, 300000, 700));
		events.add(new Event(BUFF_KEY, 600000, 2500));

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		X = (dim.width-W)/2;
		Y = dim.height/2 - H - 50;

		//main window, close it to close program
		frame = new JFrame("Fishing Bot");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.setSize(700,710);
		frame.setLocation(300, 200);
		frame.setVisible(true);
		pane = frame.getContentPane();

		JPanel outsidePanel = new JPanel();
		GridLayout grid3 = new GridLayout(0,2);
		grid3.setHgap(20);
		grid3.setVgap(20);
		outsidePanel.setLayout(grid3);
		frame.add(outsidePanel);

		JPanel showPanel = new JPanel();
		FlowLayout mgr = new FlowLayout(FlowLayout.CENTER, 20, 20);
		showPanel.setLayout(mgr);
		outsidePanel.add(showPanel);

		output = new JTextArea();
		JScrollPane sp = new JScrollPane(output);
		outsidePanel.add(sp);

		JPanel buttonPanel = new JPanel();
		GridLayout grid = new GridLayout(2,2);
		grid.setHgap(20);
		grid.setVgap(20);
		buttonPanel.setLayout(grid);
		showPanel.add(buttonPanel);

		hotkeyLabel = new JLabel("", JLabel.CENTER);
		hotkeyLabel2 = new JLabel("", JLabel.CENTER);
		setHotkeyLabelTexts();
		buttonPanel.add(hotkeyLabel);
		buttonPanel.add(hotkeyLabel2);

		JButton button1 = new JButton("Change Start Key");
		button1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evento1){
				noticeLabel.setText("Type in the new Starting hotkey");
				SetHooks(1);
			}
		});
		JButton button2 = new JButton("Change Fishing Key");
		button2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evento1){
				noticeLabel.setText("Type in the new Fishing hotkey");
				SetHooks(2);
			}
		});

		buttonPanel.add(button1);
		buttonPanel.add(button2);

		noticeLabel = new JLabel();
		ResetLabel(noticeLabel);
		noticeLabel.setForeground(Color.red);
		showPanel.add(noticeLabel);


		JLabel warningLabel = new JLabel(
				"<html>The following values set the scanning<br>" +
				"area for the fishing float. The default<br>" +
				"values are pretty good so don't change<br>" +
		"them unless the bot keeps recasting.</html>");
		showPanel.add(warningLabel);

		JPanel textPanel = new JPanel();
		showPanel.add(textPanel);

		GridLayout experimentLayout = new GridLayout(0,4);
		experimentLayout.setHgap(10);
		experimentLayout.setVgap(5);
		textPanel.setLayout(experimentLayout);

		JLabel label1 = new JLabel("X Position:");
		JLabel label2 = new JLabel("Y Position:");
		JLabel label3 = new JLabel("Width:");
		JLabel label4 = new JLabel("Height:");     

		ActionListener al = new ActionListener(){
			public void actionPerformed(ActionEvent evento1){
				UpdateValues();
			}
		};

		txtX = new JTextField(""+X, 5);
		txtY = new JTextField(""+Y, 5);
		txtW = new JTextField(""+W, 5);
		txtH = new JTextField(""+H, 5);
		txtX.addActionListener(al);
		txtY.addActionListener(al);
		txtW.addActionListener(al);
		txtH.addActionListener(al);

		textPanel.add(label1);
		textPanel.add(txtX);
		textPanel.add(label2);
		textPanel.add(txtY);
		textPanel.add(label3);
		textPanel.add(txtW);
		textPanel.add(label4);
		textPanel.add(txtH);

		JLabel warningLabel2 = new JLabel(
				"<html>The following value sets how much wobble<br>" +
				"room the bot will allow before reeling in.<br>" +
				"Increase it if the bot is clicking early,<br>" +
				"decrease it if the bot isn't clicking at all.<br>" +
		"Range 0-10</html>");
		showPanel.add(warningLabel2);

		txtWobble = new JTextField(""+WOBBLE_BUFFER, 5);
		txtWobble.addActionListener(al);
		showPanel.add(txtWobble);

		JLabel colorLabel = new JLabel(
				"<html>The following values set the scanning<br>" +
				"color for the fishing float. These values<br>" +
				"depend on which map you're fishing in. They<br>" +
		"vary greatly.</html>");
		showPanel.add(colorLabel);

		JPanel colorPanel = new JPanel();
		showPanel.add(colorPanel);

		GridLayout colorLayout = new GridLayout(0,2);
		experimentLayout.setHgap(10);
		experimentLayout.setVgap(5);
		colorPanel.setLayout(colorLayout);

		JLabel labelc1 = new JLabel("Red:");
		JLabel labelc2 = new JLabel("Green:");
		JLabel labelc3 = new JLabel("Blue:");

		ActionListener al2 = new ActionListener(){
			public void actionPerformed(ActionEvent evento1){
				UpdateValues();
			}
		};

		txtR = new JTextField(""+R, 5);
		txtG = new JTextField(""+G, 5);
		txtB = new JTextField(""+B, 5);
		txtR.addActionListener(al2);
		txtG.addActionListener(al2);
		txtB.addActionListener(al2);

		colorPanel.add(labelc1);
		colorPanel.add(txtR);
		colorPanel.add(labelc2);
		colorPanel.add(txtG);
		colorPanel.add(labelc3);
		colorPanel.add(txtB);


		errorLabel = new JLabel("");
		errorLabel.setForeground(Color.red);
		ResetLabel(errorLabel);
		showPanel.add(errorLabel);


		frame.validate();

		jint = JIntellitype.getInstance();
		initJIntellitype();
		RegisterKeys();

		Go();
	}

	public void SetHooks(int t){
		settingHotKeys = true;
		jint.unregisterHotKey(STARTER_KEY);
		for(int i = 1; i < 230; i++){
			if(i != 16 && i != 17 && i != 18){
				jint.registerHotKey(i+t*1000, 0, i);
			}
		}
	}

	public void UpdateValues(){
		ResetLabel(errorLabel);
		try{
			X = Integer.parseInt(txtX.getText());
			Y = Integer.parseInt(txtY.getText());
			W = Integer.parseInt(txtW.getText());
			H = Integer.parseInt(txtH.getText());
			WOBBLE_BUFFER = Integer.parseInt(txtWobble.getText());
			R = Integer.parseInt(txtR.getText());
			G = Integer.parseInt(txtG.getText());
			B = Integer.parseInt(txtB.getText());
			println("Updating values: X=" + X + " Y=" + Y + " W=" + W + " H=" + H + " R=" + R + " G=" + G + " B=" + B);
		}
		catch(NumberFormatException e){
			errorLabel.setText("<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+					
					"Error: Not an integer"+
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html>");
		}
	}

	public void Go() throws Exception {
		while(true){
			switch(step){
			case 1: {
				CastLine();
				Thread.sleep(1000);
				break;
			}
			case 2: {
				FindBobble();
				timeout = System.currentTimeMillis();
				break;
			}
			case 3: {
				Thread.sleep(200);
				WaitforBobble();
				break;
			}
			case 4: {
				ClickOnBobble();
				Thread.sleep(2000);
				break;
			}
			case 5: {
				Reset();
				Thread.sleep(700);
				break;
			}
			default: {
				Thread.sleep(100);
			}
			}
		}
	}

	//Presses the fishing key
	public void CastLine() throws Exception{
		for(int i = 0; i < events.size(); i++){
			Event e = events.get(i);
			if(System.currentTimeMillis() > e.timer + e.interval){
				for(int j = 0; j < e.keys.size(); j++){
					int k = e.keys.get(j);
					robot.keyPress(k);
					HumanDelay();
					robot.keyRelease(k);
					println(k + " pressed.");
					Thread.sleep(e.sleep);
				}
				e.timer = System.currentTimeMillis();
			}
			println(""+ (e.timer + e.interval - System.currentTimeMillis()));
		}
		robot.keyPress(FISHING_KEY);
		HumanDelay();
		robot.keyRelease(FISHING_KEY);
		println("Fishing key: " + KeyEvent.getKeyText(FISHING_KEY) + " pressed.");
		step++;
	}

	//takes screenshot of middle of screen and looks for bobble
	//sets bobble area
	//find center of bobble
	//move mouse to center
	public void FindBobble() throws Exception{
		println("Taking Sceen Capture");

		Rectangle r = new Rectangle(X,Y,W,H);
		b = robot.createScreenCapture(r);

		boolean found = false;
		for(int j = b.getHeight()-1; j >= 0 ; j-=3){
			for(int i = 0; i < b.getWidth(); i+=3){
				int rgb = b.getRGB(i, j);
				if(Reddish(rgb)){
					println("Found at (" + i + "," + j + ")");
					/*for(int a = 0; a < 2; a++){
						for(int c = 0; c < 2; c++){
							b.setRGB(i+a, j+c, 0xFF);
						}
					}*/
					bobbleX = X+i;
					bobbleY = Y+j;
					found = true;
				}
				else{
					b.setRGB(i, j, 0);
				}
			}
		}
		if(found){
			//Map red area
			boolean[][] map = new boolean[10][10];
			int[] xarray = new int[10];
			int[] yarray = new int[10];
			r = new Rectangle(bobbleX-5,bobbleY-5,10,10);
			BufferedImage b2 = robot.createScreenCapture(r);
			for(int i = 0; i < 10; i++){
				for(int j = 0; j < 10; j++){
					int rgb = b2.getRGB(i, j);
					if(Reddish(rgb)){
						map[i][j] = true;
						xarray[i]++;
						yarray[j]++;
						//print("Y");
					}
					else{
						//print("N");
					}
				}
				//println();
			}

			//find center
			int minx = -1, maxx = -1;
			int miny = -1, maxy = -1;
			for(int i = 0; i < 10; i++){
				if(xarray[i] > 0){
					if(minx < 0){
						minx = i;
					}
					maxx = i;
				}
			}
			for(int i = 0; i < 10; i++){
				if(yarray[i] > 0){
					if(miny < 0){
						miny = i;
					}
					maxy = i;
				}
			}
			bobbleX = bobbleX + (maxx + minx)/2 - 5;
			bobbleY = bobbleY + (maxy + miny)/2 - 5;
			robot.mouseMove(bobbleX, bobbleY);
		}
		/*ColorPanel panel = new ColorPanel(b);
		pane.removeAll();
		pane.add(panel);
		frame.validate();*/

		step++;
		if(!found){
			step = 5;
			println("Could not find bobble");
		}
	}

	public void WaitforBobble(){
		boolean b = false;
		for(int i = -WOBBLE_BUFFER; i <= WOBBLE_BUFFER; i++){
			Color c = robot.getPixelColor(bobbleX, bobbleY+i);
			b = b || Reddish(c);
		}
		if(b){
			//println("Waiting for bobble");
			if(System.currentTimeMillis() > timeout + TIMEOUT){
				step = 1;
			}
		}
		else{
			println("Bobble moved");
			step++;
		}
	}

	public void ClickOnBobble() throws Exception{
		robot.mouseMove(bobbleX, bobbleY);
		HumanDelay();
		robot.mousePress(InputEvent.BUTTON1_MASK);
		HumanDelay();
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
		step = 1;
	}

	public void Reset() throws InterruptedException{
		println("Reseting");
		/*if(resetTimer < 0){
			resetTimer = System.currentTimeMillis();
		}
		if(System.currentTimeMillis() > resetTimer + TIMEOUT){
			resetTimer = -1;
			step = 1;
		}*/
		robot.keyPress(KeyEvent.VK_ESCAPE);
		HumanDelay();
		robot.keyRelease(KeyEvent.VK_ESCAPE);
		step = 1;
	}

	@Override
	public void onHotKey(int identifier) {
		println(identifier + " pressed; ");
		if(settingHotKeys){
			int modifier = identifier / 1000;
			identifier = identifier % 1000;

			if(modifier == 1){
				STARTER_KEY = identifier;
			}
			else{
				FISHING_KEY = identifier;
			}
			setHotkeyLabelTexts();
			ResetLabel(noticeLabel);
			for(int i = 1; i < 230; i++){
				if(i != 16 && i != 17 && i != 18){
					jint.unregisterHotKey(i+modifier*1000);
				}
			}
			RegisterKeys();
			settingHotKeys = false;
		}
		else if(identifier == STARTER_KEY){
			if(step < 1){
				step = 1;
				println("Starting");
			}
			else{
				step = -10;
				println("Stoping");
			}
		}
		else if(identifier == COLOR_KEY){
			Point p = MouseInfo.getPointerInfo().getLocation();
			Color c = robot.getPixelColor(p.x, p.y);
			println("Color at mouse location is ("+ c.getRed() + "," + c.getGreen() + "," + c.getBlue()+")");
			if(Reddish(c)){
				println("Passes the requirements of ("+ R + "," + G + "," + B+")");
			}
			else {
				println("Does not pass the requirements of ("+ R + "," + G + "," + B+")");
			}
			println();
		}
	}

	public void RegisterKeys(){
		jint.registerHotKey(STARTER_KEY, 0, STARTER_KEY);
		jint.registerHotKey(COLOR_KEY, 0, COLOR_KEY);
	}

	public void setHotkeyLabelTexts(){
		hotkeyLabel.setText("Start Key: " + KeyEvent.getKeyText(STARTER_KEY));
		hotkeyLabel2.setText("Fishing Key: " + KeyEvent.getKeyText(FISHING_KEY));
	}


	@Override
	public void onIntellitype(int command) {
	}

	private void initJIntellitype() throws Exception{
		// initialize JIntellitype with the frame so all windows commands can
		// be attached to this window
		JIntellitype.getInstance().addHotKeyListener(this);
		JIntellitype.getInstance().addIntellitypeListener(this);
	}

	public void HumanDelay() throws InterruptedException{
		Thread.sleep((long)(5+15*Math.random()));
	}

	public boolean Reddish(Color c){
		int r = c.getRed();
		int g = c.getGreen();
		int b = c.getBlue();
		return Reddish(r,g,b);
	}

	public boolean Reddish(int c){
		int r = (c >> 16) & 0xFF;
		int g = (c >>  8) & 0xFF;
		int b = (c      ) & 0xFF;
		return Reddish(r,g,b);
	}

	public boolean Reddish(int r, int g, int b){
		return r > R && r > g + G && r > b + B;
	}

	public void ResetLabel(JLabel l){
		l.setText("<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
		"</html>");
	}

	public void print(String str){
		output.append(str);
	}

	public void println(String str){
		output.append(str + "\n");
	}

	public void println(){
		output.append("\n");
	}
}

/*class ColorPanel extends JPanel{
	BufferedImage theCat;
	public ColorPanel(BufferedImage image){
	theCat = image;
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(theCat, null, 50,50);
	}
}*/
