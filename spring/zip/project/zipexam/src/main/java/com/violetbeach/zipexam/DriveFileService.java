package com.violetbeach.zipexam;

import java.util.List;

public interface DriveFileService {
	List<DriveFile> getFiles(String driveId);
}
