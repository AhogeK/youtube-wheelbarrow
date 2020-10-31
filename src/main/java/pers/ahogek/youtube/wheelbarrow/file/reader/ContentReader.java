package pers.ahogek.youtube.wheelbarrow.file.reader;

import pers.ahogek.youtube.wheelbarrow.common.CommonProperty;
import pers.ahogek.youtube.wheelbarrow.file.wrapper.FileWrapper;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 内容读取类
 * </p>
 *
 * @author AhogeK
 * @since 2020-10-31 23:07
 */
public class ContentReader {

    private final FileWrapper wrapper;

    public ContentReader(FileWrapper wrapper) {
        this.wrapper = wrapper;
    }

    public void read() throws IOException {
        try(LineNumberReader lineReader = new LineNumberReader(new FileReader(wrapper.getFile()))) {
            List<String> contents = lineReader.lines().collect(Collectors.toList());
            if (contents.size() > wrapper.getCurrentLine()) {
                for (int i = wrapper.getCurrentLine(); i < contents.size(); i++) {
                    // 这里只是简单打印出新加的内容到控制台
                    System.out.println(contents.get(i));
                    CommonProperty.ammunition.add(contents.get(i));
                }
            }
            // 保存当前读取的行数
            wrapper.setCurrentLine(contents.size());
        }
    }
}
