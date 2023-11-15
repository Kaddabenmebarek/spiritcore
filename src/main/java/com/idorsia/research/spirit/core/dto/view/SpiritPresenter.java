package com.idorsia.research.spirit.core.dto.view;

import java.io.Serializable;

import com.idorsia.research.spirit.core.util.IAskUserChoice;

public class SpiritPresenter implements Serializable {
	private static final long serialVersionUID = -7563288663871107279L;
	private static SpiritPresenter instance;
	public static int ABORT = 0;
	public static int YES = 1;
	public static int YES_TO_ALL = 2;
	private IAskUserChoice userChoiceRequestor;

	private SpiritPresenter() {
	}

	public static SpiritPresenter getInstance() {
		if (instance == null) instance = new SpiritPresenter();
		return instance;
	}

	public static void registerPresenter(SpiritPresenter presenter) {
		instance = presenter;
	}

	public void setCallbackUXClass(IAskUserChoice someUXComp) {
		userChoiceRequestor = someUXComp;
	}
	public int askUserChoice(String message, String... choices) {

		if (userChoiceRequestor!=null) return userChoiceRequestor.askUserChoice(message, choices);

		return ABORT;
	}

}
