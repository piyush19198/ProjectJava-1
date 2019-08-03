package com.project;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;

class Student{
	private int rollNo;
	private String name;
	private float marks;

	public Student(){
		this(0, null, 0.0f);
	}

	public Student(int rollNo, String name, float marks){
		super();
		this.rollNo = rollNo;
		this.name = name;
		this.marks = marks;
	}
//GETTER SETTER
	public int getRollNo(){
		return rollNo;
	}
	public String getName(){
		return name;
	}
	public float getMarks(){
		return marks;
	}

	public void setName(String name){
		this.name = name;
	}
	public void setRollNo(int rollNo){
		this.rollNo = rollNo;
	}
	public void setMarks(float marks){
		this.marks = marks;
	}
//toString METHOD
	@Override
	public String toString(){
		return "Student [rollNo=" + rollNo + ", name=" + name + ", marks=" + marks + "]";
	}
}

interface StudentOperation{
	void add(Student student) throws SQLException;

	void delete(int rollNo) throws SQLException;

	Student find(int rollNo) throws StudentNotFoundException, SQLException;

	void list() throws SQLException;
}

class StudentNotFoundException extends Exception{
	private static final long serialVersionUID = 1L;

	public StudentNotFoundException(String message){
		System.out.println(message);
	}
}

class StudentOperationImpl implements StudentOperation{

	final String DB_DRIVER = "com.mysql.jdbc.Driver";
	final String DB_URL = "jdbc:mysql://localhost:3306/db";
	final String DB_USERNAME = "root";
	final String DB_PASSWORD = "";

	Connection con;

	public StudentOperationImpl() throws ClassNotFoundException, SQLException{
		//LOAD THE JDBC DRIVER
		Class.forName(DB_DRIVER);
		//ESTABLISH CONNECTION
		con = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

		System.out.println("****************************");
		System.out.println("DB CONNECTION IS ESTABLISHED");
	}

	@Override
	public void add(Student student) throws SQLException{
		String sql = "INSERT INTO student(Name, RollNo, Marks) VALUES (?,?,?)";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setString(1, student.getName());
		pstmt.setInt(2, student.getRollNo());
		pstmt.setFloat(3, student.getMarks());
		pstmt.executeUpdate();
		pstmt.close();
		System.out.println("STUDENT DATA IS INSERTED");
	}

	@Override
	public void delete(int rollNo) throws SQLException{
		String sql = "DELETE FROM `student` WHERE ROLLNO=?";
		PreparedStatement pstmt = con.prepareStatement(sql);
		pstmt.setInt(1, rollNo);
		pstmt.executeUpdate();
		pstmt.close();
		System.out.println("STUDENT DATA IS DELETED");
	}

	@Override
	public Student find(int rollNo) throws StudentNotFoundException, SQLException{
		Student student = null;
		Statement stmt = con.createStatement();
		
		ResultSet rs = stmt.executeQuery("SELECT `Name`, `RollNo`, `Marks` FROM `student`");
		while(rs.next()){
			if(rs.getInt("RollNo") == rollNo){
				student = new Student();
				student.setRollNo(rs.getInt("RollNo"));
				student.setMarks(rs.getFloat("Marks"));
				student.setName(rs.getString("Name"));
				System.out.println("ROLL NO" + rollNo + "FOUND IN DATABASE");
			}
		}
		
		if(student == null)
			throw new StudentNotFoundException("RollNo:" + rollNo + "!!!STUDENT NOT FOUND!!!");
		return student;
	}

	@Override
	public void list() throws SQLException{
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT `Name`, `RollNo`, `Marks` FROM `student`");
		while(rs.next()) {
			System.out.println(rs.getInt("RollNo") + " " + rs.getString("Name") + " " + rs.getFloat("Marks"));
		}
		rs.close();
		stmt.close();
		System.out.println("DB IS DISPLAYED");
	}

}

@SuppressWarnings("serial")
class StudentOperationGUI extends JPanel{
	JLabel lblName, lblMarks, lblRollNo, lblHeading;
	JTextField txtName, txtMarks, txtRollNo;
	JButton btnAdd, btnDelete, btnFind, btnList, btnUpdate;
	
	JPanel panelMain, panelLogin, panelButton;

	public StudentOperationGUI(){
		super();
		
		lblName = new JLabel("Name");
		lblMarks = new JLabel("Marks");
		lblRollNo = new JLabel("Roll No");
		lblHeading = new JLabel("Student Database");

		txtName = new JTextField(10);
		txtRollNo = new JTextField(10);
		txtMarks = new JTextField(10);

		btnAdd = new JButton("ADD");
		btnDelete = new JButton("DELETE");
		btnFind = new JButton("FIND");
		btnList = new JButton("LIST");
		
		panelMain = new JPanel();
		panelLogin = new JPanel();
		panelButton = new JPanel();

		panelLogin.add(lblName);
		panelLogin.add(txtName);
		panelLogin.add(lblRollNo);
		panelLogin.add(txtRollNo);
		panelLogin.add(lblMarks);
		panelLogin.add(txtMarks);
		
		panelButton.add(btnAdd);
		panelButton.add(btnDelete);
		panelButton.add(btnFind);
		panelButton.add(btnList);
		
		panelMain.setLayout(new BorderLayout());
		panelMain.add(panelLogin, BorderLayout.CENTER);
		panelMain.add(lblHeading, BorderLayout.NORTH);
		panelMain.add(panelButton, BorderLayout.SOUTH);

		add(panelMain);
		
		ActionEvent_Handler actionEvent_Handler = new ActionEvent_Handler();
		btnAdd.addActionListener(actionEvent_Handler);
		btnDelete.addActionListener(actionEvent_Handler);
		btnFind.addActionListener(actionEvent_Handler);
		btnList.addActionListener(actionEvent_Handler);
		
	}
	
	
	private class ActionEvent_Handler implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			
			Object button = e.getSource();
			
			if(button == btnAdd){
				String name = txtName.getText();
				int rollNo = Integer.parseInt(txtRollNo.getText());
				float marks = Float.parseFloat(txtMarks.getText());
				Student s1 = new Student(rollNo, name, marks);
				
				try{
					StudentOperationImpl studentOperationImpl= new StudentOperationImpl();
					studentOperationImpl.add(s1);
				} 
				catch(ClassNotFoundException e1){
					e1.printStackTrace();
				} 
				catch(SQLException e1){
					e1.printStackTrace();
				}
			}
			
			if(button == btnDelete){
				String name = null;
				int rollNo = Integer.parseInt(txtRollNo.getText());
				float marks = 0.0f;
				
				@SuppressWarnings("unused")
				Student s1 = new Student(rollNo, name, marks);
				
				try{
					StudentOperationImpl studentOperationImpl= new StudentOperationImpl();
					studentOperationImpl.delete(rollNo);
				} 
				catch(ClassNotFoundException e1){
					e1.printStackTrace();
				} 
				catch(SQLException e1){
					e1.printStackTrace();
				}
				
			}
			
			if(button == btnFind){
				String name = null;
				int rollNo = Integer.parseInt(txtRollNo.getText());
				float marks = 0.0f;

				@SuppressWarnings("unused")
				Student s1 = new Student(rollNo, name, marks);

				try{
					StudentOperationImpl studentOperationImpl = new StudentOperationImpl();
					studentOperationImpl.find(rollNo);
					
				} 
				catch(ClassNotFoundException e1){
					e1.printStackTrace();
				} 
				catch(SQLException e1){
					e1.printStackTrace();
				} 
				catch(StudentNotFoundException e1){
					e1.printStackTrace();
				}
			}
			if(button == btnList){
				try{
					StudentOperationImpl studentOperationImpl = new StudentOperationImpl();
					studentOperationImpl.list();
					
				} catch(ClassNotFoundException e1){

					e1.printStackTrace();
				} catch(SQLException e1){

					e1.printStackTrace();
				}
			}
		}
	}	
}

public class StudentDB{
	public static void main(String[] args) throws ClassNotFoundException, SQLException, StudentNotFoundException{
		JFrame frame = new JFrame("Student Database");
		frame.setVisible(true);

		frame.add(new StudentOperationGUI());
		frame.pack();
		frame.setResizable(false);	
	}
}
