package com.hankcs.hanlp.corpus.io;

import java.io.*;
import java.nio.file.Files;

import static com.hankcs.hanlp.utility.Predefine.logger;

public class CacheResourceIOAdapter implements IIOAdapter {

    private static final String TMP_DIR;

    static {
        try {
            TMP_DIR = Files.createTempDirectory("hanlp").toAbsolutePath().toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static final String SEP = File.separator;

    @Override
    public InputStream open(String path) throws IOException {
        String cachePath = getCachePath(path);
        return IOUtil.isFileExisted(cachePath) ?
            new FileInputStream(cachePath) : this.getClass().getClassLoader().getResourceAsStream(path);
    }

    @Override
    public OutputStream create(String path) throws IOException {
        String cachePath = getCachePath(path);
        mkdir(cachePath);
        return new FileOutputStream(cachePath);
    }

    private String getCachePath(String path) {
        return new File(TMP_DIR + "/" + path).getPath().intern();
    }

    private void mkdir(String path) {
        if (new File(path).exists()) {
            logger.info(String.format("Path %s already exists, do nothing", path));
            return;
        }
        int pos = path.lastIndexOf(SEP);
        String dir = pos == -1 ? path : path.substring(0, pos);
        logger.info(String.format("Created cache folder %s", dir));
        new File(dir).mkdirs();
    }
}
