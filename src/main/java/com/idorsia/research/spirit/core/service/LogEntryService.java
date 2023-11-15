package com.idorsia.research.spirit.core.service;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idorsia.research.spirit.core.constants.Constants;
import com.idorsia.research.spirit.core.constants.LogAction;
import com.idorsia.research.spirit.core.dao.LogEntryDao;
import com.idorsia.research.spirit.core.dto.view.LogEntry;
import com.idorsia.research.spirit.core.util.UserUtil;

@Service
public class LogEntryService implements Serializable {
	
	private static final long serialVersionUID = 5245458414366000720L;
	@Autowired
	private LogEntryDao logEntryDao;
	
	public int getNbLogByAction(LogAction action){
		return logEntryDao.getNbLogByAction(action);
	}
	
	public void log(LogAction action, String comments) {
		log(UserUtil.getUser()==null?System.getProperty("user.name"):UserUtil.getUsername(), action, comments);
	}
	
	@Transactional
	public void log(String user, LogAction action, String comments) {
		assert user!=null;
		assert action!=null;
		String pcName = "";
		try {
			InetAddress localMachine = InetAddress.getLocalHost();
			pcName = localMachine==null? null: localMachine.getHostAddress();
		} catch(Exception e) {
			System.err.println(e);
		}
		if (comments != null && comments.length() > 248) {
			comments = comments.substring(0, Constants.LOGENTRY_COMMENTS_MAX_SIZE); // DB column limit is 250 chars
		}

		LogEntry log = new LogEntry();
		log.setAction(action);
		log.setComments(comments);
		log.setIpAddress(pcName);
		logEntryDao.save(log);
	}
	
	public List<LogEntry> getLogs(String user, LogAction action, int isinceDays) {
		List<LogEntry> res = logEntryDao.getLogs(user, action, isinceDays);
		Collections.sort(res, new Comparator<LogEntry>() {
			@Override
			public int compare(LogEntry l1, LogEntry l2) {
				return l1.getCreDate().compareTo(l2.getCreDate());
			}
		});
		return res;
	}
	
	public boolean isLocked(String user) {
		return logEntryDao.isLocked(user);
	}
}
