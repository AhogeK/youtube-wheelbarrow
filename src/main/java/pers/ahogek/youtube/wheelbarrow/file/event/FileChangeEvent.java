package pers.ahogek.youtube.wheelbarrow.file.event;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * <p>
 * 文件变更事件
 * </p>
 *
 * @author AhogeK
 * @since 2020-10-31 22:49
 */
public class FileChangeEvent {

    private final Path path;

    private final WatchEvent.Kind<?> kind;

    public FileChangeEvent(Path path, WatchEvent.Kind<?> kind) {
        this.path = path;
        this.kind = kind;
    }

    public Path getPath() {
        return this.path;
    }

    public WatchEvent.Kind<?> getKind() {
        return this.kind;
    }
}
