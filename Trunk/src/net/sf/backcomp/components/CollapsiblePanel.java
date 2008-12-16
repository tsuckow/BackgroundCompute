package net.sf.backcomp.components;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class CollapsiblePanel extends JPanel implements ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JComponent content;
	JButton button = new JButton();
	Icon visible;
	Icon notVisible;
	
	
	public CollapsiblePanel(JComponent c, Icon V, Icon NV)
	{
		super( new BorderLayout() );
		
		content = c;
		visible = V;
		notVisible = NV;
		
		button.setAlignmentX(LEFT_ALIGNMENT);
		button.addActionListener(this);
		
		button.setIcon(notVisible);
		button.setContentAreaFilled(false);
		
		JPanel buttonpane = new JPanel( new BorderLayout() ); 
		buttonpane.add(button, BorderLayout.WEST);
		
		content.setVisible(true);
		content.setVisible(false);
		
		add(buttonpane,BorderLayout.NORTH);
		add(content,BorderLayout.CENTER);
	}
	
	public CollapsiblePanel(JComponent c)
	{
		super( new BorderLayout() );
		
		content = c;
		visible = new ImageIcon(
			"images" + File.separator
			+ "components" + File.separator
			+ "arrowdown.png",
			"Visible"
		);
		notVisible = new ImageIcon(
			"images" + File.separator
			+ "components" + File.separator
			+ "arrow.png",
			"Hidden"
		);
		
		button.setAlignmentX(LEFT_ALIGNMENT);
		button.addActionListener(this);
		
		button.setIcon(notVisible);
		button.setContentAreaFilled(false);
		
		JPanel buttonpane = new JPanel( new BorderLayout() ); 
		buttonpane.add(button, BorderLayout.WEST);
		
		content.setVisible(true);
		content.setVisible(false);
		
		add(buttonpane,BorderLayout.NORTH);
		add(content,BorderLayout.CENTER);
	}
	
    public void actionPerformed (ActionEvent e)
    {
        if (e.getSource() == button)
        {
            setCollapsed(!isCollapsed());
        }
    }

    /**
     * Is the panel collapsed?
     */
    public boolean isCollapsed ()
    {
        return !content.isVisible();
    }

    /**
     * Set the collapsed state.
     */
    public void setCollapsed (boolean collapse)
    {
        if (collapse)
        {
            content.setVisible(false);
            button.setIcon(notVisible);
        }
        else
        {
            content.setVisible(true);
            button.setIcon(visible);
        }

        invalidate();
        repaint();
    }


}
