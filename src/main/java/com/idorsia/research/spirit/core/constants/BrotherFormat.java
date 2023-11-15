package com.idorsia.research.spirit.core.constants;

public enum BrotherFormat {
	_9x24("9mmx24_1", 0),

	_12x23N("12mmx23_1", 0),
	_12x33("12mmx33_1", 8),
	_12x33N("12mmx33_1", 0),
	_12x42("12mmx42_1", 8),

	_12x49("12mmx49_1", 10),
	_12x62("12mmx62_1", 10)
	,
	_12x62N("12mmx62_1", 0),

	_18x24("18mmx24_1", 0),

	;

	private final String media;
	private final float leftLineOffset;

	/**
	 *
	 * @param medias
	 * @param leftLineOffset - distance of line from the left border in mm
	 * @param barcodeOnRight
	 */
	private BrotherFormat(String media,  float leftLineOffset) {
		this.media = media;
		this.leftLineOffset = leftLineOffset;
	}

	public String getMedia() {
		return media;
	}

	public float getLineOffset() {
		return leftLineOffset;
	}

	@Override
	public String toString() {
		return media;
	}
}
