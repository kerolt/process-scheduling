package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import process.PCB;
import process.Process;

public class InputDialog extends JFrame {
    private int size;
    private int selectRow;
    private JFrame myThis = this;
    
    public InputDialog(String algorithm) {
        this.setVisible(true);
        this.setTitle("选择输入方式");
        this.setBounds(250, 70, 700, 375);
        this.setResizable(false);
        
        String[] colNames = {"进程名", "到达时间", "所需运行时间", "优先级"};
        String[][] tableValues = {};
        JScrollPane scrollPane = new JScrollPane();
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        DefaultTableModel tableModel = new DefaultTableModel(tableValues, colNames);
        JTable table = new JTable(tableModel);
        scrollPane.setViewportView(table);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectRow = table.getSelectedRow();
                
            }
        });
        
        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);
        JTextField text1 = new JTextField(3);
        panel.add(new JLabel("进程名"));
        panel.add(text1);
        JTextField text2 = new JTextField(3);
        panel.add(new JLabel("到达时间"));
        panel.add(text2);
        JTextField text3 = new JTextField(3);
        panel.add(new JLabel("运行时间"));
        panel.add(text3);
        JTextField text4 = new JTextField(3);
        panel.add(new JLabel("优先级"));
        panel.add(text4);
        
        //添加按钮
        JButton addButton = new JButton("添加");
        panel.add(addButton);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] rowValues = {text1.getText(), text2.getText(), text3.getText(), text4.getText()};
                tableModel.addRow(rowValues);
                text1.setText("");
                text2.setText("");
                text3.setText("");
                text4.setText("");
            }
        });
        //修改按钮
        JButton updButton = new JButton("修改");
        panel.add(updButton);
        updButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectRow = table.getSelectedRow();
                if (selectRow != -1) {
                    tableModel.setValueAt(text1.getText(), selectRow, 0);
                    tableModel.setValueAt(text2.getText(), selectRow, 1);
                    tableModel.setValueAt(text3.getText(), selectRow, 2);
                    tableModel.setValueAt(text4.getText(), selectRow, 3);
                    text1.setText("");
                    text2.setText("");
                    text3.setText("");
                    text4.setText("");
                }
            }
        });
        //删除按钮
        JButton delButton = new JButton("删除");
        panel.add(delButton);
        delButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectRow = table.getSelectedRow();
                if (selectRow != -1) {
                    tableModel.removeRow(selectRow);
                }
            }
        });
        //提交
        JButton submit = new JButton("提交");
        panel.add(submit);
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                size = table.getRowCount();
                for (int i = 0; i < size; i++) {
                    String name = tableModel.getValueAt(i, 0).toString();
                    double arriveTime = Double.parseDouble(tableModel.getValueAt(i, 1).toString());
                    double runTime = Double.parseDouble(tableModel.getValueAt(i, 2).toString());
                    int priority = Integer.parseInt(tableModel.getValueAt(i, 3).toString());
                    PCB p = new PCB(name, arriveTime, runTime, priority);
                    Process.pcb.add(p);
                }
                new Process(algorithm, 1).doIt();
                new OverDialog();
            }
        });
    }
    
    class OverDialog extends JDialog {
        public OverDialog() {
            this.setVisible(true);
            this.setTitle("提示");
            this.setBounds(350, 100, 500, 100);
            JLabel label = new JLabel("您已经输入完成，请关闭多余窗口，在主窗口点击“执行”按钮",SwingConstants.CENTER);
            getContentPane().add(label);
        }
    }
}
