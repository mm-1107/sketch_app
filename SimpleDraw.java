import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

//------------------------- bufferを作って線をかく ---------------------------//
// public classはファイル名と同じものを１つしか定義できない
class DrawPanel extends JPanel{
  BufferedImage bufferImage=null;
	Graphics2D bufferGraphics=null;
  Color c = Color.BLACK;
  Color background_color = Color.WHITE;
  Float currentWidth = 10.0f;
  Image img = Toolkit.getDefaultToolkit().getImage("./image/flower.png");

  public void createBuffer(int width, int height) {
    //バッファ用のImageとGraphicsを用意する
		bufferImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_BGR);
		bufferGraphics=bufferImage.createGraphics(); //getGraphicsと似ているが、戻り値がGraphics2D。
    bufferGraphics.setBackground(background_color);
		bufferGraphics.clearRect(0, 0, width, height); //バッファクリア
    // ここからtest
    // bufferGraphics.setPaint (background_color);
    // bufferGraphics.fillRect ( 0, 0, bufferImage.getWidth(), bufferImage.getHeight() );
    // System.out.println(bufferGraphics);
  }

	public void drawLine(int x1, int y1, int x2, int y2){
    // ここからtest
    // if(null==bufferGraphics) {
		//  	this.createBuffer(this.getWidth(),this.getHeight());  //バッファをまだ作ってなければ作る
    // }
		//太さがcurrentWidth の線を描く．線の両端は丸くする．
		bufferGraphics.setStroke(new BasicStroke(currentWidth ,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));

    // ペンの色を設定
    bufferGraphics.setColor(c);

    // 線を描く
		bufferGraphics.drawLine(x1, y1, x2, y2);

    repaint();//再描画するためpaintComponentを呼び出す。
	}

  // stampの表示
  public void drawImage(int x,int y){
    bufferGraphics.drawImage(img, x, y, this);
    repaint();
  }

  public void paintComponent(Graphics g) {
		super.paintComponent(g);//他に描画するものがあるかもしれないので親を呼んでおく
		if(null!=bufferImage) g.drawImage(bufferImage, 0,0,this);//バッファを表示する
	}

  public void openFile(File file2open){
		BufferedImage pictureImage;
		try {
			pictureImage = ImageIO.read(file2open);
		} catch(Exception e){
			System.out.println("Error: reading file="+file2open.getName());
			return;
		}
		//画像に合わせたサイズでbufferImageとbufferGraphicsを作りなおして画像を読み込む
		//ImageIO.readの戻り値をbufferImageに代入するのでは駄目
		this.createBuffer(pictureImage.getWidth(),pictureImage.getHeight());
		bufferGraphics.drawImage(pictureImage,0,0,this);
		repaint(); //画像を表示するためにpaintComponentを呼ぶ
	}

  public void saveFile(File file2save) {
		try {
			ImageIO.write(bufferImage, "jpg", file2save);
		} catch (Exception e) {
			System.out.println("Error: writing file="+file2save.getName());
			return;
		}
	}
 }

//--------------------------- パレット機能 -----------------------------------//
 class OperationFrame extends JFrame implements ActionListener{
   SimpleDraw mainwindow;
   JButton color_button;
   JButton stamp_button;
   JButton eraser_button;
   JButton pen_button;
   JButton fill_button;
   JPanel panel = new JPanel();

   // 自分を呼び出したclassを覚えておくためのメソッド
   public void setMain(SimpleDraw w){
     mainwindow = w;
     this.init();
   }

   public void init(){
     setBounds(10, 0, 80,600);

     // panel.setLayout(null);

     ImageIcon color_icon = new ImageIcon("./image/palette.png");
     color_button = new JButton(color_icon);
     panel.add(color_button);
     color_button.addActionListener(this);
     // createButton(color_icon,color_button,this);

     ImageIcon eraser_icon = new ImageIcon("./image/eraser.png");
     eraser_button = new JButton(eraser_icon);
     panel.add(eraser_button);
     eraser_button.addActionListener(this);
     // createButton(eraser_icon,eraser_button,this);

     ImageIcon pen_icon = new ImageIcon("./image/pen.png");
     pen_button = new JButton(pen_icon);
     panel.add(pen_button);
     pen_button.addActionListener(this);
     // createButton(pen_icon,pen_button,this);

     ImageIcon fill_icon = new ImageIcon("./image/fill.png");
     fill_button = new JButton(fill_icon);
     panel.add(fill_button);
     fill_button.addActionListener(this);
     // createButton(fill_icon,fill_button,this);

     ImageIcon stamp_icon = new ImageIcon("./image/flower.png");
     stamp_button = new JButton(stamp_icon);
     panel.add(stamp_button);
     stamp_button.addActionListener(this);

     this.getContentPane().add(panel);
     this.setVisible(true);
 		 this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }

   // // buttonを追加するメソッド
   private void createButton(ImageIcon icon,JButton button,ActionListener listener){
     button = new JButton(icon);
     panel.add(button);
     button.addActionListener(listener);
   }

   public void actionPerformed(ActionEvent e){

     if(e.getSource() == stamp_button){
       mainwindow.stamp = 1;
     }

     else{
       mainwindow.stamp = 0;
       if (e.getSource() == color_button){

         JColorChooser colorchooser = new JColorChooser();
         Color color = colorchooser.showDialog(this, "色の選択", Color.white);

         if(color != null){
            mainwindow.ChangeColor(color);
         }
       }
       if(e.getSource() == eraser_button){
         mainwindow.panel.currentWidth = 10.0f;
         // 背景色と同じ色を消しゴムにする
         mainwindow.ChangeColor(mainwindow.panel.bufferGraphics.getBackground());
       }
       if(e.getSource() == pen_button){
         mainwindow.ChangeColor(Color.BLACK);
       }
       if(e.getSource() == fill_button){
         // カラーパレットの表示
         JColorChooser colorchooser = new JColorChooser();
         Color color = colorchooser.showDialog(this, "背景色の選択", Color.white);
         if(color != null){
            mainwindow.panel.bufferGraphics.setBackground(color);
            mainwindow.panel.bufferGraphics.clearRect ( 0, 0,mainwindow.panel.getWidth(), mainwindow.panel.getHeight() );
            mainwindow.panel.repaint();   // これを呼ばないと更新されない
         }
       }
   }

   }

 }

//----------------------------- mainのクラス ------------------------------- //
public class SimpleDraw extends JFrame implements ActionListener,MouseMotionListener , MouseListener{
  int stamp = 0;  // stampかどうかのフラグ(0->pen,1->stamp)
	int lastx, lasty, newx, newy;
  int nowx,nowy;
	DrawPanel panel;
  JButton color_button;
  JFileChooser filechooser;

  // マウスイベントの処理に関連するクラスは、このインタフェースのすべてのメソッドを定義するか、
  // 関連するメソッドだけをオーバーライドして abstract クラス MouseAdapter を拡張

  // MouseListener部分
  public void mouseClicked(MouseEvent e){
  }

  // マウスが押されたところから書き始める
  public void mousePressed(MouseEvent e){
    if(stamp == 1){
      panel.drawImage(e.getX(),e.getY()-50);
    }
    lastx = e.getX();
    lasty = e.getY()-50;
  }
  public void mouseReleased(MouseEvent e){}
  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}

  // MouseMotionListener部分
	public void mouseMoved(MouseEvent arg0) {}

	public void mouseDragged(MouseEvent arg0) {
    if(stamp == 0){
  		newx=arg0.getX();
  		newy=arg0.getY()-50;
  		panel.drawLine(lastx,lasty,newx,newy);
  		lastx=newx; //これがないと扇型を描いてしまう
  		lasty=newy;
    }
	}


	private void init() {
		this.setTitle("Simple Draw");

    // モニタサイズに合わせてウィンドウを表示
    // GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
    // Rectangle rect = env.getMaximumWindowBounds();
    // setBounds(rect);
    setBounds(95,0,700,600);

		this.addMouseMotionListener(this);
    this.addMouseListener(this);
		panel = new DrawPanel();

    // test:bufferを作っておいてみる
    panel.createBuffer(this.getWidth(),this.getHeight());

    // System.out.println(panel.bufferGraphics.getBackground());
    this.initMenu();

		this.getContentPane().add(panel);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

  // メニューを追加するメソッド
  private void addMenuItem(JMenu targetMenu, String itemName, String actionName, ActionListener listener) {
		JMenuItem menuItem = new JMenuItem(itemName);
		menuItem.setActionCommand(actionName);
		menuItem.addActionListener(listener);
		targetMenu.add(menuItem);
	}


	private void initMenu() {
		JMenuBar menubar=new JMenuBar();
		JMenu menuFile = new JMenu("File");

    // JMenuItem menu_new = new JMenuItem("New");
    // menu_new.setActionCommand("New");
    // menu_new.addMouseListener(this);
    // menuFile.add(menu_new);
    // menu_new.setMnemonic(KeyEvent.VK_N);
    // menu_new.setAccelerator(
    // KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));

		this.addMenuItem(menuFile,"New","New",this);

    JMenuItem menu_open = new JMenuItem("Open");
    menu_open.setActionCommand("Open");
		menu_open.addActionListener(this);
		menuFile.add(menu_open);
    // ショートカットキー　CONTROL + O を設定
    menu_open.setMnemonic(KeyEvent.VK_O);
    menu_open.setAccelerator(
    KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));

    JMenuItem menu_save = new JMenuItem("Save");
    menu_save.setActionCommand("Save");
    menu_save.addActionListener(this);
    menuFile.add(menu_save);
    // ショートカットキー CONTROL + S を設定
    menu_save.setMnemonic(KeyEvent.VK_S);
    menu_save.setAccelerator(
    KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));

		menubar.add(menuFile);

    JMenu menuColor = new JMenu("Pen");
    this.addMenuItem(menuColor,"Black","Black",this);
    this.addMenuItem(menuColor,"Blue","Blue",this);
    this.addMenuItem(menuColor,"Yellow","Yellow",this);
    this.addMenuItem(menuColor,"Green","Green",this);
    this.addMenuItem(menuColor,"Red","Red",this);
    this.addMenuItem(menuColor,"MoreColor","MoreColor",this);
    menubar.add(menuColor);

		JMenu menuLineWeight = new JMenu("LineWeight");
		this.addMenuItem(menuLineWeight,"thin","thin",this);
		this.addMenuItem(menuLineWeight,"nomal","nomal",this);
		this.addMenuItem(menuLineWeight,"thick","thick",this);
		menubar.add(menuLineWeight);

    JMenu menuEraser = new JMenu("Eraser");
    this.addMenuItem(menuEraser,"eraser","s_eraser",this);
    this.addMenuItem(menuEraser,"all clear","all_clear",this);
    menubar.add(menuEraser);

    this.setJMenuBar(menubar);
	}

  public void ChangeColor(Color color){
    panel.c = color;
  }

  public void actionPerformed(ActionEvent e){
    stamp = 0;
		String command = e.getActionCommand();
    // ファイル選択のため
    filechooser = new JFileChooser();
    if(command == "New"){
      panel.background_color = Color.WHITE;
      panel.createBuffer(panel.getWidth(),panel.getHeight());
    }
    if(command == "Open"){
     int selected = filechooser.showOpenDialog(this);
     if (selected == JFileChooser.APPROVE_OPTION){
       panel.openFile(filechooser.getSelectedFile());
     }
    }
    if(command == "Save"){
      int selected = filechooser.showSaveDialog(this);
      if (selected == JFileChooser.APPROVE_OPTION) {
      	panel.saveFile(filechooser.getSelectedFile());
      }
    }
		if(command == "Black"){
      ChangeColor(Color.BLACK);
    }
		if (command == "Blue") {
			ChangeColor(Color.BLUE);
		}
    if (command == "Yellow") {
			ChangeColor(Color.YELLOW);
		}
    if (command == "Green"){
       ChangeColor(Color.GREEN);
    }
    if (command == "Red"){
      ChangeColor(Color.RED);
    }
    if(e.getSource() == color_button) {
      // カラーパレットの表示
      JColorChooser colorchooser = new JColorChooser();
      Color color = colorchooser.showDialog(this, "色の選択", Color.white);
      if(color != null){
         // panel.c = color;
         ChangeColor(color);
      }
    }
    if (command == "thin"){
      // this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      panel.currentWidth = 5.0f;
    }
    if (command == "nomal"){
      panel.currentWidth = 10.0f;
    }
    if (command == "thick") {
      panel.currentWidth = 15.0f;
    }
    if (command == "s_eraser"){
      panel.currentWidth = 10.0f;
      ChangeColor(panel.bufferGraphics.getBackground());
    }
    if(command == "all_clear"){
       // ここからtest
       panel.bufferGraphics.setBackground (panel.bufferGraphics.getBackground());
       panel.bufferGraphics.clearRect (0, 0,this.getWidth(), this.getHeight());
       panel.repaint(); // これを呼ばないと更新されない
    }
	}


	public static void main(String[] args) {
		SimpleDraw frame = new SimpleDraw();
		frame.init();

    OperationFrame frame2 = new OperationFrame();
    frame2.setMain(frame);
	}

}
