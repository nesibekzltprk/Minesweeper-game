import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GUI extends JFrame implements MouseListener {

    public Btn[][] board;
    public Date startDate;
    public long sec;
    public Timer timer;
    public JFrame jf;
    public JPanel panel;
    public JPanel panelb;
    public JButton timeButton;
    public JButton startButton;
    public int openButtonCount;
    public boolean gameWon = false;
    public boolean gameOver = false;
    public int Openbutton;

    public GUI(int boardSize) {
        timer = new Timer();
        openButtonCount = 0;
        jf = new JFrame();
        setTitle("MINESWEEPER GAME");
        setSize(900, 900);
        panel = new JPanel();
        panelb = new JPanel();
        panelb.setLayout(new BorderLayout());
        panel.setLayout(new GridLayout(boardSize, boardSize));
        timeButton = new JButton();
        timeButton.setSize(150, 70);
        timeButton.setBackground(Color.pink);
        startButton = new JButton("START THE GAME");
        startButton.setFont(new Font("Tahoma", Font.PLAIN, 70));
        startButton.setBackground(Color.pink);



        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String difficulty = (String) JOptionPane.showInputDialog(
                        jf,
                        "Choose difficulty:",
                        "Difficulty",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Easy", "Medium", "Hard"},
                        "Medium");

                int boardSize;
                switch (difficulty) {
                    case "Easy":
                        boardSize = 5;
                        break;
                    case "Medium":
                        boardSize = 10;
                        break;
                    case "Hard":
                        boardSize = 15;
                        break;
                    default:
                        boardSize = 10;
                        break;
                }

                startGame(boardSize);
            }
        });

        board = new Btn[boardSize][boardSize];

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Btn b = new Btn(i, j);
                b.setFont(new Font("Arial Unicode Ms", Font.BOLD, 30));
                panel.add(b);
                b.addMouseListener(this);
                board[i][j] = b;
            }
        }

        panelb.add(timeButton, BorderLayout.SOUTH);
        panelb.add(startButton, BorderLayout.NORTH);
        panelb.add(panel, BorderLayout.CENTER);

        generateMine(boardSize);
        updatecount();
        add(panelb);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void startGame(int boardSize) {
        startDate = new Date();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateTimer();
            }
        }, 0, 1000);

        startButton.setEnabled(false);

        // Yeni oyun başlatıldığında board'u güncelle
        board = new Btn[boardSize][boardSize];
        panel.removeAll(); // Eski buttonları temizle
        panel.setLayout(new GridLayout(boardSize, boardSize));

        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                Btn b = new Btn(i, j);
                b.setFont(new Font("Arial Unicode Ms", Font.BOLD, 30));
                panel.add(b);
                b.addMouseListener(GUI.this); // GUI sınıfının MouseListener'ını kullan
                board[i][j] = b;
            }
        }

        // Yeniden çizim yap
        //revalidate();
        //repaint();
        generateMine(boardSize);
        updatecount();
    }
    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void updateTimer() {
        if (startDate != null) {
            sec = ((new Date().getTime() - startDate.getTime()) / 1000);
            if (sec > 999) {
                sec = 999;
            }
            String timeText = String.format("TIME: %03d", sec);
            timeButton.setText(timeText);
            timeButton.setFont(new Font("Tahoma", Font.PLAIN, 50));
        }
    }

    public void generateMine(int boardSize){
        int i=1;
        while(i < boardSize-2) {
            int randRow = (int) (Math.random() * board.length);
            int randCol = (int) (Math.random() * board[0].length);

            while(board[randRow][randCol].isMine()){
                randRow = (int) (Math.random() * board.length);
                randCol = (int) (Math.random() * board[0].length);
            }
            board[randRow][randCol].setMine(true);
            i++;
        }
    }
    public void print(){
        for(int row=0; row < board.length; row++){
            for(int col=0; col < board[0].length; col++){
                if(board[row][col].isMine()){
                    board[row][col].setText("💣");
                }
                else{
                    board[row][col].setText(board[row][col].getCount()+"");
                    board[row][col].setForeground(Color.BLUE);
                    board[row][col].setEnabled(false);
                }
            }
        }
    }
    public void printMine() {
        for(int row=0; row < board.length; row++){
            for(int col=0; col < board[0].length; col++){
                if(board[row][col].isMine()){
                    board[row][col].setText("💣");
                }
            }
        }
    }
    public void updatecount() {
        for(int row=0; row < board.length; row++){
            for(int col=0; col < board[0].length; col++){
                if(board[row][col].isMine()){
                    counting(row,col);
                }
            }
        }
    }
    public void counting(int row,int col){
        for(int i=row-1; i<= row+1; i++){
            for(int j=col-1; j<=col+1; j++){
                try {
                    int value = board[i][j].getCount();
                    board[i][j].setCount(++value);
                } catch(Exception e) {

                }
            }
        }
    }
    public void Open(int r,int c){
        if(r < 0 || r >=board.length || c<0 || c >=board[0].length || board[r][c].getText().length() > 0
                || board[r][c].isEnabled()==false){
            return;
        }
        else if(board[r][c].getCount() != 0) {
            board[r][c].setText(board[r][c].getCount()+"");
            board[r][c].setEnabled(false);
            Openbutton++;
        }
        else{
            Openbutton++;
            board[r][c].setEnabled(false);
            Open(r-1,c);
            Open(r+1,c);
            Open(r,c+1);
            Open(r,c-1);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Btn b = (Btn) e.getComponent();
        if(e.getButton() == 1) { // sol click
            //System.out.println("sol tik");
            if(b.isMine()){
                gameOver = true;
                stopTimer();
                JOptionPane.showMessageDialog(jf,"GAME OVER");
                print();
            }
            else {
                Open(b.getRow(),b.getCol());
                if(Openbutton == (board.length*board[0].length)-15){
                    gameWon = true;
                    stopTimer();
                    JOptionPane.showMessageDialog(jf,"GAME WIN");
                    print();
                }
            }
        }
        else if(e.getButton() == 3) { // sağ click
           //System.out.println("sağ tik");
            if(!b.isFlag()){
                b.setText("🏳");
                b.setForeground(Color.RED);
                b.setFlag(true);
            }
            else {
                b.setText("");
                b.setFlag(false);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
