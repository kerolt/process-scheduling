package ui;

import process.PCB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import process.Process;

/*运行结束窗口类*/
public class EndOfRunDialog extends JDialog {
    EndOfRunDialog(JFrame father) {
        this.setVisible(true);
        this.setBounds(500, 180, 880, 450);
        this.setTitle("运行完成");
        this.setLayout(null);
        father.setEnabled(false); //弹出结束窗口后，不可操作父级窗口，除非关闭当前窗口
        Container container = this.getContentPane();
        //提示信息
        JLabel label = new JLabel("进程调度运行完成，关闭该窗口可重新选择算法");
        label.setForeground(Color.red);
        label.setHorizontalAlignment(0);
        label.setBounds(0, 0, 880, 100);
        container.add(label);
        //显示表
        JTextArea table = new JTextArea();
        table.setEnabled(false); //文本域不可编辑
        table.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 100, 830, 300);
        
        writeToTable(table);
        container.add(scrollPane);
        //设置关闭事件
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                father.setEnabled(true);
                AppFrame.readyArea.setText("");
                AppFrame.finishArea.setText("");
                AppFrame.countText.setText("00");
                AppFrame.hint.setText("请先选择算法");
                AppFrame.hint.setForeground(Color.black);
            }
        });
    }
    
    /*将pcb中的内容展现在table文本域中*/
    private void writeToTable(JTextArea table) {
        ArrayList<PCB> pcb = Process.pcb;
        double totalTurnTime = 0, totalPowerTime = 0;
        table.append("进程名\t创建时间\t结束时间\t等待时间\t运行时间\t优先级\t周转时间\t带权周转时间\n");
        for (PCB p : pcb) {
            table.append(p.name + "\t" + p.arriveTime + "\t" + p.finishTime + "\t" + p.waitTime + "\t" + p.runTime + "\t" + p.priority + "\t" + p.turnTime + "\t" + (float)p.powerTime + "\n");
            totalTurnTime += p.turnTime;
            totalPowerTime += p.powerTime;
        }
        table.append("平均周转时间：" + (float)totalTurnTime / pcb.size() + "\n");
        table.append("平均带权周转时间：" + (float)totalPowerTime / pcb.size());
    }
}