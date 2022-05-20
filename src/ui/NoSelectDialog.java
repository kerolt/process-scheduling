package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/*未选择窗口类*/
public class NoSelectDialog extends JDialog {
    public NoSelectDialog(JFrame father) {
        this.setVisible(true);
        this.setBounds(400, 400, 300, 150);
        this.setTitle("错误提示");
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        father.setEnabled(false);
        Container contentPane = this.getContentPane();
        JLabel label = new JLabel("错误！！！请选择算法！！！");
        label.setForeground(Color.red);
        label.setHorizontalAlignment(0);
        contentPane.add(label);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                father.setEnabled(true);
            }
        });
    }
}
