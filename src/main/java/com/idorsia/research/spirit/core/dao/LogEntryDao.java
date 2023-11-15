package com.idorsia.research.spirit.core.dao;

import java.util.List;

import com.idorsia.research.spirit.core.constants.LogAction;
import com.idorsia.research.spirit.core.dto.view.LogEntry;

public interface LogEntryDao {

	public Integer getNbLogByAction(LogAction action);
	
	public void save(LogEntry log);

	public List<LogEntry> getLogs(String user, LogAction action, int isinceDays);

	public boolean isLocked(String user);
}
