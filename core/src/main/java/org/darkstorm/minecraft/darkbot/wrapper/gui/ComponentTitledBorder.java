package org.darkstorm.minecraft.darkbot.wrapper.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.Border;

public class ComponentTitledBorder implements Border, MouseListener,
		SwingConstants {
	private Component comp;
	private JComponent container;
	private Rectangle rect;
	private Border border;
	private int offset = 5;

	public ComponentTitledBorder(Component comp, JComponent container,
			Border border) {
		this.comp = comp;
		this.container = container;
		this.border = border;
		container.addMouseListener(this);
	}

	public boolean isBorderOpaque() {
		return true;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		Insets borderInsets = border.getBorderInsets(c);
		Insets insets = getBorderInsets(c);
		int temp = (insets.top - borderInsets.top) / 2;
		border.paintBorder(c, g, x, y + temp, width, height - temp);
		Dimension size = comp.getPreferredSize();
		rect = new Rectangle(offset, 0, size.width, size.height);
		SwingUtilities.paintComponent(g, comp, (Container) c, rect);
	}

	public Insets getBorderInsets(Component c) {
		Dimension size = comp.getPreferredSize();
		Insets insets = border.getBorderInsets(c);
		insets.top = Math.max(insets.top, size.height);
		return insets;
	}

	private void dispatchEvent(MouseEvent me) {
		if(rect != null && rect.contains(me.getX(), me.getY())) {
			Point pt = me.getPoint();
			pt.translate(-offset, 0);
			comp.setBounds(rect);
			comp.dispatchEvent(new MouseEvent(comp, me.getID(), me.getWhen(),
					me.getModifiers(), pt.x, pt.y, me.getClickCount(), me
							.isPopupTrigger(), me.getButton()));
			if(!comp.isValid())
				container.repaint();
		}
	}

	public void mouseClicked(MouseEvent me) {
		dispatchEvent(me);
	}

	public void mouseEntered(MouseEvent me) {
		dispatchEvent(me);
	}

	public void mouseExited(MouseEvent me) {
		dispatchEvent(me);
	}

	public void mousePressed(MouseEvent me) {
		dispatchEvent(me);
	}

	public void mouseReleased(MouseEvent me) {
		dispatchEvent(me);
	}
}