import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

    public class SimpleGUI {
	private String name;
	
	public SimpleGUI(String name) {
		this.name = name;
	}
	
	public void initializeGUI() {
		JFrame frame = new JFrame(name);
		JPanel pane = new JPanel();
		final JButton button = new JButton("Press");
        


		JButton quitbutton = new JButton("QUIT");
		final JLabel label = new JLabel(" ");

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                label.setText("Hello");
                button.setText("Test");
            }
        });
	
		pane.setLayout(new BorderLayout());
		pane.add(button, BorderLayout.SOUTH);
		pane.add(quitbutton, BorderLayout.NORTH);
		pane.add(label, BorderLayout.CENTER);
		
		frame.getContentPane().add(pane, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		SimpleGUI g = new SimpleGUI("A GUI!");
		g.initializeGUI();
	}
}
