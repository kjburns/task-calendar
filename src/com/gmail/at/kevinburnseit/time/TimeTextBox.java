package com.gmail.at.kevinburnseit.time;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gmail.at.kevinburnseit.swing.ValidatingTextBox;
import com.gmail.at.kevinburnseit.swing.ValidatingTextBoxValidator;
import com.gmail.at.kevinburnseit.time.IntervalTime.InvalidTimeException;
import com.gmail.at.kevinburnseit.util.RegexHelper;

public class TimeTextBox extends ValidatingTextBox implements TimeInterpretationClueProvider {
	public static interface ValueChangedListener {
		void valueChanged(int oldTime, int newTime);
	}
	/**
	 * 
	 * @author Kevin J. Burns
	 *
	 */
	public static abstract class TimeInterpretationHint {
		/**
		 * Examines an input time and a clue and determines the time that the user likely intends
		 * to enter.
		 * @param input Integer representation of the input time, in the range [0, 86400).
		 * @param clue Integer representation of the clue, the meaning of which varies by
		 * implementation
		 * @return The time that the user likely intends, as an integer in the range [0, 86400). 
		 * Return value varies by implementation.
		 * @throws InvalidTimeException if either input or clue is not in the proper range.
		 */
		public abstract int hint(int input, int clue) throws InvalidTimeException;
		/**
		 * Examines an input time and an object which provides a clue and determines the time
		 * that the user likely intends to enter.
		 * @param input Integer representation of the input time, in the range [0, 86400).
		 * @param cp An object that can provide a clue, which will vary by implementation
		 * @return The time that the user likely intends, as an integer in the range [0, 86400).
		 * Return value varies by implementation.
		 * @throws InvalidTimeException if either the input or clue is not in the proper range.
		 */
		public int hint(int input, TimeInterpretationClueProvider cp) throws InvalidTimeException {
			return this.hint(input, cp.getClue());
		}
	}
	private static final long serialVersionUID = -7881645092348745960L;
	/**
	 * Finds the most likely candidate for after another time.
	 */
	public static TimeInterpretationHint afterUsing12HourTime = new TimeInterpretationHint() {
		/**
		 * This implementation looks at an input time between 00:00 and 11:59 (AM or PM unknown) and
		 * finds the first occurrence of that 12-hour time after the time represented by the
		 * clue. If input is at or after 12:00, twelve hours are subtracted before processing.
		 * @throws InvalidTimeException if either input or clue is not in the proper range.
		 */
		@Override
		public int hint(int input, int clue) throws InvalidTimeException {
			input %= 43200;
			
			IntervalTime am = new IntervalTime(clue, input);
			IntervalTime pm = new IntervalTime(clue, input + 43200);
			
			if (am.getLength() < pm.getLength()) return am.getEnd();
			else return pm.getEnd();
		}
	};
	/**
	 * Finds the most likely candidate for before another time.
	 */
	public static TimeInterpretationHint beforeUsing12HourTime = new TimeInterpretationHint() {
		/**
		 * This implementation looks at an input time between 00:00 and 11:59 (AM or PM unknown) and
		 * finds the last occurrence of that 12-hour time before the time represented by the
		 * clue. If input is at or after 12:00, twelve hours are subtracted before processing.
		 * @throws InvalidTimeException if either input or clue is not in the proper range.
		 */
		@Override
		public int hint(int input, int clue) throws InvalidTimeException {
			input %= 43200;
			
			IntervalTime am = new IntervalTime(input, clue);
			IntervalTime pm = new IntervalTime(input + 43200, clue);
			
			if (am.getLength() < pm.getLength()) return am.getBegin();
			else return pm.getBegin();
		}
	};
	
	private final FocusListener focusListener = new FocusListener() {
		@Override
		public void focusGained(FocusEvent arg0) {
			TimeTextBox th = TimeTextBox.this;
			th.selectAll();
			suspendValidation();
		}
		@Override
		public void focusLost(FocusEvent arg0) {
			int v = tryInterpret();
			if (v != -1) {
				try {
					setValue(v);
				} catch (InvalidTimeException e) {
					// this shouldn't happen
				}
			}
			else value = v;
			TimeTextBox.this.select(0, 0);
			enableValidation();
			validateNow();
		}
	};
	private boolean validationSuspended;
	private TimeInterpretationHint hint = null;
	private TimeInterpretationClueProvider clueProvider = null;
	private int value = -1;
	private Vector<ValueChangedListener> valueChangedListeners = new Vector<ValueChangedListener>();
	public static final TimeInterpretationHint nearestToNoon = new TimeInterpretationHint() {
		@Override
		public int hint(int input, int clue) throws InvalidTimeException {
			int am = input % 43200;
			int pm = am + 43200;
			int noon = 43200;
			if (pm - noon < noon - am) return pm;
			else return am;
		}
	};

	public TimeTextBox() {
		super();
		
		this.addFocusListener(this.focusListener);
		super.addValidationListener(new ValidatingTextBoxValidator() {
			@Override
			public boolean validate(String newValue) {
//				int time = tryInterpret();
//				return (time != -1);
				return value != -1;
			}
		});
	}

	private String formatTime(int val) {
		int hr = val / 3600;
		int min = (val - hr * 3600) / 60;
		
		return String.format("%02d:%02d", hr, min);
	}

	private void suspendValidation() {
		this.validationSuspended = true;
	}
	
	private void enableValidation() {
		this.validationSuspended = false;
	}

	/* (non-Javadoc)
	 * @see us.galtware.trafficsim.controls.ValidatingTextBox#validateNow()
	 */
	@Override
	public void validateNow() {
		if (this.validationSuspended) return;
		super.validateNow();
	}

	public TimeInterpretationHint getHint() {
		return hint;
	}

	public void setHint(TimeInterpretationHint hint) {
		this.hint = hint;
	}
	
	private int tryInterpret() {
		String data = this.getText().trim().toLowerCase();
		int time = -1;
		Matcher m;
		/*
		 * HH:MM am/pm
		 */
		Pattern pt1 = Pattern.compile("^" + RegexHelper.integerGroup + ":" + 
									  RegexHelper.integerGroup + "\\s*([ap])m?$");
		m = pt1.matcher(data);
		if (m.matches()) {
			try {
				time = this.fromTwelveHour(m.group(1), m.group(2));
				if (m.group(3).equals("p")) time += 43200;
			} catch (InvalidTimeException e) {
			}
		}
		/*
		 * HHMM am/pm
		 * HH am/pm
		 */
		Pattern pt2 = Pattern.compile("^" + RegexHelper.integerGroup + "\\s*([ap])m?$");
		m = pt2.matcher(data);
		if (m.matches()) {
			int input = Integer.valueOf(m.group(1));
			int hr, min;
			if (input >= 100) {
				hr = input / 100;
				min = input % 100;
			}
			else {
				hr = input;
				min = 0;
			}
			try {
				time = this.fromTwelveHour(hr, min);
				if (m.group(2).equals("p")) time += 43200;
			} catch (InvalidTimeException e) {
			}
		}
		
		/*
		 * HHMM
		 * HH
		 */
		Pattern pt3 = Pattern.compile("^" + RegexHelper.integerGroup + "$");
		m = pt3.matcher(data);
		if (m.matches()) {
			int input = Integer.valueOf(m.group(1));
			int hr, min;
			if (input >= 100) {
				hr = input / 100;
				min = input % 100;
			}
			else {
				hr = input;
				min = 0;
			}
			if (hr == 0) {
				time = min * 60;
			}
			else if (hr < 12) {
				try {
					int interim = this.fromTwelveHour(hr, min);
					if (this.hint == null) {
						time = interim;
					} else {
						if (this.clueProvider != null) {
							if (this.clueProvider.getClue() == -1) {
								time = this.getValue();
							} else
								time = this.hint.hint(interim,
										this.clueProvider);
						} else
							time = this.hint.hint(interim, 0);
					}
				} catch (InvalidTimeException e) {
				}
			}
			else time = hr * 3600 + min * 60;
		}
		
		/*
		 * HH:MM
		 * HH:
		 */
		Pattern pt4 = Pattern.compile("^" + RegexHelper.integerGroup + ":" + RegexHelper.integerGroup + "?$");
		m = pt4.matcher(data);
		if (m.matches()) {
			String hrPart = m.group(1);
			String minPart = m.group(2);
			int hr, min;
			hr = Integer.valueOf(hrPart);
			if (minPart != null) min = Integer.valueOf(minPart);
			else min = 0;
			if (hr == 0) {
				time = min * 60;
			}
			else if (hr < 12) {
				try {
					int interim = this.fromTwelveHour(hr, min);
					if (this.hint == null) {
						time = interim;
					}
					else {
						if (this.clueProvider != null) {
							if (this.clueProvider.getClue() == -1) {
								time = this.getValue();
							}
							else time = this.hint.hint(interim, this.clueProvider);
						}
						else time = this.hint.hint(interim, 0);
					}
				} catch (InvalidTimeException e) {
				}
			}
			else time = hr * 3600 + min * 60;
		}
		
		return time;
	}
	
	private int fromTwelveHour(String hour, String minute) throws InvalidTimeException {
		int hr = Integer.valueOf(hour);
		int min = Integer.valueOf(minute);
		return fromTwelveHour(hr, min);
	}

	private int fromTwelveHour(int hr, int min) throws InvalidTimeException {
		if ((hr < 1) || (hr > 12)) throw new InvalidTimeException();
		if (hr == 12) hr = 0;
		if ((min < 0) || (min > 59)) throw new InvalidTimeException();
		
		return hr * 3600 + min * 60;
	}

	public static void test() {
		TimeTextBox tester = new TimeTextBox();
		TimeTextBox tester2 = new TimeTextBox();
		tester2.setText("0400p");
		System.out.println("Should see 18000 two times");
		tester.setClueProvider(tester2);
		tester.setHint(beforeUsing12HourTime);
		tester.setText("500");
		System.out.println(tester.tryInterpret());
		tester.setText("5");
		System.out.println(tester.tryInterpret());
		System.out.println("Should see 61200 once");
		tester.setHint(afterUsing12HourTime);
		tester.setText("500");
		System.out.println(tester.tryInterpret());
		tester.setHint(nearestToNoon);
		System.out.println("Should see 46800 three times");
		tester.setText("1");
		System.out.println(tester.tryInterpret());
		tester.setText("100");
		System.out.println(tester.tryInterpret());
		tester.setText("1");
		System.out.println(tester.tryInterpret());
		tester.setText("100");
	}

	@Override
	public int getClue() {
		return this.getValue();
	}

	/**
	 * @param clueProvider the clueProvider to set
	 */
	public void setClueProvider(TimeInterpretationClueProvider clueProvider) {
		this.clueProvider = clueProvider;
	}

	/**
	 * This function does nothing as its functionality has been usurped by the widget.
	 */
	/* (non-Javadoc)
	 * @see us.galtware.trafficsim.controls.ValidatingTextBox#addValidationListener(us.galtware.trafficsim.controls.ValidatingTextBoxValidator)
	 */
	@Override
	public void addValidationListener(ValidatingTextBoxValidator v) {
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the value to set. must be in the interval [0, 86400)
	 * @throws InvalidTimeException if not in the above interval
	 */
	public void setValue(int value) throws InvalidTimeException {
		if ((value >= 0) && (value < 86400)) {
			int oldValue = this.value;
			this.value = value;
			this.setText(this.formatTime(this.value));
			for (ValueChangedListener l : this.valueChangedListeners) {
				l.valueChanged(oldValue, value);
			}
		}
		else throw new InvalidTimeException();
	}
	
	public void addValueChangedListener(ValueChangedListener l) {
		this.valueChangedListeners.add(l);
	}
	
	public void removeValueChangedListener(ValueChangedListener l) {
		this.valueChangedListeners.remove(l);
	}
}
