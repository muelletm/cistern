// Copyright 2013 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.util;

import java.security.InvalidParameterException;

public class Timer {

	private long total_time_;
	private long current_start_time_;
	private State state_;
	
	private enum State {Running, Stopped}; 
	
	public Timer() {
		reset();
	}
	
	public void start() {
		current_start_time_ = System.currentTimeMillis();
		state_ = State.Running;
	}
	
	public void stop() {
		if (state_.equals(State.Stopped))
			throw new InvalidParameterException();
		total_time_ += System.currentTimeMillis() - current_start_time_;
	}
	
	public long getTime() {
		long time = total_time_;
		
		if (state_.equals(State.Running)) {
			time += System.currentTimeMillis() - current_start_time_;
		}
		return time;
	}
	
	public long getTimeInSeconds() {
		return getTime() / 1000;
	}
	
	public void reset() {
		total_time_ = 0;
		current_start_time_ = -1;
		state_ = State.Stopped;
	}
	
}
