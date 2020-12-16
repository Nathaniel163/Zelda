import java.awt.Dimension;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

public class StartGame {
	public static void main(String[] argv) {
		new Start();
	}
}

class Start {
	JFrame MainFrame = new JFrame();
	JLayeredPane MainPane = MainFrame.getLayeredPane();
	JLabel Background = new JLabel();
	Character player;
	Room room;
	Sound music;
	int location = 1;
	boolean locked = true;
	Dialog message;
	int dude = 0;
	String[] text = new String[100];

	class Thing extends Thread {
		JLabel image = new JLabel();
		int Rlocation;
		boolean movable;
		boolean pickupable;
		String identity;
		Point loc;
		Dimension size;

		Thing(int a, String b, Point c) {
			Rlocation = a;
			identity = b;
			loc = c;
		}

		void place() {
			if (identity == "wall") {
				image.setIcon(new ImageIcon("images\\blank.png"));
				image.setBounds(loc.x, loc.y, size.width, size.height);
			} else {
				image.setIcon(new ImageIcon("images\\" + identity + ".png"));
				image.setBounds(loc.x, loc.y, image.getPreferredSize().width, image.getPreferredSize().height);
				size = image.getSize();
			}
			image.setVisible(true);
			MainPane.add(image, 0);
		}

		int getLocation() {
			return this.Rlocation;
		}
	}

	class Item extends Thing {
		Item(int a, String b, Point c) {
			super(a, b, c);
			movable = true;
			pickupable = true;
		}

		void equip() {
			if (identity == "smallheart") {
				new Sound("health");
				if (player.getHP() + 2 >= player.getMaxHP()) {
					player.setHP(player.getMaxHP());
				} else {
					player.setHP(player.getHP() + 2);
				}
				player.heartchange();
			}
			if (identity == "rupee") {
				player.rupee++;
				player.rupeechange();
				new Sound("coin");
			}
			if (identity == "key") {
				player.keys++;
				player.keychange();
				new Sound("key");
			}
			image.setVisible(false);
			pickupable = false;
		}
	}

	class Wall extends Thing {
		Wall(int a, Point c, Dimension d) {
			super(a, "wall", c);
			movable = false;
			pickupable = false;
			size = d;
		}
	}

	class Enemy extends Thing {
		int x = 5;
		int y = 4;
		int health;
		int count = 0;
		int direction;
		Timer sTimer = new Timer();
		Timer giantTimer = new Timer();
		Timer manTimer = new Timer();
		Timer ghostTimer = new Timer();
		boolean invincible = false;

		Enemy(int a, String d, Point e) {
			super(a, d, e);
		}

		void MakeDed() {
			health--;
			if (health == 0) {
				String ranS;
				int ran = (int) (Math.random() * 4);
				if (ran == 0) {
					ranS = "rupee";
				} else {
					ranS = "smallheart";
				}
				Item tmpItem = new Item(location, ranS, image.getLocation());
				room.incItems.add(tmpItem);
				room.incThings.add(tmpItem);
				tmpItem.place();
				image.setVisible(false);
				image.setEnabled(false);
				sTimer.cancel();
			}
		}

		void InvinciTimer() {
			invincible = true;
			Timer timer = new Timer();
			TimerTask timertask = new TimerTask() {
				public void run() {
					invincible = false;
				}
			};
			timer.schedule(timertask, 1000);
		}
	}

	class Spider extends Enemy {
		public Spider(int a, String d, Point e) {
			super(a, d, e);
			health = 1;
			sTimer.scheduleAtFixedRate(tTask, 30, 30);
		}

		TimerTask tTask = new TimerTask() {
			public void run() {
				count++;
				if (count == 10) {
					image.setIcon(new ImageIcon("images\\spiderUP.png"));
				}
				if (count == 20) {
					image.setIcon(new ImageIcon("images\\spiderDown.png"));
					count = 0;
				}
				if (image.getX() >= 760 || image.getY() >= 465 || image.getX() <= 95 || image.getY() <= 70) {
					direction = (int) (Math.random() * 2) + 0;
					if (image.getX() >= 760 || image.getX() <= 95) {
						x = x * -1;
						if (direction == 1 && y <= 0) {
							y = (int) (Math.random() * 5) + 0;
						} else if (direction == 1) {
							y = (int) (Math.random() * 5) + 0;
							y = y * -1;
						}
					} else if (image.getY() >= 465 || image.getY() <= 70) {
						y = y * -1;
						if (direction == 1 && x <= 0) {
							x = (int) (Math.random() * 5) + 1;
						} else if (direction == 1) {
							x = (int) (Math.random() * 5) + 1;
							x = x * -1;
						}
					}
				}
				image.setLocation(image.getX() + x, image.getY() + y);
			}
		};
	}

	class Giant extends Enemy {
		boolean hit = false;

		Giant(int a, String d, Point e) {
			super(a, d, e);
			giantTimer.scheduleAtFixedRate(horsemanTimer, 30, 30);
			health = 3;
		}

		TimerTask horsemanTimer = new TimerTask() {
			public void run() {
				if (x <= 0) {
					if (count == 15) {
						image.setIcon(new ImageIcon("images\\horsemanDownL.png"));
					} else if (count == 30) {
						image.setIcon(new ImageIcon("images\\horsemanUpL.png"));
						count = 0;
					}
				} else if (x >= 0) {
					if (count == 15) {
						image.setIcon(new ImageIcon("images\\horsemanDownR.png"));
					} else if (count == 30) {
						image.setIcon(new ImageIcon("images\\horsemanUpR.png"));
						count = 0;
					}
				}
				count++;
				if (image.getX() >= 710 || image.getY() >= 400 || image.getX() <= 95 || image.getY() <= 70) {
					direction = (int) (Math.random() * 2) + 0;
					if (image.getX() >= 700 || image.getX() <= 95) {
						x = x * -1;
						if (direction == 1 && y <= 0) {
							y = (int) (Math.random() * 5) + 0;
						} else if (direction == 1) {
							y = (int) (Math.random() * 5) + 0;
							y = y * -1;
						}
					} else if (image.getY() >= 410 || image.getY() <= 100) {
						y = y * -1;
						if (direction == 1 && x <= 0) {
							x = (int) (Math.random() * 5) + 1;
						} else if (direction == 1) {
							x = (int) (Math.random() * 5) + 1;
							x = x * -1;
						}
					}
				}
				image.setLocation(image.getX() + x, image.getY() + y);
				if (health == 2) {
					giantTimer.cancel();
					manTimer.scheduleAtFixedRate(manTask, 30, 30);
					x = 6;
					y = 6;
					InvinciTimer();
				}
			}
		};
		TimerTask manTask = new TimerTask() {
			public void run() {
				if (x <= 0) {
					if (count == 15) {
						image.setIcon(new ImageIcon("images\\manLeft1.png"));
					} else if (count == 30) {
						image.setIcon(new ImageIcon("images\\manLeft2.png"));
						count = 0;
					}
				} else if (x >= 0) {
					if (count == 15) {
						image.setIcon(new ImageIcon("images\\manRight1.png"));
					} else if (count == 30) {
						image.setIcon(new ImageIcon("images\\manRight2.png"));
						count = 0;
					}
				}
				count++;
				if (image.getX() >= 710 || image.getY() >= 400 || image.getX() <= 95 || image.getY() <= 70) {
					direction = (int) (Math.random() * 2) + 0;
					if (image.getX() >= 700 || image.getX() <= 95) {
						x = x * -1;
						if (direction == 1) {
							y = y * -1;
						}
					} else if (image.getY() >= 410 || image.getY() <= 100) {
						y = y * -1;
						if (direction == 1) {
							x = x * -1;
						}
					}
				}
				image.setLocation(image.getX() + x, image.getY() + y);
				if (health == 1) {
					giantTimer.cancel();
					ghostTimer.scheduleAtFixedRate(ghostTask, 30, 30);
					y = (int) (Math.random() * 3) + 1;
					x = (int) (Math.random() * 3) + 1;
					identity = "ghost";
					InvinciTimer();
				}
			}
		};
		TimerTask ghostTask = new TimerTask() {
			public void run() {
				if (x <= 0) {
					if (count == 15) {
						image.setIcon(new ImageIcon("images\\ghostLeft1.png"));
					} else if (count == 30) {
						image.setIcon(new ImageIcon("images\\ghostLeft2.png"));
						count = 0;
					}
				} else if (x >= 0) {
					if (count == 15) {
						image.setIcon(new ImageIcon("images\\ghostRight1.png"));
					} else if (count == 30) {
						image.setIcon(new ImageIcon("images\\ghostRight2.png"));
						count = 0;
					}
				}
				count++;
				if (image.getX() >= 710 || image.getY() >= 400 || image.getX() <= 95 || image.getY() <= 70) {
					direction = (int) (Math.random() * 2) + 0;
					if (image.getX() >= 700 || image.getX() <= 95) {
						x = x * -1;
						if (direction == 1 && y <= 0) {
							y = (int) (Math.random() * 3) + 1;
						} else if (direction == 1) {
							y = (int) (Math.random() * 3) + 1;
							y = y * -1;
						}
					} else if (image.getY() >= 410 || image.getY() <= 100) {
						y = y * -1;
						if (direction == 1 && x <= 0) {
							x = (int) (Math.random() * 3) + 1;
						} else if (direction == 1) {
							x = (int) (Math.random() * 3) + 1;
							x = x * -1;
						}
					}
				}
				image.setLocation(image.getX() + x, image.getY() + y);
				if (health == 0) {
					ghostTimer.cancel();
					image.setIcon(new ImageIcon("images\\ghostDed.png"));
					InvinciTimer();
				}
			}
		};
	}

	class Riddler extends Enemy {
		public Riddler(int a, String d, Point e) {
			super(a, d, e);
			health = 3;
		}

		public void run() {
			message.message();
			dude = 3;
			JLabel bossfinal = new JLabel();
			bossfinal.setSize(bossfinal.getPreferredSize().width, bossfinal.getPreferredSize().height);
			bossfinal.setLocation(300, 200);
		}
	}

	class Dialog {
		JLabel textbox = new JLabel();
		JLabel bub = new JLabel();

		public void message() {
// to call from other rooms just type message.message;
// and set dude to the number with the number with the text
			text[1] = ("Welcome");
			text[2] = ("Bye");
			text[3] = ("welcome to the end");
			text[4] = ("lets start your final task");
			text[5] = ("question 1");
			text[6] = ("question 2");
			text[7] = ("question 3");
			text[8] = ("correct");
			text[7] = ("incrroect try again");
			bub.setIcon(new ImageIcon("images\\text.png"));
			bub.setLocation(100, 50);
			bub.setSize(bub.getPreferredSize().width, bub.getPreferredSize().height);
			textbox.setText(text[dude]);
			textbox.setLocation(235, 100);
			textbox.setSize(500, 200);
			textbox.setFont(new Font("TimesRoman", Font.PLAIN, 60));
			if ((dude >= 10)) {
				bub.setVisible(false);
				textbox.setVisible(false);
			}
			textbox.setVisible(true);
			bub.setVisible(true);
			MainPane.add(bub, 0);
			MainPane.add(textbox, 0);
			if ((dude == 5)) {
				String q1 = JOptionPane.showInputDialog(null, "answer");
				String q2 = JOptionPane.showInputDialog(null, "answer");
				String q3 = JOptionPane.showInputDialog(null, "answer");
				if ((q1.equals("yes"))) {
					dude++;
					if ((q2.equals("yes"))) {
						dude++;
						if ((q3.equals("yes"))) {
							System.exit(0);
							JOptionPane.showInputDialog(null, "answer");
						}
					}
				}
			}
		}
	}

	class Room {
		ArrayList<Thing> incThings = new ArrayList<Thing>();
		ArrayList<Item> incItems = new ArrayList<Item>();
		ArrayList<Wall> incWalls = new ArrayList<Wall>();
		ArrayList<Enemy> incEnemies = new ArrayList<Enemy>();
		boolean[] doors = new boolean[4];
		int[] doorslead = new int[4];

		Room() {
			for (Item obj : items) {
				if (obj.getLocation() == location) {
					if (obj.pickupable == true) {
						incThings.add(obj);
						incItems.add(obj);
					}
				}
			}
			for (Wall obj : walls) {
				if (obj.getLocation() == location) {
					incThings.add(obj);
					incWalls.add(obj);
				}
			}
			for (Enemy obj : enemies) {
				if (obj.getLocation() == location) {
					incThings.add(obj);
					incEnemies.add(obj);
				}
			}
			setup();
		}

		void setup() {
			switch (location) {
			case 1:
				doors = new boolean[] { true, false, false, false };
				doorslead = new int[] { 2, 0, 0, 0 };
				break;
			case 2:
				doors = new boolean[] { true, false, true, true };
				doorslead = new int[] { 3, 0, 1, 4 };
				break;
			case 3:
				doors = new boolean[] { false, false, true, true };
				doorslead = new int[] { 0, 0, 2, 5 };
				break;
			case 4:
				doors = new boolean[] { true, true, false, true };
				doorslead = new int[] { 5, 2, 0, 0 };
				break;
			case 5:
				doors = new boolean[] { false, true, true, true };
				doorslead = new int[] { 0, 3, 4, -1 };
				if (location == 5 && locked == false) {
					doorslead[3] = 6;
				}
				break;
			case 6:
				doors = new boolean[] { false, false, false, false };
				doorslead = new int[] { 7, 5, 8, 9 };
				break;
			}
			int num = 50;
			for (Thing obj : incThings) {
				Timer timer = new Timer();
				TimerTask timertask = new TimerTask() {
					public void run() {
						obj.place();
					}
				};
				timer.schedule(timertask, num);
				num = num + 50;
			}
			Background.setIcon(new ImageIcon("images\\room" + location + ".png"));
			if (location == 5 && doorslead[3] == 6) {
				Background.setIcon(new ImageIcon("images\\room" + location + "b.png"));
			}
			Background.setVisible(false);
			Background.setVisible(true);
		}

		boolean[] getDoors() {
			return doors;
		}

		void dispose() {
			for (Thing obj : incThings) {
				MainPane.remove(obj.image);
			}
		}
	}

	class Character {
		JLabel thing = new JLabel();
		int keys;
		int direction = 2;
		int rupee = 0;
		int HP = 6;
		int MaxHP = 6;
		boolean walking = false;
		boolean isAttack = false;
		boolean swingable = true;
		boolean invincible;
		JLabel[] hearts = new JLabel[3];
		JLabel rupeeCounter = new JLabel();
		JLabel[] keyLabel = new JLabel[2];
		MoveTimer MT;

		class MoveTimer extends Thread {
			int num = 0;
			private int xmove;
			private int ymove;
			Timer timer = new Timer();

			MoveTimer() {
				switch (direction) {
				case 0:
					ymove = -5;
					break;
				case 1:
					xmove = -5;
					break;
				case 2:
					ymove = 5;
					break;
				case 3:
					xmove = 5;
					break;
				}
				TimerTask task = new TimerTask() {
					@Override
					public void run() {
						thing.setSize(thing.getPreferredSize());
						num++;
						if (num == 1) {
							thing.setIcon(new ImageIcon("images\\walk" + direction + ".png"));
							thing.setSize(thing.getPreferredSize());
						} else if (num == 5) {
							new Sound("step");
						} else if (num == 10) {
							thing.setIcon(new ImageIcon("images\\walk" + direction + "b.png"));
							thing.setSize(thing.getPreferredSize());
						} else if (num == 20) {
							num = 0;
						}
						if (bordercheck() == false && solidcheck() == false) {
							thing.setLocation(thing.getX() + xmove, thing.getY() + ymove);
						}
						if (doorcheck()) {
							room.dispose();
							location = room.doorslead[direction];
							switch (direction) {
							case 0:
								thing.setLocation(425, 460);
								break;
							case 1:
								thing.setLocation(760, 265);
								break;
							case 2:
								thing.setLocation(425, 70);
								break;
							case 3:
								thing.setLocation(95, 265);
								break;
							}
							room = new Room();
						}
						itemcheck();
						enemycheck();
					}
				};
				timer.schedule(task, 25, 25);
			}

			boolean bordercheck() {
				if (70 <= thing.getY() + ymove && thing.getY() + ymove < 465 && 95 <= thing.getX() + xmove
						&& thing.getX() + xmove <= 760) {
					return false;
				} else {
					return true;
				}
			}

			boolean solidcheck() {
				for (Wall x : room.incWalls) {
					if (charOverlaps(x.loc, new Point(x.loc.x + x.size.width, x.loc.y + x.size.height))) {
						return true;
					}
				}
				return false;
			}

			void enemycheck() {
				for (Enemy x : room.incEnemies) {
					if (charOverlaps(x.image.getLocation(), new Point(x.image.getLocation().x + x.image.getSize().width,
							x.loc.y + x.image.getSize().height))) {
						if (invincible == false && isAttack == false && x.health > 0) {
							new InvinciTimer(x.identity);
						}
					}
				}
			}

			boolean doorcheck() {
				boolean[] doors = room.getDoors();
				int[] x1 = new int[] { 410, 90, 410, 765 };
				int[] x2 = new int[] { 440, 90, 440, 765 };
				int[] y1 = new int[] { 65, 255, 465, 255 };
				int[] y2 = new int[] { 65, 295, 465, 295 };
				for (int i = 0; i <= 3; i++) {
					if (doors[i] == true) {
						if (x1[i] <= thing.getX() + xmove && thing.getX() + xmove <= x2[i]
								&& y1[i] <= thing.getY() + ymove && thing.getY() + ymove <= y2[i]) {
							if (location == 5 && locked == true && keys >= 1 && direction == 3) {
								new Sound("unlock");
								keys--;
								keychange();
								locked = false;
								new Room();
								MT.cancel();
								walking = true;
								new Sound("unlock");
								Timer timer = new Timer();
								TimerTask timertask = new TimerTask() {
									public void run() {
										walking = false;
										timer.cancel();
									}
								};
								timer.schedule(timertask, 3000);
							} else {
								return true;
							}
						}
					}
				}
				return false;
			}

			void itemcheck() {
				for (Item x : room.incItems) {
					if (x.pickupable == true) {
						if (charOverlaps(x.image.getLocation(),
								new Point(x.image.getLocation().x + x.image.getSize().width,
										x.image.getLocation().y + x.image.getSize().height))) {
							x.equip();
						}
					}
				}
			}

			void cancel() {
				walking = false;
				timer.cancel();
			}
		}

		Character() {
			thing.setIcon(new ImageIcon("images\\walk2b.png"));
			MainFrame.setFocusable(true);
			MainFrame.setFocusTraversalKeysEnabled(true);
			thing.setBounds(300, 300, thing.getPreferredSize().width, thing.getPreferredSize().height);
			keylisteners();
			varsetup();
			MainPane.add(thing, 0);
		}

		boolean charOverlaps(Point r1, Point r2) {
			Point c1 = new Point(thing.getX() + MT.xmove, thing.getY() + MT.ymove);
			Point c2 = new Point(thing.getX() + thing.getSize().width + MT.xmove,
					thing.getY() + thing.getSize().height + MT.ymove);
			if (r1.x > c2.x || c1.x > r2.x) {
				return false;
			}
			if (r1.y > c2.y || c1.y > r2.y) {
				return false;
			}
			return true;
		}

		void damage() {
			for (Enemy x : room.incEnemies) {
				if (charOverlaps(x.image.getLocation(), new Point(x.image.getX() + x.image.getSize().width,
						x.image.getY() + x.image.getSize().height))) {
					x.MakeDed();
				}
			}
		}

		public void keychange() {
			for (JLabel x : keyLabel) {
				try {
					MainPane.remove(x);
				} catch (Exception e) {
				}
			}
			for (int i = 0; i + 1 <= keys; i++) {
				keyLabel[i] = new JLabel();
				keyLabel[i].setIcon(new ImageIcon("images\\key.png"));
				keyLabel[i].setBounds(150 + i * 50, 10, keyLabel[i].getPreferredSize().width,
						keyLabel[i].getPreferredSize().height);
				MainPane.add(keyLabel[i], 0);
			}
		}

		void varsetup() {
			int[] TX = new int[] { 600, 670, 740 };
			for (int i = 0; i <= 2; i++) {
				hearts[i] = new JLabel();
				hearts[i].setIcon(new ImageIcon("images\\heart.png"));
				hearts[i].setBounds(TX[i], 25, hearts[i].getPreferredSize().width, hearts[i].getPreferredSize().height);
				MainPane.add(hearts[i], 0);
			}
			JLabel rupeePic = new JLabel();
			rupeePic.setIcon(new ImageIcon("images\\rupee.png"));
			rupeePic.setBounds(10, 10, rupeePic.getPreferredSize().width, rupeePic.getPreferredSize().height);
			MainPane.add(rupeePic, 0);
			rupeeCounter.setFont(new java.awt.Font("Georgia", 1, 70));
			rupeeCounter.setText("0");
			rupeeCounter.setBounds(50, 5, 200, 100);
			MainPane.add(rupeeCounter, 0);
		}

		void heartchange() {
			for (int i = 0; i <= 2; i++) {
				hearts[i].setIcon(new ImageIcon("images\\noheart.png"));
			}
			if (HP <= 0) {
				gameend();
			} else if (HP == 1) {
				hearts[0].setIcon(new ImageIcon("images\\halfheart.png"));
			} else if (HP > 1) {
				hearts[0].setIcon(new ImageIcon("images\\heart.png"));
				if (HP == 3) {
					hearts[1].setIcon(new ImageIcon("images\\halfheart.png"));
				} else if (HP > 3) {
					hearts[1].setIcon(new ImageIcon("images\\heart.png"));
					if (HP == 5) {
						hearts[2].setIcon(new ImageIcon("images\\halfheart.png"));
					} else if (HP > 5) {
						hearts[2].setIcon(new ImageIcon("images\\heart.png"));
					}
				}
			}
		}

		void gameend() {
			room.dispose();
			new Sound("ded");
			JLabel gameOver = new JLabel();
			gameOver.setIcon(new ImageIcon("images\\ded.jpg"));
			gameOver.setBounds(-25, -25, gameOver.getPreferredSize().width, gameOver.getPreferredSize().height);
			MainPane.add(gameOver, 0);
		}

		void rupeechange() {
			rupeeCounter.setText(String.valueOf(rupee));
		}

		void keylisteners() {
			MainFrame.addKeyListener(new java.awt.event.KeyListener() {
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
						if (walking == true && direction != 0) {
							MT.cancel();
						}
						if (walking == false || direction != 0) {
							direction = 0;
							MT = new MoveTimer();
							walking = true;
						}
					}
					if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
						if (walking == true && direction != 2) {
							MT.cancel();
						}
						if (walking == false || direction != 2) {
							direction = 2;
							MT = new MoveTimer();
							walking = true;
						}
					}
					if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
						if (walking == true && direction != 3) {
							MT.cancel();
						}
						if (walking == false || direction != 3) {
							direction = 3;
							MT = new MoveTimer();
							walking = true;
						}
					}
					if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
						if (walking == true && direction != 1) {
							MT.cancel();
						}
						if (walking == false || direction != 1) {
							direction = 1;
							MT = new MoveTimer();
							walking = true;
						}
					}
					if (e.getKeyCode() == KeyEvent.VK_SPACE) {
						System.out.println(MouseInfo.getPointerInfo().getLocation());
						if (walking == true) {
							MT.cancel();
						}
						if (swingable == true) {
							new Sound("swing");
							thing.setIcon(new ImageIcon("images\\sword" + direction + ".png"));
							thing.setSize(thing.getPreferredSize());
							if (direction == 1) {
								thing.setLocation(thing.getX() - 40, thing.getY());
							} else if (direction == 0) {
								thing.setLocation(thing.getX() - 10, thing.getY() - 40);
							}
							isAttack = true;
							damage();
							Timer timer = new Timer();
							TimerTask task = new TimerTask() {
								@Override
								public void run() {
									thing.setIcon(new ImageIcon("images\\walk" + direction + "b.png"));
									if (direction == 1) {
										thing.setLocation(thing.getX() + 40, thing.getY());
									} else if (direction == 0) {
										thing.setLocation(thing.getX() + 10, thing.getY() + 40);
									}
									isAttack = false;
									swingable = true;
									timer.cancel();
									thing.setSize(thing.getPreferredSize());
								}
							};
							swingable = false;
							timer.schedule(task, 400, 400);
						}
					}
				}

				public void keyTyped(KeyEvent e) {
				}

				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
						if (direction == 0) {
							thing.setIcon(new ImageIcon("images\\walk0b.png"));
							MT.cancel();
						}
					}
					if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
						if (direction == 2) {
							thing.setIcon(new ImageIcon("images\\walk2b.png"));
							MT.cancel();
						}
					}
					if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
						if (direction == 3) {
							thing.setIcon(new ImageIcon("images\\walk3b.png"));
							MT.cancel();
						}
					}
					if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
						if (direction == 1) {
							thing.setIcon(new ImageIcon("images\\walk1b.png"));
							MT.cancel();
						}
					}
				}
			});
		}

		class InvinciTimer extends Thread {
			InvinciTimer(String identity) {
				invincible = true;
				HP--;
				if (identity == "ghost") {
					HP--;
				}
				new Sound("oof");
				heartchange();
				Timer timer = new Timer();
				TimerTask timertask = new TimerTask() {
					public void run() {
						invincible = false;
					}
				};
				timer.schedule(timertask, 1000);
			}
		}

		int getHP() {
			return this.HP;
		}

		void setHP(int x) {
			this.HP = x;
		}

		int getMaxHP() {
			return this.MaxHP;
		}

		void setMaxHP(int x) {
			this.MaxHP = x;
		}
	}

	Start() {
		MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MainFrame.setSize(907, 635);
		MainFrame.setLocation(new java.awt.Point(150, 30));
		MainFrame.setLayout(null);
		MainFrame.setIconImage(new ImageIcon("images\\icon.png").getImage());
		Background.setIcon(new ImageIcon("images\\room1.png"));
		Background.setBounds(0, 0, Background.getPreferredSize().width, Background.getPreferredSize().height);
		MainPane.add(Background, 1);
		setup();
		room = new Room();
		player = new Character();
		message = new Dialog();
		MainFrame.setVisible(true);
        music = new Sound("battlemusic");
	}

	ArrayList<Item> items = new ArrayList<Item>();
	ArrayList<Wall> walls = new ArrayList<Wall>();
	ArrayList<Enemy> enemies = new ArrayList<Enemy>();

	void setup() {
		items.add(new Item(1, "rupee", new Point(300, 100)));
		items.add(new Item(1, "smallheart", new Point(500, 100)));
		enemies.add(new Spider(1, "spiderDown", new Point(200, 300)));
		enemies.add(new Spider(1, "spiderDown", new Point(300, 300)));
		walls.add(new Wall(2, new Point(210, 210), new Dimension(120, 180)));
		walls.add(new Wall(2, new Point(570, 210), new Dimension(120, 180)));
		enemies.add(new Spider(2, "spiderDown", new Point(200, 300)));
		enemies.add(new Giant(3, "horsemanUpL", new Point(200, 300)));
		walls.add(new Wall(4, new Point(390, 210), new Dimension(120, 180)));
		items.add(new Item(5, "rupee", new Point(140, 410)));
		items.add(new Item(5, "key", new Point(700, 410)));
		walls.add(new Wall(5, new Point(90, 195), new Dimension(820, 60)));
		walls.add(new Wall(5, new Point(90, 330), new Dimension(820, 60)));
	}
}