package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmController {
	private final AlarmUseCase alarmUseCase;

	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void postAlarm(@RequestBody PostAlarmRequest request) {
		String content = request.getContent();
	}
}
