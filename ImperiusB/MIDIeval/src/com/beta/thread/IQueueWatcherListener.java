package com.beta.thread;

public interface IQueueWatcherListener {
	void fn_QueueIsNowEmpty();
	void fn_QueueIsNowFilling();
}
