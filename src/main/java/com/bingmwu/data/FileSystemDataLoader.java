package com.bingmwu.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemDataLoader implements DataLoader {
	Logger logger = LoggerFactory.getLogger(FileSystemDataLoader.class);
	private final String[] paths;

	public FileSystemDataLoader(String... args) {
		paths = args;
	}

	@Override
	public List<DataItem> loadData() {
		List<DataItem> dataList = null;
		String extension = ".csv";
		List<String> names = DataUtils.listFiles(extension, paths);
		if (names == null) {
			logger.error("Unable to list files from paths: " + paths);
			throw new FileSystemNotFoundException("Unable to list files");
		}
		for (String name : names) {
			logger.info("Reading file " + name);
			dataList = createDataSet(name);
		}
		if (dataList == null || dataList.size() == 0) {
			logger.error("No data read from paths: " + paths);
			throw new IllegalStateException("No files to read!");
		}
		return dataList;

	}

	private List<DataItem> createDataSet(String name) {
		File file = new File(name);
		dataFileSanityCheck(file);
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			List<DataItem> lines = DataUtils.createLineList(br);
			logger.info("Got " + lines.size() + " lines of daily Data");

			return lines;
		} catch (IOException e) {
			logger.error("Unable to process file: " + name);
			throw new IllegalStateException("Unable to process file: " + name);
		}
	}

	private void dataFileSanityCheck(File file) {
		if (!file.isFile()) {
			String message = String.format("Unable to process file '%s' - not a file.", file.getName());
			logger.error(message);
			throw new IllegalStateException(message);
		}
	}
}
