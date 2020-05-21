import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import java.sql.Date;
import java.util.Calendar;

// App Class
class App {

	App() {

		JFrame jf = new JFrame("MEDIPLUS");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// jf.setLayout(new FlowLayout());
		jf.setSize(600, 600);
		JTabbedPane jtp = new JTabbedPane();
		jtp.addTab("Home", new Home());
		jtp.addTab("Add", new Add(jf));
		jtp.addTab("Check Expiry", new CExp(jf));
		jtp.addTab("Remove", new Remove(jf));
		jtp.addTab("View Bill", new Bill());
		jf.add(jtp);
		jf.setVisible(true);
		jf.setExtendedState(JFrame.MAXIMIZED_BOTH);

	}
}

// Bill Class
class Bill extends JPanel implements ActionListener {
	JLabel pid;
	JTextField pd;
	Connection con = null;
	ResultSet r2, r3;
	Statement stmt;
	int p;
	JTextArea txt;
	String s, pname, s1;
	JButton viewBill, reset;
	Dimension screenSize;
	float total;

	public Bill() {
		pid = new JLabel("Patient ID");
		pd = new JTextField();
		viewBill = new JButton("View Bill");
		reset = new JButton("Reset");
		setLayout(null);
		add(pid);
		add(pd);
		add(viewBill);
		add(reset);
		pid.setBounds(50, 10, 100, 30);
		pd.setBounds(200, 10, 100, 30);
		viewBill.setBounds(400, 10, 100, 30);
		reset.setBounds(550, 10, 100, 30);
		viewBill.addActionListener(this);
		reset.addActionListener(this);
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == viewBill) {
			p = Integer.parseInt(pd.getText());
			s = "\t\t MEDIPLUS PHARMACY\n \t\t   Mulund-400080\n\n";
			s1 = "\t\t MEDIPLUS PHARMACY\n \t\t   Mulund-400080\n\n";
			// s+="\nCustomer ID:"+p+"\n\n Medicine name\tQuantity\t\tCost";

			txt = new JTextArea(s);

			try {

				Class.forName("oracle.jdbc.driver.OracleDriver");
				con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "oracle");
				stmt = con.createStatement();
				r2 = stmt.executeQuery("select pname from patient where pid='" + p + "'");
				while (r2.next()) {
					pname = r2.getString(1);
				}

				s = "\nCustomer ID:" + p + "\nCustomer Name:" + pname + "\n\n  Medicine Name\t\t\tCost\n";
				txt.append(s);

				r2 = stmt.executeQuery("select * from sold where pid='" + p + "'");
				while (r2.next()) {
					txt.append("\n  " + r2.getString(3) + "\t\t" + "\t\t" + r2.getString(4));

				}
				r2 = stmt.executeQuery("select net from patient where pid='" + p + "'");
				while (r2.next()) {
					total = r2.getFloat(1);
				}
				txt.append(
						"\n--------------------------------------------------------------------------------------------------\nTotal\t\t\t\t"
								+ total);
				add(txt);
				txt.setBounds(10, 100, (screenSize.width - 100), screenSize.height - 400);
				txt.setEditable(false);

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		if (ae.getSource() == reset) {
			System.out.println("In reset");
			txt.setText(s1);
			pd.setText("");

		}

	}

}

// Home Class
class Home extends JPanel implements ActionListener, ItemListener {
	JPanel left, right;
	JButton add, cal, res;
	JLabel details, med, medname, qty;
	JLabel l1, l2, l3;
	JTextField cn, dn;
	JSpinner qt;
	Connection con = null;
	ResultSet rs, r, r2, rl, r3, r4;
	PreparedStatement stat;
	Statement stmt;
	List md;
	JTextArea txt;
	float net = 0, cost;
	String s, t1, t2, today;
	int flag = 0, scount = 1, flgsp = 0, pid = 0, flagp = 0;
	Integer max = new Integer(1);
	SpinnerNumberModel snm;
	Dimension screenSize;
	// public Home(List arr){

	// try{
	// Class.forName("oracle.jdbc.driver.OracleDriver");
	// Connection
	// con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","oracle");
	// stmt=con.createStatement();
	// md.removeAll();
	// rs=stmt.executeQuery("select unique mname from inventory");

	// while(rs.next())
	// {
	// md.add(rs.getString(1));
	// System.out.println(rs.getString(1));

	// }

	// }
	// catch(Exception e){ System.out.println(e);}

	// }
	public Home() {
		right = new JPanel();
		right.setSize(500, 600);
		right.setBorder(BorderFactory.createLineBorder(Color.gray));

		right.setVisible(false);
		right.setVisible(true);

		left = new JPanel();
		left.setSize(500, 600);
		left.setBorder(BorderFactory.createLineBorder(Color.gray));
		left.setVisible(false);
		left.setVisible(true);

		details = new JLabel("Details");
		med = new JLabel("Medicine Details");
		medname = new JLabel("Medicine Name");
		qty = new JLabel("Quantity");
		// qt=new JSpinner(new SpinnerNumberModel(1, 1, scount, 1));
		add = new JButton("Add");
		cal = new JButton("Generate total");
		res = new JButton("Reset");
		t1 = "";
		t2 = "";
		s = "\t\t\t MEDIPLUS PHARMACY\n \t\t\t   Mulund-400080\n\n";
		txt = new JTextArea(s);

		// left.add(b1);
		l1 = new JLabel("Customer Name");
		l2 = new JLabel("Doctor Name");
		l3 = new JLabel("Quantity");

		cn = new JTextField(20);
		dn = new JTextField(20);
		snm = new SpinnerNumberModel();
		qt = new JSpinner(snm);
		qt.setValue(1);

		left.add(qt);
		qt.setBounds(150, 380, 50, 20);
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		JSplitPane splitPane = new JSplitPane();
		splitPane.setSize(screenSize.width, screenSize.height);
		splitPane.setDividerSize(0);
		splitPane.setDividerLocation(screenSize.width / 2);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(left);
		splitPane.setRightComponent(right);
		left.setLayout(null);
		right.setLayout(null);

		this.add(splitPane);
		md = new List();

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "oracle");

			stmt = con.createStatement();
			rs = stmt.executeQuery("select unique mname from inventory order by mname asc");

			while (rs.next()) {
				md.add(rs.getString(1));

			}
			// con.close();

			rs = stmt.executeQuery("select max(pid) from patient");

			while (rs.next()) {
				int i = rs.getInt(1);
				pid = i + 1;

			}

		} catch (Exception e) {
			System.out.println(e);
		}

		// JTextField qt=new JTextField(10);
		left.add(details);
		left.add(l1);
		left.add(cn);
		left.add(l2);
		left.add(dn);
		left.add(l3);
		left.add(md);
		left.add(med);
		left.add(medname);
		left.add(qty);
		// left.add(qt);
		left.add(add);
		left.add(cal);
		left.add(res);
		// add(qt);

		setLayout(null);
		details.setBounds(100, 20, 100, 20);
		l1.setBounds(20, 50, 100, 20);
		l2.setBounds(20, 100, 100, 20);

		cn.setBounds(150, 50, 200, 20);
		dn.setBounds(150, 100, 200, 20);
		med.setBounds(100, 150, 200, 20);
		medname.setBounds(20, 200, 200, 20);
		md.setBounds(150, 200, 200, 120);
		qty.setBounds(20, 380, 100, 20);
		add.setBounds(20, 450, 100, 20);
		cal.setBounds(180, 450, 150, 20);
		res.setBounds(390, 450, 100, 20);

		add.addActionListener(this);
		cal.addActionListener(this);
		res.addActionListener(this);
		md.addItemListener(this);

		// Right pane

		right.add(txt);
		txt.setBounds(5, 5, (screenSize.width / 2) - 20, (screenSize.height) - 20);
		txt.setEditable(false);
	}

	public void itemStateChanged(ItemEvent a) {
		flgsp++;
		String xx = md.getSelectedItem();
		System.out.println(xx);
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "oracle");
			stmt = con.createStatement();

			rl = stmt.executeQuery("select count(mid) from inventory where mname='" + xx + "'");

			while (rl.next()) {
				scount = (rl.getInt(1));

			}
			// if(flgsp!=1){
			// left.remove(qt);
			// }
			snm.setValue(1);
			snm.setMinimum(1);

			snm.setMaximum(scount);

		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public void actionPerformed(ActionEvent ae) {

		if (ae.getSource() == add) {
			if (flag == 0) {
				t1 = cn.getText();
				t2 = dn.getText();
				s = "PID:" + pid + "\nCustomer Name:" + t1 + "\nDoctor Name:" + t2 + "\n\n  Medicine name\tQuantity\t\tCost\n";
				txt.append(s);
				flag = 1;
			}

			String m = md.getSelectedItem();
			int q = (Integer) qt.getValue();
			int i = q;

			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "oracle");
				stmt = con.createStatement();

				if (flagp == 0) {
					stat = con.prepareStatement("insert into patient values (?, ?, ?, ?)");

					String pname = cn.getText();
					String dname = dn.getText();

					stat.setInt(1, pid);
					stat.setString(2, pname);
					stat.setString(3, dname);
					stat.setFloat(4, 0);

					stat.executeUpdate();
					flagp = 1;

				}

				r = stmt.executeQuery("select unique mcost from inventory where mname='" + m + "'");

				while (r.next()) {
					cost = (r.getFloat(1));

				}
				txt.append("\n  " + m + "\t\t" + q + "\t\t" + cost * q);

				net = net + cost * q;

				// con.close();

				r3 = stmt.executeQuery("select * from inventory where mname='" + m + "' order by expdt asc");

				while (r3.next()) {
					if (i == 0) {
						break;
					}

					stat = con.prepareStatement("insert into sold values (?, ?, ?, ?, ?, ?)");
					int idd = r3.getInt(1);
					String mnames = r3.getString(2);
					float mcosts = r3.getFloat(3);
					java.sql.Date expdts = r3.getDate(4);

					stat.setInt(1, pid);
					stat.setInt(2, idd);
					stat.setString(3, mnames);
					stat.setFloat(4, mcosts);
					stat.setDate(5, expdts);

					java.util.Date date = Calendar.getInstance().getTime();
					java.sql.Date sqlDate = new java.sql.Date(date.getTime());
					stat.setDate(6, sqlDate);

					stat.executeUpdate();

					stat = con.prepareStatement("delete from inventory where mid=?");
					stat.setInt(1, idd);
					stat.executeUpdate();
					i--;

				}
				md.removeAll();
				rs = stmt.executeQuery("select unique mname from inventory order by mname asc");

				while (rs.next()) {
					md.add(rs.getString(1));
					System.out.println(rs.getString(1));

				}

			} catch (Exception e) {
				System.out.println(e);
			}

		}

		if (ae.getSource() == cal) {
			txt.append(
					"\n--------------------------------------------------------------------------------------------------\n\n Total\t\t\t\t "
							+ net);
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "oracle");
				stmt = con.createStatement();
				stat = con.prepareStatement("update patient set net=? where pid=?");

				stat.setFloat(1, net);
				stat.setInt(2, pid);

				stat.executeUpdate();

			} catch (Exception e) {
				System.out.println(e);
			}

		}

		if (ae.getSource() == res) {
			flagp = 0;
			pid++;
			net = 0;
			s = "\t\t\t MEDIPLUS PHARMACY\n \t\t\t   Mulund-400080\n\n";
			txt.setText(s);
			flag = 0;
			cn.setText("");
			dn.setText("");
			qt.setValue(1);

		}
	}

}

// Add Class
class Add extends JPanel implements ActionListener {
	JButton b1;
	Connection con = null;
	ResultSet r2, r3;
	Statement stmt;
	PreparedStatement stat;
	JLabel l1, l2, l3, l4;
	JTextField m1, m2, m3, m4;
	int counter, qty = 0, max1 = 0, max2 = 0;
	JFrame temp;

	public Add(JFrame jf) {
		temp = jf;
		l1 = new JLabel("Medicine name");
		// l2=new JLabel("Medicine barcode");
		l2 = new JLabel("Expiry date");
		l3 = new JLabel("Price");
		l4 = new JLabel("Quantity");
		m1 = new JTextField(20);
		// m2=new JTextField(20);
		m2 = new JTextField(20);
		m3 = new JTextField(20);
		m4 = new JTextField(20);

		b1 = new JButton("Add");
		setLayout(null);
		l1.setBounds(100, 60, 100, 20);
		l2.setBounds(100, 140, 100, 20);
		l3.setBounds(100, 220, 100, 20);
		l4.setBounds(100, 300, 100, 20);
		m1.setBounds(300, 60, 200, 20);
		m2.setBounds(300, 140, 200, 20);
		m3.setBounds(300, 220, 200, 20);
		m4.setBounds(300, 300, 200, 20);
		b1.setBounds(450, 400, 100, 30);
		add(l1);
		add(m1);
		add(l2);
		add(m2);
		add(l3);
		add(m3);
		add(l4);
		add(m4);
		add(b1);
		b1.addActionListener(this);

	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == b1) {
			qty = Integer.parseInt(m4.getText());
			try {

				Class.forName("oracle.jdbc.driver.OracleDriver");
				Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "oracle");

				stmt = con.createStatement();
				r2 = stmt.executeQuery("select max(mid) from inventory");

				while (r2.next()) {
					max1 = r2.getInt(1);
					System.out.println("max1" + max1);
				}
				r3 = stmt.executeQuery("select max(mid) from sold");
				while (r3.next()) {
					max2 = r3.getInt(1);
					System.out.println("max1" + max1);
				}
				if (max1 >= max2)
					counter = max1;
				else
					counter = max2;
				System.out.println(counter);

				for (int i = 1; i <= qty; i++) {
					stat = con.prepareStatement("insert into inventory values(?,?,?,?)");

					int mid = counter + 1;

					String mname = m1.getText();
					String mcost = m3.getText();
					String exp = m2.getText();
					stat.setInt(1, mid);
					stat.setString(2, mname);
					stat.setFloat(3, Float.parseFloat(mcost));
					stat.setString(4, exp);
					stat.executeUpdate();
					counter++;
					stat = con.prepareStatement("commit");
					stat.executeUpdate();

				}
				String infoMessage = m1.getText() + " added!";
				JOptionPane.showMessageDialog(this, infoMessage);

				App h = new App();

				temp.dispose();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

}

// Check Expiry Class
class CExp extends JPanel implements ActionListener, ItemListener {
	JLabel sdate, edate, name, exp;
	JTextField lsdate, ledate;
	JButton search, remove, reset;
	Checkbox sa;
	int counter;
	Connection con = null;
	ResultSet rs;
	Statement stmt;
	PreparedStatement stat;
	String query;
	JLabel labelarr[];
	JLabel expA[];
	Checkbox arr[];
	JFrame temp;

	public CExp(JFrame jf) {
		// Checkbox arr[]=new Checkbox[counter];
		// for(int i=0; i<arr.length; i++)
		// arr[i]=new Checkbox("Heyo"+i);

		// for(int i=0; i<arr.length; i++)
		// {
		// add(arr[i]);
		// arr[i].addItemListener(this);
		// }
		temp = jf;

		sdate = new JLabel("Start date");
		edate = new JLabel("End date");

		search = new JButton("Search");
		remove = new JButton("Remove");
		reset = new JButton("Reset");

		lsdate = new JTextField(20);
		ledate = new JTextField(20);

		add(sdate);
		add(edate);

		add(search);
		add(reset);

		add(lsdate);
		add(ledate);

		setLayout(null);

		sdate.setBounds(50, 20, 100, 20);
		lsdate.setBounds(200, 20, 100, 20);
		edate.setBounds(350, 20, 100, 20);
		ledate.setBounds(450, 20, 100, 20);
		search.setBounds(600, 20, 100, 20);
		reset.setBounds(750, 20, 100, 20);

		search.addActionListener(this);
		remove.addActionListener(this);
		reset.addActionListener(this);

	}

	public void actionPerformed(ActionEvent ae) {
		int globalCounter;

		if (ae.getSource() == search) {

			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "oracle");
				query = "select * from inventory where expdt>=to_date(?,'DD-MON-YY') and expdt<=to_date(?,'DD-MON-YY')";
				stat = con.prepareStatement(query);
				String sd = lsdate.getText();
				String ed = ledate.getText();
				stat.setString(1, sd);
				stat.setString(2, ed);
				System.out.println(stat.executeQuery());
				rs = stat.executeQuery();
				int y = 100;
				int x = 50;
				counter = 0;
				while (rs.next()) {
					counter += 1;

				}
				labelarr = new JLabel[counter];
				expA = new JLabel[counter];
				arr = new Checkbox[counter];

				int index = 0;
				rs = stat.executeQuery();

				while (rs.next()) {
					labelarr[index] = new JLabel(rs.getString(2));
					expA[index] = new JLabel(rs.getString(4).substring(0, 10));
					arr[index] = new Checkbox();
					index += 1;
				}
				name = new JLabel("Name");
				exp = new JLabel("Expiry Date");
				sa = new Checkbox("Select all");
				add(name);
				add(exp);
				add(sa);
				sa.addItemListener(this);

				name.setBounds(50, 75, 100, 15);
				exp.setBounds(200, 75, 150, 15);
				sa.setBounds(400, 75, 100, 15);

				for (int i = 0; i < arr.length; i++) {

					add(arr[i]);
					add(expA[i]);
					add(labelarr[i]);
					labelarr[i].setBounds(50, y, 100, 15);
					expA[i].setBounds(200, y, 150, 15);
					arr[i].setBounds(400, y, 50, 15);

					y += 20;
				}
				remove.setVisible(true);
				add(remove);
				remove.setBounds(200, y + 20, 100, 20);

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}
		if (ae.getSource() == remove) {
			for (int i = 0; i < counter; i++) {
				if (arr[i].getState()) {
					try {
						Class.forName("oracle.jdbc.driver.OracleDriver");
						con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "oracle");
						query = "delete from inventory where expdt=to_date(?,'YYYY-MM-DD') and mname=?";
						stat = con.prepareStatement(query);
						stat.setString(1, expA[i].getText());
						stat.setString(2, labelarr[i].getText());
						stat.executeUpdate();
						System.out.println("Checked" + expA[i].getText());

					} catch (Exception e) {
						System.out.println(e.getMessage());
					}

				}
			}
			String infoMessage = "Medicines removed";
			JOptionPane.showMessageDialog(this, infoMessage);
			App h = new App();

			temp.dispose();

		}
		if (ae.getSource() == reset) {
			lsdate.setText("");
			ledate.setText("");

			remove(sa);
			name.setText("");
			exp.setText("");
			remove.setVisible(false);
			for (int i = 0; i < counter; i++) {
				labelarr[i].setText("");
				expA[i].setText("");
				remove(arr[i]);

			}

		}

	}

	public void itemStateChanged(ItemEvent e) {
		if (sa.getState()) {
			for (int i = 0; i < counter; i++) {
				arr[i].setState(true);

			}
			sa.setState(false);

		}

	}

}

// Update
class Remove extends JPanel implements ActionListener {
	JButton b1;
	Connection con = null;
	ResultSet r2;
	Statement stmt;
	PreparedStatement stat;
	JLabel l1;
	JTextField m1;
	int counter;
	JFrame temp;

	public Remove(JFrame jf) {
		l1 = new JLabel("Medicine name");
		m1 = new JTextField(20);
		b1 = new JButton("Remove");
		setLayout(null);
		l1.setBounds(400, 60, 100, 20);
		m1.setBounds(600, 60, 200, 20);
		b1.setBounds(500, 120, 100, 30);
		add(l1);
		add(m1);
		add(b1);
		b1.addActionListener(this);

	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == b1)
		// String mn=m1.getText();
		{

			try {
				counter = 0;
				Class.forName("oracle.jdbc.driver.OracleDriver");
				con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "oracle");
				stmt = con.createStatement();
				r2 = stmt.executeQuery("select * from inventory where mname='" + m1.getText() + "'");

				if (!r2.next()) {
					String infoMessage = "No medicine found. Please enter valid name";
					JOptionPane.showMessageDialog(this, infoMessage);

				} else {
					stat = con.prepareStatement("delete from inventory where mname='" + m1.getText() + "'");
					stat.executeUpdate();
					String infoMessage = m1.getText() + " removed";
					JOptionPane.showMessageDialog(this, infoMessage);

				}
				App h = new App();

				temp.dispose();

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}
	}

}

// Main Class
class PManagement {
	public static void main(String args[]) {
		App a = new App();
	}
}