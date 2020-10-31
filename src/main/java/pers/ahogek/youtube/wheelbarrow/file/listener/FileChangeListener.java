package pers.ahogek.youtube.wheelbarrow.file.listener;

import pers.ahogek.youtube.wheelbarrow.file.event.FileChangeEvent;
import pers.ahogek.youtube.wheelbarrow.file.reader.ContentReader;
import pers.ahogek.youtube.wheelbarrow.file.wrapper.FileWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author AhogeK
 * @since 2020-10-31 22:54
 */
public class FileChangeListener implements Listener {

    /**
     * 保存路径跟文件包装类的映射
     */
    private final Map<String, FileWrapper> map = new ConcurrentHashMap<>();

    @Override
    public void fire(FileChangeEvent event) {
        switch (event.getKind().name()) {
            case "ENTRY_MODIFY":
                // 文件修改事件
                if (new File(event.getPath().toString()).exists()) {
                    modify(event.getPath());
                }
                break;
            default:
                throw new UnsupportedOperationException(
                        String.format("The kind [%s] is unsupport.", event.getKind().name())
                );
        }
    }

    private void modify(Path path) {
        // 根据全路径获取包装类对象
        FileWrapper wrapper = map.get(path.toString());
        if (wrapper == null) {
            wrapper = new FileWrapper((path.toFile()));
            map.put(path.toString(), wrapper);
        }
        try {
            // 读取追加的内容
            new ContentReader(wrapper).read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}