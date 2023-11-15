package com.idorsia.research.spirit.core.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.idorsia.research.spirit.core.constants.LogAction;
import com.idorsia.research.spirit.core.dao.AbstractDao;
import com.idorsia.research.spirit.core.dao.LogEntryDao;
import com.idorsia.research.spirit.core.dto.view.LogEntry;
import com.idorsia.research.spirit.core.util.UserUtil;

@Repository
public class LogEntryDaoImpl extends AbstractDao<LogEntry> implements LogEntryDao{

	private static final String TABLE_NAME = "LOGENTRY";

	@Override
	public Integer getNbLogByAction(LogAction action) {
		String sql = String.format("SELECT * FROM LOGENTRY WHERE ACTION = %s", action.name());
		return super.getObjectList(TABLE_NAME, LogEntry.class, sql).size();
	}

	@Override
	public void save(LogEntry log) {
		log.setCreDate(new Date());
		log.setCreUser(UserUtil.getUsername()==null?System.getProperty("user.name"):UserUtil.getUsername());
		super.add(TABLE_NAME, log);
	}

	@Override
	public List<LogEntry> getLogs(String user, LogAction action, int sinceDays) {
		StringBuilder sql = new StringBuilder("select * from logentry where 1=1");
		sql.append(user != null && user.length() > 0 ? "and creuser like '%" + user + "%'" : "");
		if (sinceDays >= 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DAY_OF_YEAR, -sinceDays);
			
			String targetMinDate = new SimpleDateFormat("dd/MM/yy hh:mm:ss").format(cal.getTime());
			sql.append(" and credate >= to_timestamp('").append(targetMinDate).append("','DD/MM/RR HH.MI.SS')");
		}
		sql.append(action != null ? " and action = '" + action.name().toUpperCase() + "'"  : "");
		return (List<LogEntry>) super.getObjectList(TABLE_NAME, LogEntry.class, sql.toString());
	}

	@Override
	@SuppressWarnings("unlikely-arg-type")
	public boolean isLocked(String user) {
		String sql = "select * from logentry where creuser = '"+user+"' order by credate desc";
		List<LogEntry> res = getJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(LogEntry.class));
		for (LogEntry l : res) {
			if(l.getAction().equals(LogAction.UNLOCK.name())) {
				return false; //The account has been reenabled withing the last max attempts
			}
			if(l.getAction().equals(LogAction.LOGON_SUCCESS.name())) {
				return false; //The account has been successfully authentificated withing the last max attempts
			}
//			switch(l.getAction()) {
//			case UNLOCK: return false; //The account has been reenabled withing the last max attempts
//			case LOGON_SUCCESS: return false; //The account has been successfully authentificated withing the last max attempts
//			case LOGON_FAILED: /*continue*/;
		}
		return true;		
	}

	
}
