package com.idorsia.research.spirit.core.constants;

public enum LogAction {
	/**Register successful logon attempts*/
	LOGON_SUCCESS,
	/**Register unsuccessful logon attempts*/
	LOGON_FAILED,
	/**when the account has been unlocked */
	UNLOCK,
	/** when the user valide all missing results */
	VALIDATE_ALL
}
