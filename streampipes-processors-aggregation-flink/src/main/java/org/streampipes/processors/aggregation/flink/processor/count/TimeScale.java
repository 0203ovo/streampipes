package org.streampipes.processors.aggregation.flink.processor.count;

import org.apache.flink.streaming.api.windowing.time.Time;

public class TimeScale {

	private String value;
	
	TimeScale(String value)
	{
		this.value = value;
	}
	
	public String value()
	{
		return value;
	}

	public Time toFlinkTime(Integer count) {
		if (this.value.equals(CountController.MINUTES_INTERNAL_NAME)) {
			return Time.minutes(count);
		} else if (this.value.equals(CountController.SECONDS_INTERNAL_NAME)) {
			return Time.seconds(count);
		} else {
			return Time.hours(count);
		}
	}
}
