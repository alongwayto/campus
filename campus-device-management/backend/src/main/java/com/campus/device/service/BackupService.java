package com.campus.device.service;

import java.util.List;

public interface BackupService {

    String backupDatabase() throws Exception;

    List<String> listBackups();
}
