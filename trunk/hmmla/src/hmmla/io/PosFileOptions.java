// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.io;

import java.io.File;

public class PosFileOptions {
	private static final String FORM_INDEX = "form-index";
	private static final String TAG_INDEX = "tag-index";
	private static final String LIMIT = "limit";

	private int form_index_;
	private int tag_index_;
	private String filename_;
	private int limit_;
	
	public PosFileOptions(String option_string) {
		parse(option_string);
	}
	
	private void parse(String option_string) {
		form_index_ = -1;
		tag_index_ = -1;
		limit_ = -1;
		filename_ = null;

		String[] args = option_string.split(",");
		for (String arg : args) {
			
			if (arg.length() == 0) {
				continue;
			}

			int index = arg.indexOf('=');

			if (index < 0) {
				if (filename_ != null) {
					RuntimeException e = new RuntimeException(
							"Option string contains more than one filename: "
									+ option_string);
					e.initCause(new Throwable("filename"));
					throw e;
				}
				filename_ = arg;
			} else {
				String option = arg.substring(0, index);
				String value = arg.substring(index + 1, arg.length());

				if (option.equalsIgnoreCase(FORM_INDEX)) {
					if (form_index_ != -1) {					
						RuntimeException e = new RuntimeException(
								"Option string contains more than one form index: "
										+ option_string);
						e.initCause(new Throwable("form-index"));
						throw e;
					}
					form_index_ = Integer.parseInt(value);
				} else if (option.equalsIgnoreCase(TAG_INDEX)) {
					if (tag_index_ != -1) {
						RuntimeException e = new RuntimeException(
								"Option string contains more than one tag index: "
										+ option_string);
						
						e.initCause(new Throwable("tag-index"));
						throw e;
						
					}
					tag_index_ = Integer.parseInt(value);
				} else if (option.equalsIgnoreCase(LIMIT)) {
					if (limit_ != -1) {
						RuntimeException e = new RuntimeException(
								"Option string contains more than one limit: "
										+ option_string);
						
						e.initCause(new Throwable("limit"));
						throw e;
						
					}
					limit_ = Integer.parseInt(value);
				} else {
					RuntimeException e = new RuntimeException("Unknown option: " + option);
					e.initCause(new Throwable("option"));
					throw e;
				}
			}

		}

		if (filename_ == null) {
			RuntimeException e = new RuntimeException("No filename in option string: "
					+ option_string);
			e.initCause(new Throwable("no filename"));
			throw e;
		}
	}

	public String getFilename() {
		return filename_;
	}

	public int getLimit() {
		return limit_;
	}

	public int getFormIndex() {
		return form_index_;
	}

	public int getTagIndex() {
		return tag_index_;
	}

	public File getFile() {
		return new File(filename_);
	}
	
}
