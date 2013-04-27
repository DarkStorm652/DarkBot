package org.darkstorm.darkbot.darkbotmc;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class ClosableTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = -5923476382802695732L;
	private TabCloseUI closeUI = new TabCloseUI();

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		closeUI.paint(g);
	}

	// @Override
	// public void addTab(String title, Component component) {
	// super.addTab(title + "   ", component);
	// }
	//
	// @Override
	// public void addTab(String title, Icon icon, Component component) {
	// super.addTab(title + "   ", icon, component);
	// }
	//
	// @Override
	// public void addTab(String title, Icon icon, Component component, String
	// tip) {
	// super.addTab(title + "   ", icon, component, tip);
	// }

	public void insertClosableTab(String title, Icon icon, Component component,
			String tip, int index) {
		super.insertTab(title + "   ", icon, component, tip, index);
	}

	public void addUnclosableTab(String title, Component component) {
		super.addTab(title.trim(), component);
	}

	public void addUnclosableTab(String title, Icon icon, Component component) {
		super.addTab(title.trim(), icon, component);
	}

	public void addUnclosableTab(String title, Icon icon, Component component,
			String tip) {
		super.addTab(title.trim(), icon, component, tip);
	}

	private class TabCloseUI implements MouseListener, MouseMotionListener {
		private int closeX = 0, closeY = 0, mouseX = 0, mouseY = 0;
		private int selectedTab;
		private final int width = 8, height = 8;
		private Rectangle rectangle = new Rectangle(0, 0, width, height);
		private boolean lastOverClose = false;

		public TabCloseUI() {
			addMouseMotionListener(this);
			addMouseListener(this);
		}

		public void mouseEntered(MouseEvent me) {
			int meX = me.getX();
			int meY = me.getY();
			if(mouseOverTab(meX, meY)) {
				controlCursor();
				repaint();
			}
		}

		public void mouseExited(MouseEvent me) {
			int meX = me.getX();
			int meY = me.getY();
			if(mouseOverTab(meX, meY)) {
				controlCursor();
				repaint();
			}
		}

		public void mousePressed(MouseEvent me) {
		}

		public void mouseClicked(MouseEvent me) {
		}

		public void mouseDragged(MouseEvent me) {
		}

		public void mouseReleased(MouseEvent me) {
			if(closeUnderMouse(me.getX(), me.getY())) {
				boolean isToCloseTab = tabAboutToClose(selectedTab);
				if(isToCloseTab && selectedTab > -1) {
					System.out.println(getSelectedIndex());
					if(getSelectedIndex() > 0)
						setSelectedIndex(getSelectedIndex() - 1);
					System.out.println(getSelectedIndex());
					System.out.println("-");
					System.out.println(getTabCount());
					removeTabAt(selectedTab);
					System.out.println(getTabCount());
				}
				selectedTab = getSelectedIndex();
			}
			int meX = me.getX();
			int meY = me.getY();
			if(mouseOverTabUnclosable(meX, meY)) {
				controlCursor();
				repaint();
			}
		}

		public void mouseMoved(MouseEvent me) {
			mouseX = me.getX();
			mouseY = me.getY();
			boolean mouseOverTab = mouseOverTabUnclosable(mouseX, mouseY);
			if(mouseOverTab || lastOverClose) {
				controlCursor();
				repaint();
				lastOverClose = mouseOverTab;
			}
		}

		private void controlCursor() {
			if(getTabCount() > 0)
				if(closeUnderMouse(mouseX, mouseY)) {
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					if(selectedTab > -1)
						setToolTipTextAt(selectedTab, "Close "
								+ getTitleAt(selectedTab));
				} else {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					if(selectedTab > -1)
						setToolTipTextAt(selectedTab, "");
				}
		}

		private boolean closeUnderMouse(int x, int y) {
			rectangle.x = closeX;
			rectangle.y = closeY;
			return rectangle.contains(x, y);
		}

		public void paint(Graphics g) {
			int tabCount = getTabCount();
			for(int j = 0; j < tabCount; j++)
				if(getSelectedIndex() == j && getTitleAt(j).endsWith("   ")) {
					Rectangle bounds = getBoundsAt(j);
					int x = bounds.x + bounds.width - width - 8;
					int y = bounds.y + 8;
					drawClose(g, x, y);
					break;
				}
			if(mouseOverTab(mouseX, mouseY)) {
				drawClose(g, closeX, closeY);
			}
		}

		private void drawClose(Graphics g, int x, int y) {
			if(getTabCount() > 0) {
				Graphics2D g2 = (Graphics2D) g;
				drawColored(g2, isUnderMouse(x, y) ? Color.RED : Color.WHITE,
						x, y);
			}
		}

		private void drawColored(Graphics2D g2, Color color, int x, int y) {
			g2.setStroke(new BasicStroke(5, BasicStroke.JOIN_ROUND,
					BasicStroke.CAP_ROUND));
			g2.setColor(Color.BLACK);
			g2.drawLine(x, y, x + width, y + height);
			g2.drawLine(x + width, y, x, y + height);
			g2.setColor(color);
			g2.setStroke(new BasicStroke(3, BasicStroke.JOIN_ROUND,
					BasicStroke.CAP_ROUND));
			g2.drawLine(x, y, x + width, y + height);
			g2.drawLine(x + width, y, x, y + height);

		}

		private boolean isUnderMouse(int x, int y) {
			if(Math.abs(x - mouseX) < width && Math.abs(y - mouseY) < height)
				return true;
			return false;
		}

		private boolean mouseOverTab(int x, int y) {
			int tabCount = getTabCount();
			for(int j = 0; j < tabCount; j++)
				if(getBoundsAt(j).contains(mouseX, mouseY)) {
					selectedTab = j;
					closeX = getBoundsAt(j).x + getBoundsAt(j).width - width
							- 8;
					closeY = getBoundsAt(j).y + 8;
					return getTitleAt(j).endsWith("   ");
				}
			return false;
		}

		private boolean mouseOverTabUnclosable(int x, int y) {
			int tabCount = getTabCount();
			for(int j = 0; j < tabCount; j++)
				if(getBoundsAt(j).contains(mouseX, mouseY)) {
					selectedTab = j;
					closeX = getBoundsAt(j).x + getBoundsAt(j).width - width
							- 8;
					closeY = getBoundsAt(j).y + 8;
					return true;
				}
			return false;
		}
	}

	public boolean tabAboutToClose(int tabIndex) {
		if(!getTitleAt(tabIndex).endsWith("   "))
			return false;
		Component c = getComponentAt(tabIndex);
		c.firePropertyChange("TAB_REMOVE", 0, 0);
		return true;
	}
}