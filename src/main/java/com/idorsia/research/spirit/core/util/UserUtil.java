package com.idorsia.research.spirit.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.actelion.research.business.Department;
import com.actelion.research.business.Employee;
import com.actelion.research.security.entity.User;
import com.actelion.research.security.exception.DataReadAccessException;
import com.actelion.research.security.service.UserService;
import com.actelion.research.services.DBDepartment;
import com.actelion.research.services.DBEmployee;
import com.actelion.research.util.SysLogStream;
import com.idorsia.research.spirit.core.constants.Constants;

public class UserUtil {
	private static Boolean firstConnection = true;
	private static User user;
	private static Employee employee;
	private static UserService userService = new UserService();
	private static Set<String> managedUsers = new HashSet<>();

	public static User getUser() {
		if(user==null && firstConnection) {
			try {
				firstConnection=false;
				setUser(userService.loadUserByUsername(System.getProperty("user.name"), true, true));
			} catch (DataReadAccessException e) {
				e.getMessage();
			}
		}
		return user;
	}

	public static void setUser(User user) {
		UserUtil.user = user;
		employee=user==null?null:DBEmployee.loadEmployeeByUserId(user.getId());
		managedUsers.clear();
		populateManagedUsersRec(employee, managedUsers);
		if(user!=null) {
			if ("true".equalsIgnoreCase(System.getProperty("production")))
				SysLogStream.getInstance("spirit", user.getUsername());
		}
	}
	
	public static Employee getEmployee() {
		if(user==null) {
			getUser();
		}
		return employee;
	}

	public static String getUsername() {
		return getUser()==null?null:getUser().getUsername();
	}

	public static Set<String> getRoles() {
		return getEmployee().getRoles();
	}
	
	public static boolean isMember(Department department) {
		return getEmployee().isMember(department);
	}

	public static Set<String> getManagedUsers() {
		return managedUsers;
	}
	
	private static void populateManagedUsersRec(Employee root, Set<String> res) {
		if(root == null || res.contains(root.getUserName())) return;
		res.add(root.getUserName());
		for (Employee emp : root.getChildren()) {
			populateManagedUsersRec(emp, res);
		}
	}

	public static Set<Department> getDepartments() {
		return getEmployee().getDepartments();
	}

	public static boolean isSuperAdmin() {
		return isRole(Constants.ADMIN);
	}
	
	public static boolean isRole(String role) {
		return getEmployee().getRoles().contains(role);
	}

	public static Department getMainDepartment() {
		return getEmployee().getMainDepartment();
	}
	
	public static Department getMainDepartment(User u) {
		return DBEmployee.loadEmployeeByUserId(user.getId()).getMainDepartment();
	}
	
	public static List<String> getNames(List<Department> departments) {
		List<String> res = new ArrayList<>();
		for (Department department : departments) {
			res.add(getNameShort(department));
		}
		return res;
	}

	public static List<Employee> getEmployeesByDepartment(String departmentName) {
		Department department =  DBDepartment.loadDepartment(departmentName);
		List<Employee> employees =  DBEmployee.loadEmployees(department);
		return employees;
	}
	
	public static String getNameShort(Department department) {
		String name = department.getName();
		final int newLength = 15;
		if(name==null || name.length()<newLength) return name;
		StringBuilder sb = new StringBuilder();
		String[] split = name.split(" ");
		int toBeCompressed = name.length()-newLength-split.length;
		for (int i = 0; i < split.length; i++) {
			String n = split[i];
			if(sb.length()>0) sb.append(" ");
			if(toBeCompressed>0 && n.length()>3) {
				sb.append(n.substring(0, Math.max(3, n.length()-toBeCompressed/(split.length-i))));
			} else {
				sb.append(n);
			}
		}
		return sb.toString();
	}

	public static User loadUser(String name) {
		try {
			return userService.loadUserByUsername(name,false,true);
		} catch (DataReadAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getUserFullname() {
		return "(" + employee.getUserName() + ") " + employee.getFirstName() + " " + employee.getLastName();
	}

	public static int getDepth(Department department) {
		int depth = 0;
		Department d = department;
		while(d!=null && depth<6) {
			depth++;
			d = d.getParent();
		}
		return depth;
	}

	public static Department getDepartment(String departmentName) {
		return DBDepartment.loadDepartment(departmentName);
	}

	public static void checkValid(Collection<String> usernames) throws Exception {
		for(String u: usernames) {
			if(DBEmployee.loadEmployeeByUserNameSimple(u) == null) 
				throw new Exception("The user "+u+" is not valid");
		}
	}

	public static boolean isSH() {
		return System.getProperty("user.name").startsWith("sh-");
	}
}
