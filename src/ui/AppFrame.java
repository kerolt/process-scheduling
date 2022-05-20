package ui;

import process.PCB;
import process.Process;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

/*主窗口类*/
public class AppFrame extends JFrame {
    private int second = 0; //时钟数（秒）
    private boolean flag = false; //用于判断是否有选择算法
    public static JLabel  t1, t2, t3, t4, t5;
    public static JLabel countText; //时钟数显示区域
    public static JLabel hint; //底部提示信息
    public static JTextArea readyArea, blockArea, finishArea;
    private JButton pause;
    private final String[] algorithm = new String[1]; //用于接收所选择的算法名称
    private final AppFrame myThis = this;

    //时钟，用于更新
    private final Timer timer = new Timer(100, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String time;
            second++;
            if(second < 10) time = "0" + second; else time = second + "";
            countText.setText(time);
            if (Integer.parseInt(time) < Process.run.size()) {
                PCB p = Process.run.get(Integer.parseInt(time));
                t1.setText(p.name);
                t2.setText(String.valueOf(p.priority));
                t3.setText(String.valueOf(p.arriveTime));
                t4.setText(String.valueOf(++p.count));
                t5.setText("running");
                readyArea.setText("");
                finishArea.setText("");
                readyArea.append(writeToArea(Process.ready, Integer.parseInt(time), "Ready"));
                finishArea.append(writeToArea(Process.finish, Integer.parseInt(time), "Finish"));
                hint.setText("程序正在运行···");
                hint.setForeground(Color.green);
            } else {
                new EndOfRunDialog(myThis);
                pause.doClick();
                finishArea.append("            " + t1.getText() + "                       " + t2.getText() + "                       " + t3.getText() + "                       " + t4.getText() +"                    " + "Finish" );
                t1.setText(""); t2.setText(""); t3.setText(""); t4.setText(""); t5.setText("");
                hint.setText("运行结束！");
                hint.setForeground(Color.red);
                second = 0;
            }
        }
    });
    
    public AppFrame() {
        this.setVisible(true);
        this.setBounds(350, 70, 800, 700);
        this.setTitle("进程模拟");
        this.setResizable(false);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container container = this.getContentPane();
        //整体设置为绝对布局
        container.setLayout(null);
    
        setHeader(container);
        setContent(container);
        setFooter(container);
    }
    
    /*设置header部分*/
    public void setHeader(Container container) {
        //标题
        JLabel title = new JLabel("系统进程状态");
        Font font = new Font(Font.DIALOG, Font.PLAIN, 25);
        title.setFont(font);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(0, 0, 750, 30);
        container.add(title);
        //计时器
        JPanel counter = new JPanel(); //计时器面板
        counter.setBounds(700, 0, 50, 50);
        counter.setLayout(null);
        JLabel countLabel = new JLabel("时钟"); //计时器文字
        countLabel.setBounds(0, 0, 50, 15);
        countText = new JLabel(); //计时器内容
        countText.setBounds(0, 15, 50, 35);
        countText.setForeground(Color.red);
        countText.setText("00");
        counter.add(countLabel);
        counter.add(countText);
        container.add(counter);
        //头部文字
        JPanel headText = new JPanel(new GridLayout(1, 6));
        JLabel l1 = new JLabel("队列", SwingConstants.CENTER);
        JLabel l2 = new JLabel("进程名", SwingConstants.CENTER);
        JLabel l3 = new JLabel("优先级", SwingConstants.CENTER);
        JLabel l4 = new JLabel("创建时间", SwingConstants.CENTER);
        JLabel l5 = new JLabel("已运行时间", SwingConstants.CENTER);
        JLabel l6 = new JLabel("进程状态", SwingConstants.CENTER);
        headText.setBounds(0, 50, 750, 40);
        headText.add(l1); headText.add(l2); headText.add(l3);
        headText.add(l4); headText.add(l5); headText.add(l6);
        container.add(headText);
    }
    
    /*设置content部分*/
    public void setContent(Container container) {
        JPanel runPanel = new JPanel(new GridLayout(1, 6, 5,0));
        runPanel.setBounds(0, 100, 750, 40);
        JLabel runQueue = new JLabel("正在运行", SwingConstants.CENTER);
        t1 = new JLabel("", SwingConstants.CENTER); t1.setFont(new Font(Font.DIALOG, Font.PLAIN, 16)); t1.setForeground(Color.blue);
        t2 = new JLabel("", SwingConstants.CENTER); t2.setFont(new Font(Font.DIALOG, Font.PLAIN, 16)); t2.setForeground(Color.blue);
        t3 = new JLabel("", SwingConstants.CENTER); t3.setFont(new Font(Font.DIALOG, Font.PLAIN, 16)); t3.setForeground(Color.blue);
        t4 = new JLabel("", SwingConstants.CENTER); t4.setFont(new Font(Font.DIALOG, Font.PLAIN, 16)); t4.setForeground(Color.blue);
        t5 = new JLabel("", SwingConstants.CENTER); t5.setFont(new Font(Font.DIALOG, Font.PLAIN, 16)); t5.setForeground(Color.blue);
        runPanel.add(runQueue); runPanel.add(t1); runPanel.add(t2);
        runPanel.add(t3); runPanel.add(t4); runPanel.add(t5);
        container.add(runPanel);
        
        //就绪队列
        JLabel readyQueue  = new JLabel("就绪队列", SwingConstants.CENTER);
        readyQueue.setBounds(0, 155, 120, 100);
        readyArea = new JTextArea();
        readyArea.setEnabled(false); //文本域不可编辑
        readyArea.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        JScrollPane scroll1 = new JScrollPane(readyArea);
        scroll1.setBounds(120, 155, 630, 100);
        container.add(readyQueue);
        container.add(scroll1);

        //阻塞队列
        JLabel blockQueue  = new JLabel("阻塞队列", SwingConstants.CENTER);
        blockQueue.setBounds(0, 270, 120, 100);
        blockArea = new JTextArea();
        blockArea.setEnabled(false);
        blockArea.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        JScrollPane scroll2 = new JScrollPane(blockArea);
        scroll2.setBounds(120, 270, 630, 100);
        container.add(blockQueue);
        container.add(scroll2);
        
        //完成队列
        JLabel finishQueue  = new JLabel("完成队列", SwingConstants.CENTER);
        finishQueue.setBounds(0, 385, 120, 100);
        finishArea = new JTextArea();
        finishArea.setEnabled(false);
        finishArea.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        JScrollPane scroll3 = new JScrollPane(finishArea);
        scroll3.setBounds(120, 385, 630, 100);
        container.add(finishQueue);
        container.add(scroll3);
    }
    
    /*设置footer部分*/
    private void setFooter(Container container) {
        //选择算法（下拉框）
        JComboBox<String> choose = new JComboBox<>();
        choose.setBounds(30, 500, 350, 60);
        choose.addItem("请选择算法");
        choose.addItem("先来先服务算法");
        choose.addItem("短进程优先算法");
        choose.addItem("优先级调度算法（非抢占式）");
        choose.addItem("时间片轮转算法");
        //添加一个事件监听，当点击下拉框中的一个选项时，将其赋值给algorithm
        choose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algorithm[0] = (String) choose.getSelectedItem();
            }
        });
        //选择按钮
        JButton submit1 = new JButton("文件输入");
        JButton submit2 = new JButton("用户输入");
        JButton goon = new JButton("执行/继续");
        pause = new JButton("暂停");
        submit1.setBounds(380, 500, 100, 60);
        submit2.setBounds(480, 500, 100, 60);
        goon.setBounds(580, 500, 95, 60);
        pause.setBounds(675, 500, 75, 60);
        //提示框
        hint = new JLabel();
        hint.setBounds(30, 570, 400, 60);
        hint.setText("请先选择算法");
        hint.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        //选择按钮：选择算法后点击按钮即可开始运行
        submit1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //若未选择算法，则会弹出错误提示框
                if (algorithm[0] == null || Objects.equals(algorithm[0], "请选择算法")) {
                    System.out.println("错误！");
                    new NoSelectDialog(myThis);
                    return;
                }
                hint.setText("你选择了" + algorithm[0]);
                new Process(algorithm[0], 0).doIt();
                flag = true;
            }
        });
        //用户输入按钮
        submit2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (algorithm[0] == null || Objects.equals(algorithm[0], "请选择算法")) {
                    System.out.println("错误！");
                    new NoSelectDialog(myThis);
                    return;
                }
                new InputDialog(algorithm[0]);
                hint.setText("你选择了" + algorithm[0]);
                flag = true;
            }
        });
        //继续按钮：继续时钟
        goon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (flag) {
                    timer.start();
                }
                else new NoSelectDialog(myThis);
            }
        });
        //暂停按钮：暂停时钟
        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hint.setText("程序已暂停");
                hint.setForeground(Color.red);
                timer.stop();
            }
        });
        container.add(choose);
        container.add(submit1);
        container.add(submit2);
        container.add(goon);
        container.add(pause);
        container.add(hint);
    }
    
    /*将队列内容写入文本框中*/
    public String writeToArea(ArrayList<HashSet<PCB>> queue, int time, String state) {
        StringBuilder sb = new StringBuilder();
        for (PCB p : queue.get(time)) {
            sb.append("            ").append(p.name).append("                       ").append(p.priority).append("                       ").append(p.arriveTime).append("                       ").append(p.count).append("                    ").append(state).append("\n");
        }
        return sb.toString();
    }
}
